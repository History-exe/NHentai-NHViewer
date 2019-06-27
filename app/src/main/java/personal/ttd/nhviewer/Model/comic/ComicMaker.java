package personal.ttd.nhviewer.Model.comic;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import personal.ttd.nhviewer.Controller.fragment.SettingFragment;
import personal.ttd.nhviewer.Controller.fragment.TagFragment;
import personal.ttd.nhviewer.Model.ListReturnCallBack;
import personal.ttd.nhviewer.Model.Saver.Saver;
import personal.ttd.nhviewer.Model.Saver.SaverMaker;
import personal.ttd.nhviewer.Model.api.NHTranlator;
import personal.ttd.nhviewer.Model.tag.TagManager;

public class ComicMaker {

    public static void getComicById(String cid, Context context, ListReturnCallBack comicReturnCallback) {
        NHTranlator.Companion.getComicById(cid, context, comicReturnCallback);

    }

    public static void getComicListHistory(Context context, ListReturnCallBack comicListReturnCallback) {
        Saver saver = SaverMaker.getDefaultSaver();
        Collection history = saver.getHistory();

        comicListReturnCallback.onResponse(history.comicList);
    }

    public static void getComicListFavorite(ListReturnCallBack comicListReturnCallback) throws JSONException, IOException {
        Saver saver = SaverMaker.getDefaultSaver();
        Collection favorite = saver.getFavorite();

        comicListReturnCallback.onResponse(favorite.comicList);
    }

    ///default language = all
    public static void getComicListQuery(String query, int page, boolean sortPopular, Context context, ListReturnCallBack comicListReturnCallback, SharedPreferences pref) {
        Set<String> tags = TagManager.getTagAll(pref);
        StringBuilder tagQuery = new StringBuilder(" ");
        boolean enableTag = pref.getBoolean(TagFragment.KEY_PREF_TAG_SEARCH_ENABLE, true);

        if (enableTag)
            for (String t :
                    tags) {
                tagQuery.append(t).append(" ");
            }
        String storedLanguage = pref.getString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, "");
        String defaultLanguage = " Language:";

        if (storedLanguage.equals("All"))
            defaultLanguage = "";
        else
            defaultLanguage += storedLanguage;

        String url = NHTranlator.Companion.getSearchBaseUrl() + tagQuery.toString() + query + defaultLanguage;

        if (sortPopular)
            url += NHTranlator.Companion.getSuffixSortPopular();

        NHTranlator.Companion.getComicsBySite(url, String.valueOf(page), context, comicListReturnCallback);
    }

    ///default language = all
    public static void getComicListDefault(int page, boolean sortPopular, Context context, ListReturnCallBack comicListReturnCallback, SharedPreferences pref) {
        String language = pref.getString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, "");

        if (!language.equals("All") && !language.equals(""))
            getComicListByLanguage(language, page, sortPopular, context, comicListReturnCallback, pref);
        else
            getComiListAll(page, sortPopular, context, comicListReturnCallback, pref);
    }

    public static void getComicListByLanguage(String language, int page, boolean sortPopular, Context context, ListReturnCallBack comicListReturnCallback, SharedPreferences pref) {

        if (pref.getBoolean(TagFragment.KEY_PREF_TAG_HOME_ENABLE, true)) {
            getComicListQuery("", page, sortPopular, context, comicListReturnCallback, pref);
        } else {
            language += " ";
            String url = NHTranlator.Companion.getSearchBaseUrl() + "Language:" + language.toLowerCase();

            if (sortPopular)
                url += NHTranlator.Companion.getSuffixSortPopular();

            NHTranlator.Companion.getComicsBySite(url, String.valueOf(page), context, comicListReturnCallback);
        }
    }

    public static void getComiListAll(int page, boolean sortPopular, Context context, ListReturnCallBack comicListReturnCallback, SharedPreferences pref) {
        Set<String> tags = TagManager.getTagAll(pref);
        String url = NHTranlator.Companion.getBaseUrl();
        Boolean enableHomeTag = pref.getBoolean(TagFragment.KEY_PREF_TAG_HOME_ENABLE, true);

        if (enableHomeTag && !tags.isEmpty())
            getComicListQuery("", page, sortPopular, context, comicListReturnCallback, pref);
        else
            NHTranlator.Companion.getComicsBySite(url, String.valueOf(page), context, comicListReturnCallback);
    }

    //some json related methods

    public static ArrayList<Comic> getComicListByJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Comic> comics = new ArrayList<>();
        JSONObject obj;

        for (int i = 0; i < jsonArray.length(); i++) {
            obj = jsonArray.getJSONObject(i);
            comics.add(getComicByJSONObject(obj));
        }

        return comics;
    }

    public static Comic getComicByJSONObject(JSONObject jsonObject) throws JSONException {
        Comic c = new Comic();

        c.setTitle(jsonObject.getString(Collection.COLUMN_TITLE));
        c.setThumbLink(jsonObject.getString(Collection.COLUMN_THUMB_LINK));
        c.setId(jsonObject.getString(Collection.COLUMN_ID));
        //c.setMid(c.getThumbLink().split("/")[c.getThumbLink().split("/").length - 2]);
        // TODO: 6/2/2019  thumblink may not be set, error will thrown: java.lang.ArrayIndexOutOfBoundsException: length=1; index=-1

        return c;
    }

}
