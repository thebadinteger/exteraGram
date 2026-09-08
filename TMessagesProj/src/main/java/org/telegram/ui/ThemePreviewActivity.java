serviceBitmap == null || serviceBitmapShader == null) {
                Theme.ResourcesProvider.super.applyServiceShaderMatrix(w, h, translationX, translationY);
            } else {
                Theme.applyServiceShaderMatrix(serviceBitmap, serviceBitmapShader, serviceBitmapMatrix, w, h, translationX, translationY);
            }
        }

        @Override
        public boolean isDark() {
            return onSwitchDayNightDelegate != null ? onSwitchDayNightDelegate.isDark() : (parentProvider != null ? parentProvider.isDark() : Theme.isCurrentThemeDark());
        }
    }

    private View changeDayNightView;
    private float changeDayNightViewProgress;
    private ValueAnimator changeDayNightViewAnimator;

    @SuppressLint("NotifyDataSetChanged")
    public void toggleTheme() {
        if (changeDayNightView != null) {
            return;
        }

        FrameLayout decorView1 = insideBottomSheet() ? (FrameLayout) parentLayout.getBottomSheet().getWindow().getDecorView() : (FrameLayout) getParentActivity().getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(decorView1.getWidth(), decorView1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        dayNightItem.setAlpha(0f);
        decorView1.draw(bitmapCanvas);
        dayNightItem.setAlpha(1f);

        Paint xRefPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xRefPaint.setColor(0xff000000);
        xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);
        int[] position = new int[2];
        dayNightItem.getLocationInWindow(position);
        float x = position[0];
        float y = position[1];
        float cx = x + dayNightItem.getMeasuredWidth() / 2f;
        float cy = y + dayNightItem.getMeasuredHeight() / 2f;

        float r = Math.max(bitmap.getHeight(), bitmap.getWidth()) + AndroidUtilities.navigationBarHeight;

        Shader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapPaint.setShader(bitmapShader);
        changeDayNightView = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (themeDelegate.isDark()) {
                    if (changeDayNightViewProgress > 0f) {
                        bitmapCanvas.drawCircle(cx, cy, r * changeDayNightViewProgress, xRefPaint);
                    }
                    canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
                } else {
                    canvas.drawCircle(cx, cy, r * (1f - changeDayNightViewProgress), bitmapPaint);
                }
                canvas.save();
                canvas.translate(x, y);
                dayNightItem.draw(canvas);
                canvas.restore();
            }
        };
        changeDayNightView.setOnTouchListener((v, event) -> true);
        changeDayNightViewProgress = 0f;
        changeDayNightViewAnimator = ValueAnimator.ofFloat(0, 1f);
        changeDayNightViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean changedNavigationBarColor = false;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                changeDayNightViewProgress = (float) valueAnimator.getAnimatedValue();
                changeDayNightView.invalidate();
                if (!changedNavigationBarColor && changeDayNightViewProgress > .5f) {
                    changedNavigationBarColor = true;
                }
            }
        });
        changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (changeDayNightView != null) {
                    if (changeDayNightView.getParent() != null) {
                        ((ViewGroup) changeDayNightView.getParent()).removeView(changeDayNightView);
                    }
                    changeDayNightView = null;
                }
                changeDayNightViewAnimator = null;
                super.onAnimationEnd(animation);
            }
        });
        changeDayNightViewAnimator.setDuration(400);
        changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
        changeDayNightViewAnimator.start();

        decorView1.addView(changeDayNightView, new ViewGroup.LayoutParams(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        AndroidUtilities.runOnUIThread(() -> {
            onSwitchDayNightDelegate.switchDayNight(false);
            setForceDark(themeDelegate.isDark(), true);
            setCurrentImage(false);
            invalidateBlur();
            updateBlurred();
            if (themeDescriptions != null) {
                for (int i = 0; i < themeDescriptions.size(); ++i) {
                    themeDescriptions.get(i).setColor(getThemedColor(themeDescriptions.get(i).getCurrentKey()), false, false);
                }
            }

            if (shouldShowBrightnessControll) {
                if (onSwitchDayNightDelegate != null && onSwitchDayNightDelegate.isDark()) {
                    dimmingSlider.setVisibility(View.VISIBLE);
                    dimmingSlider.animateValueTo(dimAmount);
                } else {
                    dimmingSlider.animateValueTo(0);
                }
                if (changeDayNightViewAnimator2 != null) {
                    changeDayNightViewAnimator2.removeAllListeners();
                    changeDayNightViewAnimator2.cancel();
                }
                changeDayNightViewAnimator2 = ValueAnimator.ofFloat(progressToDarkTheme, onSwitchDayNightDelegate.isDark() ? 1 : 0);
                changeDayNightViewAnimator2.addUpdateListener(animation -> {
                    progressToDarkTheme = (float) animation.getAnimatedValue();
                    backgroundImage.invalidate();
                    bottomOverlayChat.invalidate();
                    dimmingSlider.setAlpha(progressToDarkTheme);
                    dimmingSliderContainer.invalidate();
                    invalidateBlur();
                });
                changeDayNightViewAnimator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!onSwitchDayNightDelegate.isDark()) {
                            dimmingSlider.setVisibility(View.GONE);
                        }
                    }
                });
                changeDayNightViewAnimator2.setDuration(250);
                changeDayNightViewAnimator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                changeDayNightViewAnimator2.start();
            }
        });
    }

    public void setForceDark(boolean isDark, boolean playAnimation) {
        if (playAnimation) {
            sunDrawable.setCustomEndFrame(isDark ? sunDrawable.getFramesCount() : 0);
            if (sunDrawable != null) {
                sunDrawable.start();
            }
        } else {
            int frame = isDark ? sunDrawable.getFramesCount() - 1 : 0;
            sunDrawable.setCurrentFrame(frame, false, true);
            sunDrawable.setCustomEndFrame(frame);
            if (dayNightItem != null) {
                dayNightItem.invalidate();
            }
        }
    }
}
