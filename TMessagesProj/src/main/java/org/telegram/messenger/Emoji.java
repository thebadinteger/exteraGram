&& (i + 1) >= limitCount) {
                break;
            }
        }
        return s;
    }

    public static CharSequence replaceWithRestrictedEmoji(CharSequence cs, TextView textView, Runnable update) {
        return replaceWithRestrictedEmoji(cs, textView.getPaint().getFontMetricsInt(), update);
    }

    public static CharSequence replaceWithRestrictedEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, Runnable update) {
        return replaceWithRestrictedEmoji(cs, fontMetrics, AnimatedEmojiDrawable.CACHE_TYPE_STANDARD_EMOJI, update);
    }

    public static CharSequence replaceWithRestrictedEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int cacheType, Runnable update) {
        if (SharedConfig.useSystemEmoji || cs == null || cs.length() == 0) {
            return cs;
        }

        final int currentAccount = UserConfig.selectedAccount;
        TLRPC.InputStickerSet inputStickerSet = new TLRPC.TL_inputStickerSetShortName();
        inputStickerSet.short_name = "RestrictedEmoji";
        TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(currentAccount).getStickerSet(inputStickerSet, 0, false, true, update == null ? null : s -> update.run());

        Spannable s;
        if (cs instanceof Spannable) {
            s = (Spannable) cs;
        } else {
            s = Spannable.Factory.getInstance().newSpannable(cs.toString());
        }
        ArrayList<EmojiSpanRange> emojis = parseEmojis(s, null);
        if (emojis.isEmpty()) {
            return cs;
        }

        AnimatedEmojiSpan[] animatedEmojiSpans = s.getSpans(0, s.length(), AnimatedEmojiSpan.class);
        EmojiSpan span;
        Drawable drawable;
        int limitCount = SharedConfig.getDevicePerformanceClass() >= SharedConfig.PERFORMANCE_CLASS_HIGH ? 100 : 50;
        for (int i = 0; i < emojis.size(); ++i) {
            try {
                EmojiSpanRange emojiRange = emojis.get(i);
                if (animatedEmojiSpans != null) {
                    boolean hasAnimated = false;
                    for (int j = 0; j < animatedEmojiSpans.length; ++j) {
                        AnimatedEmojiSpan animatedSpan = animatedEmojiSpans[j];
                        if (animatedSpan != null && s.getSpanStart(animatedSpan) == emojiRange.start && s.getSpanEnd(animatedSpan) == emojiRange.end) {
                            hasAnimated = true;
                            break;
                        }
                    }
                    if (hasAnimated) {
                        continue;
                    }
                }
                TLRPC.Document document = null;
                if (set != null) {
                    for (TLRPC.Document d : set.documents) {
                        if (MessageObject.findAnimatedEmojiEmoticon(d, null).contains(emojiRange.code)) {
                            document = d;
                            break;
                        }
                    }
                }
                AnimatedEmojiSpan animatedSpan;
                if (document != null) {
                    animatedSpan = new AnimatedEmojiSpan(document, fontMetrics);
                } else {
                    animatedSpan = new AnimatedEmojiSpan(0, fontMetrics);
                }
                animatedSpan.emoji = (emojiRange.code).toString();
                animatedSpan.cacheType = cacheType;
                s.setSpan(animatedSpan, emojiRange.start, emojiRange.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                FileLog.e(e);
            }
            if ((Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 29)/* && !BuildVars.DEBUG_PRIVATE_VERSION*/ && (i + 1) >= limitCount) {
                break;
            }
        }
        return s;
    }

    public static class EmojiSpan extends ImageSpan {
        public Paint.FontMetricsInt fontMetrics;
        public float scale = 1f;
        public int size = AndroidUtilities.dp(20);
        public String emoji;

        public EmojiSpan(Drawable d, int verticalAlignment, Paint.FontMetricsInt original) {
            super(d, verticalAlignment);
            fontMetrics = original;
            if (original != null) {
                size = Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.ascent);
                if (size == 0) {
                    size = AndroidUtilities.dp(20);
                }
            }
        }

        public void replaceFontMetrics(Paint.FontMetricsInt newMetrics, int newSize) {
            fontMetrics = newMetrics;
            size = newSize;
        }

        public void replaceFontMetrics(Paint.FontMetricsInt newMetrics) {
            fontMetrics = newMetrics;
            if (fontMetrics != null) {
                size = Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.ascent);
                if (size == 0) {
                    size = AndroidUtilities.dp(20);
                }
            }
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = new Paint.FontMetricsInt();
            }

            int scaledSize = (int) (scale * size);
            if (fontMetrics == null) {
                int sz = super.getSize(paint, text, start, end, fm);

                int offset = AndroidUtilities.dp(8);
                int w = AndroidUtilities.dp(10);
                fm.top = -w - offset;
                fm.bottom = w - offset;
                fm.ascent = -w - offset;
                fm.leading = 0;
                fm.descent = w - offset;

                return sz;
            } else {
                if (fm != null) {
                    fm.ascent = fontMetrics.ascent;
                    fm.descent = fontMetrics.descent;

                    fm.top = fontMetrics.top;
                    fm.bottom = fontMetrics.bottom;
                }
                if (getDrawable() != null) {
                    getDrawable().setBounds(0, 0, scaledSize, scaledSize);
                }
                return scaledSize;
            }
        }

        public boolean drawn;
        public float lastDrawX, lastDrawY;

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            lastDrawX = x + scale * size / 2f;
            lastDrawY = top + (bottom - top) / 2f;
            drawn = true;

            boolean restoreAlpha = false;
            if (paint.getAlpha() != 255 && emojiDrawingUseAlpha) {
                restoreAlpha = true;
                getDrawable().setAlpha(paint.getAlpha());
            }
            boolean needRestore = false;
            float ty = emojiDrawingYOffset - (size - scale * size) / 2;
            if (ty != 0) {
                needRestore = true;
                canvas.save();
                canvas.translate(0, ty);
            }
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            if (needRestore) {
                canvas.restore();
            }
            if (restoreAlpha) {
                getDrawable().setAlpha(255);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (getDrawable() instanceof EmojiDrawable) {
                ((EmojiDrawable) getDrawable()).placeholderColor = 0x10ffffff & ds.getColor();
            }
            super.updateDrawState(ds);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmojiSpan emojiSpan = (EmojiSpan) o;
            return Float.compare(scale, emojiSpan.scale) == 0 && size == emojiSpan.size && Objects.equals(emoji, emojiSpan.emoji);
        }
    }

    public static void addRecentEmoji(String code) {
        Integer count = emojiUseHistory.get(code);
        if (count == null) {
            count = 0;
        }
        if (count == 0 && emojiUseHistory.size() >= MAX_RECENT_EMOJI_COUNT) {
            String emoji = recentEmoji.get(recentEmoji.size() - 1);
            emojiUseHistory.remove(emoji);
            recentEmoji.set(recentEmoji.size() - 1, code);
        }
        emojiUseHistory.put(code, ++count);
    }

    public static void removeRecentEmoji(String code) {
        emojiUseHistory.remove(code);
        recentEmoji.remove(code);
        if (emojiUseHistory.isEmpty() || recentEmoji.isEmpty()) {
            addRecentEmoji(DEFAULT_RECENT[0]);
        }
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (HashMap.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, (lhs, rhs) -> {
            Integer count1 = emojiUseHistory.get(lhs);
            Integer count2 = emojiUseHistory.get(rhs);
            if (count1 == null) {
                count1 = 0;
            }
            if (count2 == null) {
                count2 = 0;
            }
            if (count1 > count2) {
                return -1;
            } else if (count1 < count2) {
                return 1;
            }
            return 0;
        });
        while (recentEmoji.size() > MAX_RECENT_EMOJI_COUNT) {
            recentEmoji.remove(recentEmoji.size() - 1);
        }
    }

    public static void saveRecentEmoji() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (HashMap.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    public static void clearRecentEmoji() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        preferences.edit().putBoolean("filled_default", true).commit();
        emojiUseHistory.clear();
        recentEmoji.clear();
        saveRecentEmoji();
    }

    public static void loadRecentEmoji() {
        if (recentEmojiLoaded) {
            return;
        }
        recentEmojiLoaded = true;
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();

        String str;
        try {
            emojiUseHistory.clear();
            if (preferences.contains("emojis")) {
                str = preferences.getString("emojis", "");
                if (str != null && str.length() > 0) {
                    String[] args = str.split(",");
                    for (String arg : args) {
                        String[] args2 = arg.split("=");
                        long value = Utilities.parseLong(args2[0]);
                        StringBuilder string = new StringBuilder();
                        for (int a = 0; a < 4; a++) {
                            char ch = (char) value;
                            string.insert(0, ch);
                            value >>= 16;
                            if (value == 0) {
                                break;
                            }
                        }
                        if (string.length() > 0) {
                            emojiUseHistory.put(string.toString(), Utilities.parseInt(args2[1]));
                        }
                    }
                }
                preferences.edit().remove("emojis").commit();
                saveRecentEmoji();
            } else {
                str = preferences.getString("emojis2", "");
                if (str != null && str.length() > 0) {
                    String[] args = str.split(",");
                    for (String arg : args) {
                        String[] args2 = arg.split("=");
                        emojiUseHistory.put(args2[0], Utilities.parseInt(args2[1]));
                    }
                }
            }
            if (emojiUseHistory.isEmpty()) {
                if (!preferences.getBoolean("filled_default", false)) {
                    for (int i = 0; i < DEFAULT_RECENT.length; i++) {
                        emojiUseHistory.put(DEFAULT_RECENT[i], DEFAULT_RECENT.length - i);
                    }
                    preferences.edit().putBoolean("filled_default", true).commit();
                    saveRecentEmoji();
                }
            }
            sortEmoji();
        } catch (Exception e) {
            FileLog.e(e);
        }

        try {
            str = preferences.getString("color", "");
            if (str != null && str.length() > 0) {
                String[] args = str.split(",");
                for (int a = 0; a < args.length; a++) {
                    String arg = args[a];
                    String[] args2 = arg.split("=");
                    emojiColor.put(args2[0], args2[1]);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void saveEmojiColors() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("color", stringBuilder.toString()).commit();
    }
}
