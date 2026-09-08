} else {
                if (currentType == TYPE_ADD_BOT) {
                    manageRow = rowCount++;
                }
                changeInfoRow = rowCount++;
                deleteMessagesRow = rowCount++;
                banUsersRow = rowCount++;
                addUsersRow = rowCount++;
                pinMessagesRow = rowCount++;
                if (currentType != TYPE_ADD_BOT) { // TODO: bots will support?
                    editTagsRow = rowCount++;
                }
                if (ChatObject.isChannel(currentChat)) {
                    channelStoriesRow = rowCount++;
                    if (channelStoriesExpanded) {
                        channelPostStoriesRow = rowCount++;
                        channelEditStoriesRow = rowCount++;
                        channelDeleteStoriesRow = rowCount++;
                    }
                }
                startVoiceChatRow = rowCount++;
                addAdminsRow = rowCount++;
                anonymousRow = rowCount++;
                if (isForum) {
                    manageTopicsRow = rowCount++;
                }
                if (currentUserIsBotGuard) {
                    guardBotRow = rowCount++;
                    guardBotInfoRow = rowCount++;
                }
            }
        } else if (currentType == TYPE_BANNED) {
            sendMessagesRow = rowCount++;
            sendMediaRow = rowCount++;
            if (sendMediaExpanded) {
                sendPhotosRow = rowCount++;
                sendVideosRow = rowCount++;
                sendFilesRow = rowCount++;
                sendMusicRow = rowCount++;
                sendVoiceRow = rowCount++;
                sendRoundRow = rowCount++;
                sendStickersRow = rowCount++;
                sendPollsRow = rowCount++;
                embedLinksRow = rowCount++;
                sendReactionsRow = rowCount++;
            }

            addUsersRow = rowCount++;
            pinMessagesRow = rowCount++;
            editTagsRow = rowCount++;
            changeInfoRow = rowCount++;
            if (isForum) {
                manageTopicsRow = rowCount++;
            }
            untilSectionRow = rowCount++;
            untilDateRow = rowCount++;
        }
        permissionsEndRow = rowCount;

        if (canEdit) {
            if (!isChannel && (currentType == TYPE_ADMIN || currentType == TYPE_ADD_BOT && asAdmin || currentType == TYPE_BANNED)) {
                rightsShadowRow = rowCount++;
                rankRow = rowCount++;
                rankInfoRow = rowCount++;
            }
            if (currentChat != null && currentChat.creator && currentType == TYPE_ADMIN && hasAllAdminRights() && !currentUser.bot && !isCommunity) {
                if (rightsShadowRow == -1) {
                    transferOwnerShadowRow = rowCount++;
                }
                transferOwnerRow = rowCount++;
                if (rightsShadowRow != -1) {
                    transferOwnerShadowRow = rowCount++;
                }
            }
            if (initialIsSet) {
                if (rightsShadowRow == -1) {
                    rightsShadowRow = rowCount++;
                }
                removeAdminRow = rowCount++;
                removeAdminShadowRow = rowCount++;
            }
        } else {
            if (currentType == TYPE_ADMIN) {
                if (!isChannel && (!currentRank.isEmpty() || currentChat.creator && UserObject.isUserSelf(currentUser))) {
                    rightsShadowRow = rowCount++;
//                    rankHeaderRow = rowCount++;
                    rankRow = rowCount++;
                    if (currentChat.creator && UserObject.isUserSelf(currentUser)) {
                        rankInfoRow = rowCount++;
                    } else {
                        cantEditInfoRow = rowCount++;
                    }
                } else {
                    cantEditInfoRow = rowCount++;
                }
            } else {
                rightsShadowRow = rowCount++;
            }
        }
        if (currentType == TYPE_ADD_BOT) {
            addBotButtonRow = rowCount++;
        }

        if (update) {
            if (transferOwnerShadowRowPrev == -1 && transferOwnerShadowRow != -1) {
                listViewAdapter.notifyItemRangeInserted(Math.min(transferOwnerShadowRow, transferOwnerRow), 2);
            } else if (transferOwnerShadowRowPrev != -1 && transferOwnerShadowRow == -1) {
                listViewAdapter.notifyItemRangeRemoved(transferOwnerShadowRowPrev, 2);
            }
        }
    }

    private void onDonePressed() {
        onDonePressed(true);
    }

    private void onDonePressed(boolean ask) {
        if (loading) {
            return;
        }
        if (!ChatObject.isChannel(currentChat) && (currentType == TYPE_BANNED || currentType == TYPE_ADMIN && (!isDefaultAdminRights() || rankRow != -1 && currentRank.codePointCount(0, currentRank.length()) <= MAX_RANK_LENGTH) || currentType == TYPE_ADD_BOT && (currentRank != null || !isDefaultAdminRights()))) {
            MessagesController.getInstance(currentAccount).convertToMegaGroup(getParentActivity(), chatId, this, param -> {
                if (param != 0) {
                    chatId = param;
                    currentChat = MessagesController.getInstance(currentAccount).getChat(param);
                    onDonePressed();
                }
            });
            return;
        }

        if (ask) {
            if (isCommunity && !initialAsAdmin && adminRights.manage_linked_peers && adminRights.change_info) {
                AlertsCreator.showSimpleConfirmAlert(this,
                    getString(R.string.CommunityMakeAdminTitle),
                    AndroidUtilities.replaceTags(formatString(R.string.CommunityMakeAdminMessage, DialogObject.getShortName(currentUser))),
                    getString(R.string.CommunityMakeAdminPromote), false, () -> onDonePressed(false));
                return;
            }
        }

        if (rankRow != -1 && currentRank != null && currentRank.codePointCount(0, currentRank.length()) > MAX_RANK_LENGTH) {
            listView.smoothScrollToPosition(rankRow);
            Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(200);
            }
            RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(rankRow);
            if (holder != null) {
                AndroidUtilities.shakeView(holder.itemView);
            }
            return;
        }
        if (currentType == TYPE_ADMIN || currentType == TYPE_ADD_BOT) {
            if (isChannel) {
                adminRights.pin_messages = false;
                adminRights.manage_ranks = false;
            } else {
                adminRights.post_messages = adminRights.edit_messages = false;
            }
            if (!adminRights.change_info && !adminRights.post_messages && !adminRights.edit_messages && !adminRights.manage_direct_messages &&
                    !adminRights.delete_messages && !adminRights.ban_users && !adminRights.invite_users && (!isForum || !adminRights.manage_topics) &&
                    !adminRights.pin_messages && !adminRights.manage_ranks && !adminRights.add_admins && !adminRights.anonymous && !adminRights.manage_call && (!isChannel || !adminRights.post_stories && !adminRights.edit_stories && !adminRights.delete_stories)) {
                adminRights.other = true;
            } else {
                adminRights.other = false;
            }
        }
        boolean finishFragment = true;
        if (currentType == TYPE_ADMIN) {
            finishFragment = delegate == null;
            setLoading(true);
            MessagesController.getInstance(currentAccount).setUserAdminRole(chatId, currentUser, adminRights, currentRank, isChannel, this, isAddingNew && !isCommunity, false, null, () -> {
                if (hasGuardBotToSet) {
                    setGuardBotImpl(guardBotIdToSet);
                }

                if (delegate != null) {
                    delegate.didSetRights(
                            adminRights.change_info || adminRights.post_messages || adminRights.manage_direct_messages || adminRights.edit_messages ||
                                    adminRights.delete_messages || adminRights.ban_users || adminRights.invite_users || (isForum && adminRights.manage_topics) ||
                                    adminRights.pin_messages || adminRights.manage_ranks || adminRights.add_admins || adminRights.anonymous || adminRights.manage_call ||
                                    isChannel && (adminRights.post_stories || adminRights.edit_stories || adminRights.delete_stories) ||
                                    adminRights.other ? 1 : 0, adminRights, bannedRights, currentRank);
                    finishFragment();
                }
            }, err -> {
                setLoading(false);
                if (err != null && "USER_PRIVACY_RESTRICTED".equals(err.text)) {
                    if (!ChatObject.isChannel(currentChat)) {
                        LimitReachedBottomSheet restrictedUsersBottomSheet = new LimitReachedBottomSheet(ChatRightsEditActivity.this, getParentActivity(), LimitReachedBottomSheet.TYPE_ADD_MEMBERS_RESTRICTED, currentAccount, getResourceProvider());
                        ArrayList<TLRPC.User> arrayList = new ArrayList<>();
                        arrayList.add(currentUser);
                        restrictedUsersBottomSheet.setRestrictedUsers(currentChat, arrayList, null, null, null);
                        restrictedUsersBottomSheet.show();
                    }
                    return false;
                }
                return true;
            });
        } else if (currentType == TYPE_BANNED) {
            if (rankRow >= 0) {
                final TLRPC.TL_messages_editChatParticipantRank req = new TLRPC.TL_messages_editChatParticipantRank();
                req.peer = MessagesController.getInputPeer(currentChat);
                req.participant = MessagesController.getInputPeer(currentUser);
                req.rank = currentRank == null ? "" : currentRank;
                ConnectionsManager.getInstance(currentAccount).sendRequest(req, null);
            }
            MessagesController.getInstance(currentAccount).setParticipantBannedRole(chatId, currentUser, null, bannedRights, isChannel, getFragmentForAlert(1));
            int rights;
            if (bannedRights.send_messages || bannedRights.send_stickers || bannedRights.embed_links || bannedRights.send_media ||
                    bannedRights.send_gifs || bannedRights.send_games || bannedRights.send_inline) {
                rights = 1;
            } else {
                bannedRights.until_date = 0;
                rights = 2;
            }
            if (delegate != null) {
                delegate.didSetRights(rights, adminRights, bannedRights, currentRank);
            }
        } else if (currentType == TYPE_ADD_BOT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(asAdmin ?
                    LocaleController.getString(R.string.AddBotAdmin) :
                    LocaleController.getString(R.string.AddBot)
            );
            boolean isChannel = ChatObject.isChannel(currentChat) && !currentChat.megagroup;
            String chatName = currentChat == null ? "" : currentChat.title;
            builder.setMessage(AndroidUtilities.replaceTags(
                    asAdmin ? (
                            isChannel ?
                                    formatString(R.string.AddBotMessageAdminChannel, chatName) :
                                    formatString(R.string.AddBotMessageAdminGroup, chatName)
                    ) : formatString(R.string.AddMembersAlertNamesText, UserObject.getUserName(currentUser), chatName)
            ));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setPositiveButton(asAdmin ? LocaleController.getString(R.string.AddAsAdmin) : LocaleController.getString(R.string.AddBot), (di, i) -> {
                setLoading(true);
                Runnable onFinish = () -> {
                    if (delegate != null) {
                        delegate.didSetRights(0, asAdmin ? adminRights : null, null, currentRank);
                    }
                    closingKeyboardAfterFinish = true;

                    Bundle args1 = new Bundle();
                    args1.putBoolean("scrollToTopOnResume", true);
                    args1.putLong("chat_id", currentChat.id);
                    if (!getMessagesController().checkCanOpenChat(args1, this)) {
                        setLoading(false);
                        return;
                    }
                    ChatActivity chatActivity = new ChatActivity(args1);
                    presentFragment(chatActivity, true);
                    if (BulletinFactory.canShowBulletin(chatActivity)) {
                        if (isAddingNew && asAdmin) {
                            BulletinFactory.createAddedAsAdminBulletin(chatActivity, currentUser.first_name).show();
                        } else if (!isAddingNew && !initialAsAdmin && asAdmin) {
                            BulletinFactory.createPromoteToAdminBulletin(chatActivity, currentUser.first_name).show();
                        }
                    }
                };
                if (asAdmin || initialAsAdmin) {
                    getMessagesController().setUserAdminRole(currentChat.id, currentUser, asAdmin ? adminRights : emptyAdminRights(false), currentRank, false, this, isAddingNew, asAdmin, botHash, onFinish, err -> {
                        setLoading(false);
                        return true;
                    });
                } else {
                    getMessagesController().addUserToChat(currentChat.id, currentUser, 0, botHash, this, true, onFinish, err -> {
                        setLoading(false);
                        return true;
                    });
                }
            });
            showDialog(builder.create());
            finishFragment = false;
        }
        if (finishFragment) {
            finishFragment();
        }
    }

    private ValueAnimator doneDrawableAnimator;

    public void setLoading(boolean newLoading) {
        if (doneDrawableAnimator != null) {
            doneDrawableAnimator.cancel();
        }
        loading = newLoading;
        actionBar.getBackButton().setEnabled(!loading);
        if (doneDrawable != null) {
            doneDrawableAnimator = ValueAnimator.ofFloat(doneDrawable.getProgress(), loading ? 1f : 0f);
            doneDrawableAnimator.addUpdateListener(a -> {
                doneDrawable.setProgress((float) a.getAnimatedValue());
                doneDrawable.invalidateSelf();
            });
            doneDrawableAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    doneDrawable.setProgress(loading ? 1 : 0);
                    doneDrawable.invalidateSelf();
                }
            });
            doneDrawableAnimator.setDuration((long) (150 * Math.abs(doneDrawable.getProgress() - (loading ? 1 : 0))));
            doneDrawableAnimator.start();
        }
    }

    public void setDelegate(ChatRightsEditActivityDelegate channelRightsEditActivityDelegate) {
        delegate = channelRightsEditActivityDelegate;
    }

    private boolean checkDiscard(boolean invoked) {
        if (currentType == TYPE_ADD_BOT) {
            return true;
        }
        boolean changed;
        if (currentType == TYPE_BANNED) {
            String newBannedRights = ChatObject.getBannedRightsString(bannedRights);
            changed = !currentBannedRights.equals(newBannedRights);
        } else {
            changed = !initialRank.equals(currentRank);
        }
        if (changed) {
            if (invoked) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.UserRestrictionsApplyChanges));
                final TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(chatId);
                builder.setMessage(AndroidUtilities.replaceTags(formatString("UserRestrictionsApplyChangesText", R.string.UserRestrictionsApplyChangesText, chat.title)));
                builder.setPositiveButton(LocaleController.getString(R.string.ApplyTheme), (dialogInterface, i) -> onDonePressed());
                builder.setNegativeButton(LocaleController.getString(R.string.PassportDiscard), (dialog, which) -> finishFragment());
                showDialog(builder.create());
            }
            return false;
        }
        return true;
    }

    private final static int MAX_RANK_LENGTH = 16;

    private void setTextLeft(View cell) {
        if (cell instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) cell;
            int left = MAX_RANK_LENGTH - (currentRank != null ? currentRank.codePointCount(0, currentRank.length()) : 0);
            if (left <= MAX_RANK_LENGTH - MAX_RANK_LENGTH * 0.7f) {
                headerCell.setText2(String.format("%d", left));
                SimpleTextView textView = headerCell.getTextView2();
                int key = left < 0 ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteGrayText3;
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
            } else {
                headerCell.setText2("");
            }
        }
    }

    @Override
    public boolean onBackPressed(boolean invoked) {
        return checkDiscard(invoked);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final int VIEW_TYPE_USER_CELL = 0;
        private final int VIEW_TYPE_INFO_CELL = 1;
        private final int VIEW_TYPE_TRANSFER_CELL = 2;
        private final int VIEW_TYPE_HEADER_CELL = 3;
        private final int VIEW_TYPE_SWITCH_CELL = 4;
        private final int VIEW_TYPE_SHADOW_CELL = 5;
        private final int VIEW_TYPE_UNTIL_DATE_CELL = 6;
        private final int VIEW_TYPE_RANK_CELL = 7;
        private final int VIEW_TYPE_ADD_BOT_CELL = 8;
        private final int VIEW_TYPE_EXPANDABLE_SWITCH = 9;
        private final int VIEW_TYPE_INNER_CHECK = 10;
        private final int VIEW_TYPE_TAG_CELL = 11;

        private Context mContext;
        private boolean ignoreTextChange;

        public ListAdapter(Context context) {
            if (currentType == TYPE_ADD_BOT) {
                setHasStableIds(true);
            }
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            if (currentType == TYPE_ADD_BOT) {
                if (position == manageRow) return 1;
                if (position == changeInfoRow) return 2;
                if (position == postMessagesRow) return 3;
                if (position == editMesagesRow) return 4;
                if (position == deleteMessagesRow) return 5;
                if (position == addAdminsRow) return 6;
                if (position == anonymousRow) return 7;
                if (position == banUsersRow) return 8;
                if (position == addUsersRow) return 9;
                if (position == pinMessagesRow) return 10;
                if (position == rightsShadowRow) return 11;
                if (position == removeAdminRow) return 12;
                if (position == removeAdminShadowRow) return 13;
                if (position == cantEditInfoRow) return 14;
                if (position == transferOwnerShadowRow) return 15;
                if (position == transferOwnerRow) return 16;
                if (position == rankHeaderRow) return 17;
                if (position == rankRow) return 18;
                if (position == rankInfoRow) return 19;
                if (position == sendMessagesRow) return 20;
                if (position == sendPhotosRow) return 21;
                if (position == sendStickersRow) return 22;
                if (position == sendPollsRow) return 23;
                if (position == embedLinksRow) return 24;
                if (position == startVoiceChatRow) return 25;
                if (position == untilSectionRow) return 26;
                if (position == untilDateRow) return 27;
                if (position == addBotButtonRow) return 28;
                if (position == manageTopicsRow) return 29;
                if (position == sendVideosRow) return 30;
                if (position == sendFilesRow) return 31;
                if (position == sendMusicRow) return 32;
                if (position == sendVoiceRow) return 33;
                if (position == sendRoundRow) return 34;
                if (position == sendMediaRow) return 35;
                if (position == channelMessagesRow) return 36;
                if (position == channelPostMessagesRow) return 37;
                if (position == channelEditMessagesRow) return 38;
                if (position == channelDeleteMessagesRow) return 39;
                if (position == channelStoriesRow) return 40;
                if (position == channelPostStoriesRow) return 41;
                if (position == channelEditStoriesRow) return 42;
                if (position == channelDeleteStoriesRow) return 43;
                if (position == manageDirectRow) return 44;
                if (position == editTagsRow) return 45;
                if (position == sendReactionsRow) return 46;
                if (position == guardBotRow) return 47;
                if (position == guardBotInfoRow) return 48;
                if (position == manageLinkedPeersRow) return 49;
                return 0;
            } else {
                return super.getItemId(position);
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (currentChat.creator && (currentType == TYPE_ADMIN || currentType == TYPE_ADD_BOT && asAdmin) && type == VIEW_TYPE_SWITCH_CELL && holder.getAdapterPosition() == anonymousRow) {
                return true;
            }
            if (!canEdit) {
                return false;
            }
            if ((currentType == TYPE_ADMIN || currentType == TYPE_ADD_BOT) && type == VIEW_TYPE_SWITCH_CELL) {
                int position = holder.getAdapterPosition();
                if (position == manageRow) {
                    return myAdminRights.add_admins || (currentChat != null && currentChat.creator);
                } else {
                    if (currentType == TYPE_ADD_BOT && !asAdmin) {
                        return false;
                    }
                    if (position == changeInfoRow) {
                        return myAdminRights.change_info && (defaultBannedRights == null || defaultBannedRights.change_info || isChannel);
                    } else if (position == postMessagesRow) {
                        return myAdminRights.post_messages;
                    } else if (position == manageDirectRow) {
                        return myAdminRights.manage_direct_messages;
                    } else if (position == editMesagesRow) {
                        return myAdminRights.edit_messages;
                    } else if (position == deleteMessagesRow) {
                        return myAdminRights.delete_messages;
                    } else if (position == startVoiceChatRow) {
                        return myAdminRights.manage_call;
                    } else if (position == addAdminsRow) {
                        return myAdminRights.add_admins;
                    } else if (position == anonymousRow) {
                        return myAdminRights.anonymous;
                    } else if (position == banUsersRow) {
                        return myAdminRights.ban_users;
                    } else if (position == addUsersRow) {
                        return myAdminRights.invite_users;
                    } else if (position == pinMessagesRow) {
                        return myAdminRights.pin_messages && (defaultBannedRights == null || defaultBannedRights.pin_messages);
                    } else if (position == editTagsRow) {
                        return myAdminRights.manage_ranks;
                    } else if (position == manageTopicsRow) {
                        return myAdminRights.manage_topics;
                    } else if (position == channelPostStoriesRow) {
                        return myAdminRights.post_stories;
                    } else if (position == channelEditStoriesRow) {
                        return myAdminRights.edit_stories;
                    } else if (position == channelDeleteStoriesRow) {
                        return myAdminRights.delete_stories;
                    } else if (position == manageLinkedPeersRow) {
                        return myAdminRights.manage_linked_peers;
                    }
                }
            }
            return type != VIEW_TYPE_HEADER_CELL && type != VIEW_TYPE_INFO_CELL && type != VIEW_TYPE_SHADOW_CELL && type != VIEW_TYPE_ADD_BOT_CELL && type != VIEW_TYPE_TAG_CELL;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_TYPE_USER_CELL:
                    view = new UserCell2(mContext, 4, 0);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_INFO_CELL:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
                case VIEW_TYPE_TRANSFER_CELL:
                default:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_HEADER_CELL:
                    view = new HeaderCell(mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, true);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_EXPANDABLE_SWITCH:
                case VIEW_TYPE_SWITCH_CELL:
                    view = new TextCheckCell2(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_SHADOW_CELL:
                    view = new ShadowSectionCell(mContext);
                    break;
                case VIEW_TYPE_UNTIL_DATE_CELL:
                    view = new TextDetailCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_ADD_BOT_CELL:
                    addBotButtonContainer = new FrameLayout(mContext);
                    addBotButtonContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    addBotButton = new FrameLayout(mContext);
                    addBotButtonText = new AnimatedTextView(mContext, true, false, false);
                    addBotButtonText.setTypeface(AndroidUtilities.bold());
                    addBotButtonText.setTextColor(0xffffffff);
                    addBotButtonText.setTextSize(AndroidUtilities.dp(14));
                    addBotButtonText.setGravity(Gravity.CENTER);
                    addBotButtonText.setText(LocaleController.getString(R.string.AddBotButton) + " " + (asAdmin ? LocaleController.getString(R.string.AddBotButtonAsAdmin) : LocaleController.getString(R.string.AddBotButtonAsMember)));
                    addBotButton.addView(addBotButtonText, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
                    addBotButton.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4));
                    addBotButton.setOnClickListener(e -> onDonePressed());
                    addBotButtonContainer.addView(addBotButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.FILL, 14, 28, 14, 14));
                    addBotButtonContainer.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    View bg = new View(mContext);
                    bg.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    addBotButtonContainer.setClipChildren(false);
                    addBotButtonContainer.setClipToPadding(false);
                    addBotButtonContainer.addView(bg, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 800, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0, 0, -800));
                    view = addBotButtonContainer;
                    break;
                case VIEW_TYPE_RANK_CELL:
                    PollEditTextCell cell = rankEditTextCell = new PollEditTextCell(mContext, null);
                    cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    cell.addTextWatcher(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (ignoreTextChange) {
                                return;
                            }
                            currentRank = s.toString();
                            RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(rankHeaderRow);
                            if (holder != null) {
                                setTextLeft(holder.itemView);
                            }
                        }
                    });
                    view = cell;
                    break;
                case VIEW_TYPE_TAG_CELL:
                    final TagEditCell cell2 = new TagEditCell(mContext, currentAccount, -chatId, resourceProvider);
                    view = cell2;
                    break;
                case VIEW_TYPE_INNER_CHECK:
                    CheckBoxCell checkBoxCell = new CheckBoxCell(mContext, 4, 21, getResourceProvider());
                    checkBoxCell.setPad(1);
                    checkBoxCell.getCheckBoxRound().setDrawBackgroundAsArc(14);
                    checkBoxCell.getCheckBoxRound().setColor(Theme.key_switch2TrackChecked, Theme.key_radioBackground, Theme.key_checkboxCheck);
                    checkBoxCell.setEnabled(true);
                    view = checkBoxCell;
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_INNER_CHECK:
                    CheckBoxCell checkBoxCell = (CheckBoxCell) holder.itemView;
                    boolean animated = checkBoxCell.getTag() != null && (Integer) checkBoxCell.getTag() == position;
                    checkBoxCell.setTag(position);
                    if (position == sendStickersRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPermissionStickersGifs), "", !bannedRights.send_stickers && !defaultBannedRights.send_stickers, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_stickers ? R.drawable.permission_locked : 0);
                    } else if (position == embedLinksRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.UserRestrictionsEmbedLinks), "", !bannedRights.embed_links && !defaultBannedRights.embed_links && !bannedRights.send_plain && !defaultBannedRights.send_plain, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.embed_links ? R.drawable.permission_locked : 0);
                    } else if (position == sendPollsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPolls), "", !bannedRights.send_polls && !defaultBannedRights.send_polls, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_polls ? R.drawable.permission_locked : 0);
                    } else if (position == sendPhotosRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPermissionPhotos), "", !bannedRights.send_photos && !defaultBannedRights.send_photos, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_photos ? R.drawable.permission_locked : 0);
                    } else if (position == sendVideosRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPermissionVideos), "", !bannedRights.send_videos && !defaultBannedRights.send_videos, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_videos ? R.drawable.permission_locked : 0);
                    } else if (position == sendReactionsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.UserRestrictionsSendReactions), "", !bannedRights.send_reactions && !defaultBannedRights.send_reactions, true);
                        checkBoxCell.setIcon(defaultBannedRights.send_reactions ? R.drawable.permission_locked : 0);
                    } else if (position == sendMusicRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPermissionMusic), "", !bannedRights.send_audios && !defaultBannedRights.send_audios, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_audios ? R.drawable.permission_locked : 0);
                    } else if (position == sendFilesRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPermissionFiles), "", !bannedRights.send_docs && !defaultBannedRights.send_docs, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_docs ? R.drawable.permission_locked : 0);
                    } else if (position == sendVoiceRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPermissionVoice), "", !bannedRights.send_voices && !defaultBannedRights.send_voices, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_voices ? R.drawable.permission_locked : 0);
                    } else if (position == sendRoundRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendMediaPermissionRound), "", !bannedRights.send_roundvideos && !defaultBannedRights.send_roundvideos, true, animated);
                        checkBoxCell.setIcon(defaultBannedRights.send_roundvideos ? R.drawable.permission_locked : 0);
                    } else if (position == channelPostMessagesRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.EditAdminPostMessages), "", adminRights.post_messages, true, animated);
                    } else if (position == channelEditMessagesRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.EditAdminEditMessages), "", adminRights.edit_messages, true, animated);
                    } else if (position == channelDeleteMessagesRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.EditAdminDeleteMessages), "", adminRights.delete_messages, true, animated);
                    } else if (position == channelPostStoriesRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.EditAdminPostStories), "", adminRights.post_stories, true, animated);
                    } else if (position == channelEditStoriesRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.EditAdminEditStories), "", adminRights.edit_stories, true, animated);
                    } else if (position == channelDeleteStoriesRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.EditAdminDeleteStories), "", adminRights.delete_stories, true, animated);
                    }
                    break;
                case VIEW_TYPE_USER_CELL:
                    UserCell2 userCell2 = (UserCell2) holder.itemView;
                    String status = null;
                    if (currentType == TYPE_ADD_BOT) {
                        status = LocaleController.getString(R.string.Bot);
                    }
                    userCell2.setData(currentUser, null, status, 0);
                    break;
                case VIEW_TYPE_INFO_CELL:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == guardBotInfoRow) {
                        privacyCell.setText(LocaleController.getString(R.string.EditAdminProcessJoinRequestsInfo));
                    } else if (position == cantEditInfoRow) {
                        privacyCell.setText(LocaleController.getString(R.string.EditAdminCantEdit));
                    } else if (position == rankInfoRow) {
                        String hint;
                        if (UserObject.isUserSelf(currentUser) && currentChat.creator) {
                            hint = LocaleController.getString(R.string.ChannelCreator);
                        } else {
                            hint = LocaleController.getString(R.string.ChannelAdmin);
                        }
                        privacyCell.setText(
                            currentType == TYPE_ADMIN ?
                                formatString(R.string.EditAdminRankInfo, hint) :
                                formatString(R.string.EditMemberRankInfo, UserObject.getUserName(currentUser))
                        );
                    }
                    break;
                case VIEW_TYPE_TRANSFER_CELL:
                    TextSettingsCell actionCell = (TextSettingsCell) holder.itemView;
                    if (position == removeAdminRow) {
                        actionCell.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                        actionCell.setTag(Theme.key_text_RedRegular);
                        if (currentType == TYPE_ADMIN) {
                            actionCell.setText(LocaleController.getString(R.string.EditAdminRemoveAdmin), false);
                        } else if (currentType == TYPE_BANNED) {
                            actionCell.setText(LocaleController.getString(R.string.UserRestrictionsBlock), false);
                        }
                    } else if (position == transferOwnerRow) {
                        actionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        actionCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        if (isChannel) {
                            actionCell.setText(LocaleController.getString(R.string.EditAdminChannelTransfer), false);
                        } else {
                            actionCell.setText(LocaleController.getString(R.string.EditAdminGroupTransfer), false);
                        }
                    }
                    break;
                case VIEW_TYPE_HEADER_CELL:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == 2) {
                        if (currentType == TYPE_ADD_BOT || (currentUser != null && currentUser.bot)) {
                            headerCell.setText(LocaleController.getString(R.string.BotRestrictionsCanDo));
                        } else if (currentType == TYPE_ADMIN) {
                            headerCell.setText(LocaleController.getString(R.string.EditAdminWhatCanDo));
                        } else if (currentType == TYPE_BANNED) {
                            headerCell.setText(LocaleController.getString(R.string.UserRestrictionsCanDo));
                        }
                    } else if (position == rankHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.EditAdminRank));
                    }
                    break;
                case VIEW_TYPE_EXPANDABLE_SWITCH:
                case VIEW_TYPE_SWITCH_CELL:
                    TextCheckCell2 checkCell = (TextCheckCell2) holder.itemView;
                    boolean asAdminValue = currentType != TYPE_ADD_BOT || asAdmin;
                    boolean isCreator = (currentChat != null && currentChat.creator);
                    if (position == sendMediaRow) {
                        int sentMediaCount = getSendMediaSelectedCount();
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.UserRestrictionsSendMedia), sentMediaCount > 0, true, true);
                        checkCell.setCollapseArrow(String.format(Locale.US, "%d/10", sentMediaCount), !sendMediaExpanded, () -> {
                            if (!checkCell.isEnabled()) return;
                            if (allDefaultMediaBanned()) {
                                new AlertDialog.Builder(getParentActivity())
                                        .setTitle(LocaleController.getString(R.string.UserRestrictionsCantModify))
                                        .setMessage(LocaleController.getString(R.string.UserRestrictionsCantModifyEnabled))
                                        .setPositiveButton(LocaleController.getString(R.string.OK), null)
                                        .create()
                                        .show();
                                return;
                            }
                            boolean checked = !checkCell.isChecked();
                            checkCell.setChecked(checked);
                            setSendMediaEnabled(checked);
                        });
                        checkCell.setIcon(allDefaultMediaBanned() ? R.drawable.permission_locked : 0);
                    } else if (position == channelMessagesRow) {
                        int count = getChannelMessagesSelectedCount();
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.ChannelManageMessages), count > 0, true, true);
                        checkCell.setCollapseArrow(String.format(Locale.US, "%d/3", count), !channelMessagesExpanded, () -> {
                            if (!checkCell.isEnabled()) return;
                            boolean checked = checkCell.isChecked();
                            checkCell.setChecked(checked);
                            setChannelMessagesEnabled(checked);
                        });
                    } else if (position == channelStoriesRow) {
                        int count = getChannelStoriesSelectedCount();
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.ChannelManageStories), count > 0, true, true);
                        checkCell.setCollapseArrow(String.format(Locale.US, "%d/3", count), !channelStoriesExpanded, () -> {
                            if (!checkCell.isEnabled()) return;
                            boolean checked = checkCell.isChecked();
                            checkCell.setChecked(checked);
                            setChannelStoriesEnabled(checked);
                        });
                    } else if (position == manageRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.ManageGroup), asAdmin, true);
                        checkCell.setIcon(myAdminRights.add_admins || isCreator ? 0 : R.drawable.permission_locked);
                    } else if (position == changeInfoRow) {
                        if (currentType == TYPE_ADMIN || currentType == TYPE_ADD_BOT) {
                            if (isCommunity) {
                                checkCell.setTextAndCheck(LocaleController.getString(R.string.CommunityAdminRightEditCommunityName), asAdminValue && adminRights.change_info, true);
                            } else if (isChannel) {
                                checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminChangeChannelInfo), asAdminValue && adminRights.change_info, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminChangeGroupInfo), asAdminValue && adminRights.change_info || !defaultBannedRights.change_info, true);
                            }
                            if (currentType == TYPE_ADD_BOT) {
                                checkCell.setIcon(myAdminRights.change_info || isCreator ? 0 : R.drawable.permission_locked);
                            }
                        } else if (currentType == TYPE_BANNED) {
                            checkCell.setTextAndCheck(LocaleController.getString(isCommunity ? R.string.CommunityAdminRightEditCommunityName : R.string.UserRestrictionsChangeInfo), !bannedRights.change_info && !defaultBannedRights.change_info, manageTopicsRow != -1);
                            checkCell.setIcon(defaultBannedRights.change_info ? R.drawable.permission_locked : 0);
                        }
                    } else if (position == postMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminPostMessages), asAdminValue && adminRights.post_messages, true);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.post_messages || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == manageDirectRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminManageDirect), asAdminValue && adminRights.manage_direct_messages, true);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.manage_direct_messages || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == editMesagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminEditMessages), asAdminValue && adminRights.edit_messages, true);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.edit_messages || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == deleteMessagesRow) {
                        if (isChannel) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminDeleteMessages), asAdminValue && adminRights.delete_messages, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminGroupDeleteMessages), asAdminValue && adminRights.delete_messages, true);
                        }
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.delete_messages || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == addAdminsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminAddAdmins), asAdminValue && adminRights.add_admins, banUsersRow != -1 && isChannel || anonymousRow != -1);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.add_admins || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == anonymousRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminSendAnonymously), asAdminValue && adminRights.anonymous, manageTopicsRow != -1 || currentUserIsBotGuard);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.anonymous || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == guardBotRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminProcessJoinRequests), chatInfo != null && currentUser != null && (hasGuardBotToSet ? guardBotIdToSet : chatInfo.guard_bot_id) == currentUser.id, false);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(0);
                        }
                    } else if (position == banUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(isCommunity ? R.string.CommunityAdminRightBanMembers : R.string.EditAdminBanUsers), asAdminValue && adminRights.ban_users, !isCommunity);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.ban_users || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == manageLinkedPeersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.CommunityAdminRightEditGroupList), asAdminValue && adminRights.manage_linked_peers, true);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.manage_linked_peers || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == startVoiceChatRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.StartVoipChatPermission), asAdminValue && adminRights.manage_call, true);
                        if (currentType == TYPE_ADD_BOT) {
                            checkCell.setIcon(myAdminRights.manage_call || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == manageTopicsRow) {
                        if (currentType == TYPE_ADMIN) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.ManageTopicsPermission), asAdminValue && adminRights.manage_topics, currentUserIsBotGuard);
                        } else if (currentType == TYPE_BANNED) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.CreateTopicsPermission), !bannedRights.manage_topics && !defaultBannedRights.manage_topics, currentUserIsBotGuard);
                            checkCell.setIcon(defaultBannedRights.manage_topics ? R.drawable.permission_locked : 0);
                        } else if (currentType == TYPE_ADD_BOT) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.ManageTopicsPermission), asAdminValue && adminRights.manage_topics, currentUserIsBotGuard);
                            checkCell.setIcon(myAdminRights.manage_topics || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == addUsersRow) {
                        if (currentType == TYPE_ADMIN) {
                            if (ChatObject.isActionBannedByDefault(currentChat, ChatObject.ACTION_INVITE)) {
                                checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminAddUsers), adminRights.invite_users, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminAddUsersViaLink), adminRights.invite_users, true);
                            }
                        } else if (currentType == TYPE_BANNED) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.UserRestrictionsInviteUsers), !bannedRights.invite_users && !defaultBannedRights.invite_users, true);
                            checkCell.setIcon(defaultBannedRights.invite_users ? R.drawable.permission_locked : 0);
                        } else if (currentType == TYPE_ADD_BOT) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminAddUsersViaLink), asAdminValue && adminRights.invite_users, true);
                            checkCell.setIcon(myAdminRights.invite_users || isCreator ? 0 : R.drawable.permission_locked);
                        }
                    } else if (position == pinMessagesRow) {
                        if (currentType == TYPE_ADMIN || currentType == TYPE_ADD_BOT) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminPinMessages), asAdminValue && adminRights.pin_messages || !defaultBannedRights.pin_messages, true);
                            if (currentType == TYPE_ADD_BOT) {
                                checkCell.setIcon(myAdminRights.pin_messages || isCreator ? 0 : R.drawable.permission_locked);
                            }
                        } else if (currentType == TYPE_BANNED) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.UserRestrictionsPinMessages), !bannedRights.pin_messages && !defaultBannedRights.pin_messages, true);
                            checkCell.setIcon(defaultBannedRights.pin_messages ? R.drawable.permission_locked : 0);
                        }
                    } else if (position == editTagsRow) {
                        if (currentType == TYPE_ADMIN || currentType == TYPE_ADD_BOT) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.EditAdminEditTags), asAdminValue && adminRights.manage_ranks, true);
                            if (currentType == TYPE_ADD_BOT) {
                                checkCell.setIcon(myAdminRights.manage_ranks || isCreator ? 0 : R.drawable.permission_locked);
                            }
                        } else if (currentType == TYPE_BANNED) {
                            checkCell.setTextAndCheck(LocaleController.getString(R.string.UserRestrictionsEditTags), !bannedRights.edit_rank && !defaultBannedRights.edit_rank, true);
                            checkCell.setIcon(defaultBannedRights.edit_rank ? R.drawable.permission_locked : 0);
                        }
                    } else if (position == sendMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.UserRestrictionsSend), !bannedRights.send_plain && !defaultBannedRights.send_plain, true);
                        checkCell.setIcon(defaultBannedRights.send_plain ? R.drawable.permission_locked : 0);
                    }

                    if (currentType == TYPE_ADD_BOT) {
//                        checkCell.setEnabled((asAdmin || position == manageRow) && !checkCell.hasIcon(), false);
                    } else {
//                        if (position == sendMediaRow || position == sendStickersRow || position == embedLinksRow || position == sendPollsRow) {
//                            checkCell.setEnabled(!bannedRights.send_messages && !bannedRights.view_messages && !defaultBannedRights.send_messages && !defaultBannedRights.view_messages);
//                        } else
                        if (position == sendMessagesRow) {
                            checkCell.setEnabled(!bannedRights.view_messages && !defaultBannedRights.view_messages);
                        }
                    }
                    break;
                case VIEW_TYPE_SHADOW_CELL:
                    ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
                    if (currentType == TYPE_ADD_BOT && (position == rightsShadowRow || position == rankInfoRow)) {
                        shadowCell.setAlpha(asAdminT);
                    } else {
                        shadowCell.setAlpha(1);
                    }
                    break;
                case VIEW_TYPE_UNTIL_DATE_CELL:
                    TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                    if (position == untilDateRow) {
                        String value;
                        if (bannedRights.until_date == 0 || Math.abs(bannedRights.until_date - System.currentTimeMillis() / 1000) > 10 * 365 * 24 * 60 * 60) {
                            value = LocaleController.getString(R.string.UserRestrictionsUntilForever);
                        } else {
                            value = LocaleController.formatDateForBan(bannedRights.until_date);
                        }
                        detailCell.setTextAndValue(LocaleController.getString(R.string.UserRestrictionsDuration), value, false);
                    }
                    break;
                case VIEW_TYPE_RANK_CELL:
                    PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                    String hint;
                    if (UserObject.isUserSelf(currentUser) && currentChat.creator) {
                        hint = LocaleController.getString(R.string.ChannelCreator);
                    } else {
                        hint = LocaleController.getString(R.string.ChannelAdmin);
                    }
                    ignoreTextChange = true;
                    textCell.getTextView().setEnabled(canEdit || currentChat.creator);
                    textCell.getTextView().setSingleLine(true);
                    textCell.getTextView().setImeOptions(EditorInfo.IME_ACTION_DONE);
                    textCell.setTextAndHint(currentRank, hint, false);
                    ignoreTextChange = false;
                    break;
                case VIEW_TYPE_TAG_CELL:
                    final TagEditCell cell = (TagEditCell) holder.itemView;
                    cell.set(currentUser, currentRank, currentType == TYPE_ADMIN, false, rank -> {
                        currentRank = rank;
                    });
                    break;
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == rankHeaderRow) {
                setTextLeft(holder.itemView);
            }
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == rankRow && getParentActivity() != null) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isExpandableSendMediaRow(position)) {
                return VIEW_TYPE_INNER_CHECK;
            } else if (position == sendMediaRow || position == channelMessagesRow || position == channelStoriesRow) {
                return VIEW_TYPE_EXPANDABLE_SWITCH;
            } else if (position == 0) {
                return VIEW_TYPE_USER_CELL;
            } else if (position == 1 || position == rightsShadowRow || position == removeAdminShadowRow || position == untilSectionRow || position == transferOwnerShadowRow) {
                return VIEW_TYPE_SHADOW_CELL;
            } else if (position == 2 || position == rankHeaderRow) {
                return VIEW_TYPE_HEADER_CELL;
            } else if (position == changeInfoRow || position == postMessagesRow
                    || position == manageDirectRow || position == editMesagesRow
                    || position == deleteMessagesRow || position == addAdminsRow
                    || position == banUsersRow || position == addUsersRow
                    || position == pinMessagesRow || position == editTagsRow
                    || position == sendMessagesRow || position == anonymousRow
                    || position == startVoiceChatRow || position == manageRow
                    || position == manageTopicsRow || position == guardBotRow
                    || position == manageLinkedPeersRow
            ) {
                return VIEW_TYPE_SWITCH_CELL;
            } else if (position == cantEditInfoRow || position == rankInfoRow || position == guardBotInfoRow) {
                return VIEW_TYPE_INFO_CELL;
            } else if (position == untilDateRow) {
                return VIEW_TYPE_UNTIL_DATE_CELL;
            } else if (position == rankRow) {
                return VIEW_TYPE_TAG_CELL;
            } else if (position == addBotButtonRow) {
                return VIEW_TYPE_ADD_BOT_CELL;
            } else {
                return VIEW_TYPE_TRANSFER_CELL;
            }
        }
    }

    private void setSendMediaEnabled(boolean enabled) {
        bannedRights.send_media = !enabled;
        bannedRights.send_photos = !enabled;
        bannedRights.send_videos = !enabled;
        bannedRights.send_stickers = !enabled;
        bannedRights.send_gifs = !enabled;
        bannedRights.send_games = !enabled;
        bannedRights.send_inline = !enabled;
        bannedRights.send_audios = !enabled;
        bannedRights.send_docs = !enabled;
        bannedRights.send_voices = !enabled;
        bannedRights.send_roundvideos = !enabled;
        bannedRights.embed_links = !enabled;
        bannedRights.send_polls = !enabled;
        bannedRights.send_reactions = !enabled;
        AndroidUtilities.updateVisibleRows(listView);
    }

    private int getSendMediaSelectedCount() {
        int i = 0;
        if (!bannedRights.send_photos && !defaultBannedRights.send_photos) {
            i++;
        }
        if (!bannedRights.send_videos && !defaultBannedRights.send_videos) {
            i++;
        }
        if (!bannedRights.send_stickers && !defaultBannedRights.send_stickers) {
            i++;
        }
        if (!bannedRights.send_audios && !defaultBannedRights.send_audios) {
            i++;
        }
        if (!bannedRights.send_docs && !defaultBannedRights.send_docs) {
            i++;
        }
        if (!bannedRights.send_voices && !defaultBannedRights.send_voices) {
            i++;
        }
        if (!bannedRights.send_roundvideos && !defaultBannedRights.send_roundvideos) {
            i++;
        }
        if (!bannedRights.embed_links && !defaultBannedRights.embed_links && !bannedRights.send_plain && !defaultBannedRights.send_plain) {
            i++;
        }
        if (!bannedRights.send_polls && !defaultBannedRights.send_polls) {
            i++;
        }
        if (!bannedRights.send_reactions && !defaultBannedRights.send_reactions) {
            i++;
        }
        return i;
    }

    private int getChannelMessagesSelectedCount() {
        int i = 0;
        if (adminRights.post_messages) {
            i++;
        }
        if (adminRights.edit_messages) {
            i++;
        }
        if (adminRights.delete_messages) {
            i++;
        }
        return i;
    }

    private void setChannelMessagesEnabled(boolean enabled) {
        adminRights.post_messages = !enabled;
        adminRights.edit_messages = !enabled;
        adminRights.delete_messages = !enabled;
        AndroidUtilities.updateVisibleRows(listView);
    }

    private int getChannelStoriesSelectedCount() {
        int i = 0;
        if (adminRights.post_stories) {
            i++;
        }
        if (adminRights.edit_stories) {
            i++;
        }
        if (adminRights.delete_stories) {
            i++;
        }
        return i;
    }

    private void setChannelStoriesEnabled(boolean enabled) {
        adminRights.post_stories = !enabled;
        adminRights.edit_stories = !enabled;
        adminRights.delete_stories = !enabled;
        AndroidUtilities.updateVisibleRows(listView);
    }

    private boolean allDefaultMediaBanned() {
        return defaultBannedRights.send_photos && defaultBannedRights.send_videos && defaultBannedRights.send_stickers
            && defaultBannedRights.send_audios && defaultBannedRights.send_docs && defaultBannedRights.send_voices
            && defaultBannedRights.send_roundvideos && defaultBannedRights.embed_links && defaultBannedRights.send_polls
            && defaultBannedRights.send_reactions;
    }

    private boolean isExpandableSendMediaRow(int position) {
        if (position == sendStickersRow || position == embedLinksRow || position == sendPollsRow ||
            position == sendPhotosRow || position == sendVideosRow || position == sendFilesRow ||
            position == sendMusicRow || position == sendRoundRow || position == sendVoiceRow || position == sendReactionsRow ||
            position == channelPostMessagesRow || position == channelEditMessagesRow || position == channelDeleteMessagesRow ||
            position == channelPostStoriesRow || position == channelEditStoriesRow || position == channelDeleteStoriesRow) {
            return true;
        }
        return false;
    }

    private ValueAnimator asAdminAnimator;

    private void updateAsAdmin(boolean animated) {
        if (addBotButton != null) {
            addBotButton.invalidate();
        }
        final int count = listView.getChildCount();
        for (int i = 0; i < count; ++i) {
            View child = listView.getChildAt(i);
            int childPosition = listView.getChildAdapterPosition(child);
            if (child instanceof TextCheckCell2) {
                if (!asAdmin) {
                    if (
                        childPosition == changeInfoRow && !defaultBannedRights.change_info ||
                        childPosition == pinMessagesRow && !defaultBannedRights.pin_messages ||
                        childPosition == editTagsRow && !defaultBannedRights.edit_rank
                    ) {
                        ((TextCheckCell2) child).setChecked(true);
                        ((TextCheckCell2) child).setEnabled(false, false);
                    } else {
                        ((TextCheckCell2) child).setChecked(false);
                        ((TextCheckCell2) child).setEnabled(childPosition == manageRow, animated);
                    }
                } else {
                    boolean childValue = false, childEnabled = false;
                    if (childPosition == manageRow) {
                        childValue = asAdmin;
                        childEnabled = myAdminRights.add_admins || (currentChat != null && currentChat.creator);
                    } else if (childPosition == changeInfoRow) {
                        childValue = adminRights.change_info;
                        childEnabled = myAdminRights.change_info && defaultBannedRights.change_info;
                    } else if (childPosition == postMessagesRow) {
                        childValue = adminRights.post_messages;
                        childEnabled = myAdminRights.post_messages;
                    } else if (childPosition == manageDirectRow) {
                        childValue = adminRights.manage_direct_messages;
                        childEnabled = myAdminRights.manage_direct_messages;
                    } else if (childPosition == editMesagesRow) {
                        childValue = adminRights.edit_messages;
                        childEnabled = myAdminRights.edit_messages;
                    } else if (childPosition == deleteMessagesRow) {
                        childValue = adminRights.delete_messages;
                        childEnabled = myAdminRights.delete_messages;
                    } else if (childPosition == banUsersRow) {
                        childValue = adminRights.ban_users;
                        childEnabled = myAdminRights.ban_users;
                    } else if (childPosition == addUsersRow) {
                        childValue = adminRights.invite_users;
                        childEnabled = myAdminRights.invite_users;
                    } else if (childPosition == pinMessagesRow) {
                        childValue = adminRights.pin_messages;
                        childEnabled = myAdminRights.pin_messages && defaultBannedRights.pin_messages;
                    } else if (childPosition == editTagsRow) {
                        childValue = adminRights.manage_ranks;
                        childEnabled = myAdminRights.manage_ranks;
                    } else if (childPosition == startVoiceChatRow) {
                        childValue = adminRights.manage_call;
                        childEnabled = myAdminRights.manage_call;
                    } else if (childPosition == addAdminsRow) {
                        childValue = adminRights.add_admins;
                        childEnabled = myAdminRights.add_admins;
                    } else if (childPosition == anonymousRow) {
                        childValue = adminRights.anonymous;
                        childEnabled = myAdminRights.anonymous || (currentChat != null && currentChat.creator);
                    } else if (childPosition == manageTopicsRow) {
                        childValue = adminRights.manage_topics;
                        childEnabled = myAdminRights.manage_topics;
                    } else if (childPosition == manageLinkedPeersRow) {
                        childValue = adminRights.manage_linked_peers;
                        childEnabled = myAdminRights.manage_linked_peers;
                    }
                    ((TextCheckCell2) child).setChecked(childValue);
                    ((TextCheckCell2) child).setEnabled(childEnabled, animated);
                }
            }
        }
//        listViewAdapter.notifyItemRangeChanged(permissionsStartRow, permissionsEndRow - permissionsStartRow);
//        if (asAdmin) {
//            listViewAdapter.notifyItemMoved(addBotButtonRow, rightsShadowRow + 1);
//            listViewAdapter.notifyItemRangeInserted(rightsShadowRow, rankInfoRow - rightsShadowRow + 1);
//        } else {
//            listViewAdapter.notifyItemRangeRemoved(rightsShadowRow, rankInfoRow - rightsShadowRow + 1);
//            listViewAdapter.notifyItemMoved(addBotButtonRow, permissionsEndRow + 1);
//        }
        listViewAdapter.notifyDataSetChanged();

        if (addBotButtonText != null) {
            addBotButtonText.setText(LocaleController.getString(R.string.AddBotButton) + " " + (asAdmin ? LocaleController.getString(R.string.AddBotButtonAsAdmin) : LocaleController.getString(R.string.AddBotButtonAsMember)), animated, asAdmin);
        }
        if (asAdminAnimator != null) {
            asAdminAnimator.cancel();
            asAdminAnimator = null;
        }
        if (animated) {
            asAdminAnimator = ValueAnimator.ofFloat(asAdminT, asAdmin ? 1f : 0f);
            asAdminAnimator.addUpdateListener(a -> {
                asAdminT = (float) a.getAnimatedValue();
                if (addBotButton != null) {
                    addBotButton.invalidate();
                }
            });
            asAdminAnimator.setDuration((long) (Math.abs(asAdminT - (asAdmin ? 1f : 0f)) * 200));
            asAdminAnimator.start();
        } else {
            asAdminT = asAdmin ? 1f : 0f;
            if (addBotButton != null) {
                addBotButton.invalidate();
            }
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        ThemeDescription.ThemeDescriptionDelegate cellDelegate = () -> {
            if (listView != null) {
                int count = listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    if (child instanceof UserCell2) {
                        ((UserCell2) child).update(0);
                    }
                }
            }
        };

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell2.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class, TextDetailCell.class, PollEditTextCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_text_RedRegular));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switch2Track));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switch2TrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_text_RedRegular));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteHintText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{UserCell2.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));

        themeDescriptions.add(new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_dialogTextGray2));
        themeDescriptions.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_dialogRadioBackground));
        themeDescriptions.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_dialogRadioBackgroundChecked));

        return themeDescriptions;
    }
}
