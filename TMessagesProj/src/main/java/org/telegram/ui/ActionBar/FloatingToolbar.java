private int mLineHeight;
        private final int mIconTextSpacing;

        private final Runnable mPreparePopupContentRTLHelper = new Runnable() {
            @Override
            public void run() {
                setPanelsStatesAtRestingPosition();
                setContentAreaAsTouchableSurface();
                mContentContainer.setAlpha(1);
            }
        };
        private boolean mDismissed = true; // tracks whether this popup is dismissed or dismissing.
        private boolean mHidden; // tracks whether this popup is hidden or hiding.
        /* Calculated sizes for panels and overflow button. */
        private final Size mOverflowButtonSize;
        private Size mOverflowPanelSize;  // Should be null when there is no overflow.
        private Size mMainPanelSize;
        /* Item click listeners */
        private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;
        private final View.OnClickListener mMenuItemButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() instanceof MenuItem) {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.onMenuItemClick((MenuItem) v.getTag());
                    }
                }
            }
        };
        private boolean mOpenOverflowUpwards;  // Whether the overflow opens upwards or downwards.
        private boolean mIsOverflowOpen;
        private int mTransitionDurationScale;  // Used to scale the toolbar transition duration.


        public FloatingToolbarPopup(Context context, View parent) {
            mParent = parent;
            mContext = context;
            mContentContainer = createContentContainer(context);
            mPopupWindow = createPopupWindow(mContentContainer);
            mMarginHorizontal = dp(16);
            mMarginVertical = dp(8);
            mLineHeight = dp(48);
            mIconTextSpacing = dp(8);

            mLogAccelerateInterpolator = new LogAccelerateInterpolator();
            mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(mContext, android.R.interpolator.fast_out_slow_in);
            mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear_out_slow_in);
            mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(mContext, android.R.interpolator.fast_out_linear_in);

            mArrow = mContext.getDrawable(R.drawable.ft_avd_tooverflow).mutate();
            mArrow.setAutoMirrored(true);
            mOverflow = mContext.getDrawable(R.drawable.ft_avd_toarrow).mutate();
            mOverflow.setAutoMirrored(true);
            mToArrow = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.ft_avd_toarrow_animation).mutate();
            mToArrow.setAutoMirrored(true);
            mToOverflow = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.ft_avd_tooverflow_animation).mutate();
            mToOverflow.setAutoMirrored(true);

            mOverflowButton = new FrameLayout(mContext);
            mOverflowButtonIcon = new ImageButton(mContext) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent event) {
                    if (mIsOverflowOpen) {
                        return false;
                    }
                    return super.dispatchTouchEvent(event);
                }
            };
            mOverflowButtonIcon.setLayoutParams(new ViewGroup.LayoutParams(dp(56), dp(48)));
            mOverflowButtonIcon.setPaddingRelative(dp(16), dp(12), dp(16), dp(12));
            mOverflowButtonIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mOverflowButtonIcon.setImageDrawable(mOverflow);
            mOverflowButtonText = new TextView(mContext);
            mOverflowButtonText.setText(LocaleController.getString(R.string.Back));
            mOverflowButtonText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            mOverflowButtonText.setAlpha(0f);
            mOverflowButtonShadow = new View(mContext);
            int color;
            if (currentStyle == STYLE_DIALOG) {
                color = getThemedColor(Theme.key_dialogTextBlack);
                mOverflowButtonIcon.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), Theme.RIPPLE_MASK_CIRCLE_20DP));
                mOverflowButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), Theme.RIPPLE_MASK_ALL));
                mOverflowButtonShadow.setBackgroundColor(Theme.multAlpha(getThemedColor(Theme.key_dialogTextBlack), .4f));
            } else if (currentStyle == STYLE_BLACK) {
                color = 0xfffafafa;
                mOverflowButtonIcon.setBackground(Theme.createSelectorDrawable(0x20ffffff, Theme.RIPPLE_MASK_CIRCLE_20DP));
                mOverflowButton.setBackground(Theme.createSelectorDrawable(0x20ffffff, Theme.RIPPLE_MASK_ALL));
                mOverflowButtonShadow.setBackgroundColor(0x20FFFFFF);
            } else {
                color = getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
                mOverflowButtonIcon.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), Theme.RIPPLE_MASK_CIRCLE_20DP));
                mOverflowButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), Theme.RIPPLE_MASK_ALL));
                mOverflowButtonShadow.setBackgroundColor(getThemedColor(Theme.key_divider));
            }
            mOverflow.setTint(color);
            mArrow.setTint(color);
            mToArrow.setTint(color);
            mToOverflow.setTint(color);
            mOverflowButtonText.setTextColor(color);
            mOverflowButtonIcon.setOnClickListener(v -> onBackPressed());
            mOverflowButton.addView(mOverflowButtonIcon, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.LEFT));
            mOverflowButton.addView(mOverflowButtonText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.LEFT, 56, 0, 0, 0));
            mOverflowButton.addView(mOverflowButtonShadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 1f / AndroidUtilities.density, Gravity.TOP | Gravity.FILL_HORIZONTAL));
            mOverflowButtonSize = measure(mOverflowButtonIcon);
            mMainPanel = createMainPanel();
            mOverflowPanelViewHelper = new OverflowPanelViewHelper(mContext, mIconTextSpacing);
            mOverflowPanel = createOverflowPanel();

            Animation.AnimationListener mOverflowAnimationListener = createOverflowAnimationListener();
            mOpenOverflowAnimation = new AnimationSet(true);
            mOpenOverflowAnimation.setAnimationListener(mOverflowAnimationListener);
            mCloseOverflowAnimation = new AnimationSet(true);
            mCloseOverflowAnimation.setAnimationListener(mOverflowAnimationListener);
            mShowAnimation = createEnterAnimation(mContentContainer);
            mDismissAnimation = createExitAnimation(mContentContainer, 150, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(() -> {
                        mPopupWindow.dismiss();
                        mContentContainer.removeAllViews();
                    });
                }
            });
            mHideAnimation = createExitAnimation(mContentContainer, 0, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(() -> {
                        mPopupWindow.dismiss();
                    });
                }
            });
        }

        private void onBackPressed() {
            if (mIsOverflowOpen) {
                mOverflowButtonIcon.setImageDrawable(mToOverflow);
                mToOverflow.start();
                closeOverflow();
            } else {
                mOverflowButtonIcon.setImageDrawable(mToArrow);
                mToArrow.start();
                openOverflow();
            }
        }

        private void updateOverflowButtonClickListener() {
            if (mIsOverflowOpen) {
                mOverflowButton.setClickable(true);
                mOverflowButton.setOnClickListener(v -> onBackPressed());
                mOverflowButtonIcon.setClickable(false);
                mOverflowButtonIcon.setOnClickListener(null);
            } else {
                mOverflowButton.setClickable(false);
                mOverflowButton.setOnClickListener(null);
                mOverflowButtonIcon.setClickable(true);
                mOverflowButtonIcon.setOnClickListener(v -> onBackPressed());
            }
        }

        public boolean setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
            boolean ret = false;
            if (mPopupWindow.isOutsideTouchable() ^ outsideTouchable) {
                mPopupWindow.setOutsideTouchable(outsideTouchable);
                mPopupWindow.setFocusable(!outsideTouchable);
                ret = true;
            }
            mPopupWindow.setOnDismissListener(onDismiss);
            return ret;
        }

        public void layoutMenuItems(List<MenuItem> menuItems, MenuItem.OnMenuItemClickListener menuItemClickListener, int suggestedWidth) {
            mOnMenuItemClickListener = menuItemClickListener;
            cancelOverflowAnimations();
            clearPanels();
            menuItems = layoutMainPanelItems(menuItems, getAdjustedToolbarWidth(suggestedWidth));
            if (!menuItems.isEmpty()) {
                layoutOverflowPanelItems(menuItems);
            }
            updatePopupSize();
        }

        public void show(Rect contentRectOnScreen) {
            if (isShowing()) {
                return;
            }
            mHidden = false;
            mDismissed = false;
            cancelDismissAndHideAnimations();
            cancelOverflowAnimations();
            refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
            preparePopupContent();
            mPopupWindow.showAtLocation(mParent, Gravity.NO_GRAVITY, mCoordsOnWindow.x, mCoordsOnWindow.y);
            setTouchableSurfaceInsetsComputer();
            runShowAnimation();
        }

        public void dismiss() {
            if (mDismissed) {
                return;
            }
            mHidden = false;
            mDismissed = true;
            mHideAnimation.cancel();
            runDismissAnimation();
            setZeroTouchableSurface();
        }

        public void hide() {
            if (!isShowing()) {
                return;
            }
            mHidden = true;
            runHideAnimation();
            setZeroTouchableSurface();
        }

        public boolean isShowing() {
            return !mDismissed && !mHidden;
        }

        public boolean isHidden() {
            return mHidden;
        }

        public void updateCoordinates(Rect contentRectOnScreen) {
            if (!isShowing() || !mPopupWindow.isShowing()) {
                return;
            }
            cancelOverflowAnimations();
            refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
            preparePopupContent();
            mPopupWindow.update(mCoordsOnWindow.x, mCoordsOnWindow.y, mPopupWindow.getWidth(), mPopupWindow.getHeight());
        }

        private void refreshCoordinatesAndOverflowDirection(Rect contentRectOnScreen) {
            refreshViewPort();
            final int x = Math.min(contentRectOnScreen.centerX() - mPopupWindow.getWidth() / 2, mViewPortOnScreen.right - mPopupWindow.getWidth());
            final int y;
            final int availableHeightAboveContent = contentRectOnScreen.top - mViewPortOnScreen.top;
            final int availableHeightBelowContent = mViewPortOnScreen.bottom - contentRectOnScreen.bottom;
            final int margin = 2 * mMarginVertical;
            final int toolbarHeightWithVerticalMargin = mLineHeight + margin;
            if (!hasOverflow()) {
                if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin) {
                    y = contentRectOnScreen.top - toolbarHeightWithVerticalMargin;
                } else if (availableHeightBelowContent >= toolbarHeightWithVerticalMargin) {
                    y = contentRectOnScreen.bottom;
                } else if (availableHeightBelowContent >= mLineHeight) {
                    y = contentRectOnScreen.bottom - mMarginVertical;
                } else {
                    y = Math.max(mViewPortOnScreen.top, contentRectOnScreen.top - toolbarHeightWithVerticalMargin);
                }
            } else {
                final int minimumOverflowHeightWithMargin = calculateOverflowHeight(MIN_OVERFLOW_SIZE) + margin;
                final int availableHeightThroughContentDown = mViewPortOnScreen.bottom - contentRectOnScreen.top + toolbarHeightWithVerticalMargin;
                final int availableHeightThroughContentUp = contentRectOnScreen.bottom - mViewPortOnScreen.top + toolbarHeightWithVerticalMargin;
                if (availableHeightAboveContent >= minimumOverflowHeightWithMargin) {
                    updateOverflowHeight(availableHeightAboveContent - margin);
                    y = contentRectOnScreen.top - mPopupWindow.getHeight();
                    mOpenOverflowUpwards = true;
                } else if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin && availableHeightThroughContentDown >= minimumOverflowHeightWithMargin) {
                    updateOverflowHeight(availableHeightThroughContentDown - margin);
                    y = contentRectOnScreen.top - toolbarHeightWithVerticalMargin;
                    mOpenOverflowUpwards = false;
                } else if (availableHeightBelowContent >= minimumOverflowHeightWithMargin) {
                    updateOverflowHeight(availableHeightBelowContent - margin);
                    y = contentRectOnScreen.bottom;
                    mOpenOverflowUpwards = false;
                } else if (availableHeightBelowContent >= toolbarHeightWithVerticalMargin && mViewPortOnScreen.height() >= minimumOverflowHeightWithMargin) {
                    updateOverflowHeight(availableHeightThroughContentUp - margin);
                    y = contentRectOnScreen.bottom + toolbarHeightWithVerticalMargin - mPopupWindow.getHeight();
                    mOpenOverflowUpwards = true;
                } else {
                    updateOverflowHeight(mViewPortOnScreen.height() - margin);
                    y = mViewPortOnScreen.top;
                    mOpenOverflowUpwards = false;
                }
            }

            mParent.getRootView().getLocationOnScreen(mTmpCoords);
            int rootViewLeftOnScreen = mTmpCoords[0];
            int rootViewTopOnScreen = mTmpCoords[1];
            mParent.getRootView().getLocationInWindow(mTmpCoords);
            int rootViewLeftOnWindow = mTmpCoords[0];
            int rootViewTopOnWindow = mTmpCoords[1];
            int windowLeftOnScreen = rootViewLeftOnScreen - rootViewLeftOnWindow;
            int windowTopOnScreen = rootViewTopOnScreen - rootViewTopOnWindow;
            mCoordsOnWindow.set(Math.max(0, x - windowLeftOnScreen), Math.max(0, y - windowTopOnScreen));
        }

        private void runShowAnimation() {
            mShowAnimation.start();
        }

        private void runDismissAnimation() {
            mDismissAnimation.start();
        }

        private void runHideAnimation() {
            mHideAnimation.start();
        }

        private void cancelDismissAndHideAnimations() {
            mDismissAnimation.cancel();
            mHideAnimation.cancel();
        }

        private void cancelOverflowAnimations() {
            mContentContainer.clearAnimation();
            mMainPanel.animate().cancel();
            mOverflowPanel.animate().cancel();
            mToArrow.stop();
            mToOverflow.stop();
        }

        private void openOverflow() {
            final int targetWidth = mOverflowPanelSize.getWidth();
            final int targetHeight = mOverflowPanelSize.getHeight();
            final int startWidth = mContentContainer.getWidth();
            final int startHeight = mContentContainer.getHeight();
            final float startY = mContentContainer.getY();
            final float left = mContentContainer.getX();
            final float right = left + mContentContainer.getWidth();
            Animation widthAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaWidth = (int) (interpolatedTime * (targetWidth - startWidth));
                    setWidth(mContentContainer, startWidth + deltaWidth);
                    if (isInRTLMode()) {
                        mContentContainer.setX(left);
                        mMainPanel.setX(0);
                        mOverflowPanel.setX(0);
                    } else {
                        mContentContainer.setX(right - mContentContainer.getWidth());
                        mMainPanel.setX(mContentContainer.getWidth() - startWidth);
                        mOverflowPanel.setX(mContentContainer.getWidth() - targetWidth);
                    }
                }
            };
            Animation heightAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaHeight = (int) (interpolatedTime * (targetHeight - startHeight));
                    setHeight(mContentContainer, startHeight + deltaHeight);
                    if (mOpenOverflowUpwards) {
                        mContentContainer.setY(
                                startY - (mContentContainer.getHeight() - startHeight));
                        positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float overflowButtonStartX = mOverflowButton.getX();
            final float overflowButtonTargetX = isInRTLMode() ? overflowButtonStartX + targetWidth - mOverflowButtonIcon.getWidth() : overflowButtonStartX - targetWidth + mOverflowButtonIcon.getWidth();
            Animation overflowButtonAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    float overflowButtonX = overflowButtonStartX + interpolatedTime * (overflowButtonTargetX - overflowButtonStartX);
                    float deltaContainerWidth = isInRTLMode() ? 0 : mContentContainer.getWidth() - startWidth;
                    float actualOverflowButtonX = overflowButtonX + deltaContainerWidth;
                    mOverflowButton.setX(actualOverflowButtonX);
                    mOverflowButtonText.setAlpha(interpolatedTime);
                    mOverflowButtonShadow.setAlpha(interpolatedTime);
                }
            };
            widthAnimation.setInterpolator(mLogAccelerateInterpolator);
            widthAnimation.setDuration(getAdjustedDuration(250));
            heightAnimation.setInterpolator(mFastOutSlowInInterpolator);
            heightAnimation.setDuration(getAdjustedDuration(250));
            overflowButtonAnimation.setInterpolator(mFastOutSlowInInterpolator);
            overflowButtonAnimation.setDuration(getAdjustedDuration(250));
            mOpenOverflowAnimation.getAnimations().clear();
            mOpenOverflowAnimation.addAnimation(widthAnimation);
            mOpenOverflowAnimation.addAnimation(heightAnimation);
            mOpenOverflowAnimation.addAnimation(overflowButtonAnimation);
            mContentContainer.startAnimation(mOpenOverflowAnimation);
            mIsOverflowOpen = true;
            updateOverflowButtonClickListener();
            mMainPanel.animate().alpha(0).withLayer().setInterpolator(mLinearOutSlowInInterpolator).setDuration(250).start();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mOverflowButton.getLayoutParams();
            lp.width = mOverflowPanel.getWidth();
            mOverflowButton.setLayoutParams(lp);
            mOverflowPanel.setAlpha(1);
        }

        private void closeOverflow() {
            final int targetWidth = mMainPanelSize.getWidth();
            final int startWidth = mContentContainer.getWidth();
            final float left = mContentContainer.getX();
            final float right = left + mContentContainer.getWidth();
            Animation widthAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaWidth = (int) (interpolatedTime * (targetWidth - startWidth));
                    setWidth(mContentContainer, startWidth + deltaWidth);
                    if (isInRTLMode()) {
                        mContentContainer.setX(left);
                        mMainPanel.setX(0);
                        mOverflowPanel.setX(0);
                    } else {
                        mContentContainer.setX(right - mContentContainer.getWidth());
                        mMainPanel.setX(mContentContainer.getWidth() - targetWidth);
                        mOverflowPanel.setX(mContentContainer.getWidth() - startWidth);
                    }
                }
            };
            final int targetHeight = mMainPanelSize.getHeight();
            final int startHeight = mContentContainer.getHeight();
            final float bottom = mContentContainer.getY() + mContentContainer.getHeight();
            Animation heightAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaHeight = (int) (interpolatedTime * (targetHeight - startHeight));
                    setHeight(mContentContainer, startHeight + deltaHeight);
                    if (mOpenOverflowUpwards) {
                        mContentContainer.setY(bottom - mContentContainer.getHeight());
                        positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float overflowButtonStartX = mOverflowButton.getX();
            final float overflowButtonTargetX = isInRTLMode() ? overflowButtonStartX - startWidth + mOverflowButtonIcon.getWidth() : overflowButtonStartX + startWidth - mOverflowButtonIcon.getWidth();
            Animation overflowButtonAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    float overflowButtonX = overflowButtonStartX + interpolatedTime * (overflowButtonTargetX - overflowButtonStartX);
                    float deltaContainerWidth = isInRTLMode() ? 0 : mContentContainer.getWidth() - startWidth;
                    float actualOverflowButtonX = overflowButtonX + deltaContainerWidth;
                    mOverflowButton.setX(actualOverflowButtonX);
                    mOverflowButtonText.setAlpha(1f - interpolatedTime);
                    mOverflowButtonShadow.setAlpha(1f - interpolatedTime);
                }
            };
            widthAnimation.setInterpolator(mFastOutSlowInInterpolator);
            widthAnimation.setDuration(getAdjustedDuration(250));
            heightAnimation.setInterpolator(mLogAccelerateInterpolator);
            heightAnimation.setDuration(getAdjustedDuration(250));
            overflowButtonAnimation.setInterpolator(mFastOutSlowInInterpolator);
            overflowButtonAnimation.setDuration(getAdjustedDuration(250));
            mCloseOverflowAnimation.getAnimations().clear();
            mCloseOverflowAnimation.addAnimation(widthAnimation);
            mCloseOverflowAnimation.addAnimation(heightAnimation);
            mCloseOverflowAnimation.addAnimation(overflowButtonAnimation);
            mContentContainer.startAnimation(mCloseOverflowAnimation);
            mIsOverflowOpen = false;
            updateOverflowButtonClickListener();
            mMainPanel.animate()
                    .alpha(1).withLayer()
                    .setInterpolator(mFastOutLinearInInterpolator)
                    .setDuration(100)
                    .start();
            mOverflowPanel.animate()
                    .alpha(0).withLayer()
                    .setInterpolator(mLinearOutSlowInInterpolator)
                    .setDuration(150)
                    .start();
        }

        private void setPanelsStatesAtRestingPosition() {
            mOverflowButton.setEnabled(true);
            mOverflowPanel.awakenScrollBars();
            if (mIsOverflowOpen) {
                final Size containerSize = mOverflowPanelSize;
                setSize(mContentContainer, containerSize);
                mMainPanel.setAlpha(0);
                mMainPanel.setVisibility(View.INVISIBLE);
                mOverflowPanel.setAlpha(1);
                mOverflowPanel.setVisibility(View.VISIBLE);
                mOverflowButtonIcon.setImageDrawable(mArrow);
                mOverflowButton.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));

                if (isInRTLMode()) {
                    mContentContainer.setX(mMarginHorizontal);
                    mMainPanel.setX(0);
                    mOverflowButton.setX(containerSize.getWidth() - mOverflowButtonSize.getWidth());
                    mOverflowPanel.setX(0);
                } else {
                    mContentContainer.setX(mPopupWindow.getWidth() - containerSize.getWidth() - mMarginHorizontal);
                    mMainPanel.setX(-mContentContainer.getX());
                    mOverflowButton.setX(0);
                    mOverflowPanel.setX(0);
                }
                if (mOpenOverflowUpwards) {
                    mContentContainer.setY(mMarginVertical);
                    mMainPanel.setY(containerSize.getHeight() - mContentContainer.getHeight());
                    mOverflowButton.setY(containerSize.getHeight() - mOverflowButtonSize.getHeight());
                    mOverflowPanel.setY(0);
                } else {
                    mContentContainer.setY(mMarginVertical);
                    mMainPanel.setY(0);
                    mOverflowButton.setY(0);
                    mOverflowPanel.setY(mOverflowButtonSize.getHeight());
                }
            } else {
                final Size containerSize = mMainPanelSize;
                setSize(mContentContainer, containerSize);
                mMainPanel.setAlpha(1);
                mMainPanel.setVisibility(View.VISIBLE);
                mOverflowPanel.setAlpha(0);
                mOverflowPanel.setVisibility(View.INVISIBLE);
                mOverflowButtonIcon.setImageDrawable(mOverflow);
                mOverflowButton.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
                if (hasOverflow()) {
                    if (isInRTLMode()) {
                        mContentContainer.setX(mMarginHorizontal);
                        mMainPanel.setX(0);
                        mOverflowButton.setX(0);
                        mOverflowPanel.setX(0);
                    } else {
                        mContentContainer.setX(mPopupWindow.getWidth() - containerSize.getWidth() - mMarginHorizontal);
                        mMainPanel.setX(0);
                        mOverflowButton.setX(containerSize.getWidth() - mOverflowButtonSize.getWidth());
                        mOverflowPanel.setX(containerSize.getWidth() - mOverflowPanelSize.getWidth());
                    }
                    if (mOpenOverflowUpwards) {
                        mContentContainer.setY(mMarginVertical + mOverflowPanelSize.getHeight() - containerSize.getHeight());
                        mMainPanel.setY(0);
                        mOverflowButton.setY(0);
                        mOverflowPanel.setY(containerSize.getHeight() - mOverflowPanelSize.getHeight());
                    } else {
                        mContentContainer.setY(mMarginVertical);
                        mMainPanel.setY(0);
                        mOverflowButton.setY(0);
                        mOverflowPanel.setY(mOverflowButtonSize.getHeight());
                    }
                } else {
                    mContentContainer.setX(mMarginHorizontal);
                    mContentContainer.setY(mMarginVertical);
                    mMainPanel.setX(0);
                    mMainPanel.setY(0);
                }
            }
        }

        private void updateOverflowHeight(int suggestedHeight) {
            if (hasOverflow()) {
                final int maxItemSize = (suggestedHeight - mOverflowButtonSize.getHeight()) / mLineHeight;
                final int newHeight = calculateOverflowHeight(maxItemSize);
                if (mOverflowPanelSize.getHeight() != newHeight) {
                    mOverflowPanelSize = new Size(mOverflowPanelSize.getWidth(), newHeight);
                }
                setSize(mOverflowPanel, mOverflowPanelSize);
                if (mIsOverflowOpen) {
                    setSize(mContentContainer, mOverflowPanelSize);
                    if (mOpenOverflowUpwards) {
                        final int deltaHeight = mOverflowPanelSize.getHeight() - newHeight;
                        mContentContainer.setY(mContentContainer.getY() + deltaHeight);
                        mOverflowButton.setY(mOverflowButton.getY() - deltaHeight);
                    }
                } else {
                    setSize(mContentContainer, mMainPanelSize);
                }
                updatePopupSize();
            }
        }

        private void updatePopupSize() {
            int width = 0;
            int height = 0;
            if (mMainPanelSize != null) {
                width = Math.max(width, mMainPanelSize.getWidth());
                height = Math.max(height, mMainPanelSize.getHeight());
            }
            if (mOverflowPanelSize != null) {
                width = Math.max(width, mOverflowPanelSize.getWidth());
                height = Math.max(height, mOverflowPanelSize.getHeight());
            }
            mPopupWindow.setWidth(width + mMarginHorizontal * 2);
            mPopupWindow.setHeight(height + mMarginVertical * 2);
            maybeComputeTransitionDurationScale();
        }

        private void refreshViewPort() {
            mParent.getWindowVisibleDisplayFrame(mViewPortOnScreen);
        }

        private int getAdjustedToolbarWidth(int suggestedWidth) {
            int width = suggestedWidth;
            refreshViewPort();
            int maximumWidth = mViewPortOnScreen.width() - 2 * dp(16);
            if (width <= 0) {
                width = dp(400);
            }
            return Math.min(width, maximumWidth);
        }

        private void setZeroTouchableSurface() {
            mTouchableRegion.setEmpty();
        }

        private void setContentAreaAsTouchableSurface() {
            final int width;
            final int height;
            if (mIsOverflowOpen) {
                width = mOverflowPanelSize.getWidth();
                height = mOverflowPanelSize.getHeight();
            } else {
                width = mMainPanelSize.getWidth();
                height = mMainPanelSize.getHeight();
            }
            mTouchableRegion.set((int) mContentContainer.getX(), (int) mContentContainer.getY(), (int) mContentContainer.getX() + width, (int) mContentContainer.getY() + height);
        }

        private void setTouchableSurfaceInsetsComputer() {
            /*ViewTreeObserver viewTreeObserver = mPopupWindow.getContentView().getRootView().getViewTreeObserver(); TODO
            viewTreeObserver.removeOnComputeInternalInsetsListener(mInsetsComputer);
            viewTreeObserver.addOnComputeInternalInsetsListener(mInsetsComputer);*/
        }

        private boolean isInRTLMode() {
            return false;
        }

        private boolean hasOverflow() {
            return mOverflowPanelSize != null;
        }

        public void layoutStyleItems(List<Integer> buttons) {
            if (buttons == null || buttons.isEmpty()) {
                mMainPanel.setOrientation(LinearLayout.HORIZONTAL);
                mMainPanelButtons = null;
                mLineHeight = dp(48);
                return;
            }
            mMainPanel.setOrientation(LinearLayout.VERTICAL);
            mMainPanelButtons = new LinearLayout(mContext);
            mMainPanelButtons.setOrientation(LinearLayout.HORIZONTAL);
            mMainPanel.addView(mMainPanelButtons);
            mLineHeight = dp(48 + 48);
        }

        public List<MenuItem> layoutMainPanelItems(List<MenuItem> menuItems, final int toolbarWidth) {
            int availableWidth = toolbarWidth;
            final LinearLayout panel = mMainPanelButtons != null ? mMainPanelButtons : mMainPanel;
            final LinkedList<MenuItem> remainingMenuItems = new LinkedList<>(menuItems);
            boolean isFirstItem = true;
            Iterator<MenuItem> it = remainingMenuItems.iterator();
            while (it.hasNext()) {
                final MenuItem menuItem = it.next();
                boolean isLastItem = !it.hasNext();
                if (menuItem != null && premiumLockClickListener != null) {
                    if (premiumOptions.contains(menuItem.getItemId())) {
                        continue;
                    }
                }
                /*if (!isFirstItem && menuItem.requiresOverflow()) {
                    break;
                }*/
                final View menuItemButton = createMenuItemButton(mContext, menuItem, mIconTextSpacing, false, isFirstItem, isLastItem);
                if (menuItemButton instanceof LinearLayout) {
                    ((LinearLayout) menuItemButton).setGravity(Gravity.CENTER);
                }
                menuItemButton.setPaddingRelative((int) ((isFirstItem ? 1.5 : 1) * menuItemButton.getPaddingStart()), menuItemButton.getPaddingTop(), (int) ((isLastItem ? 1.5 : 1) * menuItemButton.getPaddingEnd()), menuItemButton.getPaddingBottom());
                menuItemButton.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                final int menuItemButtonWidth = Math.min(menuItemButton.getMeasuredWidth(), toolbarWidth);
                final boolean canFitWithOverflow = menuItemButtonWidth <= availableWidth - mOverflowButtonSize.getWidth();
                final boolean canFitNoOverflow = isLastItem && menuItemButtonWidth <= availableWidth;
                if (canFitWithOverflow || canFitNoOverflow) {
                    setButtonTagAndClickListener(menuItemButton, menuItem);
                    //menuItemButton.setTooltipText(menuItem.getTooltipText()); TODO
                    panel.addView(menuItemButton);
                    final ViewGroup.LayoutParams params = menuItemButton.getLayoutParams();
                    params.width = menuItemButtonWidth;
                    menuItemButton.setLayoutParams(params);
                    availableWidth -= menuItemButtonWidth;
                    it.remove();
                } else {
                    break;
                }
                isFirstItem = false;
            }
            if (!remainingMenuItems.isEmpty()) {
                panel.setPaddingRelative(0, 0, mOverflowButtonSize.getWidth(), 0);
            }
            mMainPanelSize = measure(mMainPanel);
            return remainingMenuItems;
        }

        private void updateMainPanelItemsSelectors() {

        }

        @SuppressWarnings("unchecked")
        private void layoutOverflowPanelItems(List<MenuItem> menuItems) {
            ArrayAdapter<MenuItem> overflowPanelAdapter = (ArrayAdapter<MenuItem>) mOverflowPanel.getAdapter();
            overflowPanelAdapter.clear();
            if (premiumLockClickListener != null) {
                Collections.sort(menuItems, (a, b) -> {
                    final int aPremium = premiumOptions.contains(a.getItemId()) ? 1 : 0;
                    final int bPremium = premiumOptions.contains(b.getItemId()) ? 1 : 0;
                    return aPremium - bPremium;
                });
            }
            final int size = menuItems.size();
            final boolean premiumLocked = MessagesController.getInstance(UserConfig.selectedAccount).premiumFeaturesBlocked();
            for (int i = 0; i < size; i++) {
                final MenuItem menuItem = menuItems.get(i);
                final boolean show;
                if (premiumLockClickListener == null) {
                    show = true;
                } else if (premiumOptions.contains(menuItem.getItemId())) {
                    show = !premiumLocked;
                } else {
                    show = true;
                }
                if (show) {
                    overflowPanelAdapter.add(menuItem);
                }
            }
            mOverflowPanel.setAdapter(overflowPanelAdapter);
            if (mOpenOverflowUpwards) {
                mOverflowPanel.setY(0);
            } else {
                mOverflowPanel.setY(mOverflowButtonSize.getHeight());
            }
            int width = Math.max(getOverflowWidth(), mOverflowButtonSize.getWidth());
            int height = calculateOverflowHeight(MAX_OVERFLOW_SIZE);
            mOverflowPanelSize = new Size(width, height);
            setSize(mOverflowPanel, mOverflowPanelSize);
        }

        private void preparePopupContent() {
            mContentContainer.removeAllViews();
            if (hasOverflow()) {
                mContentContainer.addView(mOverflowPanel);
            }
            mContentContainer.addView(mMainPanel);
            if (hasOverflow()) {
                mContentContainer.addView(mOverflowButton);
            }
            setPanelsStatesAtRestingPosition();
            setContentAreaAsTouchableSurface();
            if (isInRTLMode()) {
                mContentContainer.setAlpha(0);
                mContentContainer.post(mPreparePopupContentRTLHelper);
            }
        }

        @SuppressWarnings("unchecked")
        private void clearPanels() {
            mOverflowPanelSize = null;
            mMainPanelSize = null;
            mIsOverflowOpen = false;
            updateOverflowButtonClickListener();
            mMainPanel.removeAllViews();
            mMainPanel.setPaddingRelative(0, 0, 0, 0);
            ArrayAdapter<MenuItem> overflowPanelAdapter = (ArrayAdapter<MenuItem>) mOverflowPanel.getAdapter();
            overflowPanelAdapter.clear();
            mOverflowPanel.setAdapter(overflowPanelAdapter);
            mContentContainer.removeAllViews();
        }

        private void positionContentYCoordinatesIfOpeningOverflowUpwards() {
            if (mOpenOverflowUpwards) {
                mMainPanel.setY(mContentContainer.getHeight() - mMainPanelSize.getHeight());
                mOverflowButton.setY(mContentContainer.getHeight() - mOverflowButton.getHeight());
                mOverflowPanel.setY(mContentContainer.getHeight() - mOverflowPanelSize.getHeight());
            }
        }

        private int getOverflowWidth() {
            int overflowWidth = 0;
            final int count = mOverflowPanel.getAdapter().getCount();
            for (int i = 0; i < count; i++) {
                MenuItem menuItem = (MenuItem) mOverflowPanel.getAdapter().getItem(i);
                overflowWidth = Math.max(mOverflowPanelViewHelper.calculateWidth(menuItem), overflowWidth);
            }
            return overflowWidth;
        }

        private int calculateOverflowHeight(int maxItemSize) {
            int actualSize = Math.min(MAX_OVERFLOW_SIZE, Math.min(Math.max(MIN_OVERFLOW_SIZE, maxItemSize), mOverflowPanel.getCount()));
            int extension = 0;
            if (actualSize < mOverflowPanel.getCount()) {
                extension = (int) (mLineHeight * 0.5f);
            }
            return actualSize * mLineHeight + mOverflowButtonSize.getHeight() + extension;
        }

        private void setButtonTagAndClickListener(View menuItemButton, MenuItem menuItem) {
            menuItemButton.setTag(menuItem);
            menuItemButton.setOnClickListener(mMenuItemButtonOnClickListener);
        }

        private int getAdjustedDuration(int originalDuration) {
            if (mTransitionDurationScale < 150) {
                return Math.max(originalDuration - 50, 0);
            } else if (mTransitionDurationScale > 300) {
                return originalDuration + 50;
            }
            return originalDuration;
        }

        private void maybeComputeTransitionDurationScale() {
            if (mMainPanelSize != null && mOverflowPanelSize != null) {
                int w = mMainPanelSize.getWidth() - mOverflowPanelSize.getWidth();
                int h = mOverflowPanelSize.getHeight() - mMainPanelSize.getHeight();
                mTransitionDurationScale = (int) (Math.sqrt(w * w + h * h) / mContentContainer.getContext().getResources().getDisplayMetrics().density);
            }
        }

        private LinearLayout createMainPanel() {
            return new LinearLayout(mContext) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    if (isOverflowAnimating() && mMainPanelSize != null) {
                        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMainPanelSize.getWidth(), MeasureSpec.EXACTLY);
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return isOverflowAnimating();
                }
            };
        }

        private int shiftDp = -4;

        private OverflowPanel createOverflowPanel() {
            final OverflowPanel overflowPanel = new OverflowPanel(this);
            overflowPanel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            overflowPanel.setDivider(null);
            overflowPanel.setDividerHeight(0);
            final ArrayAdapter<MenuItem> adapter = new ArrayAdapter<MenuItem>(mContext, 0) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    return mOverflowPanelViewHelper.getView(getItem(position), mOverflowPanelSize.getWidth(), convertView);
                }
            };
            overflowPanel.setPadding(0, dp(4), 0, dp(2));
            overflowPanel.setClipToPadding(false);
            overflowPanel.setAdapter(adapter);
            overflowPanel.setOnItemClickListener((parent, view, position, id) -> {
                MenuItem menuItem = (MenuItem) overflowPanel.getAdapter().getItem(position);
                if (premiumLockClickListener != null && premiumOptions.contains(menuItem.getItemId())) {
                    AndroidUtilities.shakeViewSpring(view, shiftDp = -shiftDp);
                    BotWebViewVibrationEffect.APP_ERROR.vibrate();
                    premiumLockClickListener.run();
                } else if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onMenuItemClick(menuItem);
                }
            });
            return overflowPanel;
        }

        private boolean isOverflowAnimating() {
            final boolean overflowOpening = mOpenOverflowAnimation.hasStarted() && !mOpenOverflowAnimation.hasEnded();
            final boolean overflowClosing = mCloseOverflowAnimation.hasStarted() && !mCloseOverflowAnimation.hasEnded();
            return overflowOpening || overflowClosing;
        }

        private Animation.AnimationListener createOverflowAnimationListener() {
            return new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mOverflowButton.setEnabled(false);
                    mMainPanel.setVisibility(View.VISIBLE);
                    mOverflowPanel.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mContentContainer.post(() -> {
                        setPanelsStatesAtRestingPosition();
                        setContentAreaAsTouchableSurface();
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            };
        }

        private Size measure(View view) {
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            return new Size(view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        private void setSize(View view, int width, int height) {
            view.setMinimumWidth(width);
            view.setMinimumHeight(height);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params = (params == null) ? new ViewGroup.LayoutParams(0, 0) : params;
            params.width = width;
            params.height = height;
            view.setLayoutParams(params);
        }

        private void setSize(View view, Size size) {
            setSize(view, size.getWidth(), size.getHeight());
        }

        private void setWidth(View view, int width) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            setSize(view, width, params.height);
        }

        private void setHeight(View view, int height) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            setSize(view, params.width, height);
        }

        private final class OverflowPanel extends ListView {
            private final FloatingToolbarPopup mPopup;

            OverflowPanel(FloatingToolbarPopup popup) {
                super(popup.mContext);
                this.mPopup = popup;
                setVerticalScrollBarEnabled(false);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = mPopup.mOverflowPanelSize.getHeight() - mPopup.mOverflowButtonSize.getHeight();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            private final int fadeH = 16;
            private final Shader shaderTop = new LinearGradient(0, dp(fadeH), 0, 0, generateColors(Color.BLACK), null, Shader.TileMode.CLAMP);
            private final Shader shaderBottom = new LinearGradient(0, 0, 0, dp(fadeH), generateColors(Color.BLACK), null, Shader.TileMode.CLAMP);
            private final Paint paintTop = new Paint(Paint.ANTI_ALIAS_FLAG);
            private final Paint paintBottom = new Paint(Paint.ANTI_ALIAS_FLAG);
            private final Matrix matrix = new Matrix();

            {
                paintTop.setShader(shaderTop);
                paintTop.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                paintBottom.setShader(shaderBottom);
                paintBottom.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            }

            private int[] generateColors(int color) {
                final int[] colors = new int[8];
                GradientProtectionDrawable.fillColors(GradientProtectionDrawable.DEFAULT_INTERPOLATOR, color, colors);
                return colors;
            }

            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                matrix.reset();
                matrix.postTranslate(0, h - dp(fadeH));
                shaderBottom.setLocalMatrix(matrix);
            }

            @Override
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                final float top = child.getY();
                final float bottom = top + child.getHeight();
                final boolean isFadedTop = top < dp(fadeH);
                final boolean isFadedBottom = bottom > getHeight() - dp(fadeH);

                if (isFadedTop || isFadedBottom) {
                    canvas.saveLayer(0, top, getWidth(), bottom, null);
                    final boolean result = super.drawChild(canvas, child, drawingTime);

                    if (isFadedTop) {
                        canvas.drawRect(0, 0, getWidth(), dp(fadeH), paintTop);
                    }
                    if (isFadedBottom) {
                        canvas.drawRect(0, getHeight() - dp(fadeH), getWidth(), getHeight(), paintBottom);
                    }

                    canvas.restore();
                    return result;
                }

                return super.drawChild(canvas, child, drawingTime);
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (mPopup.isOverflowAnimating()) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            protected boolean awakenScrollBars() {
                return super.awakenScrollBars();
            }
        }

        private final class LogAccelerateInterpolator implements Interpolator {
            private final int BASE = 100;
            private final float LOGS_SCALE = 1f / computeLog(1, BASE);

            private float computeLog(float t, int base) {
                return (float) (1 - Math.pow(base, -t));
            }

            @Override
            public float getInterpolation(float t) {
                return 1 - computeLog(1 - t, BASE) * LOGS_SCALE;
            }
        }

        private final class OverflowPanelViewHelper {
            private final View mCalculator;
            private final int mIconTextSpacing;
            private final int mSidePadding;
            private final Context mContext;

            public OverflowPanelViewHelper(Context context, int iconTextSpacing) {
                mContext = context;
                mIconTextSpacing = iconTextSpacing;
                mSidePadding = dp(18);
                mCalculator = createMenuButton(null);
            }

            public View getView(MenuItem menuItem, int minimumWidth, View convertView) {
                if (convertView != null) {
                    updateMenuItemButton(convertView, menuItem, mIconTextSpacing, premiumLockClickListener != null);
                } else {
                    convertView = createMenuButton(menuItem);
                }
                convertView.setMinimumWidth(minimumWidth);
                return convertView;
            }

            public int calculateWidth(MenuItem menuItem) {
                updateMenuItemButton(mCalculator, menuItem, mIconTextSpacing, premiumLockClickListener != null);
                mCalculator.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                return mCalculator.getMeasuredWidth();
            }

            private View createMenuButton(MenuItem menuItem) {
                View button = createMenuItemButton(mContext, menuItem, mIconTextSpacing, true, false, false);
                button.setPadding(mSidePadding, 0, mSidePadding, 0);
                return button;
            }
        }
    }

    private View createMenuItemButton(Context context, MenuItem menuItem, int iconTextSpacing, boolean overflow, boolean first, boolean last) {
        LinearLayout menuItemButton = new LinearLayout(context);
        menuItemButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        menuItemButton.setOrientation(LinearLayout.HORIZONTAL);
        menuItemButton.setMinimumWidth(dp(48));
        menuItemButton.setMinimumHeight(dp(overflow ? 42 : 48));
        menuItemButton.setPaddingRelative(dp(16), 0, dp(16), 0);

        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setFocusable(false);
        textView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        textView.setFocusableInTouchMode(false);
        int color;
        int selectorColor = getThemedColor(Theme.key_listSelector);
        if (currentStyle == STYLE_DIALOG) {
            textView.setTextColor(color = getThemedColor(Theme.key_dialogTextBlack));
        } else if (currentStyle == STYLE_BLACK) {
            textView.setTextColor(color = 0xfffafafa);
            selectorColor = 0x20ffffff;
        } else if (currentStyle == STYLE_THEME) {
            textView.setTextColor(color = getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        } else {
            color = getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
        }
        if (first || last) {
            menuItemButton.setBackground(Theme.createRadSelectorDrawable(selectorColor, first ? 12 : 0, last ? 12 : 0, last ? 12 : 0, first ? 12 : 0));
        } else {
            menuItemButton.setBackground(Theme.getSelectorDrawable(selectorColor, false));
        }

        textView.setPaddingRelative(dp(11), 0, 0, 0);
        menuItemButton.addView(textView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(overflow ? 42 : 48)));

        menuItemButton.addView(new Space(context), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1, 1));

        ImageView lockView = new ImageView(context);
        lockView.setImageResource(R.drawable.msg_mini_lock3);
        lockView.setScaleType(ImageView.ScaleType.CENTER);
        lockView.setColorFilter(new PorterDuffColorFilter(Theme.multAlpha(color, .4f), PorterDuff.Mode.SRC_IN));
        lockView.setVisibility(View.GONE);
        menuItemButton.addView(lockView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, 0, 0, 12, 0, 0, 0));

        if (menuItem != null) {
            updateMenuItemButton(menuItemButton, menuItem, iconTextSpacing, premiumLockClickListener != null);
        }
        return menuItemButton;
    }

    private static void updateMenuItemButton(View menuItemButton, MenuItem menuItem, int iconTextSpacing, boolean containsPremium) {
        ViewGroup viewGroup = (ViewGroup) menuItemButton;
        final TextView buttonText = (TextView) viewGroup.getChildAt(0);
        buttonText.setEllipsize(null);
        if (TextUtils.isEmpty(menuItem.getTitle())) {
            buttonText.setVisibility(View.GONE);
        } else {
            buttonText.setVisibility(View.VISIBLE);
            buttonText.setText(menuItem.getTitle());
        }
        buttonText.setPaddingRelative(0, 0, 0, 0);

        final boolean premium = containsPremium && premiumOptions.contains(menuItem.getItemId());
        viewGroup.getChildAt(2).setVisibility(premium ? View.VISIBLE : View.GONE);
        /*final CharSequence contentDescription = menuItem.getContentDescription(); TODO
        if (TextUtils.isEmpty(contentDescription)) {
            menuItemButton.setContentDescription(menuItem.getTitle());
        } else {
            menuItemButton.setContentDescription(contentDescription);
        }*/
    }

    private ViewGroup createContentContainer(Context context) {
        RelativeLayout contentContainer = new RelativeLayout(context);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = layoutParams.leftMargin = layoutParams.topMargin = layoutParams.rightMargin = dp(20);
        contentContainer.setLayoutParams(layoutParams);
        contentContainer.setElevation(dp(1));
        contentContainer.setFocusable(true);
        contentContainer.setFocusableInTouchMode(true);

        if (blurredBackgroundDrawableViewFactory != null) {
            contentContainer.setBackground(blurredBackgroundDrawableViewFactory
                .create(contentContainer, true)
                .setColorProvider(BlurredBackgroundProviderImpl.photoViewerMenu(resourcesProvider))
                .setRadius(dp(12)));
        } else {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            int r = dp(12);
            shape.setCornerRadii(new float[] { r, r, r, r, r, r, r, r });
            if (currentStyle == STYLE_DIALOG) {
                shape.setColor(getThemedColor(Theme.key_dialogBackground));
            } else if (currentStyle == STYLE_BLACK) {
                shape.setColor(0xf9222222);
            } else if (currentStyle == STYLE_THEME) {
                shape.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
            }
            contentContainer.setBackground(shape);
        }

        contentContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentContainer.setClipToOutline(true);
        return contentContainer;
    }

    private int getThemedColor(int key) {
        return Theme.getColor(key, resourcesProvider);
    }

    private static PopupWindow createPopupWindow(ViewGroup content) {
        ViewGroup popupContentHolder = new LinearLayout(content.getContext()) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return super.onInterceptTouchEvent(ev);
            }
            private boolean isParent(View child, View parent) {
                if (child == parent) return true;
                if (child.getParent() == null) return false;
                if (child.getParent() instanceof View) {
                    return isParent((View) child.getParent(), parent);
                } else if (child.getParent() == parent) {
                    return true;
                } else if (child.getRootView() == parent) {
                    return true;
                }
                return false;
            }

            private final int[] p = new int[2];
            private View downRootView = null;
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                boolean r = super.dispatchTouchEvent(ev);
                if (!r) {
                    getLocationOnScreen(p);
                    ev.offsetLocation(p[0], p[1]);
                    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        final List<View> views = allGlobalViews();
                        if (views != null && views.size() > 1) {
                            for (int i = views.size() - 2; i >= 0; --i) {
                                final View view = views.get(i);
                                if (isParent(this, view)) continue;
                                view.getLocationOnScreen(p);
                                ev.offsetLocation(-p[0], -p[1]);
                                r = view.dispatchTouchEvent(ev);
                                if (r) {
                                    downRootView = view;
                                    return true;
                                }
                                ev.offsetLocation(p[0], p[1]);
                            }
                        }
                    } else if (downRootView != null) {
                        View view = downRootView;
                        view.getLocationOnScreen(p);
                        ev.offsetLocation(-p[0], -p[1]);
                        r = view.dispatchTouchEvent(ev);
                    }
                }
                if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                    downRootView = null;
                }
                return r;
            }
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return super.onTouchEvent(event);
            }
        };
        PopupWindow popupWindow = new PopupWindow(popupContentHolder);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setSplitTouchEnabled(true);
        content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        popupContentHolder.addView(content);
        return popupWindow;
    }

    private static AnimatorSet createEnterAnimation(View view) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(
                ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1).setDuration(150));
        return animation;
    }

    private static AnimatorSet createExitAnimation(View view, int startDelay, Animator.AnimatorListener listener) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0).setDuration(100));
        animation.setStartDelay(startDelay);
        animation.addListener(listener);
        return animation;
    }

}
