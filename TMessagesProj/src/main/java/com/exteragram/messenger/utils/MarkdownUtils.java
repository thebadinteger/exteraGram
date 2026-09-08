package com.exteragram.messenger.utils;

import android.text.TextUtils;
import com.google.android.gms.cast.HlsSegmentFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_iv;

public abstract class MarkdownUtils {
    private static final String[] MARKDOWN_TEXT_EXTENSIONS = {"txt", "text"};
    private static final String[] MARKDOWN_MIME_PREFIXES = {"text/plain", "text/x-diff", "text/x-patch", "text/csv", "text/xml", "text/yaml", "text/x-yaml", "text/css", "text/javascript", "application/json", "application/ld+json", "application/json5", "application/xml", "application/yaml", "application/x-yaml", "application/javascript", "application/x-javascript", "application/x-sh"};
    private static final HashMap<String, String> PREFORMATTED_EXTENSION_LANGUAGES = new HashMap<>();
    private static final HashMap<String, String> PREFORMATTED_FILENAMES = new HashMap<>();

    static {
        addLanguage("plain", "log");
        addLanguage("diff", "diff", "patch");
        addLanguage("json", "json", "webmanifest");
        addLanguage("json5", "json5");
        addLanguage("xml", "xml", "rss", "atom");
        addLanguage("svg", "svg");
        addLanguage("html", "html", "htm", "xhtml");
        addLanguage("css", "css");
        addLanguage("scss", "scss");
        addLanguage("sass", "sass");
        addLanguage("less", "less");
        addLanguage("javascript", "js", "mjs", "cjs");
        addLanguage("jsx", "jsx");
        addLanguage("typescript", HlsSegmentFormat.TS);
        addLanguage("tsx", "tsx");
        addLanguage("java", "java");
        addLanguage("kotlin", "kt", "kts");
        addLanguage("gradle", "gradle");
        addLanguage("groovy", "groovy");
        addLanguage("python", "py", "pyw", "plugin");
        addLanguage("bash", "sh", "bash", "zsh", "fish", "shell");
        addLanguage("powershell", "ps1", "psm1", "psd1");
        addLanguage("batch", "bat", "cmd");
        addLanguage("sql", "sql");
        addLanguage("yaml", "yaml", "yml");
        addLanguage("ini", "ini", "toml", "properties", "props", "conf", "cfg", "config", "env", "dotenv");
        addLanguage("csv", "csv", "tsv");
        addLanguage("docker", "dockerfile");
        addLanguage("makefile", "make", "mk", "mak");
        addLanguage("cmake", "cmake");
        addLanguage("go", "go");
        addLanguage("rust", "rs");
        addLanguage("swift", "swift");
        addLanguage("dart", "dart");
        addLanguage("php", "php", "phtml");
        addLanguage("ruby", "rb", "gemspec");
        addLanguage("c", "c");
        addLanguage("cpp", "h", "hh", "hpp", "hxx", "cpp", "cc", "cxx");
        addLanguage("csharp", "cs");
        addLanguage("fsharp", "fs", "fsx");
        addLanguage("visual-basic", "vb", "vba");
        addLanguage("lua", "lua");
        addLanguage("perl", "pl", "pm");
        addLanguage("r", "r");
        addLanguage("scala", "scala");
        addLanguage("haskell", "hs");
        addLanguage("elixir", "ex", "exs");
        addLanguage("erlang", "erl", "hrl");
        addLanguage("protobuf", "proto", "protobuf");
        addLanguage("graphql", "graphql", "gql");
        addLanguage("glsl", "glsl", "vert", "frag", "geom", "comp");
        addLanguage("http", "http");
        addFilename("docker", "Dockerfile");
        addFilename("makefile", "Makefile", "GNUmakefile");
        addFilename("cmake", "CMakeLists.txt");
        addFilename("git", ".gitignore", ".gitattributes", ".gitmodules");
        addFilename("docker", ".dockerignore");
        addFilename("ini", ".editorconfig", ".env");
    }

    public static boolean isExteraMarkdown(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        return isExteraMarkdownExtension(messageObject.getExtension()) || isExteraMarkdownMime(messageObject.getMimeType()) || !TextUtils.isEmpty(getPreformattedLanguage(getDocumentFileName(messageObject.getDocument()), messageObject.getExtension(), messageObject.getMimeType()));
    }

    public static boolean isExteraMarkdownExtension(String str) {
        if (isMarkdownTextExtension(str)) {
            return true;
        }
        return !TextUtils.isEmpty(getPreformattedLanguage(null, str, null));
    }

    public static boolean isExteraMarkdownMime(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String lowerCase = str.toLowerCase(Locale.ROOT);
        for (String str2 : MARKDOWN_MIME_PREFIXES) {
            if (lowerCase.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }

    public static String getDocumentFileName(TLRPC.Document document) {
        ArrayList<TLRPC.DocumentAttribute> arrayList;
        if (document != null && (arrayList = document.attributes) != null) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                TLRPC.DocumentAttribute documentAttribute = arrayList.get(i);
                i++;
                TLRPC.DocumentAttribute documentAttribute2 = documentAttribute;
                if (documentAttribute2 instanceof TLRPC.TL_documentAttributeFilename) {
                    return documentAttribute2.file_name;
                }
            }
        }
        return null;
    }

