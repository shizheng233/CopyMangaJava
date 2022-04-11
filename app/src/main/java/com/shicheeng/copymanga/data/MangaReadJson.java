package com.shicheeng.copymanga.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.apiName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaReadJson {

    public static HashMap<Integer, String> getPicBitmap(String pathWord, String uuid)
            throws IOException, ExecutionException, InterruptedException {
        String url = apiName.mangaPhotoApi(pathWord, uuid);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            HashMap<Integer, String> hashMap = new HashMap<>();
            String json = Objects.requireNonNull(response.body()).string();
            JsonObject object = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("results");
            JsonArray array = object.get("chapter").getAsJsonObject().getAsJsonArray("contents");
            JsonArray array2 = object.get("chapter").getAsJsonObject().getAsJsonArray("words");
            for (int i = 0; i < array.size(); i++) {
                String url2 = array.get(i).getAsJsonObject().get("url").getAsString();
                int num = array2.get(i).getAsInt();
                hashMap.put(num, url2);
            }
            return hashMap;
        }

    }



    public static String getNextChapterName(String path_word, String nextUuid) throws IOException {
        String url = apiName.mangaPhotoApi(path_word, nextUuid);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            JsonObject object = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("results");

            return object.get("chapter").getAsJsonObject().get("name").getAsString();
        }
    }

    public static String getNextUUID(String pathWord, String uuid) throws IOException {
        String url = apiName.mangaPhotoApi(pathWord, uuid);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            JsonObject object = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("results");
            String uuid2;
            if (!object.get("chapter").getAsJsonObject().get("next").isJsonNull()) {
                uuid2 = object.get("chapter").getAsJsonObject().get("next").getAsString();
            } else {
                uuid2 = KeyWordSwap.NON_JSON;
            }

            return uuid2;
        }
    }

}
