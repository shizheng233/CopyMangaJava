package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.adapter.MangaListAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MangaListActivity extends AppCompatActivity {

    private final List<ListBeanManga> mangaListAll = new ArrayList<>();
    MangaListAdapter adapter = new MangaListAdapter(mangaListAll);
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private MyHandler myHandler;
    private LinearProgressIndicator indicator;
    private int offset = 0;
    private int whatUp;
    private String flag;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_list);
        //控件配置
        toolbar = findViewById(R.id.manga_list_toolbar);
        recyclerView = findViewById(R.id.manga_list_total_rec);
        indicator = findViewById(R.id.linear_manga_list_bar);
        indicator.setVisibility(View.VISIBLE);

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, 2
                        , RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        flag = intent.getStringExtra(KeyWordSwap.FLAG_);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        myHandler = new MyHandler(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSliding = false;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSliding = dy > 0;
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    assert layoutManager != null;
                    int last = layoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    setWhatUp(totalItemCount);
                    if (last == (totalItemCount - 1) && isSliding) {
                        offset = offset + 21;
                        switch (flag) {
                            case KeyWordSwap.FLAG_RECOMMEND:
                                new Thread(new MyRunRec(offset)).start();
                                break;
                            case KeyWordSwap.FLAG_NEWEST:
                                new Thread(new MyRunNewest(offset)).start();
                                break;
                            case KeyWordSwap.FLAG_FINISH:
                                new Thread(new MyRunFin(offset)).start();
                                break;
                        }
                        indicator.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        switch (flag) {
            case KeyWordSwap.FLAG_RECOMMEND:
                new Thread(new MyRunRec(offset)).start();
                toolbar.setTitle(R.string.recommend);
                break;
            case KeyWordSwap.FLAG_NEWEST:
                new Thread(new MyRunNewest(offset)).start();
                toolbar.setTitle(R.string.new_manga);
                break;
            case KeyWordSwap.FLAG_FINISH:
                new Thread(new MyRunFin(offset)).start();
                toolbar.setTitle(R.string.finish_manga);
                break;
        }


    }

    public int getWhatUp() {
        return whatUp;
    }

    public void setWhatUp(int whatUp) {
        this.whatUp = whatUp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class MyRunRec implements Runnable {


        int offset_1;

        public MyRunRec(int i) {
            this.offset_1 = i;
        }

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_3_WHAT;
            try {
                message.obj = MangaRequestActivity.listDataRec(offset_1);
                myHandler.sendMessage(message);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_ERROR;
                myHandler.sendMessage(message1);
            }
        }
    }

    private class MyRunNewest implements Runnable {

        int offset_1;

        public MyRunNewest(int offset_1) {
            this.offset_1 = offset_1;
        }

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_3_WHAT;
            try {
                message.obj = MangaRequestActivity.getNewestMangaTotal(offset_1);
                myHandler.sendMessage(message);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_ERROR;
                myHandler.sendMessage(message1);
            }
        }
    }

    private class MyRunFin implements Runnable {

        int offset_1;

        public MyRunFin(int offset_1) {
            this.offset_1 = offset_1;
        }

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_3_WHAT;
            try {
                message.obj = MangaRequestActivity.getFinishMangaTotal(offset_1);
                myHandler.sendMessage(message);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_ERROR;
                myHandler.sendMessage(message1);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        private final WeakReference<Activity> reference;

        private RecyclerView view;

        public MyHandler(Activity activity) {
            // 使用WeakReference弱引用持有Activity实例
            reference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_INFO_3_WHAT) {
                Collection<ListBeanManga> mangaList = (Collection<ListBeanManga>) msg.obj;
                Log.i("INFO", "" + mangaList);
                Log.i("SSS", " " + mangaList.size());
                mangaListAll.addAll(getWhatUp(), mangaList);
                indicator.setVisibility(View.GONE);
            }
            if (msg.what == KeyWordSwap.HANDLER_ERROR) {
                Snackbar.make(recyclerView, R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, view -> {
                            switch (flag) {
                                case KeyWordSwap.FLAG_RECOMMEND:
                                    new Thread(new MyRunRec(offset)).start();
                                    toolbar.setTitle(R.string.recommend);
                                    break;
                                case KeyWordSwap.FLAG_NEWEST:
                                    new Thread(new MyRunNewest(offset)).start();
                                    toolbar.setTitle(R.string.new_manga);
                                    break;
                                case KeyWordSwap.FLAG_FINISH:
                                    new Thread(new MyRunFin(offset)).start();
                                    toolbar.setTitle(R.string.finish_manga);
                                    break;
                            }
                            indicator.setVisibility(View.VISIBLE);
                        }).show();
                indicator.setVisibility(View.GONE);
            }
        }
    }

}
