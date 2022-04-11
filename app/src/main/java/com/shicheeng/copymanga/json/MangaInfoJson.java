package com.shicheeng.copymanga.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.apiName;
import com.shicheeng.copymanga.data.ChipTextBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaInfoJson {

    /**
     * 获取漫画的信息并使用哈希表返还
     *
     * @param path_word 漫画的path word
     * @param context   Activity的上下文
     * @return 带有信息的哈希表
     * @throws IOException          错误一
     * @throws ExecutionException   错误二
     * @throws InterruptedException 错误三
     */
    public static HashMap<Integer, Object> getMangaInfo(String path_word, Context context)
            throws IOException, ExecutionException, InterruptedException {
        String url = apiName.mangaInfoAdapter(path_word);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String result = Objects.requireNonNull(response.body()).string();
            Log.i("SC_MI_!002", " " + result);
            JsonObject jsonObject_1 = JsonParser.parseString(result).getAsJsonObject()
                    .getAsJsonObject("results").getAsJsonObject("comic");
            Log.i("SC_MI_!001", " " + jsonObject_1);

            JsonArray array_1 = jsonObject_1.getAsJsonArray("author");
            JsonArray array_2 = jsonObject_1.getAsJsonArray("theme");

            String mangaName = jsonObject_1.get("name").getAsString();
            String mangaAlias;
            if (jsonObject_1.get("alias").isJsonNull()) {
                mangaAlias = "无别名";
            } else {
                mangaAlias = jsonObject_1.get("alias").getAsString();
            }

            List<ChipTextBean> mangaAuthor = new ArrayList<>();
            List<ChipTextBean> mangaThemes = new ArrayList<>();
            for (int i = 0; i < array_1.size(); i++) {
                ChipTextBean bean = new ChipTextBean();
                bean.setText(array_1.get(i).getAsJsonObject().get("name").getAsString());
                mangaAuthor.add(bean);
            }
            for (int a = 0; a < array_2.size(); a++) {
                ChipTextBean bean = new ChipTextBean();
                bean.setText(array_2.get(a).getAsJsonObject().get("name").getAsString());
                mangaThemes.add(bean);
            }
            String mangaDetail = jsonObject_1.get("brief").getAsString();
            String mangaCoverUrl = jsonObject_1.get("cover").getAsString();
            String mangaStatus = jsonObject_1.get("status").getAsJsonObject()
                    .get("display").getAsString();
            String mangaBuUid = jsonObject_1.get("uuid").getAsString();
            Bitmap bitmap = Glide.with(context).asBitmap().load(mangaCoverUrl).submit().get();
            HashMap<Integer, Object> mapWithInfo = new HashMap<>();
            mapWithInfo.put(1, mangaName);
            mapWithInfo.put(2, mangaAuthor);
            mapWithInfo.put(3, bitmap);
            mapWithInfo.put(4, mangaDetail);
            mapWithInfo.put(5, mangaStatus);
            mapWithInfo.put(6, mangaThemes);
            mapWithInfo.put(7, mangaAlias);
            mapWithInfo.put(8, mangaCoverUrl);
            mapWithInfo.put(9,mangaBuUid);
            return mapWithInfo;
        }
    }

    public static JsonObject getMangaContent(String path_word) throws IOException {
        String url = apiName.mangaChapterApi(path_word);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return JsonParser.parseString(response.body().string())
                    .getAsJsonObject()
                    .getAsJsonObject("results");
        }
    }

}
