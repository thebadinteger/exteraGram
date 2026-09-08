privacyCell.setText(getString("SuggestContactsInfo", R.string.SuggestContactsInfo));
                    } else if (position == newChatsSectionRow) {
                        privacyCell.setText(getString("ArchiveAndMuteInfo", R.string.ArchiveAndMuteInfo));
                    }
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == privacySectionRow) {
                        headerCell.setText(getString("PrivacyTitle", R.string.PrivacyTitle));
                    } else if (position == securitySectionRow) {
                        headerCell.setText(getString("SecurityTitle", R.string.SecurityTitle));
                    } else if (position == advancedSectionRow) {
                        headerCell.setText(getString("DeleteMyAccount", R.string.DeleteMyAccount));
                    } else if (position == secretSectionRow) {
                        headerCell.setText(getString("SecretChat", R.string.SecretChat));
                    } else if (position == botsSectionRow) {
                        headerCell.setText(getString("PrivacyBots", R.string.PrivacyBots));
                    } else if (position == contactsSectionRow) {
                        headerCell.setText(getString("Contacts", R.string.Contacts));
                    } else if (position == newChatsHeaderRow) {
                        headerCell.setText(getString("NewChatsFromNonContacts", R.string.NewChatsFromNonContacts));
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position == secretWebpageRow) {
                        textCheckCell.setTextAndCheck(getString("SecretWebPage", R.string.SecretWebPage), getMessagesController().secretWebpagePreview == 1, false);
                    } else if (position == contactsSyncRow) {
                        textCheckCell.setTextAndCheck(getString("SyncContacts", R.string.SyncContacts), newSync, true);
                    } else if (position == contactsSuggestRow) {
                        textCheckCell.setTextAndCheck(getString("SuggestContacts", R.string.SuggestContacts), newSuggest, false);
                    } else if (position == newChatsRow) {
                        textCheckCell.setTextAndCheck(getString("ArchiveAndMute", R.string.ArchiveAndMute), archiveChats, false);
                    }
                    break;
                case 5:
                    TextCell textCell2 = (TextCell) holder.itemView;
                    animated = holder.itemView.getTag() != null && ((Integer) holder.itemView.getTag()) == position;
                    holder.itemView.setTag(position);
                    showLoading = false;
                    loadingLen = 16;
                    value = null;
                    textCell2.setPrioritizeTitleOverValue(false);
                    if (position == autoDeleteMesages) {
                        int ttl = getUserConfig().getGlobalTTl();
                        if (ttl == -1) {
                            showLoading = true;
                        } else if (ttl > 0) {
                            value = LocaleController.formatTTLString(ttl * 60);
                        } else {
                            value = getString("PasswordOff", R.string.PasswordOff);
                        }
                        textCell2.setTextAndValueAndIcon(getString("AutoDeleteMessages", R.string.AutoDeleteMessages), value, true, R.drawable.msg2_autodelete, true);
                    } else if (position == sessionsRow) {
                        String count = "";
                        if (devicesActivityPreload.getSessionsCount() == 0) {
                            if (getMessagesController().lastKnownSessionsCount == 0) {
                                showLoading = true;
                            } else {
                                count = String.format(LocaleController.getInstance().getCurrentLocale(), "%d", getMessagesController().lastKnownSessionsCount);
                            }
                        } else {
                            count = String.format(LocaleController.getInstance().getCurrentLocale(), "%d", devicesActivityPreload.getSessionsCount());
                        }
                        getMessagesController().lastKnownSessionsCount = devicesActivityPreload.getSessionsCount();
                        textCell2.setTextAndValueAndIcon(getString(R.string.SessionsTitle), count, true, R.drawable.msg2_devices, false);
                    } else if (position == emailLoginRow) {
                        CharSequence val = "";
                        if (currentPassword == null) {
                            showLoading = true;
                        } else {
                            SpannableStringBuilder spannable = SpannableStringBuilder.valueOf(currentPassword.login_email_pattern);
                            int startIndex = currentPassword.login_email_pattern.indexOf('*');
                            int endIndex = currentPassword.login_email_pattern.lastIndexOf('*');
                            if (startIndex != endIndex && startIndex != -1 && endIndex != -1) {
                                TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                                run.flags |= TextStyleSpan.FLAG_STYLE_SPOILER;
                                run.start = startIndex;
                                run.end = endIndex + 1;
                                spannable.setSpan(new TextStyleSpan(run), startIndex, endIndex + 1, 0);
                            }
                            val = spannable;
                        }
                        textCell2.setPrioritizeTitleOverValue(true);
                        textCell2.setTextAndSpoilersValueAndIcon(getString(R.string.EmailLogin), val, R.drawable.msg2_email, true);
                    } else if (position == passwordRow) {
                        value = "";
                        int icon = R.drawable.menu_2sv;
                        if (currentPassword == null) {
                            showLoading = true;
                        } else if (currentPassword.has_password) {
                            icon = R.drawable.menu_2sv_on;
                            value = getString(R.string.PasswordOn);
                        } else {
                            value = getString(R.string.PasswordOff);
                        }
                        textCell2.setTextAndValueAndIcon(getString(R.string.TwoStepVerification), value, true, icon, true);
                    } else if (position == passkeysRow) {
                        value = "";
                        if (currentPasskeys == null) {
                            showLoading = true;
                        } else if (currentPasskeys.size() == 1 && textCell2.valueTextView.getPaint().measureText(currentPasskeys.get(0).name) < AndroidUtilities.displaySize.x / 3f) {
                            value = currentPasskeys.get(0).name;
                        } else if (currentPasskeys.size() > 0) {
                            value = currentPasskeys.size() + "";
                        } else {
                            value = getString(R.string.PasswordOff);
                        }
                        textCell2.setTextAndValueAndIcon(getString(R.string.Passkey), value, true, R.drawable.msg2_permissions, true);
                    } else if (position == passcodeRow) {
                        int icon;
                        if (SharedConfig.passcodeHash.length() != 0) {
                            value = getString(R.string.PasswordOn);
                            icon = R.drawable.msg2_secret;
                        } else {
                            value = getString(R.string.PasswordOff);
                            icon = R.drawable.msg2_secret;
                        }
                        textCell2.setTextAndValueAndIcon(getString(R.string.Passcode), value, true, icon, true);
                    } else if (position == blockedRow) {
                        int totalCount = getMessagesController().totalBlockedCount;
                        if (totalCount == 0) {
                            value = getString("BlockedEmpty", R.string.BlockedEmpty);
                        } else if (totalCount > 0) {
                            value = String.format(LocaleController.getInstance().getCurrentLocale(), "%d", totalCount);
                        } else {
                            showLoading = true;
                            value = "";
                        }
                        textCell2.setTextAndValueAndIcon(getString("BlockedUsers", R.string.BlockedUsers), value, true, R.drawable.msg2_block2, true);
                    }
                    textCell2.setDrawLoading(showLoading, loadingLen, animated);
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == passportRow || position == lastSeenRow || position == phoneNumberRow ||
                    position == deleteAccountRow || position == webSessionsRow || position == groupsRow || position == paymentsClearRow ||
                    position == secretMapRow || position == contactsDeleteRow || position == botsBiometryRow) {
                return 0;
            } else if (position == privacyShadowRow || position == deleteAccountDetailRow || position == groupsDetailRow || position == sessionsDetailRow || position == secretDetailRow || position == botsDetailRow || position == contactsDetailRow || position == newChatsSectionRow) {
                return 1;
            } else if (position == securitySectionRow || position == advancedSectionRow || position == privacySectionRow || position == secretSectionRow || position == botsSectionRow || position == contactsSectionRow || position == newChatsHeaderRow) {
                return 2;
            } else if (position == secretWebpageRow || position == contactsSyncRow || position == contactsSuggestRow || position == newChatsRow) {
                return 3;
            } else if (position == botsAndWebsitesShadowRow) {
                return 4;
            } else if (position == autoDeleteMesages || position == sessionsRow || position == emailLoginRow || position == passwordRow || position == passkeysRow || position == passcodeRow || position == blockedRow) {
                return 5;
            }
            return 0;
        }
    }

    private SpannableString premiumStar;
    private CharSequence addPremiumStar(String text) {
//        if (getUserConfig().isPremium()) {
//            return text;
//        }
        if (premiumStar == null) {
            premiumStar = new SpannableString("★");
            Drawable drawable = new AnimatedEmojiDrawable.WrapSizeDrawable(PremiumGradient.getInstance().premiumStarMenuDrawable, dp(18), dp(18));
            drawable.setBounds(0, 0, dp(18), dp(18));
            premiumStar.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_CENTER), 0, premiumStar.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return new SpannableStringBuilder(text).append("  ").append(premiumStar);
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

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
    }
}
