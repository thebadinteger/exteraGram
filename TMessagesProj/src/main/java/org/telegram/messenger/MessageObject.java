) {
                boolean changedCaption;
                boolean changedMedia;
                boolean addedMedia = false;
                if (!TextUtils.equals(newMessage.message, oldMessage.message)) {
                    changedCaption = true;
                } else {
                    changedCaption = false;
                }
                TLRPC.MessageMedia newMedia = getMedia(newMessage);
                TLRPC.MessageMedia oldMedia = getMedia(oldMessage);
                if (oldMedia == null) {
                    addedMedia = true;
                    changedMedia = false;
                } else if (
                    newMedia.getClass() != oldMedia.getClass() ||
                    newMedia.photo != null && oldMedia.photo != null && newMedia.photo.id != oldMedia.photo.id ||
                    newMedia.document != null && oldMedia.document != null && getMedia(newMessage).document.id != oldMedia.document.id) {
                    addedMedia = false;
                    changedMedia = true;
                } else {
                    addedMedia = false;
                    changedMedia = false;
                }
                if (addedMedia) {
                    messageText = replaceWithLink(getString(R.string.EventLogAddedMedia), "un1", fromUser);
                } else if (changedMedia && changedCaption) {
                    messageText = replaceWithLink(getString(R.string.EventLogEditedMediaCaption), "un1", fromUser);
                } else if (changedCaption) {
                    messageText = replaceWithLink(getString(R.string.EventLogEditedCaption), "un1", fromUser);
                } else {
                    messageText = replaceWithLink(getString(R.string.EventLogEditedMedia), "un1", fromUser);
                }
                message.media = getMedia(newMessage);
                if (changedCaption) {
                    message.media.webpage = new TLRPC.TL_webPage();
                    message.media.webpage.site_name = getString(R.string.EventLogOriginalCaption);
                    if (TextUtils.isEmpty(oldMessage.message)) {
                        message.media.webpage.description = getString(R.string.EventLogOriginalCaptionEmpty);
                    } else {
                        message.media.webpage.description = oldMessage.message;
                        webPageDescriptionEntities = oldMessage.entities;
                    }
                }
            } else {
                messageText = replaceWithLink(getString(R.string.EventLogEditedMessages), "un1", fromUser);
                if (newMessage.action instanceof TLRPC.TL_messageActionGroupCall) {
                    message = newMessage;
                    message.media = new TLRPC.TL_messageMediaEmpty();
                } else {
                    message.message = newMessage.message;
                    message.entities = newMessage.entities;
                    message.media = new TLRPC.TL_messageMediaWebPage();
                    message.media.webpage = new TLRPC.TL_webPage();
                    message.media.webpage.site_name = getString(R.string.EventLogOriginalMessages);
                    if (TextUtils.isEmpty(oldMessage.message)) {
                        message.media.webpage.description = getString(R.string.EventLogOriginalCaptionEmpty);
                    } else {
                        message.media.webpage.description = oldMessage.message;
                        webPageDescriptionEntities = oldMessage.entities;
                    }
                }
            }
            message.reply_markup = newMessage.reply_markup;
            if (message.media.webpage != null) {
                message.media.webpage.flags = 10;
                message.media.webpage.display_url = "";
                message.media.webpage.url = "";
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeEmojiStickerSet) {
            TLRPC.InputStickerSet newPack = ((TLRPC.TL_channelAdminLogEventActionChangeEmojiStickerSet) event.action).new_stickerset;
            TLRPC.InputStickerSet oldPack = ((TLRPC.TL_channelAdminLogEventActionChangeEmojiStickerSet) event.action).new_stickerset;
            if (newPack == null || newPack instanceof TLRPC.TL_inputStickerSetEmpty) {
                messageText = replaceWithLink(getString(R.string.EventLogRemovedEmojiPack), "un1", fromUser);
            } else {
                messageText = replaceWithLink(getString(R.string.EventLogChangedEmojiPack), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeStickerSet) {
            TLRPC.InputStickerSet newStickerset = ((TLRPC.TL_channelAdminLogEventActionChangeStickerSet) event.action).new_stickerset;
            TLRPC.InputStickerSet oldStickerset = ((TLRPC.TL_channelAdminLogEventActionChangeStickerSet) event.action).new_stickerset;
            if (newStickerset == null || newStickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                messageText = replaceWithLink(getString(R.string.EventLogRemovedStickersSet), "un1", fromUser);
            } else {
                messageText = replaceWithLink(getString(R.string.EventLogChangedStickersSet), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeLocation) {
            TLRPC.TL_channelAdminLogEventActionChangeLocation location = (TLRPC.TL_channelAdminLogEventActionChangeLocation) event.action;
            if (location.new_value instanceof TLRPC.TL_channelLocationEmpty) {
                messageText = replaceWithLink(getString(R.string.EventLogRemovedLocation), "un1", fromUser);
            } else {
                TLRPC.TL_channelLocation channelLocation = (TLRPC.TL_channelLocation) location.new_value;
                messageText = replaceWithLink(formatString("EventLogChangedLocation", R.string.EventLogChangedLocation, channelLocation.address), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionToggleSlowMode) {
            TLRPC.TL_channelAdminLogEventActionToggleSlowMode slowMode = (TLRPC.TL_channelAdminLogEventActionToggleSlowMode) event.action;
            if (slowMode.new_value == 0) {
                messageText = replaceWithLink(getString(R.string.EventLogToggledSlowmodeOff), "un1", fromUser);
            } else {
                String string;
                if (slowMode.new_value < 60) {
                    string = LocaleController.formatPluralString("Seconds", slowMode.new_value);
                } else if (slowMode.new_value < 60 * 60) {
                    string = LocaleController.formatPluralString("Minutes", slowMode.new_value / 60);
                } else {
                    string = LocaleController.formatPluralString("Hours", slowMode.new_value / 60 / 60);
                }
                messageText = replaceWithLink(formatString(R.string.EventLogToggledSlowmodeOn, string), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionToggleAutotranslation) {
            final TLRPC.TL_channelAdminLogEventActionToggleAutotranslation action = (TLRPC.TL_channelAdminLogEventActionToggleAutotranslation) event.action;
            messageText = replaceWithLink(getString(action.new_value ? R.string.EventLogToggledAutotranslationOn : R.string.EventLogToggledAutotranslationOff), "un1", fromUser);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionStartGroupCall) {
            if (ChatObject.isChannel(chat) && (!chat.megagroup || chat.gigagroup)) {
                messageText = replaceWithLink(getString(R.string.EventLogStartedLiveStream), "un1", fromUser);
            } else {
                messageText = replaceWithLink(getString(R.string.EventLogStartedVoiceChat), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionDiscardGroupCall) {
            if (ChatObject.isChannel(chat) && (!chat.megagroup || chat.gigagroup)) {
                messageText = replaceWithLink(getString(R.string.EventLogEndedLiveStream), "un1", fromUser);
            } else {
                messageText = replaceWithLink(getString(R.string.EventLogEndedVoiceChat), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantMute) {
            TLRPC.TL_channelAdminLogEventActionParticipantMute action = (TLRPC.TL_channelAdminLogEventActionParticipantMute) event.action;
            long id = getPeerId(action.participant.peer);
            TLObject object;
            if (id > 0) {
                object = MessagesController.getInstance(currentAccount).getUser(id);
            } else {
                object = MessagesController.getInstance(currentAccount).getChat(-id);
            }
            messageText = replaceWithLink(getString(R.string.EventLogVoiceChatMuted), "un1", fromUser);
            messageText = replaceWithLink(messageText, "un2", object);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantUnmute) {
            TLRPC.TL_channelAdminLogEventActionParticipantUnmute action = (TLRPC.TL_channelAdminLogEventActionParticipantUnmute) event.action;
            long id = getPeerId(action.participant.peer);
            TLObject object;
            if (id > 0) {
                object = MessagesController.getInstance(currentAccount).getUser(id);
            } else {
                object = MessagesController.getInstance(currentAccount).getChat(-id);
            }
            messageText = replaceWithLink(getString(R.string.EventLogVoiceChatUnmuted), "un1", fromUser);
            messageText = replaceWithLink(messageText, "un2", object);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionToggleGroupCallSetting) {
            TLRPC.TL_channelAdminLogEventActionToggleGroupCallSetting action = (TLRPC.TL_channelAdminLogEventActionToggleGroupCallSetting) event.action;
            if (action.join_muted) {
                messageText = replaceWithLink(getString(R.string.EventLogVoiceChatNotAllowedToSpeak), "un1", fromUser);
            } else {
                messageText = replaceWithLink(getString(R.string.EventLogVoiceChatAllowedToSpeak), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantJoinByInvite) {
            TLRPC.TL_channelAdminLogEventActionParticipantJoinByInvite action = (TLRPC.TL_channelAdminLogEventActionParticipantJoinByInvite) event.action;
            if (action.via_chatlist) {
                messageText = replaceWithLink(getString(ChatObject.isChannelAndNotMegaGroup(chat) ? R.string.ActionInviteChannelUserFolder : R.string.ActionInviteUserFolder), "un1", fromUser);
            } else {
                messageText = replaceWithLink(getString(ChatObject.isChannelAndNotMegaGroup(chat) ? R.string.ActionInviteChannelUser : R.string.ActionInviteUser), "un1", fromUser);
            }
            if (action.invite != null && !TextUtils.isEmpty(action.invite.link)) {
                messageText = TextUtils.concat(messageText, " ", action.invite.link);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionToggleNoForwards) {
            TLRPC.TL_channelAdminLogEventActionToggleNoForwards action = (TLRPC.TL_channelAdminLogEventActionToggleNoForwards) event.action;
            boolean isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
            if (action.new_value) {
                if (isChannel) {
                    messageText = replaceWithLink(getString(R.string.ActionForwardsRestrictedChannel), "un1", fromUser);
                } else {
                    messageText = replaceWithLink(getString(R.string.ActionForwardsRestrictedGroup), "un1", fromUser);
                }
            } else {
                if (isChannel) {
                    messageText = replaceWithLink(getString(R.string.ActionForwardsEnabledChannel), "un1", fromUser);
                } else {
                    messageText = replaceWithLink(getString(R.string.ActionForwardsEnabledGroup), "un1", fromUser);
                }
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionExportedInviteDelete) {
            TLRPC.TL_channelAdminLogEventActionExportedInviteDelete action = (TLRPC.TL_channelAdminLogEventActionExportedInviteDelete) event.action;
            messageText = replaceWithLink(formatString(R.string.ActionDeletedInviteLinkClickable), "un1", fromUser);
            messageText = replaceWithLink(messageText, "un2", action.invite);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke) {
            TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke action = (TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke) event.action;
            messageText = replaceWithLink(formatString(R.string.ActionRevokedInviteLinkClickable, action.invite.link), "un1", fromUser);
            messageText = replaceWithLink(messageText, "un2", action.invite);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionExportedInviteEdit) {
            TLRPC.TL_channelAdminLogEventActionExportedInviteEdit action = (TLRPC.TL_channelAdminLogEventActionExportedInviteEdit) event.action;
            if (action.prev_invite.link != null && action.prev_invite.link.equals(action.new_invite.link)) {
                messageText = replaceWithLink(formatString(R.string.ActionEditedInviteLinkToSameClickable), "un1", fromUser);
            } else {
                messageText = replaceWithLink(formatString(R.string.ActionEditedInviteLinkClickable), "un1", fromUser);
            }
            messageText = replaceWithLink(messageText, "un2", action.prev_invite);
            messageText = replaceWithLink(messageText, "un3", action.new_invite);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantVolume) {
            TLRPC.TL_channelAdminLogEventActionParticipantVolume action = (TLRPC.TL_channelAdminLogEventActionParticipantVolume) event.action;
            long id = getPeerId(action.participant.peer);
            TLObject object;
            if (id > 0) {
                object = MessagesController.getInstance(currentAccount).getUser(id);
            } else {
                object = MessagesController.getInstance(currentAccount).getChat(-id);
            }
            double vol = ChatObject.getParticipantVolume(action.participant) / 100.0;
            messageText = replaceWithLink(formatString("ActionVolumeChanged", R.string.ActionVolumeChanged, (int) (vol > 0 ? Math.max(vol, 1) : 0)), "un1", fromUser);
            messageText = replaceWithLink(messageText, "un2", object);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeHistoryTTL) {
            TLRPC.TL_channelAdminLogEventActionChangeHistoryTTL action = (TLRPC.TL_channelAdminLogEventActionChangeHistoryTTL) event.action;
            if (!chat.megagroup) {
                if (action.new_value != 0) {
                    messageText = formatString("ActionTTLChannelChanged", R.string.ActionTTLChannelChanged, LocaleController.formatTTLString(action.new_value));
                } else {
                    messageText = getString(R.string.ActionTTLChannelDisabled);
                }
            } else if (action.new_value == 0) {
                messageText = replaceWithLink(getString(R.string.ActionTTLDisabled), "un1", fromUser);
            } else {
                String time;
                if (action.new_value > 24 * 60 * 60) {
                    time = LocaleController.formatPluralString("Days", action.new_value / (24 * 60 * 60));
                } else if (action.new_value >= 60 * 60) {
                    time = LocaleController.formatPluralString("Hours", action.new_value / (60 * 60));
                } else if (action.new_value >= 60) {
                    time = LocaleController.formatPluralString("Minutes", action.new_value / 60);
                } else {
                    time = LocaleController.formatPluralString("Seconds", action.new_value);
                }
                messageText = replaceWithLink(formatString(R.string.ActionTTLChanged, time), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantJoinByRequest) {
            TLRPC.TL_channelAdminLogEventActionParticipantJoinByRequest action = (TLRPC.TL_channelAdminLogEventActionParticipantJoinByRequest) event.action;
            if (action.invite instanceof TLRPC.TL_chatInviteExported && "https://t.me/+PublicChat".equals(((TLRPC.TL_chatInviteExported) action.invite).link) ||
                    action.invite instanceof TLRPC.TL_chatInvitePublicJoinRequests) {
                messageText = replaceWithLink(getString(R.string.JoinedViaRequestApproved), "un1", fromUser);
                messageText = replaceWithLink(messageText, "un2", MessagesController.getInstance(currentAccount).getUser(action.approved_by));
            } else {
                messageText = replaceWithLink(getString(R.string.JoinedViaInviteLinkApproved), "un1", fromUser);
                messageText = replaceWithLink(messageText, "un2", action.invite);
                messageText = replaceWithLink(messageText, "un3", MessagesController.getInstance(currentAccount).getUser(action.approved_by));
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionSendMessage) {
            message = ((TLRPC.TL_channelAdminLogEventActionSendMessage) event.action).message;
            messageText = replaceWithLink(getString(R.string.EventLogSendMessages), "un1", fromUser);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantEditRank) {
            final TLRPC.TL_channelAdminLogEventActionParticipantEditRank action = (TLRPC.TL_channelAdminLogEventActionParticipantEditRank) event.action;
            if (action.user_id == event.user_id) {
                if (!TextUtils.isEmpty(action.prev_rank) && !TextUtils.isEmpty(action.new_rank)) {
                    messageText = formatString(R.string.EventLogRankSelfEdit, action.prev_rank, action.new_rank);
                    messageText = replaceWithLink(messageText, "un1", fromUser);
                } else if (TextUtils.isEmpty(action.prev_rank) && !TextUtils.isEmpty(action.new_rank)) {
                    messageText = formatString(R.string.EventLogRankSelfAdd, action.new_rank);
                    messageText = replaceWithLink(messageText, "un1", fromUser);
                } else {
                    messageText = formatString(R.string.EventLogRankSelfRemove, action.prev_rank);
                    messageText = replaceWithLink(messageText, "un1", fromUser);
                }
            } else {
                final TLRPC.User ofUser = MessagesController.getInstance(currentAccount).getUser(action.user_id);
                if (!TextUtils.isEmpty(action.prev_rank) && !TextUtils.isEmpty(action.new_rank)) {
                    messageText = formatString(R.string.EventLogRankEdit, action.prev_rank, action.new_rank);
                    messageText = replaceWithLink(messageText, "un1", fromUser);
                    messageText = replaceWithLink(messageText, "un2", ofUser);
                } else if (TextUtils.isEmpty(action.prev_rank) && !TextUtils.isEmpty(action.new_rank)) {
                    messageText = formatString(R.string.EventLogRankAdd, action.new_rank);
                    messageText = replaceWithLink(messageText, "un1", fromUser);
                    messageText = replaceWithLink(messageText, "un2", ofUser);
                } else {
                    messageText = formatString(R.string.EventLogRankRemove, action.prev_rank);
                    messageText = replaceWithLink(messageText, "un1", fromUser);
                    messageText = replaceWithLink(messageText, "un2", ofUser);
                }
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeAvailableReactions) {
            TLRPC.TL_channelAdminLogEventActionChangeAvailableReactions eventActionChangeAvailableReactions = (TLRPC.TL_channelAdminLogEventActionChangeAvailableReactions) event.action;
            boolean customReactionsChanged = eventActionChangeAvailableReactions.prev_value instanceof TLRPC.TL_chatReactionsSome
                    && eventActionChangeAvailableReactions.new_value instanceof TLRPC.TL_chatReactionsSome;
            CharSequence newReactions = getStringFrom(eventActionChangeAvailableReactions.new_value);
            String newStr = "**new**";
            String oldStr = "**old**";
            if (customReactionsChanged) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replaceWithLink(formatString(R.string.ActionReactionsChangedList, newStr), "un1", fromUser));
                int i = spannableStringBuilder.toString().indexOf(newStr);
                if (i > 0) {
                    spannableStringBuilder.replace(i, i + newStr.length(), newReactions);
                }
                messageText = spannableStringBuilder;
            } else {
                CharSequence oldReactions = getStringFrom(eventActionChangeAvailableReactions.prev_value);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replaceWithLink(formatString(R.string.ActionReactionsChanged, oldStr, newStr), "un1", fromUser));
                int i = spannableStringBuilder.toString().indexOf(oldStr);
                if (i > 0) {
                    spannableStringBuilder.replace(i, i + oldStr.length(), oldReactions);
                }
                i = spannableStringBuilder.toString().indexOf(newStr);
                if (i > 0) {
                    spannableStringBuilder.replace(i, i + newStr.length(), newReactions);
                }
                messageText = spannableStringBuilder;
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeUsernames) {
            TLRPC.TL_channelAdminLogEventActionChangeUsernames log = (TLRPC.TL_channelAdminLogEventActionChangeUsernames) event.action;

            ArrayList<String> oldUsernames = log.prev_value;
            ArrayList<String> newUsernames = log.new_value;

            messageText = null;

            if (oldUsernames != null && newUsernames != null) {
                if (newUsernames.size() + 1 == oldUsernames.size()) {
                    String removed = null;
                    for (int i = 0; i < oldUsernames.size(); ++i) {
                        String username = oldUsernames.get(i);
                        if (!newUsernames.contains(username)) {
                            if (removed == null) {
                                removed = username;
                            } else {
                                removed = null;
                                break;
                            }
                        }
                    }
                    if (removed != null) {
                        messageText = replaceWithLink(
                            formatString("EventLogDeactivatedUsername", R.string.EventLogDeactivatedUsername, "@" + removed),
                            "un1", fromUser
                        );
                    }
                } else if (oldUsernames.size() + 1 == newUsernames.size()) {
                    String added = null;
                    for (int i = 0; i < newUsernames.size(); ++i) {
                        String username = newUsernames.get(i);
                        if (!oldUsernames.contains(username)) {
                            if (added == null) {
                                added = username;
                            } else {
                                added = null;
                                break;
                            }
                        }
                    }
                    if (added != null) {
                        messageText = replaceWithLink(
                            formatString("EventLogActivatedUsername", R.string.EventLogActivatedUsername, "@" + added),
                            "un1", fromUser
                        );
                    }
                }
            }

            if (messageText == null) {
                messageText = replaceWithLink(
                    formatString("EventLogChangeUsernames", R.string.EventLogChangeUsernames, getUsernamesString(oldUsernames), getUsernamesString(newUsernames)),
                    "un1", fromUser
                );
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionToggleForum) {
            TLRPC.TL_channelAdminLogEventActionToggleForum toggleForum = (TLRPC.TL_channelAdminLogEventActionToggleForum) event.action;
            if (toggleForum.new_value) {
                messageText = replaceWithLink(
                        formatString("EventLogSwitchToForum", R.string.EventLogSwitchToForum),
                        "un1", fromUser
                );
            } else {
                messageText = replaceWithLink(
                        formatString("EventLogSwitchToGroup", R.string.EventLogSwitchToGroup),
                        "un1", fromUser
                );
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionCreateTopic) {
            TLRPC.TL_channelAdminLogEventActionCreateTopic createTopic = (TLRPC.TL_channelAdminLogEventActionCreateTopic) event.action;
            messageText = replaceWithLink(
                    formatString("EventLogCreateTopic", R.string.EventLogCreateTopic),
                    "un1", fromUser
            );
            messageText = replaceWithLink(messageText, "un2", createTopic.topic);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionEditTopic) {
            TLRPC.TL_channelAdminLogEventActionEditTopic editTopic = (TLRPC.TL_channelAdminLogEventActionEditTopic) event.action;
            if (
                editTopic.prev_topic instanceof TLRPC.TL_forumTopic && editTopic.new_topic instanceof TLRPC.TL_forumTopic &&
                ((TLRPC.TL_forumTopic) editTopic.prev_topic).hidden != ((TLRPC.TL_forumTopic) editTopic.new_topic).hidden
            ) {
                String text = ((TLRPC.TL_forumTopic) editTopic.new_topic).hidden ? getString(R.string.TopicHidden2) : getString(R.string.TopicShown2);
                messageText = replaceWithLink(text, "%s", fromUser);
            } else if (
                editTopic.prev_topic instanceof TLRPC.TL_forumTopic && editTopic.new_topic instanceof TLRPC.TL_forumTopic &&
                ((TLRPC.TL_forumTopic) editTopic.prev_topic).closed != ((TLRPC.TL_forumTopic) editTopic.new_topic).closed
            ) {
                if (((TLRPC.TL_forumTopic) editTopic.new_topic).closed) {
                    messageText = replaceWithLink(getString(R.string.EventLogClosedTopic), "%s", fromUser);
                } else {
                    messageText = replaceWithLink(getString(R.string.EventLogReopenedTopic), "%s", fromUser);
                }
                messageText = replaceWithLink(messageText, "un2", editTopic.new_topic);
            } else {
                messageText = replaceWithLink(
                    getString(R.string.EventLogEditTopic),
                    "un1", fromUser
                );
                messageText = replaceWithLink(messageText, "un2", editTopic.prev_topic);
                messageText = replaceWithLink(messageText, "un3", editTopic.new_topic);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionDeleteTopic) {
            TLRPC.TL_channelAdminLogEventActionDeleteTopic deleteTopic = (TLRPC.TL_channelAdminLogEventActionDeleteTopic) event.action;
            messageText = replaceWithLink(
                    getString(R.string.EventLogDeleteTopic),
                    "un1", fromUser
            );
            messageText = replaceWithLink(messageText, "un2", deleteTopic.topic);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionPinTopic) {
            TLRPC.TL_channelAdminLogEventActionPinTopic pinTopic = (TLRPC.TL_channelAdminLogEventActionPinTopic) event.action;
            if (pinTopic.new_topic instanceof TLRPC.TL_forumTopic && ((TLRPC.TL_forumTopic)pinTopic.new_topic).pinned) {
                messageText = replaceWithLink(
                        formatString("EventLogPinTopic", R.string.EventLogPinTopic),
                        "un1", fromUser
                );
                messageText = replaceWithLink(messageText, "un2", pinTopic.new_topic);
            } else {
                messageText = replaceWithLink(
                        formatString("EventLogUnpinTopic", R.string.EventLogUnpinTopic),
                        "un1", fromUser
                );
                messageText = replaceWithLink(messageText, "un2", pinTopic.new_topic);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionToggleAntiSpam) {
            TLRPC.TL_channelAdminLogEventActionToggleAntiSpam action = (TLRPC.TL_channelAdminLogEventActionToggleAntiSpam) event.action;
            messageText = replaceWithLink(
                action.new_value ?
                    getString(R.string.EventLogEnabledAntiSpam) :
                    getString(R.string.EventLogDisabledAntiSpam),
                "un1",
                fromUser
            );
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeColor) {
            boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
            TLRPC.TL_channelAdminLogEventActionChangeColor action = (TLRPC.TL_channelAdminLogEventActionChangeColor) event.action;
            messageText = replaceWithLink(formatString(isChannel ? R.string.EventLogChangedColor : R.string.EventLogChangedColorGroup, AvatarDrawable.colorName(action.prev_value).toLowerCase(), AvatarDrawable.colorName(action.new_value).toLowerCase()), "un1", fromUser);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangePeerColor) {
            boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
            TLRPC.TL_channelAdminLogEventActionChangePeerColor action = (TLRPC.TL_channelAdminLogEventActionChangePeerColor) event.action;
            SpannableStringBuilder ssb = new SpannableStringBuilder(getString(isChannel ? R.string.EventLogChangedPeerColorIcon : R.string.EventLogChangedPeerColorIconGroup));

            SpannableStringBuilder prev = new SpannableStringBuilder();
            if ((action.prev_value.flags & 1) != 0) {
                prev.append("c");
                prev.setSpan(new PeerColorActivity.PeerColorSpan(false, currentAccount, action.prev_value.color).setSize(dp(18)), prev.length() - 1, prev.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((action.prev_value.flags & 2) != 0) {
                if (prev.length() > 0)
                    prev.append(", ");
                prev.append("e");
                prev.setSpan(new AnimatedEmojiSpan(action.prev_value.background_emoji_id, Theme.chat_actionTextPaint.getFontMetricsInt()), prev.length() - 1, prev.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (prev.length() == 0) {
                prev.append(getString(R.string.EventLogEmojiNone));
            }

            SpannableStringBuilder next = new SpannableStringBuilder();
            if ((action.new_value.flags & 1) != 0) {
                next.append("c");
                next.setSpan(new PeerColorActivity.PeerColorSpan(false, currentAccount, action.new_value.color).setSize(dp(18)), next.length() - 1, next.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((action.new_value.flags & 2) != 0) {
                if (next.length() > 0)
                    next.append(", ");
                next.append("e");
                next.setSpan(new AnimatedEmojiSpan(action.new_value.background_emoji_id, Theme.chat_actionTextPaint.getFontMetricsInt()), next.length() - 1, next.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (next.length() == 0) {
                next.append(getString(R.string.EventLogEmojiNone));
            }

            ssb = AndroidUtilities.replaceCharSequence("%1$s", ssb, prev);
            ssb = AndroidUtilities.replaceCharSequence("%2$s", ssb, next);

            messageText = replaceWithLink(ssb, "un1", fromUser);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeProfilePeerColor) {
            boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
            TLRPC.TL_channelAdminLogEventActionChangeProfilePeerColor action = (TLRPC.TL_channelAdminLogEventActionChangeProfilePeerColor) event.action;
            SpannableStringBuilder ssb = new SpannableStringBuilder(getString(isChannel ? R.string.EventLogChangedProfileColorIcon : R.string.EventLogChangedProfileColorIconGroup));

            SpannableStringBuilder prev = new SpannableStringBuilder();
            if ((action.prev_value.flags & 1) != 0) {
                prev.append("c");
                prev.setSpan(new PeerColorActivity.PeerColorSpan(true, currentAccount, action.prev_value.color).setSize(dp(18)), prev.length() - 1, prev.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((action.prev_value.flags & 2) != 0) {
                if (prev.length() > 0)
                    prev.append(", ");
                prev.append("e");
                prev.setSpan(new AnimatedEmojiSpan(action.prev_value.background_emoji_id, Theme.chat_actionTextPaint.getFontMetricsInt()), prev.length() - 1, prev.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (prev.length() == 0) {
                prev.append(getString(R.string.EventLogEmojiNone));
            }

            SpannableStringBuilder next = new SpannableStringBuilder();
            if ((action.new_value.flags & 1) != 0) {
                next.append("c");
                next.setSpan(new PeerColorActivity.PeerColorSpan(true, currentAccount, action.new_value.color).setSize(dp(18)), next.length() - 1, next.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((action.new_value.flags & 2) != 0) {
                if (next.length() > 0)
                    next.append(", ");
                next.append("e");
                next.setSpan(new AnimatedEmojiSpan(action.new_value.background_emoji_id, Theme.chat_actionTextPaint.getFontMetricsInt()), next.length() - 1, next.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (next.length() == 0) {
                next.append(getString(R.string.EventLogEmojiNone));
            }

            ssb = AndroidUtilities.replaceCharSequence("%1$s", ssb, prev);
            ssb = AndroidUtilities.replaceCharSequence("%2$s", ssb, next);

            messageText = replaceWithLink(ssb, "un1", fromUser);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeEmojiStatus) {
            boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
            TLRPC.TL_channelAdminLogEventActionChangeEmojiStatus action = (TLRPC.TL_channelAdminLogEventActionChangeEmojiStatus) event.action;

            boolean prevNone = false;
            SpannableString prev;
            if (action.prev_value instanceof TLRPC.TL_emojiStatusEmpty) {
                prev = new SpannableString(getString(R.string.EventLogEmojiNone));
                prevNone = true;
            } else {
                prev = new SpannableString("e");
                prev.setSpan(new AnimatedEmojiSpan(DialogObject.getEmojiStatusDocumentId(action.prev_value), Theme.chat_actionTextPaint.getFontMetricsInt()), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            final int until = DialogObject.getEmojiStatusUntil(action.new_value);

            SpannableString next;
            if (action.new_value instanceof TLRPC.TL_emojiStatusEmpty) {
                next = new SpannableString(getString(R.string.EventLogEmojiNone));
            } else {
                next = new SpannableString("e");
                next.setSpan(new AnimatedEmojiSpan(DialogObject.getEmojiStatusDocumentId(action.new_value), Theme.chat_actionTextPaint.getFontMetricsInt()), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            SpannableStringBuilder ssb = new SpannableStringBuilder(getString(
                prevNone ? (
                    until != 0 ? (isChannel ? R.string.EventLogChangedEmojiStatusFor : R.string.EventLogChangedEmojiStatusForGroup) : (isChannel ? R.string.EventLogChangedEmojiStatus : R.string.EventLogChangedEmojiStatusGroup)
                ) : (
                    until != 0 ? (isChannel ? R.string.EventLogChangedEmojiStatusFromFor : R.string.EventLogChangedEmojiStatusFromForGroup) : (isChannel ? R.string.EventLogChangedEmojiStatusFrom : R.string.EventLogChangedEmojiStatusFromGroup)
                )
            ));

            ssb = AndroidUtilities.replaceCharSequence("%1$s", ssb, prev);
            ssb = AndroidUtilities.replaceCharSequence("%2$s", ssb, next);
            if (until != 0) {
                final String untilString = LocaleController.formatTTLString((int) ((until - event.date) * 1.05f));
                ssb = AndroidUtilities.replaceCharSequence("%3$s", ssb, untilString);
            }

            messageText = replaceWithLink(ssb, "un1", fromUser);
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeWallpaper) {
            TLRPC.TL_channelAdminLogEventActionChangeWallpaper action = (TLRPC.TL_channelAdminLogEventActionChangeWallpaper) event.action;
            boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
            if (action.new_value instanceof TLRPC.TL_wallPaperNoFile && action.new_value.id == 0 && action.new_value.settings == null) {
                messageText = replaceWithLink(getString(isChannel ? R.string.EventLogRemovedWallpaper : R.string.EventLogRemovedWallpaperGroup), "un1", fromUser);
            } else {
                photoThumbs = new ArrayList<>();
                if (action.new_value.document != null) {
                    photoThumbs.addAll(action.new_value.document.thumbs);
                    photoThumbsObject = action.new_value.document;
                }
                messageText = replaceWithLink(getString(isChannel ? R.string.EventLogChangedWallpaper : R.string.EventLogChangedWallpaperGroup), "un1", fromUser);
            }
        } else if (event.action instanceof TLRPC.TL_channelAdminLogEventActionChangeBackgroundEmoji) {
            boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
            TLRPC.TL_channelAdminLogEventActionChangeBackgroundEmoji action = (TLRPC.TL_channelAdminLogEventActionChangeBackgroundEmoji) event.action;
            messageText = replaceWithLink(getString(isChannel ? R.string.EventLogChangedEmoji : R.string.EventLogChangedEmojiGroup), "un1", fromUser);

            SpannableString emoji1;
            if (action.prev_value == 0) {
                emoji1 = new SpannableString(getString(R.string.EventLogEmojiNone));
            } else {
                emoji1 = new SpannableString("e");
                emoji1.setSpan(new AnimatedEmojiSpan(action.prev_value, Theme.chat_actionTextPaint.getFontMetricsInt()), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            messageText = AndroidUtilities.replaceCharSequence("%1$s", messageText, emoji1);

            SpannableString emoji2;
            if (action.new_value == 0) {
                emoji2 = new SpannableString(getString(R.string.EventLogEmojiNone));
            } else {
                emoji2 = new SpannableString("e");
                emoji2.setSpan(new AnimatedEmojiSpan(action.new_value, Theme.chat_actionTextPaint.getFontMetricsInt()), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            messageText = AndroidUtilities.replaceCharSequence("%2$s", messageText, emoji2);
        } else {
            messageText = "unsupported " + event.action;
        }

        if (messageOwner == null) {
            messageOwner = new TLRPC.TL_messageService();
        }
        messageOwner.message = messageText.toString();
        messageOwner.from_id = new TLRPC.TL_peerUser();
        messageOwner.from_id.user_id = event.user_id;
        messageOwner.date = event.date;
        messageOwner.id = mid[0]++;
        eventId = event.id;
        messageOwner.out = false;
        messageOwner.peer_id = new TLRPC.TL_peerChannel();
        messageOwner.peer_id.channel_id = chat.id;
        messageOwner.unread = false;
        MediaController mediaController = MediaController.getInstance();
        isOutOwnerCached = null;

        if (message instanceof TLRPC.TL_messageEmpty) {
            message = null;
        }

        if (message != null) {
            message.out = false;
            message.realId = message.id;
            message.id = mid[0]++;
            message.flags = message.flags & ~TLRPC.MESSAGE_FLAG_EDITED;
            message.dialog_id = -chat.id;
            int realDate = 0;
            if (event.action instanceof TLRPC.TL_channelAdminLogEventActionDeleteMessage) {
                realDate = message.date;
                message.date = event.date;
            }
            MessageObject messageObject = new MessageObject(currentAccount, message, null, null, true, true, eventId);
            messageObject.realDate = realDate;
            messageObject.currentEvent = event;
            if (messageObject.contentType >= 0) {
                if (mediaController.isPlayingMessage(messageObject)) {
                    MessageObject player = mediaController.getPlayingMessageObject();
                    messageObject.audioProgress = player.audioProgress;
                    messageObject.audioProgressSec = player.audioProgressSec;
                }
                createDateArray(currentAccount, event, messageObjects, messagesByDays, addToEnd);
                if (addToEnd) {
                    messageObjects.add(0, messageObject);
                } else {
                    messageObjects.add(messageObjects.size() - 1, messageObject);
                }
            } else {
                contentType = -1;
            }
            if (webPageDescriptionEntities != null) {
                messageObject.webPageDescriptionEntities = webPageDescriptionEntities;
                messageObject.linkDescription = null;
                messageObject.generateLinkDescription();
            }
        }
        if (event.action instanceof TLRPC.TL_channelAdminLogEventActionDeleteMessage) {
            return;
        }
        if (contentType >= 0) {
            createDateArray(currentAccount, event, messageObjects, messagesByDays, addToEnd);
            if (addToEnd) {
                messageObjects.add(0, this);
            } else {
                messageObjects.add(messageObjects.size() - 1, this);
            }
        } else {
            return;
        }

        if (messageText == null) {
            messageText = "";
        }

        TextPaint paint;
        if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGame) {
            paint = Theme.chat_msgGameTextPaint;
        } else {
            paint = Theme.chat_msgTextPaint;
        }

        int[] emojiOnly = allowsBigEmoji() ? new int[1] : null;
        messageText = Emoji.replaceEmoji(messageText, paint.getFontMetricsInt(), false, emojiOnly);
        messageText = replaceAnimatedEmoji(messageText, paint.getFontMetricsInt());
        if (emojiOnly != null && emojiOnly[0] > 1) {
            replaceEmojiToLottieFrame(messageText, emojiOnly);
        }
        checkEmojiOnly(emojiOnly);

        setType();
        measureInlineBotButtons();
        generateCaption();

        if (mediaController.isPlayingMessage(this)) {
            MessageObject player = mediaController.getPlayingMessageObject();
            audioProgress = player.audioProgress;
            audioProgressSec = player.audioProgressSec;
        }
        generateLayout(fromUser);
        layoutCreated = true;
        generateThumbs(false);
        checkMediaExistance();
    }

    private boolean spoiledLoginCode = false;
    private static Pattern loginCodePattern;
    public void spoilLoginCode() { // spoil login code from +42777
        if (!spoiledLoginCode && messageText != null && messageOwner != null && messageOwner.entities != null && messageOwner.from_id instanceof TLRPC.TL_peerUser && (messageOwner.from_id.user_id == 777000 || messageOwner.from_id.user_id == UserObject.VERIFY)) {
            if (loginCodePattern == null) {
                loginCodePattern = Pattern.compile("[\\d\\-]{5,8}");
            }
            try {
                Matcher matcher = loginCodePattern.matcher(messageText);
                if (matcher.find()) {
                    TLRPC.TL_messageEntitySpoiler spoiler = new TLRPC.TL_messageEntitySpoiler();
                    spoiler.offset = matcher.start();
                    spoiler.length = matcher.end() - spoiler.offset;
                    messageOwner.entities.add(spoiler);
                }
            } catch (Exception e) {
                FileLog.e(e, false);
            }
            spoiledLoginCode = true;
        }
    }

    public boolean didSpoilLoginCode() {
        return spoiledLoginCode;
    }

    private CharSequence getStringFrom(TLRPC.ChatReactions reactions) {
        if (reactions instanceof TLRPC.TL_chatReactionsAll) {
            return getString(R.string.AllReactions);
        }
        if (reactions instanceof TLRPC.TL_chatReactionsSome) {
            TLRPC.TL_chatReactionsSome reactionsSome = (TLRPC.TL_chatReactionsSome) reactions;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            for (int i = 0; i < reactionsSome.reactions.size(); i++) {
                if (i != 0) {
                    spannableStringBuilder.append(" ");
                }
                CharSequence reaction = ReactionsUtils.reactionToCharSequence(reactionsSome.reactions.get(i));
                spannableStringBuilder.append(Emoji.replaceEmoji(reaction, null, false));
            }
            return spannableStringBuilder;
        }
        return getString(R.string.NoReactions);
    }

    private String getUsernamesString(ArrayList<String> usernames) {
        if (usernames == null || usernames.size() == 0) {
            return getString(R.string.UsernameEmpty).toLowerCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < usernames.size(); ++i) {
            sb.append("@");
            sb.append(usernames.get(i));
            if (i < usernames.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String getUserName(TLObject object, ArrayList<TLRPC.MessageEntity> entities, int offset) {
        String name;
        String username;
        long id;
        if (object == null) {
            name = "";
            username = null;
            id = 0;
        } else if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            if (user.deleted) {
                name = getString(R.string.HiddenName);
            } else {
                name = ContactsController.formatName(user.first_name, user.last_name);
            }
            username = UserObject.getPublicUsername(user);
            id = user.id;
        } else {
            TLRPC.Chat chat = (TLRPC.Chat) object;
            name = chat.title;
            username = ChatObject.getPublicUsername(chat);
            id = -chat.id;
        }
        if (offset >= 0) {
            TLRPC.TL_messageEntityMentionName entity = new TLRPC.TL_messageEntityMentionName();
            entity.user_id = id;
            entity.offset = offset;
            entity.length = name.length();
            entities.add(entity);
        }
        if (!TextUtils.isEmpty(username)) {
            if (offset >= 0) {
                TLRPC.TL_messageEntityMentionName entity = new TLRPC.TL_messageEntityMentionName();
                entity.user_id = id;
                entity.offset = offset + name.length() + 2;
                entity.length = username.length() + 1;
                entities.add(entity);
            }
            return String.format("%1$s (@%2$s)", name, username);
        }
        return name;
    }

    public boolean updateTranslation() {
        return updateTranslation(false);
    }

    public boolean translated = false;
    public boolean summarized = false;
    public boolean updateTranslation(boolean force) {
        boolean replyUpdated = replyMessageObject != null && replyMessageObject != this && replyMessageObject.updateTranslation(force);
        TranslateController translateController = MessagesController.getInstance(currentAccount).getTranslateController();
        final TLRPC.TL_textWithEntities translatedText = messageOwner != null ? (messageOwner.voiceTranscriptionOpen ? messageOwner.translatedVoiceTranscription : messageOwner.translatedText) : null;
        final TLRPC.TL_textWithEntities summarizedText = messageOwner != null && messageOwner.summarizedOpen ? messageOwner.summaryText : null;
        final TLRPC.TL_textWithEntities summarizeTranslatedText = messageOwner != null && messageOwner.summarizedOpen ? messageOwner.translatedSummaryText : null;
        if (
            summarizeTranslatedText != null &&
            messageOwner != null &&
            messageOwner.summarizedOpen &&
            TranslateController.isSummarizable(this) &&
            TranslateController.isTranslatable(this) &&
            translateController.isTranslatingDialog(getDialogId()) &&
            !translateController.isTranslateDialogHidden(getDialogId()) &&
            TextUtils.equals(translateController.getDialogTranslateTo(getDialogId()), messageOwner.translatedSummaryLanguage)
        ) {
            if (summarized && translated) {
                return replyUpdated || false;
            }
            summarized = true;
            translated = true;
            applyNewText(summarizeTranslatedText.text);
            generateCaption();
            return replyUpdated || true;
        } else if (
            messageOwner != null &&
            messageOwner.summarizedOpen &&
            TranslateController.isSummarizable(this) &&
            summarizedText != null
        ) {
            if (summarized && !translated) {
                return replyUpdated || false;
            }
            summarized = true;
            translated = false;
            applyNewText(summarizedText.text);
            generateCaption();
            return replyUpdated || true;
        } else if (
            messageOwner != null &&
            TranslateController.isTranslatable(this) &&
            translateController.isTranslatingDialog(getDialogId()) &&
            !translateController.isTranslateDialogHidden(getDialogId()) &&
            (translatedText != null || messageOwner.translatedPoll != null || messageOwner.translatedRichMessage != null) &&
            TextUtils.equals(translateController.getDialogTranslateTo(getDialogId()), messageOwner.translatedToLanguage)
        ) {
            if (translated && !summarized) {
                return replyUpdated || false;
            }
            translated = true;
            summarized = false;
            if (type == TYPE_ARTICLE) {
                generateLayout(null);
            } else if (translatedText != null) {
                applyNewText(translatedText.text);
                generateCaption();
            }
            return replyUpdated || true;
        } else if (messageOwner != null && (force || translated || summarized)) {
            translated = false;
            summarized = false;
            if (type == TYPE_ARTICLE) {
                generateLayout(null);
            } else {
                applyNewText(messageOwner.message);
                generateCaption();
            }
            return replyUpdated || true;
        }
        return replyUpdated || false;
    }

    public void applyNewText() {
        translated = false;
        summarized = false;
        applyNewText(messageOwner.message);
    }

    public void applyNewText(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        TLRPC.User fromUser = null;
        if (isFromUser()) {
            fromUser = MessagesController.getInstance(currentAccount).getUser(messageOwner.from_id.user_id);
        }
        messageText = text;
        final ArrayList<TLRPC.MessageEntity> entities = getEntities();
        final TextPaint paint;
        if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGame) {
            paint = Theme.chat_msgGameTextPaint;
        } else {
            paint = Theme.chat_msgTextPaint;
        }
        int[] emojiOnly = allowsBigEmoji() ? new int[1] : null;
        messageText = Emoji.replaceEmoji(messageText, paint.getFontMetricsInt(), false, emojiOnly);
        messageText = replaceAnimatedEmoji(messageText, entities, paint.getFontMetricsInt());
        if (emojiOnly != null && emojiOnly[0] > 1) {
            replaceEmojiToLottieFrame(messageText, emojiOnly);
        }
        checkEmojiOnly(emojiOnly);
        generateLayout(fromUser);
        setType();
    }

    private boolean allowsBigEmoji() {
        if (!SharedConfig.allowBigEmoji) {
            return false;
        }
        if (messageOwner == null || messageOwner.peer_id == null || messageOwner.peer_id.channel_id == 0 && messageOwner.peer_id.chat_id == 0) {
            return true;
        }
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.peer_id.channel_id != 0 ? messageOwner.peer_id.channel_id : messageOwner.peer_id.chat_id);
        return chat != null && chat.gigagroup || (!ChatObject.isActionBanned(chat, ChatObject.ACTION_SEND_STICKERS) || ChatObject.hasAdminRights(chat));
    }

    public void generateGameMessageText(TLRPC.User fromUser) {
        if (fromUser == null && isFromUser()) {
            fromUser = MessagesController.getInstance(currentAccount).getUser(messageOwner.from_id.user_id);
        }
        TLRPC.TL_game game = null;
        if (replyMessageObject != null && getMedia(replyMessageObject) != null && getMedia(replyMessageObject).game != null) {
            game = getMedia(replyMessageObject).game;
        }
        if (game == null) {
            if (fromUser != null && fromUser.id == UserConfig.getInstance(currentAccount).getClientUserId()) {
                messageText = formatString("ActionYouScored", R.string.ActionYouScored, LocaleController.formatPluralString("Points", messageOwner.action.score));
            } else {
                messageText = replaceWithLink(formatString("ActionUserScored", R.string.ActionUserScored, LocaleController.formatPluralString("Points", messageOwner.action.score)), "un1", fromUser);
            }
        } else {
            if (fromUser != null && fromUser.id == UserConfig.getInstance(currentAccount).getClientUserId()) {
                messageText = formatString("ActionYouScoredInGame", R.string.ActionYouScoredInGame, LocaleController.formatPluralString("Points", messageOwner.action.score));
            } else {
                messageText = replaceWithLink(formatString("ActionUserScoredInGame", R.string.ActionUserScoredInGame, LocaleController.formatPluralString("Points", messageOwner.action.score)), "un1", fromUser);
            }
            messageText = replaceWithLink(messageText, "un2", game);
        }
    }

    public boolean hasValidReplyMessageObject() {
        if (messageOwner != null && messageOwner.reply_to != null && messageOwner.reply_to.forum_topic && messageOwner.reply_to.reply_to_msg_id == messageOwner.reply_to.reply_to_top_id) {
            return false;
        }
        return !(replyMessageObject == null || replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty || replyMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear || replyMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionTopicCreate);
    }

    @Deprecated
    public void generateSuggestionApprovalMessageText() {
        return 0;
    }

    private TLRPC.Photo getPhotoWithId(TLRPC.WebPage webPage, long id) {
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (webPage.photo != null && webPage.photo.id == id) {
            return webPage.photo;
        }
        for (int a = 0; a < webPage.cached_page.photos.size(); a++) {
            TLRPC.Photo photo = webPage.cached_page.photos.get(a);
            if (photo.id == id) {
                return photo;
            }
        }
        return null;
    }

    private TLRPC.Document getDocumentWithId(TLRPC.WebPage webPage, long id) {
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (webPage.document != null && webPage.document.id == id) {
            return webPage.document;
        }
        for (int a = 0; a < webPage.cached_page.documents.size(); a++) {
            TLRPC.Document document = webPage.cached_page.documents.get(a);
            if (document.id == id) {
                return document;
            }
        }
        return null;
    }

    public boolean isSupergroup() {
        if (localSupergroup) {
            return true;
        }
        if (cachedIsSupergroup != null) {
            return cachedIsSupergroup;
        }
        if (messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0) {
            TLRPC.Chat chat = getChat(null, null, messageOwner.peer_id.channel_id);
            if (chat != null) {
                return (cachedIsSupergroup = chat.megagroup);
            } else {
                return false;
            }
        } else {
            cachedIsSupergroup = false;
        }
        return false;
    }

    private MessageObject getMessageObjectForBlock(TLRPC.WebPage webPage, TL_iv.PageBlock pageBlock) {
        TLRPC.TL_message message = null;
        if (pageBlock instanceof TL_iv.pageBlockPhoto) {
            TL_iv.pageBlockPhoto pageBlockPhoto = (TL_iv.pageBlockPhoto) pageBlock;
            TLRPC.Photo photo = getPhotoWithId(webPage, pageBlockPhoto.photo_id);
            if (photo == webPage.photo) {
                return this;
            }
            message = new TLRPC.TL_message();
            message.media = new TLRPC.TL_messageMediaPhoto();
            message.media.photo = photo;
        } else if (pageBlock instanceof TL_iv.pageBlockVideo) {
            TL_iv.pageBlockVideo pageBlockVideo = (TL_iv.pageBlockVideo) pageBlock;
            TLRPC.Document document = getDocumentWithId(webPage, pageBlockVideo.video_id);
            if (document == webPage.document) {
                return this;
            }
            message = new TLRPC.TL_message();
            message.media = new TLRPC.TL_messageMediaDocument();
            message.media.document = getDocumentWithId(webPage, pageBlockVideo.video_id);
        }
        message.message = "";
        message.realId = getId();
        message.id = Utilities.random.nextInt();
        message.date = messageOwner.date;
        message.peer_id = messageOwner.peer_id;
        message.out = messageOwner.out;
        message.from_id = messageOwner.from_id;
        return new MessageObject(currentAccount, message, false, true);
    }

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> array, ArrayList<TL_iv.PageBlock> blocksToSearch) {
        ArrayList<MessageObject> messageObjects = array == null ? new ArrayList<>() : array;
        if (getMedia(messageOwner) == null || getMedia(messageOwner).webpage == null) {
            return messageObjects;
        }
        TLRPC.WebPage webPage = getMedia(messageOwner).webpage;
        if (webPage.cached_page == null) {
            return messageObjects;
        }
        ArrayList<TL_iv.PageBlock> blocks = blocksToSearch == null ? webPage.cached_page.blocks : blocksToSearch;
        for (int a = 0; a < blocks.size(); a++) {
            TL_iv.PageBlock block = blocks.get(a);
            if (block instanceof TL_iv.pageBlockSlideshow) {
                TL_iv.pageBlockSlideshow slideshow = (TL_iv.pageBlockSlideshow) block;
                for (int b = 0; b < slideshow.items.size(); b++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, slideshow.items.get(b)));
                }
            } else if (block instanceof TL_iv.pageBlockCollage) {
                TL_iv.pageBlockCollage slideshow = (TL_iv.pageBlockCollage) block;
                for (int b = 0; b < slideshow.items.size(); b++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, slideshow.items.get(b)));
                }
            }
        }
        return messageObjects;
    }

    public void createMessageSendInfo() {
        createMessageSendInfo(false);
    }
    public void createMessageSendInfo(boolean force) {
        boolean notReadyYet = videoEditedInfo != null && videoEditedInfo.notReadyYet;
        if (messageOwner.message != null && (messageOwner.id < 0 || isEditing()) && messageOwner.params != null) {
            String param;

            final TLRPC.MessageMedia messageMedia = getMedia(this);
            boolean allowVideoEditedInfo = force || isVideo() || isNewGif() || isRoundVideo() || isVideoSticker() || isPaidVideo(messageMedia);

            if (!allowVideoEditedInfo && messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                int pollIndex = PollAttachedMediaPack.INDEX_NONE;
                String pollIndexStr = messageOwner.params.get("pollMediaIndex");
                if (pollIndexStr != null) {
                    try {
                        pollIndex = Integer.parseInt(pollIndexStr);
                    } catch (Throwable t) {}
                }
                TLRPC.MessageMedia pollMessageMedia = PollAttachedMediaPack.getMedia(((TLRPC.TL_messageMediaPoll) messageMedia), pollIndex);
                if (pollMessageMedia != null && isVideoDocument(pollMessageMedia.document)) {
                    allowVideoEditedInfo = true;
                }
            }

            if ((param = messageOwner.params.get("ve")) != null && allowVideoEditedInfo) {
                videoEditedInfo = new VideoEditedInfo();
                if (!videoEditedInfo.parseString(param)) {
                    videoEditedInfo = null;
                } else {
                    videoEditedInfo.roundVideo = isRoundVideo();
                    videoEditedInfo.notReadyYet = notReadyYet;
                }
            }
            if (messageOwner.send_state == MESSAGE_SEND_STATE_EDITING && (param = messageOwner.params.get("prevMedia")) != null) {
                SerializedData serializedData = new SerializedData(Base64.decode(param, Base64.DEFAULT));
                int constructor = serializedData.readInt32(false);
                previousMedia = TLRPC.MessageMedia.TLdeserialize(serializedData, constructor, false);
                previousMessage = serializedData.readString(false);
                previousAttachPath = serializedData.readString(false);
                int count = serializedData.readInt32(false);
                previousMessageEntities = new ArrayList<>(count);
                for (int a = 0; a < count; a++) {
                    constructor = serializedData.readInt32(false);
                    TLRPC.MessageEntity entity = TLRPC.MessageEntity.TLdeserialize(serializedData, constructor, false);
                    previousMessageEntities.add(entity);
                }
                serializedData.cleanup();
            }
        }
    }

    public static boolean isPaidVideo(TLRPC.MessageMedia m) {
        return m instanceof TLRPC.TL_messageMediaPaidMedia && m.extended_media.size() == 1 && isExtendedVideo(m.extended_media.get(0));
    }

    public static boolean isExtendedVideo(TLRPC.MessageExtendedMedia em) {
        if (em instanceof TLRPC.TL_messageExtendedMedia) {
            TLRPC.TL_messageExtendedMedia eem = (TLRPC.TL_messageExtendedMedia) em;
            return eem.media instanceof TLRPC.TL_messageMediaDocument && isVideoDocument(eem.media.document);
        } else if (em instanceof TLRPC.TL_messageExtendedMediaPreview) {
            TLRPC.TL_messageExtendedMediaPreview eem = (TLRPC.TL_messageExtendedMediaPreview) em;
            return (eem.flags & 4) != 0;
        }
        return false;
    }

    public boolean hasSuggestionInlineButtons() {
        boolean needShow = messageOwner != null
            && messageOwner.suggested_post != null
            && !messageOwner.suggested_post.rejected
            && !messageOwner.suggested_post.accepted
            && !isSendError()
            && !isSending();

        if (needShow) {
            final long selfId = UserConfig.getInstance(currentAccount).getClientUserId();
            final long topicId = DialogObject.getPeerDialogId(messageOwner.saved_peer_id);
            final long senderId = DialogObject.getPeerDialogId(messageOwner.from_id);

            final boolean isUser = selfId == topicId;
            final boolean isAdmin = !isUser;
            final boolean messageFromUser = topicId == senderId;
            final boolean messageFromAdmin = !messageFromUser;

            if (isUser && messageFromUser || isAdmin && messageFromAdmin) {
                needShow = false;
            }
        }

        return needShow;
    }

    public BotInlineKeyboard.Source getInlineBotButtons() {
        return inlineKeyboardSource;
    }

    public boolean hasInlineBotButtons() {
        return !isRestrictedMessage && !isRepostPreview && messageOwner != null && (messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup && !messageOwner.reply_markup.rows.isEmpty() || getInlineBotButtons() != null);
    }

    public void measureInlineBotButtons() {
        if (isRestrictedMessage) {
            return;
        }
        wantedBotKeyboardWidth = 0;

        inlineKeyboardSource = null;

        BotInlineKeyboard.Builder builder = new BotInlineKeyboard.Builder();

        if (messageOwner != null && messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup && messageOwner.reply_markup.rows != null) {
            builder.addBotKeyboard((TLRPC.TL_replyInlineMarkup) messageOwner.reply_markup);
        }
        if (hasSuggestionInlineButtons()) {
            builder.addSeparator();
            builder.addSuggestionKeyboard();
        }
        if (builder.isNotEmpty()) {
            inlineKeyboardSource = builder.build();
        }


        final BotInlineKeyboard.Source inlineKeyboard = inlineKeyboardSource;


        if (inlineKeyboard != null && !hasExtendedMedia() || messageOwner.reactions != null && !messageOwner.reactions.results.isEmpty()) {
            Theme.createCommonMessageResources();
            if (botButtonsLayout == null) {
                botButtonsLayout = new StringBuilder();
            } else {
                botButtonsLayout.setLength(0);
            }
        }

        if (inlineKeyboard != null && !hasExtendedMedia()) {
            for (int a = 0; a < inlineKeyboard.getRowsCount(); a++) {
                int maxButtonSize = 0;
                int size = inlineKeyboard.getColumnsCount(a);
                for (int b = 0; b < size; b++) {
                    BotInlineKeyboard.Button button = inlineKeyboard.getButton(a, b);
                    botButtonsLayout.append(a).append(b);
                    CharSequence text;
                    if ((button instanceof BotInlineKeyboard.ButtonBot) && ((BotInlineKeyboard.ButtonBot) button).button instanceof TLRPC.TL_keyboardButtonBuy && (getMedia(messageOwner).flags & 4) != 0) {
                        text = getString(R.string.PaymentReceipt);
                    } else {
                        String str = button.getText();
                        if (str == null) {
                            str = "";
                        }
                        text = Emoji.replaceEmoji(str, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), false);
                    }
                    StaticLayout staticLayout = new StaticLayout(text, Theme.chat_msgBotButtonPaint, dp(2000), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout.getLineCount() > 0) {
                        float width = staticLayout.getLineWidth(0);
                        float left = staticLayout.getLineLeft(0);
                        if (left < width) {
                            width -= left;
                        }
                        if (button.getIconRes() != 0) {
                            width += dp(24 + 12);
                        }
                        if (button.getIconEmoji() != 0) {
                            width += dp(24 + 12);
                        }
                        maxButtonSize = Math.max(maxButtonSize, (int) Math.ceil(width) + dp(4));
                    }
                }
                wantedBotKeyboardWidth = Math.max(wantedBotKeyboardWidth, (maxButtonSize + dp(12)) * size + dp(5) * (size - 1));
            }
        }
    }

    public boolean isVideoAvatar() {
        return messageOwner.action != null && messageOwner.action.photo != null && !messageOwner.action.photo.video_sizes.isEmpty();
    }

    public boolean isFcmMessage() {
        return localType != 0;
    }

    private TLRPC.User getUser(AbstractMap<Long, TLRPC.User> users, LongSparseArray<TLRPC.User> sUsers, long userId) {
        TLRPC.User user = null;
        if (users != null) {
            user = users.get(userId);
        } else if (sUsers != null) {
            user = sUsers.get(userId);
        }
        if (user == null) {
            user = MessagesController.getInstance(currentAccount).getUser(userId);
        }
        return user;
    }

    private TLRPC.Chat getChat(AbstractMap<Long, TLRPC.Chat> chats, LongSparseArray<TLRPC.Chat> sChats, long chatId) {
        TLRPC.Chat chat = null;
        if (chats != null) {
            chat = chats.get(chatId);
        } else if (sChats != null) {
            chat = sChats.get(chatId);
        }
        if (chat == null) {
            chat = MessagesController.getInstance(currentAccount).getChat(chatId);
        }
        return chat;
    }

    public void updateMessageText() {
        updateMessageText(MessagesController.getInstance(currentAccount).getUsers(), MessagesController.getInstance(currentAccount).getChats(), null, null);
    }

    private void updateMessageText(AbstractMap<Long, TLRPC.User> users, AbstractMap<Long, TLRPC.Chat> chats, LongSparseArray<TLRPC.User> sUsers, LongSparseArray<TLRPC.Chat> sChats) {
        TLRPC.User fromUser = null;
        TLRPC.Chat fromChat = null;
        if (messageOwner.from_id instanceof TLRPC.TL_peerUser) {
            fromUser = getUser(users, sUsers, messageOwner.from_id.user_id);
        } else if (messageOwner.from_id instanceof TLRPC.TL_peerChannel) {
            fromChat = getChat(chats, sChats, messageOwner.from_id.channel_id);
        }
        TLObject fromObject = fromUser != null ? fromUser : fromChat;
        drawServiceWithDefaultTypeface = false;

        channelJoined = false;
        if (messageOwner instanceof TLRPC.TL_messageService) {
            if (messageOwner.action != null) {
                if (messageOwner.action instanceof TLRPC.TL_messageActionNewCreatorPending) {
                    final TLRPC.TL_messageActionNewCreatorPending action = (TLRPC.TL_messageActionNewCreatorPending) messageOwner.action;
                    final TLRPC.User new_creator = getUser(users, sUsers, action.new_creator_id);
                    messageText = getString(R.string.ActionNewCreatorPending);
                    messageText = replaceWithLink(messageText, "un1", new_creator);
                    messageText = replaceWithLink(messageText, "un2", fromObject);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChangeCreator) {
                    final TLRPC.TL_messageActionChangeCreator action = (TLRPC.TL_messageActionChangeCreator) messageOwner.action;
                    final TLRPC.User new_creator = getUser(users, sUsers, action.new_creator_id);
                    messageText = getString(R.string.ActionChangeCreator);
                    messageText = replaceWithLink(messageText, "un1", fromObject);
                    messageText = replaceWithLink(messageText, "un2", new_creator);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionManagedBotCreated) {
                    final TLRPC.TL_messageActionManagedBotCreated action = (TLRPC.TL_messageActionManagedBotCreated) messageOwner.action;
                    final TLRPC.User bot = getUser(users, sUsers, action.bot_id);
                    final TLRPC.User toBot = getUser(users, sUsers, messageOwner.peer_id.user_id);
                    messageText = getString(R.string.ActionManagedBotCreated);
                    messageText = replaceWithLink(messageText, "un1", bot);
                    messageText = replaceWithLink(messageText, "un2", toBot);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionSetSameChatWallPaper) {
                    contentType = 1;
                    type = TYPE_DATE;
                    TLRPC.TL_messageActionSetSameChatWallPaper action = (TLRPC.TL_messageActionSetSameChatWallPaper) messageOwner.action;
                    TLRPC.User user = getUser(users, sUsers, isOutOwner() ? 0 : getDialogId());
                    photoThumbs = new ArrayList<>();
                    if (action.wallpaper.document != null) {
                        photoThumbs.addAll(action.wallpaper.document.thumbs);
                        photoThumbsObject = action.wallpaper.document;
                    }
                    if (user != null) {
                        if (user.id == UserConfig.getInstance(currentAccount).clientUserId) {
                            messageText = formatString(R.string.ActionSetSameWallpaperForThisChatSelf);
                        } else {
                            messageText = formatString(R.string.ActionSetSameWallpaperForThisChat, user.first_name);
                        }
                    } else if (fromChat != null) {
                        messageText = getString(ChatObject.isChannelAndNotMegaGroup(fromChat) ? R.string.ActionSetWallpaperForThisChannel : R.string.ActionSetWallpaperForThisGroup);
                    } else if (fromUser != null) {
                        messageText = formatString(R.string.ActionSetWallpaperForThisGroupByUser, UserObject.getFirstName(fromUser));
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionSetChatWallPaper) {
                    contentType = 1;
                    TLRPC.TL_messageActionSetChatWallPaper wallPaper = (TLRPC.TL_messageActionSetChatWallPaper) messageOwner.action;
                    type = TYPE_ACTION_WALLPAPER;
                    photoThumbs = new ArrayList<>();
                    if (wallPaper.wallpaper.document != null) {
                        photoThumbs.addAll(wallPaper.wallpaper.document.thumbs);
                        photoThumbsObject = wallPaper.wallpaper.document;
                    }
                    TLRPC.User user = getUser(users, sUsers, isOutOwner() ? 0 : getDialogId());
                    TLRPC.User partner = getUser(users, sUsers, getDialogId());
                    if (user != null) {
                        if (user.id == UserConfig.getInstance(currentAccount).clientUserId) {
                            if (wallPaper.same) {
                                type = TYPE_DATE;
                                messageText = formatString(R.string.ActionSetSameWallpaperForThisChatSelf);
                            } else if (wallPaper.for_both && partner != null) {
                                messageText = getString(R.string.ActionSetWallpaperForThisChatSelfBoth);
                                CharSequence partnerName = new SpannableString(UserObject.getFirstName(partner));
                                ((SpannableString) partnerName).setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, partnerName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                messageText = AndroidUtilities.replaceCharSequence("%s", messageText, partnerName);
                            } else {
                                messageText = getString(R.string.ActionSetWallpaperForThisChatSelf);
                            }
                        } else {
                            CharSequence userName = new SpannableString(UserObject.getFirstName(user));
                            ((SpannableString) userName).setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (wallPaper.same) {
                                type = TYPE_DATE;
                                messageText = getString(R.string.ActionSetSameWallpaperForThisChat);
                            } else if (wallPaper.for_both) {
                                messageText = getString(R.string.ActionSetWallpaperForThisChatBoth);
                            } else {
                                messageText = getString(R.string.ActionSetWallpaperForThisChat);
                            }
                            messageText = AndroidUtilities.replaceCharSequence("%s", messageText, userName);
                        }
                    } else if (fromChat != null) {
                        messageText = getString(ChatObject.isChannelAndNotMegaGroup(fromChat) ? R.string.ActionSetWallpaperForThisChannel : R.string.ActionSetWallpaperForThisGroup);
                    } else if (fromUser != null) {
                        messageText = formatString(R.string.ActionSetWallpaperForThisGroupByUser, UserObject.getFirstName(fromUser));
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGroupCallScheduled) {
                    TLRPC.TL_messageActionGroupCallScheduled action = (TLRPC.TL_messageActionGroupCallScheduled) messageOwner.action;
                    if (messageOwner.peer_id instanceof TLRPC.TL_peerChat || isSupergroup()) {
                        messageText = formatString(R.string.ActionGroupCallScheduled, LocaleController.formatStartsTime(action.schedule_date, 3, false));
                    } else {
                        messageText = formatString(R.string.ActionChannelCallScheduled, LocaleController.formatStartsTime(action.schedule_date, 3, false));
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGroupCall) {
                    if (messageOwner.action.duration != 0) {
                        String time;
                        int days = messageOwner.action.duration / (3600 * 24);
                        if (days > 0) {
                            time = formatPluralString("Days", days);
                        } else {
                            int hours = messageOwner.action.duration / 3600;
                            if (hours > 0) {
                                time = formatPluralString("Hours", hours);
                            } else {
                                int minutes = messageOwner.action.duration / 60;
                                if (minutes > 0) {
                                    time = formatPluralString("Minutes", minutes);
                                } else {
                                    time = formatPluralString("Seconds", messageOwner.action.duration);
                                }
                            }
                        }

                        if (messageOwner.peer_id instanceof TLRPC.TL_peerChat || isSupergroup()) {
                            if (isOut()) {
                                messageText = formatString(R.string.ActionGroupCallEndedByYou, time);
                            } else {
                                messageText = replaceWithLink(formatString(R.string.ActionGroupCallEndedBy, time), "un1", fromObject);
                            }
                        } else {
                            messageText = formatString(R.string.ActionChannelCallEnded, time);
                        }
                    } else {
                        if (messageOwner.peer_id instanceof TLRPC.TL_peerChat || isSupergroup()) {
                            if (isOut()) {
                                messageText = getString(R.string.ActionGroupCallStartedByYou);
                            } else {
                                messageText = replaceWithLink(getString(R.string.ActionGroupCallStarted), "un1", fromObject);
                            }
                        } else {
                            messageText = getString(R.string.ActionChannelCallJustStarted);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionInviteToGroupCall) {
                    long singleUserId = messageOwner.action.user_id;
                    if (singleUserId == 0 && messageOwner.action.users.size() == 1) {
                        singleUserId = messageOwner.action.users.get(0);
                    }
                    if (singleUserId != 0) {
                        TLRPC.User whoUser = getUser(users, sUsers, singleUserId);

                        if (isOut()) {
                            messageText = replaceWithLink(getString(R.string.ActionGroupCallYouInvited), "un2", whoUser);
                        } else if (singleUserId == UserConfig.getInstance(currentAccount).getClientUserId()) {
                            messageText = replaceWithLink(getString(R.string.ActionGroupCallInvitedYou), "un1", fromObject);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionGroupCallInvited), "un2", whoUser);
                            messageText = replaceWithLink(messageText, "un1", fromObject);
                        }
                    } else {
                        if (isOut()) {
                            messageText = replaceWithLink(getString(R.string.ActionGroupCallYouInvited), "un2", messageOwner.action.users, users, sUsers);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionGroupCallInvited), "un2", messageOwner.action.users, users, sUsers);
                            messageText = replaceWithLink(messageText, "un1", fromObject);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGeoProximityReached) {
                    TLRPC.TL_messageActionGeoProximityReached action = (TLRPC.TL_messageActionGeoProximityReached) messageOwner.action;
                    long fromId = getPeerId(action.from_id);
                    TLObject from;
                    if (fromId > 0) {
                        from = getUser(users, sUsers, fromId);
                    } else {
                        from = getChat(chats, sChats, -fromId);
                    }
                    long toId = getPeerId(action.to_id);
                    long selfUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                    if (toId == selfUserId) {
                        messageText = replaceWithLink(formatString(R.string.ActionUserWithinRadius, LocaleController.formatDistance(action.distance, 2)), "un1", from);
                    } else {
                        TLObject to;
                        if (toId > 0) {
                            to = getUser(users, sUsers, toId);
                        } else {
                            to = getChat(chats, sChats, -toId);
                        }
                        if (fromId == selfUserId) {
                            messageText = replaceWithLink(formatString(R.string.ActionUserWithinYouRadius, LocaleController.formatDistance(action.distance, 2)), "un1", to);
                        } else {
                            messageText = replaceWithLink(formatString(R.string.ActionUserWithinOtherRadius, LocaleController.formatDistance(action.distance, 2)), "un2", to);
                            messageText = replaceWithLink(messageText, "un1", from);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionCustomAction) {
                    messageText = messageOwner.action.message;
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatCreate) {
                    if (isOut()) {
                        messageText = getString(R.string.ActionYouCreateGroup);
                    } else {
                        messageText = replaceWithLink(getString(R.string.ActionCreateGroup), "un1", fromObject);
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser) {
                    if (isFromUser() && messageOwner.action.user_id == messageOwner.from_id.user_id) {
                        if (isOut()) {
                            messageText = getString(R.string.ActionYouLeftUser);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionLeftUser), "un1", fromObject);
                        }
                    } else {
                        TLRPC.User whoUser = getUser(users, sUsers, messageOwner.action.user_id);
                        if (isOut()) {
                            messageText = replaceWithLink(getString(R.string.ActionYouKickUser), "un2", whoUser);
                        } else if (messageOwner.action.user_id == UserConfig.getInstance(currentAccount).getClientUserId()) {
                            messageText = replaceWithLink(getString(R.string.ActionKickUserYou), "un1", fromObject);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionKickUser), "un2", whoUser);
                            messageText = replaceWithLink(messageText, "un1", fromObject);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionPaymentRefunded) {
                    TLRPC.TL_messageActionPaymentRefunded action = (TLRPC.TL_messageActionPaymentRefunded) messageOwner.action;
                    long did = DialogObject.getPeerDialogId(action.peer);
                    TLObject who;
                    if (did >= 0) {
                        who = getUser(users, sUsers, did);
                    } else {
                        who = getChat(chats, sChats, -did);
                    }
                    messageText = StarsIntroActivity.replaceStars(replaceWithLink(formatString(R.string.ActionRefunded, action.currency + " " + LocaleController.formatNumber(action.total_amount, ',')), "un1", who));
                } else if (TlUtils.isInstance(messageOwner.action, TLRPC.TL_messageActionSuggestedPostRefund.class, TLRPC.TL_messageActionSuggestedPostSuccess.class)) {
                    String channelName = ForumUtilities.getMonoForumTitle(currentAccount, DialogObject.getPeerDialogId(messageOwner.peer_id), true);
                    if (channelName == null) {
                        TLRPC.Chat chat = getChat(chats, sChats, -DialogObject.getPeerDialogId(messageOwner.peer_id));
                        if (chat != null && chat.linked_monoforum_id != 0) {
                            TLRPC.Chat chat2 = getChat(chats, sChats, chat.linked_monoforum_id);
                            if (chat2 != null) {
                                chat = chat2;
                            }
                        }

                        channelName = DialogObject.getDialogTitle(chat);
                    }
                    final String userName = DialogObject.getName(getUser(users, sUsers, DialogObject.getPeerDialogId(messageOwner.saved_peer_id)));
                    final MessageSuggestionParams sp = obtainSuggestionOfferFromReply();

                    if (messageOwner.action instanceof TLRPC.TL_messageActionSuggestedPostRefund) {
                        final boolean refundByUser = ((TLRPC.TL_messageActionSuggestedPostRefund) messageOwner.action).payer_initiated;
                        if (sp != null && sp.amount != null) {
                            final int key = refundByUser ? R.string.SuggestedOfferRefundByUserAmountF : R.string.SuggestedOfferRefundByAdminAmountF;
                            messageText = StarsIntroActivity.replaceStars(sp.amount.currency == AmountUtils.Currency.TON,
                                LocaleController.formatString(key, userName, channelName, sp.amount.asDecimalString()));
                        } else {
                            final int key = refundByUser ?
                                R.string.SuggestedOfferRefundByUserAmountUnknown :
                                R.string.SuggestedOfferRefundByAdminAmountUnknown;

                            messageText = LocaleController.formatString(key, userName, channelName);
                        }
                    } else if (messageOwner.action instanceof TLRPC.TL_messageActionSuggestedPostSuccess) {
                        if (sp != null && sp.amount != null) {
                            messageText = StarsIntroActivity.replaceStars(sp.amount.currency == AmountUtils.Currency.TON,
                                LocaleController.formatString(R.string.SuggestedOfferCompleteAmountF, channelName, sp.amount.asDecimalString()));
                        } else {
                            messageText = LocaleController.formatString(R.string.SuggestedOfferCompleteAmountUnknown, channelName);
                        }
                    }

                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser) {
                    long singleUserId = messageOwner.action.user_id;
                    if (singleUserId == 0 && messageOwner.action.users.size() == 1) {
                        singleUserId = messageOwner.action.users.get(0);
                    }
                    if (singleUserId != 0) {
                        TLRPC.User whoUser = getUser(users, sUsers, singleUserId);
                        TLRPC.Chat chat = null;
                        if (messageOwner.peer_id.channel_id != 0) {
                            chat = getChat(chats, sChats, messageOwner.peer_id.channel_id);
                        }
                        if (messageOwner.from_id != null && singleUserId == messageOwner.from_id.user_id) {
                            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                                channelJoined = true;
                                messageText = getString(R.string.ChannelJoined);
                            } else {
                                if (messageOwner.peer_id.channel_id != 0) {
                                    if (singleUserId == UserConfig.getInstance(currentAccount).getClientUserId()) {
                                        messageText = getString(R.string.ChannelMegaJoined);
                                    } else {
                                        messageText = replaceWithLink(getString(R.string.ActionAddUserSelfMega), "un1", fromObject);
                                    }
                                } else if (isOut()) {
                                    messageText = getString(R.string.ActionAddUserSelfYou);
                                } else {
                                    messageText = replaceWithLink(getString(R.string.ActionAddUserSelf), "un1", fromObject);
                                }
                            }
                        } else {
                            if (isOut()) {
                                messageText = replaceWithLink(getString(R.string.ActionYouAddUser), "un2", whoUser);
                            } else if (singleUserId == UserConfig.getInstance(currentAccount).getClientUserId()) {
                                if (messageOwner.peer_id.channel_id != 0) {
                                    if (chat != null && chat.megagroup) {
                                        messageText = replaceWithLink(getString(R.string.MegaAddedBy), "un1", fromObject);
                                    } else {
                                        messageText = replaceWithLink(getString(R.string.ChannelAddedBy), "un1", fromObject);
                                    }
                                } else {
                                    messageText = replaceWithLink(getString(R.string.ActionAddUserYou), "un1", fromObject);
                                }
                            } else {
                                messageText = replaceWithLink(getString(R.string.ActionAddUser), "un2", whoUser);
                                messageText = replaceWithLink(messageText, "un1", fromObject);
                            }
                        }
                    } else {
                        if (isOut()) {
                            messageText = replaceWithLink(getString(R.string.ActionYouAddUser), "un2", messageOwner.action.users, users, sUsers);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionAddUser), "un2", messageOwner.action.users, users, sUsers);
                            messageText = replaceWithLink(messageText, "un1", fromObject);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByLink) {
                    if (isOut()) {
                        messageText = getString(R.string.ActionInviteYou);
                    } else {
                        messageText = replaceWithLink(getString(R.string.ActionInviteUser), "un1", fromObject);
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGiveawayLaunch) {
                    TLRPC.TL_messageActionGiveawayLaunch giveawayLaunch = (TLRPC.TL_messageActionGiveawayLaunch) messageOwner.action;
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
                    boolean isStars = (giveawayLaunch.flags & 1) != 0;
                    if (isStars) {
                        messageText = formatPluralStringComma(isChannel ? "BoostingStarsGiveawayJustStarted" : "BoostingStarsGiveawayJustStartedGroup", (int) giveawayLaunch.stars, chat != null ? chat.title : "");
                    } else {
                        messageText = formatString(isChannel ? R.string.BoostingGiveawayJustStarted : R.string.BoostingGiveawayJustStartedGroup, chat != null ? chat.title : "");
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionBoostApply) {
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
                    TLRPC.TL_messageActionBoostApply messageActionBoostApply = (TLRPC.TL_messageActionBoostApply) messageOwner.action;
                    String name = "";
                    boolean self = false;
                    if (fromObject instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) fromObject;
                        self = UserObject.isUserSelf(user);
                        name = UserObject.getFirstName(user);
                    } else if (fromObject instanceof TLRPC.Chat) {
                        name = ((TLRPC.Chat) fromObject).title;
                    }
                    if (self) {
                        if (messageActionBoostApply.boosts <= 1) {
                            messageText = getString(isChannel ? R.string.BoostingBoostsChannelByYouServiceMsg : R.string.BoostingBoostsGroupByYouServiceMsg);
                        } else {
                            messageText = LocaleController.formatPluralString(isChannel ? "BoostingBoostsChannelByYouServiceMsgCount" : "BoostingBoostsGroupByYouServiceMsgCount", messageActionBoostApply.boosts);
                        }
                    } else {
                        if (messageActionBoostApply.boosts <= 1) {
                            messageText = formatString(isChannel ? R.string.BoostingBoostsChannelByUserServiceMsg : R.string.BoostingBoostsGroupByUserServiceMsg, name);
                        } else {
                            messageText = LocaleController.formatPluralString(isChannel ? "BoostingBoostsChannelByUserServiceMsgCount" : "BoostingBoostsGroupByUserServiceMsgCount", messageActionBoostApply.boosts, name);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGiveawayResults) {
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    boolean isChannel = ChatObject.isChannelAndNotMegaGroup(chat);
                    TLRPC.TL_messageActionGiveawayResults giveawayResults = (TLRPC.TL_messageActionGiveawayResults) messageOwner.action;
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                    if (giveawayResults.stars) {
                        stringBuilder.append(formatPluralStringComma("BoostingStarsGiveawayServiceWinnersSelected", giveawayResults.winners_count));
                        if (giveawayResults.unclaimed_count > 0) {
                            stringBuilder.append("\n");
                            stringBuilder.append(LocaleController.formatPluralString(isChannel ? "BoostingStarsGiveawayServiceUndistributed" : "BoostingStarsGiveawayServiceUndistributedGroup", giveawayResults.unclaimed_count));
                        }
                    } else {
                        stringBuilder.append(LocaleController.formatPluralString("BoostingGiveawayServiceWinnersSelected", giveawayResults.winners_count));
                        if (giveawayResults.unclaimed_count > 0) {
                            stringBuilder.append("\n");
                            stringBuilder.append(LocaleController.formatPluralString(isChannel ? "BoostingGiveawayServiceUndistributed" : "BoostingGiveawayServiceUndistributedGroup", giveawayResults.unclaimed_count));
                        }
                    }
                    messageText = stringBuilder;
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionPrizeStars) {
                    final TLRPC.TL_messageActionPrizeStars action = (TLRPC.TL_messageActionPrizeStars) messageOwner.action;
                    final long chatId = -DialogObject.getPeerDialogId(action.boost_peer);
                    final TLRPC.Chat chat = getChat(chats, sChats, chatId);
                    messageText = replaceWithLink(AndroidUtilities.replaceTags(formatPluralStringComma("ActionStarGiveawayPrize", (int) action.stars)), "un1", chat);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionStarGift) {
                    final TLRPC.TL_messageActionStarGift action = (TLRPC.TL_messageActionStarGift) messageOwner.action;
                    int stars = 0;
                    if (action.prepaid_upgrade) {
                        stars += (int) action.upgrade_stars;
                    } else {
                        if (action.gift != null) {
                            stars = (int) action.gift.stars;
                        }
                        if (!action.upgrade_separate) {
                            stars += (int) action.upgrade_stars;
                        }
                    }
//                    if (action.can_upgrade && action.upgrade_stars == 0) {
//                    }
                    final boolean isForChannel = action.peer != null && DialogObject.getPeerDialogId(action.peer) < 0;
                    TLRPC.User user = getUser(users, sUsers, messageOwner.peer_id.user_id);
                    TLObject obj = fromObject;
                    if (!action.prepaid_upgrade && action.from_id != null) {
                        final long fromId = DialogObject.getPeerDialogId(action.from_id);
                        if (fromId >= 0) {
                            obj = getUser(users, sUsers, fromId);
                        } else {
                            obj = getChat(chats, sChats, -fromId);
                        }
                    }
                    TLObject peerObj = null;
                    if (action.peer != null) {
                        final long peerId = DialogObject.getPeerDialogId(action.peer);
                        if (peerId >= 0) {
                            peerObj = getUser(users, sUsers, peerId);
                        } else {
                            peerObj = getChat(chats, sChats, -peerId);
                        }
                    }
                    if (action.prepaid_upgrade) {
                        if (obj instanceof TLRPC.User && ((TLRPC.User) obj).self && !action.forceIn) {
                            messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionPrepaidGiftOutbound)), "un1", user);
                        } else {
                            messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionPrepaidGiftInbound)), "un1", obj);
                        }
                    } else if (UserObject.isService(getDialogId()) && action.from_id == null) {
                        messageText = AndroidUtilities.replaceTags(getString(action.auction_acquired ? R.string.ActionGiftAuctionSelf : R.string.ActionGiftSomeone));
                        messageTextShort = getString(R.string.ActionStarGift);
                    } else if (isForChannel) {
                        messageText = AndroidUtilities.replaceTags(formatPluralStringComma("ActionGiftChannel", stars));
                        messageText = replaceWithLink(messageText, "un1", obj);
                        messageText = replaceWithLink(messageText, "un2", peerObj);
                        messageTextShort = getString(R.string.ActionStarGift);
                    } else if (UserObject.isUserSelf(user)) {
                        messageText = AndroidUtilities.replaceTags(getString(action.auction_acquired ? R.string.ActionGiftAuctionSelf : R.string.ActionGiftSelf));
                        messageTextShort = getString(R.string.ActionStarGift);
                    } else if (obj instanceof TLRPC.User && ((TLRPC.User) obj).self && !action.forceIn) {
                        messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionGiftOutbound)), "un1", user);
                        if (action.message != null && !TextUtils.isEmpty(action.message.text)) {
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(action.message.text);
                            addEntitiesToText(stringBuilder, action.message.entities, isOutOwner(), false, false, false);
                            messageTextShort = stringBuilder;
                        } else {
                            messageTextShort = getString(R.string.ActionStarGift);
                        }
                    } else if (obj instanceof TLRPC.User && UserObject.isService(((TLRPC.User) obj).id)) {
                        messageText = TextUtils.replace(AndroidUtilities.replaceTags(getString(R.string.ActionGiftInbound)), new String[] {"un1"}, new CharSequence[]{ getString(R.string.StarsTransactionUnknown) });
                    } else {
                        messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionGiftInbound)), "un1", obj);
                        if (action.message != null && !TextUtils.isEmpty(action.message.text)) {
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(action.message.text);
                            addEntitiesToText(stringBuilder, action.message.entities, isOutOwner(), false, false, false);
                            messageTextShort = stringBuilder;
                        } else {
                            messageTextShort = getString(R.string.ActionStarGift);
                        }
                    }
                    int i = messageText.toString().indexOf("un2");
                    if (i != -1) {
                        SpannableStringBuilder sb = SpannableStringBuilder.valueOf(messageText);
                        messageText = sb.replace(i, i + 3, formatPluralStringComma("Gift2StarsCount", (int) stars));
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionStarGiftUnique) {
                    TLRPC.TL_messageActionStarGiftUnique action = (TLRPC.TL_messageActionStarGiftUnique) messageOwner.action;
                    TLRPC.User user = getUser(users, sUsers, getDialogId());
                    if (action.resale_amount != null) {
                        final AmountUtils.Amount amount = AmountUtils.Amount.ofSafe(action.resale_amount);
                        long fromId = getDialogId();
                        if (action.from_id != null) {
                            fromId = DialogObject.getPeerDialogId(action.from_id);
                        }
                        TLObject obj;
                        if (fromId >= 0) {
                            obj = getUser(users, sUsers, fromId);
                        } else {
                            obj = getChat(chats, sChats, -fromId);
                        }
                        if (action.craft) {
                            messageText = AndroidUtilities.replaceTags(getString(R.string.ActionUniqueGiftCrafted));
                        } else if (action.peer != null) {
                            long peerId = DialogObject.getPeerDialogId(action.peer);
                            TLObject peer;
                            if (peerId >= 0) {
                                peer = getUser(users, sUsers, peerId);
                            } else {
                                peer = getChat(chats, sChats, -peerId);
                            }
                            if (amount.currency == AmountUtils.Currency.TON) {
                                messageText = AndroidUtilities.replaceTags(LocaleController.formatString(R.string.ActionUniqueGiftResaleServiceTON, amount.asFormatString()));
                            } else {
                                messageText = AndroidUtilities.replaceTags(formatPluralStringComma("ActionUniqueGiftResaleService", (int) amount.asDecimal()));
                            }
                            messageText = replaceWithLink(messageText, "un1", obj);
                            messageText = replaceWithLink(messageText, "un2", peer);
                        } else if (action.from_offer) {
                            if (amount.currency == AmountUtils.Currency.TON) {
                                messageText = AndroidUtilities.replaceTags(formatString(isOutOwner() ? R.string.ActionUniqueGiftResaleSoldOutboundTON : R.string.ActionUniqueGiftResaleOutboundTON, amount.asFormatString()));
                            } else {
                                messageText = AndroidUtilities.replaceTags(formatPluralStringComma(isOutOwner() ? "ActionUniqueGiftResaleSoldOutbound" : "ActionUniqueGiftResaleOutbound", (int) amount.asDecimal()));
                            }
                        } else {
                            if (amount.currency == AmountUtils.Currency.TON) {
                                messageText = replaceWithLink(AndroidUtilities.replaceTags(formatString(isOutOwner() ? R.string.ActionUniqueGiftResaleOutboundTON : R.string.ActionUniqueGiftResaleInboundTON, amount.asFormatString())), "un1", obj);
                            } else {
                                messageText = replaceWithLink(AndroidUtilities.replaceTags(formatPluralStringComma(isOutOwner() ? "ActionUniqueGiftResaleOutbound" : "ActionUniqueGiftResaleInbound", (int) amount.asDecimal())), "un1", obj);
                            }
                        }
                    } else if (action.upgrade) {
                        if (action.peer != null) {
                            long peerId = DialogObject.getPeerDialogId(action.peer);
                            TLObject peer;
                            if (peerId >= 0) {
                                peer = getUser(users, sUsers, peerId);
                            } else {
                                peer = getChat(chats, sChats, -peerId);
                            }
                            messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionUniqueGiftUpgradeInboundChannel)), "un1", peer);
                        } else if (UserObject.isUserSelf(user)) {
                            messageText = AndroidUtilities.replaceTags(getString(R.string.ActionUniqueGiftUpgradeSelf));
                        } else {
                            messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(isOutOwner() ? R.string.ActionUniqueGiftUpgradeOutbound : R.string.ActionUniqueGiftUpgradeInbound)), "un1", user);
                        }
                    } else {
                        long fromId = getDialogId();
                        if (action.from_id != null) {
                            fromId = DialogObject.getPeerDialogId(action.from_id);
                        }
                        TLObject obj;
                        if (fromId >= 0) {
                            obj = getUser(users, sUsers, fromId);
                        } else {
                            obj = getChat(chats, sChats, -fromId);
                        }
                        if (action.craft) {
                            messageText = AndroidUtilities.replaceTags(getString(R.string.ActionUniqueGiftCrafted));
                        } else if (action.peer != null) {
                            long peerId = DialogObject.getPeerDialogId(action.peer);
                            TLObject peer;
                            if (peerId >= 0) {
                                peer = getUser(users, sUsers, peerId);
                            } else {
                                peer = getChat(chats, sChats, -peerId);
                            }
                            messageText = AndroidUtilities.replaceTags(getString(R.string.ActionUniqueGiftTransferService));
                            messageText = replaceWithLink(messageText, "un1", obj);
                            messageText = replaceWithLink(messageText, "un2", peer);
                        } else if (action.assigned) {
                            String giftName = action.gift.title + " #" + LocaleController.formatNumber(action.gift.num, ',');
                            messageText = replaceWithLink(AndroidUtilities.replaceTags(formatString(R.string.ActionUniqueGiftTransferOutboundAssigned, giftName)), "un1", obj);
                        } else {
                            messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(isOutOwner() ? R.string.ActionUniqueGiftTransferOutbound : R.string.ActionUniqueGiftTransferInbound)), "un1", obj);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGiftStars) {
                    if (fromObject instanceof TLRPC.User && ((TLRPC.User) fromObject).self) {
                        TLRPC.User user = getUser(users, sUsers, messageOwner.peer_id.user_id);
                        messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionGiftOutbound)), "un1", user);
                    } else if (fromObject instanceof TLRPC.User && UserObject.isService(((TLRPC.User) fromObject).id)) {
                        messageText = TextUtils.replace(AndroidUtilities.replaceTags(getString(R.string.ActionGiftInbound)), new String[] {"un1"}, new CharSequence[]{ getString(R.string.StarsTransactionUnknown) });
                    } else {
                        messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionGiftInbound)), "un1", fromObject);
                    }
                    int i = messageText.toString().indexOf("un2");
                    if (i != -1) {
                        SpannableStringBuilder sb = SpannableStringBuilder.valueOf(messageText);
                        CharSequence price = BillingController.getInstance().formatCurrency(messageOwner.action.amount, messageOwner.action.currency);
                        if ((messageOwner.action.flags & 1) != 0) {
                            price = String.format("%.2f", (messageOwner.action.cryptoAmount * Math.pow(10, -9))) + " " + messageOwner.action.cryptoCurrency + " (~ " + price + ")";
                        }
                        messageText = sb.replace(i, i + 3, price);
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGiftCode && ((TLRPC.TL_messageActionGiftCode) messageOwner.action).boost_peer != null) {
                    messageText = getString(R.string.BoostingReceivedGiftNoName);
                } else if (TlUtils.isInstance(messageOwner.action, TLRPC.TL_messageActionGiftPremium.class, TLRPC.TL_messageActionGiftCode.class, TLRPC.TL_messageActionGiftTon.class)) {
                    if (fromObject instanceof TLRPC.User && ((TLRPC.User) fromObject).self) {
                        TLRPC.User user = getUser(users, sUsers, messageOwner.peer_id.user_id);
                        messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionGiftOutbound)), "un1", user);
                    } else if (messageOwner.action instanceof TLRPC.TL_messageActionGiftTon) {
                        messageText = AndroidUtilities.replaceTags(getString(R.string.ActionGiftTonInbound));
                    } else {
                        messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionGiftInbound)), "un1", fromObject);
                    }
                    int i = messageText.toString().indexOf("un2");
                    if (i != -1) {
                        SpannableStringBuilder sb = SpannableStringBuilder.valueOf(messageText);
                        CharSequence price = BillingController.getInstance().formatCurrency(messageOwner.action.amount, messageOwner.action.currency);
                        if ((messageOwner.action.flags & 1) != 0) {
                            price = String.format("%.2f", (messageOwner.action.cryptoAmount * Math.pow(10, -9))) + " " + messageOwner.action.cryptoCurrency + " (~ " + price + ")";
                        }
                        messageText = sb.replace(i, i + 3, price);
                    }
                    messageText = StarsIntroActivity.replaceStars(messageText);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionSuggestBirthday) {
                    if (isOutOwner()) {
                        messageText = getString(R.string.ActionYouSuggestBirthday);
                    } else {
                        messageText = replaceWithLink(AndroidUtilities.replaceTags(getString(R.string.ActionSuggestBirthday)), "un1", fromObject);
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionSuggestProfilePhoto) {
                    if (messageOwner.action.photo != null && messageOwner.action.photo.video_sizes != null && !messageOwner.action.photo.video_sizes.isEmpty()) {
                        messageText = getString(R.string.ActionSuggestVideoShort);
                    } else {
                        messageText = getString(R.string.ActionSuggestPhotoShort);
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto) {
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        if (isVideoAvatar()) {
                            messageText = getString(R.string.ActionChannelChangedVideo);
                        } else {
                            messageText = getString(R.string.ActionChannelChangedPhoto);
                        }
                    } else {
                        if (isOut()) {
                            if (isVideoAvatar()) {
                                messageText = getString(R.string.ActionYouChangedVideo);
                            } else {
                                messageText = getString(R.string.ActionYouChangedPhoto);
                            }
                        } else {
                            if (isVideoAvatar()) {
                                messageText = replaceWithLink(getString(R.string.ActionChangedVideo), "un1", fromObject);
                            } else {
                                messageText = replaceWithLink(getString(R.string.ActionChangedPhoto), "un1", fromObject);
                            }
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatEditTitle) {
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        messageText = getString(R.string.ActionChannelChangedTitle).replace("un2", messageOwner.action.title);
                    } else {
                        if (isOut()) {
                            messageText = getString(R.string.ActionYouChangedTitle).replace("un2", messageOwner.action.title);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionChangedTitle).replace("un2", messageOwner.action.title), "un1", fromObject);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatDeletePhoto) {
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        messageText = getString(R.string.ActionChannelRemovedPhoto);
                    } else {
                        if (isOut()) {
                            messageText = getString(R.string.ActionYouRemovedPhoto);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionRemovedPhoto), "un1", fromObject);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionTTLChange) {
                    if (messageOwner.action.ttl != 0) {
                        if (isOut()) {
                            messageText = formatString(R.string.MessageLifetimeChangedOutgoing, LocaleController.formatTTLString(messageOwner.action.ttl));
                        } else {
                            messageText = formatString(R.string.MessageLifetimeChanged, UserObject.getFirstName(fromUser), LocaleController.formatTTLString(messageOwner.action.ttl));
                        }
                    } else {
                        if (isOut()) {
                            messageText = getString(R.string.MessageLifetimeYouRemoved);
                        } else {
                            messageText = formatString(R.string.MessageLifetimeRemoved, UserObject.getFirstName(fromUser));
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionRequestedPeer) {
                    List<TLObject> peerObjects = new ArrayList<>();
                    int sharedUsers = 0;
                    int sharedChannels = 0;
                    int sharedChats = 0;
                    List<TLRPC.Peer> peers = ((TLRPC.TL_messageActionRequestedPeer) messageOwner.action).peers;
                    for (TLRPC.Peer peer : peers) {
                        TLObject peerObject = null;
                        if (peer instanceof TLRPC.TL_peerUser) {
                            peerObject = MessagesController.getInstance(currentAccount).getUser(peer.user_id);
                            if (peerObject == null) {
                                peerObject = getUser(users, sUsers, peer.user_id);
                            }
                        } else if (peer instanceof TLRPC.TL_peerChat) {
                            peerObject = MessagesController.getInstance(currentAccount).getChat(peer.chat_id);
                            if (peerObject == null) {
                                peerObject = getChat(chats, sChats, peer.chat_id);
                            }
                        } else if (peer instanceof TLRPC.TL_peerChannel) {
                            peerObject = MessagesController.getInstance(currentAccount).getChat(peer.channel_id);
                            if (peerObject == null) {
                                peerObject = getChat(chats, sChats, peer.channel_id);
                            }
                        }
                        if (peer instanceof TLRPC.TL_peerUser) {
                            sharedUsers++;
                        } else if (peer instanceof TLRPC.TL_peerChat) {
                            sharedChats++;
                        } else {
                            sharedChannels++;
                        }
                        if (peerObject != null) {
                            peerObjects.add(peerObject);
                        }
                    }
                    if (sharedUsers > 0 && sharedUsers != peerObjects.size()) {
                        messageText = LocaleController.getPluralString("ActionRequestedPeerUserPlural", sharedUsers);
                    } else if (sharedChannels > 0 && sharedChannels != peerObjects.size()) {
                        messageText = LocaleController.getPluralString("ActionRequestedPeerChannelPlural", sharedChannels);
                    } else if (sharedChats > 0 && sharedChats != peerObjects.size()) {
                        messageText = LocaleController.getPluralString("ActionRequestedPeerChatPlural", sharedChats);
                    } else {
                        String separator = ", ";
                        SpannableStringBuilder names = new SpannableStringBuilder();
                        for (int i = 0; i < peerObjects.size(); i++) {
                            names.append(replaceWithLink("un1", "un1", peerObjects.get(i)));
                            if (i < peerObjects.size() - 1) {
                                names.append(separator);
                            }
                        }
                        messageText = AndroidUtilities.replaceCharSequence("un1", getString(R.string.ActionRequestedPeer), names);
                    }
                    TLRPC.User bot = MessagesController.getInstance(currentAccount).getUser(getDialogId());
                    if (bot == null) {
                        bot = getUser(users, sUsers, getDialogId());
                    }
                    messageText = replaceWithLink(messageText, "un2", bot);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionSetMessagesTTL) {
                    TLRPC.TL_messageActionSetMessagesTTL action = (TLRPC.TL_messageActionSetMessagesTTL) messageOwner.action;
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    if (chat != null && !chat.megagroup) {
                        if (action.period != 0) {
                            messageText = formatString(R.string.ActionTTLChannelChanged, LocaleController.formatTTLString(action.period));
                        } else {
                            messageText = getString(R.string.ActionTTLChannelDisabled);
                        }
                    } else if (action.auto_setting_from != 0) {
                        drawServiceWithDefaultTypeface = true;
                        if (action.auto_setting_from == UserConfig.getInstance(currentAccount).clientUserId) {
                            messageText = AndroidUtilities.replaceTags(formatString(R.string.AutoDeleteGlobalActionFromYou, LocaleController.formatTTLString(action.period)));
                        } else {
                            TLObject object = null;
                            if (sUsers != null) {
                                object = sUsers.get(action.auto_setting_from);
                            }
                            if (object == null && users != null) {
                                object = users.get(action.auto_setting_from);
                            }
                            if (object == null && chats != null) {
                                object = chats.get(action.auto_setting_from);
                            }
                            if (object == null) {
                                if (action.auto_setting_from > 0) {
                                    object = MessagesController.getInstance(currentAccount).getUser(action.auto_setting_from);
                                } else {
                                    object = MessagesController.getInstance(currentAccount).getChat(-action.auto_setting_from);
                                }
                            }
                            if (object == null) {
                                object = fromObject;
                            }
                            messageText = replaceWithLink(AndroidUtilities.replaceTags(formatString(R.string.AutoDeleteGlobalAction, LocaleController.formatTTLString(action.period))), "un1", object);
                        }
                    } else if (action.period != 0) {
                        if (isOut()) {
                            messageText = formatString(R.string.ActionTTLYouChanged, LocaleController.formatTTLString(action.period));
                        } else {
                            messageText = replaceWithLink(formatString(R.string.ActionTTLChanged, LocaleController.formatTTLString(action.period)), "un1", fromObject);
                        }
                    } else {
                        if (isOut()) {
                            messageText = getString(R.string.ActionTTLYouDisabled);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionTTLDisabled), "un1", fromObject);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation) {
                    String date;
                    long time = ((long) messageOwner.date) * 1000;
                    if (LocaleController.getInstance().getFormatterDay() != null && LocaleController.getInstance().getFormatterYear() != null) {
                        date = formatString(R.string.formatDateAtTime, LocaleController.getInstance().getFormatterYear().format(time), LocaleController.getInstance().getFormatterDay().format(time));
                    } else {
                        date = "" + messageOwner.date;
                    }
                    TLRPC.User to_user = UserConfig.getInstance(currentAccount).getCurrentUser();
                    if (to_user == null) {
                        to_user = getUser(users, sUsers, messageOwner.peer_id.user_id);
                    }
                    String name = to_user != null ? UserObject.getFirstName(to_user) : "";
                    messageText = formatString(R.string.NotificationUnrecognizedDevice, name, date, messageOwner.action.title, messageOwner.action.address);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionUserJoined || messageOwner.action instanceof TLRPC.TL_messageActionContactSignUp) {
                    messageText = formatString(R.string.NotificationContactJoined, UserObject.getUserName(fromUser));
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    messageText = formatString(R.string.NotificationContactNewPhoto, UserObject.getUserName(fromUser));
                } else if (messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) {
                    if (messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                        if (isOut()) {
                            messageText = getString(R.string.ActionTakeScreenshootYou);
                        } else {
                            messageText = replaceWithLink(getString(R.string.ActionTakeScreenshoot), "un1", fromObject);
                        }
                    } else if (messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                        TLRPC.TL_decryptedMessageActionSetMessageTTL action = (TLRPC.TL_decryptedMessageActionSetMessageTTL) messageOwner.action.encryptedAction;
                        if (action.ttl_seconds != 0) {
                            if (isOut()) {
                                messageText = formatString(R.string.MessageLifetimeChangedOutgoing, LocaleController.formatTTLString(action.ttl_seconds));
                            } else {
                                messageText = formatString(R.string.MessageLifetimeChanged, UserObject.getFirstName(fromUser), LocaleController.formatTTLString(action.ttl_seconds));
                            }
                        } else {
                            if (isOut()) {
                                messageText = getString(R.string.MessageLifetimeYouRemoved);
                            } else {
                                messageText = formatString(R.string.MessageLifetimeRemoved, UserObject.getFirstName(fromUser));
                            }
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionScreenshotTaken) {
                    if (isOut()) {
                        messageText = getString(R.string.ActionTakeScreenshootYou);
                    } else {
                        messageText = replaceWithLink(getString(R.string.ActionTakeScreenshoot), "un1", fromObject);
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionCreatedBroadcastList) {
                    messageText = getString(R.string.YouCreatedBroadcastList);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChannelCreate) {
                    TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(chats, sChats, messageOwner.peer_id.channel_id) : null;
                    if (ChatObject.isChannel(chat) && chat.megagroup) {
                        messageText = getString(R.string.ActionCreateMega);
                    } else {
                        messageText = getString(R.string.ActionCreateChannel);
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo) {
                    messageText = getString(R.string.ActionMigrateFromGroup);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionChannelMigrateFrom) {
                    messageText = getString(R.string.ActionMigrateFromGroup);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionPinMessage) {
                    TLRPC.Chat chat;
                    if (fromUser == null) {
                        chat = getChat(chats, sChats, messageOwner.peer_id.channel_id);
                    } else {
                        chat = null;
                    }
                    generatePinMessageText(fromUser, chat);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear) {
                    messageText = getString(R.string.HistoryCleared);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionTopicCreate) {
                    messageText = getString(R.string.TopicCreated);

                    TLRPC.TL_messageActionTopicCreate createAction = (TLRPC.TL_messageActionTopicCreate) messageOwner.action;
                    TLRPC.TL_forumTopic forumTopic = new TLRPC.TL_forumTopic();
                    forumTopic.icon_emoji_id = createAction.icon_emoji_id;
                    forumTopic.title = createAction.title;
                    forumTopic.icon_color = createAction.icon_color;

                    messageTextShort = AndroidUtilities.replaceCharSequence("%s", getString(R.string.TopicWasCreatedAction), ForumUtilities.getTopicSpannedName(forumTopic, null, false));
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionTopicEdit) {
                    TLRPC.TL_messageActionTopicEdit editAction = (TLRPC.TL_messageActionTopicEdit) messageOwner.action;

                    String name = null;
                    TLObject object = null;
                    if (fromUser != null) {
                        name = ContactsController.formatName(fromUser.first_name, fromUser.last_name);
                        object = fromUser;
                    } else if (fromChat != null) {
                        name = fromChat.title;
                        object = fromChat;
                    }
                    if (name != null) {
                        name = name.trim();
                    } else {
                        name = "DELETED";
                    }

                    if ((messageOwner.action.flags & 8) > 0) {
                        if (((TLRPC.TL_messageActionTopicEdit) messageOwner.action).hidden) {
                            messageText = replaceWithLink(getString(R.string.TopicHidden2), "%s", object);
                            messageTextShort = getString(R.string.TopicHidden);
                        } else {
                            messageText = replaceWithLink(getString(R.string.TopicShown2), "%s", object);
                            messageTextShort = getString(R.string.TopicShown);
                        }
                    } else if ((messageOwner.action.flags & 4) > 0) {
                        if (((TLRPC.TL_messageActionTopicEdit) messageOwner.action).closed) {
                            messageText = replaceWithLink(getString(R.string.TopicClosed2), "%s", object);
                            messageTextShort = getString(R.string.TopicClosed);
                        } else {
                            messageText = replaceWithLink(getString(R.string.TopicRestarted2), "%s", object);
                            messageTextShort = getString(R.string.TopicRestarted);
                        }
                    } else {
                        if ((messageOwner.action.flags & 2) != 0 && (messageOwner.action.flags & 1) != 0) {
                            TLRPC.TL_forumTopic forumTopic = new TLRPC.TL_forumTopic();
                            forumTopic.icon_emoji_id = editAction.icon_emoji_id;
                            forumTopic.title = editAction.title;
                            forumTopic.icon_color = ForumBubbleDrawable.serverSupportedColor[0];

                            CharSequence topicName = ForumUtilities.getTopicSpannedName(forumTopic, null, topicIconDrawable, false);
                            CharSequence str = AndroidUtilities.replaceCharSequence("%1$s", getString(R.string.TopicChangeIconAndTitleTo), name);
                            messageText = AndroidUtilities.replaceCharSequence("%2$s", str,  topicName);
                            messageTextShort = getString(R.string.TopicRenamed);
                            messageTextForReply = AndroidUtilities.replaceCharSequence("%s", getString(R.string.TopicChangeIconAndTitleToInReply), topicName);
                        } else if ((messageOwner.action.flags & 2) != 0) {
                            TLRPC.TL_forumTopic forumTopic = new TLRPC.TL_forumTopic();
                            forumTopic.icon_emoji_id = editAction.icon_emoji_id;
                            forumTopic.title = "";
                            forumTopic.icon_color = ForumBubbleDrawable.serverSupportedColor[0];
                            CharSequence topicName = ForumUtilities.getTopicSpannedName(forumTopic, null, topicIconDrawable, false);
                            CharSequence str = AndroidUtilities.replaceCharSequence("%1$s", getString(R.string.TopicIconChangedTo), name);
                            messageText = AndroidUtilities.replaceCharSequence("%2$s", str, topicName);
                            messageTextShort = getString(R.string.TopicIconChanged);
                            messageTextForReply = AndroidUtilities.replaceCharSequence("%s", getString(R.string.TopicIconChangedToInReply), topicName);
                        } else if ((messageOwner.action.flags & 1) != 0) {
                            CharSequence str = AndroidUtilities.replaceCharSequence("%1$s", getString(R.string.TopicRenamedTo), name);
                            messageText = AndroidUtilities.replaceCharSequence("%2$s", str, editAction.title);
                            messageTextShort = getString(R.string.TopicRenamed);
                            messageTextForReply = AndroidUtilities.replaceCharSequence("%s", getString(R.string.TopicRenamedToInReply), editAction.title);
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionGameScore) {
                    generateGameMessageText(fromUser);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionConferenceCall) {
                    final TLRPC.TL_messageActionConferenceCall call = (TLRPC.TL_messageActionConferenceCall) messageOwner.action;
                    if (isOutOwner()) {
                        messageText = getString(R.string.ConferenceCallOutgoing);
                    } else if (call.missed) {
                        messageText = getString(R.string.ConferenceCallMissed);
                    } else {
                        messageText = getString(R.string.ConferenceCallIncoming);
                    }
                    if (call.duration > 0) {
                        String duration = LocaleController.formatCallDuration(call.duration);
                        messageText = formatString(R.string.CallMessageWithDuration, messageText, duration);
                        String _messageText = messageText.toString();
                        int start = _messageText.indexOf(duration);
                        if (start != -1) {
                            SpannableString sp = new SpannableString(messageText);
                            int end = start + duration.length();
                            if (start > 0 && _messageText.charAt(start - 1) == '(') {
                                start--;
                            }
                            if (end < _messageText.length() && _messageText.charAt(end) == ')') {
                                end++;
                            }
                            sp.setSpan(new TypefaceSpan(Typeface.DEFAULT), start, end, 0);
                            messageText = sp;
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                    final TLRPC.TL_messageActionPhoneCall call = (TLRPC.TL_messageActionPhoneCall) messageOwner.action;
                    boolean isMissed = call.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed;
                    if (isFromUser() && messageOwner.from_id.user_id == UserConfig.getInstance(currentAccount).getClientUserId()) {
                        if (isMissed) {
                            if (call.video) {
                                messageText = getString(R.string.CallMessageVideoOutgoingMissed);
                            } else {
                                messageText = getString(R.string.CallMessageOutgoingMissed);
                            }
                        } else {
                            if (call.video) {
                                messageText = getString(R.string.CallMessageVideoOutgoing);
                            } else {
                                messageText = getString(R.string.CallMessageOutgoing);
                            }
                        }
                    } else {
                        if (isMissed) {
                            if (call.video) {
                                messageText = getString(R.string.CallMessageVideoIncomingMissed);
                            } else {
                                messageText = getString(R.string.CallMessageIncomingMissed);
                            }
                        } else if (call.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
                            if (call.video) {
                                messageText = getString(R.string.CallMessageVideoIncomingDeclined);
                            } else {
                                messageText = getString(R.string.CallMessageIncomingDeclined);
                            }
                        } else {
                            if (call.video) {
                                messageText = getString(R.string.CallMessageVideoIncoming);
                            } else {
                                messageText = getString(R.string.CallMessageIncoming);
                            }
                        }
                    }
                    if (call.duration > 0) {
                        String duration = LocaleController.formatCallDuration(call.duration);
                        messageText = formatString(R.string.CallMessageWithDuration, messageText, duration);
                        String _messageText = messageText.toString();
                        int start = _messageText.indexOf(duration);
                        if (start != -1) {
                            SpannableString sp = new SpannableString(messageText);
                            int end = start + duration.length();
                            if (start > 0 && _messageText.charAt(start - 1) == '(') {
                                start--;
                            }
                            if (end < _messageText.length() && _messageText.charAt(end) == ')') {
                                end++;
                            }
                            sp.setSpan(new TypefaceSpan(Typeface.DEFAULT), start, end, 0);
                            messageText = sp;
                        }
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent) {
                    final TLRPC.User user = getUser(users, sUsers, getDialogId());
                    generatePaymentSentMessageText(user, false);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionPaymentSentMe) {
                    final TLRPC.User user = getUser(users, sUsers, getDialogId());
                    generatePaymentSentMessageText(user, true);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionBotAllowed) {
                    String domain = ((TLRPC.TL_messageActionBotAllowed) messageOwner.action).domain;
                    TLRPC.BotApp botApp = ((TLRPC.TL_messageActionBotAllowed) messageOwner.action).app;
                    if (((TLRPC.TL_messageActionBotAllowed) messageOwner.action).from_request) {
                        messageText = getString(R.string.ActionBotAllowedWebapp);
                    } else if (botApp != null) {
                        String botAppTitle = botApp.title;
                        if (botAppTitle == null) {
                            botAppTitle = "";
                        }
                        String text = getString(R.string.ActionBotAllowedApp);
                        int start = text.indexOf("%1$s");
                        SpannableString str = new SpannableString(String.format(text, botAppTitle));
                        TLRPC.User bot = getUser(users, sUsers, getDialogId());
                        if (start >= 0 && bot != null) {
                            final String username = UserObject.getPublicUsername(bot);
                            if (username != null) {
                                final String link = "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/" + username + "/" + botApp.short_name;
                                str.setSpan(new URLSpanNoUnderlineBold(link), start, start + botAppTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        messageText = str;
                    } else {
                        if (domain == null) {
                            domain = "";
                        }
                        String text = getString(R.string.ActionBotAllowed);
                        int start = text.indexOf("%1$s");
                        SpannableString str = new SpannableString(String.format(text, domain));
                        if (start >= 0 && !TextUtils.isEmpty(domain)) {
                            str.setSpan(new URLSpanNoUnderlineBold("http://" + domain), start, start + domain.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        messageText = str;
                    }
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionAttachMenuBotAllowed || messageOwner.action instanceof TLRPC.TL_messageActionBotAllowed && ((TLRPC.TL_messageActionBotAllowed) messageOwner.action).attach_menu) {
                    messageText = getString(R.string.ActionAttachMenuBotAllowed);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionSecureValuesSent) {
                    TLRPC.TL_messageActionSecureValuesSent valuesSent = (TLRPC.TL_messageActionSecureValuesSent) messageOwner.action;
                    StringBuilder str = new StringBuilder();
                    for (int a = 0, size = valuesSent.types.size(); a < size; a++) {
                        TLRPC.SecureValueType type = valuesSent.types.get(a);
                        if (str.length() > 0) {
                            str.append(", ");
                        }
                        if (type instanceof TLRPC.TL_secureValueTypePhone) {
                            str.append(getString(R.string.ActionBotDocumentPhone));
                        } else if (type instanceof TLRPC.TL_secureValueTypeEmail) {
                            str.append(getString(R.string.ActionBotDocumentEmail));
                        } else if (type instanceof TLRPC.TL_secureValueTypeAddress) {
                            str.append(getString(R.string.ActionBotDocumentAddress));
                        } else if (type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                            str.append(getString(R.string.ActionBotDocumentIdentity));
                        } else if (type instanceof TLRPC.TL_secureValueTypePassport) {
                            str.append(getString(R.string.ActionBotDocumentPassport));
                        } else if (type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                            str.append(getString(R.string.ActionBotDocumentDriverLicence));
                        } else if (type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                            str.append(getString(R.string.ActionBotDocumentIdentityCard));
                        } else if (type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                            str.append(getString(R.string.ActionBotDocumentUtilityBill));
                        } else if (type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                            str.append(getString(R.string.ActionBotDocumentBankStatement));
                        } else if (type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                            str.append(getString(R.string.ActionBotDocumentRentalAgreement));
                        } else if (type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                            str.append(getString(R.string.ActionBotDocumentInternalPassport));
                        } else if (type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                            str.append(getString(R.string.ActionBotDocumentPassportRegistration));
                        } else if (type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                            str.append(getString(R.string.ActionBotDocumentTemporaryRegistration));
                        }
                    }
                    TLRPC.User user = null;
                    if (messageOwner.peer_id != null) {
                        user = getUser(users, sUsers, messageOwner.peer_id.user_id);
                    }
                    messageText = formatString(R.string.ActionBotDocuments, UserObject.getFirstName(user), str.toString());
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionWebViewDataSent) {
                    TLRPC.TL_messageActionWebViewDataSent dataSent = (TLRPC.TL_messageActionWebViewDataSent) messageOwner.action;
                    messageText = formatString(R.string.ActionBotWebViewData, dataSent.text);
                } else if (messageOwner.action instanceof TLRPC.TL_messageActionSetChatTheme) {
                    final TLRPC.ChatTheme actionTheme = ((TLRPC.TL_messageActionSetChatTheme) messageOwner.action).theme;
                    final String title = TlUtils.getThemeEmoticonOrGiftTitle(actionTheme);
                    CharSequence emoticon = title;
                    if (title != null && actionTheme instanceof TLRPC.TL_chatThemeUniqueGift) {
                        final SpannableStringBuilder ssb = new SpannableStringBuilder(title);
                        final TLRPC.Document document = TlUtils.getGiftDocument(((TLRPC.TL_chatThemeUniqueGift) actionTheme).gift);
                        if (document != null) {
                            //ssb.setSpan(new AnimatedEmojiSpan(document, width <= 1280 && height <= 1280) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewGifDocument(TLRPC.Document document) {
        if (document != null && "video/mp4".equals(document.mime_type)) {
            int width = 0;
            int height = 0;
            boolean animated = false;
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                    animated = true;
                } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                    width = attribute.w;
                    height = attribute.h;
                }
            }
            if (animated && width <= 1280 && height <= 1280) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSystemSignUp(MessageObject message) {
        return message != null && message.messageOwner instanceof TLRPC.TL_messageService && ((TLRPC.TL_messageService) message.messageOwner).action instanceof TLRPC.TL_messageActionContactSignUp;
    }

    public void generateThumbs(boolean update) {
        if (hasExtendedMediaPreview()) {
            TLRPC.TL_messageExtendedMediaPreview preview = (TLRPC.TL_messageExtendedMediaPreview) messageOwner.media.extended_media.get(0);
            if (!update) {
                photoThumbs = new ArrayList<>(Collections.singletonList(preview.thumb));
            } else {
                updatePhotoSizeLocations(photoThumbs, Collections.singletonList(preview.thumb));
            }
            photoThumbsObject = messageOwner;

            if (strippedThumb == null) {
                createStrippedThumb();
            }
        } else if (messageOwner instanceof TLRPC.TL_messageService) {
            if (messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto) {
                TLRPC.Photo photo = messageOwner.action.photo;
                if (!update) {
                    photoThumbs = new ArrayList<>(photo.sizes);
                } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
                    for (int a = 0; a < photoThumbs.size(); a++) {
                        TLRPC.PhotoSize photoObject = photoThumbs.get(a);
                        for (int b = 0; b < photo.sizes.size(); b++) {
                            TLRPC.PhotoSize size = photo.sizes.get(b);
                            if (size instanceof TLRPC.TL_photoSizeEmpty) {
                                continue;
                            }
                            if (size.type.equals(photoObject.type)) {
                                photoObject.location = size.location;
                                break;
                            }
                        }
                    }
                }
                if (photo.dc_id != 0 && photoThumbs != null) {
                    for (int a = 0, N = photoThumbs.size(); a < N; a++) {
                        TLRPC.FileLocation location = photoThumbs.get(a).location;
                        if (location == null) {
                            continue;
                        }
                        location.dc_id = photo.dc_id;
                        location.file_reference = photo.file_reference;
                    }
                }
                photoThumbsObject = messageOwner.action.photo;
            }
        } else if (emojiAnimatedSticker != null || emojiAnimatedStickerId != null) {
            if (TextUtils.isEmpty(emojiAnimatedStickerColor) && isDocumentHasThumb(emojiAnimatedSticker)) {
                if (!update || photoThumbs == null) {
                    photoThumbs = new ArrayList<>();
                    photoThumbs.addAll(emojiAnimatedSticker.thumbs);
                } else if (!photoThumbs.isEmpty()) {
                    updatePhotoSizeLocations(photoThumbs, emojiAnimatedSticker.thumbs);
                }
                photoThumbsObject = emojiAnimatedSticker;
            }
        } else if (getMedia(messageOwner) != null && !(getMedia(messageOwner) instanceof TLRPC.TL_messageMediaEmpty)) {
            if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
                TLRPC.Photo photo = getMedia(messageOwner).photo;
                if (!update || photoThumbs != null && photoThumbs.size() != photo.sizes.size()) {
                    photoThumbs = new ArrayList<>(photo.sizes);
                } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
                    for (int a = 0; a < photoThumbs.size(); a++) {
                        TLRPC.PhotoSize photoObject = photoThumbs.get(a);
                        if (photoObject == null) {
                            continue;
                        }
                        for (int b = 0; b < photo.sizes.size(); b++) {
                            TLRPC.PhotoSize size = photo.sizes.get(b);
                            if (size == null || size instanceof TLRPC.TL_photoSizeEmpty) {
                                continue;
                            }
                            if (size.type.equals(photoObject.type)) {
                                photoObject.location = size.location;
                                break;
                            } else if ("s".equals(photoObject.type) && size instanceof TLRPC.TL_photoStrippedSize) {
                                photoThumbs.set(a, size);
                                break;
                            }
                        }
                    }
                }
                photoThumbsObject = getMedia(messageOwner).photo;
            } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.TL_messageMediaDocument mediaDocument = (TLRPC.TL_messageMediaDocument) getMedia(messageOwner);
                if (mediaDocument.video_cover != null) {
                    TLRPC.Photo photo = mediaDocument.video_cover;
                    if (!update || photoThumbs != null && photoThumbs.size() != photo.sizes.size()) {
                        photoThumbs = new ArrayList<>(photo.sizes);
                    } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
                        for (int a = 0; a < photoThumbs.size(); a++) {
                            TLRPC.PhotoSize photoObject = photoThumbs.get(a);
                            if (photoObject == null) {
                                continue;
                            }
                            for (int b = 0; b < photo.sizes.size(); b++) {
                                TLRPC.PhotoSize size = photo.sizes.get(b);
                                if (size == null || size instanceof TLRPC.TL_photoSizeEmpty) {
                                    continue;
                                }
                                if (size.type.equals(photoObject.type)) {
                                    photoObject.location = size.location;
                                    break;
                                } else if ("s".equals(photoObject.type) && size instanceof TLRPC.TL_photoStrippedSize) {
                                    photoThumbs.set(a, size);
                                    break;
                                }
                            }
                        }
                    }
                    photoThumbsObject = photo;
                } else {
                    TLRPC.Document document = getDocument();
                    if (isDocumentHasThumb(document)) {
                        if (!update || photoThumbs == null) {
                            photoThumbs = new ArrayList<>();
                            photoThumbs.addAll(document.thumbs);
                        } else if (!photoThumbs.isEmpty()) {
                            updatePhotoSizeLocations(photoThumbs, document.thumbs);
                        }
                        photoThumbsObject = document;
                    }
                }
            } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGame) {
                TLRPC.Document document = getMedia(messageOwner).game.document;
                if (document != null) {
                    if (isDocumentHasThumb(document)) {
                        if (!update) {
                            photoThumbs = new ArrayList<>();
                            photoThumbs.addAll(document.thumbs);
                        } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
                            updatePhotoSizeLocations(photoThumbs, document.thumbs);
                        }
                        photoThumbsObject = document;
                    }
                }
                TLRPC.Photo photo = getMedia(messageOwner).game.photo;
                if (photo != null) {
                    if (!update || photoThumbs2 == null) {
                        photoThumbs2 = new ArrayList<>(photo.sizes);
                    } else if (!photoThumbs2.isEmpty()) {
                        updatePhotoSizeLocations(photoThumbs2, photo.sizes);
                    }
                    photoThumbsObject2 = photo;
                }
                if (photoThumbs == null && photoThumbs2 != null) {
                    photoThumbs = photoThumbs2;
                    photoThumbs2 = null;
                    photoThumbsObject = photoThumbsObject2;
                    photoThumbsObject2 = null;
                }
            } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage) {
                TLRPC.Photo photo = getMedia(messageOwner).webpage.photo;
                TLRPC.Document document = getMedia(messageOwner).webpage.document;
                if (photo != null) {
                    if (!update || photoThumbs == null) {
                        photoThumbs = new ArrayList<>(photo.sizes);
                    } else if (!photoThumbs.isEmpty()) {
                        updatePhotoSizeLocations(photoThumbs, photo.sizes);
                    }
                    photoThumbsObject = photo;
                } else if (document != null) {
                    if (isDocumentHasThumb(document)) {
                        if (!update) {
                            photoThumbs = new ArrayList<>();
                            photoThumbs.addAll(document.thumbs);
                        } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
                            updatePhotoSizeLocations(photoThumbs, document.thumbs);
                        }
                        photoThumbsObject = document;
                    }
                }
            }
        } else if (messageOwner != null && messageOwner.rich_message != null) {
            final TLRPC.Document video = findVideo(messageOwner.rich_message);
            final TLRPC.Photo photo = findPhoto(messageOwner.rich_message);
            if (video != null) {
                if (isDocumentHasThumb(video)) {
                    if (!update) {
                        photoThumbs = new ArrayList<>();
                        photoThumbs.addAll(video.thumbs);
                    } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
                        updatePhotoSizeLocations(photoThumbs, video.thumbs);
                    }
                    photoThumbsObject = video;
                }
            } else if (photo != null) {
                if (!update || photoThumbs == null) {
                    photoThumbs = new ArrayList<>(photo.sizes);
                } else if (!photoThumbs.isEmpty()) {
                    updatePhotoSizeLocations(photoThumbs, photo.sizes);
                }
                photoThumbsObject = photo;
                if (strippedThumb == null) {
                    createStrippedThumb();
                }
            }
        } else if (sponsoredMedia != null) {
            TLRPC.Photo photo = sponsoredMedia.photo;
            TLRPC.Document document = sponsoredMedia.document;
            if (photo != null) {
                if (!update || photoThumbs == null) {
                    photoThumbs = new ArrayList<>(photo.sizes);
                } else if (!photoThumbs.isEmpty()) {
                    updatePhotoSizeLocations(photoThumbs, photo.sizes);
                }
                photoThumbsObject = photo;
            } else if (document != null) {
                if (isDocumentHasThumb(document)) {
                    if (!update) {
                        photoThumbs = new ArrayList<>();
                        photoThumbs.addAll(document.thumbs);
                    } else if (photoThumbs != null && !photoThumbs.isEmpty()) {
                        updatePhotoSizeLocations(photoThumbs, document.thumbs);
                    }
                    photoThumbsObject = document;
                }
            }
        } else if (sponsoredPhoto != null) {
            if (!update || photoThumbs == null) {
                photoThumbs = new ArrayList<>(sponsoredPhoto.sizes);
            } else if (!photoThumbs.isEmpty()) {
                updatePhotoSizeLocations(photoThumbs, sponsoredPhoto.sizes);
            }
            photoThumbsObject = sponsoredPhoto;
            if (strippedThumb == null) {
                createStrippedThumb();
            }
        }
    }

    private static void updatePhotoSizeLocations(ArrayList<TLRPC.PhotoSize> o, List<TLRPC.PhotoSize> n) {
        for (int a = 0, N = o.size(); a < N; a++) {
            TLRPC.PhotoSize photoObject = o.get(a);
            if (photoObject == null) {
                continue;
            }
            for (int b = 0, N2 = n.size(); b < N2; b++) {
                TLRPC.PhotoSize size = n.get(b);
                if (size instanceof TLRPC.TL_photoSizeEmpty || size instanceof TLRPC.TL_photoCachedSize || size == null) {
                    continue;
                }
                if (size.type.equals(photoObject.type)) {
                    photoObject.location = size.location;
                    break;
                }
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence source, String param, ArrayList<Long> uids, AbstractMap<Long, TLRPC.User> usersDict, LongSparseArray<TLRPC.User> sUsersDict) {
        int start = TextUtils.indexOf(source, param);
        if (start >= 0) {
            SpannableStringBuilder names = new SpannableStringBuilder("");
            for (int a = 0; a < uids.size(); a++) {
                TLRPC.User user = null;
                if (usersDict != null) {
                    user = usersDict.get(uids.get(a));
                } else if (sUsersDict != null) {
                    user = sUsersDict.get(uids.get(a));
                }
                if (user == null) {
                    user = MessagesController.getInstance(currentAccount).getUser(uids.get(a));
                }
                if (user != null) {
                    String name = UserObject.getUserName(user);
                    start = names.length();
                    if (names.length() != 0) {
                        names.append(", ");
                    }
                    names.append(name);
                    names.setSpan(new URLSpanNoUnderlineBold("" + user.id), start, start + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return TextUtils.replace(source, new String[]{param}, new CharSequence[]{names});
        }
        return source;
    }

    public static CharSequence replaceWithLink(CharSequence source, String param, CharSequence object) {
        int start = TextUtils.indexOf(source, param);
        if (start >= 0) {
            return TextUtils.replace(source, new String[]{param}, new CharSequence[]{object});
        }
        return source;
    }

    public static CharSequence replaceWithLink(CharSequence source, String param, TLObject object) {
        int start = TextUtils.indexOf(source, param);
        if (start >= 0) {
            CharSequence name;
            String id;
            TLObject spanObject = null;
            if (object instanceof TLRPC.User) {
                name = UserObject.getUserName((TLRPC.User) object).replace('\n', ' ');
                id = "" + ((TLRPC.User) object).id;
            } else if (object instanceof TLRPC.Chat) {
                name = ((TLRPC.Chat) object).title.replace('\n', ' ');
                id = "" + -((TLRPC.Chat) object).id;
            } else if (object instanceof TLRPC.TL_game) {
                TLRPC.TL_game game = (TLRPC.TL_game) object;
                name = game.title.replace('\n', ' ');
                id = "game";
            } else if (object instanceof TLRPC.TL_chatInviteExported) {
                TLRPC.TL_chatInviteExported invite = (TLRPC.TL_chatInviteExported) object;
                name = invite.link.replace('\n', ' ');
                id = "invite";
                spanObject = invite;
            } else if (object instanceof TLRPC.ForumTopic) {
                name = ForumUtilities.getTopicSpannedName((TLRPC.ForumTopic) object, null, false);
                id = "topic";
                spanObject = object;
            } else {
                name = "";
                id = "0";
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new CharSequence[]{name}));
            URLSpanNoUnderlineBold span = new URLSpanNoUnderlineBold("" + id);
            span.setObject(spanObject);
            builder.setSpan(span, start, start + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }
        return source;
    }

    public String getExtension() {
        String fileName = getFileName();
        int idx = fileName.lastIndexOf('.');
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = getDocument().mime_type;
        }
        if (ext == null) {
            ext = "";
        }
        ext = ext.toUpperCase();
        return ext;
    }

    public String getFileName() {
        if (getDocument() != null) {
            return getFileName(getDocument());
        }
        return getFileName(messageOwner);
    }

    public String getFileNameFast() {
        if (getDocumentFast() != null) {
            return getFileName(getDocumentFast());
        }
        return getFileName(messageOwner);
    }

    public static String getFileName(TLRPC.Message messageOwner) {
        if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
            return getFileName(getDocument(messageOwner));
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
            ArrayList<TLRPC.PhotoSize> sizes = getMedia(messageOwner).photo.sizes;
            if (sizes.size() > 0) {
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    return FileLoader.getAttachFileName(sizeFull);
                }
            }
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage != null) {
            return getFileName(getMedia(messageOwner).webpage.document);
        }
        return "";
    }

    public static String getFileName(TLRPC.Document document) {
        return FileLoader.getAttachFileName(document);
    }

    public static String getFileName(TLRPC.MessageMedia media) {
        if (media instanceof TLRPC.TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(media.document);
        } else if (media instanceof TLRPC.TL_messageMediaPhoto) {
            ArrayList<TLRPC.PhotoSize> sizes = media.photo.sizes;
            if (sizes.size() > 0) {
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    return FileLoader.getAttachFileName(sizeFull);
                }
            }
        } else if (media instanceof TLRPC.TL_messageMediaWebPage && media.webpage != null) {
            return FileLoader.getAttachFileName(media.webpage.document);
        }
        return "";
    }

    public int getMediaType() {
        if (isVideo()) {
            return FileLoader.MEDIA_DIR_VIDEO;
        } else if (isVoice()) {
            return FileLoader.MEDIA_DIR_AUDIO;
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
            return FileLoader.MEDIA_DIR_DOCUMENT;
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
            return FileLoader.MEDIA_DIR_IMAGE;
        }
        return FileLoader.MEDIA_DIR_CACHE;
    }

    public static boolean containsUrls(CharSequence message) {
        if (message == null || message.length() < 2 || message.length() > 1024 * 20) {
            return false;
        }

        int length = message.length();

        int digitsInRow = 0;
        int schemeSequence = 0;
        int dotSequence = 0;

        char lastChar = 0;

        for (int i = 0; i < length; i++) {
            char c = message.charAt(i);

            if (c >= '0' && c <= '9') {
                digitsInRow++;
                if (digitsInRow >= 6) {
                    return true;
                }
                schemeSequence = 0;
                dotSequence = 0;
            } else if (!(c != ' ' && digitsInRow > 0)) {
                digitsInRow = 0;
            }
            if ((c == '@' || c == '#' || c == '/' || c == '$') && i == 0 || i != 0 && (message.charAt(i - 1) == ' ' || message.charAt(i - 1) == '\n')) {
                return true;
            }
            if (c == ':') {
                if (schemeSequence == 0) {
                    schemeSequence = 1;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '/') {
                if (schemeSequence == 2) {
                    return true;
                }
                if (schemeSequence == 1) {
                    schemeSequence++;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '.') {
                if (dotSequence == 0 && lastChar != ' ') {
                    dotSequence++;
                } else {
                    dotSequence = 0;
                }
            } else if (c != ' ' && lastChar == '.' && dotSequence == 1) {
                return true;
            } else {
                dotSequence = 0;
            }
            lastChar = c;
        }
        return false;
    }

    public void generateLinkDescription() {
        if (linkDescription != null) {
            return;
        }
        boolean allowUsernames = false;
        int hashtagsType = 0;
        TLRPC.WebPage webpage = null;
        if (storyMentionWebpage != null) {
            webpage = storyMentionWebpage;
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage) {
            webpage = ((TLRPC.TL_messageMediaWebPage) getMedia(messageOwner)).webpage;
        }
        if (webpage != null) {
            for (int i = 0; i < webpage.attributes.size(); ++i) {
                TLRPC.WebPageAttribute attr = webpage.attributes.get(i);
                if (attr instanceof TLRPC.TL_webPageAttributeStory) {
                    TLRPC.TL_webPageAttributeStory storyAttr = (TLRPC.TL_webPageAttributeStory) attr;
                    if (storyAttr.storyItem != null && storyAttr.storyItem.caption != null) {
                        linkDescription = new SpannableStringBuilder(storyAttr.storyItem.caption);
                        webPageDescriptionEntities = storyAttr.storyItem.entities;
                        allowUsernames = true;
                        break;
                    }
                }
            }
        }
        if (linkDescription == null) {
            if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage instanceof TLRPC.TL_webPage && getMedia(messageOwner).webpage.description != null) {
                linkDescription = Spannable.Factory.getInstance().newSpannable(getMedia(messageOwner).webpage.description);
                String siteName = getMedia(messageOwner).webpage.site_name;
                if (siteName != null) {
                    siteName = siteName.toLowerCase();
                }
                if ("instagram".equals(siteName)) {
                    hashtagsType = 1;
                } else if ("twitter".equals(siteName)) {
                    hashtagsType = 2;
                }
            } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGame && getMedia(messageOwner).game.description != null) {
                linkDescription = Spannable.Factory.getInstance().newSpannable(getMedia(messageOwner).game.description);
            } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaInvoice && getMedia(messageOwner).description != null) {
                linkDescription = Spannable.Factory.getInstance().newSpannable(getMedia(messageOwner).description);
            }
        }
        if (!TextUtils.isEmpty(linkDescription)) {
            if (containsUrls(linkDescription)) {
                try {
                    AndroidUtilities.addLinksSafe((Spannable) linkDescription, Linkify.WEB_URLS, false, true);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            linkDescription = Emoji.replaceEmoji(linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), false);
            if (webPageDescriptionEntities != null) {
                addEntitiesToText(linkDescription, webPageDescriptionEntities, isOut(), allowUsernames, false, !allowUsernames);
                replaceAnimatedEmoji(linkDescription, webPageDescriptionEntities, Theme.chat_msgTextPaint.getFontMetricsInt());
            }
            if (hashtagsType != 0) {
                if (!(linkDescription instanceof Spannable)) {
                    linkDescription = new SpannableStringBuilder(linkDescription);
                }
                addUrlsByPattern(isOutOwner(), linkDescription, false, hashtagsType, 0, false);
            }
        }
    }

    public CharSequence getVoiceTranscription() {
        if (messageOwner == null || messageOwner.voiceTranscription == null) {
            return null;
        }
        if (TextUtils.isEmpty(messageOwner.voiceTranscription)) {
            SpannableString ssb = new SpannableString(getString(R.string.NoWordsRecognized));
            ssb.setSpan(new CharacterStyle() {
                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setTextSize(textPaint.getTextSize() * .8f);
                    textPaint.setColor(Theme.chat_timePaint.getColor());
                }
            }, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ssb;
        }
        CharSequence text = translated && messageOwner.translatedVoiceTranscription != null ? messageOwner.translatedVoiceTranscription.text : messageOwner.voiceTranscription;
        if (!TextUtils.isEmpty(text)) {
            text = Emoji.replaceEmoji(text, Theme.chat_msgTextPaint.getFontMetricsInt(), false);
        }
        return text;
    }

    public float measureVoiceTranscriptionHeight() {
        CharSequence voiceTranscription = getVoiceTranscription();
        if (voiceTranscription == null) {
            return 0;
        }
        int width = AndroidUtilities.displaySize.x - dp(this.needDrawAvatar() ? 147 : 95);
        StaticLayout captionLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            captionLayout = StaticLayout.Builder.obtain(voiceTranscription, 0, voiceTranscription.length(), Theme.chat_msgTextPaint, width)
                    .setBreakStrategy(StaticLayout.BREAK_STRATEGY_HIGH_QUALITY)
                    .setHyphenationFrequency(StaticLayout.HYPHENATION_FREQUENCY_NONE)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .build();
        } else {
            captionLayout = new StaticLayout(voiceTranscription, Theme.chat_msgTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        return captionLayout.getHeight();
    }

    public boolean isVoiceTranscriptionOpen() {
        return (
            messageOwner != null &&
            (isVoice() || isRoundVideo() && TranscribeButton.isVideoTranscriptionOpen(this)) &&
            messageOwner.voiceTranscriptionOpen &&
            messageOwner.voiceTranscription != null &&
            (messageOwner.voiceTranscriptionFinal || TranscribeButton.isTranscribing(this))
        );
    }

    private boolean captionTranslated;
    private boolean captionSummarized;

    public void generateExplanation() {
        if (type != TYPE_POLL) {
            return;
        }
        String text = null;
        ArrayList<TLRPC.MessageEntity> entities = null;
        TLRPC.MessageMedia m = getMedia(messageOwner);
        if (m instanceof TLRPC.TL_messageMediaPoll) {
            TLRPC.TL_messageMediaPoll media = (TLRPC.TL_messageMediaPoll) m;
            if (media.results != null) {
                text = media.results.solution;
                entities = media.results.solution_entities;
            }
        }

        if (text != null) {
            quizExplanation = Emoji.replaceEmoji(text, Theme.chat_explanationTextPaint.getFontMetricsInt(), false);
            quizExplanation = replaceAnimatedEmoji(quizExplanation, entities, Theme.chat_explanationTextPaint.getFontMetricsInt(), false);
            addEntitiesToText(quizExplanation, entities, isOutOwner(), true, false, false);
        } else {
            quizExplanation = null;
        }
    }

    public void generateCaption() {
        if (isRoundVideo()) return;
        if (caption != null &&
            (translated && (messageOwner.translatedText != null || summarized && messageOwner.translatedSummaryText != null)) == captionTranslated &&
            summarized == captionSummarized
        ) {
            return;
        }
        String text = messageOwner.message;
        ArrayList<TLRPC.MessageEntity> entities = messageOwner.entities;
        boolean forceManualEntities = false;
        if (type == TYPE_STORY) {
            if (messageOwner.media != null && messageOwner.media.storyItem != null) {
                text = messageOwner.media.storyItem.caption;
                entities = messageOwner.media.storyItem.entities;
                forceManualEntities = true;
            } else {
                text = "";
                entities = new ArrayList<>();
            }
        } else if (hasExtendedMedia()) {
            text = messageOwner.message = messageOwner.media.description;
        }
        if (messageOwner.translatedSummaryText != null && summarized && translated) {
            captionSummarized = true;
            captionTranslated = true;
            text = messageOwner.translatedSummaryText.text;
            entities = messageOwner.translatedSummaryText.entities;
        } else if (messageOwner.summaryText != null && summarized) {
            captionSummarized = true;
            captionTranslated = false;
            text = messageOwner.summaryText.text;
            entities = messageOwner.summaryText.entities;
        } else if (messageOwner.translatedText != null && translated) {
            captionSummarized = false;
            captionTranslated = true;
            text = messageOwner.translatedText.text;
            entities = messageOwner.translatedText.entities;
        } else {
            captionSummarized = false;
            captionTranslated = false;
        }
        if (!isMediaEmpty() && !(getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGame) && !TextUtils.isEmpty(text)) {
            caption = Emoji.replaceEmoji(text, Theme.chat_msgTextPaint.getFontMetricsInt(), false);
            caption = replaceAnimatedEmoji(caption, entities, Theme.chat_msgTextPaint.getFontMetricsInt(), false);

            boolean hasEntities;
            if (messageOwner.send_state != MESSAGE_SEND_STATE_SENT) {
                hasEntities = false;
            } else {
                hasEntities = !entities.isEmpty();
            }

            boolean useManualParse = forceManualEntities || !hasEntities && (
                eventId != 0 ||
                getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto_old ||
                getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto_layer68 ||
                getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto_layer74 ||
                getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument_old ||
                getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument_layer68 ||
                getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument_layer74 ||
                isOut() && messageOwner.send_state != MESSAGE_SEND_STATE_SENT ||
                messageOwner.id < 0
            );

            if (useManualParse) {
                if (containsUrls(caption)) {
                    try {
                        AndroidUtilities.addLinksSafe((Spannable) caption, Linkify.WEB_URLS | Linkify.PHONE_NUMBERS, false, true);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                addUrlsByPattern(isOutOwner(), caption, true, 0, 0, true);
            }

            addEntitiesToText(caption, useManualParse);
            caption = FormattedDateSpan.applyFormatedDateEntities(caption);
            if (isVideo()) {
                addUrlsByPattern(isOutOwner(), caption, true, 3, (int) getDuration(), false);
            } else if (isMusic() || isVoice()) {
                addUrlsByPattern(isOutOwner(), caption, true, 4, (int) getDuration(), false);
            }
            applyTimestampsHighlightForReplyMsg(caption);
        }
    }

    public static void addUrlsByPattern(boolean isOut, CharSequence charSequence, boolean botCommands, int patternType, int duration, boolean check) {
        if (charSequence == null) {
            return;
        }
        try {
            Matcher matcher;
            if (patternType == 3 || patternType == 4) {
                if (videoTimeUrlPattern == null) {
                    videoTimeUrlPattern = Pattern.compile("\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b(?: - |)([^\\n]*)");
                }
                matcher = videoTimeUrlPattern.matcher(charSequence);
            } else if (patternType == 1) {
                if (instagramUrlPattern == null) {
                    instagramUrlPattern = Pattern.compile("(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+");
                }
                matcher = instagramUrlPattern.matcher(charSequence);
            } else {
                if (urlPattern == null) {
                    urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[^0-9][\\w.]+(@[^0-9][\\w.]+)?|(^|\\s|\\()\\$[^0-9][\\w.]+(@[^0-9][\\w.]+)?|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)");
                }
                matcher = urlPattern.matcher(charSequence);
            }
            if (!(charSequence instanceof Spannable)) {
                return;
            }
            Spannable spannable = (Spannable) charSequence;
            int totalCount = 0;
            while (matcher.find() && totalCount < 100) {
                int start = matcher.start();
                int end = matcher.end();
                URLSpanNoUnderline url = null;
                if (patternType == 3 || patternType == 4) {
                    int count = matcher.groupCount();
                    int s1 = matcher.start(1);
                    int e1 = matcher.end(1);
                    int s2 = matcher.start(2);
                    int e2 = matcher.end(2);
                    int s3 = matcher.start(3);
                    int e3 = matcher.end(3);
                    int s4 = matcher.start(4);
                    int e4 = matcher.end(4);
                    int minutes = Utilities.parseInt(charSequence.subSequence(s2, e2));
                    int seconds = Utilities.parseInt(charSequence.subSequence(s3, e3));
                    int hours = s1 >= 0 && e1 >= 0 ? Utilities.parseInt(charSequence.subSequence(s1, e1)) : -1;
                    String label = s4 < 0 || e4 < 0 ? null : charSequence.subSequence(s4, e4).toString();
                    if (s4 >= 0 || e4 >= 0) {
                        end = e3;
                    }
                    URLSpan[] spans = spannable.getSpans(start, end, URLSpan.class);
                    if (spans != null && spans.length > 0) {
                        continue;
                    }
                    seconds += minutes * 60;
                    if (hours > 0) {
                        seconds += hours * 60 * 60;
                    }
                    if (seconds > duration) {
                        continue;
                    }
                    if (patternType == 3) {
                        url = new URLSpanNoUnderline("video?" + seconds);
                    } else {
                        url = new URLSpanNoUnderline("audio?" + seconds);
                    }
                    url.label = label;
                } else {
                    char ch = charSequence.charAt(start);
                    if (patternType != 0) {
                        if (ch != '@' && ch != '#') {
                            start++;
                        }
                        ch = charSequence.charAt(start);
                        if (ch != '@' && ch != '#') {
                            continue;
                        }
                    } else {
                        if (ch != '@' && ch != '#' && ch != '/' && ch != '$') {
                            start++;
                        }
                    }
                    if (patternType == 1) {
                        if (ch == '@') {
                            url = new URLSpanNoUnderline("https://instagram.com/" + charSequence.subSequence(start + 1, end).toString());
                        } else {
                            url = new URLSpanNoUnderline("https://www.instagram.com/explore/tags/" + charSequence.subSequence(start + 1, end).toString());
                        }
                    } else if (patternType == 2) {
                        if (ch == '@') {
                            url = new URLSpanNoUnderline("https://twitter.com/" + charSequence.subSequence(start + 1, end).toString());
                        } else {
                            url = new URLSpanNoUnderline("https://twitter.com/hashtag/" + charSequence.subSequence(start + 1, end).toString());
                        }
                    } else {
                        if (charSequence.charAt(start) == '/') {
                            if (botCommands) {
                                url = new URLSpanBotCommand(charSequence.subSequence(start, end).toString(), isOut ? 1 : 0);
                            }
                        } else {
                            String uri = charSequence.subSequence(start, end).toString();
                            if (uri != null) {
                                uri = uri.replaceAll("∕|⁄|%E2%81%84|%E2%88%95", "/");
                            }
                            url = new URLSpanNoUnderline(uri);
                        }
                    }
                }
                if (url != null) {
                    if (check) {
                        ClickableSpan[] spans = spannable.getSpans(start, end, ClickableSpan.class);
                        if (spans != null && spans.length > 0) {
                            spannable.removeSpan(spans[0]);
                        }
                    }
                    spannable.setSpan(url, start, end, 0);
                    totalCount++;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static int[] getWebDocumentWidthAndHeight(TLRPC.WebDocument document) {
        if (document == null) {
            return null;
        }
        for (int a = 0, size = document.attributes.size(); a < size; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                return new int[]{attribute.w, attribute.h};
            } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return new int[]{attribute.w, attribute.h};
            }
        }
        return null;
    }

    public static double getWebDocumentDuration(TLRPC.WebDocument document) {
        if (document == null) {
            return 0;
        }
        for (int a = 0, size = document.attributes.size(); a < size; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.duration;
            } else if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attribute.duration;
            }
        }
        return 0;
    }

    public static int[] getInlineResultWidthAndHeight(TLRPC.BotInlineResult inlineResult) {
        int[] result = getWebDocumentWidthAndHeight(inlineResult.content);
        if (result == null) {
            result = getWebDocumentWidthAndHeight(inlineResult.thumb);
            if (result == null) {
                result = new int[]{0, 0};
            }
        }
        return result;
    }

    public static int getInlineResultDuration(TLRPC.BotInlineResult inlineResult) {
        int result = (int) getWebDocumentDuration(inlineResult.content);
        if (result == 0) {
            result = (int) getWebDocumentDuration(inlineResult.thumb);
        }
        return result;
    }

    // only set in searching with tags
    public boolean isPrimaryGroupMessage;
    public boolean hasValidGroupId() {
        return getGroupId() != 0 && (photoThumbs != null && !photoThumbs.isEmpty() || type == TYPE_VIDEO || type == TYPE_PHOTO || isMusic() || isDocument());
    }
    public boolean hasValidGroupIdFast() {
        return getGroupId() != 0 && (photoThumbs != null && !photoThumbs.isEmpty() || type == TYPE_VIDEO || type == TYPE_PHOTO || type == TYPE_MUSIC || type == TYPE_FILE);
    }

    public long getGroupIdForUse() {
        return localSentGroupId != 0 ? localSentGroupId : messageOwner.grouped_id;
    }

    public long getGroupId() {
        return localGroupId != 0 ? localGroupId : getGroupIdForUse();
    }

    public static void addLinks(boolean isOut, CharSequence messageText) {
        addLinks(isOut, messageText, true, false);
    }

    public static void addLinks(boolean isOut, CharSequence messageText, boolean botCommands, boolean check) {
        addLinks(isOut, messageText, botCommands, check, false);
    }

    public static void addLinks(boolean isOut, CharSequence messageText, boolean botCommands, boolean check, boolean internalOnly) {
        if (messageText instanceof Spannable && containsUrls(messageText)) {
            try {
                AndroidUtilities.addLinksSafe((Spannable) messageText, Linkify.WEB_URLS, internalOnly, false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            addPhoneLinks(messageText);
            addUrlsByPattern(isOut, messageText, botCommands, 0, 0, check);
        }
    }

    public static void addPhoneLinks(CharSequence messageText) {
//        if (messageText == null || !(messageText instanceof Spannable))
//            return;
//        Spannable spannable = (Spannable) messageText;
//        SpannableString otherText = new SpannableString(spannable);
//        AndroidUtilities.doSafe(() -> Linkify.addLinks(otherText, Linkify.PHONE_NUMBERS));
//        URLSpan[] spans = otherText.getSpans(0, otherText.length(), URLSpan.class);
//        for (int i = 0; spans != null && i < spans.length; ++i) {
//            if (spans[i].getURL().startsWith("tel:")) {
//                spannable.setSpan(
//                    new URLSpanNoUnderline(spans[i].getURL()),
//                    otherText.getSpanStart(spans[i]),
//                    otherText.getSpanEnd(spans[i]),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                );
//            }
//        }
    }

    public void resetPlayingProgress() {
        audioProgress = 0.0f;
        audioProgressSec = 0;
        bufferedProgress = 0.0f;
    }

    private boolean addEntitiesToText(CharSequence text, boolean useManualParse) {
        return addEntitiesToText(text, false, useManualParse);
    }

    public boolean addEntitiesToText(CharSequence text, boolean photoViewer, boolean useManualParse) {
        if (text == null) {
            return false;
        }
        if (isRestrictedMessage || getMedia(messageOwner) instanceof TLRPC.TL_messageMediaUnsupported) {
            ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
            TLRPC.TL_messageEntityItalic entityItalic = new TLRPC.TL_messageEntityItalic();
            entityItalic.offset = 0;
            entityItalic.length = text.length();
            entities.add(entityItalic);
            return addEntitiesToText(text, entities, isOutOwner(), true, photoViewer, useManualParse);
        } else {
            return addEntitiesToText(text, getEntities(), isOutOwner(), true, photoViewer, useManualParse);
        }
    }

    public void replaceEmojiToLottieFrame(CharSequence text, int[] emojiOnly) {
        if (!(text instanceof Spannable)) {
            return;
        }
        Spannable spannable = (Spannable) text;
        Emoji.EmojiSpan[] spans = spannable.getSpans(0, spannable.length(), Emoji.EmojiSpan.class);
        AnimatedEmojiSpan[] aspans = spannable.getSpans(0, spannable.length(), AnimatedEmojiSpan.class);

        if (spans == null || (emojiOnly == null ? 0 : emojiOnly[0]) - spans.length - (aspans == null ? 0 : aspans.length) > 0) {
            return;
        }
        for (int i = 0; i < spans.length; ++i) {
            CharSequence emoji = spans[i].emoji;
            boolean invert = false;
            if (Emoji.endsWithRightArrow(emoji)) {
                emoji = emoji.subSequence(0, emoji.length() - 2);
                invert = true;
            }
            TLRPC.Document lottieDocument = MediaDataController.getInstance(currentAccount).getEmojiAnimatedSticker(emoji);
            if (lottieDocument != null) {
                int start = spannable.getSpanStart(spans[i]);
                int end = spannable.getSpanEnd(spans[i]);
                spannable.removeSpan(spans[i]);
                AnimatedEmojiSpan span = new AnimatedEmojiSpan(lottieDocument, spans[i].fontMetrics);
                span.standard = true;
                span.invert = invert;
                spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public ArrayList<TLRPC.MessageEntity> getEntities() {
        if (messageOwner == null) return null;
        if (summarized) {
            if (translated && messageOwner.translatedSummaryText != null) {
                return messageOwner.translatedSummaryText.entities;
            } else if (messageOwner.summaryText != null) {
                return messageOwner.summaryText.entities;
            }
            return null;
        }
        if (translated) {
            if (messageOwner.voiceTranscriptionOpen) {
                return messageOwner.translatedVoiceTranscription != null ? messageOwner.translatedVoiceTranscription.entities : null;
            } else {
                return messageOwner.translatedText != null ? messageOwner.translatedText.entities : null;
            }
        }
        return messageOwner.entities;
    }

    public Spannable replaceAnimatedEmoji(CharSequence text, Paint.FontMetricsInt fontMetricsInt) {
        return replaceAnimatedEmoji(text, getEntities(), fontMetricsInt, false);
    }

    public static Spannable replaceAnimatedEmoji(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, Paint.FontMetricsInt fontMetricsInt) {
        return replaceAnimatedEmoji(text, entities, fontMetricsInt, false);
    }

    public static Spannable replaceAnimatedEmoji(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, Paint.FontMetricsInt fontMetricsInt, boolean top) {
        return replaceAnimatedEmoji(text, entities, fontMetricsInt, top, 1.2f, 0);
    }

    public static Spannable replaceAnimatedEmoji(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, Paint.FontMetricsInt fontMetricsInt, boolean top, float scale, int minusLimit) {
        if (text == null) {
            return null;
        }
        Spannable spannable = text instanceof Spannable ? (Spannable) text : new SpannableString(text);
        if (entities == null) {
            return spannable;
        }
        int limitCount = (SharedConfig.getDevicePerformanceClass() >= SharedConfig.PERFORMANCE_CLASS_HIGH ? 100 : 50) - minusLimit;
        Emoji.EmojiSpan[] emojiSpans = spannable.getSpans(0, spannable.length(), Emoji.EmojiSpan.class);
        for (int i = 0; i < entities.size(); ++i) {
            if (limitCount <= 0) break;
            TLRPC.MessageEntity messageEntity = entities.get(i);
            if (messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                TLRPC.TL_messageEntityCustomEmoji entity = (TLRPC.TL_messageEntityCustomEmoji) messageEntity;
                for (int j = 0; j < emojiSpans.length; ++j) {
                    Emoji.EmojiSpan span = emojiSpans[j];
                    if (span != null) {
                        int start = spannable.getSpanStart(span);
                        int end = spannable.getSpanEnd(span);
                        if (AndroidUtilities.intersect1d(entity.offset, entity.offset + entity.length, start, end)) {
                            spannable.removeSpan(span);
                            emojiSpans[j] = null;
                        }
                    }
                }

                if (messageEntity.offset + messageEntity.length <= spannable.length()) {
                    AnimatedEmojiSpan[] animatedSpans = spannable.getSpans(messageEntity.offset, messageEntity.offset + messageEntity.length, AnimatedEmojiSpan.class);
                    if (animatedSpans != null && animatedSpans.length > 0) {
                        for (int j = 0; j < animatedSpans.length; ++j) {
                            spannable.removeSpan(animatedSpans[j]);
                        }
                    }

                    AnimatedEmojiSpan span;
                    if (entity.document != null) {
                        span = new AnimatedEmojiSpan(entity.document, scale, fontMetricsInt);
                    } else {
                        span = new AnimatedEmojiSpan(entity.document_id, scale, fontMetricsInt);
                    }
                    span.top = top;
                    spannable.setSpan(span, messageEntity.offset, messageEntity.offset + messageEntity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    limitCount--;
                }
            }
        }
        return spannable;
    }

    public static final int ENTITIES_ALL = 0;
    public static final int ENTITIES_ONLY_HASHTAGS = 1;

    public static boolean addEntitiesToText(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, boolean out, boolean usernames, boolean photoViewer, boolean useManualParse) {
        return addEntitiesToText(text, entities, out, usernames, photoViewer, useManualParse, ENTITIES_ALL);
    }

    public static boolean addEntitiesToText(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, boolean out, boolean usernames, boolean photoViewer, boolean useManualParse, int allowed) {
        if (!(text instanceof Spannable)) {
            return false;
        }
        text = FormattedDateSpan.restoreFormatedDateEntities(text);
        Spannable spannable = (Spannable) text;
        URLSpan[] spans = spannable.getSpans(0, text.length(), URLSpan.class);
        boolean hasUrls = spans != null && spans.length > 0;
        if (entities == null || entities.isEmpty()) {
            return hasUrls;
        }

        byte t;
        if (photoViewer) {
            t = 2;
        } else if (out) {
            t = 1;
        } else {
            t = 0;
        }

        ArrayList<TextStyleSpan.TextStyleRun> runs = new ArrayList<>();
        ArrayList<TLRPC.MessageEntity> entitiesCopy = new ArrayList<>(entities);

        Collections.sort(entitiesCopy, (o1, o2) -> {
            if (o1.offset > o2.offset) {
                return 1;
            } else if (o1.offset < o2.offset) {
                return -1;
            }
            return 0;
        });
        for (int a = 0, N = entitiesCopy.size(); a < N; a++) {
            TLRPC.MessageEntity entity = entitiesCopy.get(a);
            if (entity.length <= 0 || entity.offset < 0 || entity.offset >= text.length()) {
                continue;
            } else if (entity.offset + entity.length > text.length()) {
                entity.length = text.length() - entity.offset;
            }

            if (!useManualParse ||
                    entity instanceof TLRPC.TL_messageEntityBold ||
                    entity instanceof TLRPC.TL_messageEntityItalic ||
                    entity instanceof TLRPC.TL_messageEntityStrike ||
                    entity instanceof TLRPC.TL_messageEntityUnderline ||
                    entity instanceof TLRPC.TL_messageEntityBlockquote ||
                    entity instanceof TLRPC.TL_messageEntityFormattedDate ||
                    entity instanceof TLRPC.TL_messageEntityCode ||
                    entity instanceof TLRPC.TL_messageEntityPre ||
                    entity instanceof TLRPC.TL_messageEntityMentionName ||
                    entity instanceof TLRPC.TL_inputMessageEntityMentionName ||
                    entity instanceof TLRPC.TL_messageEntityTextUrl ||
                    entity instanceof TLRPC.TL_messageEntitySpoiler ||
                    entity instanceof TLRPC.TL_messageEntityCustomEmoji ||
                    entity instanceof TLRPC.TL_messageEntityDiffInsert ||
                    entity instanceof TLRPC.TL_messageEntityDiffReplace ||
                    entity instanceof TLRPC.TL_messageEntityDiffDelete) {
                if (spans != null && spans.length > 0) {
                    for (int b = 0; b < spans.length; b++) {
                        if (spans[b] == null) {
                            continue;
                        }
                        int start = spannable.getSpanStart(spans[b]);
                        int end = spannable.getSpanEnd(spans[b]);
                        if (entity.offset <= start && entity.offset + entity.length >= start || entity.offset <= end && entity.offset + entity.length >= end) {
                            spannable.removeSpan(spans[b]);
                            spans[b] = null;
                        }
                    }
                }
            }

            if (allowed == ENTITIES_ONLY_HASHTAGS && !(entity instanceof TLRPC.TL_messageEntityHashtag))
                continue;

            if (
                entity instanceof TLRPC.TL_messageEntityCustomEmoji ||
                entity instanceof TLRPC.TL_messageEntityBlockquote ||
                entity instanceof TLRPC.TL_messageEntityPre ||
                entity instanceof TLRPC.TL_messageEntityDiffReplace
            ) {
                continue;
            }

            TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
            newRun.start = entity.offset;
            newRun.end = newRun.start + entity.length;
            TLRPC.MessageEntity urlEntity = null;
            if (entity instanceof TLRPC.TL_messageEntitySpoiler) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_SPOILER;
            } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_STRIKE;
            } else if (entity instanceof TLRPC.TL_messageEntityDiffDelete) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_STRIKE_RED;
            } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_UNDERLINE;
            } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_BOLD;
            } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_ITALIC;
            } else if (entity instanceof TLRPC.TL_messageEntityCode) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_MONO;
            } else if (entity instanceof TLRPC.TL_messageEntityDiffInsert) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_ACCENT;
            } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                if (!usernames) {
                    continue;
                }
                newRun.flags = TextStyleSpan.FLAG_STYLE_MENTION;
                newRun.urlEntity = entity;
            } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                if (!usernames) {
                    continue;
                }
                newRun.flags = TextStyleSpan.FLAG_STYLE_MENTION;
                newRun.urlEntity = entity;
            } else {
                if (useManualParse && !(entity instanceof TLRPC.TL_messageEntityTextUrl || entity instanceof TLRPC.TL_messageEntityFormattedDate)) {
                    continue;
                }
                if ((entity instanceof TLRPC.TL_messageEntityUrl || entity instanceof TLRPC.TL_messageEntityTextUrl) && Browser.isPassportUrl(entity.url)) {
                    continue;
                }
                if (entity instanceof TLRPC.TL_messageEntityMention && !usernames) {
                    continue;
                }
                newRun.flags = TextStyleSpan.FLAG_STYLE_URL;
                newRun.urlEntity = entity;

                if (entity instanceof TLRPC.TL_messageEntityTextUrl) {
                    newRun.flags |= TextStyleSpan.FLAG_STYLE_TEXT_URL;
                }
            }

            for (int b = 0, N2 = runs.size(); b < N2; b++) {
                TextStyleSpan.TextStyleRun run = runs.get(b);
                if ((run.flags & TextStyleSpan.FLAG_STYLE_SPOILER) != 0 && newRun.start >= run.start && newRun.end <= run.end) {
                    continue;
                }

                if (newRun.start > run.start) {
                    if (newRun.start >= run.end) {
                        continue;
                    }

                    if (newRun.end < run.end) {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                        r.merge(run);
                        b++;
                        N2++;
                        runs.add(b, r);

                        r = new TextStyleSpan.TextStyleRun(run);
                        r.start = newRun.end;
                        b++;
                        N2++;
                        runs.add(b, r);
                    } else {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                        r.merge(run);
                        r.end = run.end;
                        b++;
                        N2++;
                        runs.add(b, r);
                    }

                    int temp = newRun.start;
                    newRun.start = run.end;
                    run.end = temp;
                } else {
                    if (run.start >= newRun.end) {
                        continue;
                    }
                    int temp = run.start;
                    if (newRun.end == run.end) {
                        run.merge(newRun);
                    } else if (newRun.end < run.end) {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(run);
                        r.merge(newRun);
                        r.end = newRun.end;
                        b++;
                        N2++;
                        runs.add(b, r);

                        run.start = newRun.end;
                    } else {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                        r.start = run.end;
                        b++;
                        N2++;
                        runs.add(b, r);

                        run.merge(newRun);
                    }
                    newRun.end = temp;
                }
            }
            if (newRun.start < newRun.end) {
                runs.add(newRun);
            }
        }

        final int count = Math.min(MediaDataController.MAX_STYLE_RUNS_COUNT, runs.size());
        int linksCount = 0, spoilersCount = 0, codesCount = 0;
        for (int a = 0; a < count; a++) {
            TextStyleSpan.TextStyleRun run = runs.get(a);

            if (allowed == ENTITIES_ONLY_HASHTAGS && !(run.urlEntity instanceof TLRPC.TL_messageEntityHashtag))
                continue;

            boolean setRun = false;
            String url = run.urlEntity != null ? TextUtils.substring(text, run.urlEntity.offset, run.urlEntity.offset + run.urlEntity.length) : null;
            if (run.urlEntity instanceof TLRPC.TL_messageEntityBotCommand) {
                if (linksCount >= MediaDataController.MAX_LINKS_COUNT) continue;
                linksCount++;
                spannable.setSpan(new URLSpanBotCommand(url, t, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityHashtag || run.urlEntity instanceof TLRPC.TL_messageEntityMention || run.urlEntity instanceof TLRPC.TL_messageEntityCashtag) {
                if (linksCount >= MediaDataController.MAX_LINKS_COUNT) continue;
                linksCount++;
                spannable.setSpan(new URLSpanNoUnderline(url, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityEmail) {
                if (linksCount >= MediaDataController.MAX_LINKS_COUNT) continue;
                linksCount++;
                spannable.setSpan(new URLSpanReplacement("mailto:" + url, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityUrl) {
                if (linksCount >= MediaDataController.MAX_LINKS_COUNT) continue;
                linksCount++;
                hasUrls = true;
                String lowerCase = url.toLowerCase();
                url = !lowerCase.contains("://") ? (BotWebViewContainer.isTonsite(url) ? "tonsite://" : "http://") + url : url;
                if (url != null) {
                    url = url.replaceAll("∕|⁄|%E2%81%84|%E2%88%95", "/");
                }
                if (Browser.isTonsitePunycode(url)) continue;
                spannable.setSpan(new URLSpanBrowser(url, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityFormattedDate) {
                if (linksCount >= MediaDataController.MAX_LINKS_COUNT) continue;
                linksCount++;
                spannable.setSpan(new FormattedDateSpan(url, run, (TLRPC.TL_messageEntityFormattedDate) run.urlEntity), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityBankCard) {
                hasUrls = true;
                spannable.setSpan(new URLSpanNoUnderline("card:" + url, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityPhone) {
                hasUrls = true;
                String tel = PhoneFormat.stripExceptNumbers(url);
                if (url.startsWith("+")) {
                    tel = "+" + tel;
                }
                spannable.setSpan(new URLSpanNoUnderline("tel:" + tel, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                if (linksCount >= MediaDataController.MAX_LINKS_COUNT) continue;
                linksCount++;
                url = run.urlEntity.url;
                if (url != null) {
                    url = url.replaceAll("∕|⁄|%E2%81%84|%E2%88%95", "/");
                }
                if (Browser.isTonsitePunycode(url)) continue;
                spannable.setSpan(new URLSpanReplacement(url, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                spannable.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_messageEntityMentionName) run.urlEntity).user_id, t, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (run.urlEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                spannable.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName) run.urlEntity).user_id.user_id, t, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ((run.flags & TextStyleSpan.FLAG_STYLE_MONO) != 0) {
                spannable.setSpan(new URLSpanMono(spannable, run.start, run.end, t, run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                setRun = true;
                spannable.setSpan(new TextStyleSpan(run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (!setRun && (run.flags & TextStyleSpan.FLAG_STYLE_SPOILER) != 0) {
                if (spoilersCount >= SpoilerEffect.MAX_SPOILERS_COUNT) continue;
                spoilersCount++;
                spannable.setSpan(new TextStyleSpan(run), run.start, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        for (int a = 0, N = entitiesCopy.size(); a < N; a++) {
            TLRPC.MessageEntity entity = entitiesCopy.get(a);
            if (entity.length <= 0 || entity.offset < 0 || entity.offset >= text.length()) {
                continue;
            } else if (entity.offset + entity.length > text.length()) {
                entity.length = text.length() - entity.offset;
            }

            if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                QuoteSpan.putQuote(spannable, entity.offset, entity.offset + entity.length, entity.collapsed);
            } else if (entity instanceof TLRPC.TL_messageEntityPre) {
                if (codesCount >= 50) continue;
                codesCount++;
                final int start = entity.offset;
                final int end = entity.offset + entity.length;
                spannable.setSpan(new CodeHighlighting.Span(true, 0, null, entity.language, spannable.subSequence(start, end).toString()), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (entity instanceof TLRPC.TL_messageEntityDiffReplace) {
                final int start = entity.offset;
                final int end = entity.offset + entity.length;
                spannable.setSpan(new SquigglyLinesSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return hasUrls;
    }

    public boolean needDrawShareButton() {
        if (isRepostPreview) {
            return false;
        }
        if (sideMenuEnabled) {
            return false;
        }
        if (getDialogId() == UserObject.VERIFY) {
            return false;
        }
        if (isSaved) {
            long selfId = UserConfig.getInstance(currentAccount).clientUserId;
            long dialogId = MessageObject.getSavedDialogId(selfId, messageOwner);
            if (dialogId == selfId || dialogId == UserObject.ANONYMOUS) {
                return false;
            }
            if (messageOwner == null || messageOwner.fwd_from == null) {
                return false;
            }
            if (messageOwner.fwd_from.from_id == null && messageOwner.fwd_from.saved_from_id == null) {
                return false;
            }
            return true;
        }
        if (type == TYPE_JOINED_CHANNEL) {
            return false;
        } else if (isSponsored()) {
            return false;
        } else if (hasCode) {
            return false;
        } else if (preview) {
            return false;
        } else if (scheduled) {
            return false;
        } else if (eventId != 0) {
            return false;
        } else if (searchType == ChatActivity.SEARCH_PUBLIC_POSTS) {
            return true;
        } else if (messageOwner.noforwards) {
            return false;
        } else if (messageOwner.fwd_from != null && !isOutOwner() && messageOwner.fwd_from.saved_from_peer != null && getDialogId() == UserConfig.getInstance(currentAccount).getClientUserId()) {
            return true;
        } else if (type == TYPE_STICKER || type == TYPE_ANIMATED_STICKER || type == TYPE_EMOJIS) {
            return false;
        } else if (messageOwner.fwd_from != null && messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel && !isOutOwner()) {
            return true;
        } else if (isFromUser()) {
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(messageOwner.from_id.user_id);
            if (user != null && user.bot && ("reviews_bot".equals(UserObject.getPublicUsername(user)) || "ReviewInsightsBot".equals(UserObject.getPublicUsername(user)))) {
                return true;
            }
            if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaEmpty || getMedia(messageOwner) == null || getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && !(getMedia(messageOwner).webpage instanceof TLRPC.TL_webPage)) {
                return false;
            }
            if (user != null && user.bot && !hasExtendedMedia()) {
                return true;
            }
            if (!isOut()) {
                if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGame || getMedia(messageOwner) instanceof TLRPC.TL_messageMediaInvoice && !hasExtendedMedia() || getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage) {
                    return true;
                }
                TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(null, null, messageOwner.peer_id.channel_id) : null;
                if (ChatObject.isChannel(chat) && chat.megagroup) {
                    return ChatObject.isPublic(chat) && !(getMedia(messageOwner) instanceof TLRPC.TL_messageMediaContact) && !(getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGeo);
                }
            }
        } else if (messageOwner.from_id instanceof TLRPC.TL_peerChannel || messageOwner.post) {
            if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && !isOutOwner()) {
                return true;
            }
            if (isSupergroup()) {
                return false;
            }
            if (messageOwner.peer_id.channel_id != 0 && (messageOwner.via_bot_id == 0 && messageOwner.reply_to == null || type != TYPE_STICKER && type != TYPE_ANIMATED_STICKER)) {
                return true;
            }
        }
        return false;
    }

    public boolean isYouTubeVideo() {
        return getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage != null && !TextUtils.isEmpty(getMedia(messageOwner).webpage.embed_url) && "YouTube".equals(getMedia(messageOwner).webpage.site_name);
    }

    private Boolean isEmbedVideoCached;
    public boolean isEmbedVideo() {
        if (isEmbedVideoCached != null) return messageOwner != null && messageOwner.media != null && messageOwner.media.webpage != null && isEmbedVideoCached;
        return isEmbedVideoCached = messageOwner != null && messageOwner.media != null && messageOwner.media.webpage != null && !TextUtils.isEmpty(WebPlayerView.getYouTubeVideoId(messageOwner.media.webpage.url));
    }

    private int getParentWidth() {
        if (preview && parentWidth > 0)
            return parentWidth;
        if (AndroidUtilities.isTablet())
            return AndroidUtilities.getMinTabletSide();
        if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)
            return AndroidUtilities.displaySize.x - dp(50);
        return AndroidUtilities.displaySize.x;
    }

    public boolean sideMenuEnabled;
    public int getMaxMessageTextWidth() {
        int maxWidth = 0;
        if (AndroidUtilities.isTablet() && eventId != 0) {
            generatedWithMinSize = dp(530);
        } else {
            generatedWithMinSize = getParentWidth();
        }
        generatedWithDensity = AndroidUtilities.density;
        generatedWithFontSize = Theme.chat_msgTextPaint != null ? Theme.chat_msgTextPaint.getTextSize() : 0;
        if (hasCode && !isSaved) {
            maxWidth = generatedWithMinSize - dp(45 + 15);
            if (sideMenuEnabled) {
                maxWidth -= dp(64);
            } else if (needDrawAvatarInternal() && !isOutOwner() && !messageOwner.isThreadMessage) {
                maxWidth -= dp(52);
            }
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage != null && "telegram_background".equals(getMedia(messageOwner).webpage.type)) {
            try {
                Uri uri = Uri.parse(getMedia(messageOwner).webpage.url);
                String segment = uri.getLastPathSegment();
                if (uri.getQueryParameter("bg_color") != null) {
                    maxWidth = dp(220);
                } else if (segment.length() == 6 || segment.length() == 13 && segment.charAt(6) == '-') {
                    maxWidth = dp(200);
                }
            } catch (Exception ignore) {}
        } else if (isAndroidTheme()) {
            maxWidth = dp(200);
        }
        if (maxWidth == 0) {
            maxWidth = generatedWithMinSize - dp(type == TYPE_ARTICLE ? 40 : 80);
            if (sideMenuEnabled) {
                maxWidth -= dp(64);
            } else if (needDrawAvatarInternal() && !isOutOwner() && !messageOwner.isThreadMessage) {
                maxWidth -= dp(52);
            }
            if (needDrawShareButton() && (isSaved || !isOutOwner())) {
                maxWidth -= dp(isSaved && isOutOwner() ? 40 : 14);
            }
            if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaGame) {
                maxWidth -= dp(10);
            }
        }
        if (emojiOnlyCount >= 1 && totalAnimatedEmojiCount <= 100 && (emojiOnlyCount - totalAnimatedEmojiCount) < (SharedConfig.getDevicePerformanceClass() >= SharedConfig.PERFORMANCE_CLASS_HIGH ? 100 : 50) && (hasValidReplyMessageObject() || isForwarded())) {
            maxWidth = Math.min(maxWidth, (int) (generatedWithMinSize * .65f));
        }
        return maxWidth;
    }

    public boolean updateSideMenuEnabled(boolean enabled) {
        if (sideMenuEnabled == enabled) return false;
        final boolean wasEnabled = sideMenuEnabled;
//        if (!wasEnabled && enabled || !enabled) {
            sideMenuEnabled = enabled;
            generateLayout(null);
//        }
        return true;
    }

    public void applyTimestampsHighlightForReplyMsg() {
        applyTimestampsHighlightForReplyMsg(messageText);
    }
    public void applyTimestampsHighlightForReplyMsg(CharSequence text) {
        final MessageObject replyMsg = replyMessageObject;
        if (replyMsg == null) return;

        if (replyMsg.isYouTubeVideo()) {
            addUrlsByPattern(isOutOwner(), text, false, 3, Integer.MAX_VALUE, false);
            return;
        }

        if (replyMsg.isVideo()) {
            addUrlsByPattern(isOutOwner(), text, false, 3, (int) replyMsg.getDuration(), false);
            return;
        }

        if (replyMsg.isMusic() || replyMsg.isVoice()) {
            addUrlsByPattern(isOutOwner(), text, false, 4, (int) replyMsg.getDuration(), false);
        }

        if (text == messageText) {
            if (messageOwner != null && (messageOwner.action instanceof TLRPC.TL_messageActionTodoCompletions || messageOwner.action instanceof TLRPC.TL_messageActionTodoAppendTasks)) {
                updateMessageText();
            }
        }
    }

    private boolean applyEntities() {
        generateLinkDescription();
        spoilLoginCode();

        boolean hasEntities;
        if (messageOwner.send_state != MESSAGE_SEND_STATE_SENT) {
            hasEntities = false;
        } else {
            hasEntities = !getEntities().isEmpty();
        }

        boolean useManualParse = !hasEntities && (
            eventId != 0 ||
            messageOwner instanceof TLRPC.TL_message_old ||
            messageOwner instanceof TLRPC.TL_message_old2 ||
            messageOwner instanceof TLRPC.TL_message_old3 ||
            messageOwner instanceof TLRPC.TL_message_old4 ||
            messageOwner instanceof TLRPC.TL_messageForwarded_old ||
            messageOwner instanceof TLRPC.TL_messageForwarded_old2 ||
            messageOwner instanceof TLRPC.TL_message_secret ||
            getMedia(messageOwner) instanceof TLRPC.TL_messageMediaInvoice ||
            isOut() && messageOwner.send_state != MESSAGE_SEND_STATE_SENT ||
            messageOwner.id < 0
        );

        if (useManualParse) {
            addLinks(isOutOwner(), messageText, true, true);
        } else {
            addPhoneLinks(messageText);
        }
        if (isYouTubeVideo()) {
            addUrlsByPattern(isOutOwner(), messageText, false, 3, Integer.MAX_VALUE, false);
        } else {
            applyTimestampsHighlightForReplyMsg();
        }

        if (!(messageText instanceof Spannable)) {
            messageText = new SpannableStringBuilder(messageText);
        }
        return addEntitiesToText(messageText, useManualParse);
    }

    public static StaticLayout makeStaticLayout(CharSequence text_, TextPaint paint, int width, float lineSpacingMult, float lineSpacingAdd, boolean dontIncludePad) {
        return makeStaticLayout(text_, paint, width, lineSpacingMult, lineSpacingAdd, dontIncludePad, Layout.Alignment.ALIGN_NORMAL);
    }

    public static StaticLayout makeStaticLayout(CharSequence text_, TextPaint paint, int width, float lineSpacingMult, float lineSpacingAdd, boolean dontIncludePad, Layout.Alignment alignment) {
        if (width <= 0) width = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final CharSequence text = || messageOwner.post) {
            return isOutOwnerCached = false;
        }
        if (messageOwner.fwd_from == null) {
            return isOutOwnerCached = true;
        }
        return isOutOwnerCached = messageOwner.fwd_from.saved_from_peer == null || messageOwner.fwd_from.saved_from_peer.user_id == selfUserId;
    }

    public boolean needDrawAvatar() {
        if (type == TYPE_JOINED_CHANNEL) {
            return false;
        }
        if (isRepostPreview) {
            return true;
        }
        if (isSaved) {
            return true;
        }
        if (forceAvatar || customAvatarDrawable != null) {
            return true;
        }
        if (searchType != 0) {
            return true;
        }
        boolean channelSignatureProfiles = false;
        if (getDialogId() < 0) {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-getDialogId());
            channelSignatureProfiles = (chat != null && chat.signature_profiles);
        } else {
            channelSignatureProfiles = getDialogId() == UserObject.VERIFY;
        }
        return !isSponsored() && (isFromUser() || isFromGroup() || channelSignatureProfiles || eventId != 0 || messageOwner.fwd_from != null && messageOwner.fwd_from.saved_from_peer != null);
    }

    private boolean needDrawAvatarInternal() {
        if (isRepostPreview) {
            return true;
        }
        if (isSaved) {
            return true;
        }
        if (forceAvatar || customAvatarDrawable != null || messageOwner != null && messageOwner.guestchat_via_from != null) {
            return true;
        }
        if (searchType != 0) {
            return true;
        }
        boolean channelSignatureProfiles = false;
        if (getDialogId() < 0) {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-getDialogId());
            channelSignatureProfiles = (chat != null && chat.signature_profiles);
        } else {
            channelSignatureProfiles = getDialogId() == UserObject.VERIFY;
        }
        return !isSponsored() && (isFromChat() && isFromUser() || isFromGroup() || channelSignatureProfiles || eventId != 0 || messageOwner.fwd_from != null && messageOwner.fwd_from.saved_from_peer != null);
    }

    public boolean isFromChat() {
        if (getDialogId() == UserConfig.getInstance(currentAccount).clientUserId) {
            return true;
        }
        TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(null, null, messageOwner.peer_id.channel_id) : null;
        if (ChatObject.isChannel(chat) && chat.megagroup || messageOwner.peer_id != null && messageOwner.peer_id.chat_id != 0) {
            return true;
        }
        if (messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0) {
            return chat != null && chat.megagroup;
        }
        return false;
    }

    public static long getFromChatId(TLRPC.Message message) {
        return getPeerId(message.from_id);
    }

    public static long getObjectPeerId(TLObject peer) {
        if (peer == null) {
            return 0;
        }
        if (peer instanceof TLRPC.Chat) {
            return -((TLRPC.Chat) peer).id;
        } else if (peer instanceof TLRPC.User) {
            return ((TLRPC.User) peer).id;
        }
        return 0;
    }

    public static long getPeerId(TLRPC.Peer peer) {
        if (peer == null) {
            return 0;
        }
        if (peer instanceof TLRPC.TL_peerChat) {
            return -peer.chat_id;
        } else if (peer instanceof TLRPC.TL_peerChannel) {
            return -peer.channel_id;
        } else {
            return peer.user_id;
        }
    }

    public static boolean peersEqual(TLRPC.InputPeer a, TLRPC.InputPeer b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof TLRPC.TL_inputPeerChat && b instanceof TLRPC.TL_inputPeerChat) {
            return a.chat_id == b.chat_id;
        }
        if (a instanceof TLRPC.TL_inputPeerChannel && b instanceof TLRPC.TL_inputPeerChannel) {
            return a.channel_id == b.channel_id;
        }
        if (a instanceof TLRPC.TL_inputPeerUser && b instanceof TLRPC.TL_inputPeerUser) {
            return a.user_id == b.user_id;
        }
        if (a instanceof TLRPC.TL_inputPeerSelf && b instanceof TLRPC.TL_inputPeerSelf) {
            return true;
        }
        return false;
    }

    public static boolean peersEqual(TLRPC.InputPeer a, TLRPC.Peer b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof TLRPC.TL_inputPeerChat && b instanceof TLRPC.TL_peerChat) {
            return a.chat_id == b.chat_id;
        }
        if (a instanceof TLRPC.TL_inputPeerChannel && b instanceof TLRPC.TL_peerChannel) {
            return a.channel_id == b.channel_id;
        }
        if (a instanceof TLRPC.TL_inputPeerUser && b instanceof TLRPC.TL_peerUser) {
            return a.user_id == b.user_id;
        }
        return false;
    }

    public static boolean peersEqual(TLRPC.Peer a, TLRPC.Peer b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof TLRPC.TL_peerChat && b instanceof TLRPC.TL_peerChat) {
            return a.chat_id == b.chat_id;
        }
        if (a instanceof TLRPC.TL_peerChannel && b instanceof TLRPC.TL_peerChannel) {
            return a.channel_id == b.channel_id;
        }
        if (a instanceof TLRPC.TL_peerUser && b instanceof TLRPC.TL_peerUser) {
            return a.user_id == b.user_id;
        }
        return false;
    }

    public static boolean peersEqual(TLRPC.Chat a, TLRPC.Peer b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (ChatObject.isChannel(a) && b instanceof TLRPC.TL_peerChannel) {
            return a.id == b.channel_id;
        }
        if (!ChatObject.isChannel(a) && b instanceof TLRPC.TL_peerChat) {
            return a.id == b.chat_id;
        }
        return false;
    }

    public long getFromChatId() {
        return getFromChatId(messageOwner);
    }

    public long getChatId() {
        if (messageOwner.peer_id instanceof TLRPC.TL_peerChat) {
            return messageOwner.peer_id.chat_id;
        } else if (messageOwner.peer_id instanceof TLRPC.TL_peerChannel) {
            return messageOwner.peer_id.channel_id;
        }
        return 0;
    }

    public TLRPC.Peer getFromPeer() {
        if (messageOwner != null) {
            return messageOwner.from_id;
        }
        return null;
    }

    public TLObject getFromPeerObject() {
        if (messageOwner != null) {
            if (messageOwner.from_id instanceof TLRPC.TL_peerChannel_layer131 ||
                messageOwner.from_id instanceof TLRPC.TL_peerChannel) {
                return MessagesController.getInstance(currentAccount).getChat(messageOwner.from_id.channel_id);
            } else if (
                messageOwner.from_id instanceof TLRPC.TL_peerUser_layer131 ||
                messageOwner.from_id instanceof TLRPC.TL_peerUser
            ) {
                return MessagesController.getInstance(currentAccount).getUser(messageOwner.from_id.user_id);
            } else if (
                messageOwner.from_id instanceof TLRPC.TL_peerChat_layer131 ||
                messageOwner.from_id instanceof TLRPC.TL_peerChat
            ) {
                return MessagesController.getInstance(currentAccount).getChat(messageOwner.from_id.chat_id);
            }
        }
        return null;
    }

    public TLObject getPeerObject() {
        if (messageOwner != null) {
            if (messageOwner.peer_id instanceof TLRPC.TL_peerChannel_layer131 ||
                messageOwner.peer_id instanceof TLRPC.TL_peerChannel) {
                return MessagesController.getInstance(currentAccount).getChat(messageOwner.peer_id.channel_id);
            } else if (
                messageOwner.peer_id instanceof TLRPC.TL_peerUser_layer131 ||
                messageOwner.peer_id instanceof TLRPC.TL_peerUser
            ) {
                return MessagesController.getInstance(currentAccount).getUser(messageOwner.peer_id.user_id);
            } else if (
                messageOwner.peer_id instanceof TLRPC.TL_peerChat_layer131 ||
                messageOwner.peer_id instanceof TLRPC.TL_peerChat
            ) {
                return MessagesController.getInstance(currentAccount).getChat(messageOwner.peer_id.chat_id);
            }
        }
        return null;
    }

    public static String getPeerObjectName(TLObject object) {
        if (object instanceof TLRPC.User) {
            return UserObject.getUserName((TLRPC.User) object);
        } else if (object instanceof TLRPC.Chat) {
            return ((TLRPC.Chat) object).title;
        }
        return "DELETED";
    }

    public boolean isFromUser() {
        return messageOwner.from_id instanceof TLRPC.TL_peerUser && !messageOwner.post;
    }

    public boolean isFromChannel() {
        TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(null, null, messageOwner.peer_id.channel_id) : null;
        if (messageOwner.peer_id instanceof TLRPC.TL_peerChannel && ChatObject.isChannelAndNotMegaGroup(chat)) {
            return true;
        }
        chat = messageOwner.from_id != null && messageOwner.from_id.channel_id != 0 ? getChat(null, null, messageOwner.from_id.channel_id) : null;
        if (messageOwner.from_id instanceof TLRPC.TL_peerChannel && ChatObject.isChannelAndNotMegaGroup(chat)) {
            return true;
        }
        return false;
    }

    public boolean isFromGroup() {
        if (messageOwner == null) return false;
        TLRPC.Chat chat = messageOwner.peer_id != null && messageOwner.peer_id.channel_id != 0 ? getChat(null, null, messageOwner.peer_id.channel_id) : null;
        return messageOwner.from_id instanceof TLRPC.TL_peerChannel && ChatObject.isChannel(chat) && chat.megagroup;
    }

    public boolean isForwardedChannelPost() {
        return messageOwner.from_id instanceof TLRPC.TL_peerChannel && messageOwner.fwd_from != null && messageOwner.fwd_from.channel_post != 0 && messageOwner.fwd_from.saved_from_peer instanceof TLRPC.TL_peerChannel && messageOwner.from_id.channel_id == messageOwner.fwd_from.saved_from_peer.channel_id;
    }

    public boolean isUnread() {
        return messageOwner != null && messageOwner.unread;
    }

    public boolean isEdited() {
        return messageOwner != null && (messageOwner.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0 && messageOwner.edit_date != 0 && !messageOwner.edit_hide;
    }

    public boolean isContentUnread() {
        return messageOwner.media_unread;
    }

    public void setIsRead() {
        messageOwner.unread = false;
    }

    public static int getUnreadFlags(TLRPC.Message message) {
        int flags = 0;
        if (!message.unread) {
            flags |= 1;
        }
        if (!message.media_unread) {
            flags |= 2;
        }
        return flags;
    }

    public void setContentIsRead() {
        messageOwner.media_unread = false;
    }

    public int getId() {
        return messageOwner.id;
    }

    public int getRealId() {
        return messageOwner.realId != 0 ? messageOwner.realId : messageOwner.id;
    }

    public static long getMessageSize(TLRPC.Message message) {
        return getMediaSize(getMedia(message));
    }

    public static long getMediaSize(TLRPC.MessageMedia media) {
        TLRPC.Document document;
        if (media instanceof TLRPC.TL_messageMediaWebPage && media.webpage != null) {
            document = media.webpage.document;
        } else if (media instanceof TLRPC.TL_messageMediaGame) {
            document = media.game.document;
        } else {
            document = media != null ? media.document : null;
        }
        if (document != null) {
            return document.size;
        }
        return 0;
    }

    public long getSize() {
        if (highestQuality != null) {
            return highestQuality.document.size;
        } else if (thumbQuality != null) {
            return thumbQuality.document.size;
        } else if (cachedQuality != null) {
            return cachedQuality.document.size;
        }
        return getMessageSize(messageOwner);
    }

    public static void fixMessagePeer(ArrayList<TLRPC.Message> messages, long channelId) {
        if (messages == null || messages.isEmpty() || channelId == 0) {
            return;
        }
        for (int a = 0; a < messages.size(); a++) {
            TLRPC.Message message = messages.get(a);
            if (message instanceof TLRPC.TL_messageEmpty) {
                message.peer_id = new TLRPC.TL_peerChannel();
                message.peer_id.channel_id = channelId;
            }
        }
    }

    public long getChannelId() {
        return getChannelId(messageOwner);
    }

    public static long getChannelId(TLRPC.Message message) {
        if (message.peer_id != null) {
            return message.peer_id.channel_id;
        }
        return 0;
    }

    public static long getChatId(TLRPC.Message message) {
        if (message == null) {
            return 0;
        }
        if (message.peer_id instanceof TLRPC.TL_peerChat) {
            return message.peer_id.chat_id;
        } else if (message.peer_id instanceof TLRPC.TL_peerChannel) {
            return message.peer_id.channel_id;
        }
        return 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(int currentAccount, TLRPC.Message message) {
        if (message != null && message.media != null && (isVoiceDocument(getDocument(message)) || isRoundVideoMessage(message)) && message.media.ttl_seconds == 0x7FFFFFFF) {
            return true;
        }
        if (getMedia(message) instanceof TLRPC.TL_messageMediaPaidMedia) {
            return true;
        }
//        if (MessagesController.getInstance(currentAccount).isChatNoForwards(getChatId(message)) || message != null && message.noforwards) {
//            return true;
//        }
        if (message instanceof TLRPC.TL_message_secret) {
            return (getMedia(message) instanceof TLRPC.TL_messageMediaPhoto || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60;
        } else {
            return (getMedia(message) instanceof TLRPC.TL_messageMediaPhoto || getMedia(message) instanceof TLRPC.TL_messageMediaDocument) && getMedia(message).ttl_seconds != 0;
        }
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(currentAccount, messageOwner);
    }

    public static boolean isSecretPhotoOrVideo(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            return (getMedia(message) instanceof TLRPC.TL_messageMediaPhoto || isRoundVideoMessage(message) || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60;
        } else if (message instanceof TLRPC.TL_message) {
            return (getMedia(message) instanceof TLRPC.TL_messageMediaPhoto || getMedia(message) instanceof TLRPC.TL_messageMediaDocument) && getMedia(message).ttl_seconds != 0;
        }
        return false;
    }

    public static boolean isSecretMedia(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            return (getMedia(message) instanceof TLRPC.TL_messageMediaPhoto || isRoundVideoMessage(message) || isVideoMessage(message)) && getMedia(message).ttl_seconds != 0;
        } else if (message instanceof TLRPC.TL_message) {
            return (getMedia(message) instanceof TLRPC.TL_messageMediaPhoto || getMedia(message) instanceof TLRPC.TL_messageMediaDocument) && getMedia(message).ttl_seconds != 0;
        }
        return false;
    }

    public boolean needDrawBluredPreview() {
        if (isRepostPreview) {
            return false;
        }
        if (hasExtendedMediaPreview()) {
            return true;
        } else if (messageOwner instanceof TLRPC.TL_message_secret) {
            int ttl = Math.max(messageOwner.ttl, getMedia(messageOwner).ttl_seconds);
            return ttl > 0 && ((getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto || isVideo() || isGif()) && ttl <= 60 || isRoundVideo());
        } else if (messageOwner instanceof TLRPC.TL_message) {
            return (getMedia(messageOwner) != null && getMedia(messageOwner).ttl_seconds != 0) && (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto || getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument);
        }
        return false;
    }

    public boolean isSecret() {
        return messageOwner instanceof TLRPC.TL_message_secret;
    }

    public boolean isSecretMedia() {
        if (messageOwner instanceof TLRPC.TL_message_secret) {
            return (((getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto) || isGif()) && messageOwner.ttl > 0 && messageOwner.ttl <= 60 || isVoice() || isRoundVideo() || isVideo());
        } else if (messageOwner instanceof TLRPC.TL_message) {
            return (getMedia(messageOwner) != null && getMedia(messageOwner).ttl_seconds != 0) && (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto || getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument);
        }
        return false;
    }

    public static void setUnreadFlags(TLRPC.Message message, int flag) {
        message.unread = (flag & 1) == 0;
        message.media_unread = (flag & 2) == 0;
    }

    public static boolean isUnread(TLRPC.Message message) {
        return message.unread;
    }

    public static boolean isContentUnread(TLRPC.Message message) {
        return message.media_unread;
    }

    public boolean isSavedFromMegagroup() {
        if (messageOwner.fwd_from != null && messageOwner.fwd_from.saved_from_peer != null && messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.fwd_from.saved_from_peer.channel_id);
            return ChatObject.isMegagroup(chat);
        }
        return false;
    }

    public static boolean isOut(TLRPC.Message message) {
        return message.out;
    }

    public long getDialogId() {
        return getDialogId(messageOwner);
    }

    public boolean canStreamVideo() {
        if (hasVideoQualities()) return true;
        TLRPC.Document document = getDocument();
        if (document == null || document instanceof TLRPC.TL_documentEncrypted) {
            return false;
        }
        if (SharedConfig.streamAllVideo) {
            return true;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.supports_streaming;
            }
        }
        if (SharedConfig.streamMkv && "video/x-matroska".equals(document.mime_type)) {
            return true;
        }
        return false;
    }

    public static long getDialogId(TLRPC.Message message) {
        if (message.dialog_id == 0 && message.peer_id != null) {
            if (message.peer_id.chat_id != 0) {
                message.dialog_id = -message.peer_id.chat_id;
            } else if (message.peer_id.channel_id != 0) {
                message.dialog_id = -message.peer_id.channel_id;
            } else if (message.from_id == null || isOut(message) || message.guestchat_via_from != null) {
                message.dialog_id = message.peer_id.user_id;
            } else {
                message.dialog_id = message.from_id.user_id;
            }
        }
        return message.dialog_id;
    }

    public long getSavedDialogId() {
        return getSavedDialogId(UserConfig.getInstance(currentAccount).getClientUserId(), messageOwner);
    }

    public static long getSavedDialogId(long self, TLRPC.Message message) {
        if (message.saved_peer_id != null) {
            if (message.saved_peer_id.chat_id != 0) {
                return -message.saved_peer_id.chat_id;
            } else if (message.saved_peer_id.channel_id != 0) {
                return -message.saved_peer_id.channel_id;
            } else {
                return message.saved_peer_id.user_id;
            }
        }
        if (message.from_id.user_id == self) {
            if (message.fwd_from != null && message.fwd_from.saved_from_peer != null) {
                return DialogObject.getPeerDialogId(message.fwd_from.saved_from_peer);
            } else if (message.fwd_from != null && message.fwd_from.from_id != null) {
                return self;
            } else if (message.fwd_from != null) {
                return UserObject.ANONYMOUS;
            } else {
                return self;
            }
        }
        return 0;
    }

    public static TLRPC.Peer getSavedDialogPeer(long self, TLRPC.Message message) {
        if (message.saved_peer_id != null) {
            return message.saved_peer_id;
        }
        if (message.peer_id != null && message.peer_id.user_id == self && message.from_id != null && message.from_id.user_id == self) {
            if (message.fwd_from != null && message.fwd_from.saved_from_peer != null) {
                return message.fwd_from.saved_from_peer;
            } else if (message.fwd_from != null && message.fwd_from.from_id != null) {
                TLRPC.Peer peer = new TLRPC.TL_peerUser();
                peer.user_id = self;
                return peer;
            } else if (message.fwd_from != null) {
                TLRPC.Peer peer = new TLRPC.TL_peerUser();
                peer.user_id = 2666000L;
                return peer;
            } else {
                TLRPC.Peer peer = new TLRPC.TL_peerUser();
                peer.user_id = self;
                return peer;
            }
        }
        return null;
    }

    public boolean canSetReaction() {
        if (messageOwner instanceof TLRPC.TL_messageService)
            return messageOwner.reactions_are_possible;
        return true;
    }

    public boolean isSending() {
        return messageOwner.send_state == MESSAGE_SEND_STATE_SENDING && messageOwner.id < 0;
    }

    public boolean isEditing() {
        return messageOwner.send_state == MESSAGE_SEND_STATE_EDITING && messageOwner.id > 0;
    }

    public boolean isEditingMedia() {
        if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
            return getMedia(messageOwner).photo.id == 0;
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
            return getMedia(messageOwner).document.dc_id == 0;
        }
        return false;
    }

    public boolean isSendError() {
        return messageOwner.send_state == MESSAGE_SEND_STATE_SEND_ERROR && messageOwner.id < 0 || scheduled && messageOwner.id > 0 && messageOwner.date < ConnectionsManager.getInstance(currentAccount).getCurrentTime() - (messageOwner.video_processing_pending ? 5 * 60 : 60);
    }

    public boolean isSent() {
        return messageOwner.send_state == MESSAGE_SEND_STATE_SENT || messageOwner.id > 0;
    }

    public int getSecretTimeLeft() {
        int secondsLeft = messageOwner.ttl;
        if (messageOwner.destroyTime != 0) {
            secondsLeft = Math.max(0, messageOwner.destroyTime - ConnectionsManager.getInstance(currentAccount).getCurrentTime());
        }
        return secondsLeft;
    }

    private CharSequence secretOnceSpan;
    private CharSequence secretPlaySpan;

    public CharSequence getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        if (messageOwner.ttl == 0x7FFFFFFF) {
            if (secretOnceSpan == null) {
                secretOnceSpan = new SpannableString("v");
                ColoredImageSpan span = new ColoredImageSpan(R.drawable.mini_viewonce);
                span.setTranslateX(-dp(3));
                span.setWidth(dp(13));
                ((Spannable) secretOnceSpan).setSpan(span, 0, secretOnceSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return TextUtils.concat(secretOnceSpan, "1");
        }
        int secondsLeft = getSecretTimeLeft();
        String str;
        if (secondsLeft < 60) {
            str = secondsLeft + "s";
        } else {
            str = secondsLeft / 60 + "m";
        }
        if (secretPlaySpan == null) {
            secretPlaySpan = new SpannableString("p");
            ColoredImageSpan span = new ColoredImageSpan(R.drawable.play_mini_video);
            span.setTranslateX(dp(1));
            span.setWidth(dp(13));
            ((Spannable) secretPlaySpan).setSpan(span, 0, secretPlaySpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return TextUtils.concat(secretPlaySpan, str);
    }

    public String getDocumentName() {
        return FileLoader.getDocumentFileName(getDocument());
    }

    public static boolean isWebM(TLRPC.Document document) {
        return document != null && "video/webm".equals(document.mime_type);
    }

    public static boolean isVideoSticker(TLRPC.Document document) {
        return document != null && isVideoStickerDocument(document);
    }

    public boolean isVideoSticker() {
        return getDocument() != null && isVideoStickerDocument(getDocument());
    }

    public static boolean isStickerDocument(TLRPC.Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                    return "image/webp".equals(document.mime_type) || "video/webm".equals(document.mime_type);
                }
            }
        }
        return false;
    }

    public static boolean isVideoStickerDocument(TLRPC.Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeSticker ||
                    attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                    return "video/webm".equals(document.mime_type);
                }
            }
        }
        return false;
    }

    public static boolean isStickerHasSet(TLRPC.Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeSticker && attribute.stickerset != null && !(attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerDocument(TLRPC.Document document, boolean allowWithoutSet) {
        if (document != null && ("application/x-tgsticker".equals(document.mime_type) && !document.thumbs.isEmpty() || "application/x-tgsdice".equals(document.mime_type))) {
            if (allowWithoutSet) {
                return true;
            }
            for (int a = 0, N = document.attributes.size(); a < N; a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                    return attribute.stickerset instanceof TLRPC.TL_inputStickerSetShortName;
                } else if (attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAnyKindOfStickerOrEmoji(TLRPC.Document document) {
        if (document == null) {
            return false;
        }

        for (int a = 0, N = document.attributes.size(); a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                return true;
            } else if (attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                return true;
            }
        }

        return false;
    }

    public static boolean canAutoplayAnimatedSticker(TLRPC.Document document) {
        return (isAnimatedStickerDocument(document, true) || isVideoStickerDocument(document)) && LiteMode.isEnabled(LiteMode.FLAG_ANIMATED_STICKERS_KEYBOARD);
    }

    public static boolean isMaskDocument(TLRPC.Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeSticker && attribute.mask) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(TLRPC.Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    return attribute.voice;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceWebDocument(WebFile webDocument) {
        return webDocument != null && webDocument.mime_type.equals("audio/ogg");
    }

    public static boolean isImageWebDocument(WebFile webDocument) {
        return webDocument != null && !isGifDocument(webDocument) && webDocument.mime_type.startsWith("image/");
    }

    public static boolean isVideoWebDocument(WebFile webDocument) {
        return webDocument != null && webDocument.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(TLRPC.Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    return !attribute.voice;
                }
            }
            if (!TextUtils.isEmpty(document.mime_type)) {
                String mime = document.mime_type.toLowerCase();
                if (mime.equals("audio/flac") || mime.equals("audio/ogg") || mime.equals("audio/opus") || mime.equals("audio/x-opus+ogg")) {
                    return true;
                } else if (mime.equals("application/octet-stream") && FileLoader.getDocumentFileName(document).endsWith(".opus")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static TLRPC.VideoSize getDocumentVideoThumb(TLRPC.Document document) {
        if (document == null || document.video_thumbs.isEmpty()) {
            return null;
        }
        return document.video_thumbs.get(0);
    }

    public static boolean isVideoDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        boolean isAnimated = false;
        boolean isVideo = false;
        String filename = null;
        int width = 0;
        int height = 0;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                if (attribute.round_message) {
                    return false;
                }
                isVideo = true;
                width = attribute.w;
                height = attribute.h;
            } else if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                isAnimated = true;
            } else if (attribute instanceof TLRPC.TL_documentAttributeFilename) {
                filename = attribute.file_name;
            }
        }
        if (filename != null) {
            int index = filename.lastIndexOf(".");
            if (index >= 0) {
                String ext = filename.substring(index + 1);
                if (isV(ext)) {
                    return false;
                }
            }
        }
        if (isAnimated && (width > 1280 || height > 1280)) {
            isAnimated = false;
        }
        if (SharedConfig.streamMkv && !isVideo && "video/x-matroska".equals(document.mime_type)) {
            isVideo = true;
        }
        return isVideo && !isAnimated;
    }

    public static boolean isV(String ext) {
        if (ext == null) return true;
        switch (ext.toLowerCase().hashCode()) {
            case 0x17890: case 0x1794a: case 0x17a1c: case 0x17a21:
            case 0x17af5: case 0x17c14: case 0x17c15: case 0x17d07:
            case 0x18085: case 0x180a8: case 0x1813a: case 0x18181:
            case 0x1819f: case 0x181e5: case 0x181f8: case 0x1840a:
            case 0x184dc: case 0x184e4: case 0x184fe: case 0x185a8:
            case 0x18770: case 0x1889f: case 0x188be: case 0x1897a:
            case 0x18a12: case 0x18a5e: case 0x18c54: case 0x18d27:
            case 0x18dde: case 0x1909c: case 0x190ac: case 0x193ec:
            case 0x1940e: case 0x194d5: case 0x1968a: case 0x1974d:
            case 0x197e1: case 0x197ee: case 0x197f3: case 0x1981a:
            case 0x1981f: case 0x19886: case 0x1988b: case 0x19a1b:
            case 0x19c3c: case 0x19c47: case 0x19c4f: case 0x19eda:
            case 0x1a000: case 0x1a285: case 0x1a329: case 0x1a59c:
            case 0x1a5d4: case 0x1a5d5: case 0x1a735: case 0x1a75e:
            case 0x1a77d: case 0x1a783: case 0x1a78a: case 0x1a78e:
            case 0x1a81a: case 0x1ad00: case 0x1ad24: case 0x1b0a2:
            case 0x1b0d1: case 0x1b123: case 0x1b18d: case 0x1b1cc:
            case 0x1b2a1: case 0x1b2a5: case 0x1b31e: case 0x1b33c:
            case 0x1b37a: case 0x1b386: case 0x1b639: case 0x1b848:
            case 0x1b894: case 0x1b8de: case 0x1b9db: case 0x1b9ec:
            case 0x1bc22: case 0x1bc24: case 0x1bcad: case 0x1bcbe:
            case 0x1bdb5: case 0x1becd: case 0x1c079: case 0x1c0ea:
            case 0x1c11a: case 0x1bdd2: case 0x1c3b7: case 0x1c56f:
            case 0x1c739: case 0x1c747: case 0x1c781: case 0x1c9e2:
            case 0x1cb21: case 0x1cc50: case 0x1ccb2: case 0x1cd07:
            case 0x1cd0a: case 0x1cd0c: case 0x1cea7: case 0x1d09b:
            case 0x18538: case 0x18417: case 0x194e1: case 0x1b178:
            case 0x17a7e: case 0x1b274: case 0x1c1d9: case 0x1d017:
            case 0x1c270: case 0x1ba8b: case 0xe30: case 0x21bd7bf:
            case 0x1d77f: case 0x333e3f: case 0x348d4c: case 0xe55:
            case 0x349c84: case 0xc9e5d0cb: case 0xe09: case 0xda6:
            case 0x2e06d4: case 0xa473e8a5: case 0xeac: case 0xd97:
            case 0x2dd5ba: case 0x3498c3: case 0x1b0f2: case 0xd49:
            case 0x35c854: case 0x35ce71: case 0xc70: case 0x18203:
            case 0x2f2240: case 0x1cfff: case 0xdfc: case 0x35c681:
            case 0x383059: case 0xdfd: case 0xd1075a44: case 0xc8f:
            case 0x31ece8: case 0xc22: case 0xab2f7e36: case 0xedc:
            case 0x3107ab: case 0xb549144c: case 0x1be64:
            case 0xebd32e77: case 0x6cc0c23:
                return true;
            default:
                return false;
        }
    }

    public TLRPC.Document getDocument() {
        if (emojiAnimatedSticker != null) {
            return emojiAnimatedSticker;
        }
        if (hasVideoQualities() && highestQuality != null) {
            return highestQuality.document;
        }
        return getDocument(messageOwner);
    }

    public TLRPC.Document getDocumentFast() {
        if (emojiAnimatedSticker != null) {
            return emojiAnimatedSticker;
        }
        return getDocument(messageOwner);
    }

    public static TLRPC.Document getDocument(TLRPC.Message message) {
        if (message != null && message.rich_message != null)
            return findVideo(message.rich_message);
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return getMedia(message).webpage.document;
        } else if (getMedia(message) instanceof TLRPC.TL_messageMediaGame) {
            return getMedia(message).game.document;
        } else if (getMedia(message) instanceof TLRPC.TL_messageMediaStory) {
            TLRPC.TL_messageMediaStory story = (TLRPC.TL_messageMediaStory) getMedia(message);
            if (story.storyItem != null && story.storyItem.media != null && story.storyItem.media.document != null)
                return story.storyItem.media.document;
        } else if (getMedia(message) instanceof TLRPC.TL_messageMediaPaidMedia) {
            TLRPC.TL_messageMediaPaidMedia paidMedia = (TLRPC.TL_messageMediaPaidMedia) getMedia(message);
            if (paidMedia.extended_media.size() == 1 && paidMedia.extended_media.get(0) instanceof TLRPC.TL_messageExtendedMedia) {
                return ((TLRPC.TL_messageExtendedMedia) paidMedia.extended_media.get(0)).media.document;
            }
        }
        return getMedia(message) != null ? getMedia(message).document : null;
    }

    public TLRPC.Photo getPhoto() {
        return getPhoto(messageOwner);
    }

    public static TLRPC.Photo getPhoto(TLRPC.Message message) {
        if (message != null && message.rich_message != null)
            return findPhoto(message.rich_message);
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return getMedia(message).webpage.photo;
        }
        return getMedia(message) != null ? getMedia(message).photo : null;
    }

    public static TLRPC.Document findVideo(TL_iv.RichMessage rich_message) {
        if (rich_message == null) return null;
        return findVideo(rich_message.blocks, rich_message);
    }
    public static TLRPC.Document findVideo(ArrayList<TL_iv.PageBlock> blocks, TL_iv.RichMessage rich_message) {
        if (blocks == null || rich_message == null) return null;
        for (final TL_iv.PageBlock block : blocks) {
            if (block instanceof TL_iv.pageBlockVideo) {
                return AndroidUtilities.findDocument(rich_message.documents, ((TL_iv.pageBlockVideo) block).video_id);
            } else if (block instanceof TL_iv.pageBlockCollage) {
                return findVideo(((TL_iv.pageBlockCollage) block).items, rich_message);
            } else if (block instanceof TL_iv.pageBlockSlideshow) {
                return findVideo(((TL_iv.pageBlockSlideshow) block).items, rich_message);
            }
        }
        return null;
    }

    public static TLRPC.Document findAudio(TL_iv.RichMessage rich_message) {
        if (rich_message == null) return null;
        return findAudio(rich_message.blocks, rich_message);
    }
    public static TLRPC.Document findAudio(ArrayList<TL_iv.PageBlock> blocks, TL_iv.RichMessage rich_message) {
        if (blocks == null || rich_message == null) return null;
        for (final TL_iv.PageBlock block : blocks) {
            if (block instanceof TL_iv.pageBlockAudio) {
                return AndroidUtilities.findDocument(rich_message.documents, ((TL_iv.pageBlockAudio) block).audio_id);
            } else if (block instanceof TL_iv.pageBlockCollage) {
                return findAudio(((TL_iv.pageBlockCollage) block).items, rich_message);
            } else if (block instanceof TL_iv.pageBlockSlideshow) {
                return findAudio(((TL_iv.pageBlockSlideshow) block).items, rich_message);
            }
        }
        return null;
    }

    public static TLRPC.Photo findPhoto(TL_iv.RichMessage rich_message) {
        if (rich_message == null) return null;
        return findPhoto(rich_message.blocks, rich_message);
    }
    public static TLRPC.Photo findPhoto(ArrayList<TL_iv.PageBlock> blocks, TL_iv.RichMessage rich_message) {
        if (blocks == null || rich_message == null) return null;
        for (final TL_iv.PageBlock block : blocks) {
            if (block instanceof TL_iv.pageBlockPhoto) {
                return AndroidUtilities.findPhoto(rich_message.photos, ((TL_iv.pageBlockPhoto) block).photo_id);
            } else if (block instanceof TL_iv.pageBlockCollage) {
                return findPhoto(((TL_iv.pageBlockCollage) block).items, rich_message);
            } else if (block instanceof TL_iv.pageBlockSlideshow) {
                return findPhoto(((TL_iv.pageBlockSlideshow) block).items, rich_message);
            }
        }
        return null;
    }

    public static boolean isStickerMessage(TLRPC.Message message) {
        return getMedia(message) != null && isStickerDocument(getMedia(message).document);
    }

    public static boolean isAnimatedStickerMessage(TLRPC.Message message) {
        boolean isSecretChat = DialogObject.isEncryptedDialog(message.dialog_id);
        if (isSecretChat && message.stickerVerified != 1) {
            return false;
        }
        return getMedia(message) != null && isAnimatedStickerDocument(getMedia(message).document, !isSecretChat || message.out);
    }

    public static boolean isLocationMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaGeo || getMedia(message) instanceof TLRPC.TL_messageMediaGeoLive || getMedia(message) instanceof TLRPC.TL_messageMediaVenue;
    }

    public static boolean isMaskMessage(TLRPC.Message message) {
        return getMedia(message) != null && isMaskDocument(getMedia(message).document);
    }

    public static boolean isMusicMessage(TLRPC.Message message) {
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return isMusicDocument(getMedia(message).webpage.document);
        }
        return getMedia(message) != null && isMusicDocument(getMedia(message).document);
    }

    public static boolean isGifMessage(TLRPC.Message message) {
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return isGifDocument(getMedia(message).webpage.document);
        }
        return getMedia(message) != null && isGifDocument(getMedia(message).document, message.grouped_id != 0);
    }

    public static boolean isRoundVideoMessage(TLRPC.Message message) {
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage && getMedia(message).webpage != null) {
            return isRoundVideoDocument(getMedia(message).webpage.document);
        }
        return getMedia(message) != null && isRoundVideoDocument(getMedia(message).document);
    }

    public static boolean isPhoto(TLRPC.Message message) {
        if (message != null && message.rich_message != null)
            return findPhoto(message.rich_message) != null;
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return getMedia(message).webpage.photo instanceof TLRPC.TL_photo && !(getMedia(message).webpage.document instanceof TLRPC.TL_document);
        }
        if (message != null && message.action != null && message.action.photo != null) {
            return message.action.photo instanceof TLRPC.TL_photo;
        }
        return getMedia(message) instanceof TLRPC.TL_messageMediaPhoto;
    }

    public static boolean isVoiceMessage(TLRPC.Message message) {
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return isVoiceDocument(getMedia(message).webpage.document);
        }
        return getMedia(message) != null && isVoiceDocument(getMedia(message).document);
    }

    public static boolean isNewGifMessage(TLRPC.Message message) {
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return isNewGifDocument(getMedia(message).webpage.document);
        }
        return getMedia(message) != null && isNewGifDocument(getMedia(message).document);
    }

    public static boolean isLiveLocationMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(TLRPC.Message message) {
        if (message != null && message.rich_message != null)
            return isVideoDocument(findVideo(message.rich_message));
        if (getMedia(message) != null && isVideoSticker(getMedia(message).document)) {
            return false;
        }
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return isVideoDocument(getMedia(message).webpage.document);
        }
        return getMedia(message) != null && isVideoDocument(getMedia(message).document);
    }

    public static boolean isGameMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaInvoice;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message message) {
        TLRPC.Document document = getDocument(message);
        if (document != null) {
            return getInputStickerSet(document);
        }
        return null;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        for (int a = 0, N = document.attributes.size(); a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker ||
                attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return null;
                }
                return attribute.stickerset;
            }
        }
        return null;
    }

    public static String findAnimatedEmojiEmoticon(TLRPC.Document document) {
        return findAnimatedEmojiEmoticon(document, "\uD83D\uDE00");
    }

    public static String findAnimatedEmojiEmoticon(TLRPC.Document document, String fallback) {
        return findAnimatedEmojiEmoticon(document, fallback, null);
    }

    public static String findAnimatedEmojiEmoticon(TLRPC.Document document, String fallback, Integer currentAccountForFull) {
        if (document == null) {
            return fallback;
        }
        for (int a = 0, N = document.attributes.size(); a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeCustomEmoji ||
                attribute instanceof TLRPC.TL_documentAttributeSticker) {
                if (currentAccountForFull != null) {
                    TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(currentAccountForFull).getStickerSet(attribute.stickerset, true);
                    StringBuilder emoji = new StringBuilder("");
                    if (set != null && set.packs != null) {
                        for (int p = 0; p < set.packs.size(); ++p) {
                            TLRPC.TL_stickerPack pack = set.packs.get(p);
                            if (pack.documents.contains(document.id)) {
                                emoji.append(pack.emoticon);
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(emoji)) {
                        return emoji.toString();
                    }
                }
                return attribute.alt;
            }
        }
        return fallback;
    }


    public static ArrayList<String> findStickerEmoticons(TLRPC.Document document, Integer currentAccountForFull) {
        if (document == null) {
            return null;
        }
        ArrayList<String> emojis = new ArrayList<>();
        for (int a = 0, N = document.attributes.size(); a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeCustomEmoji ||
                    attribute instanceof TLRPC.TL_documentAttributeSticker) {
                if (currentAccountForFull != null) {
                    TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(currentAccountForFull).getStickerSet(attribute.stickerset, true);
                    if (set != null && set.packs != null) {
                        for (int p = 0; p < set.packs.size(); ++p) {
                            TLRPC.TL_stickerPack pack = set.packs.get(p);
                            if (pack.documents.contains(document.id) && Emoji.getEmojiDrawable(pack.emoticon) != null) {
                                emojis.add(pack.emoticon);
                            }
                        }
                    }
                    if (!emojis.isEmpty()) {
                        return emojis;
                    }
                }
                if (!TextUtils.isEmpty(attribute.alt) && Emoji.getEmojiDrawable(attribute.alt) != null) {
                    emojis.add(attribute.alt);
                    return emojis;
                }
            }
        }
        return null;
    }

    public static boolean isAnimatedEmoji(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0, N = document.attributes.size(); a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFreeEmoji(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0, N = document.attributes.size(); a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                return ((TLRPC.TL_documentAttributeCustomEmoji) attribute).free;
            }
        }
        return false;
    }

    public static boolean isTextColorEmoji(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        TLRPC.InputStickerSet set = MessageObject.getInputStickerSet(document);
        for (int a = 0, N = document.attributes.size(); a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetID && attribute.stickerset.id == 1269403972611866647L) {
                    return true;
                }
                return ((TLRPC.TL_documentAttributeCustomEmoji) attribute).text_color;
            }
        }
        return false;
    }

    public static boolean isTextColorSet(TLRPC.TL_messages_stickerSet set) {
        if (set == null || set.set == null) {
            return false;
        }
        if (set.set.text_color) {
            return true;
        }
        if (set.documents == null || set.documents.isEmpty()) {
            return false;
        }
        return MessageObject.isTextColorEmoji(set.documents.get(0));
    }

    public static boolean isPremiumEmojiPack(TLRPC.TL_messages_stickerSet set) {
        if (set != null && set.set != null && !set.set.emojis) {
            return false;
        }
        if (set != null && set.documents != null) {
            for (int i = 0; i < set.documents.size(); ++i) {
                if (!MessageObject.isFreeEmoji(set.documents.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isPremiumEmojiPack(TLRPC.StickerSetCovered covered) {
        if (covered != null && covered.set != null && !covered.set.emojis) {
            return false;
        }
        ArrayList<TLRPC.Document> documents = covered instanceof TLRPC.TL_stickerSetFullCovered ? ((TLRPC.TL_stickerSetFullCovered) covered).documents : covered.covers;
        if (covered != null && documents != null) {
            for (int i = 0; i < documents.size(); ++i) {
                if (!MessageObject.isFreeEmoji(documents.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        if (document == null) {
            return -1;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return -1;
                }
                return attribute.stickerset.id;
            }
        }
        return -1;
    }

    public static String getStickerSetName(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return null;
                }
                return attribute.stickerset.short_name;
            }
        }
        return null;
    }

    public String getStickerChar() {
        TLRPC.Document document = getDocument();
        if (document != null) {
            for (TLRPC.DocumentAttribute attribute : document.attributes) {
                if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                    return attribute.alt;
                }
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        return getApproximateHeight(false);
    }

    private Integer cachedApproximateHeight;
    public int getApproximateHeightCached() {
        if (cachedApproximateHeight != null) return cachedApproximateHeight;
        return cachedApproximateHeight = getApproximateHeight(true);
    }
    public int getApproximateHeight(boolean fast) {
        if (type == TYPE_TEXT) {
            int height = (fast ? textHeightCached() : textHeight()) + (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage instanceof TLRPC.TL_webPage ? dp(100) : 0);
            if (isReply()) {
                height += dp(42);
            }
            return height;
        } else if (type == TYPE_EXTENDED_MEDIA_PREVIEW) {
            return AndroidUtilities.getPhotoSize();
        } else if (type == TYPE_VOICE) {
            return dp(72);
        } else if (type == TYPE_CONTACT) {
            return dp(71);
        } else if (type == TYPE_FILE) {
            return dp(100);
        } else if (type == TYPE_GEO) {
            return dp(114);
        } else if (type == TYPE_MUSIC) {
            return dp(82);
        } else if (type == 10 || type == TYPE_SHARING_OFFER || type == TYPE_GIFT_OFFER || type == TYPE_GIFT_OFFER_REJECTED) {
            return dp(30);
        } else if (type == TYPE_ACTION_PHOTO || type == TYPE_GIFT_PREMIUM || type == TYPE_GIFT_THEME_UPDATE ||type == TYPE_GIFT_STARS || type == TYPE_GIFT_PREMIUM_CHANNEL || type == TYPE_SUGGEST_PHOTO) {
            return dp(50);
        } else if (type == TYPE_SUGGEST_BIRTHDAY) {
            return dp(234);
        } else if (type == TYPE_ROUND_VIDEO) {
            return AndroidUtilities.roundMessageSize;
        } else if (type == TYPE_EMOJIS) {
            return (fast ? textHeightCached() : textHeight()) + dp(30);
        } else if (type == TYPE_STICKER || type == TYPE_ANIMATED_STICKER) {
            float maxHeight = AndroidUtilities.displaySize.y * 0.4f;
            float maxWidth;
            if (AndroidUtilities.isTablet()) {
                maxWidth = AndroidUtilities.getMinTabletSide() * 0.5f;
            } else {
                maxWidth = AndroidUtilities.displaySize.x * 0.5f;
            }
            int photoHeight = 0;
            int photoWidth = 0;
            TLRPC.Document document = getDocument();
            if (document != null) {
                for (int a = 0, N = document.attributes.size(); a < N; a++) {
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        photoWidth = attribute.w;
                        photoHeight = attribute.h;
                        break;
                    }
                }
            }
            if (photoWidth == 0) {
                photoHeight = (int) maxHeight;
                photoWidth = photoHeight + dp(100);
            }
            if (photoHeight > maxHeight) {
                photoWidth *= maxHeight / photoHeight;
                photoHeight = (int) maxHeight;
            }
            if (photoWidth > maxWidth) {
                photoHeight *= maxWidth / photoWidth;
            }
            return photoHeight + dp(14);
        } else {
            int photoHeight;
            int photoWidth;

            if (AndroidUtilities.isTablet()) {
                photoWidth = (int) (AndroidUtilities.getMinTabletSide() * 0.7f);
            } else {
                photoWidth = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7f);
            }
            photoHeight = photoWidth + dp(100);
            if (photoWidth > AndroidUtilities.getPhotoSize()) {
                photoWidth = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight > AndroidUtilities.getPhotoSize()) {
                photoHeight = AndroidUtilities.getPhotoSize();
            }
            TLRPC.PhotoSize currentPhotoObject;
            if (fast) {
                currentPhotoObject = photoThumbs == null || photoThumbs.isEmpty() ? null : photoThumbs.get(0);
            } else {
                currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize());
            }

            if (currentPhotoObject != null) {
                float scale = (float) currentPhotoObject.w / (float) photoWidth;
                int h = (int) (currentPhotoObject.h / scale);
                if (h == 0) {
                    h = dp(100);
                }
                if (h > photoHeight) {
                    h = photoHeight;
                } else if (h < dp(120)) {
                    h = dp(120);
                }
                if (!fast && needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        h = (int) (AndroidUtilities.getMinTabletSide() * 0.5f);
                    } else {
                        h = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f);
                    }
                }
                photoHeight = h;
            }

            return photoHeight + dp(14);
        }
    }

    public static String getEmoji(TLRPC.Document document) {
        if (document == null) return "😀";
        final TLRPC.TL_documentAttributeCustomEmoji attrEmoji = find(document.attributes, TLRPC.TL_documentAttributeCustomEmoji.class);
        if (attrEmoji != null && !TextUtils.isEmpty(attrEmoji.alt))
            return attrEmoji.alt;
        final TLRPC.TL_documentAttributeSticker attrSticker = find(document.attributes, TLRPC.TL_documentAttributeSticker.class);
        if (attrSticker != null && !TextUtils.isEmpty(attrSticker.alt))
            return attrSticker.alt;
        return "😀";
    }

    public String getStickerEmoji() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker ||
                    attribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                return attribute.alt != null && attribute.alt.length() > 0 ? attribute.alt : null;
            }
        }
        return null;
    }

    public boolean isConferenceCall() {
        return messageOwner.action instanceof TLRPC.TL_messageActionConferenceCall;
    }

    public boolean isVideoCall() {
        return (
            messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall && messageOwner.action.video ||
            messageOwner.action instanceof TLRPC.TL_messageActionConferenceCall && messageOwner.action.video
        );
    }

    public boolean isAnimatedEmoji() {
        return emojiAnimatedSticker != null || emojiAnimatedStickerId != null;
    }

    public boolean isAnimatedAnimatedEmoji() {
        return isAnimatedEmoji() && isAnimatedEmoji(getDocument());
    }

    public boolean isDice() {
        return getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDice;
    }

    public boolean isStakeableDice() {
        final TLRPC.TL_messageMediaDice mediaDice = getMedia(messageOwner, TLRPC.TL_messageMediaDice.class);
        return mediaDice != null && TextUtils.equals("\uD83C\uDFB2", mediaDice.emoticon);
    }

    public boolean isStakedDice() {
        final TLRPC.MessageMedia media = getMedia(messageOwner);
        if (media instanceof TLRPC.TL_messageMediaDice)
            return ((TLRPC.TL_messageMediaDice) media).game_outcome != null;
        return false;
    }

    public static long getStakedDiceWinAmount(TLRPC.TL_messageMediaDice media) {
        if (media.game_outcome != null) {
            if (media.game_outcome.ton_amount > 0) {
                return media.game_outcome.ton_amount;
            } else {
                return -media.game_outcome.stake_ton_amount;
            }
        }
        return 0;
    }

    public long getStakedDiceWinAmount() {
        final TLRPC.MessageMedia media = getMedia(messageOwner);
        if (media instanceof TLRPC.TL_messageMediaDice && ((TLRPC.TL_messageMediaDice) media).game_outcome != null) {
            final TLRPC.TL_messages_emojiGameOutcome outcome = ((TLRPC.TL_messageMediaDice) media).game_outcome;
            if (outcome.ton_amount > 0) {
                return outcome.ton_amount;
            } else {
                return -outcome.stake_ton_amount;
            }
        }
        return 0;
    }

    public long getStakedDiceAmount() {
        final TLRPC.MessageMedia media = getMedia(messageOwner);
        if (media instanceof TLRPC.TL_messageMediaDice && ((TLRPC.TL_messageMediaDice) media).game_outcome != null) {
            return ((TLRPC.TL_messageMediaDice) media).game_outcome.stake_ton_amount;
        }
        return 0;
    }

    public String getDiceEmoji() {
        if (!isDice()) {
            return null;
        }
        TLRPC.TL_messageMediaDice messageMediaDice = (TLRPC.TL_messageMediaDice) getMedia(messageOwner);
        if (TextUtils.isEmpty(messageMediaDice.emoticon)) {
            return "\uD83C\uDFB2"; // 🎲
        }
        return messageMediaDice.emoticon.replace("\ufe0f", "");
    }

    public String getDiceEmoji(TLRPC.TL_messageMediaDice dice) {
        if (dice == null) return null;
        if (TextUtils.isEmpty(dice.emoticon)) {
            return "\uD83C\uDFB2"; // 🎲
        }
        return dice.emoticon.replace("\ufe0f", "");
    }

    public int getDiceValue() {
        if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDice) {
            return ((TLRPC.TL_messageMediaDice) getMedia(messageOwner)).value;
        }
        return -1;
    }

    public boolean isSticker() {
        if (type != 1000) {
            return type == TYPE_STICKER;
        }
        return isStickerDocument(getDocument()) || isVideoSticker(getDocument());
    }

    public boolean isAnimatedSticker() {
        if (type != 1000) {
            return type == TYPE_ANIMATED_STICKER;
        }
        boolean isSecretChat = DialogObject.isEncryptedDialog(getDialogId());
        if (isSecretChat && messageOwner.stickerVerified != 1) {
            return false;
        }
        if (emojiAnimatedStickerId != null && emojiAnimatedSticker == null) {
            return true;
        }
        return isAnimatedStickerDocument(getDocument(), emojiAnimatedSticker != null || !isSecretChat || isOut());
    }

    public boolean isAnyKindOfSticker() {
        return type == TYPE_STICKER || type == TYPE_ANIMATED_STICKER || type == TYPE_EMOJIS;
    }

    public boolean shouldDrawWithoutBackground() {
        return !isSponsored() && (type == TYPE_STICKER || type == TYPE_ANIMATED_STICKER || type == TYPE_ROUND_VIDEO || type == TYPE_EMOJIS || isExpiredStory());
    }

    public boolean isAnimatedEmojiStickers() {
        return type == TYPE_EMOJIS;
    }

    public boolean isAnimatedEmojiStickerSingle() {
        return emojiAnimatedStickerId != null;
    }

    public boolean isLocation() {
        return isLocationMessage(messageOwner);
    }

    public boolean isMask() {
        return isMaskMessage(messageOwner);
    }

    public boolean isMusic() {
        return isMusicMessage(messageOwner) && !isVideo() && !isRoundVideo();
    }

    public boolean isDocument() {
        return getDocument() != null && !isVideo() && !isMusic() && !isVoice() && !isAnyKindOfSticker();
    }

    public boolean isVoice() {
        return isVoiceMessage(messageOwner);
    }

    public boolean isVoiceOnce() {
        return isVoice() && messageOwner != null && messageOwner.media != null && messageOwner.media.ttl_seconds == 0x7FFFFFFF;
    }

    public boolean isRoundOnce() {
        return isRoundVideo() && messageOwner != null && messageOwner.media != null && messageOwner.media.ttl_seconds == 0x7FFFFFFF;
    }

    public boolean isVideo() {
        return isVideoMessage(messageOwner);
    }

    public boolean isLivePhoto() {
        final TLRPC.MessageMedia media = getMedia(this);
        return media != null && media.live_photo;
    }

    public boolean isVideoStory() {
        TLRPC.MessageMedia media = MessageObject.getMedia(messageOwner);
        if (media == null) {
            return false;
        }
        TL_stories.StoryItem storyItem = media.storyItem;
        if (storyItem == null || storyItem.media == null) {
            return false;
        }
        return MessageObject.isVideoDocument(storyItem.media.document);
    }

    public boolean isPhoto() {
        return isPhoto(messageOwner);
    }

    public boolean isStoryMedia() {
        return messageOwner != null && messageOwner.media instanceof TLRPC.TL_messageMediaStory;
    }

    public boolean isLiveLocation() {
        return isLiveLocationMessage(messageOwner);
    }

    public boolean isExpiredLiveLocation(int date) {
        return messageOwner.date + getMedia(messageOwner).period <= date;
    }

    public boolean isGame() {
        return isGameMessage(messageOwner);
    }

    public boolean isInvoice() {
        return isInvoiceMessage(messageOwner);
    }

    public boolean isRoundVideo() {
        if (isRoundVideoCached == 0) {
            isRoundVideoCached = type == TYPE_ROUND_VIDEO || isRoundVideoMessage(messageOwner) ? 1 : 2;
        }
        return isRoundVideoCached == 1;
    }

    public boolean shouldAnimateSending() {
        return wasJustSent && (type == MessageObject.TYPE_ROUND_VIDEO || isVoice() || (isAnyKindOfSticker() && sendAnimationData != null) || (messageText != null && sendAnimationData != null));
    }

    public boolean hasAttachedStickers() {
        if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
            return getMedia(messageOwner).photo != null && getMedia(messageOwner).photo.has_stickers;
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
            return isDocumentHasAttachedStickers(getMedia(messageOwner).document);
        }
        return false;
    }

    public static boolean isDocumentHasAttachedStickers(TLRPC.Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeHasStickers) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGif() {
        return isGifMessage(messageOwner);
    }

    public boolean isWebpageDocument() {
        return getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage.document != null && !isGifDocument(getMedia(messageOwner).webpage.document);
    }

    public boolean isWebpage() {
        return getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        return getMedia(messageOwner) != null && isNewGifDocument(getDocument());
    }

    public boolean isAndroidTheme() {
        if (getMedia(messageOwner) != null && getMedia(messageOwner).webpage != null && !getMedia(messageOwner).webpage.attributes.isEmpty()) {
            for (int b = 0, N2 = getMedia(messageOwner).webpage.attributes.size(); b < N2; b++) {
                TLRPC.WebPageAttribute attribute_ = getMedia(messageOwner).webpage.attributes.get(b);
                if (!(attribute_ instanceof TLRPC.TL_webPageAttributeTheme)) {
                    continue;
                }
                TLRPC.TL_webPageAttributeTheme attribute = (TLRPC.TL_webPageAttributeTheme) attribute_;
                ArrayList<TLRPC.Document> documents = attribute.documents;
                for (int a = 0, N = documents.size(); a < N; a++) {
                    TLRPC.Document document = documents.get(a);
                    if ("application/x-tgtheme-android".equals(document.mime_type)) {
                        return true;
                    }
                }
                if (attribute.settings != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean unknown) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (attribute.voice) {
                        if (!unknown) {
                            return null;
                        }
                        return LocaleController.formatDateAudio(messageOwner.date, true);
                    }
                    String title = attribute.title;
                    if (title == null || title.length() == 0) {
                        title = FileLoader.getDocumentFileName(document);
                        if (TextUtils.isEmpty(title) && unknown) {
                            title = getString(R.string.AudioUnknownTitle);
                        }
                    }
                    return title;
                } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                    if (attribute.round_message) {
                        if (isQuickReply()) {
                            return formatString(R.string.BusinessInReplies, "/" + getQuickReplyDisplayName());
                        }
                        return LocaleController.formatDateAudio(messageOwner.date, true);
                    }
                }
            }
            String fileName = FileLoader.getDocumentFileName(document);
            if (!TextUtils.isEmpty(fileName)) {
                return fileName;
            }
        }
        return getString(R.string.AudioUnknownTitle);
    }

    public static String getMusicTitle(TLRPC.Document document, boolean unknown) {
        if (document == null) {
            return getString(R.string.AudioUnknownTitle);
        }

        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                String title = attribute.title;
                if (title == null || title.isEmpty()) {
                    title = FileLoader.getDocumentFileName(document);
                    if (TextUtils.isEmpty(title) && unknown) {
                        title = getString(R.string.AudioUnknownTitle);
                    }
                }
                return title;
            }
        }
        String fileName = FileLoader.getDocumentFileName(document);
        if (!TextUtils.isEmpty(fileName)) {
            return fileName;
        }

        return getString(R.string.AudioUnknownTitle);
    }

    public double getDuration() {
        if (attributeDuration > 0) {
            return attributeDuration;
        }
        TLRPC.Document document = getDocument();
        if (document == null && type == TYPE_STORY) {
            TL_stories.StoryItem storyItem = getMedia(messageOwner).storyItem;
            if (storyItem != null && storyItem.media != null) {
                document = storyItem.media.document;
            }
        }
        if (document == null) {
            return 0;
        }
        if (audioPlayerDuration > 0) {
            return audioPlayerDuration;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attributeDuration = attribute.duration;
            } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attributeDuration = attribute.duration;
            }
        }
        return audioPlayerDuration;
    }

    public String getArtworkUrl(boolean small) {
        return getArtworkUrl(getDocument(), small);
    }

    public static String getArtworkUrl(TLRPC.Document document, boolean small) {
        if (document == null) {
            return null;
        }

        if ("audio/ogg".equals(document.mime_type)) {
            return null;
        }
        for (int i = 0, N = document.attributes.size(); i < N; i++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(i);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                if (attribute.voice) {
                    return null;
                } else {
                    String performer = attribute.performer;
                    String title = attribute.title;
                    if (!TextUtils.isEmpty(performer)) {
                        for (int a = 0; a < excludeWords.length; a++) {
                            performer = performer.replace(excludeWords[a], " ");
                        }
                    }
                    if (TextUtils.isEmpty(performer) && TextUtils.isEmpty(title)) {
                        return null;
                    }
                    try {
                        return "athumb://itunes.apple.com/search?term=" + URLEncoder.encode(performer + " - " + title, "UTF-8") + "&entity=song&limit=4" + (small ? "&s=1" : "");
                    } catch (Exception ignore) {

                    }
                }
            }
        }
        return null;
    }

    public String getMusicAuthor() {
        return getMusicAuthor(true);
    }

    public String getMusicAuthor(boolean unknown) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            boolean isVoice = false;
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (attribute.voice) {
                        isVoice = true;
                    } else {
                        String performer = attribute.performer;
                        if (TextUtils.isEmpty(performer) && unknown) {
                            performer = getString(R.string.AudioUnknownArtist);
                        }
                        return performer;
                    }
                } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                    if (attribute.round_message) {
                        isVoice = true;
                    }
                }
                if (isVoice) {
                    if (!unknown) {
                        return null;
                    }
                    if (isOutOwner() || messageOwner.fwd_from != null && messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser && messageOwner.fwd_from.from_id.user_id == UserConfig.getInstance(currentAccount).getClientUserId()) {
                        return getString(R.string.FromYou);
                    }
                    TLRPC.User user = null;
                    TLRPC.Chat chat = null;
                    if (messageOwner.fwd_from != null && messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
                        chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.fwd_from.from_id.channel_id);
                    } else if (messageOwner.fwd_from != null && messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat) {
                        chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.fwd_from.from_id.chat_id);
                    } else if (messageOwner.fwd_from != null && messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                        user = MessagesController.getInstance(currentAccount).getUser(messageOwner.fwd_from.from_id.user_id);
                    } else if (messageOwner.fwd_from != null && messageOwner.fwd_from.from_name != null) {
                        return messageOwner.fwd_from.from_name;
                    } else if (messageOwner.from_id instanceof TLRPC.TL_peerChat) {
                        chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.from_id.chat_id);
                    } else if (messageOwner.from_id instanceof TLRPC.TL_peerChannel) {
                        chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.from_id.channel_id);
                    } else if (messageOwner.from_id == null && messageOwner.peer_id.channel_id != 0) {
                        chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.peer_id.channel_id);
                    } else {
                        user = MessagesController.getInstance(currentAccount).getUser(messageOwner.from_id.user_id);
                    }
                    if (user != null) {
                        return UserObject.getUserName(user);
                    } else if (chat != null) {
                        return chat.title;
                    }
                }
            }
        }
        return getString(R.string.AudioUnknownArtist);
    }

    public static String getMusicAuthor(TLRPC.Document document, boolean unknown) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (!attribute.voice) {
                        String performer = attribute.performer;
                        if (TextUtils.isEmpty(performer) && unknown) {
                            performer = getString(R.string.AudioUnknownArtist);
                        }
                        return performer;
                    }
                }
            }
        }
        return getString(R.string.AudioUnknownArtist);
    }

    public TLRPC.InputStickerSet getInputStickerSet() {
        return getInputStickerSet(messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(messageOwner);
    }

    public boolean needDrawForwarded() {
        if (type == MessageObject.TYPE_STORY && !isExpiredStory()) {
            return true;
        }
        if (getDialogId() == UserObject.VERIFY) {
            return false;
        }
        if (isSaved) {
            if (messageOwner == null || messageOwner.fwd_from == null) return false;
            final long selfId = UserConfig.getInstance(currentAccount).getClientUserId();
            final long savedId = getSavedDialogId(selfId, messageOwner);
            long fromId = DialogObject.getPeerDialogId(messageOwner.fwd_from.saved_from_peer);
            if (fromId >= 0) {
                fromId = DialogObject.getPeerDialogId(messageOwner.fwd_from.from_id);
            }
            if (fromId == 0) return savedId >= 0 && savedId != UserObject.ANONYMOUS;
            return savedId != fromId && fromId != selfId;
        }
        return ((messageOwner.flags & TLRPC.MESSAGE_FLAG_FWD) != 0 || getMedia(this) instanceof TLRPC.TL_messageMediaPaidMedia) && messageOwner.fwd_from != null && !messageOwner.fwd_from.imported && (messageOwner.fwd_from.saved_from_peer == null || !(messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) || messageOwner.fwd_from.saved_from_peer.channel_id != messageOwner.fwd_from.from_id.channel_id) && UserConfig.getInstance(currentAccount).getClientUserId() != getDialogId();
    }

    public static boolean isForwardedMessage(TLRPC.Message message) {
        return (message.flags & TLRPC.MESSAGE_FLAG_FWD) != 0 && message.fwd_from != null;
    }

    public boolean isReply() {
        return !(replyMessageObject != null && replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty) && messageOwner.reply_to != null && (messageOwner.reply_to.reply_to_msg_id != 0 || messageOwner.reply_to.reply_to_random_id != 0) && (messageOwner.flags & TLRPC.MESSAGE_FLAG_REPLY) != 0;
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(messageOwner);
    }

    public boolean isMediaEmpty(boolean webpageIsEmpty) {
        return isMediaEmpty(messageOwner, webpageIsEmpty);
    }

    public boolean isMediaEmptyWebpage() {
        return isMediaEmptyWebpage(messageOwner);
    }

    public static boolean isMediaEmpty(TLRPC.Message message) {
        return isMediaEmpty(message, true);
    }

    public static boolean isMediaEmpty(TLRPC.Message message, boolean allowWebpageIsEmpty) {
        return message == null || getMedia(message) == null || getMedia(message) instanceof TLRPC.TL_messageMediaEmpty || allowWebpageIsEmpty && getMedia(message) instanceof TLRPC.TL_messageMediaWebPage;
    }

    public static boolean isMediaEmptyWebpage(TLRPC.Message message) {
        return message == null || getMedia(message) == null || getMedia(message) instanceof TLRPC.TL_messageMediaEmpty;
    }

    public boolean hasReplies() {
        return messageOwner.replies != null && messageOwner.replies.replies > 0;
    }

    public boolean canViewThread() {
        if (messageOwner.action != null) {
            return false;
        }
        return hasReplies() || replyMessageObject != null && replyMessageObject.messageOwner.replies != null || getReplyTopMsgId() != 0;
    }

    public boolean isComments() {
        return messageOwner.replies != null && messageOwner.replies.comments;
    }

    public boolean isLinkedToChat(long chatId) {
        return messageOwner.replies != null && (chatId == 0 || messageOwner.replies.channel_id == chatId);
    }

    public int getRepliesCount() {
        return messageOwner.replies != null ? messageOwner.replies.replies : 0;
    }

    public boolean canEditMessage(TLRPC.Chat chat) {
        return !isEphemeral() && canEditMessage(currentAccount, messageOwner, chat, scheduled);
    }

    public boolean canEditMessageScheduleTime(TLRPC.Chat chat) {
        return canEditMessageScheduleTime(currentAccount, messageOwner, chat);
    }

    public boolean canForwardMessage() {
        if (isQuickReply() || isEphemeral()) return false;
        if (type == TYPE_GIFT_STARS || type == TYPE_GIFT_THEME_UPDATE || type == TYPE_SUGGEST_BIRTHDAY || type == TYPE_GIFT_OFFER || type == TYPE_SHARING_OFFER || type == TYPE_COMMUNITY_CHANGED) return false;
        return !(messageOwner instanceof TLRPC.TL_message_secret) && !needDrawBluredPreview() && !isLiveLocation() && type != MessageObject.TYPE_PHONE_CALL && !isSponsored() && !messageOwner.noforwards;
    }

    public boolean canEditMedia() {
        if (isSecretMedia() || isEphemeral()) {
            return false;
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
            return true;
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
            return !isVoice() && !isSticker() && !isAnimatedSticker() && !isRoundVideo();
        } else if (isMediaEmpty()) {
            return true;
        }
        return false;
    }

    public boolean canEditMessageAnytime(TLRPC.Chat chat) {
        return canEditMessageAnytime(currentAccount, messageOwner, chat);
    }

    public static boolean canEditMessageAnytime(int currentAccount, TLRPC.Message message, TLRPC.Chat chat) {
        if (message == null || message.peer_id == null || getMedia(message) != null && (isRoundVideoDocument(getMedia(message).document) || isStickerDocument(getMedia(message).document) || isAnimatedStickerDocument(getMedia(message).document, true)) || message.action != null && !(message.action instanceof TLRPC.TL_messageActionEmpty) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0) {
            return false;
        }
        if (message.from_id instanceof TLRPC.TL_peerUser && message.from_id.user_id == message.peer_id.user_id && message.from_id.user_id == UserConfig.getInstance(currentAccount).getClientUserId() && !isLiveLocationMessage(message)) {
            return true;
        }
        if (chat == null && message.peer_id.channel_id != 0) {
            chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(message.peer_id.channel_id);
            if (chat == null) {
                return false;
            }
        }
        if (ChatObject.isChannel(chat) && !chat.megagroup && (chat.creator || chat.admin_rights != null && chat.admin_rights.edit_messages)) {
            return true;
        }
        if (message.out && chat != null && chat.megagroup && (chat.creator || chat.admin_rights != null && chat.admin_rights.pin_messages || chat.default_banned_rights != null && !chat.default_banned_rights.pin_messages)) {
            return true;
        }
        //
        return false;
    }

    public static boolean canEditMessageScheduleTime(int currentAccount, TLRPC.Message message, TLRPC.Chat chat) {
        if (message.video_processing_pending) return false;
        if (chat == null && message.peer_id.channel_id != 0) {
            chat = MessagesController.getInstance(currentAccount).getChat(message.peer_id.channel_id);
            if (chat == null) {
                return false;
            }
        }
        if (!ChatObject.isChannel(chat) || chat.megagroup || chat.creator) {
            return true;
        }
        if (chat.admin_rights != null && (chat.admin_rights.edit_messages || message.out)) {
            return true;
        }
        return false;
    }

    public static boolean canEditMessage(int currentAccount, TLRPC.Message message, TLRPC.Chat chat, boolean scheduled) {
        if (scheduled && message.date < ConnectionsManager.getInstance(currentAccount).getCurrentTime() - 60) {
            return false;
        }
        if (chat != null && (chat.left || chat.kicked) && (!chat.megagroup || !chat.has_link)) {
            return false;
        }
        if (message == null || message.peer_id == null || message.action != null && !(message.action instanceof TLRPC.TL_messageActionEmpty) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0) {
            return false;
        }
        final TLRPC.MessageMedia media = getMedia(message);
        if (media != null && message.rich_message == null && (isRoundVideoDocument(media.document) || isStickerDocument(media.document) || isAnimatedStickerDocument(media.document, true) || isLocationMessage(message))) {
            return false;
        }
        if (message.paid_suggested_post_stars || message.paid_suggested_post_ton) {
            return false;
        }
        if (message.from_id instanceof TLRPC.TL_peerUser && message.from_id.user_id == message.peer_id.user_id && message.from_id.user_id == UserConfig.getInstance(currentAccount).getClientUserId() && !isLiveLocationMessage(message) && !(media instanceof TLRPC.TL_messageMediaContact)) {
            return true;
        }
        if (chat == null && message.peer_id.channel_id != 0) {
            chat = MessagesController.getInstance(currentAccount).getChat(message.peer_id.channel_id);
            if (chat == null) {
                return false;
            }
        }
        if (
            media != null &&
            message.rich_message == null &&
            !(media instanceof TLRPC.TL_messageMediaEmpty) &&
            !(media instanceof TLRPC.TL_messageMediaPhoto) &&
            !(media instanceof TLRPC.TL_messageMediaDocument) &&
            !(media instanceof TLRPC.TL_messageMediaWebPage) &&
            !(media instanceof TLRPC.TL_messageMediaPaidMedia) &&
            !(media instanceof TLRPC.TL_messageMediaToDo)
        ) {
            return false;
        }
        if (ChatObject.isChannel(chat) && !chat.megagroup && (chat.creator || chat.admin_rights != null && chat.admin_rights.edit_messages)) {
            return true;
        }
        if (message.out && chat != null && chat.megagroup && (chat.creator || chat.admin_rights != null && chat.admin_rights.pin_messages || chat.default_banned_rights != null && !chat.default_banned_rights.pin_messages)) {
            return true;
        }
        if (!scheduled && Math.abs(message.date - ConnectionsManager.getInstance(currentAccount).getCurrentTime()) > MessagesController.getInstance(currentAccount).maxEditTime) {
            return false;
        }
        if (message.peer_id.channel_id == 0) {
            return (message.out || message.from_id instanceof TLRPC.TL_peerUser && message.from_id.user_id == UserConfig.getInstance(currentAccount).getClientUserId()) && (
                    message.rich_message != null ||
                    media instanceof TLRPC.TL_messageMediaPhoto ||
                    media instanceof TLRPC.TL_messageMediaDocument && !isStickerMessage(message) && !isAnimatedStickerMessage(message) ||
                    media instanceof TLRPC.TL_messageMediaEmpty ||
                    media instanceof TLRPC.TL_messageMediaWebPage ||
                    media instanceof TLRPC.TL_messageMediaPaidMedia ||
                    media instanceof TLRPC.TL_messageMediaToDo ||
                    media == null);
        }
        if (chat != null && chat.megagroup && message.out || chat != null && !chat.megagroup && (chat.creator || chat.admin_rights != null && (chat.admin_rights.edit_messages || message.out && chat.admin_rights.post_messages)) && message.post) {
            if (message.rich_message != null ||
                media instanceof TLRPC.TL_messageMediaPhoto ||
                media instanceof TLRPC.TL_messageMediaDocument && !isStickerMessage(message) && !isAnimatedStickerMessage(message) ||
                media instanceof TLRPC.TL_messageMediaEmpty ||
                media instanceof TLRPC.TL_messageMediaWebPage ||
                media instanceof TLRPC.TL_messageMediaPaidMedia ||
                media instanceof TLRPC.TL_messageMediaToDo ||
                media == null
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean canDeleteMessage(boolean inScheduleMode, TLRPC.Chat chat) {
        return (
            isStory() && messageOwner != null && messageOwner.dialog_id == UserConfig.getInstance(currentAccount).getClientUserId() ||
            eventId == 0 && sponsoredId == null && canDeleteMessage(currentAccount, inScheduleMode, messageOwner, chat) || isEphemeral()
        );
    }

    public static boolean canDeleteMessage(int currentAccount, boolean inScheduleMode, TLRPC.Message message, TLRPC.Chat chat) {
        if (message == null) {
            return false;
        }
        if (ChatObject.isChannelAndNotMegaGroup(chat) && message.action instanceof TLRPC.TL_messageActionChatJoinedByRequest) {
            return false;
        }
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.peer_id != null && message.peer_id.channel_id != 0) {
            chat = MessagesController.getInstance(currentAccount).getChat(message.peer_id.channel_id);
        }
        if (ChatObject.isChannel(chat)) {
            if (inScheduleMode && !chat.megagroup) {
                return chat.creator || chat.admin_rights != null && (chat.admin_rights.delete_messages || message.out);
            }
            if (message.out && message instanceof TLRPC.TL_messageService) {
                return message.id != 1 && ChatObject.canUserDoAdminAction(chat, ChatObject.ACTION_DELETE_MESSAGES);
            }
            return inScheduleMode || message.id != 1 && (chat.creator || chat.admin_rights != null && (chat.admin_rights.delete_messages || message.out && (chat.megagroup || chat.admin_rights.post_messages)) || chat.megagroup && message.out);
        }
        return inScheduleMode || isOut(message) || !ChatObject.isChannel(chat);
    }

    public String getForwardedName() {
        if (messageOwner.fwd_from != null) {
            if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
                TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.fwd_from.from_id.channel_id);
                if (chat != null) {
                    return chat.title;
                }
            } else if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat) {
                TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(messageOwner.fwd_from.from_id.chat_id);
                if (chat != null) {
                    return chat.title;
                }
            } else if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(messageOwner.fwd_from.from_id.user_id);
                if (user != null) {
                    return UserObject.getUserName(user);
                }
            } else if (messageOwner.fwd_from.from_name != null) {
                return messageOwner.fwd_from.from_name;
            }
        }
        return null;
    }

    public Long getForwardedFromId() {
        if (messageOwner == null) return null;
        if (messageOwner.fwd_from == null) return null;
        if (messageOwner.fwd_from.from_id == null) return null;
        return DialogObject.getPeerDialogId(messageOwner.fwd_from.from_id);
    }

    public TLObject getForwardedFromPeerObject() {
        final Long id = getForwardedFromId();
        if (id == null) return null;
        return MessagesController.getInstance(currentAccount).getUserOrChat(id);
    }

    public int getReplyMsgId() {
        return messageOwner.reply_to != null ? messageOwner.reply_to.reply_to_msg_id : 0;
    }

    public int getReplyTopMsgId() {
        return messageOwner.reply_to != null ? messageOwner.reply_to.reply_to_top_id : 0;
    }

    public int getReplyTopMsgId(boolean sureIsForum) {
        return messageOwner.reply_to != null ? (sureIsForum && (messageOwner.reply_to.flags & 2) > 0 && messageOwner.reply_to.reply_to_top_id == 0 ? 1 : messageOwner.reply_to.reply_to_top_id) : 0;
    }

    public static long getReplyToDialogId(TLRPC.Message message) {
        if (message.reply_to == null) {
            return 0;
        }
        if (message.reply_to.reply_to_peer_id != null) {
            return getPeerId(message.reply_to.reply_to_peer_id);
        }
        return MessageObject.getDialogId(message);
    }

    public int getReplyAnyMsgId() {
        if (messageOwner.reply_to != null) {
            if (messageOwner.reply_to.reply_to_top_id != 0) {
                return messageOwner.reply_to.reply_to_top_id;
            } else {
                return messageOwner.reply_to.reply_to_msg_id;
            }
        }
        return 0;
    }

    public boolean isPrivateForward() {
        return messageOwner.fwd_from != null && !TextUtils.isEmpty(messageOwner.fwd_from.from_name);
    }

    public boolean isImportedForward() {
        return messageOwner.fwd_from != null && messageOwner.fwd_from.imported;
    }

    public long getSenderId() {
        if (messageOwner.fwd_from != null && messageOwner.fwd_from.saved_from_peer != null) {
            if (messageOwner.fwd_from.saved_from_peer.user_id != 0) {
                if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                    return messageOwner.fwd_from.from_id.user_id;
                } else {
                    return messageOwner.fwd_from.saved_from_peer.user_id;
                }
            } else if (messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
                if (isSavedFromMegagroup() && messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                    return messageOwner.fwd_from.from_id.user_id;
                } else if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
                    return -messageOwner.fwd_from.from_id.channel_id;
                } else if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat) {
                    return -messageOwner.fwd_from.from_id.chat_id;
                } else {
                    return -messageOwner.fwd_from.saved_from_peer.channel_id;
                }
            } else if (messageOwner.fwd_from.saved_from_peer.chat_id != 0) {
                if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                    return messageOwner.fwd_from.from_id.user_id;
                } else if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
                    return -messageOwner.fwd_from.from_id.channel_id;
                } else if (messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat) {
                    return -messageOwner.fwd_from.from_id.chat_id;
                } else {
                    return -messageOwner.fwd_from.saved_from_peer.chat_id;
                }
            }
        } else if (messageOwner.from_id instanceof TLRPC.TL_peerUser) {
            return messageOwner.from_id.user_id;
        } else if (messageOwner.from_id instanceof TLRPC.TL_peerChannel) {
            return -messageOwner.from_id.channel_id;
        } else if (messageOwner.from_id instanceof TLRPC.TL_peerChat) {
            return -messageOwner.from_id.chat_id;
        } else if (messageOwner.post) {
            return messageOwner.peer_id.channel_id;
        }
        return 0;
    }

    public boolean isWallpaper() {
        return getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage != null && "telegram_background".equals(getMedia(messageOwner).webpage.type);
    }

    public boolean isTheme() {
        return getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage != null && "telegram_theme".equals(getMedia(messageOwner).webpage.type);
    }

    public int getMediaExistanceFlags() {
        int flags = 0;
        if (attachPathExists) {
            flags |= 1;
        }
        if (mediaExists) {
            flags |= 2;
        }
        return flags;
    }

    public void applyMediaExistanceFlags(int flags) {
        if (flags == -1) {
            checkMediaExistance();
        } else {
            attachPathExists = (flags & 1) != 0;
            mediaExists = (flags & 2) != 0;
        }
    }

    public void checkMediaExistance() {
        checkMediaExistance(true);
    }

    public void checkMediaExistance(boolean useFileDatabaseQueue) {
        File cacheFile = null;
        attachPathExists = false;
        mediaExists = false;
        if (type == TYPE_EXTENDED_MEDIA_PREVIEW) {
            TLRPC.TL_messageExtendedMediaPreview preview = (TLRPC.TL_messageExtendedMediaPreview) messageOwner.media.extended_media.get(0);
            if (preview.thumb != null) {
                File file = FileLoader.getInstance(currentAccount).getPathToAttach(preview.thumb, useFileDatabaseQueue);
                if (!mediaExists) {
                    mediaExists = file.exists() || preview.thumb instanceof TLRPC.TL_photoStrippedSize;
                }
            }
        } else if (type == TYPE_PHOTO) {
            TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(true));
            if (currentPhotoObject != null) {
                File file = FileLoader.getInstance(currentAccount).getPathToMessage(messageOwner, useFileDatabaseQueue);
                if (needDrawBluredPreview()) {
                    mediaExists = new File(file.getAbsolutePath() + ".enc").exists();
                }
                if (!mediaExists) {
                    mediaExists = file.exists();
                }
            }
        }
        if (!mediaExists && type == TYPE_GIF || type == TYPE_VIDEO || type == TYPE_FILE || type == TYPE_VOICE || type == TYPE_MUSIC || type == TYPE_ROUND_VIDEO) {
            if (messageOwner.attachPath != null && messageOwner.attachPath.length() > 0) {
                File f = new File(messageOwner.attachPath);
                attachPathExists = f.exists();
            }
            if (!attachPathExists) {
                File file = FileLoader.getInstance(currentAccount).getPathToMessage(messageOwner, useFileDatabaseQueue);
                if (type == TYPE_VIDEO && needDrawBluredPreview() || isVoiceOnce() || isRoundOnce()) {
                    mediaExists = new File(file.getAbsolutePath() + ".enc").exists();
                }
                if (!mediaExists) {
                    mediaExists = file.exists();
                }
            }
        }
        if (!mediaExists) {
            TLRPC.Document document = getDocument();
            if (document != null) {
                if (isWallpaper()) {
                    mediaExists = FileLoader.getInstance(currentAccount).getPathToAttach(document, null, true, useFileDatabaseQueue).exists();
                } else {
                    mediaExists = FileLoader.getInstance(currentAccount).getPathToAttach(document, null, false, useFileDatabaseQueue).exists();
                }
            } else if (type == MessageObject.TYPE_TEXT) {
                TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize());
                if (currentPhotoObject == null) {
                    return;
                }
                mediaExists = FileLoader.getInstance(currentAccount).getPathToAttach(currentPhotoObject, null, true, useFileDatabaseQueue).exists();
            } else if (type == MessageObject.TYPE_ACTION_PHOTO) {
                TLRPC.Photo photo = messageOwner.action.photo;
                if (photo == null || photo.video_sizes.isEmpty()) {
                    return;
                }
                mediaExists = FileLoader.getInstance(currentAccount).getPathToAttach(photo.video_sizes.get(0), null, true, useFileDatabaseQueue).exists();
            }
        }
        updateQualitiesCached(useFileDatabaseQueue);
    }

    public void setQuery(String query) {
        setQuery(query, true);
    }
    public void setQuery(String query, boolean cut) {
        if (TextUtils.isEmpty(query)) {
            highlightedWords = null;
            messageTrimmedToHighlight = null;
            messageTrimmedToHighlightCut = true;
            return;
        }
        ArrayList<String> foundWords = new ArrayList<>();
        query = query.trim().toLowerCase();
        String[] queryWord = query.split("[^\\p{L}#$]+");

        ArrayList<String> searchForWords = new ArrayList<>();
        if (messageOwner.reply_to != null && !TextUtils.isEmpty(messageOwner.reply_to.quote_text)) {
            String message = messageOwner.reply_to.quote_text.trim().toLowerCase();
            if (message.contains(query) && !foundWords.contains(query)) {
                foundWords.add(query);
                handleFoundWords(foundWords, queryWord, true, cut);
                return;
            }
            String[] words = message.split("[^\\p{L}#$]+");
            searchForWords.addAll(Arrays.asList(words));
        }
        if (!TextUtils.isEmpty(messageOwner.message)) {
            String message = messageOwner.message.trim().toLowerCase();
            if (message.contains(query) && !foundWords.contains(query)) {
                foundWords.add(query);
                handleFoundWords(foundWords, queryWord, false, cut);
                return;
            }
            String[] words = message.split("[^\\p{L}#$]+");
            searchForWords.addAll(Arrays.asList(words));
        }
        if (getDocument() != null) {
            String fileName = FileLoader.getDocumentFileName(getDocument()).toLowerCase();
            if (fileName.contains(query) && !foundWords.contains(query)) {
                foundWords.add(query);
            }
            String[] words = fileName.split("[^\\p{L}#$]+");
            searchForWords.addAll(Arrays.asList(words));
        }

        if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && getMedia(messageOwner).webpage instanceof TLRPC.TL_webPage) {
            TLRPC.WebPage webPage = getMedia(messageOwner).webpage;
            String title = webPage.title;
            if (title == null) {
                title = webPage.site_name;
            }
            if (title != null) {
                title = title.toLowerCase();
                if (title.contains(query) && !foundWords.contains(query)) {
                    foundWords.add(query);
                }
                String[] words = title.split("[^\\p{L}#$]+");
                searchForWords.addAll(Arrays.asList(words));
            }
        }

        String musicAuthor = getMusicAuthor();
        if (musicAuthor != null) {
            musicAuthor = musicAuthor.toLowerCase();
            if (musicAuthor.contains(query) && !foundWords.contains(query)) {
                foundWords.add(query);
            }
            String[] words = musicAuthor.split("[^\\p{L}#$]+");
            searchForWords.addAll(Arrays.asList(words));
        }
        for (int k = 0; k < queryWord.length; k++) {
            String currentQuery = queryWord[k];
            if (currentQuery.length() < 2) {
                continue;
            }
            for (int i = 0; i < searchForWords.size(); i++) {
                if (foundWords.contains(searchForWords.get(i))) {
                    continue;
                }
                String word = searchForWords.get(i);
                int startIndex = word.indexOf(currentQuery.charAt(0));
                if (startIndex < 0) {
                    continue;
                }
                int l = Math.max(currentQuery.length(), word.length());
                if (startIndex != 0) {
                    word = word.substring(startIndex);
                }
                int min = Math.min(currentQuery.length(), word.length());
                int count = 0;
                for (int j = 0; j < min; j++) {
                    if (word.charAt(j) == currentQuery.charAt(j)) {
                        count++;
                    } else {
                        break;
                    }
                }
                if (count / (float) l >= 0.5) {
                    foundWords.add(searchForWords.get(i));
                }
            }
        }
        handleFoundWords(foundWords, queryWord, false, cut);
    }

    private void handleFoundWords(ArrayList<String> foundWords, String[] queryWord, boolean inQuote) {
        handleFoundWords(foundWords, queryWord, inQuote, true);
    }
    private void handleFoundWords(ArrayList<String> foundWords, String[] queryWord, boolean inQuote, boolean cut) {
        if (!foundWords.isEmpty()) {
            boolean foundExactly = false;
            for (int i = 0; i < foundWords.size(); i++) {
                for (int j = 0; j < queryWord.length; j++) {
                    if (foundWords.get(i).contains(queryWord[j])) {
                        foundExactly = true;
                        break;
                    }
                }
                if (foundExactly) {
                    break;
                }
            }
            if (foundExactly) {
                for (int i = 0; i < foundWords.size(); i++) {
                    boolean findMatch = false;
                    for (int j = 0; j < queryWord.length; j++) {
                        if (foundWords.get(i).contains(queryWord[j])) {
                            findMatch = true;
                            break;
                        }
                    }
                    if (!findMatch) {
                        foundWords.remove(i--);
                    }
                }
                if (foundWords.size() > 0) {
                    Collections.sort(foundWords, (s, s1) -> s1.length() - s.length());
                    String s = foundWords.get(0);
                    foundWords.clear();
                    foundWords.add(s);
                }
            }
            highlightedWords = foundWords;
            if (messageOwner.message != null) {
                applyEntities();
                CharSequence text = null;
                if (!TextUtils.isEmpty(caption)) {
                    text = caption;
                } else {
                    text = messageText;
                }
                CharSequence charSequence = AndroidUtilities.replaceMultipleCharSequence("\n", text, " ");
                if (inQuote && messageOwner != null && messageOwner.reply_to != null && messageOwner.reply_to.quote_text != null) {
                    SpannableStringBuilder quoteText = new SpannableStringBuilder(messageOwner.reply_to.quote_text);
                    addEntitiesToText(quoteText, messageOwner.reply_to.quote_entities, isOutOwner(), false, false, false);
                    SpannableString quoteIcon = new SpannableString("q ");
                    ColoredImageSpan quoteIconSpan = new ColoredImageSpan(R.drawable.mini_quote);
                    quoteIconSpan.setOverrideColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
                    quoteIcon.setSpan(quoteIconSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    charSequence = new SpannableStringBuilder(quoteIcon).append(quoteText).append('\n').append(charSequence);
                }
                String str = charSequence.toString();
                int lastIndex = str.length();
                int startHighlightedIndex = str.toLowerCase().indexOf(foundWords.get(0));
                int maxSymbols = 120;
                if (startHighlightedIndex < 0) {
                    startHighlightedIndex = 0;
                }
                if (lastIndex > maxSymbols && cut) {
                    int newStart = Math.max(0, startHighlightedIndex - (int) (maxSymbols * .1f));
                    charSequence = charSequence.subSequence(newStart, Math.min(lastIndex, startHighlightedIndex - newStart + startHighlightedIndex + (int) (maxSymbols * .9f)));
                }
                messageTrimmedToHighlight = charSequence;
                messageTrimmedToHighlightCut = cut;
            }
        }
    }

    public void createMediaThumbs() {
        if (isStoryMedia()) {
            TL_stories.StoryItem storyItem = getMedia(messageOwner).storyItem;
            if (storyItem != null && storyItem.media != null) {
                TLRPC.Document document = storyItem.media.document;
                if (document != null) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                    TLRPC.PhotoSize qualityThumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320, false, null, true);
                    mediaThumb = ImageLocation.getForDocument(qualityThumb, document);
                    mediaSmallThumb = ImageLocation.getForDocument(thumb, document);
                } else {
                    TLRPC.PhotoSize currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 50);
                    TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 320, false, currentPhotoObjectThumb, true);
                    mediaThumb = ImageLocation.getForObject(currentPhotoObject, photoThumbsObject);
                    mediaSmallThumb = ImageLocation.getForObject(currentPhotoObjectThumb, photoThumbsObject);
                }
            }
        } else if (isVideo()) {
            TLRPC.Document document = getDocument();
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
            TLRPC.PhotoSize qualityThumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
            mediaThumb = ImageLocation.getForDocument(qualityThumb, document);
            mediaSmallThumb = ImageLocation.getForDocument(thumb, document);
        } else if (getMedia(messageOwner) instanceof TLRPC.TL_messageMediaPhoto && getMedia(messageOwner).photo != null && !photoThumbs.isEmpty()) {
            TLRPC.PhotoSize currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 50);
            TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 320, false, currentPhotoObjectThumb, false);
            mediaThumb = ImageLocation.getForObject(currentPhotoObject, photoThumbsObject);
            mediaSmallThumb = ImageLocation.getForObject(currentPhotoObjectThumb, photoThumbsObject);
        }
    }

    public boolean hasHighlightedWords() {
        return highlightedWords != null && !highlightedWords.isEmpty();
    }

    public boolean equals(MessageObject obj) {
        if (obj == null) {
            return false;
        }
        return getId() == obj.getId() && getDialogId() == obj.getDialogId();
    }

    public boolean isReactionsAvailable() {
        return !isEditing() && !isSponsored() && isSent() && !isEphemeral() && !isExpiredStory() && canSetReaction();
    }

    public boolean isPaidReactionChosen() {
        if (messageOwner.reactions == null) return false;
        for (int i = 0; i < messageOwner.reactions.results.size(); i++) {
            if (messageOwner.reactions.results.get(i).reaction instanceof TLRPC.TL_reactionPaid) {
                return messageOwner.reactions.results.get(i).chosen;
            }
        }
        return false;
    }

    public void addPaidReactions(int amount, boolean chosen, long peer) {
        if (messageOwner.reactions == null) {
            messageOwner.reactions = new TLRPC.TL_messageReactions();
            messageOwner.reactions.reactions_as_tags = MessageObject.getDialogId(messageOwner) == UserConfig.getInstance(currentAccount).getClientUserId();
            messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        addPaidReactions(currentAccount, messageOwner.reactions, amount, peer, chosen);
    }

    public Long getMyPaidReactionPeer() {
        if (messageOwner == null || messageOwner.reactions == null) return null;
        if (messageOwner.reactions.top_reactors == null) return null;
        for (TLRPC.MessageReactor reactor : messageOwner.reactions.top_reactors) {
            if (reactor != null && reactor.my) {
                if (reactor.anonymous) {
                    return UserObject.ANONYMOUS;
                } else if (reactor.peer_id != null) {
                    return DialogObject.getPeerDialogId(reactor.peer_id);
                }
            }
        }
        return null;
    }

    public static Long getMyPaidReactionPeer(TLRPC.MessageReactions reactions) {
        if (reactions == null) return null;
        if (reactions.top_reactors == null) return null;
        for (TLRPC.MessageReactor reactor : reactions.top_reactors) {
            if (reactor != null && reactor.my) {
                if (reactor.anonymous) {
                    return UserObject.ANONYMOUS;
                } else if (reactor.peer_id != null) {
                    return DialogObject.getPeerDialogId(reactor.peer_id);
                }
            }
        }
        return null;
    }

    public void setMyPaidReactionDialogId(long dialogId) {
        if (messageOwner == null || messageOwner.reactions == null) return;
        if (messageOwner.reactions.top_reactors == null) return;
        for (final TLRPC.MessageReactor reactor : messageOwner.reactions.top_reactors) {
            if (reactor != null && reactor.my) {
                reactor.anonymous = dialogId == UserObject.ANONYMOUS;
                if (reactor.anonymous) {
                    reactor.flags &=~ 8;
                    reactor.peer_id = null;
                } else {
                    reactor.flags |= 8;
                    reactor.peer_id = MessagesController.getInstance(currentAccount).getPeer(dialogId);
                }
            }
        }
    }

    public boolean doesPaidReactionExist() {
        if (messageOwner.reactions == null) {
            messageOwner.reactions = new TLRPC.TL_messageReactions();
            messageOwner.reactions.reactions_as_tags = MessageObject.getDialogId(messageOwner) == UserConfig.getInstance(currentAccount).getClientUserId();
            messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        for (int i = 0; i < messageOwner.reactions.results.size(); i++) {
            if (messageOwner.reactions.results.get(i).reaction instanceof TLRPC.TL_reactionPaid) {
                return true;
            }
        }
        return false;
    }

    public boolean ensurePaidReactionsExist(boolean chosen) {
        if (messageOwner.reactions == null) {
            messageOwner.reactions = new TLRPC.TL_messageReactions();
            messageOwner.reactions.reactions_as_tags = MessageObject.getDialogId(messageOwner) == UserConfig.getInstance(currentAccount).getClientUserId();
            messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        TLRPC.ReactionCount reactionCount = null;
        for (int i = 0; i < messageOwner.reactions.results.size(); i++) {
            if (messageOwner.reactions.results.get(i).reaction instanceof TLRPC.TL_reactionPaid) {
                reactionCount = messageOwner.reactions.results.get(i);
            }
        }
        if (reactionCount == null) {
            reactionCount = new TLRPC.TL_reactionCount();
            reactionCount.reaction = new TLRPC.TL_reactionPaid();
            reactionCount.count = 1;
            reactionCount.chosen = chosen;
            messageOwner.reactions.results.add(0, reactionCount);
            return true;
        }
        return false;
    }

    public static void addPaidReactions(
        int currentAccount,
        TLRPC.MessageReactions reactions,
        int amount,
        long peer,
        boolean chosen
    ) {
        TLRPC.ReactionCount reactionCount = null;
        for (int i = 0; i < reactions.results.size(); i++) {
            if (reactions.results.get(i).reaction instanceof TLRPC.TL_reactionPaid) {
                reactionCount = reactions.results.get(i);
            }
        }
        TLRPC.MessageReactor reactor = null;
        for (int i = 0; i < reactions.top_reactors.size(); i++) {
            if (reactions.top_reactors.get(i).my) {
                reactor = reactions.top_reactors.get(i);
                break;
            }
        }
        if (reactionCount == null && amount > 0) {
            reactionCount = new TLRPC.TL_reactionCount();
            reactionCount.reaction = new TLRPC.TL_reactionPaid();
            reactions.results.add(0, reactionCount);
        }
        if (reactionCount != null) {
            reactionCount.chosen = chosen;
            reactionCount.count = Math.max(0, reactionCount.count + amount);
            if (reactionCount.count <= 0) {
                reactions.results.remove(reactionCount);
            }
        }
        if (reactor == null && amount > 0) {
            reactor = new TLRPC.TL_messageReactor();
            reactor.my = true;
            reactions.top_reactors.add(reactor);
        }
        if (reactor != null) {
            reactor.count = Math.max(0, reactor.count + amount);
            reactor.anonymous = peer == UserObject.ANONYMOUS;
            if (peer == 0 || peer == UserObject.ANONYMOUS) {
                reactor.peer_id = MessagesController.getInstance(currentAccount).getPeer(UserConfig.getInstance(currentAccount).getClientUserId());
            } else {
                reactor.peer_id = MessagesController.getInstance(currentAccount).getPeer(peer);
            }
            if (reactor.count <= 0) {
                reactions.top_reactors.remove(reactor);
            }
        }
    }

    public boolean selectReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean big, boolean fromDoubleTap) {
        if (messageOwner.reactions == null) {
            messageOwner.reactions = new TLRPC.TL_messageReactions();
            messageOwner.reactions.reactions_as_tags = MessageObject.getDialogId(messageOwner) == UserConfig.getInstance(currentAccount).getClientUserId();
            messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }

        ArrayList<TLRPC.ReactionCount> choosenReactions = new ArrayList<>();
        TLRPC.ReactionCount newReaction = null;
        int maxChoosenOrder = 0;
        for (int i = 0; i < messageOwner.reactions.results.size(); i++) {
            final TLRPC.ReactionCount reactionCount = messageOwner.reactions.results.get(i);
            if (reactionCount.chosen && !(reactionCount.reaction instanceof TLRPC.TL_reactionPaid)) {
                choosenReactions.add(reactionCount);
                if (reactionCount.chosen_order > maxChoosenOrder) {
                    maxChoosenOrder = reactionCount.chosen_order;
                }
            }
            TLRPC.Reaction tl_reaction = messageOwner.reactions.results.get(i).reaction;
            if (tl_reaction instanceof TLRPC.TL_reactionEmoji) {
                if (visibleReaction.emojicon == null) {
                    continue;
                }
                if (((TLRPC.TL_reactionEmoji) tl_reaction).emoticon.equals(visibleReaction.emojicon)) {
                    newReaction = messageOwner.reactions.results.get(i);
                }
            }
            if (tl_reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                if (visibleReaction.documentId == 0) {
                    continue;
                }
                if (((TLRPC.TL_reactionCustomEmoji) tl_reaction).document_id == visibleReaction.documentId) {
                    newReaction = messageOwner.reactions.results.get(i);
                }
            }
        }

        if (!choosenReactions.isEmpty() && choosenReactions.contains(newReaction) && big) {
            return true;
        }
        int maxReactionsCount = MessagesController.getInstance(currentAccount).getMaxUserReactionsCount();

        if (!choosenReactions.isEmpty() && choosenReactions.contains(newReaction)) {
            if (newReaction != null) {
                newReaction.chosen = false;
                newReaction.count--;
                if (newReaction.count <= 0) {
                    messageOwner.reactions.results.remove(newReaction);
                }
            }
            if (messageOwner.reactions.can_see_list) {
                for (int i = 0; i < messageOwner.reactions.recent_reactions.size(); i++) {
                    if (MessageObject.getPeerId(messageOwner.reactions.recent_reactions.get(i).peer_id) == UserConfig.getInstance(currentAccount).getClientUserId() && ReactionsUtils.compare(messageOwner.reactions.recent_reactions.get(i).reaction, visibleReaction)) {
                        messageOwner.reactions.recent_reactions.remove(i);
                        i--;
                    }
                }
            }
            reactionsChanged = true;
            return false;
        }

        while (!choosenReactions.isEmpty() && choosenReactions.size() >= maxReactionsCount) {
            int minIndex = 0;
            for (int i = 1; i < choosenReactions.size(); i++) {
                if (
                    !(choosenReactions.get(i).reaction instanceof TLRPC.TL_reactionPaid) &&
                    choosenReactions.get(i).chosen_order < choosenReactions.get(minIndex).chosen_order
                ) {
                    minIndex = i;
                }
            }
            TLRPC.ReactionCount choosenReaction = choosenReactions.get(minIndex);
            choosenReaction.chosen = false;
            choosenReaction.count--;
            if (choosenReaction.count <= 0) {
                messageOwner.reactions.results.remove(choosenReaction);
            }
            choosenReactions.remove(choosenReaction);

            if (messageOwner.reactions.can_see_list) {
                for (int i = 0; i < messageOwner.reactions.recent_reactions.size(); i++) {
                    if (MessageObject.getPeerId(messageOwner.reactions.recent_reactions.get(i).peer_id) == UserConfig.getInstance(currentAccount).getClientUserId() && ReactionsUtils.compare(messageOwner.reactions.recent_reactions.get(i).reaction, visibleReaction)) {
                        messageOwner.reactions.recent_reactions.remove(i);
                        i--;
                    }
                }
            }
        }

        if (newReaction == null) {
            int maxChatReactions = MessagesController.getInstance(currentAccount).getChatMaxUniqReactions(getDialogId());
            int chosenCount = 0;
            if (messageOwner != null && messageOwner.reactions != null) {
                for (TLRPC.ReactionCount reactionCount : messageOwner.reactions.results) {
                    if (!(reactionCount.reaction instanceof TLRPC.TL_reactionPaid)) {
                        chosenCount++;
                    }
                }
            }
            if (chosenCount + 1 > maxChatReactions) {
                return false;
            }
            newReaction = new TLRPC.TL_reactionCount();
            newReaction.reaction = visibleReaction.toTLReaction();
            messageOwner.reactions.results.add(newReaction);
        }

        newReaction.chosen = true;
        newReaction.count++;
        newReaction.chosen_order = maxChoosenOrder + 1;

        if (messageOwner.reactions.can_see_list || (messageOwner.dialog_id > 0 && maxReactionsCount > 1)) {
            TLRPC.TL_messagePeerReaction action = new TLRPC.TL_messagePeerReaction();
            if (messageOwner.isThreadMessage && messageOwner.fwd_from != null) {
                action.peer_id = MessagesController.getInstance(currentAccount).getSendAsSelectedPeer(getFromChatId());
            } else {
                action.peer_id = MessagesController.getInstance(currentAccount).getSendAsSelectedPeer(getDialogId());
            }
            messageOwner.reactions.recent_reactions.add(0, action);


            if (visibleReaction.emojicon != null) {
                action.reaction = new TLRPC.TL_reactionEmoji();
                ((TLRPC.TL_reactionEmoji) action.reaction).emoticon = visibleReaction.emojicon;
            } else {
                action.reaction = new TLRPC.TL_reactionCustomEmoji();
                ((TLRPC.TL_reactionCustomEmoji) action.reaction).document_id = visibleReaction.documentId;
            }
        }
        reactionsChanged = true;
        return true;
    }

    public boolean probablyRingtone() {
        if (isVoiceOnce()) return false;
        if (getDocument() != null && RingtoneDataStore.ringtoneSupportedMimeType.contains(getDocument().mime_type) && getDocument().size < MessagesController.getInstance(currentAccount).ringtoneSizeMax * 2) {
            for (int a = 0; a < getDocument().attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = getDocument().attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (attribute.duration < 5) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public byte[] getWaveform() {
        if (getDocument() == null) {
            return null;
        }
        for (int a = 0; a < getDocument().attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = getDocument().attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                if (attribute.waveform == null || attribute.waveform.length == 0) {
                    MediaController.getInstance().generateWaveform(this);
                }
                return attribute.waveform;
            }
        }
        if (isRoundVideo()) {
            if (randomWaveform == null) {
                randomWaveform = new byte[120];
                for (int i = 0; i < randomWaveform.length; ++i) {
                    randomWaveform[i] = (byte) (255 * Math.random());
                }
            }
            return randomWaveform;
        }
        return null;
    }

    public boolean isStory() {
        return storyItem != null;
    }

    public boolean isBotPreview() {
        return storyItem instanceof StoriesController.BotPreview;
    }

    private TLRPC.WebPage storyMentionWebpage;
    public TLRPC.WebPage getStoryMentionWebpage() {
        if (!isStoryMention()) {
            return null;
        }
        if (storyMentionWebpage != null) {
            return storyMentionWebpage;
        }
        TLRPC.WebPage webpage = new TLRPC.TL_webPage();
        webpage.type = "telegram_story";
        TLRPC.TL_webPageAttributeStory attr = new TLRPC.TL_webPageAttributeStory();
        attr.id = messageOwner.media.id;
        attr.peer = MessagesController.getInstance(currentAccount).getPeer(messageOwner.media.user_id);
        if (messageOwner.media.storyItem != null) {
            attr.flags |= 1;
            attr.storyItem = messageOwner.media.storyItem;
        }
        webpage.attributes.add(attr);
        return (storyMentionWebpage = webpage);
    }

    public boolean isStoryMention() {
        return type == MessageObject.TYPE_STORY_MENTION && !isExpiredStory();
    }

    public boolean isGiveaway() {
        return type == MessageObject.TYPE_GIVEAWAY;
    }

    public boolean isGiveawayOrGiveawayResults() {
        return isGiveaway() || isGiveawayResults();
    }

    public boolean isGiveawayResults() {
        return type == MessageObject.TYPE_GIVEAWAY_RESULTS;
    }

    public boolean isAnyGift() {
        return type == MessageObject.TYPE_GIFT_STARS || type == MessageObject.TYPE_GIFT_PREMIUM || type == MessageObject.TYPE_GIFT_PREMIUM_CHANNEL;
    }

    private static CharSequence[] userSpan;
    public static CharSequence userSpan() {
        return userSpan(0);
    }
    public static CharSequence userSpan(int a) {
        if (userSpan == null) {
            userSpan = new CharSequence[2];
        }
        if (userSpan[a] == null) {
            userSpan[a] = new SpannableStringBuilder("u");
            ColoredImageSpan span = new ColoredImageSpan(R.drawable.mini_reply_user);
            span.spaceScaleX = .9f;
            if (a == 0) {
                span.translate(0, AndroidUtilities.dp(1));
            }
//            span.setScale(.7f, .7f);
            ((SpannableStringBuilder) userSpan[a]).setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return userSpan[a];
    }
    private static CharSequence groupSpan;
    public static CharSequence groupSpan() {
        if (groupSpan == null) {
            groupSpan = new SpannableStringBuilder("g");
            ColoredImageSpan span = new ColoredImageSpan(R.drawable.msg_folders_groups);
            span.setScale(.7f, .7f);
            ((SpannableStringBuilder) groupSpan).setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return groupSpan;
    }
    private static CharSequence channelSpan;
    public static CharSequence channelSpan() {
        if (channelSpan == null) {
            channelSpan = new SpannableStringBuilder("c");
            ColoredImageSpan span = new ColoredImageSpan(R.drawable.msg_folders_channels);
            span.setScale(.7f, .7f);
            ((SpannableStringBuilder) channelSpan).setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return channelSpan;
    }

    public static CharSequence peerNameWithIcon(int currentAccount, TLRPC.Peer peer) {
        return peerNameWithIcon(currentAccount, peer, !(peer instanceof TLRPC.TL_peerUser));
    }

    public static CharSequence peerNameWithIcon(int currentAccount, TLRPC.Peer peer, boolean anotherChat) {
        if (peer instanceof TLRPC.TL_peerUser) {
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(peer.user_id);
            if (user != null) {
                if (anotherChat) {
                    return new SpannableStringBuilder(userSpan()).append(" ").append(UserObject.getUserName(user));
                } else {
                    return UserObject.getUserName(user);
                }
            }
        } else if (peer instanceof TLRPC.TL_peerChat) {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(peer.chat_id);
            if (chat != null) {
                if (anotherChat) {
                    return new SpannableStringBuilder(ChatObject.isChannelAndNotMegaGroup(chat) ? channelSpan() : groupSpan()).append(" ").append(chat.title);
                } else {
                    return chat.title;
                }
            }
        } else if (peer instanceof TLRPC.TL_peerChannel) {
            TLRPC.Chat channel = MessagesController.getInstance(currentAccount).getChat(peer.channel_id);
            if (channel != null) {
                if (anotherChat) {
                    return new SpannableStringBuilder(ChatObject.isChannelAndNotMegaGroup(channel) ? channelSpan() : groupSpan()).append(" ").append(channel.title);
                } else {
                    return channel.title;
                }
            }
        }
        return "";
    }

    public static CharSequence peerNameWithIcon(int currentAccount, long did) {
        return peerNameWithIcon(currentAccount, did, false);
    }

    public static CharSequence peerNameWithIcon(int currentAccount, long did, boolean anotherChat) {
        if (did >= 0) {
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(did);
            if (user != null) {
                return AndroidUtilities.removeDiacritics(UserObject.getUserName(user));
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-did);
            if (chat != null) {
                return new SpannableStringBuilder(ChatObject.isChannelAndNotMegaGroup(chat) ? channelSpan() : groupSpan()).append(" ").append(AndroidUtilities.removeDiacritics(chat.title));
            }
        }
        return "";
    }

    public CharSequence getReplyQuoteNameWithIcon() {
        if (messageOwner == null) {
            return "";
        }
        CharSequence senderName = null;
        CharSequence chatName = null;
        if (messageOwner.reply_to == null) {
            if (DialogObject.isChatDialog(getDialogId())) {
                chatName = peerNameWithIcon(currentAccount, getDialogId());
            } else {
                senderName = peerNameWithIcon(currentAccount, getDialogId());
            }
        } else if (messageOwner.reply_to.reply_from != null) {
            final boolean anotherChat = messageOwner.reply_to.reply_to_peer_id == null || DialogObject.getPeerDialogId(messageOwner.reply_to.reply_to_peer_id) != getDialogId();
            if (messageOwner.reply_to.reply_from.from_id != null) {
                if (messageOwner.reply_to.reply_from.from_id instanceof TLRPC.TL_peerUser) {
                    senderName = peerNameWithIcon(currentAccount, messageOwner.reply_to.reply_from.from_id, anotherChat);
                } else {
                    chatName = peerNameWithIcon(currentAccount, messageOwner.reply_to.reply_from.from_id, anotherChat);
                }
            } else if (messageOwner.reply_to.reply_from.saved_from_peer != null) {
                if (messageOwner.reply_to.reply_from.saved_from_peer instanceof TLRPC.TL_peerUser) {
                    senderName = peerNameWithIcon(currentAccount, messageOwner.reply_to.reply_from.saved_from_peer, anotherChat);
                } else {
                    chatName = peerNameWithIcon(currentAccount, messageOwner.reply_to.reply_from.saved_from_peer, anotherChat);
                }
            } else if (!TextUtils.isEmpty(messageOwner.reply_to.reply_from.from_name)) {
                if (anotherChat) {
                    senderName = new SpannableStringBuilder(userSpan()).append(" ").append(messageOwner.reply_to.reply_from.from_name);
                } else {
                    senderName = new SpannableStringBuilder(messageOwner.reply_to.reply_from.from_name);
                }
            }
        }
        if (messageOwner.reply_to.reply_to_peer_id != null && DialogObject.getPeerDialogId(messageOwner.reply_to.reply_to_peer_id) != getDialogId()) {
            if (messageOwner.reply_to.reply_to_peer_id instanceof TLRPC.TL_peerUser) {
                senderName = peerNameWithIcon(currentAccount, messageOwner.reply_to.reply_to_peer_id, true);
            } else {
                chatName = peerNameWithIcon(currentAccount, messageOwner.reply_to.reply_to_peer_id);
            }
        }
        if (replyMessageObject != null) {
            if (DialogObject.isChatDialog(replyMessageObject.getSenderId())) {
                if (chatName == null) {
                    chatName = peerNameWithIcon(currentAccount, replyMessageObject.getSenderId());
                }
            } else {
                if (senderName == null) {
                    senderName = peerNameWithIcon(currentAccount, replyMessageObject.getSenderId());
                }
            }
        }
        if (chatName != null && senderName != null) {
            return new SpannableStringBuilder(senderName).append(" ").append(chatName);
        } else if (chatName != null) {
            return chatName;
        } else if (senderName != null) {
            return senderName;
        }
        return getString(R.string.Loading);
    }

    public boolean hasLinkMediaToMakeSmall() {
        final boolean hasLinkPreview = !isRestrictedMessage && MessageObject.getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && MessageObject.getMedia(messageOwner).webpage instanceof TLRPC.TL_webPage;
        final TLRPC.WebPage webpage = hasLinkPreview ? MessageObject.getMedia(messageOwner).webpage : null;
        final String webpageType = webpage != null ? webpage.type : null;
        return hasLinkPreview && !isGiveawayOrGiveawayResults() &&
            webpage != null && (webpage.photo != null || isVideoDocument(webpage.document)) &&
            !(webpage != null && TextUtils.isEmpty(webpage.description) && TextUtils.isEmpty(webpage.title)) &&
            !isSponsored() && // drawInstantViewType = 1
            !"telegram_megagroup".equals(webpageType) &&     // drawInstantViewType = 2
            !"telegram_background".equals(webpageType) &&    // drawInstantViewType = 6
            !"telegram_voicechat".equals(webpageType) &&     // drawInstantViewType = 9
            !"telegram_videochat".equals(webpageType) &&
            !"telegram_livestream".equals(webpageType) &&    // drawInstantViewType = 11
            !"telegram_user".equals(webpageType) &&          // drawInstantViewType = 13
            !"telegram_story".equals(webpageType) &&         // drawInstantViewType = 17
            !"telegram_channel_boost".equals(webpageType) && // drawInstantViewType = 18
            !"telegram_group_boost".equals(webpageType) &&   // drawInstantViewType = 21
            !"telegram_chat".equals(webpageType)
        ;
    }

    public boolean isLinkMediaSmall() {
        final boolean hasLinkPreview = !isRestrictedMessage && MessageObject.getMedia(messageOwner) instanceof TLRPC.TL_messageMediaWebPage && MessageObject.getMedia(messageOwner).webpage instanceof TLRPC.TL_webPage;
        final TLRPC.WebPage webpage = hasLinkPreview ? MessageObject.getMedia(messageOwner).webpage : null;
        final String webpageType = webpage != null ? webpage.type : null;
        return !(webpage != null && TextUtils.isEmpty(webpage.description) && TextUtils.isEmpty(webpage.title)) && (
                "app".equals(webpageType) || "profile".equals(webpageType) ||
                "article".equals(webpageType) || "telegram_bot".equals(webpageType) ||
                "telegram_user".equals(webpageType) || "telegram_channel".equals(webpageType) ||
                "telegram_megagroup".equals(webpageType) || "telegram_voicechat".equals(webpageType) || "telegram_videochat".equals(webpageType) ||
                "telegram_livestream".equals(webpageType) || "telegram_channel_boost".equals(webpageType) || "telegram_group_boost".equals(webpageType) ||
                "telegram_chat".equals(webpageType)
        );
    }


    public static class TextRange {
        public int start, end;

        public boolean quote;
        public boolean code;
        public boolean collapse;
        public String language;

        public TextRange(int start, int end) {
            this.start = start;
            this.end = end;
        }
        public TextRange(int start, int end, boolean quote, boolean code, boolean collapse, String language) {
            this.start = start;
            this.end = end;
            this.quote = quote;
            this.code = code;
            this.collapse = quote && collapse;
            this.language = language;
        }
    }

    public static void cutIntoRanges(CharSequence text, ArrayList<TextRange> ranges) {
        if (text == null) {
            return;
        }
        if (!(text instanceof Spanned)) {
            ranges.add(new TextRange(0, text.length()));
            return;
        }

        final int QUOTE_START = 1;
        final int QUOTE_END = 2;
        final int CODE_START = 4;
        final int CODE_END = 8;
        final int QUOTE_START_COLLAPSE = 16;

        final TreeSet<Integer> cutIndexes = new TreeSet<>();
        final HashMap<Integer, Integer> cutToType = new HashMap<>();

        Spanned spanned = (Spanned) text;
        QuoteSpan.QuoteStyleSpan[] quoteSpans = spanned.getSpans(0, spanned.length(), QuoteSpan.QuoteStyleSpan.class);
        for (int i = 0; i < quoteSpans.length; ++i) {
            quoteSpans[i].span.adaptLineHeight = false;

            int start = spanned.getSpanStart(quoteSpans[i]);
            int end = spanned.getSpanEnd(quoteSpans[i]);

            cutIndexes.add(start);
            cutToType.put(start, (cutToType.containsKey(start) ? cutToType.get(start) : 0) | (quoteSpans[i].span.isCollapsing ? QUOTE_START_COLLAPSE : QUOTE_START));

            cutIndexes.add(end);
            cutToType.put(end, (cutToType.containsKey(end) ? cutToType.get(end) : 0) | QUOTE_END);
        }

        // join quotes: some clients cut their quotes when formatting is inside :|
        final Iterator<Integer> I = cutIndexes.iterator();
        while (I.hasNext()) {
            final int index = I.next();
            if (index < 0 || index >= spanned.length()) continue;
            if (!cutToType.containsKey(index)) continue;
            final int type = cutToType.get(index);
            if ((type & (QUOTE_START | QUOTE_START_COLLAPSE)) != 0 && (type & QUOTE_END) != 0) {
                if (spanned.charAt(index) == '\n') continue;
                if (index - 1 > 0 && spanned.charAt(index - 1) == '\n') continue;
                I.remove();
                cutToType.remove(index);
            }
        }

        int codeSpanIndex = 0;
        CodeHighlighting.Span[] codeSpans = spanned.getSpans(0, spanned.length(), CodeHighlighting.Span.class);
        for (int i = 0; i < codeSpans.length; ++i) {
            int start = spanned.getSpanStart(codeSpans[i]);
            int end = spanned.getSpanEnd(codeSpans[i]);

            cutIndexes.add(start);
            cutToType.put(start, (cutToType.containsKey(start) ? cutToType.get(start) : 0) | CODE_START);

            cutIndexes.add(end);
            cutToType.put(end, (cutToType.containsKey(end) ? cutToType.get(end) : 0) | CODE_END);
        }

        int from = 0;
        boolean quoteCollapse = false;
        int quoteCount = 0, codeCount = 0;
        for (Iterator<Integer> i = cutIndexes.iterator(); i.hasNext(); ) {
            int cutIndex = i.next();
            final int type = cutToType.get(cutIndex);

            if (from != cutIndex) {
                if (cutIndex - 1 >= 0 && cutIndex - 1 < text.length() && text.charAt(cutIndex - 1) == '\n') {
                    cutIndex--;
                }

                String lng = null;
                if ((type & CODE_END) != 0 && codeSpanIndex < codeSpans.length) {
                    lng = codeSpans[codeSpanIndex].lng;
                    codeSpanIndex++;
                }

                ranges.add(new TextRange(from, cutIndex, quoteCount > 0, codeCount > 0, quoteCollapse, lng));
                from = cutIndex;
                if (from + 1 < text.length() && text.charAt(from) == '\n') {
                    from++;
                }
            }

            if ((type & QUOTE_END) != 0) quoteCount--;
            if ((type & QUOTE_START) != 0 || (type & QUOTE_START_COLLAPSE) != 0) {
                quoteCount++;
                quoteCollapse = (type & QUOTE_START_COLLAPSE) != 0;
            }
            if ((type & CODE_END) != 0) codeCount--;
            if ((type & CODE_START) != 0) codeCount++;
        }
        if (from < text.length()) {
            ranges.add(new TextRange(from, text.length(), quoteCount > 0, codeCount > 0, quoteCollapse, null));
        }
    }

    public void toggleChannelRecommendations() {
        expandChannelRecommendations(!channelJoinedExpanded);
    }

    public void expandChannelRecommendations(boolean expand) {
        MessagesController.getInstance(currentAccount).getMainSettings().edit()
                .putBoolean("c" + getDialogId() + "_rec", channelJoinedExpanded = expand)
                .apply();
    }

    public static int findQuoteStart(String text, String quote, int quote_offset) {
        if (text == null || quote == null) {
            return -1;
        }
        if (quote_offset == -1) {
            return text.indexOf(quote);
        }
        if (quote_offset + quote.length() < text.length() && text.startsWith(quote, quote_offset)) {
            return quote_offset;
        }
        int nextIndex = text.indexOf(quote, quote_offset);
        int prevIndex = text.lastIndexOf(quote, quote_offset);
        if (nextIndex == -1) return prevIndex;
        if (prevIndex == -1) return nextIndex;
        if (nextIndex - quote_offset < quote_offset - prevIndex) {
            return nextIndex;
        }
        return prevIndex;
    }

    public void applyQuickReply(String name, int id) {
        if (messageOwner == null) return;
        if (id != 0) {
            messageOwner.flags |= 1073741824;
            messageOwner.quick_reply_shortcut_id = id;
//        }
            TLRPC.TL_inputQuickReplyShortcutId shortcut = new TLRPC.TL_inputQuickReplyShortcutId();
            shortcut.shortcut_id = id;
            messageOwner.quick_reply_shortcut = shortcut;
        } else if (name != null) {
            TLRPC.TL_inputQuickReplyShortcut shortcut = new TLRPC.TL_inputQuickReplyShortcut();
            shortcut.shortcut = name;
            messageOwner.quick_reply_shortcut = shortcut;
        } else {
            messageOwner.flags &=~ 1073741824;
            messageOwner.quick_reply_shortcut_id = 0;
            messageOwner.quick_reply_shortcut = null;
        }
    }

    public static int getQuickReplyId(TLRPC.Message message) {
        if (message == null) return 0;
        if ((message.flags & 1073741824) != 0) {
            return message.quick_reply_shortcut_id;
        }
        if (message.quick_reply_shortcut instanceof TLRPC.TL_inputQuickReplyShortcutId) {
            return ((TLRPC.TL_inputQuickReplyShortcutId) message.quick_reply_shortcut).shortcut_id;
        }
        return 0;
    }

    public static int getQuickReplyId(int currentAccount, TLRPC.Message message) {
        if (message == null) return 0;
        if ((message.flags & 1073741824) != 0) {
            return message.quick_reply_shortcut_id;
        }
        if (message.quick_reply_shortcut instanceof TLRPC.TL_inputQuickReplyShortcutId) {
            return ((TLRPC.TL_inputQuickReplyShortcutId) message.quick_reply_shortcut).shortcut_id;
        }
        String replyName = getQuickReplyName(message);
        if (replyName != null) {
            QuickRepliesController.QuickReply reply = QuickRepliesController.getInstance(currentAccount).findReply(replyName);
            if (reply != null) {
                return reply.id;
            }
        }
        return 0;
    }

    public int getQuickReplyId() {
        return getQuickReplyId(messageOwner);
    }

    public static String getQuickReplyName(TLRPC.Message message) {
        if (message == null) return null;
        if (message.quick_reply_shortcut instanceof TLRPC.TL_inputQuickReplyShortcut) {
            return ((TLRPC.TL_inputQuickReplyShortcut) message.quick_reply_shortcut).shortcut;
        }
        return null;
    }

    public String getQuickReplyName() {
        return getQuickReplyName(messageOwner);
    }

    public String getQuickReplyDisplayName() {
        String name = getQuickReplyName();
        if (name != null) return name;
        QuickRepliesController.QuickReply quickReply = QuickRepliesController.getInstance(currentAccount).findReply(getQuickReplyId());
        if (quickReply != null) return quickReply.name;
        return "";
    }

    public static boolean isQuickReply(TLRPC.Message message) {
        return message != null && ((message.flags & 1073741824) != 0 || message.quick_reply_shortcut != null);
    }

    public boolean isQuickReply() {
        return isQuickReply(messageOwner);
    }
    
    public TLRPC.TL_availableEffect getEffect() {
        if (messageOwner == null || (messageOwner.flags2 & 4) == 0)
            return null;
        return MessagesController.getInstance(currentAccount).getEffect(messageOwner.effect);
    }

    public long getEffectId() {
        if (messageOwner == null || (messageOwner.flags2 & 4) == 0)
            return 0;
        return messageOwner.effect;
    }

    public TLRPC.TL_factCheck getFactCheck() {
        return FactCheckController.getInstance(currentAccount).getFactCheck(this);
    }

    public boolean factCheckExpanded;

    private CharSequence factCheckText;
    public CharSequence getFactCheckText() {
        if (!isFactCheckable())
            return null;
        TLRPC.TL_factCheck factCheck = getFactCheck();
        if (factCheck == null || factCheck.text == null)
            return factCheckText = null;
//        if (factCheckText == null) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(factCheck.text.text);
            addEntitiesToText(stringBuilder, factCheck.text.entities, isOutOwner(), false, false, false);
            factCheckText = stringBuilder;
//        }
        return factCheckText;
    }

    public boolean isFactCheckable() {
        return getId() >= 0 && !isSponsored() && (
            type == TYPE_TEXT ||
            type == TYPE_VOICE ||
            type == TYPE_PHOTO ||
            type == TYPE_VIDEO ||
            type == TYPE_GIF ||
            type == TYPE_FILE
        );
    }

    public boolean hasEntitiesFromServer() {
        if (messageOwner == null) return false;
        if (messageOwner.entities == null) return false;
        for (int i = 0; i < messageOwner.entities.size(); ++i) {
            final TLRPC.MessageEntity e = messageOwner.entities.get(i);
            if (e instanceof TLRPC.TL_messageEntityPhone || e instanceof TLRPC.TL_messageEntityBankCard)
                return true;
        }
        return false;
    }

    public boolean isAlbumSingle() {
        return getMedia(this) instanceof TLRPC.TL_messageMediaPaidMedia;
    }

    public float getProgress() {
        return 0f;
    }

    private Boolean videoQualitiesCached;
    public ArrayList<VideoPlayer.Quality> videoQualities;
    public TLRPC.Document qualityToSave;

    public VideoPlayer.VideoUri highestQuality, thumbQuality, cachedQuality;

    public boolean hasVideoQualities() {
        return hasVideoQualities(true);
    }

    public boolean hasVideoQualities(boolean useFileDatabaseQueue) {
        if (videoQualitiesCached == null) {
            try {
                if (messageOwner == null || messageOwner.media == null || messageOwner.media.document == null || messageOwner.media.alt_documents.isEmpty()) {
                    return videoQualitiesCached = false;
                }
                videoQualities = VideoPlayer.getQualities(currentAccount, messageOwner != null ? messageOwner.media : null, useFileDatabaseQueue);
                videoQualitiesCached = videoQualities != null && videoQualities.size() > 1;
                highestQuality = VideoPlayer.getQualityForPlayer(videoQualities);
                thumbQuality = VideoPlayer.getQualityForThumb(videoQualities);
                cachedQuality = VideoPlayer.getCachedQuality(videoQualities);
            } catch (Exception e) {
                FileLog.e(e);
                videoQualitiesCached = false;
            }
        }
        return videoQualitiesCached;
    }

    public boolean isStarGiftAction() {
        return messageOwner != null && (messageOwner.action instanceof TLRPC.TL_messageActionStarGift || messageOwner.action instanceof TLRPC.TL_messageActionStarGiftUnique);
    }

    public boolean mediaExists() {
        if (hasVideoQualities() && highestQuality != null) {
            return highestQuality.isCached();
        }
        return mediaExists;
    }

    public void updateQualitiesCached(boolean useFileDatabaseQueue) {
        if (videoQualities == null) {
            cachedQuality = null;
            hasVideoQualities(useFileDatabaseQueue);
            return;
        }
        for (VideoPlayer.Quality q : videoQualities) {
            for (VideoPlayer.VideoUri u : q.uris) {
                u.updateCached(useFileDatabaseQueue);
            }
        }
        highestQuality = VideoPlayer.getQualityForPlayer(videoQualities);
        thumbQuality = VideoPlayer.getQualityForThumb(videoQualities);
        cachedQuality = VideoPlayer.getCachedQuality(videoQualities);
    }

    public boolean areTags() {
        if (messageOwner == null) return false;
        if (messageOwner.reactions == null) return false;
        return messageOwner.reactions.reactions_as_tags;
    }

    public boolean openedInViewer;

    private Integer cachedStartsTimestamp;
    public int getVideoStartsTimestamp() {
        if (cachedStartsTimestamp != null) return cachedStartsTimestamp;
        if (!isVideo()) return cachedStartsTimestamp = -1;
        final TLRPC.MessageMedia media = getMedia(this);
        if (media.video_timestamp != 0) {
            return media.video_timestamp;
        }
        if (media != null && media.webpage != null && media.webpage.url != null) {
            try {
                return cachedStartsTimestamp = LaunchActivity.getTimestampFromLink(Uri.parse(media.webpage.url));
            } catch (Exception e) {}
        }
        return cachedStartsTimestamp = -1;
    }

    public Float cachedSavedTimestamp;
    public float getVideoSavedProgress() {
//        if (cachedSavedTimestamp != null) return cachedSavedTimestamp;
        if (cachedSavedTimestamp != null) return PhotoViewer.getSavedProgressFast(this);
        return cachedSavedTimestamp = PhotoViewer.getSavedProgress(this);
    }

    public TLRPC.Photo getVideoCover() {
        TLRPC.MessageMedia media = getMedia(this);
        if (media instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.TL_messageMediaDocument mediaDocument = (TLRPC.TL_messageMediaDocument) media;
            return mediaDocument.video_cover;
        } else if (media != null && media.webpage != null && media.webpage.video_cover_photo) {
            return media.webpage.photo;
        }
        return null;
    }

    public boolean hasVideoCover() {
        return getVideoCover() != null;
    }

    public boolean isPaid() {
        return messageOwner != null && messageOwner.paid_message_stars > 0;
    }

    private CharSequence getActionSuggestionApprovalText(String channelName, String userName) {
        TLRPC.TL_messageActionSuggestedPostApproval approval = null;
        if (messageOwner != null && messageOwner.action instanceof TLRPC.TL_messageActionSuggestedPostApproval) {
            approval = (TLRPC.TL_messageActionSuggestedPostApproval) messageOwner.action;
        }
        if (approval == null) {
            return null;
        }

        MessageSuggestionParams suggestionOffer = MessageSuggestionParams.of(approval);

        final boolean isAdmin = ChatObject.canManageMonoForum(currentAccount, DialogObject.getPeerDialogId(messageOwner.peer_id));
        final SpannableStringBuilder ssb = new SpannableStringBuilder();

        if (approval.balance_too_low) {
            ssb.append(AndroidUtilities.replaceTags(
                LocaleController.formatString(R.string.SuggestionAgreementNotEnoughStars,
                userName
            )));
        } else if (approval.rejected) {
            boolean byAdmin = true;
            boolean fromYou = false;
            if (replyMessageObject != null && replyMessageObject.messageOwner != null) {
                long fromId = DialogObject.getPeerDialogId(replyMessageObject.messageOwner.from_id);
                long topicId = DialogObject.getPeerDialogId(replyMessageObject.messageOwner.saved_peer_id);
                if (fromId != topicId) {
                    byAdmin = false;
                }
                if (fromId == UserConfig.getInstance(currentAccount).getClientUserId()) {
                    fromYou = true;
                }
            }
            final int key = TextUtils.isEmpty(approval.reject_comment) ?
                (fromYou ? R.string.SuggestionAgreementDeclinedYou : R.string.SuggestionAgreementDeclinedThis):
                (fromYou ? R.string.SuggestionAgreementDeclinedYouComment : R.string.SuggestionAgreementDeclinedThisComment);

            ssb.append(AndroidUtilities.replaceTags(LocaleController.formatString(key, byAdmin ? channelName: userName)));

            if (!TextUtils.isEmpty(approval.reject_comment)) {
                ssb.append("\n\n");
                ssb.setSpan(new RelativeSizeSpan(0.8f), ssb.length() - 1, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                int p = ssb.length();
                ssb.append('"');
                ssb.append(approval.reject_comment);
                ssb.append('"');
                ssb.setSpan(new EllipsizeSpanAnimator.TextAlphaSpan(217), p, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            final int hours = (int) MessagesController.getInstance(currentAccount).config.starsSuggestedPostAgeMin.get(TimeUnit.HOURS);

            if (approval.schedule_date > 0) {
                final String date = LocaleController.formatDateTime(approval.schedule_date, true);
                final int currentDate = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                final int key;
                if (approval.schedule_date > currentDate) {
                    key = isAdmin ?
                            R.string.SuggestionAgreementReachedAdmin1:
                            R.string.SuggestionAgreementReachedUser1;
                } else {
                    key = isAdmin ?
                            R.string.SuggestionAgreementReachedAdmin1PastSimple :
                            R.string.SuggestionAgreementReachedUser1PastSimple;
                }
                ssb.append(AndroidUtilities.replaceTags(LocaleController.formatString(key, channelName, date)));
            } else {
                final int key = isAdmin ?
                    R.string.SuggestionAgreementReachedAdmin1PresentPerfect :
                    R.string.SuggestionAgreementReachedUser1PresentPerfect;

                ssb.append(AndroidUtilities.replaceTags(LocaleController.formatString(key, channelName)));
            }
            if (suggestionOffer.amount != null && !suggestionOffer.amount.isZero()) {
                final boolean isTon = suggestionOffer.amount.currency == AmountUtils.Currency.TON;

                {
                    final String text = isAdmin ?
                        LocaleController.formatString(R.string.SuggestionAgreementReachedAdmin2, userName, suggestionOffer.amount.asDecimalString()) :
                        LocaleController.formatString(R.string.SuggestionAgreementReachedUser2, suggestionOffer.amount.asDecimalString());

                    ssb.append("\n\n");
                    ssb.setSpan(new RelativeSizeSpan(0.6f), ssb.length() - 1, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(StarsIntroActivity.replaceStars(suggestionOffer.amount.currency == AmountUtils.Currency.TON, AndroidUtilities.replaceTags(text)));
                }
                {
                    final int key;
                    if (isTon) {
                        key = isAdmin ?
                            R.string.SuggestionAgreementReachedAdmin3TON:
                            R.string.SuggestionAgreementReachedUser3TON;
                    } else {
                        key = isAdmin ?
                            R.string.SuggestionAgreementReachedAdmin3Stars:
                            R.string.SuggestionAgreementReachedUser3Stars;
                    }

                    ssb.append("\n\n");
                    ssb.setSpan(new RelativeSizeSpan(0.6f), ssb.length() - 1, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(AndroidUtilities.replaceTags(LocaleController.formatString(key, channelName, hours)));
                }
                {
                    final int key;
                    if (isTon) {
                        key = isAdmin ?
                            R.string.SuggestionAgreementReachedAdmin4TON:
                            R.string.SuggestionAgreementReachedUser4TON;
                    } else {
                        key = isAdmin ?
                            R.string.SuggestionAgreementReachedAdmin4Stars:
                            R.string.SuggestionAgreementReachedUser4Stars;
                    }

                    ssb.append("\n\n");
                    ssb.setSpan(new RelativeSizeSpan(0.6f), ssb.length() - 1, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(AndroidUtilities.replaceTags(LocaleController.formatString(key, channelName, hours)));
                }
            }
        }

        return ssb;
    }

    public static TLRPC.PollAnswer findPollItem(MessageObject messageObject, byte[] task_id) {
        final TLRPC.MessageMedia media = getMedia(messageObject);
        if (!(media instanceof TLRPC.TL_messageMediaPoll)) {
            return null;
        }
        final TLRPC.TL_messageMediaPoll mediaTodo = (TLRPC.TL_messageMediaPoll) media;
        if (mediaTodo.poll == null || mediaTodo.poll.answers == null) {
            return null;
        }
        for (int i = 0; i < mediaTodo.poll.answers.size(); ++i) {
            TLRPC.PollAnswer ti = mediaTodo.poll.answers.get(i);
            if (Arrays.equals(ti.option, task_id)) {
                return ti;
            }
        }
        return null;
    }

    public static TLRPC.TodoItem findTodoItem(MessageObject messageObject, int task_id) {
        final TLRPC.MessageMedia media = getMedia(messageObject);
        if (!(media instanceof TLRPC.TL_messageMediaToDo)) {
            return null;
        }
        final TLRPC.TL_messageMediaToDo mediaTodo = (TLRPC.TL_messageMediaToDo) media;
        if (mediaTodo.todo == null || mediaTodo.todo.list == null) {
            return null;
        }
        for (int i = 0; i < mediaTodo.todo.list.size(); ++i) {
            TLRPC.TodoItem ti = mediaTodo.todo.list.get(i);
            if (ti.id == task_id) {
                return ti;
            }
        }
        return null;
    }

    public static boolean isCompleted(MessageObject messageObject, int taskId) {
        final TLRPC.MessageMedia media = getMedia(messageObject);
        if (!(media instanceof TLRPC.TL_messageMediaToDo)) {
            return false;
        }
        final TLRPC.TL_messageMediaToDo mediaTodo = (TLRPC.TL_messageMediaToDo) media;
        if (mediaTodo.todo == null || mediaTodo.todo.list == null) {
            return false;
        }
        return isCompleted(mediaTodo, taskId);
    }

    public static boolean isCompleted(TLRPC.TL_messageMediaToDo mediaTodo, int taskId) {
        for (int i = 0; i < mediaTodo.completions.size(); ++i) {
            final TLRPC.TodoCompletion completion = mediaTodo.completions.get(i);
            if (completion.id == taskId) {
                return true;
            }
        }
        return false;
    }

    public static void toggleTodo(int currentAccount, long dialogId, TLRPC.TL_messageMediaToDo mediaTodo, int taskId, boolean enable, int currentDate) {
        for (int i = 0; i < mediaTodo.completions.size(); ++i) {
            final TLRPC.TodoCompletion completion = mediaTodo.completions.get(i);
            if (completion.id == taskId) {
                mediaTodo.completions.remove(i);
                if (mediaTodo.completions.isEmpty()) {
                    mediaTodo.flags &=~ 1;
                }
                i--;
            }
        }
        if (enable) {
            final TLRPC.TL_todoCompletion completion = new TLRPC.TL_todoCompletion();
            completion.id = taskId;
            completion.completed_by = MessagesController.getInstance(currentAccount).getPeer(dialogId);
            completion.date = currentDate;
            mediaTodo.flags |= 1;
            mediaTodo.completions.add(completion);
        }
    }

    public long getPollHash() {
        if (messageOwner == null || type != TYPE_POLL) {
            return 0;
        }
        TLRPC.MessageMedia m = getMedia(messageOwner);
        if (m instanceof TLRPC.TL_messageMediaPoll) {
            return ((TLRPC.TL_messageMediaPoll) m).poll.hash;
        }
        return 0;
    }

    public boolean isPaidSuggestedPost() {
        return messageOwner != null && (messageOwner.paid_suggested_post_stars || messageOwner.paid_suggested_post_ton);
    }

    public boolean isPaidSuggestedPostProtected() {
        if (isPaidSuggestedPost()) {
            final int currentDate = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            final long unsafePaidSuggestedPostTime = MessagesController.getInstance(currentAccount).config.starsSuggestedPostAgeMin.get(TimeUnit.SECONDS);
            if ((currentDate - messageOwner.date) < unsafePaidSuggestedPostTime) {
                return true;
            }
        }

        return false;
    }

    public MessageSuggestionParams obtainSuggestionOffer() {
        if (messageOwner == null) {
            return null;
        }

        if (messageOwner.suggested_post != null) {
            return MessageSuggestionParams.of(messageOwner.suggested_post);
        }

        if (messageOwner.action instanceof TLRPC.TL_messageActionSuggestedPostApproval) {
            return MessageSuggestionParams.of((TLRPC.TL_messageActionSuggestedPostApproval) messageOwner.action);
        }

        return null;
    }

    public MessageSuggestionParams obtainSuggestionOfferFromReply() {
        return replyMessageObject != null ?
            replyMessageObject.obtainSuggestionOffer() : null;
    }

    public boolean isEditedSuggestionOffer() {
        return replyMessageObject != null && replyMessageObject.messageOwner != null && messageOwner != null && messageOwner.suggested_post != null;
    }

    public static final int SUGGESTION_FLAG_EDIT_PRCIE = 1;
    public static final int SUGGESTION_FLAG_EDIT_TIME = 1 << 1;
    public static final int SUGGESTION_FLAG_EDIT_TEXT = 1 << 2;
    public static final int SUGGESTION_FLAG_EDIT_MEDIA = 1 << 3;

    public int getEditedSuggestionFlags() {
        final TLRPC.Message oldMessage = replyMessageObject != null ? replyMessageObject.messageOwner : null;
        final TLRPC.Message newMessage = messageOwner;
        if (oldMessage == null || oldMessage.suggested_post == null || newMessage == null || newMessage.suggested_post == null) {
            return 0;
        }

        final TLRPC.SuggestedPost oldOffer = oldMessage.suggested_post;
        final TLRPC.SuggestedPost newOffer = newMessage.suggested_post;

        int flags = 0;
        if (!AmountUtils.Amount.equals(oldOffer.price, newOffer.price)) {
            flags |= SUGGESTION_FLAG_EDIT_PRCIE;
        }
        if (oldOffer.schedule_date != newOffer.schedule_date) {
            flags |= SUGGESTION_FLAG_EDIT_TIME;
        }
        if (!TextUtils.equals(messageText, replyMessageObject.messageText)) {
            flags |= SUGGESTION_FLAG_EDIT_TEXT;
        }
        if (!TextUtils.equals(caption, replyMessageObject.caption)) {
            flags |= SUGGESTION_FLAG_EDIT_TEXT;
        }

        TLRPC.MessageMedia oldMedia = oldMessage.media;
        TLRPC.MessageMedia newMedia = newMessage.media;
        if (oldMedia instanceof TLRPC.TL_messageMediaEmpty) {
            oldMedia = null;
        }
        if (newMedia instanceof TLRPC.TL_messageMediaEmpty) {
            newMedia = null;
        }

        if (oldMedia != null && newMedia != null) {
            if (oldMedia.getClass() == newMedia.getClass()) {
                if (oldMedia.photo != null && newMedia.photo != null) {
                    if (oldMedia.photo.id != newMedia.photo.id) {
                        flags |= SUGGESTION_FLAG_EDIT_MEDIA;
                    }
                } else if ((oldMedia.photo == null) != (newMedia.photo == null)) {
                    flags |= SUGGESTION_FLAG_EDIT_MEDIA;
                }
                if (oldMedia.document != null && newMedia.document != null) {
                    if (oldMedia.document.id != newMedia.document.id) {
                        flags |= SUGGESTION_FLAG_EDIT_MEDIA;
                    }
                } else if ((oldMedia.document == null) != (newMedia.document == null)) {
                    flags |= SUGGESTION_FLAG_EDIT_MEDIA;
                }
            } else {
                flags |= SUGGESTION_FLAG_EDIT_MEDIA;
            }
        } else if ((oldMedia == null) != (newMedia == null)) {
            flags |= SUGGESTION_FLAG_EDIT_MEDIA;
        }


        return flags;
    }

    public CharSequence getMessageTextToTranslate(GroupedMessages groupedMessages, int[] messageIdToTranslate) {
        if (translated || isRestrictedMessage) {
            return null;
        }
        if (summarized) {
            return messageText;
        }
        CharSequence messageTextToTranslate = null;
        if (type != MessageObject.TYPE_EMOJIS && type != MessageObject.TYPE_ANIMATED_STICKER && type != MessageObject.TYPE_STICKER) {
            messageTextToTranslate = ChatActivity.getMessageCaption(this, groupedMessages, messageIdToTranslate);
            if (messageTextToTranslate == null && isPoll()) {
                try {
                    TLRPC.Poll poll = ((TLRPC.TL_messageMediaPoll) messageOwner.media).poll;
                    StringBuilder pollText = new StringBuilder();
                    pollText = new StringBuilder(poll.question.text).append("\n");
                    for (TLRPC.PollAnswer answer : poll.answers)
                        pollText.append("\n\uD83D\uDD18 ").append(answer.text == null ? "" : answer.text.text);
                    messageTextToTranslate = pollText.toString();
                } catch (Exception e) {
                }
            }
            if (messageTextToTranslate == null && MessageObject.isMediaEmpty(messageOwner)) {
                messageTextToTranslate = ChatActivity.getMessageContent(this, 0, false);
            }
            if (messageTextToTranslate != null && Emoji.fullyConsistsOfEmojis(messageTextToTranslate)) {
                messageTextToTranslate = null; // message fully consists of emojis, do not translate
            }
        }
        return messageTextToTranslate;
    }

    public boolean isEphemeral() {
        return isEphemeral(messageOwner);
    }

    public int getEphemeralId() {
        return isEphemeral() ? ephemeralMessageIdUnpack(getId()) : 0;
    }

    public long getEphemeralReceiverBotId() {
        return messageOwner != null ? messageOwner.ephemeralReceiverBotId : 0;
    }

    public static boolean isEphemeral(TLRPC.Message message) {
        return message != null && (isEphemeralMessageId(message.id) || message.ephemeralReceiverBotId != 0);
    }

    public static boolean isEphemeralMessageId(int id) {
        return (id & MESSAGE_ID_RESERVED_BITS_MASK) == MESSAGE_ID_EPHEMERAL_BITS_MASK;
    }

    public static int ephemeralMessageIdPack(int id) {
        return (id & ~MESSAGE_ID_RESERVED_BITS_MASK) | MESSAGE_ID_EPHEMERAL_BITS_MASK;
    }

    public static int ephemeralMessageIdUnpack(int id) {
        return isEphemeralMessageId(id) ? (id &~ MESSAGE_ID_RESERVED_BITS_MASK) : id;
    }

    public boolean needResendWhenEdit() {
        return ChatObject.isMonoForum(currentAccount, getDialogId())
            && getFromChatId() != UserConfig.getInstance(currentAccount).getClientUserId()
            && !isOutOwner();
    }

    public static int getCompletionsCount(TLRPC.TL_messageMediaToDo media) {
        if (media == null || media.todo == null || media.todo.list == null) return 0;
        int total = 0;
        for (int i = 0; i < media.completions.size(); ++i) {
            final TLRPC.TodoCompletion c = media.completions.get(i);
            boolean found = false;
            for (int j = 0; j < media.todo.list.size(); ++j) {
                if (media.todo.list.get(j).id == c.id) {
                    found = true;
                    break;
                }
            }
            if (found) total++;
        }
        return total;
    }
}
