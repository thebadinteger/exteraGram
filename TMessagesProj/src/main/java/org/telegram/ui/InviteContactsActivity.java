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

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        navigationBarHeight = bottom;

        checkUi_listViewPadding();
        checkUi_floatingButton();
    }

    @Override
    public void onFactorChanged(int id, float factor, float fraction, FactorAnimator callee) {
        if (id == ANIMATOR_ID_SELECTED_CONTAINER_HEIGHT) {
            final int oldPaddingTop = listView.getPaddingTop();

            searchField.invalidate();
            checkUi_listViewPadding();
            checkUi_searchFieldY();

            final int newPaddingTop = listView.getPaddingTop();
            if (newPaddingTop != oldPaddingTop/* && !headerShadowView.isShadowVisible()*/) {
               listView.scrollBy(0, oldPaddingTop - newPaddingTop);
            }
        }
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    private void checkUi_searchFieldY() {
        searchField.setTranslationY(actionBar.getMeasuredHeight());
    }

    private void checkUi_listViewPadding() {
        final int top = (
            dp(ADDITIONAL_LIST_HEIGHT_DP)
            + actionBar.getMeasuredHeight()
            + dp(4)
            + ((int) animatorSelectorContainerHeight.getFactor())
        );

        listView.setPadding(0, top, 0, navigationBarHeight);
        emptyView.setPadding(0, 0, 0, navigationBarHeight);
    }

    private void checkUi_floatingButton() {
        if (floatingButton != null) {
            floatingButton.setTranslationY(-Math.max(navigationBarHeight, imeInsetAnimatedHeight));
        }
    }



    /* Blur */

    private final @Nullable DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private final @Nullable BlurredBackgroundSourceRenderNode iBlur3SourceGlassFrosted;

    private IBlur3Capture iBlur3Capture;
    private boolean iBlur3Invalidated;

    private final ArrayList<RectF> iBlur3Positions = new ArrayList<>();
    private final RectF iBlur3PositionActionBar = new RectF(); {
        iBlur3Positions.add(iBlur3PositionActionBar);
    }

    private void blur3_InvalidateBlur() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || scrollableViewNoiseSuppressor == null) {
            return;
        }

        final int additionalList = dp(48);
        final int additionalSearch = dp(DialogsActivity.SEARCH_FIELD_HEIGHT) + maxSize;

        iBlur3PositionActionBar.set(0, -additionalList, fragmentView.getMeasuredWidth(), actionBar.getMeasuredHeight() + additionalList + additionalSearch );
        scrollableViewNoiseSuppressor.setupRenderNodes(iBlur3Positions, 1);
        scrollableViewNoiseSuppressor.invalidateResultRenderNodes(iBlur3Capture, fragmentView.getMeasuredWidth(), fragmentView.getMeasuredHeight());
    }
}
