package com.shicheeng.copymanga.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.apiName;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaRecommendJson {

    public static JsonArray mangaRecommendList(int index) throws IOException {

        String url = apiName.mangaRecommendApi(Integer.toString(index));
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD,KeyWordSwap.FAKE_USER_AGENT)
                .url(url).build();
        try(Response response = client.newCall(request).execute()) {
            return JsonParser.parseString(response.body().string()).getAsJsonObject()
                    .get("results")
                    .getAsJsonObject()
                    .getAsJsonArray("list");
        }
    }

}
