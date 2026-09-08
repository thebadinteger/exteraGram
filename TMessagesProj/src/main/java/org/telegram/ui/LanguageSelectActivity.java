);
                    break;
                }
                case VIEW_TYPE_SETTINGS_2: {
                    TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                    settingsCell.updateRTL();
//                    if (translationModels != null) {
//                        ArrayList<String> languages = new ArrayList<>();
//                        for (int i = 0; i < translationModels.size(); ++i) {
//                            languages.add(TranslateAlert2.languageName(translationModels.get(i)));
//                        }
//                        settingsCell.setTextAndValue("Delete Translation Models", languages.size() >= 3 ? languages.size() + " models" : TextUtils.join(", ", languages), false);
//                    }
                    break;
                }
                case VIEW_TYPE_SWITCH: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    cell.updateRTL();
                    if (position == manualTranslationPosition) {
                        cell.setTextAndCheck(LocaleController.getString(R.string.ShowTranslateButton), getContextValue(), true);
                        cell.setCheckBoxIcon(0);
                    } else if (position == autoTranslationPosition) {
                        cell.setTextAndCheck(LocaleController.getString(R.string.ShowTranslateChatButton), getChatValue(), getContextValue() || getChatValue());
                        cell.setCheckBoxIcon(!getUserConfig().isPremium() ? R.drawable.permission_locked : 0);
                    }
                    break;
                }
                case VIEW_TYPE_INFO: {
                    TextInfoPrivacyCell infoCell = (TextInfoPrivacyCell) holder.itemView;
                    infoCell.updateRTL();
                    if (position == infoPosition1) {
                        infoCell.setText(LocaleController.getString(R.string.TranslateMessagesInfo1));
                        infoCell.setTopPadding(11);
                        infoCell.setBottomPadding(16);
                    } else {
                        infoCell.setTopPadding(0);
                        infoCell.setBottomPadding(16);
                    }
                    break;
                }
                case VIEW_TYPE_HEADER: {
                    HeaderCell header = (HeaderCell) holder.itemView;
                    header.setText(position == 0 && (getMessagesController().isTranslationsManualEnabled() || getMessagesController().isTranslationsAutoEnabled()) ? LocaleController.getString(R.string.TranslateMessages) : LocaleController.getString(R.string.Language));
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            final int position = i;
            if (search) {
                return VIEW_TYPE_LANGUAGE;
            } else {
                if (getMessagesController().isTranslationsManualEnabled() || getMessagesController().isTranslationsAutoEnabled()) {
                    settingsFromPosition = position - i;
                    if (i-- == 0) return VIEW_TYPE_HEADER;
                    if (getMessagesController().isTranslationsManualEnabled()) {
                        if (i-- == 0) {
                            manualTranslationPosition = position;
                            return VIEW_TYPE_SWITCH;
                        }
                    } else {
                        manualTranslationPosition = -1;
                    }
                    if (getMessagesController().isTranslationsAutoEnabled() && !getMessagesController().premiumFeaturesBlocked()) {
                        if (i-- == 0) {
                            autoTranslationPosition = position;
                            return VIEW_TYPE_SWITCH;
                        }
                    } else {
                        autoTranslationPosition = -1;
                    }
                    if (getChatValue() || getContextValue()) {
                        doNotTranslatePosition = position;
                        if (i-- == 0) return VIEW_TYPE_SETTINGS;
                    }
//                    if (translationModels != null) {
//                        if (i-- == 0) return VIEW_TYPE_SETTINGS_2;
//                    }
                    settingsToPosition = position - i - 1;
                    if (i-- == 0) {
                        infoPosition1 = position;
                        return VIEW_TYPE_INFO;
                    }
                    //if ("system".equals(getMessagesController().translationsManualEnabled) && "system".equals(getMessagesController().translationsAutoEnabled)) {
                    //    infoPosition2 = -1;
                    //} else {
                    //    if (i-- == 0) {
                    //        infoPosition2 = position;
                    //        return VIEW_TYPE_INFO;
                    //    }
                    //}
                } else {
                    settingsFromPosition = -1;
                    settingsToPosition = -1;
                }
                if (i-- == 0) return VIEW_TYPE_HEADER;
                if (!unofficialLanguages.isEmpty() && (i == unofficialLanguages.size() || i == unofficialLanguages.size() + sortedLanguages.size() + 1) || unofficialLanguages.isEmpty() && i == sortedLanguages.size()) {
                    return VIEW_TYPE_SHADOW;
                }
                languagesStartsPosition = position - i;
                return VIEW_TYPE_LANGUAGE;
            }
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon));

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
