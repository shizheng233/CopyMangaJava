package com.shicheeng.copymanga.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.ListBeanManga;
import com.shicheeng.copymanga.apiName;
import com.shicheeng.copymanga.data.MangaSortBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaSortJson {

    public static List<MangaSortBean> getSort() throws IOException {
        List<MangaSortBean> list = new ArrayList<>();
        String url = apiName.mangaSortUrl;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject()
                    .getAsJsonObject("results");
            JsonArray array = jsonObject.get("theme").getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                MangaSortBean sortBean = new MangaSortBean();
                String stringText = array.get(i).getAsJsonObject().get("name").getAsString();
                String stringPath = array.get(i).getAsJsonObject().get("path_word").getAsString();
                sortBean.setPathName(stringText);
                sortBean.setPathWord(stringPath);
                list.add(sortBean);
            }
            MangaSortBean bean = new MangaSortBean();
            bean.setPathWord("all");
            bean.setPathName("全部");
            list.add(0,bean);
            return list;
        }
    }

    public static List<MangaSortBean> getOrder() {
        List<MangaSortBean> list = new ArrayList<>();
        MangaSortBean bean_1 = new MangaSortBean();
        bean_1.setPathName("更新時間");
        bean_1.setPathWord("datetime_updated");
        list.add(bean_1);
        MangaSortBean bean_2 = new MangaSortBean();
        bean_2.setPathName("熱度");
        bean_2.setPathWord("-popular");
        list.add(bean_2);
        return list;
    }

    public static Collection<ListBeanManga> getFilterData(int offset, String theme, String order)
            throws IOException {
        String url = apiName.mangaFilterApi(offset, order, theme);
        Collection<ListBeanManga> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .removeHeader(KeyWordSwap.USER_AGENT_WORD)
                .addHeader(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = Objects.requireNonNull(response.body()).string();
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject()
                    .getAsJsonObject("results");
            JsonArray array = jsonObject.get("list").getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonObject object = array.get(i).getAsJsonObject();
                JsonArray authorArray = object.get("author").getAsJsonArray();
                ListBeanManga listBeanManga = new ListBeanManga();
                listBeanManga.setNameManga(object.get("name").getAsString());
                listBeanManga.setUrlCoverManga(object.get("cover").getAsString());
                listBeanManga.setPathWordManga(object.get("path_word").getAsString());
                if (authorArray.size() == 1) {
                    listBeanManga.setAuthorManga(authorArray.get(0)
                            .getAsJsonObject().get("name").getAsString());
                } else {
                    listBeanManga.setAuthorManga(authorArray.get(0)
                            .getAsJsonObject().get("name").getAsString() + "等");
                }
                list.add(listBeanManga);
            }
            return list;
        }

    }

}
