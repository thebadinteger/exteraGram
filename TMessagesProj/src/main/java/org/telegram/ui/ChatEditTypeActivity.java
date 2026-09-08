) {
            return false;
        }
        return true;
    }

    private boolean deactivatingLinks = false;
    private boolean tryDeactivateAllLinks() {
        if (!isPrivate || currentChat.usernames == null || currentChat.usernames.isEmpty()) {
            return true;
        }
        if (deactivatingLinks) {
            return false;
        }
        deactivatingLinks = true;
        boolean hasActive = false;
        for (int i = 0; i < currentChat.usernames.size(); ++i) {
            final TLRPC.TL_username username = currentChat.usernames.get(i);
            if (username != null && username.active && !username.editable) {
                hasActive = true;
            }
        }
        if (hasActive) {
            TLRPC.TL_channels_deactivateAllUsernames req = new TLRPC.TL_channels_deactivateAllUsernames();
            req.channel = MessagesController.getInputChannel(currentChat);
            getConnectionsManager().sendRequest(req, (res, err) -> AndroidUtilities.runOnUIThread(() -> {
                if (res instanceof TLRPC.TL_boolTrue) {
                    for (int i = 0; i < currentChat.usernames.size(); ++i) {
                        final TLRPC.TL_username username = currentChat.usernames.get(i);
                        if (username != null && username.active && !username.editable) {
                            username.active = false;
                        }
                    }
                }
                deactivatingLinks = false;
                AndroidUtilities.runOnUIThread(this::processDone);
            }));
        } else {
            deactivatingLinks = false;
        }
        return !hasActive;
    }

    private boolean activatingEditableLink = false;
    private boolean tryActivateEditableUsername() {
        if (isPrivate || usernames == null || editableUsernameWasActive == null || editableUsernameUpdated == null || editableUsernameWasActive == editableUsernameUpdated) {
            return true;
        }
        if (activatingEditableLink) {
            return false;
        }
        activatingEditableLink = true;
        String username = null;
        for (int i = 0; i < usernames.size(); ++i) {
            if (usernames.get(i) != null && usernames.get(i).editable) {
                username = usernames.get(i).username;
            }
        }
        if (username == null) {
            activatingEditableLink = false;
            return true;
        }
        TLRPC.TL_channels_toggleUsername req = new TLRPC.TL_channels_toggleUsername();
        req.channel = MessagesController.getInputChannel(currentChat);
        req.active = editableUsernameUpdated;
        req.username = username;
        getConnectionsManager().sendRequest(req, (res, err) -> {
            activatingEditableLink = false;
            if (err == null) {
                AndroidUtilities.runOnUIThread(this::processDone);
            } else {
                updateDoneProgress(false);
            }
        });
        return false;
    }

    private void loadAdminedChannels() {
        if (loadingAdminedChannels || adminnedChannelsLayout == null) {
            return;
        }
        loadingAdminedChannels = true;
        updatePrivatePublic();
        TLRPC.TL_channels_getAdminedPublicChannels req = new TLRPC.TL_channels_getAdminedPublicChannels();
        getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            loadingAdminedChannels = false;
            if (response != null) {
                if (getParentActivity() == null) {
                    return;
                }
                for (int a = 0; a < adminedChannelCells.size(); a++) {
                    linearLayout.removeView(adminedChannelCells.get(a));
                }
                adminedChannelCells.clear();
                TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;

                for (int a = 0; a < res.chats.size(); a++) {
                    AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), view -> {
                        AdminedChannelCell cell = (AdminedChannelCell) view.getParent();
                        final TLRPC.Chat channel = cell.getCurrentChannel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString(R.string.AppName));
                        if (isChannel) {
                            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, getMessagesController().linkPrefix + "/" + ChatObject.getPublicUsername(channel), channel.title)));
                        } else {
                            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, getMessagesController().linkPrefix + "/" + ChatObject.getPublicUsername(channel), channel.title)));
                        }
                        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                        builder.setPositiveButton(LocaleController.getString(R.string.RevokeButton), (dialogInterface, i) -> {
                            TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
                            req1.channel = MessagesController.getInputChannel(channel);
                            req1.username = "";
                            getConnectionsManager().sendRequest(req1, (response1, error1) -> {
                                if (response1 instanceof TLRPC.TL_boolTrue) {
                                    AndroidUtilities.runOnUIThread(() -> {
                                        canCreatePublic = true;
                                        if (usernameTextView.length() > 0) {
                                            checkUserName(usernameTextView.getText().toString());
                                        }
                                        updatePrivatePublic();
                                    });
                                }
                            }, ConnectionsManager.RequestFlagInvokeAfter);
                        });
                        showDialog(builder.create());
                    }, false, 0);
                    adminedChannelCell.setChannel(res.chats.get(a), a == res.chats.size() - 1);
                    adminedChannelCells.add(adminedChannelCell);
                    adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 72));
                }
                updatePrivatePublic();
            }
        }));
    }

    private void updatePrivatePublic() {
        if (sectionCell2 == null) {
            return;
        }
        if (!isPrivate && !canCreatePublic && getUserConfig().isPremium()) {
            typeInfoCell.setText(LocaleController.getString(R.string.ChangePublicLimitReached));
            typeInfoCell.setTag(Theme.key_text_RedRegular);
            typeInfoCell.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
            linkContainer.setVisibility(View.GONE);
            checkTextView.setVisibility(View.GONE);
            sectionCell2.setVisibility(View.GONE);
            adminedInfoCell.setVisibility(View.VISIBLE);
            if (loadingAdminedChannels) {
                loadingAdminedCell.setVisibility(View.VISIBLE);
                adminnedChannelsLayout.setVisibility(View.GONE);
            } else {
                loadingAdminedCell.setVisibility(View.GONE);
                adminnedChannelsLayout.setVisibility(View.VISIBLE);
            }
        } else {
            typeInfoCell.setTag(Theme.key_windowBackgroundWhiteGrayText4);
            typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            if (isForcePublic) {
                sectionCell2.setVisibility(View.GONE);
            } else {
                sectionCell2.setVisibility(View.VISIBLE);
            }
            adminedInfoCell.setVisibility(View.GONE);
            adminnedChannelsLayout.setVisibility(View.GONE);
            linkContainer.setVisibility(View.VISIBLE);
            loadingAdminedCell.setVisibility(View.GONE);
            if (isChannel) {
                typeInfoCell.setText(isPrivate ? LocaleController.getString(R.string.ChannelPrivateLinkHelp) : LocaleController.getString(R.string.ChannelUsernameHelp));
                headerCell.setText(isPrivate ? LocaleController.getString(R.string.ChannelInviteLinkTitle) : LocaleController.getString(R.string.ChannelLinkTitle));
            } else {
                typeInfoCell.setText(isPrivate ? LocaleController.getString(R.string.MegaPrivateLinkHelp) : LocaleController.getString(R.string.MegaUsernameHelp));
                headerCell.setText(isPrivate ? LocaleController.getString(R.string.ChannelInviteLinkTitle) : LocaleController.getString(R.string.ChannelLinkTitle));
            }
            publicContainer.setVisibility(isPrivate ? View.GONE : View.VISIBLE);
            privateContainer.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            saveContainer.setVisibility(View.VISIBLE);
            manageLinksTextView.setVisibility(View.VISIBLE);
            manageLinksInfoCell.setVisibility(View.VISIBLE);
            linkContainer.setPadding(0, 0, 0, isPrivate ? 0 : AndroidUtilities.dp(7));
            permanentLinkView.setLink(invite != null ? invite.link : null);
            permanentLinkView.loadUsers(invite, chatId);
            checkTextView.setVisibility(!isPrivate && checkTextView.length() != 0 ? View.VISIBLE : View.GONE);
            final TLRPC.ChatFull chatFull = getMessagesController().getChatFull(chatId);
            final TLRPC.Chat chat = getMessagesController().getChat(chatId);
            manageLinksInfoCell.setText(LocaleController.getString(chatFull != null && chatFull.paid_media_allowed && ChatObject.isChannelAndNotMegaGroup(chat) ? R.string.ManageLinksInfoHelpPaid : R.string.ManageLinksInfoHelp));
        }
        radioButtonCell1.setChecked(!isPrivate, true);
        radioButtonCell2.setChecked(isPrivate, true);
        usernameTextView.clearFocus();
        if (joinContainer != null) {
            joinContainer.setVisibility(!isChannel || isPrivate ? View.VISIBLE : View.GONE);
            joinContainer.showJoinToSend(info != null && info.linked_chat_id != 0 && !isChannel);
        }
        if (usernamesListView != null) {
            usernamesListView.setVisibility(isPrivate || usernames.isEmpty() ? View.GONE : View.VISIBLE);
        }
        checkDoneButton();
    }

    private void checkDoneButton() {
        if (isPrivate || usernameTextView.length() > 0 || hasActiveLink()) {
            doneButton.setEnabled(true);
            doneButton.setAlpha(1.0f);
        } else {
            doneButton.setEnabled(false);
            doneButton.setAlpha(0.5f);
        }
    }

    public boolean hasActiveLink() {
        if (usernames == null) {
            return false;
        }
        for (int i = 0; i < usernames.size(); ++i) {
            TLRPC.TL_username u = usernames.get(i);
            if (u != null && u.active && !TextUtils.isEmpty(u.username)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUserName(final String name) {
        if (name != null && name.length() > 0) {
            checkTextView.setVisibility(View.VISIBLE);
        } else {
            checkTextView.setVisibility(View.GONE);
        }
        if (checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(checkRunnable);
            checkRunnable = null;
            lastCheckName = null;
            if (checkReqId != 0) {
                getConnectionsManager().cancelRequest(checkReqId, true);
            }
        }
        lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                checkTextView.setText(LocaleController.getString(R.string.LinkInvalid));
                checkTextView.setTextColorByKey(Theme.key_text_RedRegular);
                return false;
            }
            for (int a = 0; a < name.length(); a++) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (isChannel) {
                        checkTextView.setText(LocaleController.getString(R.string.LinkInvalidStartNumber));
                    } else {
                        checkTextView.setText(LocaleController.getString(R.string.LinkInvalidStartNumberMega));
                    }
                    checkTextView.setTextColorByKey(Theme.key_text_RedRegular);
                    return false;
                }
                if (!(ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_')) {
                    checkTextView.setText(LocaleController.getString(R.string.LinkInvalid));
                    checkTextView.setTextColorByKey(Theme.key_text_RedRegular);
                    return false;
                }
            }
        }
        if (name == null || name.length() < 4) {
            if (isChannel) {
                checkTextView.setText(LocaleController.getString(R.string.LinkInvalidShort));
            } else {
                checkTextView.setText(LocaleController.getString(R.string.LinkInvalidShortMega));
            }
            checkTextView.setTextColorByKey(Theme.key_text_RedRegular);
            return false;
        }
        if (name.length() > 32) {
            checkTextView.setText(LocaleController.getString(R.string.LinkInvalidLong));
            checkTextView.setTextColorByKey(Theme.key_text_RedRegular);
            return false;
        }

        checkTextView.setText(LocaleController.getString(R.string.LinkChecking));
        checkTextView.setTextColorByKey(Theme.key_windowBackgroundWhiteGrayText8);
        lastCheckName = name;
        checkRunnable = () -> {
            TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
            req.username = name;
            req.channel = getMessagesController().getInputChannel(chatId);
            checkReqId = getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                checkReqId = 0;
                if (lastCheckName != null && lastCheckName.equals(name)) {
                    if (error == null && response instanceof TLRPC.TL_boolTrue) {
                        checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
                        checkTextView.setTextColorByKey(Theme.key_windowBackgroundWhiteGreenText);
                        lastNameAvailable = true;
                    } else {
                        if (error != null && "USERNAME_INVALID".equals(error.text) && req.username.length() == 4) {
                            checkTextView.setText(LocaleController.getString(R.string.UsernameInvalidShort));
                            checkTextView.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                        } else if (error != null && "USERNAME_PURCHASE_AVAILABLE".equals(error.text)) {
                            if (req.username.length() == 4) {
                                checkTextView.setText(LocaleController.getString(R.string.UsernameInvalidShortPurchase));
                            } else {
                                checkTextView.setText(LocaleController.getString(R.string.UsernameInUsePurchase));
                            }
                            checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
                        } else if (error != null && "CHANNELS_ADMIN_PUBLIC_TOO_MUCH".equals(error.text)) {
                            canCreatePublic = false;
                            showPremiumIncreaseLimitDialog();
                        } else {
                            checkTextView.setText(LocaleController.getString(R.string.LinkInUse));
                            checkTextView.setTextColorByKey(Theme.key_text_RedRegular);
                        }
                        lastNameAvailable = false;
                    }
                }
            }), ConnectionsManager.RequestFlagFailOnServerErrors);
        };
        AndroidUtilities.runOnUIThread(checkRunnable, 300);
        return true;
    }

    private void generateLink(final boolean newRequest) {
        loadingInvite = true;
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.legacy_revoke_permanent = true;
        req.peer = getMessagesController().getInputPeer(-chatId);
        final int reqId = getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (error == null) {
                invite = (TLRPC.TL_chatInviteExported) response;
                if (info != null) {
                    info.exported_invite = invite;
                }
                if (newRequest) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString(R.string.RevokeAlertNewLink));
                    builder.setTitle(LocaleController.getString(R.string.RevokeLink));
                    builder.setNegativeButton(LocaleController.getString(R.string.OK), null);
                    showDialog(builder.create());
                }
            }
            loadingInvite = false;
            if (permanentLinkView != null) {
                permanentLinkView.setLink(invite != null ? invite.link : null);
                permanentLinkView.loadUsers(invite, chatId);
            }
        }));
        getConnectionsManager().bindRequestToGuid(reqId, classGuid);
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = () -> {
            if (adminnedChannelsLayout != null) {
                int count = adminnedChannelsLayout.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = adminnedChannelsLayout.getChildAt(a);
                    if (child instanceof AdminedChannelCell) {
                        ((AdminedChannelCell) child).update();
                    }
                }
            }

            permanentLinkView.updateColors();
            if (inviteLinkBottomSheet != null) {
                inviteLinkBottomSheet.updateColors();
            }
        };

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        themeDescriptions.add(new ThemeDescription(textCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_text_RedRegular));
        themeDescriptions.add(new ThemeDescription(textCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));

        themeDescriptions.add(new ThemeDescription(usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));

        themeDescriptions.add(new ThemeDescription(linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(saveHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));

        themeDescriptions.add(new ThemeDescription(saveRestrictCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_text_RedRegular));
        themeDescriptions.add(new ThemeDescription(checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText8));
        themeDescriptions.add(new ThemeDescription(checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText));

        themeDescriptions.add(new ThemeDescription(typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_text_RedRegular));

        themeDescriptions.add(new ThemeDescription(manageLinksInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_text_RedRegular));

        themeDescriptions.add(new ThemeDescription(saveRestrictInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(saveRestrictInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(saveRestrictInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_text_RedRegular));

        themeDescriptions.add(new ThemeDescription(adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
        themeDescriptions.add(new ThemeDescription(radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
        themeDescriptions.add(new ThemeDescription(radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        themeDescriptions.add(new ThemeDescription(adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        themeDescriptions.add(new ThemeDescription(adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, cellDelegate, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));

        themeDescriptions.add(new ThemeDescription(manageLinksTextView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(manageLinksTextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(manageLinksTextView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));


        return themeDescriptions;
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }
    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        if (linearLayout != null) {
            linearLayout.setPadding(dp(12), dp(4), dp(12), dp(12) + bottom);
        }
    }
}
