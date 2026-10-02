package com.exteragram.messenger.ai.ui.activities;

import android.view.View;
import androidx.core.util.Consumer;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Message;
import com.exteragram.messenger.ai.ui.MarkdownPreview;
import com.exteragram.messenger.ai.ui.components.HistoryMessageCell;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

/* JADX INFO: loaded from: classes4.dex */
public class AiHistoryActivity extends BasePreferencesActivity {
    private final ArrayList<Entry> entries = new ArrayList<>();
    private int lastEntryId = 100;

    public static class Entry {
        CharSequence content;
        boolean expanded;
        final int id;
        final Message message;

        public Entry(int i, Message message) {
            this.id = i;
            this.message = message;
            this.content = message.content() == null ? _UrlKt.FRAGMENT_ENCODE_SET : message.content();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        ArrayList<Message> conversationHistory = AiConfig.getConversationHistory();
        int size = conversationHistory.size();
        int i = 0;
        while (i < size) {
            Message message = conversationHistory.get(i);
            i++;
            ArrayList<Entry> arrayList = this.entries;
            int i2 = this.lastEntryId;
            this.lastEntryId = i2 + 1;
            arrayList.add(new Entry(i2, message));
        }
        formatContents();
        return super.onFragmentCreate();
    }

    private void formatContents() {
        if (this.entries.isEmpty()) {
            return;
        }
        final ArrayList arrayList = new ArrayList(this.entries);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.AiHistoryActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AiHistoryActivity.this.lambda$formatContents$1(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$formatContents$1(final ArrayList arrayList) {
        final ArrayList arrayList2 = new ArrayList(arrayList.size());
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            arrayList2.add(MarkdownPreview.format(((Entry) obj).message.content()));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.AiHistoryActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                AiHistoryActivity.this.lambda$formatContents$0(arrayList, arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$formatContents$0(ArrayList arrayList, ArrayList arrayList2) {
        if (getContext() == null) {
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            ((Entry) arrayList.get(i)).content = (CharSequence) arrayList2.get(i);
        }
        updateList();
    }

    private void updateList() {
        UniversalAdapter universalAdapter;
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.MessageHistory);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asRippleCheck(1, LocaleController.getString(R.string.MessageHistory)).setChecked(AiConfig.getSaveHistory()));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.HistoryInfo)));
        if (this.entries.isEmpty()) {
            return;
        }
        int i = 0;
        arrayList.add(UItem.asHeader(LocaleController.formatPluralString("messages", this.entries.size(), new Object[0])));
        ArrayList<Entry> arrayList2 = this.entries;
        int size = arrayList2.size();
        while (i < size) {
            Entry entry = arrayList2.get(i);
            i++;
            Entry entry2 = entry;
            arrayList.add(HistoryMessageCell.Factory.asHistoryCell(entry2.id, entry2.message, entry2.content, entry2.expanded));
        }
        arrayList.add(UItem.asShadow(null));
        arrayList.add(UItem.asButton(2, R.drawable.msg_delete, LocaleController.getString(R.string.ClearHistory)).red());
        arrayList.add(UItem.asShadow(null));
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        Entry entryFindEntry;
        int i2 = uItem.id;
        if (i2 == 1) {
            toggleBooleanSettingAndRefresh(uItem, bool -> AiConfig.setSaveHistory(bool.booleanValue()));
            return;
        }
        if (i2 == 2) {
            AiController.clearHistory(this, getResourceProvider(), true, new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.AiHistoryActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    AiHistoryActivity.this.lambda$onClick$2();
                }
            });
        } else {
            if (!(uItem.object instanceof Message) || (entryFindEntry = findEntry(i2)) == null) {
                return;
            }
            entryFindEntry.expanded = !entryFindEntry.expanded;
            updateList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$2() {
        this.entries.clear();
        updateList();
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public boolean onLongClick(final UItem uItem, View view, int i, float f, float f2) {
        if (uItem == null) {
            return false;
        }
        Object obj = uItem.object;
        if (!(obj instanceof Message)) {
            return false;
        }
        final Message message = (Message) obj;
        ItemOptions.makeOptions(this, view).add(R.drawable.msg_copy, LocaleController.getString(R.string.Copy), new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.AiHistoryActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AiHistoryActivity.this.lambda$onLongClick$3(message);
            }
        }).add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.Delete), true, new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.AiHistoryActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AiHistoryActivity.this.lambda$onLongClick$4(uItem);
            }
        }).setGravity(LocaleController.isRTL ? 3 : 5).setScrimViewBackground(this.listView.getClipBackground(view)).show();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$3(Message message) {
        if (AndroidUtilities.addToClipboard(message.content())) {
            BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$4(UItem uItem) {
        deleteTurn(uItem.id);
    }

    private Entry findEntry(int i) {
        ArrayList<Entry> arrayList = this.entries;
        int size = arrayList.size();
        int i2 = 0;
        while (i2 < size) {
            Entry entry = arrayList.get(i2);
            i2++;
            Entry entry2 = entry;
            if (entry2.id == i) {
                return entry2;
            }
        }
        return null;
    }

    private void deleteTurn(int i) {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i3 >= this.entries.size()) {
                i3 = -1;
                break;
            } else if (this.entries.get(i3).id == i) {
                break;
            } else {
                i3++;
            }
        }
        if (i3 == -1) {
            return;
        }
        int i4 = "assistant".equals(this.entries.get(i3).message.role()) ? i3 - 1 : i3;
        int i5 = i4 + 1;
        if (i4 < 0 || i5 >= this.entries.size() || "assistant".equals(this.entries.get(i4).message.role()) || !"assistant".equals(this.entries.get(i5).message.role())) {
            i4 = i3;
        } else {
            i3 = i5;
        }
        while (i3 >= i4) {
            this.entries.remove(i3);
            i3--;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList<Entry> arrayList2 = this.entries;
        int size = arrayList2.size();
        while (i2 < size) {
            Entry entry = arrayList2.get(i2);
            i2++;
            arrayList.add(entry.message);
        }
        AiConfig.saveConversationHistory(arrayList);
        updateList();
    }
}
