package com.exteragram.messenger.export.output.html;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.exteragram.messenger.export.api.ApiWrap$ActionChatEditPhoto;
import com.exteragram.messenger.export.api.ApiWrap$ActionSuggestProfilePhoto;
import com.exteragram.messenger.export.api.ApiWrap$Chat;
import com.exteragram.messenger.export.api.ApiWrap$DialogInfo;
import com.exteragram.messenger.export.api.ApiWrap$Document;
import com.exteragram.messenger.export.api.ApiWrap$File;
import com.exteragram.messenger.export.api.ApiWrap$Game;
import com.exteragram.messenger.export.api.ApiWrap$GeoPoint;
import com.exteragram.messenger.export.api.ApiWrap$GiveawayResults;
import com.exteragram.messenger.export.api.ApiWrap$GiveawayStart;
import com.exteragram.messenger.export.api.ApiWrap$HistoryMessageMarkupButton;
import com.exteragram.messenger.export.api.ApiWrap$Invoice;
import com.exteragram.messenger.export.api.ApiWrap$Media;
import com.exteragram.messenger.export.api.ApiWrap$MediaData;
import com.exteragram.messenger.export.api.ApiWrap$Message;
import com.exteragram.messenger.export.api.ApiWrap$PaidMedia;
import com.exteragram.messenger.export.api.ApiWrap$Peer;
import com.exteragram.messenger.export.api.ApiWrap$Poll;
import com.exteragram.messenger.export.api.ApiWrap$Reaction;
import com.exteragram.messenger.export.api.ApiWrap$SharedContact;
import com.exteragram.messenger.export.api.ApiWrap$StoryData;
import com.exteragram.messenger.export.api.ApiWrap$Tag;
import com.exteragram.messenger.export.api.ApiWrap$TextPart;
import com.exteragram.messenger.export.api.ApiWrap$UnsupportedMedia;
import com.exteragram.messenger.export.api.ApiWrap$User;
import com.exteragram.messenger.export.api.ApiWrap$Venue;
import com.exteragram.messenger.export.api.DataTypesUtils;
import com.exteragram.messenger.export.output.AbstractWriter;
import com.exteragram.messenger.export.output.FileManager;
import com.exteragram.messenger.export.output.OutputFile;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.google.android.gms.cast.MediaTrack;
import com.google.zxing.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;

public class HtmlContext {
    private final String _base;
    private final String _composedStart;
    private final OutputFile _file;
    private final ArrayList<ApiWrap$Tag> _tags = new ArrayList<>();
    private boolean _closed = false;

