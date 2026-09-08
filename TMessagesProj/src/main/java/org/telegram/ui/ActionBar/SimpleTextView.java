layout = new StaticLayout(string, 0, string.length(), textPaint, scrollNonFitText || ellipsizeByGradient ? dp(2000) : width + dp(8), getAlignment(), 1.0f, 0.0f, false);
                }

                spoilersPool.addAll(spoilers);
                spoilers.clear();
                if (layout != null && layout.getText() instanceof Spannable) {
                    SpoilerEffect.addSpoilers(this, layout, -2, -2, spoilersPool, spoilers);
                }
                calcOffset(width);
            } catch (Exception ignore) {

            }
        } else {
            layout = null;
            textWidth = 0;
            textHeight = 0;
        }
        AnimatedEmojiSpan.release(this, emojiStack);
        if (attachedToWindow) {
            emojiStack = AnimatedEmojiSpan.update(emojiCacheType, this, emojiStack, layout);
        }
        invalidate();
        return true;
    }

    public void setAlignment(Layout.Alignment alignment) {
        mAlignment = alignment;
        requestLayout();
    }

    private Layout.Alignment getAlignment() {
        return mAlignment;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (lastWidth != AndroidUtilities.displaySize.x) {
            lastWidth = AndroidUtilities.displaySize.x;
            scrollingOffset = 0;
            currentScrollDelay = SCROLL_DELAY_MS;
            checkUi_layerType();
        }
        createLayout(width - getPaddingLeft() - getPaddingRight() - minusWidth - (leftDrawableOutside && leftDrawable != null ? leftDrawable.getIntrinsicWidth() + drawablePadding : 0) - (rightDrawableOutside && rightDrawable != null ? rightDrawable.getIntrinsicWidth() + drawablePadding : 0) - (rightDrawableOutside && rightDrawable2 != null ? rightDrawable2.getIntrinsicWidth() + drawablePadding : 0));

        int finalHeight;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            finalHeight = height;
        } else {
            finalHeight = getPaddingTop() + textHeight + getPaddingBottom();
        }
        if (widthWrapContent) {
//            textWidth = (int) Math.ceil(layout.getLineWidth(0));
            width = Math.min(width, getPaddingLeft() + textWidth + getPaddingRight() + minusWidth + (leftDrawableOutside && leftDrawable != null ? leftDrawable.getIntrinsicWidth() + drawablePadding : 0) + (rightDrawableOutside && rightDrawable != null ? rightDrawable.getIntrinsicWidth() + drawablePadding : 0) + (rightDrawableOutside && rightDrawable2 != null ? rightDrawable2.getIntrinsicWidth() + drawablePadding : 0));
        }
        setMeasuredDimension(width, finalHeight);

        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
            offsetY = getPaddingTop() + (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - textHeight) / 2;
        } else {
            offsetY = getPaddingTop();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        wasLayout = true;
    }

    public int getTextWidth() {
        return textWidth + (rightDrawableInside ? (rightDrawable != null ? (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale) : 0) + (rightDrawable2 != null ? (int) (rightDrawable2.getIntrinsicWidth() * rightDrawableScale) : 0) : 0);
    }

    public int getRightDrawableWidth() {
        if (rightDrawable == null)
            return 0;
        return (int) (drawablePadding + rightDrawable.getIntrinsicWidth() * rightDrawableScale);
    }

    public int getTextHeight() {
        return textHeight;
    }

    public void setLeftDrawableTopPadding(int value) {
        leftDrawableTopPadding = value;
    }

    public void setRightDrawableTopPadding(int value) {
        rightDrawableTopPadding = value;
    }

    public void setLeftDrawable(int resId) {
        setLeftDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public Drawable getLeftDrawable() {
        return leftDrawable;
    }

    public void setRightDrawable(int resId) {
        setRightDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public void setMinWidth(int width) {
        minWidth = width;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (maxLines > 1) {
            super.setBackgroundDrawable(background);
            return;
        }
        wrapBackgroundDrawable = background;
    }

    @Override
    public Drawable getBackground() {
        if (wrapBackgroundDrawable != null) {
            return wrapBackgroundDrawable;
        }
        return super.getBackground();
    }

    public void setLeftDrawable(Drawable drawable) {
        if (leftDrawable == drawable) {
            return;
        }
        if (leftDrawable != null) {
            leftDrawable.setCallback(null);
        }
        leftDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return who == rightDrawable || who == rightDrawable2 || who == leftDrawable || super.verifyDrawable(who);
    }

    public void replaceTextWithDrawable(Drawable drawable, String replacedText) {
        if (replacedDrawable == drawable) {
            return;
        }
        if (replacedDrawable != null) {
            replacedDrawable.setCallback(null);
        }
        replacedDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
        this.replacedText = replacedText;
    }

    public void setMinusWidth(int value) {
        if (value == minusWidth) {
            return;
        }
        minusWidth = value;
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    public Drawable getRightDrawable() {
        return rightDrawable;
    }

    public boolean setRightDrawable(Drawable drawable) {
        if (rightDrawable == drawable) {
            return false;
        }
        if (rightDrawable != null) {
            rightDrawable.setCallback(null);
        }
        rightDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
        return true;
    }

    public boolean setRightDrawable2(Drawable drawable) {
        if (rightDrawable2 == drawable) {
            return false;
        }
        if (rightDrawable2 != null) {
            rightDrawable2.setCallback(null);
        }
        rightDrawable2 = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
        return true;
    }

    public Drawable getRightDrawable2() {
        return rightDrawable2;
    }

    public void setRightDrawableScale(float scale) {
        rightDrawableScale = scale;
    }

    public void setSideDrawablesColor(int color) {
        Theme.setDrawableColor(rightDrawable, color);
        Theme.setDrawableColor(leftDrawable, color);
    }

    public boolean setText(CharSequence value) {
        return setText(value, false);
    }

    public boolean setText(CharSequence value, boolean force) {
        if (text == null && value == null || !force && text != null && text.equals(value)) {
            return false;
        }
        text = value;
        currentScrollDelay = SCROLL_DELAY_MS;
        recreateLayoutMaybe();
        return true;
    }

    public void resetScrolling() {
        scrollingOffset = 0;
        checkUi_layerType();
    }

    public void copyScrolling(SimpleTextView textView) {
        scrollingOffset = textView.scrollingOffset;
        checkUi_layerType();
    }

    public void setDrawablePadding(int value) {
        if (drawablePadding == value) {
            return;
        }
        drawablePadding = value;
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    private boolean recreateLayoutMaybe() {
        if (wasLayout && getMeasuredHeight() != 0 && !buildFullLayout) {
            boolean result = createLayout(getMaxTextWidth() - getPaddingLeft() - getPaddingRight() - minusWidth);
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                offsetY = (getMeasuredHeight() - textHeight) / 2;
            } else {
                offsetY = getPaddingTop();
            }
            return result;
        } else {
            requestLayout();
        }
        return true;
    }

    public CharSequence getText() {
        if (text == null) {
            return "";
        }
        return text;
    }

    public int getLineCount() {
        int count = 0;
        if (layout != null) {
            count += layout.getLineCount();
        }
        if (fullLayout != null) {
            count += fullLayout.getLineCount();
        }
        return count;
    }

    public int getTextStartX() {
        if (layout == null) {
            return 0;
        }
        int textOffsetX = 0;
        if (leftDrawable != null) {
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT) {
                textOffsetX += drawablePadding + leftDrawable.getIntrinsicWidth();
            }
        }
        if (replacedDrawable != null && replacingDrawableTextIndex < 0) {
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT) {
                textOffsetX += drawablePadding + replacedDrawable.getIntrinsicWidth();
            }
        }
        return (int) getX() + offsetX + textOffsetX;
    }

    public TextPaint getTextPaint() {
        return textPaint;
    }

    public int getTextStartY() {
        if (layout == null) {
            return 0;
        }
        return (int) getY();
    }

    public void setRightPadding(int padding) {
        if (paddingRight != padding) {
            paddingRight = padding;

            int width = getMaxTextWidth() - getPaddingLeft() - getPaddingRight() - minusWidth;
            if (leftDrawable != null && !leftDrawableOutside) {
                width -= leftDrawable.getIntrinsicWidth();
                width -= drawablePadding;
            }
            int rightDrawableWidth = 0;
            if (!rightDrawableInside) {
                if (rightDrawable != null && !rightDrawableOutside) {
                    rightDrawableWidth = (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale);
                    width -= rightDrawableWidth;
                    width -= drawablePadding;
                }
                if (rightDrawable2 != null && !rightDrawableOutside) {
                    rightDrawableWidth = (int) (rightDrawable2.getIntrinsicWidth() * rightDrawableScale);
                    width -= rightDrawableWidth;
                    width -= drawablePadding;
                }
            }
            if (replacedText != null && replacedDrawable != null) {
                if ((replacingDrawableTextIndex = text.toString().indexOf(replacedText)) < 0) {
                    width -= replacedDrawable.getIntrinsicWidth();
                    width -= drawablePadding;
                }
            }
            if (canHideRightDrawable && rightDrawableWidth != 0 && !rightDrawableOutside) {
                CharSequence string = TextUtils.ellipsize(text, textPaint, width, TextUtils.TruncateAt.END);
                if (!text.equals(string)) {
                    rightDrawableHidden = true;
                    width += rightDrawableWidth;
                    width += drawablePadding;
                }
            }
            calcOffset(width);

            invalidate();
        }
    }

    private void checkUi_layerType() {
        final boolean fade = scrollNonFitText && (textDoesNotFit || scrollingOffset != 0);
        final boolean needHardwareLayer = fade || ellipsizeByGradient;
        final int layerType = needHardwareLayer ? LAYER_TYPE_HARDWARE : LAYER_TYPE_NONE;
        if (getLayerType() != layerType) {
            setLayerType(layerType, null);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int textOffsetX = 0;
        layoutX = 0;
        layoutY = 0;

        boolean fade = scrollNonFitText && (textDoesNotFit || scrollingOffset != 0);
        totalWidth = textWidth;
        if (leftDrawable != null && !leftDrawableOutside) {
            int x = (int) -scrollingOffset;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                x += offsetX;
            }
            int y;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                y = (getMeasuredHeight() - leftDrawable.getIntrinsicHeight()) / 2 + leftDrawableTopPadding;
            } else {
                y = getPaddingTop() + (textHeight - leftDrawable.getIntrinsicHeight()) / 2 + leftDrawableTopPadding;
            }
            leftDrawable.setBounds(x, y, x + leftDrawable.getIntrinsicWidth(), y + leftDrawable.getIntrinsicHeight());
            leftDrawable.draw(canvas);
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT || (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                textOffsetX += drawablePadding + leftDrawable.getIntrinsicWidth();
            }
            totalWidth += drawablePadding + leftDrawable.getIntrinsicWidth();
        } else if (leftDrawableOutside && leftDrawable != null) {
            textOffsetX += drawablePadding + leftDrawable.getIntrinsicWidth();
        }
        if (replacedDrawable != null && replacedText != null) {
            int x = (int) (-scrollingOffset + replacingDrawableTextOffset);
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                x += offsetX;
            }
            int y;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                y = (getMeasuredHeight() - replacedDrawable.getIntrinsicHeight()) / 2 + leftDrawableTopPadding;
            } else {
                y = (textHeight - replacedDrawable.getIntrinsicHeight()) / 2 + leftDrawableTopPadding;
            }
            replacedDrawable.setBounds(x, y, x + replacedDrawable.getIntrinsicWidth(), y + replacedDrawable.getIntrinsicHeight());
            replacedDrawable.draw(canvas);
            if (replacingDrawableTextIndex < 0) {
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT || (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                    textOffsetX += drawablePadding + replacedDrawable.getIntrinsicWidth();
                }
                totalWidth += drawablePadding + replacedDrawable.getIntrinsicWidth();
            }
        }

        if (rightDrawable != null && !rightDrawableHidden && rightDrawableScale > 0 && !rightDrawableOutside && !rightDrawableInside) {
            int x = textOffsetX + textWidth + drawablePadding + (int) -scrollingOffset;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL ||
                    (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
                x += offsetX;
            }
            int dw = (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale);
            int dh = (int) (rightDrawable.getIntrinsicHeight() * rightDrawableScale);
            int y;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
            } else {
                y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
            }
            rightDrawable.setBounds(x, y, x + dw, y + dh);
            rightDrawableX = x + (dw >> 1);
            rightDrawableY = y + (dh >> 1);
            rightDrawable.draw(canvas);
            totalWidth += drawablePadding + dw;
        }
        if (rightDrawable2 != null && !rightDrawableHidden && rightDrawableScale > 0 && !rightDrawableOutside && !rightDrawableInside) {
            int x = textOffsetX + textWidth + drawablePadding + (int) -scrollingOffset;
            if (rightDrawable != null) {
                x += (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale) + drawablePadding;
            }
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL ||
                    (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
                x += offsetX;
            }
            int dw = (int) (rightDrawable2.getIntrinsicWidth() * rightDrawableScale);
            int dh = (int) (rightDrawable2.getIntrinsicHeight() * rightDrawableScale);
            int y;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
            } else {
                y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
            }
            rightDrawable2.setBounds(x, y, x + dw, y + dh);
            rightDrawable2.draw(canvas);
            totalWidth += drawablePadding + dw;
        }
        int nextScrollX = totalWidth + dp(DIST_BETWEEN_SCROLLING_TEXT);

        if (scrollingOffset != 0) {
            if (leftDrawable != null && !leftDrawableOutside) {
                int x = (int) -scrollingOffset + nextScrollX;
                int y;
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                    y = (getMeasuredHeight() - leftDrawable.getIntrinsicHeight()) / 2 + leftDrawableTopPadding;
                } else {
                    y = getPaddingTop() + (textHeight - leftDrawable.getIntrinsicHeight()) / 2 + leftDrawableTopPadding;
                }
                leftDrawable.setBounds(x, y, x + leftDrawable.getIntrinsicWidth(), y + leftDrawable.getIntrinsicHeight());
                leftDrawable.draw(canvas);
            }
            if (rightDrawable != null && !rightDrawableOutside) {
                int dw = (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale);
                int dh = (int) (rightDrawable.getIntrinsicHeight() * rightDrawableScale);
                int x = textOffsetX + textWidth + drawablePadding + (int) -scrollingOffset + nextScrollX;
                int y;
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                    y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
                } else {
                    y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
                }
                rightDrawable.setBounds(x, y, x + dw, y + dh);
                rightDrawableX = x + (dw >> 1);
                rightDrawableY = y + (dh >> 1);
                rightDrawable.draw(canvas);
            }
            if (rightDrawable2 != null && !rightDrawableOutside) {
                int dw = (int) (rightDrawable2.getIntrinsicWidth() * rightDrawableScale);
                int dh = (int) (rightDrawable2.getIntrinsicHeight() * rightDrawableScale);
                int x = textOffsetX + textWidth + drawablePadding + (int) -scrollingOffset + nextScrollX;
                if (rightDrawable != null) {
                    x += (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale) + drawablePadding;
                }
                int y;
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                    y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
                } else {
                    y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
                }
                rightDrawable2.setBounds(x, y, x + dw, y + dh);
                rightDrawable2.draw(canvas);
            }
        }

        if (layout != null) {
            if (leftDrawableOutside || rightDrawableOutside || ellipsizeByGradient || paddingRight > 0) {
                canvas.save();
                canvas.clipRect(textOffsetX, 0, getMaxTextWidth() - paddingRight - dp(rightDrawable != null && !(rightDrawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) && rightDrawableOutside ? 2 : 0), getMeasuredHeight());
            }
            Emoji.emojiDrawingUseAlpha = usaAlphaForEmoji;
            if (wrapBackgroundDrawable != null) {
                int cx = (int) (offsetX + textOffsetX - scrollingOffset) + textWidth / 2;
                int w = Math.max(textWidth + getPaddingLeft() + getPaddingRight(), minWidth);
                int x = cx - w / 2;
                wrapBackgroundDrawable.setBounds(x, 0, x + w, getMeasuredHeight());
                wrapBackgroundDrawable.draw(canvas);
            }
            if (offsetX + textOffsetX != 0 || offsetY != 0 || scrollingOffset != 0) {
                canvas.save();
                canvas.translate(offsetX + textOffsetX - scrollingOffset, offsetY);
                layoutX += offsetX + textOffsetX - scrollingOffset;
                layoutY += offsetY;
            }
            drawLayout(canvas);
            if (partLayout != null && fullAlpha < 1.0f) {
                int prevAlpha = textPaint.getAlpha();
                textPaint.setAlpha((int) (255 * (1.0f - fullAlpha)));
                canvas.save();
                float partOffset = 0;
                if (partLayout.getText().length() == 1) {
                    partOffset = fullTextMaxLines == 1 ? dp(0.5f) : dp(4);
                }
                if (layout.getLineLeft(0) != 0) {
                    canvas.translate(-layout.getLineWidth(0) + partOffset, 0);
                } else {
                    canvas.translate(layout.getLineWidth(0) - partOffset, 0);
                }
                canvas.translate(-fullLayoutLeftOffset * fullAlpha + fullLayoutLeftCharactersOffset * fullAlpha, 0);
                partLayout.draw(canvas);
                canvas.restore();
                textPaint.setAlpha(prevAlpha);
            }
            if (fullLayout != null && fullAlpha > 0) {
                int prevAlpha = textPaint.getAlpha();
                textPaint.setAlpha((int) (255 * fullAlpha));

                canvas.translate(-fullLayoutLeftOffset * fullAlpha + fullLayoutLeftCharactersOffset * fullAlpha - fullLayoutLeftCharactersOffset, 0);
                fullLayout.draw(canvas);
                textPaint.setAlpha(prevAlpha);
            }
            if (scrollingOffset != 0) {
                canvas.translate(nextScrollX, 0);
                drawLayout(canvas);
            }
            if (offsetX + textOffsetX != 0 || offsetY != 0 || scrollingOffset != 0) {
                canvas.restore();
            }
            if (rightDrawable != null && !rightDrawableHidden && rightDrawableScale > 0 && !rightDrawableOutside && rightDrawableInside) {
                int x = textOffsetX + textWidth + drawablePadding + (int) -scrollingOffset;
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL ||
                        (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
                    x += offsetX;
                }
                int dw = (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale);
                int dh = (int) (rightDrawable.getIntrinsicHeight() * rightDrawableScale);
                int y;
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                    y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
                } else {
                    y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
                }
                rightDrawable.setBounds(x, y, x + dw, y + dh);
                rightDrawableX = x + (dw >> 1);
                rightDrawableY = y + (dh >> 1);
                rightDrawable.draw(canvas);
                totalWidth += drawablePadding + dw;
            }
            if (rightDrawable2 != null && !rightDrawableHidden && rightDrawableScale > 0 && !rightDrawableOutside && rightDrawableInside) {
                int x = textOffsetX + textWidth + drawablePadding + (int) -scrollingOffset;
                if (rightDrawable != null) {
                    x += (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale) + drawablePadding;
                }
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL ||
                        (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
                    x += offsetX;
                }
                int dw = (int) (rightDrawable2.getIntrinsicWidth() * rightDrawableScale);
                int dh = (int) (rightDrawable2.getIntrinsicHeight() * rightDrawableScale);
                int y;
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                    y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
                } else {
                    y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
                }
                rightDrawable2.setBounds(x, y, x + dw, y + dh);
                rightDrawable2.draw(canvas);
                totalWidth += drawablePadding + dw;
            }
            if (fade) {
                if (scrollingOffset < dp(10)) {
                    fadePaint.setAlpha((int) (255 * (scrollingOffset / dp(10))));
                } else if (scrollingOffset > totalWidth + dp(DIST_BETWEEN_SCROLLING_TEXT) - dp(10)) {
                    float dist = scrollingOffset - (totalWidth + dp(DIST_BETWEEN_SCROLLING_TEXT) - dp(10));
                    fadePaint.setAlpha((int) (255 * (1.0f - dist / dp(10))));
                } else {
                    fadePaint.setAlpha(255);
                }
                canvas.drawRect(textOffsetX, 0, textOffsetX + dp(6), getMeasuredHeight(), fadePaint);
                canvas.save();
                canvas.translate(getMaxTextWidth() - paddingRight - dp(6), 0);
                canvas.drawRect(0, 0, 0 + dp(6), getMeasuredHeight(), fadePaintBack);
                canvas.restore();
            } else if (ellipsizeByGradient && textDoesNotFit && fadeEllpsizePaint != null) {
                canvas.save();
                updateFadePaints();
                if (!ellipsizeByGradientLeft) {
                    canvas.translate(getMaxTextWidth() - paddingRight - fadeEllpsizePaintWidth - dp(rightDrawable != null && !(rightDrawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) && rightDrawableOutside ? +2 : 0), 0);
                }
                canvas.drawRect(textOffsetX, 0, fadeEllpsizePaintWidth, getMeasuredHeight(), fadeEllpsizePaint);
                canvas.restore();
            }
            updateScrollAnimation();
            Emoji.emojiDrawingUseAlpha = true;
            if (leftDrawableOutside || rightDrawableOutside || ellipsizeByGradient || paddingRight > 0) {
                canvas.restore();
            }
        }

        if (leftDrawable != null && leftDrawableOutside) {
            int x = 0;
            int dw = (int) (leftDrawable.getIntrinsicWidth());
            int dh = (int) (leftDrawable.getIntrinsicHeight());
            int y;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                y = (getMeasuredHeight() - dh) / 2 + leftDrawableTopPadding;
            } else {
                y = getPaddingTop() + (textHeight - dh) / 2 + leftDrawableTopPadding;
            }
            leftDrawable.setBounds(x, y, x + dw, y + dh);
            leftDrawable.draw(canvas);
        }
        if (rightDrawable != null && rightDrawableOutside) {
            int x = Math.min(textOffsetX + textWidth + drawablePadding + (scrollingOffset == 0 ? -nextScrollX : (int) -scrollingOffset) + nextScrollX, getMaxTextWidth() - paddingRight + drawablePadding);
            int dw = (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale);
            int dh = (int) (rightDrawable.getIntrinsicHeight() * rightDrawableScale);
            int y;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
            } else {
                y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
            }
            rightDrawable.setBounds(x, y, x + dw, y + dh);
            rightDrawableX = x + (dw >> 1);
            rightDrawableY = y + (dh >> 1);
            rightDrawable.draw(canvas);
        }
        if (rightDrawable2 != null && rightDrawableOutside) {
            int x = Math.min(
                    textOffsetX + textWidth + drawablePadding + (scrollingOffset == 0 ? -nextScrollX : (int) -scrollingOffset) + nextScrollX,
                    getMaxTextWidth() - paddingRight + drawablePadding
            );
            if (rightDrawable != null) {
                x += (int) (rightDrawable.getIntrinsicWidth() * rightDrawableScale) + drawablePadding;
            }
            int dw = (int) (rightDrawable2.getIntrinsicWidth() * rightDrawableScale);
            int dh = (int) (rightDrawable2.getIntrinsicHeight() * rightDrawableScale);
            int y;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                y = (getMeasuredHeight() - dh) / 2 + rightDrawableTopPadding;
            } else {
                y = getPaddingTop() + (textHeight - dh) / 2 + rightDrawableTopPadding;
            }
            rightDrawable2.setBounds(x, y, x + dw, y + dh);
            rightDrawable2.draw(canvas);
        }
    }

    public int getRightDrawableX() {
        return rightDrawableX;
    }

    public int getRightDrawableY() {
        return rightDrawableY;
    }

    public int getMaxTextWidth() {
        return getMeasuredWidth() - (rightDrawableOutside && rightDrawable != null ? rightDrawable.getIntrinsicWidth() + drawablePadding : 0) - (rightDrawableOutside && rightDrawable2 != null ? rightDrawable2.getIntrinsicWidth() + drawablePadding : 0);
    }

    public float getExactWidth() {
        return getPaint().measureText(getText().toString())
                + getSideDrawablesSize()
                - (leftDrawable != null || rightDrawable != null || rightDrawable2 != null ? drawablePadding : 0);
    }

    public float getExactWidthIncludeDrawables() {
        return getExactWidth()
            + (leftDrawable != null ? leftDrawable.getIntrinsicWidth() : 0)
            + (rightDrawable != null ? rightDrawable.getIntrinsicWidth() : 0)
            + (rightDrawable2 != null ? rightDrawable2.getIntrinsicWidth() : 0);
    }

    private void drawLayout(Canvas canvas) {
        if (fullAlpha > 0 && fullLayoutLeftOffset != 0) {
            canvas.save();
            canvas.translate(-fullLayoutLeftOffset * fullAlpha + fullLayoutLeftCharactersOffset * fullAlpha, 0);
            layoutX += -fullLayoutLeftOffset * fullAlpha + fullLayoutLeftCharactersOffset * fullAlpha;

            canvas.save();
            clipOutSpoilers(canvas);
            if (emojiStack != null) {
                emojiStack.clearPositions();
            }
            layout.draw(canvas);
            canvas.restore();

            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, layout, emojiStack, 0, null, 0, 0, 0, 1f, emojiStackColorFilter);
            drawSpoilers(canvas);
            canvas.restore();
        } else {
            canvas.save();
            clipOutSpoilers(canvas);
            if (emojiStack != null) {
                emojiStack.clearPositions();
            }
            layout.draw(canvas);
            canvas.restore();

            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, layout, emojiStack, 0, null, 0, 0, 0, 1f, emojiStackColorFilter);
            drawSpoilers(canvas);
        }
    }

    private void clipOutSpoilers(Canvas canvas) {
        if (spoilers.isEmpty()) {
            // nothing to clip
            return;
        }
        path.rewind();
        for (SpoilerEffect eff : spoilers) {
            Rect b = eff.getBounds();
            path.addRect(b.left, b.top, b.right, b.bottom, Path.Direction.CW);
        }
        canvas.clipPath(path, Region.Op.DIFFERENCE);
    }

    private void drawSpoilers(Canvas canvas) {
        for (SpoilerEffect eff : spoilers)
            eff.draw(canvas);
    }

    private void updateScrollAnimation() {
        if (!scrollNonFitText || !textDoesNotFit && scrollingOffset == 0) {
            return;
        }
        long newUpdateTime = SystemClock.elapsedRealtime();
        long dt = newUpdateTime - lastUpdateTime;
        if (dt > 17) {
            dt = 17;
        }
        if (currentScrollDelay > 0) {
            currentScrollDelay -= dt;
        } else {
            int totalDistance = totalWidth + dp(DIST_BETWEEN_SCROLLING_TEXT);
            float pixelsPerSecond;
            if (scrollingOffset < dp(SCROLL_SLOWDOWN_PX)) {
                pixelsPerSecond = PIXELS_PER_SECOND_SLOW + (PIXELS_PER_SECOND - PIXELS_PER_SECOND_SLOW) * (scrollingOffset / dp(SCROLL_SLOWDOWN_PX));
            } else if (scrollingOffset >= totalDistance - dp(SCROLL_SLOWDOWN_PX)) {
                float dist = scrollingOffset - (totalDistance - dp(SCROLL_SLOWDOWN_PX));
                pixelsPerSecond = PIXELS_PER_SECOND - (PIXELS_PER_SECOND - PIXELS_PER_SECOND_SLOW) * (dist / dp(SCROLL_SLOWDOWN_PX));
            } else {
                pixelsPerSecond = PIXELS_PER_SECOND;
            }
            scrollingOffset += dt / 1000.0f * dp(pixelsPerSecond);
            lastUpdateTime = newUpdateTime;
            if (scrollingOffset > totalDistance) {
                scrollingOffset = 0;
                currentScrollDelay = SCROLL_DELAY_MS;
            }
            checkUi_layerType();
        }
        invalidate();
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if (who == leftDrawable) {
            invalidate(leftDrawable.getBounds());
        } else if (who == rightDrawable) {
            invalidate(rightDrawable.getBounds());
        } else if (who == rightDrawable2) {
            invalidate(rightDrawable2.getBounds());
        } else if (who == replacedDrawable) {
            invalidate(replacedDrawable.getBounds());
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setVisibleToUser(true);
        info.setClassName("android.widget.TextView");
        info.setText(text);
    }

    public void setFullLayoutAdditionalWidth(int fullLayoutAdditionalWidth, int fullLayoutLeftOffset) {
        if (this.fullLayoutAdditionalWidth != fullLayoutAdditionalWidth || this.fullLayoutLeftOffset != fullLayoutLeftOffset) {
            this.fullLayoutAdditionalWidth = fullLayoutAdditionalWidth;
            this.fullLayoutLeftOffset = fullLayoutLeftOffset;
            createLayout(getMaxTextWidth() - getPaddingLeft() - getPaddingRight() - minusWidth);
        }
    }

    public void setFullTextMaxLines(int fullTextMaxLines) {
        this.fullTextMaxLines = fullTextMaxLines;
    }

    public int getTextColor() {
        return textPaint.getColor();
    }

    public void setCanHideRightDrawable(boolean b) {
        canHideRightDrawable = b;
    }

    public void setRightDrawableOutside(boolean outside) {
        rightDrawableOutside = outside;
    }

    public void setLeftDrawableOutside(boolean outside) {
        leftDrawableOutside = outside;
    }

    // right drawable is ellipsized with text
    public void setRightDrawableInside(boolean inside) {
        rightDrawableInside = inside;
    }


    public boolean getRightDrawableOutside() {
        return rightDrawableOutside;
    }

    public void setRightDrawableOnClick(OnClickListener onClickListener) {
        rightDrawableOnClickListener = onClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (rightDrawableOnClickListener != null && rightDrawable != null) {
            AndroidUtilities.rectTmp.set(rightDrawableX - dp(16), rightDrawableY - dp(16), rightDrawableX + dp(16), rightDrawableY + dp(16));
            if (event.getAction() == MotionEvent.ACTION_DOWN && AndroidUtilities.rectTmp.contains((int) event.getX(), (int) event.getY())) {
                maybeClick = true;
                touchDownX = event.getX();
                touchDownY = event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                if (rightDrawable instanceof PressableDrawable) {
                    ((PressableDrawable) rightDrawable).setPressed(true);
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE && maybeClick) {
                if (Math.abs(event.getX() - touchDownX) >= AndroidUtilities.touchSlop || Math.abs(event.getY() - touchDownY) >= AndroidUtilities.touchSlop) {
                    maybeClick = false;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    if (rightDrawable instanceof PressableDrawable) {
                        ((PressableDrawable) rightDrawable).setPressed(false);
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (maybeClick && event.getAction() == MotionEvent.ACTION_UP) {
                    rightDrawableOnClickListener.onClick(this);
                    if (rightDrawable instanceof PressableDrawable) {
                        ((PressableDrawable) rightDrawable).setPressed(false);
                    }
                }
                maybeClick = false;
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.onTouchEvent(event) || maybeClick;
    }

    public static interface PressableDrawable {
        public void setPressed(boolean value);

        public boolean isPressed();
    }
}
