package com.exteragram.messenger.utils.text;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.text.style.URLSpan;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.ui.ColorRectSpan;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LinkifyPort;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.LaunchActivity;

public abstract class LocaleUtils {
    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("\\[([^]]+?)]\\(" + LinkifyPort.WEB_URL_REGEX + "\\)");
    private static final Pattern HEX_PATTERN = Pattern.compile("(?<![a-zA-Z0-9])#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})(?![a-zA-Z0-9])");

    public static String normalizeResourceLanguage(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String lowerCase = str.toLowerCase(Locale.US);
        if ("he".equals(lowerCase)) {
            return "iw";
        }
        return "no".equals(lowerCase) ? "nb" : lowerCase;
    }

    public static String normalizeResourceRegion(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        String strNormalizeResourceLanguage = normalizeResourceLanguage(str);
        String upperCase = str2.toUpperCase(Locale.US);
        if ("zh".equals(strNormalizeResourceLanguage)) {
            String str3 = "CN";
            if (!"HANS".equals(upperCase) && !"CN".equals(upperCase) && !"SG".equals(upperCase)) {
                str3 = "TW";
                if ("HANT".equals(upperCase) || "TW".equals(upperCase) || "HK".equals(upperCase) || "MO".equals(upperCase)) {
                }
            }
            return str3;
        }
        return upperCase;
    }

    public static String getActionBarTitle() {
        return getActionBarTitle(UserConfig.selectedAccount);
    }

    public static String getActionBarTitle(int i) {
        int titleText = ExteraConfig.getTitleText();
        if (titleText == 0) {
            return LocaleController.getString(R.string.exteraAppName);
        }
        if (titleText == 3) {
            return LocaleController.getString(R.string.FilterChats);
        }
        TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
        return (titleText != 1 || TextUtils.isEmpty(UserObject.getPublicUsername(currentUser))) ? UserObject.getFirstName(currentUser) : UserObject.getPublicUsername(currentUser);
    }

    public static CharSequence formatWithUsernames(CharSequence charSequence) {
        return formatWithUsernames(charSequence, LaunchActivity.getSafeLastFragment());
    }

    public static CharSequence formatWithUsernames(CharSequence charSequence, BaseFragment baseFragment) {
        return formatWithUsernames(charSequence, baseFragment, null);
    }

