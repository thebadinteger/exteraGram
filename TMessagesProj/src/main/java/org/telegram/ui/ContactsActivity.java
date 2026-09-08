));
//                    float searchH = animatorSearchFieldHeight.getFactor();
//                    if (!searchForcedVisible && dy != 0) {
//                        searchH = MathUtils.clamp(searchH - dy, 0, dp(DialogsActivity.SEARCH_FIELD_HEIGHT));
//                        animatorSearchFieldHeight.forceFactor(searchH);
//                    }
//
//                    final boolean searchVisible = searchForcedVisible || (animatorSearchFieldHeight.getFactor() > 0);
//                    animatorSearchFieldVisible.setValue(searchVisible, true);
//                }

                final boolean shadowVisible = !(firstVisibleItem == 0 && firstViewTop >= listView.getPaddingTop());
                headerShadowView.setShadowVisible(shadowVisible, true);

                lastScrollToDown = dy < 0;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && scrollableViewNoiseSuppressor != null) {
                    scrollableViewNoiseSuppressor.onScrolled(dx, dy);
                    blur3_InvalidateBlur();
                }

                checkUi_searchFieldY();
            }
        });

        if (!createSecretChat && !returnAsResult) {
            floatingButton = new FragmentFloatingButton(context, resourceProvider);
            contentView.addView(floatingButton, FragmentFloatingButton.createDefaultLayoutParams());
            floatingButton.setOnClickListener(v -> {
                if (MessagesController.getInstance(currentAccount).isFrozen()) {
                    AccountFrozenAlert.show(currentAccount);
                    return;
                }
                new NewContactBottomSheet(ContactsActivity.this, getContext()).show();
            });

            floatingButton.setAnimation(R.raw.write_contacts_fab_icon, 44);
            floatingButton.imageView.getAnimatedDrawable().setCurrentFrame(floatingButton.imageView.getAnimatedDrawable().getFramesCount() - 1);
            floatingButton.setContentDescription(getString(R.string.CreateNewContact));
        }

        if (initialSearchString != null) {
            actionBar.openSearchField(initialSearchString, false);
            initialSearchString = null;
        }

        contentView.addView(actionBar);

        headerShadowView = new HeaderShadowView(context, parentLayout);
        headerShadowView.setShadowVisible(false, false);
        contentView.addView(headerShadowView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 5, Gravity.TOP));

        actionBar.setAdaptiveBackground(listView);
        actionBar.setDrawBlurBackground(contentView);

