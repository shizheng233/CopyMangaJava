package com.shicheeng.copymanga.json;

import com.google.gson.JsonArray;
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

public class SearchMangaJson {

    public static Collection<ListBeanManga> toGetManga(String name) throws IOException {
        String url = apiName.mangaSearchApi(name);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Collection<ListBeanManga> mangas = new ArrayList<>();
            String json = Objects.requireNonNull(response.body()).string();
            JsonObject object1 = JsonParser.parseString(json).getAsJsonObject()
                    .get("results").getAsJsonObject();
            JsonArray array1 = object1.getAsJsonArray("list");
            for (int i = 0; i < array1.size(); i++) {
                ListBeanManga manga = new ListBeanManga();
                JsonObject object2 = array1.get(i).getAsJsonObject();
                manga.setNameManga(object2.get("name").getAsString());
                manga.setPathWordManga(object2.get("path_word").getAsString());
                manga.setUrlCoverManga(object2.get("cover").getAsString());
                String author;
                JsonArray array = object2.get("author").getAsJsonArray();
                if (array.size() == 1) {
                    author = array.get(0).getAsJsonObject().get("name").getAsString();
                } else {
                    author = array.get(0).getAsJsonObject().get("name").getAsString() + " 等";
                }
                manga.setAuthorManga(author);
                mangas.add(manga);

            }
            return mangas;
        }
    }

}
