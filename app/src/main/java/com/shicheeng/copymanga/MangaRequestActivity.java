package com.shicheeng.copymanga;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaRequestActivity {


    /**
     * 获取最新的漫画列表
     *
     * @param offset 偏移量
     * @return list 漫画的列表
     * @throws IOException 错误
     */
    public static Collection<ListBeanManga> getNewestMangaTotal(int offset) throws IOException {
        Collection<ListBeanManga> list = new ArrayList<>();
        String url = apiName.mangaNewestApi(offset);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String jsonOrigin = Objects.requireNonNull(response.body()).string();
            JsonObject jsonObject_1 = JsonParser.parseString(jsonOrigin).getAsJsonObject();
            JsonArray array = jsonObject_1.get("results").getAsJsonObject()
                    .get("list")
                    .getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                ListBeanManga mangas = new ListBeanManga();
                JsonObject object_2 = array.get(i).getAsJsonObject().getAsJsonObject("comic");
                JsonArray array_2 = object_2.getAsJsonArray("author");
                mangas.setNameManga(object_2.get("name").getAsString());
                mangas.setPathWordManga(object_2.get("path_word").getAsString());
                mangas.setUrlCoverManga(object_2.get("cover").getAsString());
                if (array_2.size() == 1) {
                    mangas.setAuthorManga(array_2.get(0).getAsJsonObject()
                            .get("name").getAsString());
                } else {
                    mangas.setAuthorManga(array_2.get(0).getAsJsonObject()
                            .get("name").getAsString() + " 等");
                }
                list.add(mangas);
            }
            return list;
        }
    }

    /**
     * 获取所有完结的列表
     *
     * @param offset 页数
     * @return List
     * @throws IOException 错误
     */
    public static Collection<ListBeanManga> getFinishMangaTotal(int offset) throws IOException {
        Collection<ListBeanManga> list = new ArrayList<>();
        String url = apiName.mangaFinishApi(offset);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url)
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String jsonOrigin = Objects.requireNonNull(response.body()).string();
            JsonObject jsonObject_1 = JsonParser.parseString(jsonOrigin).getAsJsonObject();
            JsonArray array = jsonObject_1.get("results").getAsJsonObject()
                    .get("list")
                    .getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                ListBeanManga mangas = new ListBeanManga();
                JsonObject object_2 = array.get(i).getAsJsonObject();
                JsonArray array_2 = object_2.getAsJsonArray("author");
                mangas.setNameManga(object_2.get("name").getAsString());
                mangas.setPathWordManga(object_2.get("path_word").getAsString());
                mangas.setUrlCoverManga(object_2.get("cover").getAsString());
                if (array_2.size() == 1) {
                    mangas.setAuthorManga(array_2.get(0).getAsJsonObject()
                            .get("name").getAsString());
                } else {
                    mangas.setAuthorManga(array_2.get(0).getAsJsonObject()
                            .get("name").getAsString() + " 等");
                }
                list.add(mangas);
            }
            return list;
        }
    }

    /**
     * 获取漫画的推荐数据
     * @param offset 偏移
     * @return 数据
     * @throws IOException 错误一
     */

    public static Collection<ListBeanManga> listDataRec(int offset) throws IOException {
        Log.i("TAG_IIIII",""+offset);
        Collection<ListBeanManga> mangas = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(apiName.mangaRecommendApi(Integer.toString(offset)))
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String jsonMangaResult = response.body().string();
            Log.i("TAG_IIIIIRS",""+jsonMangaResult);
            JsonObject jsonObjectManga_1 = JsonParser
                    .parseString(jsonMangaResult)
                    .getAsJsonObject()
                    .getAsJsonObject("results");
            JsonArray mangaList_1 = jsonObjectManga_1.getAsJsonArray("list");
            Log.i("SC___@1"," "+mangaList_1.size());

            for (int i = 0; i < mangaList_1.size(); i++) {
                ListBeanManga beanManga_1 = new ListBeanManga();
                JsonObject jsonObjectManga_2 = mangaList_1.get(i)
                        .getAsJsonObject()
                        .getAsJsonObject("comic");
                beanManga_1.setNameManga(jsonObjectManga_2.get("name").getAsString());
                Log.i("SC___@"," "+jsonObjectManga_2.get("name").getAsString());
                beanManga_1.setUrlCoverManga(jsonObjectManga_2.get("cover").getAsString());
                beanManga_1.setPathWordManga(jsonObjectManga_2.get("path_word").getAsString());
                JsonArray mangaAuthorList = jsonObjectManga_2.get("author").getAsJsonArray();
                if (mangaAuthorList.size() > 1) {
                    beanManga_1.setAuthorManga(mangaAuthorList.get(0)
                            .getAsJsonObject()
                            .get("name")
                            .getAsString() + " 等");
                } else {
                    beanManga_1.setAuthorManga(mangaAuthorList.get(0)
                            .getAsJsonObject()
                            .get("name")
                            .getAsString());
                }
                mangas.add(beanManga_1);

            }
            return mangas;
        }


    }

}
