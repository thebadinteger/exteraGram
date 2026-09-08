package com.exteragram.messenger.plugins.ui.components;

import android.app.Activity;
import android.content.DialogInterface;
import com.exteragram.messenger.utils.MarkdownUtils;
import java.io.File;
import kotlin.Metadata;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;

@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000eJ\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u001a\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginFileViewer;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "MAX_FILE_SIZE", _UrlKt.FRAGMENT_ENCODE_SET, "MAX_BLOCK_LENGTH", "open", _UrlKt.FRAGMENT_ENCODE_SET, "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "file", "Ljava/io/File;", "fileName", _UrlKt.FRAGMENT_ENCODE_SET, "createMessageObject", "Lorg/telegram/messenger/MessageObject;", "normalizeFileName", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPluginFileViewer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginFileViewer.kt\ncom/exteragram/messenger/plugins/ui/components/PluginFileViewer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,127:1\n1#2:128\n*E\n"})
public final class PluginFileViewer {
    public static final PluginFileViewer INSTANCE = new PluginFileViewer();
    private static final int MAX_BLOCK_LENGTH = 8192;
    private static final int MAX_FILE_SIZE = 524288;

    private PluginFileViewer() {
    }

    public static private final String normalizeFileName(File file, String fileName) {
        if (fileName == null) {
            fileName = file.getName();
        } else {
            if (StringsKt.isBlank(fileName)) {
                fileName = null;
            }
            if (fileName == null) {
                fileName = file.getName();
            }
        }
        if (StringsKt.endsWith(fileName, ".plugin", true)) {
            return fileName;
        }
        return fileName + ".plugin";
    }
}
