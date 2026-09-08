package com.exteragram.messenger.export.ui;

import android.view.View;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.export.output.FileManager;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;

public class DialogsView extends BasePreferencesActivity {
    private ArrayList<ParsedDialogObject> dialogObjects;
    private final String mainPath;

    public enum DialogsItem {
        GENERAL_HEADER
    }

    public DialogsView(String str) {
        this.mainPath = str;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        loadDialogs();
        return true;
    }

    @Override 
    public String getTitle() {
        return "Dialogs";
    }

    private void loadDialogs() {
        String fileContent = FileManager.readFileContent(new File(this.mainPath, "result.json"));
        if (fileContent == null) {
            BulletinFactory.of(this).createErrorBulletin("Failed to read result.json!").show();
            this.dialogObjects = new ArrayList<>();
            return;
        }
        try {
            ParsedUserInfo parsedUserInfo = (ParsedUserInfo) ExteraConfig.getGSON().fromJson(fileContent, ParsedUserInfo.class);
            if (parsedUserInfo != null && parsedUserInfo.chats != null) {
                this.dialogObjects = new ArrayList<>(Arrays.asList(parsedUserInfo.chats));
            } else {
                this.dialogObjects = new ArrayList<>();
            }
        } catch (Throwable th) {
            FileLog.e("Export read from file failed!", th);
            BulletinFactory.of(this).createErrorBulletin("Failed to parse result.json!").show();
            this.dialogObjects = new ArrayList<>();
        }
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asHeader("Dialogs"));
        ArrayList<ParsedDialogObject> arrayList2 = this.dialogObjects;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            arrayList.add(UItem.asShadow("No dialogs found in the export file."));
            return;
        }
        for (int i = 0; i < this.dialogObjects.size(); i++) {
            boolean z = true;
            UItem uItemAsButton = UItem.asButton(DialogsItem.values().length + 1 + i, this.dialogObjects.get(i).name);
            if (i >= this.dialogObjects.size() - 1) {
                z = false;
            }
            arrayList.add(uItemAsButton.showDivider(z));
        }
    }

    @Override 
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int length = DialogsItem.values().length + 1;
        int i2 = uItem.id;
        if (i2 >= length) {
            int i3 = i2 - length;
            ArrayList<ParsedDialogObject> arrayList = this.dialogObjects;
            if (arrayList == null || i3 < 0 || i3 >= arrayList.size()) {
                return;
            }
            presentFragment(new ChatViewer(this.mainPath + "/" + this.dialogObjects.get(i3).path));
        }
    }

    public static class ParsedDialogObject {

        @SerializedName("id")
        public long id;

        @SerializedName("left")
        public boolean left;

        @SerializedName("name")
        public String name;

        @SerializedName("relativePath")
        public String path;

        private ParsedDialogObject() {
        }
    }

    public static class ParsedUserInfo {

        @SerializedName("about")
        public String about;

        @SerializedName("chats")
        public ParsedDialogObject[] chats;

        private ParsedUserInfo() {
        }
    }
}
