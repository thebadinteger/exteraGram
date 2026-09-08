package org.telegram.ui.web;

import android.text.TextUtils;
import com.exteragram.messenger.ExteraConfig;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

public class SearchEngine {
    private static ArrayList<SearchEngine> searchEngines;
    public final String autocomplete_url;
    public final String homepage;
    public final String name;
    public final String privacy_policy_url;
    public final String search_url;

    public SearchEngine(String str, String str2, String str3, String str4, String str5) {
        this.name = str;
        this.homepage = str2;
        this.search_url = str3;
        this.autocomplete_url = str4;
        this.privacy_policy_url = str5;
    }

    public SearchEngine(String str, String str2, String str3, String str4) {
        this.name = str;
        this.homepage = null;
        this.search_url = str2;
        this.autocomplete_url = str3;
        this.privacy_policy_url = str4;
    }

    public String getSearchURL(String str) {
        if (this.search_url == null) {
            return null;
        }
        return this.search_url + URLEncoder.encode(str);
    }

    public String getAutocompleteURL(String str) {
        if (this.autocomplete_url == null) {
            return null;
        }
        return this.autocomplete_url + URLEncoder.encode(str);
    }

    public String getHomepage() {
        return this.homepage;
    }

    public ArrayList<String> extractSuggestions(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(str).getJSONArray(1);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.getString(i));
            }
        } catch (Exception e) {
            FileLog.e(e);
            try {
                JSONArray jSONArray2 = new JSONObject(str).getJSONObject("gossip").getJSONArray("results");
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    arrayList.add(jSONArray2.getJSONObject(i2).getString("key"));
                }
            } catch (Exception e2) {
                FileLog.e(e2);
                try {
                    JSONArray jSONArray3 = new JSONArray(str);
                    for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                        String string = jSONArray3.getJSONObject(i3).getString("phrase");
                        if (!TextUtils.isEmpty(string)) {
                            arrayList.add(string);
                        }
                    }
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
        }
        return arrayList;
    }

    public static ArrayList<SearchEngine> getSearchEngines() {
        if (searchEngines == null) {
            searchEngines = new ArrayList<>();
            int i = 1;
            while (true) {
                String strNullable = nullable(LocaleController.getString("SearchEngine" + i + "Name"));
                if (strNullable == null) {
                    break;
                }
                searchEngines.add(new SearchEngine(strNullable, nullable(LocaleController.getString("SearchEngine" + i + "SearchURL")), nullable(LocaleController.getString("SearchEngine" + i + "AutocompleteURL")), nullable(LocaleController.getString("SearchEngine" + i + "PrivacyPolicyURL"))));
                i++;
            }
            searchEngines.add(1, ExteraConfig.getYandexSearchEngine());
        }
        return searchEngines;
    }

    private static String nullable(String str) {
        if (str == null || str.startsWith("LOC_ERR") || "reserved".equals(str)) {
            return null;
        }
        return str;
    }

    public static SearchEngine getCurrent() {
        ArrayList<SearchEngine> searchEngines2 = getSearchEngines();
        if (searchEngines2.isEmpty()) {
            return new SearchEngine("Google", "https://www.google.com/search?q=", "https://suggestqueries.google.com/complete/search?client=chrome&amp;q=", "https://policies.google.com/privacy");
        }
        return searchEngines2.get(Utilities.clamp(SharedConfig.searchEngineType, searchEngines2.size() - 1, 0));
    }
}