    public static String getPreformattedLanguage(String str, String str2, String str3) {
        String preformattedLanguageByFileName = getPreformattedLanguageByFileName(str);
        if (!TextUtils.isEmpty(preformattedLanguageByFileName)) {
            return preformattedLanguageByFileName;
        }
        String strNormalizeExtension = normalizeExtension(str2);
        if (TextUtils.isEmpty(strNormalizeExtension)) {
            strNormalizeExtension = getExtensionFromFileName(str);
        }
        String str4 = PREFORMATTED_EXTENSION_LANGUAGES.get(strNormalizeExtension);
        if (!TextUtils.isEmpty(str4)) {
            return str4;
        }
        if (!TextUtils.isEmpty(str3)) {
            String lowerCase = str3.toLowerCase(Locale.ROOT);
            if (lowerCase.startsWith("text/x-diff") || lowerCase.startsWith("text/x-patch")) {
                return "diff";
            }
            if (lowerCase.startsWith("text/csv")) {
                return "csv";
            }
            if (lowerCase.startsWith("text/xml") || lowerCase.startsWith("application/xml")) {
                return "xml";
            }
            if (lowerCase.startsWith("application/json5")) {
                return "json5";
            }
            if (lowerCase.startsWith("application/json") || lowerCase.startsWith("application/ld+json")) {
                return "json";
            }
            if (lowerCase.startsWith("text/yaml") || lowerCase.startsWith("text/x-yaml") || lowerCase.startsWith("application/yaml") || lowerCase.startsWith("application/x-yaml")) {
                return "yaml";
            }
            if (lowerCase.startsWith("text/css")) {
                return "css";
            }
            if (lowerCase.startsWith("text/javascript") || lowerCase.startsWith("application/javascript") || lowerCase.startsWith("application/x-javascript")) {
                return "javascript";
            }
            if (lowerCase.startsWith("application/x-sh")) {
                return "bash";
            }
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return _UrlKt.FRAGMENT_ENCODE_SET;
    }

    public static void appendPreformattedBlocks(List<TL_iv.PageBlock> list, String str, String str2, int i) {
        int iLastIndexOf;
        int iMax = Math.max(1, i);
        if (TextUtils.isEmpty(str)) {
            TL_iv.pageBlockPreformatted pageblockpreformatted = new TL_iv.pageBlockPreformatted();
            pageblockpreformatted.text = plain(_UrlKt.FRAGMENT_ENCODE_SET);
            if (str2 == null) {
                str2 = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            pageblockpreformatted.language = str2;
            list.add(pageblockpreformatted);
            return;
        }
        int i2 = 0;
        while (i2 < str.length()) {
            int iMin = Math.min(str.length(), i2 + iMax);
            if (iMin < str.length() && (iLastIndexOf = str.lastIndexOf(10, iMin - 1)) > i2) {
                iMin = iLastIndexOf + 1;
            }
            TL_iv.pageBlockPreformatted pageblockpreformatted2 = new TL_iv.pageBlockPreformatted();
            pageblockpreformatted2.text = plain(str.substring(i2, iMin));
            pageblockpreformatted2.language = str2 == null ? _UrlKt.FRAGMENT_ENCODE_SET : str2;
            list.add(pageblockpreformatted2);
            i2 = iMin;
        }
    }

    private static void addLanguage(String str, String... strArr) {
        for (String str2 : strArr) {
            PREFORMATTED_EXTENSION_LANGUAGES.put(str2, str);
        }
    }

    private static void addFilename(String str, String... strArr) {
        for (String str2 : strArr) {
            PREFORMATTED_FILENAMES.put(str2.toLowerCase(Locale.ROOT), str);
        }
    }

    private static boolean isMarkdownTextExtension(String str) {
        String strNormalizeExtension = normalizeExtension(str);
        for (String str2 : MARKDOWN_TEXT_EXTENSIONS) {
            if (str2.equals(strNormalizeExtension)) {
                return true;
            }
        }
        return false;
    }

    private static String getPreformattedLanguageByFileName(String str) {
        if (TextUtils.isEmpty(str)) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String lowerCase = getBaseName(str).toLowerCase(Locale.ROOT);
        String str2 = PREFORMATTED_FILENAMES.get(lowerCase);
        if (!TextUtils.isEmpty(str2)) {
            return str2;
        }
        if (lowerCase.startsWith("dockerfile.")) {
            return "docker";
        }
        if (lowerCase.startsWith("makefile.")) {
            return "makefile";
        }
        return lowerCase.startsWith(".env.") ? "ini" : _UrlKt.FRAGMENT_ENCODE_SET;
    }

    private static String getExtensionFromFileName(String str) {
        String baseName;
        int iLastIndexOf;
        return (!TextUtils.isEmpty(str) && (iLastIndexOf = (baseName = getBaseName(str)).lastIndexOf(46)) > 0 && iLastIndexOf < baseName.length() + (-1)) ? normalizeExtension(baseName.substring(iLastIndexOf + 1)) : _UrlKt.FRAGMENT_ENCODE_SET;
    }

    private static String getBaseName(String str) {
        int iMax = Math.max(str.lastIndexOf(47), str.lastIndexOf(92));
        return iMax >= 0 ? str.substring(iMax + 1) : str;
    }

    private static String normalizeExtension(String str) {
        if (TextUtils.isEmpty(str)) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String strTrim = str.trim();
        while (strTrim.startsWith(".")) {
            strTrim = strTrim.substring(1);
        }
        return strTrim.toLowerCase(Locale.ROOT);
    }

    private static TL_iv.RichText plain(String str) {
        TL_iv.textPlain textplain = new TL_iv.textPlain();
        if (str == null) {
            str = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        textplain.text = str;
        return textplain;
    }
}
