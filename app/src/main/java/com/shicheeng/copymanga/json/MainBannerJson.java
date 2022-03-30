package com.shicheeng.copymanga.json;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.apiName;
import com.shicheeng.copymanga.data.BannerList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainBannerJson {

    public static JsonObject getMianList() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(apiName.mangaMainPage)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String jsonData = response.body().string();

            return JsonParser.parseString(jsonData).getAsJsonObject().getAsJsonObject("results");
        }

    }

    public static List<BannerList> getBannerMain(JsonObject jsonObject) {

        JsonArray array1 = jsonObject.get("banners").getAsJsonArray();
        List<BannerList> bannerLists = new ArrayList<>();
        for (int i = 0; i < array1.size(); i++) {
            JsonElement element = array1.get(i).getAsJsonObject().get("type");
            if (element.getAsInt() == 1) {
                BannerList list = new BannerList();
                list.setJsonObject(array1.get(i).getAsJsonObject());
                bannerLists.add(list);
            }
        }
        return bannerLists;
    }

    public static JsonArray getRecMain(JsonObject jsonObject) {
        return jsonObject.get("recComics").getAsJsonObject().getAsJsonArray("list");

    }

    public static JsonArray getHotMain(JsonObject jsonObject) {
        return jsonObject.get("hotComics").getAsJsonArray();
    }

    public static JsonArray getNewMain(JsonObject jsonObject) {
        return jsonObject.get("newComics").getAsJsonArray();
    }

    public static JsonArray getFinishMain(JsonObject object) {
        return object.getAsJsonObject("finishComics").getAsJsonArray("list");
    }

    public static HashMap<Integer, JsonArray> getDayRankMain(JsonObject jsonObject) {
        HashMap<Integer, JsonArray> map = new HashMap<>();
        map.put(0, jsonObject.get("rankDayComics").getAsJsonObject().getAsJsonArray("list"));
        map.put(1, jsonObject.get("rankWeekComics").getAsJsonObject().getAsJsonArray("list"));
        map.put(2, jsonObject.get("rankMonthComics").getAsJsonObject().getAsJsonArray("list"));

        return map;

    }

}
