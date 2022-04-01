package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.adapter.BannerMangaAdapter;
import com.shicheeng.copymanga.adapter.MangaRankOutsideAdapter;
import com.shicheeng.copymanga.data.BannerList;
import com.shicheeng.copymanga.data.DataBannerBean;
import com.shicheeng.copymanga.data.MangaRankOutsideBean;
import com.shicheeng.copymanga.json.MainBannerJson;
import com.shicheeng.copymanga.view.HeadLineView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView_Manga_1, recyclerView_Manga_2,
            recyclerView_Manga_3, recyclerView_Manga_4, recyclerView_Manga_5, recyclerView_Manga_6;
    private Thread thread;
    private RunOnMain workHandler;
    private NestedScrollView scrollView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化
        toolbar = findViewById(R.id.main_tool_bar);
        recyclerView_Manga_1 = findViewById(R.id.recycler_view_manga_1);
        recyclerView_Manga_2 = findViewById(R.id.recycler_view_manga_2);
        recyclerView_Manga_3 = findViewById(R.id.recycler_view_manga_3);
        recyclerView_Manga_4 = findViewById(R.id.recycler_view_manga_4);
        recyclerView_Manga_5 = findViewById(R.id.recycler_view_manga_5);
        recyclerView_Manga_6 = findViewById(R.id.recycler_view_manga_6);
        progressBar = findViewById(R.id.progress_loading);
        HeadLineView headLine_1 = findViewById(R.id.headline_1);
        HeadLineView headLine_2 = findViewById(R.id.headline_2);
        HeadLineView headLine_3 = findViewById(R.id.headline_3);
        HeadLineView headLine_4 = findViewById(R.id.headline_4);
        HeadLineView headLine_5 = findViewById(R.id.headline_5);
        scrollView = findViewById(R.id.nestedScrollView);

        //配置一堆
        LinearLayoutManager layoutManager_1 =
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager layoutManager_2 =
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager layoutManager_3 =
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager layoutManager_4 =
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager layoutManager_5 =
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager layoutManager_6 =
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView_Manga_1.setLayoutManager(layoutManager_1);
        recyclerView_Manga_2.setLayoutManager(layoutManager_2);
        recyclerView_Manga_3.setLayoutManager(layoutManager_3);
        recyclerView_Manga_4.setLayoutManager(layoutManager_4);
        recyclerView_Manga_5.setLayoutManager(layoutManager_5);
        recyclerView_Manga_6.setLayoutManager(layoutManager_6);


        headLine_1.setOnHeadClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MangaListActivity.class);
            intent.putExtra(KeyWordSwap.FLAG_, KeyWordSwap.FLAG_RECOMMEND);
            startActivity(intent);
            Log.i("SC_222", " " + "点击");
        });

        headLine_2.setOnHeadClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RankMangaActivity.class);
            startActivity(intent);
            Log.i("SC_222", " " + "点击");
        });

        headLine_4.setOnHeadClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MangaListActivity.class);
            intent.putExtra(KeyWordSwap.FLAG_, KeyWordSwap.FLAG_NEWEST);
            startActivity(intent);
            Log.i("SC_222", " " + "点击");
        });

        headLine_3.setOnHeadClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, HotMangaActivity.class);
            startActivity(intent);
            Log.i("SC_222", " " + "点击");
        });

        headLine_5.setOnHeadClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MangaListActivity.class);
            intent.putExtra(KeyWordSwap.FLAG_, KeyWordSwap.FLAG_FINISH);
            startActivity(intent);
            Log.i("SC_222", " " + "点击");
        });

        //连接线程
        workHandler = new RunOnMain(this, progressBar);
        workHandler.setRecyclerView_1(recyclerView_Manga_1);
        workHandler.setRecyclerView_2(recyclerView_Manga_2);
        workHandler.setRecyclerView_3(recyclerView_Manga_3);
        workHandler.setRecyclerView_4(recyclerView_Manga_4);
        workHandler.setRecyclerView_5(recyclerView_Manga_5);
        workHandler.setRecyclerView_6(recyclerView_Manga_6);

        //线程启动
        thread = new Thread(new MyRunnable());

        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.about_main) {
                Intent intent2 = new Intent();
                intent2.setClass(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                return true;
            }
            if (item.getItemId() == R.id.id_manga_search) {
                SearchView view = (SearchView) item.getActionView();

                return true;
            }

            return false;
        });

        progressBar.setVisibility(View.VISIBLE);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(thread);
        executorService.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem itemS = menu.findItem(R.id.id_manga_search);
        SearchView view = (SearchView) itemS.getActionView();
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        view.setSubmitButtonEnabled(true);
        ComponentName componentName = new ComponentName(this,SearchOutActivity.class);
        SearchableInfo searchableInfo = manager.getSearchableInfo(componentName);
        view.setSearchableInfo(searchableInfo);
        
        //view.setSearchableInfo();

        return super.onCreateOptionsMenu(menu);
    }

    //静态内部类用作方法的集合
    private static class jsonToRecIntegrator {


        static void setJsonToBanner(RecyclerView recyclerView_1, List<BannerList> list) {
            List<DataBannerBean> myList_1 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Log.i("SC_010", "" + list.size());
                DataBannerBean bannerBean = new DataBannerBean();
                JsonObject jsonObject_1 = list.get(i).getJsonObject(); //Banner组下面的各个jsonObject
                assert jsonObject_1 != null;
                bannerBean.setBannerBrief(jsonObject_1.get("brief").getAsString());
                bannerBean.setBannerImageUrl(jsonObject_1.get("cover").getAsString());
                bannerBean.setUuidManga(jsonObject_1.get("comic")
                        .getAsJsonObject().get("path_word").getAsString());
                myList_1.add(bannerBean);
            }
            BannerMangaAdapter adapter = new BannerMangaAdapter(myList_1);
            recyclerView_1.setAdapter(adapter);
            adapter.setOnItemClickListener(new BannerMangaAdapter.OnItemClickListener() {
                @Override
                public void OnItem(int position) {
                    Log.i("SC_016", " " + position);
                    Intent intent = new Intent();
                    intent.setClass(recyclerView_1.getContext(), MangaInfoActivity.class);
                    intent.putExtra(KeyWordSwap.PATH_WORD_TYPE, myList_1.get(position).getUuidManga());
                    Log.i("SC_!001", " " + myList_1.get(position).getUuidManga());
                    recyclerView_1.getContext().startActivity(intent);
                }
            });
        }

        /*
         * 将JSONArray与RecyclerView连接起来
         * @array JsonObject
         *
         * */
        static void setJsonToRec(RecyclerView recyclerView_2, JsonArray array) {
            List<ListBeanManga> myList_1 = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                ListBeanManga beanManga = new ListBeanManga();
                //推荐组下面的各个jsonObject，因为有第二个jsonOBj，所以需要再次获取一次
                JsonObject jsonObject_1 = array.get(i).getAsJsonObject().getAsJsonObject("comic");
                beanManga.setNameManga(jsonObject_1.get("name").getAsString());
                beanManga.setUrlCoverManga(jsonObject_1.get("cover").getAsString());
                beanManga.setPathWordManga(jsonObject_1.get("path_word").getAsString());
                //获取作者列表，判定是否大于一位作家
                JsonArray mangaAuthorList = jsonObject_1.get("author").getAsJsonArray();
                if (mangaAuthorList.size() > 1) {
                    beanManga.setAuthorManga(mangaAuthorList.get(0)
                            .getAsJsonObject()
                            .get("name")
                            .getAsString() + " 等");
                } else {
                    beanManga.setAuthorManga(mangaAuthorList.get(0)
                            .getAsJsonObject()
                            .get("name")
                            .getAsString());
                }
                myList_1.add(beanManga);
            }
            RecyclerViewMangaAdapter adapter_1 = new RecyclerViewMangaAdapter(myList_1);
            adapter_1.setOnItemClickListener(new RecyclerViewMangaAdapter.OnItemClickListener() {
                @Override
                public void onItem(int position) {
                    Log.i("SC_016", " " + position);
                    Intent intent = new Intent();
                    intent.setClass(recyclerView_2.getContext(), MangaInfoActivity.class);
                    intent.putExtra(KeyWordSwap.PATH_WORD_TYPE, myList_1.get(position).getPathWordManga());
                    Log.i("SC_!001", " " + myList_1.get(position).getPathWordManga());
                    recyclerView_2.getContext().startActivity(intent);
                }
            });
            recyclerView_2.setAdapter(adapter_1);

        }

        /*
         * 将JSONArray与RecyclerView连接起来(类型二)
         * @array JsonObject
         * */

        static void setJsonToRec2(RecyclerView rec_2, JsonArray array) {
            List<ListBeanManga> myList_1 = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                ListBeanManga beanManga = new ListBeanManga();
                //推荐组下面的各个jsonObject，因为有第二个jsonOBj，所以需要再次获取一次
                JsonObject jsonObject_1 = array.get(i).getAsJsonObject();
                beanManga.setNameManga(jsonObject_1.get("name").getAsString());
                beanManga.setUrlCoverManga(jsonObject_1.get("cover").getAsString());
                beanManga.setPathWordManga(jsonObject_1.get("path_word").getAsString());
                //获取作者列表，判定是否大于一位作家
                JsonArray mangaAuthorList = jsonObject_1.get("author").getAsJsonArray();
                if (mangaAuthorList.size() > 1) {
                    beanManga.setAuthorManga(mangaAuthorList.get(0)
                            .getAsJsonObject()
                            .get("name")
                            .getAsString() + " 等");
                } else {
                    beanManga.setAuthorManga(mangaAuthorList.get(0)
                            .getAsJsonObject()
                            .get("name")
                            .getAsString());
                }
                myList_1.add(beanManga);
            }
            RecyclerViewMangaAdapter adapter_2 = new RecyclerViewMangaAdapter(myList_1);
            adapter_2.setOnItemClickListener(new RecyclerViewMangaAdapter.OnItemClickListener() {
                @Override
                public void onItem(int position) {
                    Log.i("SC_014", " " + position);
                    Intent intent = new Intent();
                    intent.setClass(rec_2.getContext(), MangaInfoActivity.class);
                    intent.putExtra(KeyWordSwap.PATH_WORD_TYPE, myList_1.get(position).getPathWordManga());
                    Log.i("SC_!001", " " + myList_1.get(position).getPathWordManga());
                    rec_2.getContext().startActivity(intent);
                }
            });
            rec_2.setAdapter(adapter_2);
        }

        /*
         * 将哈希表和recyclerView连接起来
         * @map HashMap
         *
         * */
        static void setHashMapToRec3(RecyclerView rec_3, HashMap<Integer, JsonArray> map, String[] titles) {
            List<MangaRankOutsideBean> beans = new ArrayList<>();

            for (int u = 0; u < map.size(); u++) {
                MangaRankOutsideBean bean = new MangaRankOutsideBean();
                bean.setArrayOnMange(map.get(u));
                bean.setTitleMange(titles[u]);
                beans.add(bean);
            }
            MangaRankOutsideAdapter adapter_3 = new MangaRankOutsideAdapter(beans);
            rec_3.setAdapter(adapter_3);
        }


    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_RUN;
            try {
                message.obj = MainBannerJson.getMianList();
//                    Log.i("SC_FB_LINE", "+++++++++++++++++++++++++++++++++++++++++++++");
//                    Log.i("SC_FB_HANDLER", ":" + MainBannerJson.getMianList());
                workHandler.sendMessage(message);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_ERROR;
                workHandler.sendMessage(message1);
            }
        }
    }

    //内部类用作handler
    private class RunOnMain extends Handler {

        private final WeakReference<Activity> reference;
        private final ProgressBar bar;
        private final String[] titleStrings = new String[]{getString(R.string.day_rank)
                , getString(R.string.week_rank)
                , getString(R.string.month_rank)};
        private RecyclerView recyclerView_1, recyclerView_2,
                recyclerView_3, recyclerView_4, recyclerView_5, recyclerView_6;

        // 在构造方法中传入需持有的Activity实例
        public RunOnMain(Activity activity, ProgressBar bar) {
            // 使用WeakReference弱引用持有Activity实例
            reference = new WeakReference<Activity>(activity);
            this.bar = bar;
        }

        public void setRecyclerView_1(RecyclerView recyclerView_1) {
            this.recyclerView_1 = recyclerView_1;
        }

        public void setRecyclerView_3(RecyclerView recyclerView_3) {
            this.recyclerView_3 = recyclerView_3;
        }

        public void setRecyclerView_2(RecyclerView recyclerView_2) {
            this.recyclerView_2 = recyclerView_2;
        }

        public void setRecyclerView_4(RecyclerView recyclerView_4) {
            this.recyclerView_4 = recyclerView_4;
        }

        public void setRecyclerView_5(RecyclerView recyclerView_5) {
            this.recyclerView_5 = recyclerView_5;
        }

        public void setRecyclerView_6(RecyclerView recyclerView_6) {
            this.recyclerView_6 = recyclerView_6;
        }

        //UI操作
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_RUN) {
                JsonObject object_1 = (JsonObject) msg.obj;
                //资源传递
                List<BannerList> jsonArrayMainBanner = MainBannerJson.getBannerMain(object_1);
                HashMap<Integer, JsonArray> jsonArrayHash = MainBannerJson.getDayRankMain(object_1);
                JsonArray jsonArrayMainRec = MainBannerJson.getRecMain(object_1);
                JsonArray jsonArrayMainHot = MainBannerJson.getHotMain(object_1);
                JsonArray jsonArrayMainNew = MainBannerJson.getNewMain(object_1);
                JsonArray jsonArrayMainFinish = MainBannerJson.getFinishMain(object_1);
                //资源配置
                jsonToRecIntegrator.setJsonToBanner(recyclerView_1, jsonArrayMainBanner);
                jsonToRecIntegrator.setJsonToRec(recyclerView_2, jsonArrayMainRec);
                //Log.i("SC_011", "" + jsonArrayHash);
                jsonToRecIntegrator.setHashMapToRec3(recyclerView_3, jsonArrayHash, titleStrings);
                jsonToRecIntegrator.setJsonToRec(recyclerView_4, jsonArrayMainHot);
                jsonToRecIntegrator.setJsonToRec(recyclerView_5, jsonArrayMainNew);
                jsonToRecIntegrator.setJsonToRec2(recyclerView_6, jsonArrayMainFinish);

                bar.setVisibility(View.GONE);
            }
            if (msg.what == KeyWordSwap.HANDLER_ERROR) {
                thread.interrupt();
                Snackbar.make(scrollView, R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, view -> {
                            new Thread(new MyRunnable()).start();
                            bar.setVisibility(View.VISIBLE);
                        }).show();
                bar.setVisibility(View.GONE);
            }

        }


    }
}