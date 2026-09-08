@Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }
    @Override
    public boolean drawEdgeNavigationBar() {
        return false;
    }

    private int navigationBarHeight;
    private int imeInsetAnimatedHeight;

    @Override
    public View getAnimatedInsetsTargetView() {
        return fragmentView;
    }

    @Override
    public void onAnimatedInsetsChanged(View view, WindowInsetsCompat insets) {
        imeInsetAnimatedHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
        checkUi_floatingButton();
    }

    @NonNull
    private WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
        navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
        if (buttonsContainer != null) {
            buttonsContainer.setPadding(0, 0, 0, navigationBarHeight);
        }

        checkUi_listViewPadding();
        checkUi_floatingButton();

        return WindowInsetsCompat.CONSUMED;
    }

    private void checkUi_listViewPadding() {
        final int buttonsH = isCall ? dp(14 + 48 + 14) : 0;

        listView.setPadding(
                0,
                dp(ADDITIONAL_LIST_HEIGHT_DP + DialogsActivity.SEARCH_FIELD_HEIGHT) + actionBar.getMeasuredHeight() + ((int) animatorSelectorContainerHeight.getFactor()),
                0,
                dp(ADDITIONAL_LIST_HEIGHT_DP) + navigationBarHeight + buttonsH);
        emptyView.setPadding(0, 0, 0, navigationBarHeight);
    }

    private void checkUi_searchFieldY() {
        searchField.setTranslationY(animatorSelectorContainerHeight.getFactor());
    }

    private void checkUi_headerShadowY() {
        headerShadowView.setTranslationY(dp(DialogsActivity.SEARCH_FIELD_HEIGHT) + animatorSelectorContainerHeight.getFactor());
    }

    private void checkUi_bottomButtons() {
        if (buttonsContainer == null) {
            return;
        }

        final float factor = animatorCallButtonsVisible.getFloatValue();
        buttonsContainer.setTranslationY(dp(12) * (1f - factor));
        buttonsContainer.setAlpha(factor);
        buttonsContainer.setVisibility(factor > 0 ? View.VISIBLE : View.GONE);
    }

    private void checkUi_floatingButton() {
        if (floatingButton != null) {
            floatingButton.setTranslationY(-Math.max(navigationBarHeight, imeInsetAnimatedHeight));
        }
    }

    private final Rect tmpClipRect = new Rect();
    private void checkUi_listClip() {
        if (listView.hasActiveEdgeEffects()) {
            listView.setClipBounds(null);
            return;
        }

        final int buttonsH = (int) ((navigationBarHeight + dp(14 + 48 + 14)) * animatorCallButtonsVisible.getFloatValue());

        tmpClipRect.set(
            0,
            dp(ADDITIONAL_LIST_HEIGHT_DP + DialogsActivity.SEARCH_FIELD_HEIGHT) + actionBar.getMeasuredHeight() + (int) (animatorSelectorContainerHeight.getFactor()),
            listView.getMeasuredWidth(),
            listView.getMeasuredHeight() - dp(ADDITIONAL_LIST_HEIGHT_DP) - buttonsH);
        listView.setClipBounds(tmpClipRect);
    }



    /* Blur */

    private final @Nullable DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private final @Nullable BlurredBackgroundSourceRenderNode iBlur3SourceGlassFrosted;

    private IBlur3Capture iBlur3Capture;
    private boolean iBlur3Invalidated;

    private final ArrayList<RectF> iBlur3Positions = new ArrayList<>(2);
    private final RectF iBlur3PositionActionBar = new RectF();
    private final RectF iBlur3PositionBottomBar = new RectF();
    {
        iBlur3Positions.add(iBlur3PositionActionBar);
        iBlur3Positions.add(iBlur3PositionBottomBar);
    }

    private void blur3_InvalidateBlur() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || scrollableViewNoiseSuppressor == null) {
            return;
        }

        final int additionalList = dp(48);
        final int additionalSearch = dp(DialogsActivity.SEARCH_FIELD_HEIGHT) + maxSize;

        iBlur3PositionActionBar.set(0, 0, fragmentView.getMeasuredWidth(), actionBar.getMeasuredHeight() + additionalSearch);
        iBlur3PositionActionBar.inset(0, -additionalList);
        if (buttonsContainer != null) {
            iBlur3PositionBottomBar.set(0, fragmentView.getMeasuredHeight() - buttonsContainer.getMeasuredHeight(), fragmentView.getMeasuredWidth(), fragmentView.getMeasuredHeight());
            iBlur3PositionBottomBar.inset(0, -additionalList);
        }

        scrollableViewNoiseSuppressor.setupRenderNodes(iBlur3Positions, buttonsContainer != null && animatorCallButtonsVisible.getFloatValue() > 0 ? 2 : 1);
        scrollableViewNoiseSuppressor.invalidateResultRenderNodes(iBlur3Capture, fragmentView.getMeasuredWidth(), fragmentView.getMeasuredHeight());
    }
}