    public static CharSequence formatWithUsernames(CharSequence charSequence, final BaseFragment baseFragment, final Runnable runnable) {
        int i;
        URLSpan[] uRLSpanArr;
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        int i2 = -1;
        for (int i3 = 0; i3 < charSequence.length(); i3++) {
            if (charSequence.charAt(i3) == '@') {
                i2 = i3;
            } else if (i2 != -1 && ((i = i3 + 1) == charSequence.length() || (!Character.isLetterOrDigit(charSequence.charAt(i)) && charSequence.charAt(i) != '_'))) {
                if (i - i2 > 1 && ((uRLSpanArr = (URLSpan[]) spannableStringBuilder.getSpans(i2, i, URLSpan.class)) == null || uRLSpanArr.length <= 0)) {
                    final String string = charSequence.subSequence(i2, i).toString();
                    try {
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline(string) { 
                            @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                            public void onClick(View view) {
                                Runnable runnable2 = runnable;
                                if (runnable2 != null) {
                                    runnable2.run();
                                }
                                BaseFragment baseFragment2 = baseFragment;
                                if (baseFragment2 == null || baseFragment2.getMessagesController() == null) {
                                    return;
                                }
                                baseFragment.getMessagesController().openByUserName(string.substring(1), baseFragment, 1);
                            }
                        }, i2, i, 33);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                i2 = -1;
            }
        }
        return spannableStringBuilder;
    }

    public static CharSequence formatWithHtmlURLs(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        SpannableString spannableString = new SpannableString(charSequence);
        URLSpan[] uRLSpanArr = (URLSpan[]) spannableString.getSpans(0, charSequence.length(), URLSpan.class);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
        for (URLSpan uRLSpan : uRLSpanArr) {
            int spanStart = spannableStringBuilder.getSpanStart(uRLSpan);
            int spanEnd = spannableStringBuilder.getSpanEnd(uRLSpan);
            String url = uRLSpan.getURL();
            spannableStringBuilder.removeSpan(uRLSpan);
            spannableStringBuilder.setSpan(new URLSpanNoUnderline(url) { 
                @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                public void onClick(View view) {
                    super.onClick(view);
                }
            }, spanStart, spanEnd, 33);
        }
        return spannableStringBuilder;
    }

    public static CharSequence formatWithURLs(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        Matcher matcher = LinkifyPort.WEB_URL.matcher(charSequence);
        while (matcher.find()) {
            try {
                spannableStringBuilder.setSpan(new URLSpanNoUnderline(ensureUrlHasHttps(matcher.group(0))) { 
                    @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                    public void onClick(View view) {
                        super.onClick(view);
                    }
                }, matcher.start(), matcher.end(), 33);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return spannableStringBuilder;
    }

    public static CharSequence fullyFormatText(CharSequence charSequence) {
        return fullyFormatText(charSequence, null, null);
    }

    public static CharSequence fullyFormatText(CharSequence charSequence, BaseFragment baseFragment, Runnable runnable) {
        CharSequence withUsernames;
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        CharSequence[] charSequenceArr = {formatWithURLs(charSequence)};
        parseMarkdownLinks(charSequenceArr, runnable);
        CharSequence charSequence2 = charSequenceArr[0];
        if (baseFragment != null && runnable != null) {
            withUsernames = formatWithUsernames(charSequence2, baseFragment, runnable);
        } else {
            withUsernames = formatWithUsernames(charSequence2);
        }
        return AndroidUtilities.replaceTags(withUsernames);
    }

    public static CharSequence fromHtml(String str) {
        return new SpannableString(Html.fromHtml(str, 0));
    }

    public static String getAppName() {
        try {
            return ApplicationLoader.applicationContext.getString(R.string.exteraAppName);
        } catch (Exception unused) {
            return "exteraGram";
        }
    }

    public static void parseMarkdownLinks(CharSequence[] charSequenceArr) {
        parseMarkdownLinks(charSequenceArr, null);
    }

    public static void parseMarkdownLinks(CharSequence[] charSequenceArr, final Runnable runnable) {
        CharSequence charSequence;
        if (charSequenceArr == null || charSequenceArr.length == 0 || (charSequence = charSequenceArr[0]) == null) {
            return;
        }
        Spannable spannableNewSpannable = charSequence instanceof Spannable ? (Spannable) charSequence : Spannable.Factory.getInstance().newSpannable(charSequenceArr[0].toString());
        Matcher matcher = MARKDOWN_LINK_PATTERN.matcher(spannableNewSpannable);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        while (matcher.find()) {
            int iStart = matcher.start(1);
            int iEnd = matcher.end(1);
            if (iStart >= 0 && iEnd >= 0 && iStart <= iEnd && iEnd <= spannableNewSpannable.length()) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableNewSpannable.subSequence(iStart, iEnd));
                spannableStringBuilder.setSpan(new URLSpanReplacement(ensureUrlHasHttps(matcher.group(2))) { 
                    @Override // org.telegram.ui.Components.URLSpanReplacement, android.text.style.URLSpan, android.text.style.ClickableSpan
                    public void onClick(View view) {
                        Runnable runnable2 = runnable;
                        if (runnable2 != null) {
                            runnable2.run();
                        }
                        super.onClick(view);
                    }
                }, 0, spannableStringBuilder.length(), 33);
                arrayList.add(matcher.group(0));
                arrayList2.add(spannableStringBuilder);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        charSequenceArr[0] = TextUtils.replace(charSequenceArr[0], (String[]) arrayList.toArray(new String[0]), (CharSequence[]) arrayList2.toArray(new CharSequence[0]));
    }

    public static CharSequence applyNewSpan(CharSequence charSequence) {
        if (charSequence == null) {
            charSequence = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        spannableStringBuilder.append((CharSequence) "  d");
        FilterCreateActivity.NewSpan newSpan = new FilterCreateActivity.NewSpan(10.0f);
        newSpan.setText("NEW");
        newSpan.setTypeface(AndroidUtilities.bold());
        newSpan.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        newSpan.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        spannableStringBuilder.setSpan(newSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }

    public static String ensureUrlHasHttps(String str) {
        if (str == null) {
            return null;
        }
        return (!LinkifyPort.WEB_URL.matcher(str).matches() || str.startsWith("http://") || str.startsWith("https://") || str.contains("://")) ? str : "https://".concat(str);
    }

    public static boolean parseCustomEmojis(CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList) {
        TLRPC.TL_messageEntityTextUrl tL_messageEntityTextUrl;
        String str;
        int i;
        int i2;
        if (charSequence == null || arrayList == null || arrayList.isEmpty()) {
            return false;
        }
        boolean z = false;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            TLRPC.MessageEntity messageEntity = arrayList.get(i3);
            if ((messageEntity instanceof TLRPC.TL_messageEntityTextUrl) && (str = (tL_messageEntityTextUrl = (TLRPC.TL_messageEntityTextUrl) messageEntity).url) != null && str.startsWith("tg://emoji?id=") && (i = tL_messageEntityTextUrl.offset) >= 0 && (i2 = tL_messageEntityTextUrl.length) > 0 && i + i2 <= charSequence.length()) {
                try {
                    long j = Long.parseLong(tL_messageEntityTextUrl.url.substring(14));
                    int[] iArr = new int[1];
                    int i4 = tL_messageEntityTextUrl.offset;
                    ArrayList<Emoji.EmojiSpanRange> emojis = Emoji.parseEmojis(charSequence.subSequence(i4, tL_messageEntityTextUrl.length + i4).toString(), iArr);
                    if (iArr[0] > 0 && emojis.size() == 1) {
                        TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = new TLRPC.TL_messageEntityCustomEmoji();
                        tL_messageEntityCustomEmoji.document_id = j;
                        tL_messageEntityCustomEmoji.offset = tL_messageEntityTextUrl.offset;
                        tL_messageEntityCustomEmoji.length = tL_messageEntityTextUrl.length;
                        tL_messageEntityCustomEmoji.local = true;
                        arrayList.set(i3, tL_messageEntityCustomEmoji);
                        z = true;
                    }
                } catch (NumberFormatException e) {
                    FileLog.e("Failed to parse custom emoji id: " + tL_messageEntityTextUrl.url, e);
                }
            }
        }
        return z;
    }

    public static boolean isCustomEmojiOnlyLinkMessage(TLRPC.Message message) {
        ArrayList<TLRPC.MessageEntity> arrayList;
        if (message == null || (arrayList = message.entities) == null || arrayList.isEmpty() || !MessageObject.isMediaEmptyWebpage(message)) {
            return false;
        }
        boolean z = false;
        for (int i = 0; i < message.entities.size(); i++) {
            TLRPC.MessageEntity messageEntity = message.entities.get(i);
            if (messageEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                String str = ((TLRPC.TL_messageEntityTextUrl) messageEntity).url;
                if (str == null || !str.startsWith("tg://emoji?id=")) {
                    return false;
                }
            } else {
                if (messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                    if (((TLRPC.TL_messageEntityCustomEmoji) messageEntity).local) {
                    }
                } else if ((messageEntity instanceof TLRPC.TL_messageEntityUrl) || (messageEntity instanceof TLRPC.TL_messageEntityEmail)) {
                    return false;
                }
            }
            z = true;
        }
        return z;
    }

    public static void replaceCustomEmojis(int i, long j, ArrayList<TLRPC.MessageEntity> arrayList) {
        HashSet hashSet;
        TLRPC.ChatFull chatFull;
        TLRPC.TL_messages_stickerSet groupStickerSetById;
        if (arrayList == null || arrayList.isEmpty() || !canUseLocalPremiumEmojis(i) || j == UserConfig.getInstance(i).getClientUserId()) {
            return;
        }
        if (j >= 0 || (chatFull = MessagesController.getInstance(i).getChatFull(-j)) == null || chatFull.emojiset == null || (groupStickerSetById = MediaDataController.getInstance(i).getGroupStickerSetById(chatFull.emojiset)) == null || groupStickerSetById.documents == null) {
            hashSet = null;
        } else {
            hashSet = new HashSet();
            ArrayList<TLRPC.Document> arrayList2 = groupStickerSetById.documents;
            int size = arrayList2.size();
            int i2 = 0;
            while (i2 < size) {
                TLRPC.Document document = arrayList2.get(i2);
                i2++;
                hashSet.add(Long.valueOf(document.id));
            }
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            TLRPC.MessageEntity messageEntity = arrayList.get(i3);
            if (messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = (TLRPC.TL_messageEntityCustomEmoji) messageEntity;
                if (hashSet == null || !hashSet.contains(Long.valueOf(tL_messageEntityCustomEmoji.document_id))) {
                    TLRPC.Document documentFindDocument = tL_messageEntityCustomEmoji.document;
                    if (documentFindDocument == null) {
                        documentFindDocument = AnimatedEmojiDrawable.findDocument(i, tL_messageEntityCustomEmoji.document_id);
                    }
                    if (!MessageObject.isFreeEmoji(documentFindDocument)) {
                        arrayList.set(i3, toCustomEmojiLink(tL_messageEntityCustomEmoji));
                    }
                }
            }
        }
    }

    public static void replaceLocalCustomEmojis(ArrayList<TLRPC.MessageEntity> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.MessageEntity messageEntity = arrayList.get(i);
            if (messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = (TLRPC.TL_messageEntityCustomEmoji) messageEntity;
                if (tL_messageEntityCustomEmoji.local) {
                    arrayList.set(i, toCustomEmojiLink(tL_messageEntityCustomEmoji));
                }
            }
        }
    }

    public static ArrayList<TLRPC.MessageEntity> swapLocalCustomEmojis(TLRPC.Message message) {
        if (message != null && message.entities != null) {
            for (int i = 0; i < message.entities.size(); i++) {
                TLRPC.MessageEntity messageEntity = message.entities.get(i);
                if ((messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji) && ((TLRPC.TL_messageEntityCustomEmoji) messageEntity).local) {
                    ArrayList<TLRPC.MessageEntity> arrayList = message.entities;
                    ArrayList<TLRPC.MessageEntity> arrayList2 = new ArrayList<>(arrayList);
                    message.entities = arrayList2;
                    replaceLocalCustomEmojis(arrayList2);
                    return arrayList;
                }
            }
        }
        return null;
    }

    public static void restoreLocalCustomEmojis(TLRPC.Message message, ArrayList<TLRPC.MessageEntity> arrayList) {
        if (message == null || arrayList == null) {
            return;
        }
        message.entities = arrayList;
    }

    private static TLRPC.TL_messageEntityTextUrl toCustomEmojiLink(TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji) {
        TLRPC.TL_messageEntityTextUrl tL_messageEntityTextUrl = new TLRPC.TL_messageEntityTextUrl();
        tL_messageEntityTextUrl.offset = tL_messageEntityCustomEmoji.offset;
        tL_messageEntityTextUrl.length = tL_messageEntityCustomEmoji.length;
        tL_messageEntityTextUrl.url = "tg://emoji?id=" + tL_messageEntityCustomEmoji.document_id;
        return tL_messageEntityTextUrl;
    }

    public static boolean canUseLocalPremiumEmojis() {
        return canUseLocalPremiumEmojis(UserConfig.selectedAccount);
    }

    public static boolean canUseLocalPremiumEmojis(int i) {
        return RemoteUtils.getBooleanConfigValue("local_premium_emojis", false).booleanValue() && !UserConfig.getInstance(i).isPremium();
    }

    public static Spannable createCopySpan(BaseFragment baseFragment) {
        SpannableString spannableString = new SpannableString(" ");
        Drawable drawableMutate = ContextCompat.getDrawable(baseFragment.getParentActivity(), R.drawable.msg_copy).mutate();
        drawableMutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_undo_cancelColor, baseFragment.getResourceProvider()), PorterDuff.Mode.SRC_IN));
        drawableMutate.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
        spannableString.setSpan(new ImageSpan(drawableMutate, 0), 0, 1, 33);
        return spannableString;
    }

