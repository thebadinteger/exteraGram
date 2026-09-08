return false;
    }

    protected void extendActionMode(ActionMode actionMode, Menu menu) {

    }

    private void onWindowSizeChanged() {
        int size = sizeNotifierLayout.getHeight();
        if (!keyboardVisible) {
            size -= emojiPadding;
        }
        if (delegate != null) {
            delegate.onWindowSizeChanged(size);
        }
    }

    public void onCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        sizeNotifierLayout.setDelegate(this);
    }

    public void onDestroy() {
        hidePopup();
        if (isKeyboardVisible()) {
            closeKeyboard();
        }
        keyboardVisible = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        if (sizeNotifierLayout != null) {
            sizeNotifierLayout.setDelegate(null);
        }
    }

    public void setDelegate(PhotoViewerCaptionEnterViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void setFieldText(CharSequence text) {
        if (messageEditText == null) {
            return;
        }
        messageEditText.setText(text);
        messageEditText.setSelection(messageEditText.getText().length());
        if (delegate != null) {
            delegate.onTextChanged(messageEditText.getText());
        }
    }

    public int getSelectionLength() {
        if (messageEditText == null) {
            return 0;
        }
        try {
            return messageEditText.getSelectionEnd() - messageEditText.getSelectionStart();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return 0;
    }

    public int getCursorPosition() {
        if (messageEditText == null) {
            return 0;
        }
        return messageEditText.getSelectionStart();
    }

    private class DarkTheme implements Theme.ResourcesProvider {
        @Override
        public int getColor(int key) {
            if (key == Theme.key_dialogBackground) {
                return -14803426;
            } else if (key == Theme.key_windowBackgroundWhite) {
                return -15198183;
            } else if (key == Theme.key_windowBackgroundWhiteBlackText) {
                return -1;
            } else if (key == Theme.key_chat_emojiPanelEmptyText) {
                return -8553090;
            } else if (key == Theme.key_progressCircle) {
                return -10177027;
            } else if (key == Theme.key_chat_emojiSearchIcon) {
                return -9211020;
            } else if (key == Theme.key_chat_emojiPanelStickerPackSelector || key == Theme.key_chat_emojiSearchBackground) {
                return 181267199;
            } else if (key == Theme.key_chat_emojiPanelIcon) {
                return -9539985;
            } else if (key == Theme.key_chat_emojiBottomPanelIcon) {
                return -9539985;
            } else if (key == Theme.key_chat_emojiPanelIconSelected) {
                return -10177041;
            } else if (key == Theme.key_chat_emojiPanelStickerPackSelectorLine) {
                return -10177041;
            } else if (key == Theme.key_chat_emojiPanelBackground) {
                return -14803425;
            } else if (key == Theme.key_chat_emojiPanelShadowLine) {
                return -1610612736;
            } else if (key == Theme.key_chat_emojiPanelBackspace) {
                return -9539985;
            } else if (key == Theme.key_listSelector) {
                return 771751936;
            } else if (key == Theme.key_divider) {
                return -16777216;
            } else if (key == Theme.key_chat_editMediaButton) {
                return -10177041;
            } else if (key == Theme.key_dialogFloatingIcon) {
                return 0xffffffff;
            } else if (key == Theme.key_chat_emojiPanelStickerSetName) {
                return 0x73ffffff;
            }
            return 0;
        }
    }

    private void createEmojiView() {
        if (emojiView != null && emojiView.currentAccount != UserConfig.selectedAccount) {
            sizeNotifierLayout.removeView(emojiView);
            emojiView = null;
        }
        if (emojiView != null) {
            return;
        }
        emojiView = new EmojiView(null, true, false, false, getContext(), false, null, null, true, resourcesProvider, false);
        emojiView.emojiCacheType = AnimatedEmojiDrawable.CACHE_TYPE_ALERT_PREVIEW;
        emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
            @Override
            public boolean onBackspace() {
                if (messageEditText.length() == 0) {
                    return false;
                }
                messageEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                return true;
            }

            @Override
            public void onAnimatedEmojiUnlockClick() {
                new PremiumFeatureBottomSheet(new BaseFragment() {
                    @Override
                    public int getCurrentAccount() {
                        return currentAccount;
                    }

                    @Override
                    public Context getContext() {
                        return PhotoViewerCaptionEnterView.this.getContext();
                    }

                    @Override
                    public Activity getParentActivity() {
                        Context context = getContext();
                        while (context instanceof ContextWrapper) {
                            if (context instanceof Activity) {
                                return (Activity) context;
                            }
                            context = ((ContextWrapper) context).getBaseContext();
                        }
                        return null;
                    }

                    @Override
                    public Dialog getVisibleDialog() {
                        return new Dialog(PhotoViewerCaptionEnterView.this.getContext()) {
                            @Override
                            public void dismiss() {
                                if (getParentActivity() instanceof LaunchActivity && ((LaunchActivity) getParentActivity()).getActionBarLayout() != null) {
                                    parentLayout = ((LaunchActivity) getParentActivity()).getActionBarLayout();
                                    if (parentLayout != null && parentLayout.getLastFragment() != null && parentLayout.getLastFragment().getVisibleDialog() != null) {
                                        Dialog dialog = parentLayout.getLastFragment().getVisibleDialog();
                                        if (dialog instanceof ChatAttachAlert) {
                                            ((ChatAttachAlert) dialog).dismiss(true);
                                        } else {
                                            dialog.dismiss();
                                        }
                                    }
                                }
                                PhotoViewer.getInstance().closePhoto(false, false);
                            }
                        };
                    }
                }, PremiumPreviewFragment.PREMIUM_FEATURE_ANIMATED_EMOJI, false).show();
            }

            @Override
            public void onCustomEmojiSelected(long documentId, TLRPC.Document document,  String emoticon, boolean isRecent) {
                int i = messageEditText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    innerTextChange = true;
                    SpannableString spannable = new SpannableString(emoticon);
                    AnimatedEmojiSpan span;
                    if (document != null) {
                        span = new AnimatedEmojiSpan(document, messageEditText.getPaint().getFontMetricsInt());
                    } else {
                        span = new AnimatedEmojiSpan(documentId, messageEditText.getPaint().getFontMetricsInt());
                    }
                    if (!isRecent) {
                        span.fromEmojiKeyboard = true;
                        span.cacheType = AnimatedEmojiDrawable.CACHE_TYPE_ALERT_PREVIEW;
                    }
                    spannable.setSpan(span, 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    messageEditText.setText(messageEditText.getText().insert(i, spannable));
                    int j = i + spannable.length();
                    messageEditText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e(e);
                } finally {
                    innerTextChange = false;
                }
            }

            @Override
            public void onEmojiSelected(String symbol) {
                int i = messageEditText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    innerTextChange = true;
                    CharSequence localCharSequence = Emoji.replaceEmoji(symbol, messageEditText.getPaint().getFontMetricsInt(), false);
                    messageEditText.setText(messageEditText.getText().insert(i, localCharSequence));
                    int j = i + localCharSequence.length();
                    messageEditText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e(e);
                } finally {
                    innerTextChange = false;
                }
            }
        });
        sizeNotifierLayout.addView(emojiView);
    }

    public void addEmojiToRecent(String code) {
        createEmojiView();
        emojiView.addEmojiToRecent(code);
    }

    public void replaceWithText(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, messageEditText.getPaint().getFontMetricsInt(), false);
            }
            messageEditText.setText(builder);
            messageEditText.setSelection(Math.min(start + text.length(), messageEditText.length()));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setFieldFocused(boolean focus) {
        if (messageEditText == null) {
            return;
        }
        if (focus) {
            if (!messageEditText.isFocused()) {
                messageEditText.postDelayed(() -> {
                    if (messageEditText != null) {
                        try {
                            messageEditText.requestFocus();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }, 600);
            }
        } else {
            if (messageEditText.isFocused() && !keyboardVisible) {
                messageEditText.clearFocus();
            }
        }
    }

    public CharSequence getFieldCharSequence() {
        return AndroidUtilities.getTrimmedString(messageEditText.getText());
    }

    public int getEmojiPadding() {
        return emojiPadding;
    }

    public boolean isPopupView(View view) {
        return view == emojiView;
    }

    int lastShow;
    private void showPopup(int show, boolean animated) {
        lastShow = show;
        if (show == 1) {
            createEmojiView();

            emojiView.setVisibility(VISIBLE);
            delegate.onEmojiViewOpen();

            if (keyboardHeight <= 0) {
                keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200));
            }
            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? keyboardHeightLand : keyboardHeight;

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emojiView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = currentHeight;
            emojiView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !forceFloatingEmoji) {
                AndroidUtilities.hideKeyboard(messageEditText);
            }
            if (sizeNotifierLayout != null) {
                emojiPadding = currentHeight;
                sizeNotifierLayout.requestLayout();
                emojiIconDrawable.setIcon(R.drawable.input_keyboard, true);
                onWindowSizeChanged();
            }
        } else {
            if (emojiButton != null) {
                emojiIconDrawable.setIcon(R.drawable.input_smile, true);
            }
            if (sizeNotifierLayout != null) {
                if (animated && show == 0 && emojiView != null) {
                    ValueAnimator animator = ValueAnimator.ofFloat(emojiPadding, 0);
                    float animateFrom = emojiPadding;
                    popupAnimating = true;
                    delegate.onEmojiViewCloseStart();
                    animator.addUpdateListener(animation -> {
                        float v = (float) animation.getAnimatedValue();
                        emojiPadding = (int) v;
                        emojiView.setTranslationY(animateFrom - v);
                        setTranslationY(animateFrom - v);
                        setAlpha(v / animateFrom);
                        emojiView.setAlpha(v / animateFrom);
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            emojiPadding = 0;
                            setTranslationY(0);
                            setAlpha(1f);
                            emojiView.setTranslationY(0);
                            popupAnimating = false;
                            delegate.onEmojiViewCloseEnd();
                            emojiView.setVisibility(GONE);
                            emojiView.setAlpha(1f);
                        }
                    });
                    animator.setDuration(210);
                    animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    animator.start();
                } else if (show == 0) {
                    if (emojiView != null) {
                        emojiView.setVisibility(GONE);
                    }
                    emojiPadding = 0;
                }
                sizeNotifierLayout.requestLayout();
                onWindowSizeChanged();
            }
        }
    }

    public void hidePopup() {
        if (isPopupShowing()) {
            showPopup(0, true);
        }
    }

    private void openKeyboardInternal() {
        showPopup(AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput ? 0 : 2, false);
        openKeyboard();
    }

    public void openKeyboard() {
        messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(messageEditText);
        try {
            messageEditText.setSelection(messageEditText.length(), messageEditText.length());
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean isPopupShowing() {
        return emojiView != null && emojiView.getVisibility() == VISIBLE;
    }

    public boolean isPopupAnimating() {
        return popupAnimating;
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(messageEditText);
        messageEditText.clearFocus();
    }

    public boolean isKeyboardVisible() {
        return (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) && getTag() != null || keyboardVisible;
    }

    @Override
    public void onSizeChanged(int height, boolean isWidthGreater) {
        if (height > AndroidUtilities.dp(50) && keyboardVisible && !AndroidUtilities.isInMultiwindow && !forceFloatingEmoji) {
            if (isWidthGreater) {
                keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", keyboardHeightLand).commit();
            } else {
                keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", keyboardHeight).commit();
            }
        }

        if (isPopupShowing()) {
            int newHeight;
            if (isWidthGreater) {
                newHeight = keyboardHeightLand;
            } else {
                newHeight = keyboardHeight;
            }

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emojiView.getLayoutParams();
            if (layoutParams.width != AndroidUtilities.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                emojiView.setLayoutParams(layoutParams);
                if (sizeNotifierLayout != null) {
                    emojiPadding = layoutParams.height;
                    sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }

        if (lastSizeChangeValue1 == height && lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        lastSizeChangeValue1 = height;
        lastSizeChangeValue2 = isWidthGreater;

        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && isPopupShowing()) {
            showPopup(0, false);
        }
        if (emojiPadding != 0 && !keyboardVisible && keyboardVisible != oldValue && !isPopupShowing()) {
            emojiPadding = 0;
            sizeNotifierLayout.requestLayout();
        }
        onWindowSizeChanged();
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }
        }
    }

    public void setAllowTextEntitiesIntersection(boolean value) {
        messageEditText.setAllowTextEntitiesIntersection(value);
    }

    public EditTextCaption getMessageEditText() {
        return messageEditText;
    }

    private int getThemedColor(int key) {
        return Theme.getColor(key, resourcesProvider);
    }

    public Theme.ResourcesProvider getResourcesProvider() {
        return resourcesProvider;
    }
}
