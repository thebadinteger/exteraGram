package androidx.room.util;
public class StringUtil {
    public static StringBuilder newStringBuilder() { return new StringBuilder(); }
    public static void appendPlaceholders(StringBuilder sb, int count) {
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(",");
            sb.append("?");
        }
    }
}
