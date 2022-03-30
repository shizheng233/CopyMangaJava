package com.shicheeng.copymanga.json;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.ListBeanManga;
import com.shicheeng.copymanga.apiName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaRankJson {

    public static Collection<ListBeanManga> rankGet(int offset, String type) throws IOException {
        String url = apiName.mangaRank(Integer.toString(offset), type);
        Collection<ListBeanManga> collections = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("results");
            JsonArray array = jsonObject.getAsJsonArray("list");
            Log.i("MRJ_ARRAY",array+"");
            for (JsonElement element : array) {
                ListBeanManga bean = new ListBeanManga();
                JsonObject jsonObject1 = element.getAsJsonObject().getAsJsonObject("comic");
                String stringName = jsonObject1.get("name").getAsString();
                String stringAuthor;
                if (jsonObject1.get("author").getAsJsonArray().size() == 1) {
                    stringAuthor = jsonObject1.get("author").getAsJsonArray()
                            .get(0).getAsJsonObject().get("name").getAsString();
                } else {
                    stringAuthor = jsonObject1.get("author").getAsJsonArray()
                            .get(0).getAsJsonObject().get("name").getAsString() + "等";
                }
                Log.i("MRJ",stringAuthor);
                String urlImage = jsonObject1.get("cover").getAsString();
                String pathWord = jsonObject1.get("path_word").getAsString();
                bean.setPathWordManga(pathWord);
                bean.setUrlCoverManga(urlImage);
                bean.setAuthorManga(stringAuthor);
                bean.setNameManga(stringName);
                collections.add(bean);

            }
            return collections;
        }
    }

}
