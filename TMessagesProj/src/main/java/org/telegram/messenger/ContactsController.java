return contactsMap != null ? contactsMap : new HashMap<>();
    }

    public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> original) {
        HashMap<String, Contact> ret = new HashMap<>();
        for (HashMap.Entry<String, Contact> entry : original.entrySet()) {
            Contact copyContact = new Contact();
            Contact originalContact = entry.getValue();
            copyContact.phoneDeleted.addAll(originalContact.phoneDeleted);
            copyContact.phones.addAll(originalContact.phones);
            copyContact.phoneTypes.addAll(originalContact.phoneTypes);
            copyContact.shortPhones.addAll(originalContact.shortPhones);
            copyContact.first_name = originalContact.first_name;
            copyContact.last_name = originalContact.last_name;
            copyContact.contact_id = originalContact.contact_id;
            copyContact.key = originalContact.key;
            ret.put(copyContact.key, copyContact);
        }
        return ret;
    }

    protected void migratePhoneBookToV7(final SparseArray<Contact> contactHashMap) {
        Utilities.globalQueue.postRunnable(() -> {
            if (migratingContacts) {
                return;
            }
            migratingContacts = true;
            HashMap<String, Contact> migratedMap = new HashMap<>();
            HashMap<String, Contact> contactsMap = readContactsFromPhoneBook();
            final HashMap<String, String> contactsBookShort = new HashMap<>();
            for (HashMap.Entry<String, Contact> entry : contactsMap.entrySet()) {
                Contact value = entry.getValue();
                for (int a = 0; a < value.shortPhones.size(); a++) {
                    contactsBookShort.put(value.shortPhones.get(a), value.key);
                }
            }
            for (int b = 0; b < contactHashMap.size(); b++) {
                Contact value = contactHashMap.valueAt(b);
                for (int a = 0; a < value.shortPhones.size(); a++) {
                    String sphone = value.shortPhones.get(a);
                    String key = contactsBookShort.get(sphone);
                    if (key != null) {
                        value.key = key;
                        migratedMap.put(key, value);
                        break;
                    }
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrated contacts " + migratedMap.size() + " of " + contactHashMap.size());
            }
            getMessagesStorage().putCachedPhoneBook(migratedMap, true, false);
        });
    }

    protected void performSyncPhoneBook(final HashMap<String, Contact> contactHashMap, final boolean request, final boolean first, final boolean schedule, final boolean force, final boolean checkCount, final boolean canceled) {
        if (!first && !contactsBookLoaded) {
            return;
        }
        Utilities.globalQueue.postRunnable(() -> {
            int newPhonebookContacts = 0;
            int serverContactsInPhonebook = 0;
            boolean disableDeletion = true; //disable contacts deletion, because phone numbers can't be compared due to different numbers format
            } else {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Groups.ACCOUNT_TYPE, systemAccount.type);
                values.put(ContactsContract.Groups.ACCOUNT_NAME, systemAccount.name);
                values.put(ContactsContract.Groups.GROUP_VISIBLE, 0);
                values.put(ContactsContract.Groups.GROUP_IS_READ_ONLY, 1);
                values.put(ContactsContract.Groups.TITLE, "TelegramConnectionService");
                Uri res = resolver.insert(groupsURI, values);
                groupID = Integer.parseInt(res.getLastPathSegment());
            }
            if (cursor != null)
                cursor.close();

            // 2. Find the existing ConnectionService contact and update it or create it
            cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                    ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=?",
                    new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE, groupID + ""}, null);
            int backRef = ops.size();
            if (cursor != null && cursor.moveToFirst()) {
                int contactID = cursor.getInt(0);
                ops.add(ContentProviderOperation.newUpdate(rawContactsURI)
                        .withSelection(ContactsContract.RawContacts._ID + "=?", new String[]{contactID + ""})
                        .withValue(ContactsContract.RawContacts.DELETED, 0)
                        .build());
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{contactID + "", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "+99084" + id)
                        .build());
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{contactID + "", ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
                        .build());
            } else {
                ops.add(ContentProviderOperation.newInsert(rawContactsURI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, systemAccount.type)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, systemAccount.name)
                        .withValue(ContactsContract.RawContacts.RAW_CONTACT_IS_READ_ONLY, 1)
                        .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED)
                        .build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
                        .build());
                // The prefix +990 isn't assigned to anything, so our "phone number" is going to be +990-TG-UserID
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "+99084" + id)
                        .build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupID)
                        .build());
            }
            if (cursor != null)
                cursor.close();

            resolver.applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (Exception x) {
            FileLog.e(x);
        }
    }

    public void deleteConnectionServiceContact() {
        if (!hasContactsPermission())
            return;
        try {
            ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();

            Cursor cursor = resolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{ContactsContract.Groups._ID},
                    ContactsContract.Groups.TITLE + "=? AND " + ContactsContract.Groups.ACCOUNT_TYPE + "=? AND " + ContactsContract.Groups.ACCOUNT_NAME + "=?",
                    new String[]{"TelegramConnectionService", systemAccount.type, systemAccount.name}, null);
            int groupID;
            if (cursor != null && cursor.moveToFirst()) {
                groupID = cursor.getInt(0);
                cursor.close();
            } else {
                if (cursor != null)
                    cursor.close();
                return;
            }
            cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                    ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=?",
                    new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE, groupID + ""}, null);
            int contactID;
            if (cursor != null && cursor.moveToFirst()) {
                contactID = cursor.getInt(0);
                cursor.close();
            } else {
                if (cursor != null)
                    cursor.close();
                return;
            }
            resolver.delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts._ID + "=?", new String[]{contactID + ""});
            //resolver.delete(ContactsContract.Groups.CONTENT_URI, ContactsContract.Groups._ID+"=?", new String[]{groupID+""});
        } catch (Exception x) {
            FileLog.e(x);
        }
    }

    public static String formatName(TLObject object) {
        if (object instanceof TLRPC.User) {
            return formatName((TLRPC.User) object);
        } else if (object instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) object;
            return chat.title;
        } else {
            return LocaleController.getString(R.string.HiddenName);
        }
    }

    @NonNull
    public static String formatName(TLRPC.User user) {
        if (user == null) {
            return "";
        }
        return formatName(user.first_name, user.last_name, 0);
    }

    @NonNull
    public static String formatName(String firstName, String lastName) {
        return formatName(firstName, lastName, 0);
    }

    @NonNull
    public static String formatName(String firstName, String lastName, int maxLength) {
        /*if ((firstName == null || firstName.length() == 0) && (lastName == null || lastName.length() == 0)) {
            return LocaleController.getString(R.string.HiddenName);
        }*/
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (firstName != null && lastName == null && maxLength > 0 && firstName.contains(" ") ) {
            int i = firstName.indexOf(" ");
            lastName = firstName.substring(i + 1);
            firstName = firstName.substring(0, i);
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        StringBuilder result = new StringBuilder((firstName != null ? firstName.length() : 0) + (lastName != null ? lastName.length() : 0) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (firstName != null && firstName.length() > 0) {
                if (maxLength > 0 && firstName.length() > maxLength + 2) {
                    return firstName.substring(0, maxLength) + "…";
                }
                result.append(firstName);
                if (lastName != null && lastName.length() > 0) {
                    result.append(" ");
                    if (maxLength > 0 && result.length() + lastName.length() > maxLength) {
                        result.append(lastName.charAt(0));
                    } else {
                        result.append(lastName);
                    }
                }
            } else if (lastName != null && lastName.length() > 0) {
                if (maxLength > 0 && lastName.length() > maxLength + 2) {
                    return lastName.substring(0, maxLength) + "…";
                }
                result.append(lastName);
            }
        } else {
            if (lastName != null && lastName.length() > 0) {
                if (maxLength > 0 && lastName.length() > maxLength + 2) {
                    return lastName.substring(0, maxLength) + "…";
                }
                result.append(lastName);
                if (firstName != null && firstName.length() > 0) {
                    result.append(" ");
                    if (maxLength > 0 && result.length() + firstName.length() > maxLength) {
                        result.append(firstName.charAt(0));
                    } else {
                        result.append(firstName);
                    }
                }
            } else if (firstName != null && firstName.length() > 0) {
                if (maxLength > 0 && firstName.length() > maxLength + 2) {
                    return firstName.substring(0, maxLength) + "…";
                }
                result.append(firstName);
            }
        }
        return result.toString();
    }

    private class PhoneBookContact {
        String id;
        String lookup_key;
        String name;
        String phone;
    }


    public static <T extends TLRPC.PrivacyRule> T findRule(ArrayList<TLRPC.PrivacyRule> rules, Class<T> clazz) {
        if (rules == null) {
            return null;
        }
        for (TLRPC.PrivacyRule rule : rules) {
            if (clazz.isInstance(rule)) {
                return clazz.cast(rule);
            }
        }
        return null;
    }
}