    public static CharSequence insertHexColorsPreview(CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence) && containsHash(charSequence)) {
            charSequence = charSequence instanceof Spannable ? (Spannable) charSequence : new SpannableString(charSequence);
            for (ColorRectSpan colorRectSpan : (ColorRectSpan[]) charSequence.getSpans(0, charSequence.length(), ColorRectSpan.class)) {
                charSequence.removeSpan(colorRectSpan);
            }
            Matcher matcher = HEX_PATTERN.matcher(charSequence);
            while (matcher.find()) {
                int iEnd = matcher.end();
                int i = iEnd - 1;
                if (!hasConflictingHexPreviewSpan(charSequence, i, iEnd)) {
                    try {
                        charSequence.setSpan(new ColorRectSpan(Color.parseColor(matcher.group())), i, iEnd, 33);
                    } catch (IllegalArgumentException unused) {
                        FileLog.e("Invalid HEX color: " + matcher.group());
                    }
                }
            }
        }
        return charSequence;
    }

    private static boolean hasConflictingHexPreviewSpan(Spannable spannable, int i, int i2) {
        for (Object obj : spannable.getSpans(i, i2, Object.class)) {
            if (!(obj instanceof ColorRectSpan)) {
                int spanStart = spannable.getSpanStart(obj);
                int spanEnd = spannable.getSpanEnd(obj);
                if (spanStart < i2 && spanEnd > i && (obj instanceof ReplacementSpan)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsHash(CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            if (charSequence.charAt(i) == '#') {
                return true;
            }
        }
        return false;
    }
}