//        animatorSearchFieldHeight.forceFactor(dp(DialogsActivity.SEARCH_FIELD_HEIGHT));
        animatorSearchFieldVisible.setValue(true, false);

        checkUi_searchFieldHint();

        Bulletin.addDelegate(this, new Bulletin.Delegate() {
            @Override
            public void onBottomOffsetChange(float offset) {
                additionalFloatingTranslation = Math.max(0, offset - navigationBarHeight - additionFloatingButtonOffset);
                checkUi_floatingButtonPosition();
            }

            @Override
            public int getBottomOffset(int tag) {
                return navigationBarHeight + additionFloatingButtonOffset;
            }
        });
        if (LaunchActivity.instance != null) {
            LaunchActivity.instance.getRootAnimatedInsetsListener().subscribeToWindowInsetsAnimation(this);
        }
        ViewCompat.setOnApplyWindowInsetsListener(fragmentView, this::onApplyWindowInsets);
        return fragmentView;
    }

    @Override
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = super.createActionBar(context);
        actionBar.setUseContainerForTitles();
        actionBar.getTitlesContainer().setTranslationX(dp(4));
        actionBar.setAddToContainer(false);
        actionBar.createAdditionalSubTitleOverlayContainer();
        actionBar.getAdditionalSubTitleOverlayContainer().setTranslationX(dp(4));
        actionBar.getAdditionalSubTitleOverlayContainer().setTranslationY(-dp(2));
        return actionBar;
    }

    public boolean addOrRemoveSelectedContact(UserCell cell) {
        long dialogId = cell.getDialogId();
        if (selectedContacts.indexOfKey(dialogId) >= 0) {
            selectedContacts.remove(dialogId);
            cell.setChecked(false, true);
        } else if (cell.getCurrentObject() instanceof TLRPC.User) {
            selectedContacts.put(dialogId, (TLRPC.User) cell.getCurrentObject());
            cell.setChecked(true, true);
            return true;
        }

        return false;
    }

    public boolean addOrRemoveSelectedContact(ProfileSearchCell cell) {
        long dialogId = cell.getDialogId();
        if (selectedContacts.indexOfKey(dialogId) >= 0) {
            selectedContacts.remove(dialogId);
            cell.setChecked(false, true);
        } else {
            if (cell.getUser() != null) {
                selectedContacts.put(dialogId, cell.getUser());
                cell.setChecked(true, true);
                return true;
            }
        }
        return false;
    }

    private void showOrUpdateActionMode(Object cell) {
        boolean checked;
        if (cell instanceof UserCell) {
            checked = addOrRemoveSelectedContact((UserCell) cell);
        } else if (cell instanceof ProfileSearchCell) {
            checked = addOrRemoveSelectedContact((ProfileSearchCell) cell);
        } else {
            return;
        }
        boolean updateAnimated = false;

        if (actionBar.isActionModeShowed()) {
            if (selectedContacts.isEmpty()) {
                hideActionMode();
                return;
            }
            updateAnimated = true;
        } else if (checked) {
            AndroidUtilities.hideKeyboard(fragmentView.findFocus());
            actionBar.showActionMode();

            backDrawable.setRotation(1, true);
        }

        selectedContactsCountTextView.setNumber(selectedContacts.size(), updateAnimated);
    }

    private void hideActionMode() {
        actionBar.hideActionMode();
        int count = listView.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = listView.getChildAt(i);
            if (view instanceof UserCell) {
                UserCell cell = (UserCell) view;
                if (selectedContacts.indexOfKey(cell.getDialogId()) >= 0) {
                    cell.setChecked(false, true);
                }
            } else if (view instanceof ProfileSearchCell) {
                ProfileSearchCell cell = (ProfileSearchCell) view;
                if (selectedContacts.indexOfKey(cell.getDialogId()) >= 0) {
                    cell.setChecked(false, true);
                }
            }
        }
        selectedContacts.clear();
        backDrawable.setRotation(0, true);
    }

    private void performSelectedContactsDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), getResourceProvider());
        if (selectedContacts.size() == 1) {
            builder.setTitle(getString(R.string.DeleteContactTitle));
            builder.setMessage(getString(R.string.DeleteContactSubtitle));
        } else {
            builder.setTitle(LocaleController.formatPluralString("DeleteContactsTitle", selectedContacts.size()));
            builder.setMessage(getString(R.string.DeleteContactsSubtitle));
        }
        builder.setPositiveButton(getString(R.string.Delete), (dialog, which) -> {
            ArrayList<TLRPC.User> contacts = new ArrayList<>(selectedContacts.size());
            for (int i = 0; i < selectedContacts.size(); i++) {
                long key = selectedContacts.keyAt(i);
                TLRPC.User contact = selectedContacts.get(key);
                contacts.add(contact);
            }

            getContactsController().deleteContactsUndoable(getContext(), ContactsActivity.this, contacts);

            hideActionMode();
        });
        builder.setNegativeButton(getString(R.string.Cancel), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.redPositive();
    }

    private void didSelectResult(final TLRPC.User user, boolean useAlert, String param) {
        if (useAlert && selectAlertString != null) {
            if (getParentActivity() == null) {
                return;
            }
            if (user.bot) {
                if (user.bot_nochats) {
                    try {
                        BulletinFactory.of(this).createErrorBulletin(getString(R.string.BotCantJoinGroups)).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    return;
                }
                if (channelId != 0) {
                    TLRPC.Chat chat = getMessagesController().getChat(channelId);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(getString(R.string.AddBotAdminAlert));
                        builder.setMessage(getString(R.string.AddBotAsAdmin));
                        builder.setPositiveButton(getString(R.string.AddAsAdmin), (dialogInterface, i) -> {
                            if (delegate != null) {
                                delegate.didSelectContact(user, param, this);
                                delegate = null;
                            }
                        });
                        builder.setNegativeButton(getString(R.string.Cancel), null);
                    } else {
                        builder.setMessage(getString(R.string.CantAddBotAsAdmin));
                        builder.setPositiveButton(getString(R.string.OK), null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(getString(R.string.AppName));
            String message = LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user));
            EditTextBoldCursor editText = null;
            if (!user.bot && needForwardCount) {
                message = String.format("%s\n\n%s", message, getString(R.string.AddToTheGroupForwardCount));
                editText = new EditTextBoldCursor(getParentActivity());
                editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                editText.setText("50");
                editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
                editText.setGravity(Gravity.CENTER);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editText.setBackground(Theme.createEditTextDrawable(getParentActivity(), true));
                final EditText editTextFinal = editText;
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            String str = s.toString();
                            if (!str.isEmpty()) {
                                int value = Utilities.parseInt(str);
                                if (value < 0) {
                                    editTextFinal.setText("0");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else if (value > 300) {
                                    editTextFinal.setText("300");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else if (!str.equals("" + value)) {
                                    editTextFinal.setText("" + value);
                                    editTextFinal.setSelection(editTextFinal.length());
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }

                });
                builder.setView(editText);
            }
            builder.setMessage(message);
            final EditText finalEditText = editText;
            builder.setPositiveButton(getString(R.string.OK), (dialogInterface, i) -> didSelectResult(user, false, finalEditText != null ? finalEditText.getText().toString() : "0"));
            builder.setNegativeButton(getString(R.string.Cancel), null);
            showDialog(builder.create());
            if (editText != null) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
                if (layoutParams != null) {
                    if (layoutParams instanceof FrameLayout.LayoutParams) {
                        ((FrameLayout.LayoutParams) layoutParams).gravity = Gravity.CENTER_HORIZONTAL;
                    }
                    layoutParams.rightMargin = layoutParams.leftMargin = dp(24);
                    layoutParams.height = dp(36);
                    editText.setLayoutParams(layoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        } else {
            if (delegate != null) {
                delegate.didSelectContact(user, param, this);
                if (resetDelegate) {
                    delegate = null;
                }
            }
            if (needFinishFragment) {
                finishFragment();
            }
        }
    }

    @Override
    public boolean onBackPressed(boolean invoked) {
        if (actionBar.isActionModeShowed()) {
            if (invoked) hideActionMode();
            return false;
        } else if (animatorSearchHasQuery.getValue()) {
            if (invoked) {
                searchField.editText.getText().clear();
            }
            return false;
        }
        return super.onBackPressed(invoked);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listViewAdapter != null) {
            listViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        if (checkPermission && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                checkPermission = false;
                if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED/* ||
                    activity.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED*/) {
                    if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)/* ||
                        activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)*/) {
                        AlertDialog.Builder builder = AlertsCreator.createContactsPermissionDialog(activity, param -> {
                            askAboutContacts = param != 0;
                            if (param == 0) {
                                return;
                            }
                            askForPermissons(false);
                        });
                        showDialog(permissionDialog = builder.create());
                    } else {
                        askForPermissons(true);
                    }
                }
            }
        }
    }

    protected RecyclerListView getListView() {
        return listView;
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (permissionDialog != null && dialog == permissionDialog && getParentActivity() != null && askAboutContacts) {
            askForPermissons(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity == null || !UserConfig.getInstance(currentAccount).syncContacts || activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED/* && activity.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED*/) {
            return;
        }
        if (alert && askAboutContacts) {
            AlertDialog.Builder builder = AlertsCreator.createContactsPermissionDialog(activity, param -> {
                MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts2", false).commit();
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.contactsPermissionBadgeCheck);
                askAboutContacts = param != 0;
                if (param == 0) {
                    return;
                }
                askForPermissons(false);
            });
            showDialog(builder.create());
            return;
        }
        permissionRequestTime = SystemClock.elapsedRealtime();
        ArrayList<String> permissons = new ArrayList<>();
        permissons.add(Manifest.permission.READ_CONTACTS);
        permissons.add(Manifest.permission.WRITE_CONTACTS);
        permissons.add(Manifest.permission.GET_ACCOUNTS);
        String[] items = permissons.toArray(new String[0]);
        try {
            activity.requestPermissions(items, 1);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length <= a) {
                    continue;
                }
                if (Manifest.permission.READ_CONTACTS.equals(permissions[a])) {
                    if (grantResults[a] == PackageManager.PERMISSION_GRANTED) {
                        ContactsController.getInstance(currentAccount).forceImportContacts();
                    } else {
                        MessagesController.getGlobalNotificationsSettings().edit()
                            .putBoolean("askAboutContacts", askAboutContacts = false)
                            .putBoolean("askAboutContacts2", false)
                            .apply();
                        if (SystemClock.elapsedRealtime() - permissionRequestTime < 200) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", ApplicationLoader.applicationContext.getPackageName(), null);
                                intent.setData(uri);
                                getParentActivity().startActivity(intent);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (actionBar != null) {
            actionBar.closeSearchField();
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            if (listViewAdapter != null) {
                if (!sortByName) {
                    listViewAdapter.setSortType(2, true);
                }
                listViewAdapter.notifyDataSetChanged();
            }
            if (searchListViewAdapter != null && listView.getAdapter() == searchListViewAdapter) {
                searchListViewAdapter.searchDialogs(searchQuery);
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = (Integer) args[0];
            if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0 || (mask & MessagesController.UPDATE_MASK_NAME) != 0 || (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {
                updateVisibleRows(mask);
            }
            if ((mask & MessagesController.UPDATE_MASK_STATUS) != 0 && !sortByName && listViewAdapter != null) {
                scheduleSort();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (createSecretChat && creatingChat) {
                TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) args[0];
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.closeChats);
                presentFragment(new ChatActivity(args2), false);
            }
        } else if (id == NotificationCenter.closeChats) {
            if (!creatingChat) {
                removeSelfFromStack(true);
            }
        }
    }

    boolean scheduled;
    Runnable sortContactsRunnable = new Runnable() {
        @Override
        public void run() {
            listViewAdapter.sortOnlineContacts();
            scheduled = false;
        }
    };

    private void scheduleSort() {
        if (!scheduled) {
            scheduled = true;
            AndroidUtilities.cancelRunOnUIThread(sortContactsRunnable);
            AndroidUtilities.runOnUIThread(sortContactsRunnable, 5000);
        }
    }

    private void updateVisibleRows(int mask) {
        if (listView != null) {
            int count = listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    public void setDelegate(ContactsActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public void setIgnoreUsers(LongSparseArray<TLRPC.User> users) {
        ignoreUsers = users;
    }

    public void setInitialSearchString(String initialSearchString) {
        this.initialSearchString = initialSearchString;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        ThemeDescription.ThemeDescriptionDelegate cellDelegate = () -> {
            if (listView != null) {
                int count = listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    if (child instanceof UserCell) {
                        ((UserCell) child).update(0);
                    } else if (child instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) child).update(0);
                    }
                }
            }
            if (actionModeCloseView != null) {
                actionModeCloseView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.MULTIPLY));
                actionModeCloseView.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector)));
            }
            if (actionBar != null) {
                actionBar.updateColors();
            }
            if (contentView != null) {
                contentView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundGray));
            }
        };

        if (!hasMainTabs) {
            themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        }
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));

        if (floatingButton != null) {
            themeDescriptions.add(new ThemeDescription(floatingButton.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
            themeDescriptions.add(new ThemeDescription(floatingButton.imageView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
            themeDescriptions.add(new ThemeDescription(floatingButton.imageView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
        }
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, null, null, Theme.key_chats_name));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, null, null, Theme.key_chats_secretName));

        return themeDescriptions;
    }

//    private int lastListScrollState;
//    private boolean canScrollByAnimation;
//    private float lastSearchFieldHeight;
//    private float scrollByAcc;

    @Override
    public void onFactorChanged(int id, float factor, float fraction, FactorAnimator callee) {
        if (id == ANIMATOR_ID_SEARCH_FIELD_VISIBLE) {
            checkUi_searchButton();
//        } else if (id == ANIMATOR_ID_SEARCH_FIELD_HEIGHT) {
//            searchField.setAlpha(animatorSearchFieldVisible.getFloatValue() * (animatorSearchFieldHeight.getFactor() / dp(DialogsActivity.SEARCH_FIELD_HEIGHT)));
//            headerShadowView.setTranslationY(factor);
//            final float alpha = factor / dp(DialogsActivity.SEARCH_FIELD_HEIGHT);
//            searchField.setClipHeight(alpha);
//
//            if (canScrollByAnimation && lastListScrollState == RecyclerView.SCROLL_STATE_IDLE) {
//                scrollByAcc += (lastSearchFieldHeight - factor);
//                int scrollBy = Math.round(scrollByAcc);
//                scrollByAcc -= scrollBy;
//                listView.scrollBy(0, scrollBy);
//            }
//
//            lastSearchFieldHeight = factor;
//            checkUi_listClip();
        } else if (id == ANIMATOR_ID_SEARCH_HAS_QUERY) {
            checkUi_searchButton();
            checkUi_sortItem();
        }
    }

    @Override
    public boolean canParentTabsSlide(MotionEvent ev, boolean forward) {
        if (listView != null && listView.getFastScroll() != null && listView.getFastScroll().isPressed()) {
            return false;
        }
        return true;
    }

    @Override
    public void onFactorChangeFinished(int id, float finalFactor, FactorAnimator callee) {
//        if (id == ANIMATOR_ID_SEARCH_FIELD_HEIGHT) {
//            canScrollByAnimation = false;
//            scrollByAcc = 0;
//        }
    }

    /* * */

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    private int additionNavigationBarHeight;
    private int additionFloatingButtonOffset;
    private float additionalFloatingTranslation;
    private int navigationBarHeight;
    private int imeInsetAnimatedHeight;

    @Override
    public View getAnimatedInsetsTargetView() {
        return fragmentView;
    }

    @Override
    public void onAnimatedInsetsChanged(View view, WindowInsetsCompat insets) {
        imeInsetAnimatedHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
        checkUi_emptyView();
    }

    @NonNull
    private WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
        navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

        checkUi_listViewPadding();
        checkUi_floatingButtonPosition();
        checkUi_emptyView();

        return WindowInsetsCompat.CONSUMED;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        navigationBarHeight = bottom;
        checkUi_listViewPadding();
        checkUi_floatingButtonPosition();
        checkUi_emptyView();
    }

    private void checkUi_emptyView() {
        if (emptyView != null) {
            emptyView.setKeyboardHeight(Math.max(navigationBarHeight + additionNavigationBarHeight, imeInsetAnimatedHeight), false);
        }
    }

    private void checkUi_listViewPadding() {
        listView.setPadding(
            0,
            dp(ADDITIONAL_LIST_HEIGHT_DP + 44) + actionBar.getMeasuredHeight(),
            0,
            dp(ADDITIONAL_LIST_HEIGHT_DP) + navigationBarHeight + additionNavigationBarHeight
        );
    }

    private boolean lastIsEmpty;

    private void checkUi_searchFieldHint() {
        final boolean isEmpty = listViewAdapter != null && listViewAdapter.isEmpty();

        if (lastIsEmpty != isEmpty || TextUtils.isEmpty(searchField.editText.getHint())) {
            searchField.editText.setHint(getString(isEmpty ? R.string.SearchPeopleByUsername : R.string.SearchContacts));
            searchField.editText.setContentDescription(getString(isEmpty ? R.string.SearchPeopleByUsername : R.string.SearchContacts));
            lastIsEmpty = isEmpty;
        }
    }

    private void checkUi_searchFieldY() {
        float top = listView.getY() + listView.getPaddingTop();
        for (int i = 0; i < listView.getChildCount(); ++i) {
            final View child = listView.getChildAt(i);
            final int position = listView.getChildAdapterPosition(child);
            if (position == 0) {
                final RecyclerView.ItemDecoration decoration = listView.getItemDecorationAt(i);
                decoration.getItemOffsets(AndroidUtilities.rectTmp2, child, listView, listView.mState);
                top = listView.getY() + (child.getY() - (listViewAdapter.isEmptyWithMainTabs ? 0 : AndroidUtilities.rectTmp2.top));
                break;
            } else if (position > 0) {
                top = -dp(52);
                break;
            }
        }
        searchField.setTranslationY(lerp(top, listView.getY() + listView.getPaddingTop(), animatorSearchHasQuery.getFloatValue()) - dp(48));
        animatorSearchFieldVisible.setValue(top > listView.getY() + listView.getPaddingTop() - dp(12), true);
    }

    private void checkUi_sortItem() {
        final float factor1 = 1f - animatorSearchHasQuery.getFloatValue();
        final float factor2 = listViewAdapter == null || listViewAdapter.isEmpty() ? 0 : 1;
        final float factor = factor1 * factor2;
        FragmentFloatingButton.setAnimatedVisibility(sortItem, factor);
    }

    private void checkUi_searchButton() {
        final float factor1 = 1f - animatorSearchFieldVisible.getFloatValue();
        final float factor2 = 1f - animatorSearchHasQuery.getFloatValue();
        final float factor = factor1 * factor2;
        FragmentFloatingButton.setAnimatedVisibility(searchItem, factor);
    }

    private void checkUi_floatingButtonPosition() {
        if (floatingButton != null) {
            floatingButton.setTranslationY(-navigationBarHeight - additionFloatingButtonOffset - additionalFloatingTranslation);
        }
    }

    private void checkUi_floatingButtonVisible() {
        if (floatingButton != null && listViewAdapter != null) {
            floatingButton.setButtonVisible(floatingButtonVisibleByScroll && !searching && !listViewAdapter.isEmpty(), true);
        }
    }



    /* Blur */

    private final @Nullable DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private final @Nullable BlurredBackgroundSourceRenderNode iBlur3SourceGlassFrosted;
    private final @Nullable BlurredBackgroundSourceRenderNode iBlur3SourceGlass;

    private IBlur3Capture iBlur3Capture;
    private boolean iBlur3Invalidated;

    private final ArrayList<RectF> iBlur3Positions = new ArrayList<>();
    private final RectF iBlur3PositionActionBar = new RectF();
    private final RectF iBlur3PositionMainTabs = new RectF(); {
        iBlur3Positions.add(iBlur3PositionActionBar);
        iBlur3Positions.add(iBlur3PositionMainTabs);
    }

    private void blur3_InvalidateBlur() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || scrollableViewNoiseSuppressor == null) {
            return;
        }

        final int additionalList = dp(48);
        final int additionalSearch = dp(DialogsActivity.SEARCH_FIELD_HEIGHT);

        final int mainTabBottom = fragmentView.getMeasuredHeight() - navigationBarHeight - dp(DialogsActivity.MAIN_TABS_MARGIN);
        final int mainTabTop = mainTabBottom - dp(DialogsActivity.MAIN_TABS_HEIGHT);

        iBlur3PositionActionBar.set(0, -additionalList, fragmentView.getMeasuredWidth(), actionBar.getMeasuredHeight() + additionalList + additionalSearch );
        iBlur3PositionMainTabs.set(0, mainTabTop, fragmentView.getMeasuredWidth(), mainTabBottom);
        iBlur3PositionMainTabs.inset(0, LiteMode.isEnabled(LiteMode.FLAG_LIQUID_GLASS) ? 0 : -dp(48));

        scrollableViewNoiseSuppressor.setupRenderNodes(iBlur3Positions, hasMainTabs ? 2 : 1);
        scrollableViewNoiseSuppressor.invalidateResultRenderNodes(iBlur3Capture, fragmentView.getMeasuredWidth(), fragmentView.getMeasuredHeight());
    }

    @Override
    public BlurredBackgroundSourceRenderNode getGlassSource() {
        return iBlur3SourceGlass;
    }


    @Override
    public void onParentScrollToTop() {
        if (layoutManager.findFirstVisibleItemPosition() < 15) {
            listView.smoothScrollToPosition(0);
        } else {
            scrollHelper.setScrollDirection(RecyclerAnimationScrollHelper.SCROLL_DIRECTION_UP);
            scrollHelper.scrollToPosition(0, 0, false, true);
        }
//        animatorSearchFieldHeight.animateTo(dp(DialogsActivity.SEARCH_FIELD_HEIGHT));
        animatorSearchFieldVisible.setValue(true, true);
    }
}
