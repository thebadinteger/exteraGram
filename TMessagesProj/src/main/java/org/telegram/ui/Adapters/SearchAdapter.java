}
                }
                break;
            }
            case 1: {
                GraySectionCell cell = (GraySectionCell) holder.itemView;
                if (position ==  unregistredContactsHeaderRow) {
                    cell.setText(LocaleController.getString(R.string.InviteToTelegramShort));
                } else if (getItem(position) == null) {
                    cell.setText(LocaleController.getString(R.string.GlobalSearch));
                } else {
                    cell.setText(LocaleController.getString(R.string.PhoneNumberSearch));
                }
                break;
            }
            case 2: {
                String str = (String) getItem(position);
                TextCell cell = (TextCell) holder.itemView;
                cell.setColors(-1, Theme.key_windowBackgroundWhiteBlueText2);
                cell.setText(LocaleController.formatString(R.string.AddContactByPhone, PhoneFormat.getInstance().format("+" + str)), false);
                break;
            }
            case 3: {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) holder.itemView;
                ContactsController.Contact contact = (ContactsController.Contact) getItem(position);
                profileSearchCell.setData(contact, null, ContactsController.formatName(contact.first_name, contact.last_name), PhoneFormat.getInstance().format("+" + contact.shortPhones.get(0)), false, false);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (includeSearch) {
            if (i == 0) return 4;
            i--;
        }
        if (includeLoading && searchInProgress() && i >= (getItemCount() - (includeSearch ? 1 : 0)) - 3) {
            return 5;
        }
        Object item = getItem(i);
        if (item == null) {
            return 1;
        } else if (item instanceof String) {
            String str = (String) item;
            if ("section".equals(str)) {
                return 1;
            } else {
                return 2;
            }
        } else if (item instanceof ContactsController.Contact) {
            return 3;
        }
        return 0;
    }

    private static class ContactEntry {
        String q1;
        String q2;
        ContactsController.Contact contact;
    }
}
