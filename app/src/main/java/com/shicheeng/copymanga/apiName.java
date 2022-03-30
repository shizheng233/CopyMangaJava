package com.shicheeng.copymanga;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class apiName {
    //各api网址
    //TODO

    public static final String mangaMainPage = "https://api.copymanga.net/api/v3/h5/homeIndex?platform=3&amp;format=json";
    private static final String mangaSearch = " https://api.copymanga.net/api/v3/search/comic?format=json&limit=18&offset=0&platform=3&q=%s";
    private static final String mangaChapter = "https://api.copymanga.net/api/v3/comic/%s/group/default/chapters?limit=500&offset=0&platform=3";
    private static final String mangaPhoto = "https://api.copymanga.net/api/v3/comic/%s/chapter2/%s?platform=3";
    private static final String mangaCollect = " https://copymanga.net/api/v3/member/collect/comics?limit=50&offset=0&free_type=1&ordering=-datetime_modifier";
    private static final String mangaRecommend = "https://api.copymanga.net/api/v3/recs?pos=3200102&amp;limit=21&amp;offset=%s&amp;platform=3&amp;format=json";
    private static final String mangaTopRank = "https://api.copymanga.net/api/v3/ranks?limit=21&amp;offset=%s&amp;date_type=%s&amp;platform=3&amp;format=json";
    private static final String mangaInfoUrl = "https://api.copymanga.net/api/v3/comic2/%s?platform=3&amp;format=json";
    public static final String mangaSortUrl = "https://api.copymanga.net/api/v3/h5/filterIndex/comic/tags?platform=3&amp;format=json";
    private static final String mangaFilterUrl = "https://api.copymanga.net/api/v3/comics?limit=21&amp;offset=%s&amp;ordering=%s&amp;theme=%s&amp;platform=3&amp;format=json";
    private static final String mangaNewest = "https://api.copymanga.net/api/v3/update/newest?limit=21&amp;offset=%s&amp;platform=3&amp;format=json";
    private static final String mangaFinish = "https://api.copymanga.net/api/v3/comics?limit=21&amp;offset=%s&amp;top=finish&amp;platform=3&amp;format=json";

    /**
     * 漫画搜索的api
     *
     * @param name 漫画名称
     * @return 总api
     */
    public static String mangaSearchApi(String name) {

        return String.format(mangaSearch, name);
    }

    /**
     *
     * 获取章节
     * @param chapter 其实是传入path word
     * @return String
     */
    public static String mangaChapterApi(String chapter) {

        return String.format(mangaChapter, chapter);
    }

    /**
     * 漫画内容获取
     * @param path_word 漫画的path word
     * @param uuid 漫画的uuid
     * @return 总API
     */
    public static String mangaPhotoApi(String path_word, String uuid) {

        return String.format(mangaPhoto, path_word, uuid);
    }

    /**
     * 获取推荐的漫画列表
     * @param offset 页数
     * @return 总api
     */
    public static String mangaRecommendApi(String offset) {
        return String.format(mangaRecommend, offset);
    }


    public static String mangaRank(String offset, String type) {
        return String.format(mangaTopRank, offset, type);
    }

    /**
     *
     * 获取漫画的详细信息
     * @param path_word 漫画的pathWord
     * @return 所有api
     */
    public static String mangaInfoAdapter(String path_word) {
        return String.format(mangaInfoUrl, path_word);
    }

    /**
     * 获取最新漫画
     * @param offset 页数
     * @return 整个api
     */
    public static String mangaNewestApi(int offset){
        return String.format(mangaNewest, offset);
    }

    /**
     * 获取已完结漫画
     * @param offset 页数
     * @return 整个api
     */
    public static String mangaFinishApi(int offset){
        return String.format(mangaFinish,offset);
    }

    /**
     * 过滤漫画
     * @param offset 页数
     * @param order 排列顺序
     * @param theme 类型
     * @return 整个api
     */
    public static String mangaFilterApi(int offset,String order,String theme){
        return String.format(mangaFilterUrl,offset,order,theme);
    }

    /**
     * 用户收藏漫画获取（需要设置headers['authorization']）:
     * @String authorization 传入authorization
     * @return 返回json
      */
    public static String mangaCollectApi(String authorization) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mangaCollect)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                .addHeader("authorization", authorization)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }
}