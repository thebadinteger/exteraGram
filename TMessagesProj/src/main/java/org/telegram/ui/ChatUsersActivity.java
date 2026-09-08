return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    ManageChatUserCell manageChatUserCell = new ManageChatUserCell(mContext, type == TYPE_BANNED || type == TYPE_KICKED ? 7 : 6, type == TYPE_BANNED || type == TYPE_KICKED ? 6 : 2, selectType == SELECT_TYPE_MEMBERS);
                    manageChatUserCell.setDelegate((cell, click) -> {
                        TLObject participant = listViewAdapter.getItem((Integer) cell.getTag());
                        return createMenuForParticipant(participant, !click, cell);
                    });
                    view = manageChatUserCell;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
                case 2:
                    view = new ManageChatTextCell(mContext);
                    break;
                case 3:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 4:
                    view = new TextInfoPrivacyCell(mContext);
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) view;
                    if (isChannel) {
                        privacyCell.setText(getString(R.string.NoBlockedChannel2));
                    } else if (isCommunity) {
                        privacyCell.setText(getString(R.string.NoBlockedCommunity2));
                    } else {
                        privacyCell.setText(getString(R.string.NoBlockedGroup2));
                    }
                    break;
                case 5:
                    HeaderCell headerCell = new HeaderCell(mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 11, false);
                    headerCell.setHeight(43);
                    view = headerCell;
                    break;
                case 6:
                    view = new TextSettingsCell(mContext);
                    break;
                case VIEW_TYPE_EXPANDABLE_SWITCH:
                case 7:
                    view = new TextCheckCell2(mContext);
                    break;
                case 8:
                    view = new GraySectionCell(mContext, 26, resourceProvider);
                    view.setBackground(null);
                    break;
                case 10:
                    view = new LoadingCell(mContext, dp(40), dp(120));
                    break;
                case 11:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(FlickerLoadingView.USERS_TYPE);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setUseHeaderOffset(false);
                    flickerLoadingView.setPaddingLeft(dp(5));
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.leftMargin = lp.rightMargin = dp(12);
                    lp.topMargin = dp(30);
                    flickerLoadingView.setLayoutParams(lp);
                    view = flickerLoadingView;
                    break;
                case 12:
                    TextCell textCell = new TextCell(mContext, 23, false, true, getResourceProvider());
                    textCell.heightDp = 50;
                    view = textCell;
                    break;
                case 9:
                default:
                    SlideChooseView chooseView = new SlideChooseView(mContext);
                    view = chooseView;
                    chooseView.setOptions(
                            selectedSlowmode,
                            getString("SlowmodeOff", R.string.SlowmodeOff),
                            LocaleController.formatString(R.string.SlowmodeSeconds, 5),
                            LocaleController.formatString(R.string.SlowmodeSeconds, 10),
                            LocaleController.formatString(R.string.SlowmodeSeconds, 30),
                            LocaleController.formatString(R.string.SlowmodeMinutes, 1),
                            LocaleController.formatString(R.string.SlowmodeMinutes, 5),
                            LocaleController.formatString(R.string.SlowmodeMinutes, 15),
                            LocaleController.formatString(R.string.SlowmodeHours, 1)
                    );
                    chooseView.setCallback(which -> {
                        if (info == null) {
                            return;
                        }
                        boolean needRowsUpdate = (selectedSlowmode > 0 && which == 0) || (selectedSlowmode == 0 && which > 0);
                        selectedSlowmode = which;
                        if (needRowsUpdate) {
                            DiffCallback diffCallback = saveState();
                            updateRows();
                            updateListAnimated(diffCallback);
                        }
                        listViewAdapter.notifyItemChanged(slowmodeInfoRow);
                    });
                    break;
                case VIEW_TYPE_NOT_RESTRICT_BOOSTERS_SLIDER: {
                    SlideChooseView slider = new SlideChooseView(mContext);
                    view = slider;
                    Drawable[] drawables = new Drawable[]{
                            ContextCompat.getDrawable(getContext(), R.drawable.mini_boost_profile_badge),
                            ContextCompat.getDrawable(getContext(), R.drawable.mini_boost_profile_badge2),
                            ContextCompat.getDrawable(getContext(), R.drawable.mini_boost_profile_badge2),
                            ContextCompat.getDrawable(getContext(), R.drawable.mini_boost_profile_badge2),
                            ContextCompat.getDrawable(getContext(), R.drawable.mini_boost_profile_badge2)
                    };
                    slider.setOptions(notRestrictBoosters > 0 ? (notRestrictBoosters - 1) : 0, drawables, "1", "2", "3", "4", "5");
                    slider.setCallback(which -> {
                        notRestrictBoosters = which + 1;
                    });
                    break;
                }
                case VIEW_TYPE_INNER_CHECK:
                    CheckBoxCell checkBoxCell = new CheckBoxCell(mContext, 4, 21, getResourceProvider());
                    checkBoxCell.getCheckBoxRound().setDrawBackgroundAsArc(14);
                    checkBoxCell.getCheckBoxRound().setColor(Theme.key_switch2TrackChecked, Theme.key_radioBackground, Theme.key_checkboxCheck);
                    checkBoxCell.setEnabled(true);
                    view = checkBoxCell;
                    break;
                case VIEW_TYPE_CHECK:
                    view = new TextCheckCell(mContext, getResourceProvider());
                    break;
                case VIEW_TYPE_SLIDER:
                    view = new SlideIntChooseView(mContext, getResourceProvider());
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(position);
                    TLObject item = getItem(position);
                    int lastRow;

                    boolean showJoined = false;
                    if (position >= participantsStartRow && position < participantsEndRow) {
                        lastRow = participantsEndRow;
                        showJoined = ChatObject.isChannel(currentChat) && !currentChat.megagroup;
                    } else if (position >= contactsStartRow && position < contactsEndRow) {
                        lastRow = contactsEndRow;
                        showJoined = ChatObject.isChannel(currentChat) && !currentChat.megagroup;
                    } else {
                        lastRow = botEndRow;
                    }

                    long peerId;
                    long kickedBy;
                    long promotedBy;
                    TLRPC.TL_chatBannedRights bannedRights;
                    boolean banned;
                    boolean creator;
                    boolean admin;
                    int joined;
                    if (item instanceof TLRPC.User) {
                        peerId = ((TLRPC.User) item).id;
                        kickedBy = 0;
                        promotedBy = 0;
                        bannedRights = null;
                        joined = 0;
                        banned = false;
                        creator = false;
                        admin = false;
                    } else if (item instanceof TLRPC.ChannelParticipant) {
                        TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) item;
                        peerId = MessageObject.getPeerId(participant.peer);
                        kickedBy = participant.kicked_by;
                        promotedBy = participant.promoted_by;
                        bannedRights = participant.banned_rights;
                        joined = participant.date;
                        banned = participant instanceof TLRPC.TL_channelParticipantBanned;
                        creator = participant instanceof TLRPC.TL_channelParticipantCreator;
                        admin = participant instanceof TLRPC.TL_channelParticipantAdmin;
                    } else if (item instanceof TLRPC.ChatParticipant) {
                        TLRPC.ChatParticipant participant = (TLRPC.ChatParticipant) item;
                        peerId = participant.user_id;
                        joined = participant.date;
                        kickedBy = 0;
                        promotedBy = 0;
                        bannedRights = null;
                        banned = false;
                        creator = participant instanceof TLRPC.TL_chatParticipantCreator;
                        admin = participant instanceof TLRPC.TL_chatParticipantAdmin;
                    } else {
                        return;
                    }
                    TLObject object;
                    if (peerId > 0) {
                        object = getMessagesController().getUser(peerId);
                    } else {
                        object = getMessagesController().getChat(-peerId);
                    }
                    if (object != null) {
                        if (type == TYPE_KICKED) {
                            userCell.setData(object, null, formatUserPermissions(bannedRights), position != lastRow - 1);
                        } else if (type == TYPE_BANNED) {
                            String role = null;
                            if (banned) {
                                TLRPC.User user1 = getMessagesController().getUser(kickedBy);
                                if (user1 != null) {
                                    role = LocaleController.formatString(R.string.UserRemovedBy, UserObject.getUserName(user1));
                                }
                            }
                            userCell.setData(object, null, role, position != lastRow - 1);
                        } else if (type == TYPE_ADMIN) {
                            String role = null;
                            if (creator) {
                                role = getString(R.string.ChannelCreator);
                            } else if (admin) {
                                TLRPC.User user1 = getMessagesController().getUser(promotedBy);
                                if (user1 != null) {
                                    if (user1.id == peerId) {
                                        role = getString(R.string.ChannelAdministrator);
                                    } else {
                                        role = LocaleController.formatString(R.string.EditAdminPromotedBy, UserObject.getUserName(user1));
                                    }
                                }
                            }
                            userCell.setData(object, null, role, position != lastRow - 1);
                        } else if (type == TYPE_USERS) {
                            CharSequence status;
                            if (showJoined && joined != 0) {
                                status = LocaleController.formatJoined(joined);
                            } else {
                                status = null;
                            }
                            userCell.setData(object, null, status, position != lastRow - 1);
                        }
                    }
                    break;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == antiSpamInfoRow) {
                        privacyCell.setText(getString("ChannelAntiSpamInfo", R.string.ChannelAntiSpamInfo));
                    } else if (position == participantsInfoRow) {
                        if (type == TYPE_BANNED || type == TYPE_KICKED) {
                            if (isChannel) {
                                privacyCell.setText(getString(R.string.NoBlockedChannel2));
                            } else if (isCommunity) {
                                privacyCell.setText(getString(R.string.NoBlockedCommunity2));
                            } else {
                                privacyCell.setText(getString(R.string.NoBlockedGroup2));
                            }
                        } else if (type == TYPE_ADMIN) {
                            if (addNewRow != -1) {
                                if (isChannel) {
                                    privacyCell.setText(getString("ChannelAdminsInfo", R.string.ChannelAdminsInfo));
                                } else {
                                    privacyCell.setText(getString("MegaAdminsInfo", R.string.MegaAdminsInfo));
                                }
                            } else {
                                privacyCell.setText("");
                            }
                        } else if (type == TYPE_USERS) {
                            if (!isChannel || selectType != SELECT_TYPE_MEMBERS) {
                                privacyCell.setText("");
                            } else {
                                privacyCell.setText(getString("ChannelMembersInfo", R.string.ChannelMembersInfo));
                            }
                        }
                    } else if (position == slowmodeInfoRow) {
                        int seconds = getSecondsForIndex(selectedSlowmode);
                        if (info == null || seconds == 0) {
                            privacyCell.setText(getString(R.string.SlowmodeInfoOff));
                        } else {
                            privacyCell.setText(LocaleController.formatString(R.string.SlowmodeInfoSelected, formatSeconds(seconds)));
                        }
                    } else if (position == payInfoRow) {
                        privacyCell.setText(getString(R.string.GroupMessagesChargePriceInfo));
                    } else if (position == priceInfoRow) {
                        final float revenuePercent = getMessagesController().starsPaidMessageCommissionPermille / 1000.0f;
                        final String income = String.valueOf((int) ((starsPrice * revenuePercent / 1000.0 * getMessagesController().starsUsdWithdrawRate1000)) / 100.0);
                        privacyCell.setText(LocaleController.formatString(R.string.GroupMessagesPriceInfo, percents(getMessagesController().starsPaidMessageCommissionPermille), income));
                    } else if (position == hideMembersInfoRow) {
                        privacyCell.setText(getString(R.string.ChannelHideMembersInfo));
                    } else if (position == tagsInfoRow) {
                        privacyCell.setText(getString(R.string.ChannelMemberTagsInfo));
                    }else if (position == gigaInfoRow) {
                        privacyCell.setText(getString(R.string.BroadcastGroupConvertInfo));
                    } else if (position == dontRestrictBoostersInfoRow) {
                        if (isEnabledNotRestrictBoosters) {
                            privacyCell.setText(getString(R.string.GroupNotRestrictBoostersInfo2));
                        } else {
                            privacyCell.setText(getString(R.string.GroupNotRestrictBoostersInfo));
                        }
                    } else if (position == signMessagesInfoRow) {
                        privacyCell.setText(getString(signatures ? R.string.ChannelSignProfilesInfo : R.string.ChannelSignInfo));
                    }
                    break;
                case 2:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    actionCell.setColors(Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_windowBackgroundWhiteBlackText);
                    if (position == addNewRow) {
                        if (type == TYPE_KICKED) {
                            actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                            actionCell.setText(getString("ChannelAddException", R.string.ChannelAddException), null, R.drawable.msg_contact_add, participantsStartRow != -1);
                        } else if (type == TYPE_BANNED) {
                            actionCell.setText(getString("ChannelBlockUser", R.string.ChannelBlockUser), null, R.drawable.msg_user_remove, false);
                        } else if (type == TYPE_ADMIN) {
                            actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                            boolean showDivider = !(loadingUsers && !firstLoaded);
                            actionCell.setText(getString("ChannelAddAdmin", R.string.ChannelAddAdmin), null, R.drawable.msg_admin_add, showDivider);
                        } else if (type == TYPE_USERS) {
                            actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                            boolean showDivider = addNew2Row != -1 || (!(loadingUsers && !firstLoaded) && membersHeaderRow == -1 && !participants.isEmpty());
                            if (isChannel) {
                                actionCell.setText(getString(R.string.AddSubscriber), null, R.drawable.msg_contact_add, showDivider);
                            } else {
                                actionCell.setText(getString(R.string.AddMember), null, R.drawable.msg_contact_add, showDivider);
                            }
                        }
                    } else if (position == recentActionsRow) {
                        actionCell.setText(getString(R.string.EventLog), null, R.drawable.msg_log, antiSpamRow > recentActionsRow);
                    } else if (position == addNew2Row) {
                        actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                        boolean showDivider = !(loadingUsers && !firstLoaded) && membersHeaderRow == -1 && !participants.isEmpty();
                        actionCell.setText(getString("ChannelInviteViaLink", R.string.ChannelInviteViaLink), null, R.drawable.msg_link2, showDivider);
                    } else if (position == gigaConvertRow) {
                        actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                        actionCell.setText(getString("BroadcastGroupConvert", R.string.BroadcastGroupConvert), null, R.drawable.msg_channel, false);
                    }
                    break;
                case 3:
                    break;
                case 5:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == restricted1SectionRow) {
                        if (type == TYPE_BANNED) {
                            int count = info != null ? info.kicked_count : participants.size();
                            if (count != 0) {
                                headerCell.setText(LocaleController.formatPluralString("RemovedUser", count));
                            } else {
                                headerCell.setText(getString(R.string.ChannelBlockedUsers));
                            }
                        } else {
                            headerCell.setText(getString(R.string.ChannelRestrictedUsers));
                        }
                    } else if (position == permissionsSectionRow) {
                        headerCell.setText(getString(isCommunity ? R.string.CommunityPermissionsHeader : R.string.ChannelPermissionsHeader));
                    } else if (position == slowmodeRow) {
                        headerCell.setText(getString(R.string.Slowmode));
                    } else if (position == gigaHeaderRow) {
                        headerCell.setText(getString(R.string.BroadcastGroup));
                    } else if (position == priceHeaderRow) {
                        headerCell.setText(getString(R.string.GroupMessagesPriceHeader));
                    }
                    break;
                case 6:
                    TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                    settingsCell.setTextAndValue(getString("ChannelBlacklist", R.string.ChannelBlacklist), String.format("%d", info != null ? info.kicked_count : 0), false);
                    break;
                case VIEW_TYPE_EXPANDABLE_SWITCH:
                case 7:
                    TextCheckCell2 checkCell = (TextCheckCell2) holder.itemView;
                    checkCell.getCheckBox().setDrawIconType(1);
                    checkCell.getCheckBox().setColors(Theme.key_fill_RedNormal, Theme.key_switch2TrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
                    boolean animated = checkCell.getTag() != null && (Integer) checkCell.getTag() == position;
                    checkCell.setTag(position);
                    if (position == changeInfoRow) {
                        checkCell.setTextAndCheck(getString(isCommunity ? R.string.CommunityAdminRightEditCommunityName : R.string.UserRestrictionsChangeInfo), !defaultBannedRights.change_info && !ChatObject.isPublic(currentChat), manageTopicsRow != -1, animated);
                    } else if (position == manageLinkedPeersRow) {
                        checkCell.setTextAndCheck(getString(R.string.CommunityAdminRightEditGroupList), !defaultBannedRights.manage_linked_peers, false, animated);
                    } else if (position == addUsersRow) {
                        checkCell.setTextAndCheck(getString("UserRestrictionsInviteUsers", R.string.UserRestrictionsInviteUsers), !defaultBannedRights.invite_users, true, animated);
                    } else if (position == pinMessagesRow) {
                        checkCell.setTextAndCheck(getString(R.string.UserRestrictionsPinMessages), !defaultBannedRights.pin_messages && !ChatObject.isPublic(currentChat), true, animated);
                    } else if (position == editTagRow) {
                        checkCell.setTextAndCheck(getString(R.string.UserRestrictionsEditTags), !defaultBannedRights.edit_rank, true, animated);
                    } else if (position == sendMessagesRow) {
                        checkCell.setTextAndCheck(getString("UserRestrictionsSendText", R.string.UserRestrictionsSendText), !defaultBannedRights.send_plain, true, animated);
                    } else if(position == dontRestrictBoostersRow) {
                        checkCell.setTextAndCheck(getString(R.string.GroupNotRestrictBoosters), isEnabledNotRestrictBoosters, false, animated);
                        checkCell.getCheckBox().setDrawIconType(0);
                        checkCell.getCheckBox().setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
                    } else if (position == sendMediaRow) {
                        int sentMediaCount = getSendMediaSelectedCount();
                        checkCell.setTextAndCheck(getString("UserRestrictionsSendMedia", R.string.UserRestrictionsSendMedia), sentMediaCount > 0, true, animated);
                        checkCell.setCollapseArrow(String.format(Locale.US, "%d/10", sentMediaCount), !sendMediaExpanded, new Runnable() {
                            @Override
                            public void run() {
                                boolean checked = !checkCell.isChecked();
                                checkCell.setChecked(checked);
                                setSendMediaEnabled(checked);
                            }
                        });
                    } else if (position == sendStickersRow) {
                        checkCell.setTextAndCheck(getString("UserRestrictionsSendStickers", R.string.UserRestrictionsSendStickers), !defaultBannedRights.send_stickers, true, animated);
                    } else if (position == embedLinksRow) {
                        checkCell.setTextAndCheck(getString("UserRestrictionsEmbedLinks", R.string.UserRestrictionsEmbedLinks), !defaultBannedRights.embed_links, true, animated);
                    } else if (position == sendPollsRow) {
                        checkCell.setTextAndCheck(getString("UserRestrictionsSendPollsShort", R.string.UserRestrictionsSendPollsShort), !defaultBannedRights.send_polls, true);
                    } else if (position == manageTopicsRow) {
                        checkCell.setTextAndCheck(getString("CreateTopicsPermission", R.string.CreateTopicsPermission), !defaultBannedRights.manage_topics, false, animated);
                    }
                    if ((position == pinMessagesRow || position == changeInfoRow) && ChatObject.isDiscussionGroup(currentAccount, chatId)) {
                        checkCell.setIcon(R.drawable.permission_locked);
                    } else if (ChatObject.canBlockUsers(currentChat)) {
                        if (position == addUsersRow && !ChatObject.canUserDoAdminAction(currentChat, ChatObject.ACTION_INVITE) ||
                                position == pinMessagesRow && !ChatObject.canUserDoAdminAction(currentChat, ChatObject.ACTION_PIN) ||
                                position == changeInfoRow && !ChatObject.canUserDoAdminAction(currentChat, ChatObject.ACTION_CHANGE_INFO) ||
                                position == manageTopicsRow && !ChatObject.canManageTopics(currentChat) ||
                                ChatObject.isPublic(currentChat) && (position == pinMessagesRow || position == changeInfoRow)) {
                            checkCell.setIcon(R.drawable.permission_locked);
                        } else {
                            checkCell.setIcon(0);
                        }
                    } else {
                        checkCell.setIcon(0);
                    }
                    break;
                case 8:
                    GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
                    if (position == membersHeaderRow) {
                        if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                            sectionCell.setText(getString("ChannelOtherSubscribers", R.string.ChannelOtherSubscribers));
                        } else {
                            sectionCell.setText(getString("ChannelOtherMembers", R.string.ChannelOtherMembers));
                        }
                    } else if (position == botHeaderRow) {
                        sectionCell.setText(getString("ChannelBots", R.string.ChannelBots));
                    } else if (position == contactsHeaderRow) {
                        if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                            sectionCell.setText(getString("ChannelContacts", R.string.ChannelContacts));
                        } else {
                            sectionCell.setText(getString("GroupContacts", R.string.GroupContacts));
                        }
                    } else if (position == loadingHeaderRow) {
                        sectionCell.setText("");
                    }
                    break;
                case 11:
                    FlickerLoadingView flickerLoadingView = (FlickerLoadingView) holder.itemView;
                    if (type == TYPE_BANNED) {
                        flickerLoadingView.setItemsCount(info == null ? 1 : info.kicked_count);
                    } else {
                        flickerLoadingView.setItemsCount(1);
                    }
                    break;
                case 12:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == antiSpamRow) {
                        textCell.getCheckBox().setIcon(ChatObject.canUserDoAdminAction(currentChat, ChatObject.ACTION_DELETE_MESSAGES) && (info == null || info.antispam || getParticipantsCount() >= getMessagesController().telegramAntispamGroupSizeMin) ? 0 : R.drawable.permission_locked);
                        textCell.setTextAndCheckAndIcon(getString("ChannelAntiSpam", R.string.ChannelAntiSpam), info != null && info.antispam, R.drawable.msg_policy, false);
                    } else if (position == hideMembersRow) {
                        textCell.getCheckBox().setIcon(ChatObject.canUserDoAdminAction(currentChat, ChatObject.ACTION_BLOCK_USERS) && (info == null || info.participants_hidden || getParticipantsCount() >= getMessagesController().hiddenMembersGroupSizeMin) ? 0 : R.drawable.permission_locked);
                        textCell.setTextAndCheck(getString(R.string.ChannelHideMembers), info != null && info.participants_hidden, false);
                    } else if (position == tagsRow) {
                        textCell.getCheckBox().setIcon(0);
                        textCell.setTextAndCheck(getString(R.string.ChannelMemberTags), !(currentChat != null && currentChat.default_banned_rights != null && currentChat.default_banned_rights.edit_rank), false);
                    }
                    break;
                case VIEW_TYPE_INNER_CHECK:
                    CheckBoxCell checkBoxCell = (CheckBoxCell) holder.itemView;
                    animated = checkBoxCell.getTag() != null && (Integer) checkBoxCell.getTag() == position;
                    checkBoxCell.setTag(position);
                    if (position == sendMediaPhotosRow) {
                        checkBoxCell.setText(getString("SendMediaPermissionPhotos", R.string.SendMediaPermissionPhotos), "", !defaultBannedRights.send_photos, true, animated);
                    } else if (position == sendMediaVideosRow) {
                        checkBoxCell.setText(getString("SendMediaPermissionVideos", R.string.SendMediaPermissionVideos), "", !defaultBannedRights.send_videos, true, animated);
                    } else if (position == sendMediaStickerGifsRow) {
                        checkBoxCell.setText(getString("SendMediaPermissionStickersGifs", R.string.SendMediaPermissionStickersGifs), "", !defaultBannedRights.send_stickers, true, animated);
                    } else if (position == sendMediaMusicRow) {
                        checkBoxCell.setText(getString("SendMediaPermissionMusic", R.string.SendMediaPermissionMusic), "", !defaultBannedRights.send_audios, true, animated);
                    } else if (position == sendMediaFilesRow) {
                        checkBoxCell.setText(getString("SendMediaPermissionFiles", R.string.SendMediaPermissionFiles), "", !defaultBannedRights.send_docs, true, animated);
                    } else if (position == sendMediaVoiceMessagesRow) {
                        checkBoxCell.setText(getString("SendMediaPermissionVoice", R.string.SendMediaPermissionVoice), "", !defaultBannedRights.send_voices, true, animated);
                    } else if (position == sendMediaVideoMessagesRow) {
                        checkBoxCell.setText(getString("SendMediaPermissionRound", R.string.SendMediaPermissionRound), "", !defaultBannedRights.send_roundvideos, true, animated);
                    } else if (position == sendMediaEmbededLinksRow) {
                        checkBoxCell.setText(getString("SendMediaEmbededLinks", R.string.SendMediaEmbededLinks), "", !defaultBannedRights.embed_links && !defaultBannedRights.send_plain, true, animated);
                    } else if (position == sendReactionsRow) {
                        checkBoxCell.setText(getString(R.string.UserRestrictionsSendReactions), "", !defaultBannedRights.send_reactions, false, animated);
                    } else if (position == sendPollsRow) {
                        checkBoxCell.setText(getString("SendMediaPolls", R.string.SendMediaPolls), "", !defaultBannedRights.send_polls, true, animated);
                    } else
                    //  checkBoxCell.setText(getCheckBoxTitle(item.headerName, percents[item.index < 0 ? 8 : item.index], item.index < 0), AndroidUtilities.formatFileSize(item.size), selected, item.index < 0 ? !collapsed : !item.last);
                    checkBoxCell.setPad(1);
                    break;
                case VIEW_TYPE_CHECK:
                    TextCheckCell checkCell2 = (TextCheckCell) holder.itemView;
                    if (position == signMessagesRow) {
                        checkCell2.setTextAndCheck(getString(R.string.ChannelSignMessages), signatures, signatures);
                    } else if (position == signMessagesProfilesRow) {
                        checkCell2.setTextAndCheck(getString(R.string.ChannelSignMessagesWithProfile), profiles, false);
                    } else if (position == payRow) {
                        checkCell2.setTextAndCheck(getString(R.string.GroupMessagesChargePrice), enablePrice, false);
                    }
                    break;
                case VIEW_TYPE_SLIDER:
                    SlideIntChooseView cell = (SlideIntChooseView) holder.itemView;
                    if (position == priceRow) {
                        final int[] steps = SlideIntChooseView.cut(new int[] { 1, 10, 50, 100, 200, 250, 400, 500, 1000, 2500, 5000, 7500, 9000, 10_000 }, (int) getMessagesController().starsPaidMessageAmountMax);
                        cell.set((int) Utilities.clamp(starsPrice, getMessagesController().starsPaidMessageAmountMax, 1), SlideIntChooseView.Options.make(1, steps, 20, (type, val) -> type == 0 ? LocaleController.formatPluralStringComma("Stars", val) : "" + val), newValue -> {
                            starsPrice = newValue;
                            AndroidUtilities.updateVisibleRow(listView, priceInfoRow);
                        });
                    }
                    break;
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == addNewRow || position == addNew2Row || position == recentActionsRow || position == gigaConvertRow) {
                return 2;
            } else if (position >= participantsStartRow && position < participantsEndRow ||
                    position >= botStartRow && position < botEndRow ||
                    position >= contactsStartRow && position < contactsEndRow) {
                return 0;
            } else if (position == addNewSectionRow || position == participantsDividerRow || position == participantsDivider2Row) {
                return 3;
            } else if (position == restricted1SectionRow || position == permissionsSectionRow || position == slowmodeRow || position == gigaHeaderRow || position == priceHeaderRow) {
                return 5;
            } else if (position == participantsInfoRow || position == slowmodeInfoRow || position == dontRestrictBoostersInfoRow || position == gigaInfoRow || position == antiSpamInfoRow || position == hideMembersInfoRow || position == tagsInfoRow || position == signMessagesInfoRow || position == payInfoRow || position == priceInfoRow) {
                return 1;
            } else if (position == blockedEmptyRow) {
                return 4;
            } else if (position == removedUsersRow) {
                return 6;
            } else if (position == changeInfoRow || position == addUsersRow || position == manageLinkedPeersRow || position == pinMessagesRow || position == editTagRow || position == sendMessagesRow ||
                    position == sendStickersRow || position == embedLinksRow || position == manageTopicsRow || position == dontRestrictBoostersRow) {
                return 7;
            } else if (position == membersHeaderRow || position == contactsHeaderRow || position == botHeaderRow || position == loadingHeaderRow) {
                return 8;
            } else if (position == slowmodeSelectRow) {
                return 9;
            } else if (position == loadingProgressRow) {
                return 10;
            } else if (position == loadingUserCellRow) {
                return 11;
            } else if (position == antiSpamRow || position == hideMembersRow || position == tagsRow) {
                return 12;
            } else if (isExpandableSendMediaRow(position)) {
                return VIEW_TYPE_INNER_CHECK;
            } else if (position == sendMediaRow) {
                return VIEW_TYPE_EXPANDABLE_SWITCH;
            } else if (position == dontRestrictBoostersSliderRow) {
                return VIEW_TYPE_NOT_RESTRICT_BOOSTERS_SLIDER;
            } else if (position == signMessagesRow || position == signMessagesProfilesRow || position == payRow) {
                return VIEW_TYPE_CHECK;
            } else if (position == priceRow) {
                return VIEW_TYPE_SLIDER;
            }
            return 0;
        }

        public TLObject getItem(int position) {
            if (position >= participantsStartRow && position < participantsEndRow) {
                return participants.get(position - participantsStartRow);
            } else if (position >= contactsStartRow && position < contactsEndRow) {
                return contacts.get(position - contactsStartRow);
            } else if (position >= botStartRow && position < botEndRow) {
                return bots.get(position - botStartRow);
            }
            return null;
        }
    }

    private void setSendMediaEnabled(boolean enabled) {
        defaultBannedRights.send_media = !enabled;
        defaultBannedRights.send_gifs = !enabled;
        defaultBannedRights.send_inline = !enabled;
        defaultBannedRights.send_games = !enabled;
        defaultBannedRights.send_photos = !enabled;
        defaultBannedRights.send_videos = !enabled;
        defaultBannedRights.send_stickers = !enabled;
        defaultBannedRights.send_audios = !enabled;
        defaultBannedRights.send_docs = !enabled;
        defaultBannedRights.send_voices = !enabled;
        defaultBannedRights.send_roundvideos = !enabled;
        defaultBannedRights.embed_links = !enabled;
        defaultBannedRights.send_polls = !enabled;
        defaultBannedRights.send_reactions = !enabled;
        AndroidUtilities.updateVisibleRows(listView);
        DiffCallback diffCallback = saveState();
        updateRows();
        updateListAnimated(diffCallback);
    }

    private boolean isExpandableSendMediaRow(int position) {
        return position == sendMediaPhotosRow || position == sendMediaVideosRow || position == sendMediaStickerGifsRow ||
                position == sendMediaMusicRow || position == sendMediaFilesRow || position == sendMediaVoiceMessagesRow ||
                position == sendReactionsRow ||
                position == sendMediaVideoMessagesRow || position == sendMediaEmbededLinksRow || position == sendPollsRow;
    }

    public DiffCallback saveState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = rowCount;

        diffCallback.oldBotStartRow = botStartRow;
        diffCallback.oldBotEndRow = botEndRow;
        diffCallback.oldBots.clear();
        diffCallback.oldBots.addAll(bots);

        diffCallback.oldContactsEndRow = contactsEndRow;
        diffCallback.oldContactsStartRow = contactsStartRow;
        diffCallback.oldContacts.clear();
        diffCallback.oldContacts.addAll(contacts);

        diffCallback.oldParticipantsStartRow = participantsStartRow;
        diffCallback.oldParticipantsEndRow = participantsEndRow;
        diffCallback.oldParticipants.clear();
        diffCallback.oldParticipants.addAll(participants);

        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        return diffCallback;
    }

    public void updateListAnimated(DiffCallback savedState) {
        if (listViewAdapter == null) {
            updateRows();
            return;
        }
        updateRows();
        savedState.fillPositions(savedState.newPositionToItem);
        DiffUtil.calculateDiff(savedState).dispatchUpdatesTo(listViewAdapter);
        if (listView != null && layoutManager != null && listView.getChildCount() > 0) {
            View view = null;
            int position = -1;
            for (int i = 0; i < listView.getChildCount(); i++) {
                position = listView.getChildAdapterPosition(listView.getChildAt(i));
                if (position != RecyclerListView.NO_POSITION) {
                    view = listView.getChildAt(i);
                    break;
                }
            }
            if (view != null) {
                layoutManager.scrollToPositionWithOffset(position, view.getTop() - listView.getPaddingTop());
            }
        }
    }

    private class DiffCallback extends DiffUtil.Callback {

        int oldRowCount;
        SparseIntArray oldPositionToItem = new SparseIntArray();
        SparseIntArray newPositionToItem = new SparseIntArray();

        int oldParticipantsStartRow;
        int oldParticipantsEndRow;
        int oldContactsStartRow;
        int oldContactsEndRow;
        int oldBotStartRow;
        int oldBotEndRow;

        private ArrayList<TLObject> oldParticipants = new ArrayList<>();
        private ArrayList<TLObject> oldBots = new ArrayList<>();
        private ArrayList<TLObject> oldContacts = new ArrayList<>();

        @Override
        public int getOldListSize() {
            return oldRowCount;
        }

        @Override
        public int getNewListSize() {
            return rowCount;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldItemPosition >= oldBotStartRow && oldItemPosition < oldBotEndRow && newItemPosition >= botStartRow && newItemPosition < botEndRow) {
                return oldBots.get(oldItemPosition - oldBotStartRow).equals(bots.get(newItemPosition - botStartRow));
            } else if (oldItemPosition >= oldContactsStartRow && oldItemPosition < oldContactsEndRow && newItemPosition >= contactsStartRow && newItemPosition < contactsEndRow) {
                return oldContacts.get(oldItemPosition - oldContactsStartRow).equals(contacts.get(newItemPosition - contactsStartRow));
            } else if (oldItemPosition >= oldParticipantsStartRow && oldItemPosition < oldParticipantsEndRow && newItemPosition >= participantsStartRow && newItemPosition < participantsEndRow) {
                return oldParticipants.get(oldItemPosition - oldParticipantsStartRow).equals(participants.get(newItemPosition - participantsStartRow));
            }
            return oldPositionToItem.get(oldItemPosition) == newPositionToItem.get(newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (areItemsTheSame(oldItemPosition, newItemPosition)) {
                if (restricted1SectionRow == newItemPosition) {
                    return false;
                }
                return true;
            }
            return false;
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0;
            put(++pointer, recentActionsRow, sparseIntArray);
            put(++pointer, addNewRow, sparseIntArray);
            put(++pointer, addNew2Row, sparseIntArray);
            put(++pointer, addNewSectionRow, sparseIntArray);
            put(++pointer, restricted1SectionRow, sparseIntArray);
            put(++pointer, participantsDividerRow, sparseIntArray);
            put(++pointer, participantsDivider2Row, sparseIntArray);
            put(++pointer, gigaHeaderRow, sparseIntArray);
            put(++pointer, gigaConvertRow, sparseIntArray);
            put(++pointer, gigaInfoRow, sparseIntArray);
            put(++pointer, participantsInfoRow, sparseIntArray);
            put(++pointer, blockedEmptyRow, sparseIntArray);
            put(++pointer, permissionsSectionRow, sparseIntArray);
            put(++pointer, sendMessagesRow, sparseIntArray);
            put(++pointer, sendMediaRow, sparseIntArray);
            put(++pointer, sendStickersRow, sparseIntArray);
            put(++pointer, sendPollsRow, sparseIntArray);
            put(++pointer, embedLinksRow, sparseIntArray);
            put(++pointer, addUsersRow, sparseIntArray);
            put(++pointer, manageLinkedPeersRow, sparseIntArray);
            put(++pointer, pinMessagesRow, sparseIntArray);
            put(++pointer, editTagRow, sparseIntArray);
            put(++pointer, sendReactionsRow, sparseIntArray);
            if (isForum) {
                put(++pointer, manageTopicsRow, sparseIntArray);
            }
            put(++pointer, changeInfoRow, sparseIntArray);
            put(++pointer, removedUsersRow, sparseIntArray);
            put(++pointer, contactsHeaderRow, sparseIntArray);
            put(++pointer, botHeaderRow, sparseIntArray);
            put(++pointer, membersHeaderRow, sparseIntArray);
            put(++pointer, slowmodeRow, sparseIntArray);
            put(++pointer, slowmodeSelectRow, sparseIntArray);
            put(++pointer, slowmodeInfoRow, sparseIntArray);
            put(++pointer, dontRestrictBoostersRow, sparseIntArray);
            put(++pointer, dontRestrictBoostersSliderRow, sparseIntArray);
            put(++pointer, dontRestrictBoostersInfoRow, sparseIntArray);
            put(++pointer, loadingProgressRow, sparseIntArray);
            put(++pointer, loadingUserCellRow, sparseIntArray);
            put(++pointer, loadingHeaderRow, sparseIntArray);
            put(++pointer, signMessagesRow, sparseIntArray);
            put(++pointer, signMessagesProfilesRow, sparseIntArray);
            put(++pointer, signMessagesInfoRow, sparseIntArray);
            put(++pointer, payRow, sparseIntArray);
            put(++pointer, payInfoRow, sparseIntArray);
            put(++pointer, priceHeaderRow, sparseIntArray);
            put(++pointer, priceRow, sparseIntArray);
            put(++pointer, priceInfoRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    private int getSendMediaSelectedCount() {
        return getSendMediaSelectedCount(defaultBannedRights);
    }

    public static int getSendMediaSelectedCount(TLRPC.TL_chatBannedRights bannedRights) {
        int i = 0;
        if (!bannedRights.send_photos) {
            i++;
        }
        if (!bannedRights.send_videos) {
            i++;
        }
        if (!bannedRights.send_stickers) {
            i++;
        }
        if (!bannedRights.send_audios) {
            i++;
        }
        if (!bannedRights.send_docs) {
            i++;
        }
        if (!bannedRights.send_voices) {
            i++;
        }
        if (!bannedRights.send_roundvideos) {
            i++;
        }
        if (!bannedRights.embed_links && !bannedRights.send_plain) {
            i++;
        }
        if (!bannedRights.send_polls) {
            i++;
        }
        if (!bannedRights.send_reactions) {
            i++;
        }
        return i;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        ThemeDescription.ThemeDescriptionDelegate cellDelegate = () -> {
            if (listView != null) {
                int count = listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    if (child instanceof ManageChatUserCell) {
                        ((ManageChatUserCell) child).update(0);
                    }
                }
            }
        };

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, SlideChooseView.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_graySectionText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switch2Track));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switch2TrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));

        themeDescriptions.add(new ThemeDescription(undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        themeDescriptions.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, null, null, null, Theme.key_undo_infoColor));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueIcon));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"title"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"subtitle"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ManageChatUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));

        return themeDescriptions;
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }
    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        listView.setPadding(0, 0, 0, bottom);
        listView.setClipToPadding(false);
        undoView.setTranslationY(-bottom);
    }
}
