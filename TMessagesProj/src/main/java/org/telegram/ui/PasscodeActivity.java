private void setCustomKeyboardVisible(boolean visible, boolean animate) {
        if (visible) {
            AndroidUtilities.hideKeyboard(fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), classGuid);
        } else {
            AndroidUtilities.removeAltFocusable(getParentActivity(), classGuid);
        }

        if (!animate) {
            keyboardView.setVisibility(visible ? View.VISIBLE : View.GONE);
            keyboardView.setAlpha(visible ? 1 : 0);
            keyboardView.setTranslationY(visible ? 0 : dp(CustomPhoneKeyboardView.KEYBOARD_HEIGHT_DP));
            fragmentView.requestLayout();
        } else {
            ValueAnimator animator = ValueAnimator.ofFloat(visible ? 0 : 1, visible ? 1 : 0).setDuration(150);
            animator.setInterpolator(visible ? CubicBezierInterpolator.DEFAULT : Easings.easeInOutQuad);
            animator.addUpdateListener(animation -> {
                float val = (float) animation.getAnimatedValue();
                keyboardView.setAlpha(val);
                keyboardView.setTranslationY((1f - val) * dp(CustomPhoneKeyboardView.KEYBOARD_HEIGHT_DP) * 0.75f);
                fragmentView.requestLayout();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (visible) {
                        keyboardView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!visible) {
                        keyboardView.setVisibility(View.GONE);
                    }
                }
            });
            animator.start();
        }
    }

    /**
     * @return New fragment to open when Passcode entry gets clicked
     */
    public static BaseFragment determineOpenFragment() {
        if (!SharedConfig.passcodeHash.isEmpty()) {
            return new PasscodeActivity(TYPE_ENTER_CODE_TO_MANAGE_SETTINGS);
        }
        return new ActionIntroActivity(ActionIntroActivity.ACTION_TYPE_SET_PASSCODE);
    }

    private void animateSuccessAnimation(Runnable callback) {
        if (!isPinCode()) {
            callback.run();
            return;
        }
        for (int i = 0; i < codeFieldContainer.codeField.length; i++) {
            CodeNumberField field = codeFieldContainer.codeField[i];
            field.postDelayed(()-> field.animateSuccessProgress(1f), i * 75L);
        }
        codeFieldContainer.postDelayed(() -> {
            for (CodeNumberField f : codeFieldContainer.codeField) {
                f.animateSuccessProgress(0f);
            }
            callback.run();
        }, codeFieldContainer.codeField.length * 75L + 350L);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setCustomKeyboardVisible(isCustomKeyboardVisible(), false);
        if (lockImageView != null) {
            lockImageView.setVisibility(!AndroidUtilities.isSmallScreen() && AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y ? View.VISIBLE : View.GONE);
        }
        if (codeFieldContainer != null && codeFieldContainer.codeField != null) {
            for (CodeNumberField f : codeFieldContainer.codeField) {
                f.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (type != TYPE_MANAGE_CODE_SETTINGS && !isCustomKeyboardVisible()) {
            AndroidUtilities.runOnUIThread(this::showKeyboard, 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);

        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), classGuid);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAltFocusable(getParentActivity(), classGuid);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetPasscode && (args.length == 0 || (Boolean) args[0])) {
            if (type == TYPE_MANAGE_CODE_SETTINGS) {
                updateRows();
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void updateRows() {
        fingerprintRow = -1;
        rowCount = 0;
        utyanRow = rowCount++;
        hintRow = rowCount++;
        changePasscodeRow = rowCount++;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (
                    BiometricManager.from(ApplicationLoader.applicationContext).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS &&
                    AndroidUtilities.isKeyguardSecure()
                ) {
                    fingerprintRow = rowCount++;
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        autoLockRow = rowCount++;
        autoLockDetailRow = rowCount++;
        captureHeaderRow = rowCount++;
        captureRow = rowCount++;
        captureDetailRow = rowCount++;
        disablePasscodeRow = rowCount++;
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && type != TYPE_MANAGE_CODE_SETTINGS) {
            showKeyboard();
        }
    }

    private void showKeyboard() {
        if (isPinCode()) {
            codeFieldContainer.codeField[0].requestFocus();
            if (!isCustomKeyboardVisible()) {
                AndroidUtilities.showKeyboard(codeFieldContainer.codeField[0]);
            }
        } else if (isPassword()) {
            passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(passwordEditText);
        }
    }

    private void updateFields() {
        String text;
        if (type == TYPE_ENTER_CODE_TO_MANAGE_SETTINGS) {
            text = LocaleController.getString(R.string.EnterYourPasscodeInfo);
        } else if (passcodeSetStep == 0) {
            text = LocaleController.getString(currentPasswordType == SharedConfig.PASSCODE_TYPE_PIN ? R.string.CreatePasscodeInfoPIN : R.string.CreatePasscodeInfoPassword);
        } else text = descriptionTextSwitcher.getCurrentView().getText().toString();

        boolean animate = !(descriptionTextSwitcher.getCurrentView().getText().equals(text) || TextUtils.isEmpty(descriptionTextSwitcher.getCurrentView().getText()));
        if (type == TYPE_ENTER_CODE_TO_MANAGE_SETTINGS) {
            descriptionTextSwitcher.setText(LocaleController.getString(R.string.EnterYourPasscodeInfo), animate);
        } else if (passcodeSetStep == 0) {
            descriptionTextSwitcher.setText(LocaleController.getString(currentPasswordType == SharedConfig.PASSCODE_TYPE_PIN ? R.string.CreatePasscodeInfoPIN : R.string.CreatePasscodeInfoPassword), animate);
        }
        if (isPinCode()) {
            AndroidUtilities.updateViewVisibilityAnimated(codeFieldContainer, true, 1f, animate);
            AndroidUtilities.updateViewVisibilityAnimated(outlinePasswordView, false, 1f, animate);
        } else if (isPassword()) {
            AndroidUtilities.updateViewVisibilityAnimated(codeFieldContainer, false, 1f, animate);
            AndroidUtilities.updateViewVisibilityAnimated(outlinePasswordView, true, 1f, animate);
        }
        boolean show = isPassword();
        if (show) {
            onShowKeyboardCallback = () -> {
                floatingButton.setButtonVisible(true, animate);
                AndroidUtilities.cancelRunOnUIThread(onShowKeyboardCallback);
            };
            AndroidUtilities.runOnUIThread(onShowKeyboardCallback, 3000); // Timeout for floating keyboard
        } else {
            floatingButton.setButtonVisible(false, animate);
        }
        setCustomKeyboardVisible(isCustomKeyboardVisible(), animate);
        showKeyboard();
    }

    /**
     * @return If custom keyboard should be visible
     */
    private boolean isCustomKeyboardVisible() {
        return isPinCode() && type != TYPE_MANAGE_CODE_SETTINGS && !AndroidUtilities.isTablet() &&
                AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    private void processNext() {
        if (currentPasswordType == SharedConfig.PASSCODE_TYPE_PASSWORD && passwordEditText.getText().length() == 0 || currentPasswordType == SharedConfig.PASSCODE_TYPE_PIN && codeFieldContainer.getCode().length() != 4) {
            onPasscodeError();
            return;
        }

        if (otherItem != null) {
            otherItem.setVisibility(View.GONE);
        }

        titleTextView.setText(LocaleController.getString(R.string.ConfirmCreatePasscode));
        descriptionTextSwitcher.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PasscodeReinstallNotice)));
        firstPassword = isPinCode() ? codeFieldContainer.getCode() : passwordEditText.getText().toString();
        passwordEditText.setText("");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        for (CodeNumberField f : codeFieldContainer.codeField) f.setText("");
        showKeyboard();
        passcodeSetStep = 1;
    }

    private boolean isPinCode() {
        return type == TYPE_SETUP_CODE && currentPasswordType == SharedConfig.PASSCODE_TYPE_PIN ||
                type == TYPE_ENTER_CODE_TO_MANAGE_SETTINGS && SharedConfig.passcodeType == SharedConfig.PASSCODE_TYPE_PIN;
    }

    private boolean isPassword() {
        return type == TYPE_SETUP_CODE && currentPasswordType == SharedConfig.PASSCODE_TYPE_PASSWORD ||
                type == TYPE_ENTER_CODE_TO_MANAGE_SETTINGS && SharedConfig.passcodeType == SharedConfig.PASSCODE_TYPE_PASSWORD;
    }

    private void processDone() {
        if (isPassword() && passwordEditText.getText().length() == 0) {
            onPasscodeError();
            return;
        }
        String password = isPinCode() ? codeFieldContainer.getCode() : passwordEditText.getText().toString();
        if (type == TYPE_SETUP_CODE) {
            if (!firstPassword.equals(password)) {
                AndroidUtilities.updateViewVisibilityAnimated(passcodesDoNotMatchTextView, true);
                for (CodeNumberField f : codeFieldContainer.codeField) {
                    f.setText("");
                }
                if (isPinCode()) {
                    codeFieldContainer.codeField[0].requestFocus();
                }
                passwordEditText.setText("");
                onPasscodeError();

                codeFieldContainer.removeCallbacks(hidePasscodesDoNotMatch);
                codeFieldContainer.post(()->{
                    codeFieldContainer.postDelayed(hidePasscodesDoNotMatch, 3000);
                    postedHidePasscodesDoNotMatch = true;
                });
                return;
            }

            boolean isFirst = SharedConfig.passcodeHash.isEmpty();
            try {
                SharedConfig.passcodeSalt = new byte[16];
                Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                byte[] passcodeBytes = firstPassword.getBytes(StandardCharsets.UTF_8);
                byte[] bytes = new byte[32 + passcodeBytes.length];
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, 0, 16);
                System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
            } catch (Exception e) {
                FileLog.e(e);
            }
            SharedConfig.allowScreenCapture = true;
            SharedConfig.passcodeType = currentPasswordType;
            SharedConfig.saveConfig();

            passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(passwordEditText);
            for (CodeNumberField f : codeFieldContainer.codeField) {
                f.clearFocus();
                AndroidUtilities.hideKeyboard(f);
            }
            keyboardView.setEditText(null);

            animateSuccessAnimation(() -> {
                getMediaDataController().buildShortcuts();
                if (isFirst) {
                    presentFragment(new PasscodeActivity(TYPE_MANAGE_CODE_SETTINGS), true);
                    if (openedSettings != null) {
                        AndroidUtilities.runOnUIThread(openedSettings);
                        openedSettings = null;
                    }
                } else {
                    finishFragment();
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode);
            });
        } else if (type == TYPE_ENTER_CODE_TO_MANAGE_SETTINGS) {
            if (SharedConfig.passcodeRetryInMs > 0) {
                int value = Math.max(1, (int) Math.ceil(SharedConfig.passcodeRetryInMs / 1000.0));
                Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", R.string.TooManyTries, LocaleController.formatPluralString("Seconds", value)), Toast.LENGTH_SHORT).show();

                for (CodeNumberField f : codeFieldContainer.codeField) {
                    f.setText("");
                }
                passwordEditText.setText("");
                if (isPinCode()) {
                    codeFieldContainer.codeField[0].requestFocus();
                }
                onPasscodeError();
                return;
            }
            if (!SharedConfig.checkPasscode(password)) {
                SharedConfig.increaseBadPasscodeTries();
                passwordEditText.setText("");
                for (CodeNumberField f : codeFieldContainer.codeField) {
                    f.setText("");
                }
                if (isPinCode()) {
                    codeFieldContainer.codeField[0].requestFocus();
                }
                onPasscodeError();
                return;
            }
            SharedConfig.badPasscodeTries = 0;
            SharedConfig.saveConfig();

            passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(passwordEditText);
            for (CodeNumberField f : codeFieldContainer.codeField) {
                f.clearFocus();
                AndroidUtilities.hideKeyboard(f);
            }
            keyboardView.setEditText(null);

            animateSuccessAnimation(() -> {
                presentFragment(new PasscodeActivity(TYPE_MANAGE_CODE_SETTINGS), true);
                if (openedSettings != null) {
                    AndroidUtilities.runOnUIThread(openedSettings);
                    openedSettings = null;
                }
            });
        }
    }

    private Runnable openedSettings;
    public void setOnOpenedSettings(Runnable openedSettings) {
        this.openedSettings = openedSettings;
    }

    private void onPasscodeError() {
        if (getParentActivity() == null) return;
        try {
            fragmentView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        } catch (Exception ignore) {}
        if (isPinCode()) {
            for (CodeNumberField f : codeFieldContainer.codeField) {
                f.animateErrorProgress(1f);
            }
        } else {
            outlinePasswordView.animateError(1f);
        }
        AndroidUtilities.shakeViewSpring(isPinCode() ? codeFieldContainer : outlinePasswordView, isPinCode() ? 10 : 4, () -> AndroidUtilities.runOnUIThread(()->{
            if (isPinCode()) {
                for (CodeNumberField f : codeFieldContainer.codeField) {
                    f.animateErrorProgress(0f);
                }
            } else {
                outlinePasswordView.animateError(0f);
            }
        }, isPinCode() ? 150 : 1000));
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final static int VIEW_TYPE_CHECK = 0,
                VIEW_TYPE_SETTING = 1,
                VIEW_TYPE_INFO = 2,
                VIEW_TYPE_HEADER = 3,
                VIEW_TYPE_UTYAN = 4;

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == fingerprintRow || position == autoLockRow || position == captureRow ||
                    position == changePasscodeRow || position == disablePasscodeRow;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_TYPE_CHECK:
                    view = new TextCheckCell(mContext);
                    break;
                case VIEW_TYPE_SETTING:
                    view = new TextSettingsCell(mContext);
                    break;
                case VIEW_TYPE_HEADER:
                    view = new HeaderCell(mContext);
                    break;
                case VIEW_TYPE_UTYAN:
                    view = new RLottieImageHolderView(mContext);
                    view.setTag(RecyclerListView.TAG_NOT_SECTION);
                    break;
                case VIEW_TYPE_INFO:
                default:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_CHECK: {
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    if (position == fingerprintRow) {
                        textCell.setTextAndCheck(LocaleController.getString(R.string.UnlockFingerprint), SharedConfig.useFingerprintLock, false);
                    } else if (position == captureRow) {
                        textCell.setTextAndCheck(LocaleController.getString(R.string.ScreenCaptureShowContent), SharedConfig.allowScreenCapture, false);
                    }
                    break;
                }
                case VIEW_TYPE_SETTING: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == changePasscodeRow) {
                        textCell.setText(LocaleController.getString(R.string.ChangePasscode), true);
                        if (SharedConfig.passcodeHash.isEmpty()) {
                            textCell.setTag(Theme.key_windowBackgroundWhiteGrayText7);
                            textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                        } else {
                            textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                            textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        }
                    } else if (position == autoLockRow) {
                        String val;
                        if (SharedConfig.autoLockIn == 0) {
                            val = LocaleController.formatString("AutoLockDisabled", R.string.AutoLockDisabled);
                        } else if (SharedConfig.autoLockIn < 60 * 60) {
                            val = LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60));
                        } else if (SharedConfig.autoLockIn < 60 * 60 * 24) {
                            val = LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", (int) Math.ceil(SharedConfig.autoLockIn / 60.0f / 60)));
                        } else {
                            val = LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Days", (int) Math.ceil(SharedConfig.autoLockIn / 60.0f / 60 / 24)));
                        }
                        textCell.setTextAndValue(LocaleController.getString(R.string.AutoLock), val, true);
                        textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    } else if (position == disablePasscodeRow) {
                        textCell.setText(LocaleController.getString(R.string.DisablePasscode), false);
                        textCell.setTag(Theme.key_text_RedBold);
                        textCell.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                    }
                    break;
                }
                case VIEW_TYPE_HEADER: {
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    cell.setHeight(46);
                    if (position == captureHeaderRow) {
                        cell.setText(LocaleController.getString(R.string.ScreenCaptureHeader));
                    }
                    break;
                }
                case VIEW_TYPE_UTYAN: {
                    RLottieImageHolderView holderView = (RLottieImageHolderView) holder.itemView;
                    holderView.imageView.setAnimation(R.raw.utyan_passcode, 100, 100);
                    holderView.imageView.playAnimation();
                    break;
                }
                case VIEW_TYPE_INFO: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == hintRow) {
                        cell.setText(LocaleController.getString(R.string.PasscodeScreenHint));
                        cell.setBackground(null);
                        cell.getTextView().setGravity(Gravity.CENTER_HORIZONTAL);
                    } else if (position == autoLockDetailRow) {
                        cell.setText(LocaleController.getString(R.string.AutoLockInfo));
                        cell.getTextView().setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
                    } else if (position == captureDetailRow) {
                        cell.setText(LocaleController.getString(R.string.ScreenCaptureInfo));
                        cell.getTextView().setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == fingerprintRow || position == captureRow) {
                return VIEW_TYPE_CHECK;
            } else if (position == changePasscodeRow || position == autoLockRow || position == disablePasscodeRow) {
                return VIEW_TYPE_SETTING;
            } else if (position == autoLockDetailRow || position == captureDetailRow || position == hintRow) {
                return VIEW_TYPE_INFO;
            } else if (position == captureHeaderRow) {
                return VIEW_TYPE_HEADER;
            } else if (position == utyanRow) {
                return VIEW_TYPE_UTYAN;
            }
            return VIEW_TYPE_CHECK;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));

        if (type != TYPE_MANAGE_CODE_SETTINGS)
            themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        themeDescriptions.add(new ThemeDescription(passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText7));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        return themeDescriptions;
    }

    private final static class RLottieImageHolderView extends FrameLayout {
        private final RLottieImageView imageView;

        private RLottieImageHolderView(@NonNull Context context) {
            super(context);
            imageView = new RLottieImageView(context);
            imageView.setOnClickListener(v -> {
                if (!imageView.getAnimatedDrawable().isRunning()) {
                    imageView.getAnimatedDrawable().setCurrentFrame(0, false);
                    imageView.playAnimation();
                }
            });
            int size = dp(120);
            LayoutParams params = new LayoutParams(size, size);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            addView(imageView, params);

            setPadding(0, dp(32), 0, 0);
            setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}