    public static String repeat(String str, int count) {
        if (str == null || count <= 0) return "";
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public HtmlContext(String str, String str2, OutputFile.Stats stats) {
        this._file = new OutputFile(str, stats);
        String strSubstring = str.substring(str2.length() + 1);
        this._base = repeat("../", strSubstring.length() - strSubstring.replace("/", "").length());
        this._composedStart = composeStart();
    }

    public static String pathWithRelativePath(String str) {
        return FileManager.defaultSavePath + "/" + str;
    }

    public static String SerializeString(String str) {
        int i;
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        ArrayList arrayList = new ArrayList();
        for (char c2 : str.toCharArray()) {
            arrayList.add(Character.valueOf(c2));
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            char cCharValue = ((Character) arrayList.get(i2)).charValue();
            if (cCharValue == '\n') {
                sb.append("<br>");
            } else if (cCharValue == '\"') {
                sb.append("&quot;");
            } else if (cCharValue == '&') {
                sb.append("&amp;");
            } else if (cCharValue == '\'') {
                sb.append("&apos;");
            } else if (cCharValue == '<') {
                sb.append("&lt;");
            } else if (cCharValue == '>') {
                sb.append("&gt;");
            } else if (cCharValue >= 0 && cCharValue < ' ') {
                sb.append("&#x");
                sb.append((cCharValue >> 4) + 48);
                int i3 = cCharValue & 15;
                if (i3 >= 10) {
                    sb.append(i3 + 55);
                } else {
                    sb.append(i3 + 48);
                }
                sb.append(';');
            } else if (cCharValue == 226 && (i = i2 + 2) < length && ((Character) arrayList.get(i2 + 1)).charValue() == 128) {
                if (((Character) arrayList.get(i)).charValue() == 168) {
                    sb.append("<br>");
                } else if (((Character) arrayList.get(i)).charValue() == 169) {
                    sb.append("<br>");
                } else {
                    sb.append(cCharValue);
                }
            } else {
                sb.append(cCharValue);
            }
        }
        return sb.toString();
    }

    public static Pair<String, Dimension> WriteImageThumb(String str, String str2, Function<Dimension, Dimension> function, String str3, int i, String str4) {
        int i2;
        int i3;
        String strConcat;
        if (str4 == null) {
            str4 = "_thumb";
        }
        if (str2.isEmpty()) {
            return new Pair<>(null, null);
        }
        String str5 = str + "/" + str2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str5, options);
        if (new File(str5).length() == 0 || (i2 = options.outWidth) >= 10000 || (i3 = options.outHeight) >= 10000) {
            Log.e("exteraGram", "width or height are more than 10000, path: ".concat(str5));
            return new Pair<>(null, null);
        }
        Dimension dimensionApply = function.apply(new Dimension(i2, i3));
        if (dimensionApply == null) {
            return new Pair<>(null, null);
        }
        Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmapDecodeFile, dimensionApply.getWidth(), dimensionApply.getHeight(), true);
        if (i == -1) {
            i = 100;
        }
        int iIndexOf = str2.indexOf(46, str2.lastIndexOf(47) + 1);
        if (iIndexOf >= 0) {
            strConcat = str2.substring(0, iIndexOf) + str4 + str2.substring(iIndexOf);
        } else {
            strConcat = str2.concat(str4);
        }
        String strPrepareRelativePath = OutputFile.PrepareRelativePath(str, strConcat);
        try {
            File file = new File(str + "/" + strPrepareRelativePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmapCreateScaledBitmap.compress("PNG".equals(str3) ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, i, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            bitmapDecodeFile.recycle();
            bitmapCreateScaledBitmap.recycle();
            return new Pair<>(strPrepareRelativePath, dimensionApply);
        } catch (Exception e) {
            FileLog.e(e);
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static Function<Dimension, Dimension> CalculateThumbSize(final int i, final int i2, final int i3, final int i4, final boolean z) {
        return new Function() { // from class: com.exteragram.messenger.export.output.html.HtmlContext$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return HtmlContext.$r8$lambda$IDS2iDiaoey4r44KFrBNEkJs1Sk(z, i, i2, i3, i4, (Dimension) obj);
            }
        };
    }

    public static /* synthetic */ Dimension $r8$lambda$IDS2iDiaoey4r44KFrBNEkJs1Sk(boolean z, int i, int i2, int i3, int i4, Dimension dimension) {
        Dimension dimension2;
        int i5 = z ? 2 : 1;
        int width = dimension.getWidth() * i5;
        int height = dimension.getHeight() * i5;
        if (width > i || height > i2) {
            double d = width;
            double d2 = height;
            double dMin = Math.min(((double) i) / d, ((double) i2) / d2);
            dimension2 = new Dimension((int) (d * dMin), (int) (d2 * dMin));
        } else {
            dimension2 = new Dimension(dimension.getWidth(), dimension.getHeight());
        }
        int width2 = dimension2.getWidth() % 2 == 0 ? dimension2.getWidth() : dimension2.getWidth() - 1;
        int height2 = dimension2.getHeight() % 2;
        int height3 = dimension2.getHeight();
        if (height2 != 0) {
            height3--;
        }
        Dimension dimension3 = new Dimension(width2, height3);
        return (dimension3.getWidth() < i3 || dimension3.getHeight() < i4) ? new Dimension(0, 0) : dimension3;
    }

    private static String countryToEmoji(String str) {
        return new String(Character.toChars(Character.codePointAt(str, 0) - (-127397))).concat(new String(Character.toChars(Character.codePointAt(str, 1) - (-127397))));
    }

    public String relativePath(String str) {
        return this._base + str;
    }

    public String pushDiv(String str) {
        return pushDiv(str, _UrlKt.FRAGMENT_ENCODE_SET);
    }

    public String pushDiv(String str, String str2) {
        if (str2.isEmpty()) {
            return pushTag("div", new Pair("class", str));
        }
        return pushTag("div", new Pair("class", str), new Pair("style", str2));
    }

    public String composeStart() {
        StringBuilder sb = new StringBuilder("<!DOCTYPE html>" + pushTag("html", new Pair[0]));
        sb.append(pushTag("head", new Pair[0]));
        sb.append(pushTag("meta", new Pair("charset", "utf-8"), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)));
        sb.append(pushTag("title", new Pair("inline", _UrlKt.FRAGMENT_ENCODE_SET)));
        sb.append("Exported Data");
        sb.append(popTag());
        sb.append(pushTag("meta", new Pair("name", "viewport"), new Pair("content", "width=device-width, initial-scale=1.0"), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)));
        sb.append(pushTag("link", new Pair("href", relativePath("css/style.css")), new Pair("rel", "stylesheet"), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)));
        sb.append(pushTag("script", new Pair("src", relativePath("js/script.js")), new Pair(TeXSymbolParser.TYPE_ATTR, "text/javascript")));
        sb.append(popTag());
        sb.append(popTag());
        sb.append(pushTag("body", new Pair("onload", "CheckLocation();")));
        sb.append(pushDiv("page_wrap", _UrlKt.FRAGMENT_ENCODE_SET));
        return String.valueOf(sb);
    }

    public String pushTag(String str, Pair<String, String>... pairArr) {
        ApiWrap$Tag apiWrap$Tag = new ApiWrap$Tag();
        apiWrap$Tag.name = str;
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (Pair<String, String> pair : pairArr) {
            String str2 = (String) pair.first;
            String str3 = (String) pair.second;
            if (str2.equals("inline")) {
                apiWrap$Tag.block = false;
            } else if (str2.equals("empty")) {
                z = true;
            } else {
                sb.append(' ');
                sb.append(str2);
                sb.append("=\"");
                sb.append(SerializeString(str3));
                sb.append("\"");
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(apiWrap$Tag.block ? "\n" + indent() : _UrlKt.FRAGMENT_ENCODE_SET);
        sb2.append("<");
        sb2.append(apiWrap$Tag.name);
        sb2.append((Object) sb);
        sb2.append(z ? "/" : _UrlKt.FRAGMENT_ENCODE_SET);
        sb2.append(">");
        sb2.append(apiWrap$Tag.block ? "\n" : _UrlKt.FRAGMENT_ENCODE_SET);
        String string = sb2.toString();
        if (!z) {
            this._tags.add(apiWrap$Tag);
        }
        return string;
    }

    public String pushListEntry(HtmlWriter.UserpicData userpicData, String str, String str2, String str3, String str4) {
        return pushGenericListEntry(str4, userpicData, str, null, Collections.singletonList(str2), str3);
    }

    public String pushSessionListEntry(int i, String str, String str2, ArrayList<String> arrayList, String str3) {
        HtmlWriter.UserpicData userpicData = new HtmlWriter.UserpicData();
        userpicData.colorIndex = DataTypesUtils.ApplicationColorIndex(i);
        userpicData.pixelSize = 48;
        userpicData.firstName = str;
        return pushGenericListEntry(_UrlKt.FRAGMENT_ENCODE_SET, userpicData, str, str2, arrayList, str3);
    }

    public String pushStoriesListEntry(ApiWrap$StoryData apiWrap$StoryData, String str, ArrayList<String> arrayList, String str2, ArrayList<ApiWrap$TextPart> arrayList2, String str3, String str4) {
        StringBuilder sb = new StringBuilder(pushDiv("entry clearfix"));
        if (!str4.isEmpty()) {
            sb.append(pushTag("a", new Pair("class", "pull_left userpic_wrap"), new Pair("href", relativePath(str4) + "#allow_back")));
        } else {
            sb.append(pushDiv("pull_left userpic_wrap"));
        }
        boolean zIsEmpty = apiWrap$StoryData.imageLink.isEmpty();
        String strFormatText = _UrlKt.FRAGMENT_ENCODE_SET;
        if (!zIsEmpty) {
            sb.append(pushTag("img", new Pair("class", "story"), new Pair("style", "width: 45px; height: 80px"), new Pair("src", relativePath(apiWrap$StoryData.imageLink)), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)));
        }
        sb.append(popTag());
        sb.append(pushDiv("body"));
        if (!str2.isEmpty()) {
            sb.append(pushDiv("pull_right info details"));
            sb.append(SerializeString(str2));
            sb.append(popTag());
        }
        if (!str.isEmpty()) {
            if (!str4.isEmpty()) {
                sb.append(pushTag("a", new Pair("class", "block_link expanded"), new Pair("href", relativePath(str4) + "#allow_back")));
            }
            sb.append(pushDiv("name bold"));
            sb.append(SerializeString(str));
            sb.append(popTag());
            if (!str4.isEmpty()) {
                sb.append(popTag());
            }
        }
        if (!arrayList2.isEmpty()) {
            strFormatText = DataTypesUtils.FormatText(arrayList2, str3, this._base);
        }
        if (!strFormatText.isEmpty()) {
            sb.append(pushDiv("text"));
            sb.append(strFormatText);
            sb.append(popTag());
        }
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            String str5 = arrayList.get(i);
            i++;
            sb.append(pushDiv("details_entry details"));
            sb.append(SerializeString(str5));
            sb.append(popTag());
        }
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }

    public String pushGenericListEntry(String str, HtmlWriter.UserpicData userpicData, String str2, String str3, List<String> list, String str4) {
        String strPushTag;
        if (str.isEmpty()) {
            strPushTag = pushDiv("entry clearfix");
        } else {
            strPushTag = pushTag("a", new Pair("class", "entry block_link clearfix"), new Pair("href", relativePath(str) + "#allow_back"));
        }
        StringBuilder sb = new StringBuilder(strPushTag);
        sb.append(pushDiv("pull_left userpic_wrap"));
        sb.append(pushUserpic(userpicData));
        sb.append(popTag());
        sb.append(pushDiv("body"));
        if (!str4.isEmpty()) {
            sb.append(pushDiv("pull_right info details"));
            sb.append(SerializeString(str4));
            sb.append(popTag());
        }
        if (!str2.isEmpty()) {
            sb.append(pushDiv("name bold"));
            sb.append(SerializeString(str2));
            sb.append(popTag());
        }
        if (str3 != null && !str3.isEmpty()) {
            sb.append(pushDiv("subname bold"));
            sb.append(SerializeString(str3));
            sb.append(popTag());
        }
        for (String str5 : list) {
            sb.append(pushDiv("details_entry details"));
            sb.append(SerializeString(str5));
            sb.append(popTag());
        }
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }

    public String indent() {
        return repeat(" ", this._tags.size());
    }

    public String popTag() {
        String str;
        ArrayList<ApiWrap$Tag> arrayList = this._tags;
        ApiWrap$Tag apiWrap$Tag = arrayList.get(arrayList.size() - 1);
        ArrayList<ApiWrap$Tag> arrayList2 = this._tags;
        arrayList2.remove(arrayList2.size() - 1);
        StringBuilder sb = new StringBuilder();
        boolean z = apiWrap$Tag.block;
        String str2 = _UrlKt.FRAGMENT_ENCODE_SET;
        if (z) {
            str = "\n" + indent();
        } else {
            str = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        sb.append(str);
        sb.append("</");
        sb.append(apiWrap$Tag.name);
        sb.append(">");
        if (apiWrap$Tag.block) {
            str2 = "\n";
        }
        sb.append(str2);
        return sb.toString();
    }

    public boolean isTagsEmpty() {
        return this._tags.isEmpty();
    }

    public String pushUserpic(HtmlWriter.UserpicData userpicData) {
        String str = userpicData.pixelSize + "px";
        StringBuilder sb = new StringBuilder();
        if (!userpicData.largeLink.isEmpty()) {
            sb.append(pushTag("a", new Pair("class", "userpic_link"), new Pair("href", pathWithRelativePath(userpicData.largeLink))));
        }
        String str2 = "width: " + str + "; height: " + str;
        String str3 = userpicData.imageLink;
        String strSerializeString = _UrlKt.FRAGMENT_ENCODE_SET;
        if (str3 != null && !str3.isEmpty()) {
            sb.append(pushTag("img", new Pair("class", "userpic"), new Pair("style", str2), new Pair("src", pathWithRelativePath(userpicData.imageLink)), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)));
        } else {
            sb.append(pushTag("div", new Pair("class", "userpic userpic" + (userpicData.colorIndex + 1)), new Pair("style", str2)));
            if (userpicData.tooltip.isEmpty()) {
                sb.append(pushDiv("initials", "line-height: ".concat(str)));
            } else {
                sb.append(pushTag("div", new Pair("class", "initials"), new Pair("style", "line-height: ".concat(str)), new Pair("title", userpicData.tooltip)));
            }
            String str4 = userpicData.firstName;
            sb.append(str4.trim().isEmpty() ? _UrlKt.FRAGMENT_ENCODE_SET : SerializeString(str4.trim().substring(0, 1)));
            String str5 = userpicData.lastName;
            if (!str5.trim().isEmpty()) {
                strSerializeString = SerializeString(str5.trim().substring(0, 1));
            }
            sb.append(strSerializeString);
            sb.append(popTag());
            sb.append(popTag());
        }
        if (!userpicData.largeLink.isEmpty()) {
            sb.append(popTag());
        }
        return sb.toString();
    }

    public AbstractWriter.Result writeBlock(ArrayList<String> arrayList) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            String str = arrayList.get(i);
            i++;
            String str2 = str;
            if (!writeBlock(str2).isSuccess()) {
                throw new IllegalStateException("writeBlock : " + str2);
            }
        }
        return AbstractWriter.Result.Success();
    }

    public AbstractWriter.Result writeBlock(String str) {
        AbstractWriter.Result resultWriteBlock;
        if (this._closed) {
            throw new IllegalStateException("file is closed!");
        }
        boolean zIsEmpty = str.isEmpty();
        OutputFile outputFile = this._file;
        if (zIsEmpty) {
            resultWriteBlock = outputFile.writeBlock(str);
        } else {
            boolean zEmpty = outputFile.empty();
            OutputFile outputFile2 = this._file;
            if (zEmpty) {
                resultWriteBlock = outputFile2.writeBlock(this._composedStart + str);
            } else {
                resultWriteBlock = outputFile2.writeBlock(str);
            }
        }
        if (!resultWriteBlock.isSuccess()) {
            this._closed = true;
        }
        return resultWriteBlock;
    }

    public AbstractWriter.Result close() {
        if (!this._closed && !this._file.empty()) {
            this._closed = true;
            StringBuilder sb = new StringBuilder();
            while (!isTagsEmpty()) {
                sb.append(popTag());
            }
            return this._file.writeBlock(sb.toString());
        }
        return AbstractWriter.Result.Success();
    }

    public String pushHeader(String str, String str2) {
        String strPushTag;
        StringBuilder sb = new StringBuilder(pushDiv("page_header", _UrlKt.FRAGMENT_ENCODE_SET));
        if (str2.isEmpty()) {
            strPushTag = pushDiv("content", _UrlKt.FRAGMENT_ENCODE_SET);
        } else {
            strPushTag = pushTag("a", new Pair("class", "content block_link"), new Pair("href", relativePath(str2)), new Pair("onclick", "return GoBack(this)"));
        }
        sb.append(strPushTag);
        sb.append(pushDiv("text bold", _UrlKt.FRAGMENT_ENCODE_SET));
        sb.append(SerializeString(str));
        sb.append(popTag());
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }

    public String pushSection(String str, String str2, int i, String str3) {
        StringBuilder sb = new StringBuilder();
        sb.append(pushTag("a", new Pair("class", "section block_link " + str2), new Pair("href", str3 + "#allow_back")));
        sb.append(pushDiv("counter details", _UrlKt.FRAGMENT_ENCODE_SET));
        sb.append(i);
        sb.append(popTag());
        sb.append(pushDiv("label bold", _UrlKt.FRAGMENT_ENCODE_SET));
        sb.append(SerializeString(str));
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }

    public String pushAbout(String str, boolean z) {
        String str2;
        StringBuilder sb = new StringBuilder();
        if (z) {
            str2 = "page_about details with_divider";
        } else {
            str2 = "page_about details";
        }
        sb.append(pushDiv(str2, _UrlKt.FRAGMENT_ENCODE_SET));
        sb.append(HtmlWriter.MakeLinks(SerializeString(str)));
        sb.append(popTag());
        return sb.toString();
    }

    public String pushServiceMessage(int i, ApiWrap$DialogInfo apiWrap$DialogInfo, String str, String str2, HtmlWriter.Photo photo) {
        StringBuilder sb = new StringBuilder(pushTag("div", new Pair("class", "message service"), new Pair("id", "message" + i)));
        sb.append(pushDiv("body details"));
        sb.append(str2);
        sb.append(popTag());
        if (photo != null) {
            HtmlWriter.UserpicData userpicData = new HtmlWriter.UserpicData();
            userpicData.colorIndex = apiWrap$DialogInfo.colorIndex;
            userpicData.firstName = apiWrap$DialogInfo.name;
            userpicData.lastName = apiWrap$DialogInfo.lastName;
            userpicData.pixelSize = 60;
            String str3 = photo.image.file.relativePath;
            userpicData.largeLink = str3;
            userpicData.imageLink = HtmlWriter.WriteUserpicThumb(str, str3, userpicData);
            sb.append(pushDiv("userpic_wrap"));
            sb.append(pushUserpic(userpicData));
            sb.append(popTag());
        }
        sb.append(popTag());
        return String.valueOf(sb);
    }

    public Pair<HtmlWriter.MessageInfo, String> pushMessage(ApiWrap$Message apiWrap$Message, HtmlWriter.MessageInfo messageInfo, ApiWrap$DialogInfo apiWrap$DialogInfo, String str, HashMap<Long, ApiWrap$Peer> map, HtmlWriter htmlWriter, String str2, Utilities.Callback2Return<Integer, String, String> callback2Return) {
        String str3;
        Utilities.Callback2Return<Integer, String, String> callback2Return2;
        String str4;
        String str5;
        HtmlWriter.UserpicData userpicData;
        ApiWrap$Peer apiWrap$Peer;
        int iPeerColorIndex;
        HtmlWriter.Photo photo = null;
        HtmlWriter.Photo photo2;
        if (apiWrap$Message == null || apiWrap$DialogInfo == null) {
            return null;
        }
        HtmlWriter.MessageInfo messageInfo2 = new HtmlWriter.MessageInfo();
        messageInfo2.id = apiWrap$Message.id;
        long j = apiWrap$Message.fromId;
        messageInfo2.fromId = j;
        messageInfo2.viaBotId = apiWrap$Message.viaBotId;
        messageInfo2.date = apiWrap$Message.date;
        messageInfo2.forwardedFromId = apiWrap$Message.forwardedFromId;
        messageInfo2.forwardedFromName = apiWrap$Message.forwardedFromName;
        messageInfo2.forwardedDate = apiWrap$Message.forwardedDate;
        messageInfo2.forwarded = apiWrap$Message.forwarded;
        messageInfo2.showForwardedAsOriginal = apiWrap$Message.showForwardedAsOriginal;
        if (apiWrap$Message.media.content instanceof ApiWrap$UnsupportedMedia) {
            return new Pair<>(messageInfo2, pushServiceMessage(apiWrap$Message.id, apiWrap$DialogInfo, str, LocaleController.getString(R.string.UnsupportedMedia2), null));
        }
        ApiWrap$DialogInfo.Type type = apiWrap$DialogInfo.type;
        String textFromAction = htmlWriter.getTextFromAction(apiWrap$Message, HtmlWriter.wrapPeerName(j), type == ApiWrap$DialogInfo.Type.PrivateChannel || type == ApiWrap$DialogInfo.Type.PublicChannel);
        if (!textFromAction.isEmpty()) {
            Object obj = apiWrap$Message.parsedAction;
            if (obj instanceof ApiWrap$ActionSuggestProfilePhoto) {
                photo2 = ((ApiWrap$ActionSuggestProfilePhoto) obj).photo();
            } else {
                if (obj instanceof ApiWrap$ActionChatEditPhoto) {
                    photo2 = ((ApiWrap$ActionChatEditPhoto) obj).photo();
                } else {
                    photo = null;
                }
                return new Pair<>(messageInfo2, pushServiceMessage(apiWrap$Message.id, apiWrap$DialogInfo, str, textFromAction, photo));
            }
            photo = photo2;
            return new Pair<>(messageInfo2, pushServiceMessage(apiWrap$Message.id, apiWrap$DialogInfo, str, textFromAction, photo));
        }
        messageInfo2.type = HtmlWriter.MessageInfo.Type.Default;
        boolean zMessageNeedsWrap = DataTypesUtils.messageNeedsWrap(apiWrap$Message, messageInfo);
        long j2 = apiWrap$Message.fromId;
        boolean z = apiWrap$Message.forwarded && !apiWrap$Message.showForwardedAsOriginal;
        HtmlWriter.UserpicData userpicData2 = new HtmlWriter.UserpicData();
        if (apiWrap$Message.forwarded) {
            long j3 = apiWrap$Message.forwardedFromId;
            if (j3 != 0) {
                iPeerColorIndex = DataTypesUtils.PeerColorIndex(j3);
            } else {
                iPeerColorIndex = DataTypesUtils.PeerColorIndex(apiWrap$Message.id);
            }
            userpicData2.colorIndex = iPeerColorIndex;
            userpicData2.pixelSize = 42;
            long j4 = apiWrap$Message.forwardedFromId;
            if (j4 != 0) {
                DataTypesUtils.FillUserpicNames(userpicData2, map.get(Long.valueOf(j4)));
            } else {
                DataTypesUtils.FillUserpicNames(userpicData2, apiWrap$Message.forwardedFromName);
            }
        }
        HtmlWriter.UserpicData userpicData3 = new HtmlWriter.UserpicData();
        if (apiWrap$Message.showForwardedAsOriginal) {
            userpicData3 = userpicData2;
        } else {
            userpicData3.colorIndex = DataTypesUtils.PeerColorIndex(j2);
            userpicData3.pixelSize = 42;
            DataTypesUtils.FillUserpicNames(userpicData3, map.get(Long.valueOf(j2)));
        }
        long j5 = apiWrap$Message.viaBotId;
        if (j5 != 0 && (apiWrap$Peer = map.get(Long.valueOf(j5))) != null && !apiWrap$Peer.user.username.isEmpty()) {
            SerializeString(apiWrap$Peer.user.username);
        }
        if (zMessageNeedsWrap) {
            str3 = "message default clearfix";
        } else {
            str3 = "message default clearfix joined";
        }
        StringBuilder sb = new StringBuilder(pushTag("div", new Pair("class", str3), new Pair("id", "message" + apiWrap$Message.id)));
        if (zMessageNeedsWrap) {
            sb.append(pushDiv("pull_left userpic_wrap"));
            sb.append(pushUserpic(userpicData3));
            sb.append(popTag());
        }
        sb.append(pushDiv("body"));
        sb.append(pushTag("div", new Pair("class", "pull_right date details"), new Pair("title", LocaleController.getInstance().getExportFullDateFormatter().format(((long) apiWrap$Message.date) * 1000))));
        sb.append(LocaleController.getInstance().getFormatterDay().format(((long) apiWrap$Message.date) * 1000));
        sb.append(popTag());
        if (zMessageNeedsWrap) {
            sb.append(pushDiv("from_name"));
            sb.append(SerializeString(DataTypesUtils.ComposeName(userpicData3, "Deleted Account")));
            sb.append(popTag());
        }
        if (z) {
            boolean zForwardedNeedsWrap = DataTypesUtils.forwardedNeedsWrap(apiWrap$Message, messageInfo);
            if (zForwardedNeedsWrap) {
                sb.append(pushDiv("pull_left forwarded userpic_wrap"));
                userpicData = userpicData2;
                sb.append(pushUserpic(userpicData));
                sb.append(popTag());
            } else {
                userpicData = userpicData2;
            }
            sb.append(pushDiv("forwarded body"));
            if (zForwardedNeedsWrap) {
                sb.append(pushDiv("from_name"));
                sb.append(SerializeString(DataTypesUtils.ComposeName(userpicData, "Deleted Account")));
                sb.append(pushTag("span", new Pair("class", "date details"), new Pair("title", LocaleController.formatDate(apiWrap$Message.forwardedDate)), new Pair("inline", _UrlKt.FRAGMENT_ENCODE_SET)));
                sb.append(" " + LocaleController.formatDate(apiWrap$Message.forwardedDate));
                sb.append(popTag());
                sb.append(popTag());
            }
        }
        if (apiWrap$Message.replyToMsgId != 0) {
            sb.append(pushDiv("reply_to details"));
            if (apiWrap$Message.replyToPeerId != 0) {
                sb.append("In reply to a message in another chat");
                callback2Return2 = callback2Return;
            } else {
                sb.append("In reply to ");
                callback2Return2 = callback2Return;
                sb.append(callback2Return2.run(Integer.valueOf(apiWrap$Message.replyToMsgId), "this message"));
            }
            sb.append(popTag());
        } else {
            callback2Return2 = callback2Return;
        }
        sb.append(pushMedia(apiWrap$Message, str, map, str2, callback2Return2));
        String strFormatText = DataTypesUtils.FormatText(apiWrap$Message.text, str2, this._base);
        if (!strFormatText.isEmpty()) {
            sb.append(pushDiv("text"));
            sb.append(strFormatText);
            sb.append(popTag());
        }
        ArrayList<ArrayList<ApiWrap$HistoryMessageMarkupButton>> arrayList = apiWrap$Message.inlineButtonRows;
        if (arrayList != null && !arrayList.isEmpty()) {
            sb.append(pushTag("table", new Pair("class", "bot_button_table")));
            sb.append(pushTag("tbody", new Pair[0]));
            ArrayList<ArrayList<ApiWrap$HistoryMessageMarkupButton>> arrayList2 = apiWrap$Message.inlineButtonRows;
            int size = arrayList2.size();
            int i = 0;
            while (i < size) {
                ArrayList<ApiWrap$HistoryMessageMarkupButton> arrayList3 = arrayList2.get(i);
                i++;
                ArrayList<ApiWrap$HistoryMessageMarkupButton> arrayList4 = arrayList3;
                sb.append(pushTag("tr", new Pair[0]));
                sb.append(pushTag("td", new Pair("class", "bot_button_row")));
                int size2 = arrayList4.size();
                int i2 = 0;
                while (i2 < size2) {
                    ApiWrap$HistoryMessageMarkupButton apiWrap$HistoryMessageMarkupButton = arrayList4.get(i2);
                    i2++;
                    ApiWrap$HistoryMessageMarkupButton apiWrap$HistoryMessageMarkupButton2 = apiWrap$HistoryMessageMarkupButton;
                    ArrayList<ArrayList<ApiWrap$HistoryMessageMarkupButton>> arrayList5 = arrayList2;
                    if (apiWrap$HistoryMessageMarkupButton2.data() == null || apiWrap$HistoryMessageMarkupButton2.data().length == 0) {
                        str4 = _UrlKt.FRAGMENT_ENCODE_SET;
                    } else {
                        str4 = "Data: " + ChatUtils.getInstance().getTextFromCallback(apiWrap$HistoryMessageMarkupButton2.data()) + " | ";
                    }
                    if (apiWrap$HistoryMessageMarkupButton2.forwardText() != null && !apiWrap$HistoryMessageMarkupButton2.forwardText().isEmpty()) {
                        str4 = str4 + "Forward text: " + apiWrap$HistoryMessageMarkupButton2.forwardText() + " | ";
                    }
                    String str6 = str4 + "Type: " + ApiWrap$HistoryMessageMarkupButton.TypeToString(apiWrap$HistoryMessageMarkupButton2);
                    ApiWrap$HistoryMessageMarkupButton.Type type2 = apiWrap$HistoryMessageMarkupButton2.type();
                    ApiWrap$HistoryMessageMarkupButton.Type type3 = ApiWrap$HistoryMessageMarkupButton.Type.Url;
                    String textFromCallback = type2 == type3 ? ChatUtils.getInstance().getTextFromCallback(apiWrap$HistoryMessageMarkupButton2.data()) : _UrlKt.FRAGMENT_ENCODE_SET;
                    if (apiWrap$HistoryMessageMarkupButton2.type() != type3) {
                        str5 = "return ShowTextCopied('" + str6 + "');";
                    } else {
                        str5 = _UrlKt.FRAGMENT_ENCODE_SET;
                    }
                    sb.append(pushTag("div", new Pair("class", "bot_button")));
                    sb.append(pushTag("a", textFromCallback.isEmpty() ? new Pair(_UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET) : new Pair("href", textFromCallback), str5.isEmpty() ? new Pair(_UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET) : new Pair("onclick", str5)));
                    sb.append(pushTag("div", new Pair[0]));
                    sb.append(apiWrap$HistoryMessageMarkupButton2.text());
                    sb.append(popTag());
                    sb.append(popTag());
                    sb.append(popTag());
                    if (apiWrap$HistoryMessageMarkupButton2 != arrayList4.get(arrayList4.size() - 1)) {
                        sb.append(pushTag("div", new Pair("class", "bot_button_column_separator")));
                        sb.append(popTag());
                    }
                    size = size;
                    i = i;
                    arrayList2 = arrayList5;
                }
                sb.append(popTag());
                sb.append(popTag());
                size = size;
                arrayList2 = arrayList2;
            }
            sb.append(popTag());
            sb.append(popTag());
        }
        if (!apiWrap$Message.signature.isEmpty()) {
            sb.append(pushDiv("signature details"));
            sb.append(SerializeString(apiWrap$Message.signature));
            sb.append(popTag());
        }
        if (z) {
            sb.append(popTag());
        }
        if (!apiWrap$Message.reactions.isEmpty()) {
            sb.append(pushDiv("reactions"));
            Iterator<ApiWrap$Reaction> it = apiWrap$Message.reactions.iterator();
            if (it.hasNext()) {
                it.next();
                throw null;
            }
            sb.append(popTag());
        }
        sb.append(popTag());
        sb.append(popTag());
        return new Pair<>(messageInfo2, sb.toString());
    }

    public String pushMedia(ApiWrap$Message apiWrap$Message, String str, HashMap<Long, ApiWrap$Peer> map, String str2, Utilities.Callback2Return<Integer, String, String> callback2Return) {
        ApiWrap$MediaData apiWrap$MediaDataPrepareMediaData = prepareMediaData(apiWrap$Message, str, map, str2);
        String str3 = apiWrap$MediaDataPrepareMediaData.classes;
        if (str3 == null || !str3.isEmpty()) {
            return pushGenericMedia(apiWrap$MediaDataPrepareMediaData);
        }
        Object obj = apiWrap$Message.media.content;
        if (obj instanceof ApiWrap$Document) {
            ApiWrap$Document apiWrap$Document = (ApiWrap$Document) obj;
            if (apiWrap$Document.isSticker) {
                return pushStickerMedia(apiWrap$Document, str);
            }
            if (apiWrap$Document.isAnimated) {
                return pushAnimatedMedia(apiWrap$Document, str);
            }
            if (apiWrap$Document.isVideoFile) {
                return pushVideoFileMedia(apiWrap$Document, str);
            }
            throw new RuntimeException("Non generic document in pushMedia.");
        }
        if (obj instanceof HtmlWriter.Photo) {
            return pushPhotoMedia((HtmlWriter.Photo) obj, str);
        }
        if (obj instanceof ApiWrap$Poll) {
            return pushPoll((ApiWrap$Poll) obj);
        }
        if (obj instanceof ApiWrap$GiveawayStart) {
            return pushGiveaway(map, (ApiWrap$GiveawayStart) obj);
        }
        if (obj instanceof ApiWrap$GiveawayResults) {
            return pushGiveaway(map, (ApiWrap$GiveawayResults) obj, callback2Return);
        }
        return _UrlKt.FRAGMENT_ENCODE_SET;
    }

    private String pushStickerMedia(ApiWrap$Document apiWrap$Document, String str) {
        Pair<String, Dimension> pairWriteImageThumb = WriteImageThumb(str, apiWrap$Document.file.relativePath, CalculateThumbSize(384, 384, 80, 80, false), "PNG", -1, _UrlKt.FRAGMENT_ENCODE_SET);
        String str2 = (String) pairWriteImageThumb.first;
        Dimension dimension = (Dimension) pairWriteImageThumb.second;
        if (str2 == null || str2.isEmpty()) {
            ApiWrap$MediaData apiWrap$MediaData = new ApiWrap$MediaData();
            apiWrap$MediaData.title = "Sticker";
            apiWrap$MediaData.status = apiWrap$Document.stickerEmoji;
            if (apiWrap$Document.file.relativePath.isEmpty()) {
                if (!apiWrap$MediaData.status.isEmpty()) {
                    apiWrap$MediaData.status += ", ";
                }
                apiWrap$MediaData.status += AndroidUtilities.formatFileSize(apiWrap$Document.file.size);
            } else {
                apiWrap$MediaData.link = apiWrap$Document.file.relativePath;
            }
            apiWrap$MediaData.description = DataTypesUtils.NoFileDescription(apiWrap$Document.file.skipReason);
            apiWrap$MediaData.classes = "media_photo";
            return pushGenericMedia(apiWrap$MediaData);
        }
        return pushDiv("media_wrap clearfix") + pushTag("a", new Pair("class", "sticker_wrap clearfix pull_left"), new Pair("href", apiWrap$Document.file.relativePath)) + pushTag("img", new Pair("class", "sticker"), new Pair("style", "width: " + DataTypesUtils.NumberToString(dimension.getWidth() / 2) + "px; height: " + DataTypesUtils.NumberToString(dimension.getHeight() / 2) + "px"), new Pair("src", str2), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)) + popTag() + popTag();
    }

    private String pushAnimatedMedia(ApiWrap$Document apiWrap$Document, String str) {
        Dimension dimension = new Dimension(apiWrap$Document.width, apiWrap$Document.height);
        Function<Dimension, Dimension> functionCalculateThumbSize = CalculateThumbSize(520, 520, 80, 80, true);
        if (apiWrap$Document.thumb.file.relativePath.isEmpty() || apiWrap$Document.file.relativePath.isEmpty() || functionCalculateThumbSize.apply(dimension).getWidth() == 0 || functionCalculateThumbSize.apply(dimension).getHeight() == 0) {
            ApiWrap$MediaData apiWrap$MediaData = new ApiWrap$MediaData();
            apiWrap$MediaData.title = "Animation";
            apiWrap$MediaData.status = AndroidUtilities.formatFileSize(apiWrap$Document.file.size);
            ApiWrap$File apiWrap$File = apiWrap$Document.file;
            apiWrap$MediaData.link = apiWrap$File.relativePath;
            apiWrap$MediaData.description = DataTypesUtils.NoFileDescription(apiWrap$File.skipReason);
            apiWrap$MediaData.classes = "media_video";
            return pushGenericMedia(apiWrap$MediaData);
        }
        return pushDiv("media_wrap clearfix") + pushTag("a", new Pair("class", "animated_wrap clearfix pull_left"), new Pair("href", relativePath(apiWrap$Document.file.relativePath))) + pushDiv("video_play_bg") + pushDiv("gif_play") + "GIF" + popTag() + popTag() + pushTag("img", new Pair("class", "animated"), new Pair("style", "width: " + DataTypesUtils.NumberToString(functionCalculateThumbSize.apply(dimension).getWidth() / 2) + "px; height: " + DataTypesUtils.NumberToString(functionCalculateThumbSize.apply(dimension).getHeight() / 2) + "px"), new Pair("src", apiWrap$Document.thumb.file.relativePath), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)) + popTag() + popTag();
    }

    private ApiWrap$MediaData prepareMediaData(ApiWrap$Message apiWrap$Message, String str, HashMap<Long, ApiWrap$Peer> map, String str2) {
        String str3;
        ApiWrap$Peer apiWrap$Peer;
        ApiWrap$User apiWrap$User;
        String str4;
        byte[] bArr;
        String str5;
        String str6;
        String str7;
        long j;
        String str8;
        ApiWrap$MediaData apiWrap$MediaData = new ApiWrap$MediaData();
        TLRPC.MessageAction messageAction = apiWrap$Message.action;
        boolean z = messageAction instanceof TLRPC.TL_messageActionPhoneCall;
        String strName = _UrlKt.FRAGMENT_ENCODE_SET;
        if (z) {
            TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall = (TLRPC.TL_messageActionPhoneCall) messageAction;
            apiWrap$MediaData.classes = "media_call";
            if (apiWrap$Message.out) {
                j = apiWrap$Message.peerId;
            } else {
                j = apiWrap$Message.selfId;
            }
            ApiWrap$Peer apiWrap$Peer2 = map.get(Long.valueOf(j));
            if (apiWrap$Peer2 != null) {
                strName = apiWrap$Peer2.name();
            }
            apiWrap$MediaData.title = strName;
            TLRPC.PhoneCallDiscardReason phoneCallDiscardReason = tL_messageActionPhoneCall.reason;
            boolean z2 = phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonMissed;
            boolean z3 = phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonBusy;
            if (apiWrap$Message.out) {
                str8 = z2 ? "Cancelled" : "Outgoing";
            } else if (z2) {
                str8 = "Missed";
            } else if (!z3) {
                str8 = "Incoming";
            } else {
                str8 = "Declined";
            }
            apiWrap$MediaData.status = str8;
            if (tL_messageActionPhoneCall.duration > 0) {
                apiWrap$MediaData.classes += " success";
                apiWrap$MediaData.status += " (" + LocaleController.formatCallDuration(tL_messageActionPhoneCall.duration) + " seconds)";
                return apiWrap$MediaData;
            }
        } else {
            ApiWrap$Media apiWrap$Media = apiWrap$Message.media;
            Object obj = apiWrap$Media.content;
            if (obj instanceof HtmlWriter.Photo) {
                HtmlWriter.Photo photo = (HtmlWriter.Photo) obj;
                if (apiWrap$Media.ttl != 0) {
                    apiWrap$MediaData.title = "Self-destructing photo";
                    apiWrap$MediaData.status = photo.id == 0 ? "Please view it on your mobile" : "Expired";
                    apiWrap$MediaData.classes = "media_photo";
                    return apiWrap$MediaData;
                }
            } else if (obj instanceof ApiWrap$Document) {
                ApiWrap$Document apiWrap$Document = (ApiWrap$Document) obj;
                if (apiWrap$Media.ttl != 0) {
                    apiWrap$MediaData.title = "Self-destructing video";
                    apiWrap$MediaData.status = apiWrap$Document.id != 0 ? "Please view it on your mobile" : "Expired";
                    apiWrap$MediaData.classes = "media_video";
                    return apiWrap$MediaData;
                }
                boolean zIsEmpty = apiWrap$Document.file.relativePath.isEmpty();
                apiWrap$MediaData.link = apiWrap$Document.file.relativePath;
                apiWrap$MediaData.description = DataTypesUtils.NoFileDescription(apiWrap$Message.skipReason);
                if (!apiWrap$Document.isSticker) {
                    if (apiWrap$Document.isVideoMessage) {
                        apiWrap$MediaData.title = "Video message";
                        apiWrap$MediaData.status = LocaleController.formatDuration(apiWrap$Document.duration);
                        if (zIsEmpty) {
                            apiWrap$MediaData.status += ", " + AndroidUtilities.formatFileSize(apiWrap$Document.file.size);
                        }
                        apiWrap$MediaData.thumb = apiWrap$Document.thumb.file.relativePath;
                        apiWrap$MediaData.classes = "media_video";
                        return apiWrap$MediaData;
                    }
                    if (apiWrap$Document.isVoiceMessage) {
                        apiWrap$MediaData.title = "Voice message";
                        apiWrap$MediaData.status = LocaleController.formatDuration(apiWrap$Document.duration);
                        if (zIsEmpty) {
                            apiWrap$MediaData.status += ", " + AndroidUtilities.formatFileSize(apiWrap$Document.file.size);
                        }
                        apiWrap$MediaData.classes = "media_voice_message";
                        return apiWrap$MediaData;
                    }
                    if (!apiWrap$Document.isAnimated && !apiWrap$Document.isVideoFile) {
                        if (apiWrap$Document.isAudioFile) {
                            String str9 = apiWrap$Document.songPerformer;
                            if (str9 == null || str9.isEmpty() || (str7 = apiWrap$Document.songTitle) == null || str7.isEmpty()) {
                                str6 = "Audio file";
                            } else {
                                str6 = apiWrap$Document.songPerformer + " – " + apiWrap$Document.songTitle;
                            }
                            apiWrap$MediaData.title = str6;
                            apiWrap$MediaData.status = AndroidUtilities.formatLongDuration(apiWrap$Document.duration);
                            if (zIsEmpty) {
                                apiWrap$MediaData.status += ", " + AndroidUtilities.formatFileSize(apiWrap$Document.file.size);
                            }
                            apiWrap$MediaData.classes = "media_audio_file";
                            return apiWrap$MediaData;
                        }
                        String str10 = apiWrap$Document.name;
                        if (str10 == null || str10.isEmpty()) {
                            str5 = "File";
                        } else {
                            str5 = apiWrap$Document.name;
                        }
                        apiWrap$MediaData.title = str5;
                        apiWrap$MediaData.status = AndroidUtilities.formatFileSize(apiWrap$Document.file.size);
                        apiWrap$MediaData.classes = "media_file";
                        return apiWrap$MediaData;
                    }
                }
            } else if (obj instanceof ApiWrap$SharedContact) {
                ApiWrap$SharedContact apiWrap$SharedContact = (ApiWrap$SharedContact) obj;
                apiWrap$MediaData.title = apiWrap$SharedContact.info.firstName + " " + apiWrap$SharedContact.info.lastName;
                apiWrap$MediaData.classes = "media_contact";
                apiWrap$MediaData.status = PhoneFormat.getInstance().format(apiWrap$SharedContact.info.phoneNumber);
                ApiWrap$File apiWrap$File = apiWrap$SharedContact.vcard;
                if (apiWrap$File != null && (bArr = apiWrap$File.content) != null && bArr.length > 0) {
                    apiWrap$MediaData.status += " - vCard";
                    apiWrap$MediaData.link = apiWrap$SharedContact.vcard.relativePath;
                    return apiWrap$MediaData;
                }
            } else {
                if (obj instanceof ApiWrap$GeoPoint) {
                    ApiWrap$GeoPoint apiWrap$GeoPoint = (ApiWrap$GeoPoint) obj;
                    if (apiWrap$Media.ttl != 0) {
                        apiWrap$MediaData.classes = "media_live_location";
                        apiWrap$MediaData.title = "Live location";
                        apiWrap$MediaData.status = _UrlKt.FRAGMENT_ENCODE_SET;
                    } else {
                        apiWrap$MediaData.classes = "media_location";
                        apiWrap$MediaData.title = "Location";
                    }
                    String strValueOf = String.valueOf(apiWrap$GeoPoint.latitude);
                    String strValueOf2 = String.valueOf(apiWrap$GeoPoint.longitude);
                    String str11 = strValueOf + ',' + strValueOf2;
                    apiWrap$MediaData.status = strValueOf + ", " + strValueOf2;
                    apiWrap$MediaData.link = "https://maps.google.com/maps?q=" + str11 + "&ll=" + str11 + "&z=16";
                    return apiWrap$MediaData;
                }
                if (obj instanceof ApiWrap$Venue) {
                    ApiWrap$Venue apiWrap$Venue = (ApiWrap$Venue) obj;
                    apiWrap$MediaData.classes = "media_venue";
                    apiWrap$MediaData.title = apiWrap$Venue.title;
                    apiWrap$MediaData.description = apiWrap$Venue.address;
                    ApiWrap$GeoPoint apiWrap$GeoPoint2 = apiWrap$Venue.point;
                    if (apiWrap$GeoPoint2 != null && apiWrap$GeoPoint2.valid) {
                        String str12 = String.valueOf(apiWrap$GeoPoint2.latitude) + ',' + String.valueOf(apiWrap$Venue.point.longitude);
                        apiWrap$MediaData.link = "https://maps.google.com/maps?q=" + str12 + "&ll=" + str12 + "&z=16";
                        return apiWrap$MediaData;
                    }
                } else if (obj instanceof ApiWrap$Game) {
                    ApiWrap$Game apiWrap$Game = (ApiWrap$Game) obj;
                    apiWrap$MediaData.classes = "media_game";
                    apiWrap$MediaData.title = apiWrap$Game.title;
                    apiWrap$MediaData.description = apiWrap$Game.description;
                    if (apiWrap$Game.botId != 0 && (str3 = apiWrap$Game.shortName) != null && !str3.isEmpty() && (apiWrap$Peer = map.get(Long.valueOf(apiWrap$Game.botId))) != null && (apiWrap$User = apiWrap$Peer.user) != null && apiWrap$User.isBot && (str4 = apiWrap$User.username) != null && !str4.isEmpty()) {
                        String str13 = str2 + apiWrap$Peer.user.username + "?game=" + apiWrap$Game.shortName;
                        apiWrap$MediaData.link = str13;
                        apiWrap$MediaData.status = str13;
                    }
                } else {
                    if (obj instanceof ApiWrap$Invoice) {
                        ApiWrap$Invoice apiWrap$Invoice = (ApiWrap$Invoice) obj;
                        apiWrap$MediaData.classes = "media_invoice";
                        apiWrap$MediaData.title = apiWrap$Invoice.title;
                        apiWrap$MediaData.description = apiWrap$Invoice.description;
                        apiWrap$MediaData.status = LocaleController.getInstance().formatCurrencyString(apiWrap$Invoice.amount, apiWrap$Invoice.currency);
                        return apiWrap$MediaData;
                    }
                    if (obj instanceof ApiWrap$PaidMedia) {
                        apiWrap$MediaData.classes = "media_invoice";
                        apiWrap$MediaData.status = LocaleController.getInstance().formatCurrencyString(((ApiWrap$PaidMedia) obj).stars, "XTR");
                    }
                    return apiWrap$MediaData;
                }
            }
        }
        return apiWrap$MediaData;
    }

    private String pushGenericMedia(ApiWrap$MediaData apiWrap$MediaData) {
        String strRelativePath;
        StringBuilder sb = new StringBuilder(pushDiv("media_wrap clearfix"));
        if (apiWrap$MediaData.link.isEmpty()) {
            sb.append(pushDiv("media clearfix pull_left " + apiWrap$MediaData.classes));
        } else {
            Pair pair = new Pair("class", "media clearfix pull_left block_link " + apiWrap$MediaData.classes);
            if (apiWrap$MediaData.link.toLowerCase().startsWith("http://") || apiWrap$MediaData.link.toLowerCase().startsWith("https://")) {
                strRelativePath = apiWrap$MediaData.link;
            } else {
                strRelativePath = relativePath(apiWrap$MediaData.link);
            }
            sb.append(pushTag("a", pair, new Pair("href", strRelativePath)));
        }
        if (apiWrap$MediaData.thumb.isEmpty()) {
            sb.append(pushDiv("fill pull_left"));
            sb.append(popTag());
        } else {
            sb.append(pushTag("img", new Pair("class", "thumb pull_left"), new Pair("src", relativePath(apiWrap$MediaData.thumb)), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)));
        }
        sb.append(pushDiv("body"));
        if (!apiWrap$MediaData.title.isEmpty()) {
            sb.append(pushDiv("title bold"));
            sb.append(SerializeString(apiWrap$MediaData.title));
            sb.append(popTag());
        }
        if (!apiWrap$MediaData.description.isEmpty()) {
            sb.append(pushDiv(MediaTrack.ROLE_DESCRIPTION));
            sb.append(SerializeString(apiWrap$MediaData.description));
            sb.append(popTag());
        }
        if (!apiWrap$MediaData.status.isEmpty()) {
            sb.append(pushDiv("status details"));
            sb.append(SerializeString(apiWrap$MediaData.status));
            sb.append(popTag());
        }
        sb.append(popTag());
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }

    private String pushVideoFileMedia(ApiWrap$Document apiWrap$Document, String str) {
        Dimension dimensionApply = CalculateThumbSize(520, 520, 80, 80, true).apply(new Dimension(apiWrap$Document.width, apiWrap$Document.height));
        if (apiWrap$Document.thumb.file.relativePath.isEmpty() || apiWrap$Document.file.relativePath.isEmpty() || dimensionApply.getWidth() == 0 || dimensionApply.getHeight() == 0) {
            ApiWrap$MediaData apiWrap$MediaData = new ApiWrap$MediaData();
            apiWrap$MediaData.title = "Video file";
            apiWrap$MediaData.status = AndroidUtilities.formatLongDuration(apiWrap$Document.duration);
            if (apiWrap$Document.file.relativePath.isEmpty()) {
                apiWrap$MediaData.status += ", " + AndroidUtilities.formatFileSize(apiWrap$Document.file.size);
            } else {
                apiWrap$MediaData.link = apiWrap$Document.file.relativePath;
            }
            apiWrap$MediaData.description = DataTypesUtils.NoFileDescription(apiWrap$Document.file.skipReason);
            apiWrap$MediaData.classes = "media_video";
            return pushGenericMedia(apiWrap$MediaData);
        }
        return pushDiv("media_wrap clearfix") + pushTag("a", new Pair("class", "video_file_wrap clearfix pull_left"), new Pair("href", apiWrap$Document.file.relativePath)) + pushDiv("video_play_bg") + pushDiv("video_play") + popTag() + popTag() + pushDiv("video_duration") + AndroidUtilities.formatLongDuration(apiWrap$Document.duration) + popTag() + pushTag("img", new Pair("class", "video_file"), new Pair("style", "width: " + DataTypesUtils.NumberToString(dimensionApply.getWidth() / 2) + "px; height: " + DataTypesUtils.NumberToString(dimensionApply.getHeight() / 2) + "px"), new Pair("src", relativePath(apiWrap$Document.thumb.file.relativePath)), new Pair("empty", _UrlKt.FRAGMENT_ENCODE_SET)) + popTag() + popTag();
    }

    private String pushPhotoMedia(HtmlWriter.Photo photo, String str) {
        ApiWrap$MediaData apiWrap$MediaData = new ApiWrap$MediaData();
        apiWrap$MediaData.title = "Photo";
        apiWrap$MediaData.status = photo.image.width + "×" + photo.image.height;
        if (photo.image.file.relativePath.isEmpty()) {
            apiWrap$MediaData.status += ", " + AndroidUtilities.formatFileSize(photo.image.file.size);
        } else {
            apiWrap$MediaData.link = photo.image.file.relativePath;
        }
        apiWrap$MediaData.description = DataTypesUtils.NoFileDescription(photo.image.file.skipReason);
        apiWrap$MediaData.classes = "media_photo";
        return pushGenericMedia(apiWrap$MediaData);
    }

    private String pushPoll(ApiWrap$Poll apiWrap$Poll) {
        StringBuilder sb = new StringBuilder(pushDiv("media_wrap clearfix"));
        sb.append(pushDiv("media_poll"));
        sb.append(pushDiv("question bold"));
        sb.append(SerializeString(apiWrap$Poll.question));
        sb.append(popTag());
        sb.append(pushDiv("details"));
        if (apiWrap$Poll.closed) {
            sb.append(SerializeString("Final results"));
        } else {
            sb.append(SerializeString("Anonymous poll"));
        }
        sb.append(popTag());
        final Utilities.CallbackReturn callbackReturn = new Utilities.CallbackReturn() { // from class: com.exteragram.messenger.export.output.html.HtmlContext$$ExternalSyntheticLambda2
            @Override // org.telegram.messenger.Utilities.CallbackReturn
            public final Object run(Object obj) {
                return HtmlContext.m1078$r8$lambda$NQmNSWBFypYJJj_9YIdSy44Sh4((Integer) obj);
            }
        };
        Utilities.CallbackReturn callbackReturn2 = new Utilities.CallbackReturn() { // from class: com.exteragram.messenger.export.output.html.HtmlContext$$ExternalSyntheticLambda3
            @Override // org.telegram.messenger.Utilities.CallbackReturn
            public final Object run(Object obj) {
                return HtmlContext.m1079$r8$lambda$pbHCCbDwwIyimhpdjqWjW9ppqA(callbackReturn, (ApiWrap$Poll.Answer) obj);
            }
        };
        ArrayList<ApiWrap$Poll.Answer> arrayList = apiWrap$Poll.answers;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$Poll.Answer answer = arrayList.get(i);
            i++;
            ApiWrap$Poll.Answer answer2 = answer;
            sb.append(pushDiv("answer"));
            sb.append("- ");
            sb.append(SerializeString(answer2.text()));
            sb.append((String) callbackReturn2.run(answer2));
            sb.append(popTag());
        }
        sb.append(pushDiv("total details\t"));
        sb.append((String) callbackReturn.run(Integer.valueOf(apiWrap$Poll.totalVotes)));
        sb.append(popTag());
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }

    /* JADX INFO: renamed from: $r8$lambda$NQmNSWBFypYJJj_9YIdSy44-Sh4, reason: not valid java name */
    public static /* synthetic */ String m1078$r8$lambda$NQmNSWBFypYJJj_9YIdSy44Sh4(Integer num) {
        if (num.intValue() > 1) {
            return DataTypesUtils.NumberToString(num.intValue()) + " votes";
        }
        if (num.intValue() > 0) {
            return DataTypesUtils.NumberToString(num.intValue()) + " vote";
        }
        return "No votes";
    }

    /* JADX INFO: renamed from: $r8$lambda$pbHCCbDwwIyim-hpdjqWjW9ppqA, reason: not valid java name */
    public static /* synthetic */ String m1079$r8$lambda$pbHCCbDwwIyimhpdjqWjW9ppqA(Utilities.CallbackReturn callbackReturn, ApiWrap$Poll.Answer answer) {
        if (answer.votes() == 0) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        if (!answer.my()) {
            return " <span class=\"details\">" + ((String) callbackReturn.run(Integer.valueOf(answer.votes()))) + "</span>";
        }
        return " <span class=\"details\">" + ((String) callbackReturn.run(Integer.valueOf(answer.votes()))) + ", chosen vote</span>";
    }

    private String pushGiveaway(HashMap<Long, ApiWrap$Peer> map, ApiWrap$GiveawayStart apiWrap$GiveawayStart) {
        String strSerializeString;
        int i;
        ApiWrap$Chat apiWrap$Chat;
        StringBuilder sb = new StringBuilder(pushDiv("media_wrap clearfix"));
        sb.append(pushDiv("media_giveaway"));
        sb.append(pushDiv("section_title bold"));
        if (apiWrap$GiveawayStart.quantity > 1) {
            strSerializeString = SerializeString("Giveaway Prizes");
        } else {
            strSerializeString = SerializeString("Giveaway Prize");
        }
        sb.append(strSerializeString);
        sb.append(popTag());
        sb.append(pushDiv("section_body"));
        sb.append("<b>");
        sb.append(DataTypesUtils.NumberToString(apiWrap$GiveawayStart.quantity));
        sb.append("</b> ");
        sb.append(SerializeString(apiWrap$GiveawayStart.additionalPrize));
        sb.append(popTag());
        sb.append(pushDiv("section_title bold"));
        sb.append(SerializeString("with"));
        sb.append(popTag());
        sb.append(pushDiv("section_body"));
        if (apiWrap$GiveawayStart.credits > 0) {
            sb.append("<b>");
            sb.append(DataTypesUtils.NumberToString((int) apiWrap$GiveawayStart.credits));
            sb.append(SerializeString(apiWrap$GiveawayStart.credits == 1 ? " Star" : " Stars"));
            sb.append("/<b>");
            sb.append(SerializeString("will be distributed "));
            if (apiWrap$GiveawayStart.quantity == 1) {
                sb.append(SerializeString("to "));
                sb.append(SerializeString("<b>"));
                sb.append(DataTypesUtils.NumberToString(apiWrap$GiveawayStart.quantity));
                sb.append(SerializeString("</b> "));
                sb.append(SerializeString("winner."));
            } else {
                sb.append(SerializeString("among "));
                sb.append("<b>");
                sb.append(DataTypesUtils.NumberToString(apiWrap$GiveawayStart.quantity));
                sb.append(SerializeString("</b> "));
                sb.append(SerializeString("winners."));
            }
        } else {
            sb.append("<b>");
            sb.append(DataTypesUtils.NumberToString(apiWrap$GiveawayStart.quantity));
            sb.append("</b> ");
            if (apiWrap$GiveawayStart.quantity > 1) {
                sb.append(SerializeString("Telegram Premium Subscriptions"));
            } else {
                sb.append(SerializeString("Telegram Premium Subscription"));
            }
            sb.append(" for <b>");
            sb.append(DataTypesUtils.NumberToString(apiWrap$GiveawayStart.months));
            sb.append("</b> ");
            if (apiWrap$GiveawayStart.months > 1) {
                sb.append("months.");
            } else {
                sb.append("month.");
            }
        }
        sb.append(popTag());
        sb.append(pushDiv("section_title bold"));
        sb.append(SerializeString("Participants"));
        sb.append(popTag());
        sb.append(pushDiv("section_body"));
        ArrayList arrayList = new ArrayList();
        ArrayList<Long> arrayList2 = apiWrap$GiveawayStart.channels;
        int size = arrayList2.size();
        boolean z = false;
        boolean z2 = false;
        int i2 = 0;
        while (i2 < size) {
            Long l = arrayList2.get(i2);
            i2++;
            Long l2 = l;
            ApiWrap$Peer apiWrap$Peer = map.get(l2);
            if (apiWrap$Peer != null && (apiWrap$Chat = apiWrap$Peer.chat) != null) {
                if (apiWrap$Chat.isBroadcast) {
                    z2 = true;
                } else if (apiWrap$Chat.isSupergroup) {
                    z = true;
                }
            }
            arrayList.add("<b>" + HtmlWriter.wrapPeerName(l2.longValue()) + "</b>");
        }
        String str = (apiWrap$GiveawayStart.all && !z && z2 && arrayList.size() == 1) ? "All subscribers of the channel:" : _UrlKt.FRAGMENT_ENCODE_SET;
        if (apiWrap$GiveawayStart.all && !z && z2 && arrayList.size() > 1) {
            str = "All subscribers of the channels:";
        }
        if (apiWrap$GiveawayStart.all && z && !z2 && arrayList.size() == 1) {
            str = "All members of the group:";
        }
        if (apiWrap$GiveawayStart.all && z && !z2 && arrayList.size() > 1) {
            str = "All members of the groups:";
        }
        String str2 = (apiWrap$GiveawayStart.all && z && z2 && arrayList.size() == 1) ? "All members of the group:" : str;
        if (apiWrap$GiveawayStart.all && z && z2 && arrayList.size() > 1) {
            str2 = "All members of the groups and channels:";
        }
        if (!apiWrap$GiveawayStart.all && !z && z2 && arrayList.size() == 1) {
            str2 = "All users who joined the channel below after this date:";
        }
        if (!apiWrap$GiveawayStart.all && !z && z2 && arrayList.size() > 1) {
            str2 = "All users who joined the channels below after this date:";
        }
        if (!apiWrap$GiveawayStart.all && z && !z2 && arrayList.size() == 1) {
            str2 = "All users who joined the group below after this date:";
        }
        if (!apiWrap$GiveawayStart.all && z && !z2 && arrayList.size() > 1) {
            str2 = "All users who joined the groups below after this date:";
        }
        String str3 = (!apiWrap$GiveawayStart.all && z && z2 && arrayList.size() == 1) ? "All users who joined the group below after this date:" : str2;
        if (!apiWrap$GiveawayStart.all && z && z2) {
            i = 1;
            if (arrayList.size() > 1) {
                str3 = "All users who joined the groups and channels below after this date:";
            }
        } else {
            i = 1;
        }
        sb.append(SerializeString(str3));
        sb.append(TextUtils.join(", ", arrayList));
        sb.append(popTag());
        ArrayList arrayList3 = new ArrayList();
        HashMap map2 = new HashMap();
        for (String str4 : Locale.getISOCountries()) {
            map2.put(new Locale(_UrlKt.FRAGMENT_ENCODE_SET, str4).getDisplayCountry(), str4);
        }
        ArrayList<String> arrayList4 = apiWrap$GiveawayStart.countries;
        int size2 = arrayList4.size();
        int i3 = 0;
        while (i3 < size2) {
            String str5 = arrayList4.get(i3);
            i3++;
            String str6 = (String) map2.get(str5);
            arrayList3.add(countryToEmoji(str6) + "\t " + str6);
        }
        if (!arrayList3.isEmpty()) {
            int size3 = arrayList3.size();
            String str7 = (String) arrayList3.get(0);
            while (i != size3) {
                int i4 = i + 1;
                str7 = String.format(i4 == size3 ? "%1s and %2s" : "%1s, %2s", str7, arrayList3.get(i));
                i = i4;
            }
            sb.append(pushDiv("section_body"));
            sb.append(SerializeString(String.format("from %s", str7)));
            sb.append(popTag());
        }
        sb.append(pushDiv("section_title bold"));
        sb.append(SerializeString("Winners Selection Date"));
        sb.append(popTag());
        sb.append(pushDiv("section_body"));
        sb.append(LocaleController.formatDateTime(apiWrap$GiveawayStart.untilDate, false));
        sb.append(popTag());
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }

    private String pushGiveaway(HashMap<Long, ApiWrap$Peer> map, ApiWrap$GiveawayResults apiWrap$GiveawayResults, Utilities.Callback2Return<Integer, String, String> callback2Return) {
        String strSerializeString;
        String strSerializeString2;
        String str;
        String str2;
        StringBuilder sb = new StringBuilder(pushDiv("media_wrap clearfix"));
        sb.append(pushDiv("media_giveaway"));
        sb.append(pushDiv("section_title bold"));
        if (apiWrap$GiveawayResults.winnersCount > 1) {
            strSerializeString = SerializeString("Winners Selected!");
        } else {
            strSerializeString = SerializeString("Winner Selected!");
        }
        sb.append(strSerializeString);
        sb.append(popTag());
        sb.append(pushDiv("section_body"));
        sb.append("<b>");
        sb.append(DataTypesUtils.NumberToString(apiWrap$GiveawayResults.winnersCount));
        sb.append("</b> ");
        sb.append(SerializeString(apiWrap$GiveawayResults.winnersCount > 1 ? "winners" : "winner"));
        sb.append(" of the ");
        sb.append(callback2Return.run(Integer.valueOf(apiWrap$GiveawayResults.launchId), "Giveaway"));
        sb.append(" was randomly selected by Telegram.");
        sb.append(popTag());
        sb.append(pushDiv("section_title bold"));
        if (apiWrap$GiveawayResults.winnersCount > 1) {
            strSerializeString2 = SerializeString("Winners");
        } else {
            strSerializeString2 = SerializeString("Winner");
        }
        sb.append(strSerializeString2);
        sb.append(popTag());
        sb.append(pushDiv("section_body"));
        ArrayList arrayList = new ArrayList();
        ArrayList<Long> arrayList2 = apiWrap$GiveawayResults.winners;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Long l = arrayList2.get(i);
            i++;
            arrayList.add("<b>" + HtmlWriter.wrapPeerName(l.longValue()) + "</b>");
        }
        int i2 = apiWrap$GiveawayResults.winnersCount;
        int size2 = apiWrap$GiveawayResults.winners.size();
        String strSerializeString3 = _UrlKt.FRAGMENT_ENCODE_SET;
        if (i2 > size2) {
            str = SerializeString(" and ") + DataTypesUtils.NumberToString(apiWrap$GiveawayResults.winnersCount - apiWrap$GiveawayResults.winners.size()) + SerializeString(" more!");
        } else {
            str = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        sb.append(TextUtils.join(", ", arrayList));
        sb.append(str);
        sb.append(popTag());
        sb.append(pushDiv("section_body"));
        long j = apiWrap$GiveawayResults.credits;
        boolean z = j == 1;
        if (j != 0 && apiWrap$GiveawayResults.winnersCount == 1) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(SerializeString("The winner received "));
            sb2.append("<b>");
            sb2.append(DataTypesUtils.NumberToString((int) apiWrap$GiveawayResults.credits));
            sb2.append("</b>");
            sb2.append(SerializeString(z ? " Star." : " Stars."));
            strSerializeString3 = sb2.toString();
        } else if (j != 0 && apiWrap$GiveawayResults.winnersCount > 1) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(SerializeString("All winners received "));
            sb3.append("<b>");
            sb3.append(DataTypesUtils.NumberToString((int) apiWrap$GiveawayResults.credits));
            sb3.append("</b>");
            if (z) {
                str2 = " Star in total.";
            } else {
                str2 = " Stars in total.";
            }
            sb3.append(SerializeString(str2));
            strSerializeString3 = sb3.toString();
        } else if (apiWrap$GiveawayResults.unclaimedCount != 0) {
            strSerializeString3 = SerializeString("Some winners couldn't be selected.");
        } else {
            int i3 = apiWrap$GiveawayResults.winnersCount;
            if (i3 == 1) {
                strSerializeString3 = SerializeString("The winner received their gift link in a private message.");
            } else if (i3 > 1) {
                strSerializeString3 = SerializeString("All winners received gift links in private messages.");
            }
        }
        sb.append(strSerializeString3);
        sb.append(popTag());
        sb.append(popTag());
        sb.append(popTag());
        return sb.toString();
    }
}
