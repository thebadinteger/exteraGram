package com.exteragram.messenger.export.api;

public class ApiWrap$TextPart {
    public String additional;
    public String text;
    public Type type = Type.Text;

    public enum Type {
        Text,
        Unknown,
        Mention,
        Hashtag,
        BotCommand,
        Url,
        Email,
        Bold,
        Italic,
        Code,
        Pre,
        TextUrl,
        MentionName,
        Phone,
        Cashtag,
        Underline,
        Strike,
        Blockquote,
        BankCard,
        Spoiler,
        CustomEmoji
    }

    public static String UnavailableEmoji() {
        return "(unavailable)";
    }
}
