public boolean hasForceLightStatusBar() {
        return false;
    }

    public int getNavigationBarColor() {
        int color = Theme.getColor(Theme.key_windowBackgroundGray, getResourceProvider());
        if (sheetsStack != null) {
            for (int i = 0; i < sheetsStack.size(); ++i) {
                AttachedSheet sheet = sheetsStack.get(i);
                if (sheet.attachedToParent()) {
                    color = sheet.getNavigationBarColor(color);
                }
            }
        }
        return color;
    }

    public void setNavigationBarColor(int color) {
        if (isSupportEdgeToEdge()) {
            return;
        }

        Activity activity = getParentActivity();
        if (activity instanceof LaunchActivity) {
            LaunchActivity launchActivity = (LaunchActivity) activity;
            launchActivity.setNavigationBarColor(color);
        } else {
            if (activity != null) {
                Window window = activity.getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && window != null && window.getNavigationBarColor() != color) {
                    // window.setNavigationBarColor(color);
                }
            }
        }

        final float brightness = AndroidUtilities.computePerceivedBrightness(color);
        AndroidUtilities.setLightNavigationBar(activity, brightness >= 0.721f);

        if (parentLayout != null) {
            parentLayout.setNavigationBarColor(color);
        }
    }

    public boolean isBeginToShow() {
        return fragmentBeginToShow;
    }

    private void setParentDialog(Dialog dialog) {
        parentDialog = dialog;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return resourceProvider;
    }

    protected boolean allowPresentFragment() {
        return true;
    }

    public boolean isRemovingFromStack() {
        return removingFromStack;
    }

    public void setRemovingFromStack(boolean b) {
        removingFromStack = b;
    }

    public boolean isLightStatusBar() {
        if (getLastStoryViewer() != null && getLastStoryViewer().isShown()) {
            return false;
        }
        if (hasForceLightStatusBar() && !Theme.getCurrentTheme().isDark()) {
            return true;
        }
        Theme.ResourcesProvider resourcesProvider = getResourceProvider();
        int color;
        int key = Theme.key_actionBarDefault;
        if (actionBar != null && actionBar.isActionModeShowed()) {
            key = Theme.key_actionBarActionModeDefault;
        }
        if (resourcesProvider != null) {
            color = resourcesProvider.getColorOrDefault(key);
        } else {
            color = Theme.getColor(key, null, true);
        }
        return ColorUtils.calculateLuminance(color) > 0.7f;
    }

    public void setPreviewOpenedProgress(float progress) {

    }

    public void setPreviewReplaceProgress(float progress) {

    }

    public boolean closeLastFragment() {
        return false;
    }

    public void setPreviewDelegate(PreviewDelegate previewDelegate) {
        this.previewDelegate = previewDelegate;
    }

    public void resetFragment() {
        if (isFinished) {
            clearViews();
            isFinished = false;
            finishing = false;
        }
    }

    public void setResourceProvider(Theme.ResourcesProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public void onFragmentClosed() {

    }

    public void attachSheets(ActionBarLayout.LayoutContainer parentLayout) {
        if (sheetsStack != null) {
            for (int i = 0; i < sheetsStack.size(); ++i) {
                AttachedSheet sheet = sheetsStack.get(i);
                if (sheet != null && sheet.attachedToParent()) {
                    AndroidUtilities.removeFromParent(sheet.getWindowView());
                    parentLayout.addView(sheet.getWindowView());
                }
            }
        }
    }

    public void detachSheets() {
        if (sheetsStack != null) {
            for (int i = 0; i < sheetsStack.size(); ++i) {
                AttachedSheet sheet = sheetsStack.get(i);
                if (sheet != null && sheet.attachedToParent()) {
                    AndroidUtilities.removeFromParent(sheet.getWindowView());
                }
            }
        }
    }

    public boolean isStoryViewer(View child) {
        if (sheetsStack != null) {
            for (int i = 0; i < sheetsStack.size(); ++i) {
                AttachedSheet sheet = sheetsStack.get(i);
                if (sheet instanceof StoryViewer && child == sheet.getWindowView()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBotView(View child) {
        if (sheetsStack != null) {
            for (int i = 0; i < sheetsStack.size(); ++i) {
                AttachedSheet sheet = sheetsStack.get(i);
                if (sheet instanceof BotWebViewAttachedSheet && child == sheet.getWindowView()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setKeyboardHeightFromParent(int keyboardHeight) {
        if (sheetsStack != null) {
            for (int i = 0; i < sheetsStack.size(); ++i) {
                AttachedSheet storyViewer = sheetsStack.get(i);
                if (storyViewer != null) {
                    storyViewer.setKeyboardHeightFromParent(keyboardHeight);
                }
            }
        }
    }

    public interface PreviewDelegate {
        void finishFragment();
    }

    public StoryViewer getOrCreateStoryViewer() {
        if (sheetsStack == null) {
            sheetsStack = new ArrayList<>();
        }
        StoryViewer storyViewer = null;
        if (!sheetsStack.isEmpty() && sheetsStack.get(sheetsStack.size() - 1) instanceof StoryViewer) {
            storyViewer = (StoryViewer) sheetsStack.get(sheetsStack.size() - 1);
        }
        if (storyViewer == null) {
            storyViewer = new StoryViewer(this);
            if (parentLayout != null && parentLayout.isSheet()) {
                storyViewer.fromBottomSheet = true;
            }
            sheetsStack.add(storyViewer);
            updateSheetsVisibility();
        }
        return storyViewer;
    }

    public StoryViewer getOrCreateStoryViewer(int account) {
        if (sheetsStack == null) {
            sheetsStack = new ArrayList<>();
        }
        StoryViewer storyViewer = null;
        if (!sheetsStack.isEmpty() && sheetsStack.get(sheetsStack.size() - 1) instanceof StoryViewer) {
            storyViewer = (StoryViewer) sheetsStack.get(sheetsStack.size() - 1);
        }
        if (storyViewer != null && storyViewer.currentAccount != account) {
            storyViewer.close(true);
            removeSheet(storyViewer);
            storyViewer = null;
        }
        if (storyViewer == null) {
            storyViewer = new StoryViewer(this);
            if (parentLayout != null && parentLayout.isSheet()) {
                storyViewer.fromBottomSheet = true;
            }
            sheetsStack.add(storyViewer);
            updateSheetsVisibility();
        }
        return storyViewer;
    }


    public void setTitleOverlayTextIfActionBarAttached(String title, int titleId, Runnable action) {
        if (actionBar != null && actionBar.shouldAddToContainer()) {
            setTitleOverlayText(title, titleId, action);
        }
    }

    public void setTitleOverlayText(String title, int titleId, Runnable action) {
        if (actionBar != null) {
            actionBar.setTitleOverlayText(title, titleId, action);
        }
    }

    public void removeSheet(BaseFragment.AttachedSheet sheet) {
        if (sheetsStack == null) return;
        sheetsStack.remove(sheet);
        updateSheetsVisibility();
    }

    public void addSheet(BaseFragment.AttachedSheet sheet) {
        if (sheetsStack == null) {
            sheetsStack = new ArrayList<>();
        }
        StoryViewer storyViewer = getLastStoryViewer();
        if (storyViewer != null) {
            storyViewer.listenToAttachedSheet(sheet);
        }
        sheetsStack.add(sheet);
        updateSheetsVisibility();
    }

    public StoryViewer createOverlayStoryViewer() {
        if (sheetsStack == null) {
            sheetsStack = new ArrayList<>();
        }
        StoryViewer storyViewer = new StoryViewer(this);
        if (parentLayout != null && parentLayout.isSheet()) {
            storyViewer.fromBottomSheet = true;
        }
        sheetsStack.add(storyViewer);
        updateSheetsVisibility();
        return storyViewer;
    }

    public ArticleViewer getArticleViewer() {
        if (getLastSheet() instanceof ArticleViewer.Sheet && getLastSheet().isShown()) {
            return ((ArticleViewer.Sheet) getLastSheet()).getArticleViewer();
        }
        if (
            parentLayout instanceof ActionBarLayout &&
            ((ActionBarLayout) parentLayout).getSheetFragment(false) != null &&
            ((ActionBarLayout) parentLayout).getSheetFragment(false).getLastSheet() instanceof ArticleViewer.Sheet
        ) {
            ArticleViewer.Sheet lastSheet = (ArticleViewer.Sheet) ((ActionBarLayout) parentLayout).getSheetFragment(false).getLastSheet();
            if (lastSheet.isShown()) {
                return lastSheet.getArticleViewer();
            }
        }
        return null;
    }

    public ArticleViewer createArticleViewer(boolean forceRecreate) {
        if (sheetsStack == null) {
            sheetsStack = new ArrayList<>();
        }
        if (!forceRecreate) {
            if (getLastSheet() instanceof ArticleViewer.Sheet && getLastSheet().isShown()) {
                return ((ArticleViewer.Sheet) getLastSheet()).getArticleViewer();
            }
            if (
                parentLayout instanceof ActionBarLayout &&
                ((ActionBarLayout) parentLayout).getSheetFragment(false) != null &&
                ((ActionBarLayout) parentLayout).getSheetFragment(false).getLastSheet() instanceof ArticleViewer.Sheet
            ) {
                ArticleViewer.Sheet lastSheet = (ArticleViewer.Sheet) ((ActionBarLayout) parentLayout).getSheetFragment(false).getLastSheet();
                if (lastSheet.isShown()) {
                    return lastSheet.getArticleViewer();
                }
            }
        }
        ArticleViewer articleViewer = ArticleViewer.makeSheet(this);
        addSheet(articleViewer.sheet);
        BottomSheetTabDialog.checkSheet(articleViewer.sheet);
        return articleViewer;
    }

    public void onBottomSheetCreated() {

    }

    public static class BottomSheetParams {
        public boolean transitionFromLeft;
        public boolean allowNestedScroll;
        public Runnable onDismiss;
        public Runnable onOpenAnimationFinished;
        public Runnable onPreFinished;
        public boolean occupyNavigationBar;
    }

    public boolean isSupportEdgeToEdge() {
        // warn: overridden method must return a constant
        return false;
    }

    public boolean drawEdgeNavigationBar() {
        return isSupportEdgeToEdge();
    }

    public WindowInsetsCompat onInsetsInternal(@NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
        final Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars() | WindowInsetsCompat.Type.statusBars());
        onInsets(insets.left, insets.top, insets.right, bottomInset = insets.bottom);
        return WindowInsetsCompat.CONSUMED;
    }

    private int bottomInset;
    public int getBottomInset() {
        return bottomInset;
    }

    public void onInsets(int left, int top, int right, int bottom) {

    }


    private Bulletin.Delegate bulletinDelegate;

    public void setBulletinDelegate(Bulletin.Delegate bulletinDelegate) {
        this.bulletinDelegate = bulletinDelegate;
    }

    public Bulletin.Delegate getBulletinDelegate() {
        return bulletinDelegate;
    }


    protected void dumpCanvas() {
        AndroidUtilities.dumpCanvas(fragmentView);
    }
}