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

    public HtmlContext(String str, String str2, OutputFile.Stats stats) {
        this._file = new OutputFile(str, stats);
        String strSubstring = str.substring(str2.length() + 1);
        this._base = com.exteragram.messenger.utils.RecordUtils.repeat("../", strSubstring.length() - strSubstring.replace("/", _UrlKt.FRAGMENT_ENCODE_SET).length());
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
            zzaak$$ExternalSyntheticBUOutline0.m(e);
            return null;
        }
    }

    public static Function<Dimension, Dimension> CalculateThumbSize(final int i, final int i2, final int i3, final int i4, final boolean z) {
        return new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return HtmlContext.$r8$lambda$IDS2iDiaoey4r44KFrBNEkJs1Sk(z, i, i2, i3, i4, (Dimension) obj);
            }
        };
    }

    public static String m1078$r8$lambda$NQmNSWBFypYJJj_9YIdSy44Sh4(Integer num) {
        if (num.intValue() > 1) {
            return DataTypesUtils.NumberToString(num.intValue()) + " votes";
        }
        if (num.intValue() > 0) {
            return DataTypesUtils.NumberToString(num.intValue()) + " vote";
        }
        return "No votes";
    }

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
        sb.append(android.text.TextUtils.join(", ", arrayList));
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
