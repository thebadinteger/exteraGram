package com.exteragram.messenger.components;

import android.content.Context;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.gms.cast.MediaTrack;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\r\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ \u0010\u000f\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0018\u0010\u0011\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u0013H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/exteragram/messenger/components/BlendedReplyFileText;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "fileIcon", "Lcom/exteragram/messenger/components/BlendedReplyColorIconSpan;", "musicIcon", "build", _UrlKt.FRAGMENT_ENCODE_SET, "context", "Landroid/content/Context;", "reply", "Lorg/telegram/messenger/MessageObject;", "fontMetrics", "Landroid/graphics/Paint$FontMetricsInt;", "formatCaption", MediaTrack.ROLE_CAPTION, "createIcon", "res", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nBlendedReplySpans.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BlendedReplySpans.kt\ncom/exteragram/messenger/components/BlendedReplyFileText\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,116:1\n1#2:117\n*E\n"})
public final class BlendedReplyFileText {
    private BlendedReplyColorIconSpan fileIcon;
    private BlendedReplyColorIconSpan musicIcon;

    public final CharSequence build(Context context, MessageObject reply, Paint.FontMetricsInt fontMetrics) {
        BlendedReplyColorIconSpan blendedReplyColorIconSpanCreateIcon;
        String documentName;
        boolean z = reply.type == 14;
        if (z) {
            blendedReplyColorIconSpanCreateIcon = this.musicIcon;
            if (blendedReplyColorIconSpanCreateIcon == null) {
                blendedReplyColorIconSpanCreateIcon = createIcon(context, R.drawable.filled_widget_music);
                this.musicIcon = blendedReplyColorIconSpanCreateIcon;
            }
        } else {
            blendedReplyColorIconSpanCreateIcon = this.fileIcon;
            if (blendedReplyColorIconSpanCreateIcon == null) {
                blendedReplyColorIconSpanCreateIcon = createIcon(context, R.drawable.msg_round_file_s);
                this.fileIcon = blendedReplyColorIconSpanCreateIcon;
            }
        }
        if (z) {
            documentName = reply.getMusicAuthor() + " – " + reply.getMusicTitle();
        } else {
            documentName = reply.getDocumentName();
            if (documentName == null || documentName.length() == 0) {
                documentName = LocaleController.getString(R.string.AttachDocument);
            }
        }
        SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder("d ").append(Emoji.replaceEmoji(documentName, fontMetrics, false));
        spannableStringBuilderAppend.setSpan(blendedReplyColorIconSpanCreateIcon, 0, 1, 33);
        spannableStringBuilderAppend.setSpan(new BlendedReplyColorSpan(), 0, spannableStringBuilderAppend.length(), 33);
        CharSequence charSequence = reply.caption;
        if (charSequence != null && charSequence.length() != 0) {
            spannableStringBuilderAppend.append((CharSequence) ", ").append(formatCaption(reply, charSequence, fontMetrics));
        }
        return spannableStringBuilderAppend;
    }

    private final CharSequence formatCaption(MessageObject reply, CharSequence caption, Paint.FontMetricsInt fontMetrics) {
        ArrayList<TLRPC.MessageEntity> arrayList;
        String str = caption.toString();
        if (str.length() > 150) {
            str = str.substring(0, 150);
        }
        CharSequence charSequenceReplaceEmoji = Emoji.replaceEmoji(str.replace('\n', ' '), fontMetrics, true);
        TLRPC.Message message = reply.messageOwner;
        if (message == null || (arrayList = message.entities) == null) {
            return charSequenceReplaceEmoji;
        }
        ArrayList arrayList2 = new ArrayList(arrayList);
        LocaleUtils.parseCustomEmojis(charSequenceReplaceEmoji, arrayList2);
        Spannable spannableReplaceAnimatedEmoji = MessageObject.replaceAnimatedEmoji(charSequenceReplaceEmoji, arrayList2, fontMetrics, true);
        MediaDataController.addTextStyleRuns(arrayList, caption, spannableReplaceAnimatedEmoji);
        return spannableReplaceAnimatedEmoji;
    }

    private final BlendedReplyColorIconSpan createIcon(Context context, int res) {
        BlendedReplyColorIconSpan blendedReplyColorIconSpan = new BlendedReplyColorIconSpan(ContextCompat.getDrawable(context, res).mutate());
        blendedReplyColorIconSpan.setScale(0.7f, 0.7f);
        return blendedReplyColorIconSpan;
    }
}
