package com.shicheeng.copymanga;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.adapter.MangaListAdapter;
import com.shicheeng.copymanga.dialog.SortDialogFragment;
import com.shicheeng.copymanga.json.MangaSortJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HotMangaActivity extends AppCompatActivity {

    SortDialogFragment dialogFragment = new SortDialogFragment();
    MyHandler myHandler;
    private RecyclerView recyclerView;
    private String themePathWord, orderPathWord;
    private String themeName, orderName;
    private Thread thread;
    private MaterialToolbar toolbar;
    private LinearProgressIndicator indicator;
    private CoordinatorLayout conView;
    private int offset;
    private List<ListBeanManga> myList = new ArrayList<>();
    private MangaListAdapter adapter = new MangaListAdapter(myList);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hot_manga_layout);

        //初始化控件
        recyclerView = findViewById(R.id.recycler_manga_hot);
        FloatingActionButton actionButton = findViewById(R.id.fab_hot);
        indicator = findViewById(R.id.progress_indicator_hot);
        toolbar = findViewById(R.id.tool_bar_hot);
        conView = findViewById(R.id.hot_con_view);
        myHandler = new MyHandler();

        toolbar.setNavigationOnClickListener(view -> finish());
        //必须提前做
        //否则会重绘列表
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 2
                        , RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        //传递数据
        this.setThemePathWord("all");
        this.setOrderPathWord("-popular");
        this.setThemeName("全部");
        this.setOrderName("热门");

        actionButton.setOnClickListener(view -> {
            dialogFragment.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

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
                    if (last == (totalItemCount - 1) && isSliding) {
                        offset = offset + 21;
                        MyRun myRun = new MyRun();
                        myRun.setOffset(offset);
                        new Thread(myRun).start();
                        indicator.setVisibility(View.VISIBLE);
                    }

                }
            }


        });

        dialogFragment.setOnDialogBottomPopMenuClickListener(
                (list, position) -> {
                    this.setOrderPathWord(list.get(position).getPathWord());
                    this.setOrderName(list.get(position).getPathName());
                });
        dialogFragment.setOnDialogTopPopMenuClickListener(
                (list, position) -> {
                    this.setThemePathWord(list.get(position).getPathWord());
                    this.setThemeName(list.get(position).getPathName());
                });

        dialogFragment.setOnButtonClickListener(view -> {
            offset = 0;
            Thread thread2 = new Thread(() -> {
                Message message = new Message();
                message.what = KeyWordSwap.HANDLER_INFO_5_WHAT;
                try {
                    Log.i("SC_0011", getOrderPathWord() + getThemePathWord());
                    message.obj = MangaSortJson.getFilterData(0, getThemePathWord(), getOrderPathWord());
                    myHandler.sendMessage(message);
                } catch (IOException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Message message_3 = new Message();
                    message_3.what = KeyWordSwap.HANDLER_ERROR;
                    myHandler.sendMessage(message_3);
                }
            });
            thread2.start();
            dialogFragment.dismiss();
            myList = new ArrayList<>();
            adapter = new MangaListAdapter(myList);
            recyclerView.setAdapter(adapter);
            actionButton.show();
            indicator.setVisibility(View.VISIBLE);
        });

        MyRun myRun = new MyRun();
        myRun.setOffset(0);
        thread = new Thread(myRun);
        thread.start();
    }

    //在类中传递数据
    public String getOrderPathWord() {
        return orderPathWord;
    }

    public void setOrderPathWord(String orderPathWord) {
        this.orderPathWord = orderPathWord;
    }

    public String getThemePathWord() {
        return themePathWord;
    }

    public void setThemePathWord(String themePathWord) {
        this.themePathWord = themePathWord;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    private class MyRun implements Runnable {

        int offset_1;

        public void setOffset(int offset) {
            this.offset_1 = offset;
        }

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_5_WHAT;
            try {
                message.obj = MangaSortJson.
                        getFilterData(offset_1, getThemePathWord(), getOrderPathWord());
                myHandler.sendMessage(message);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message messageE = new Message();
                messageE.what = KeyWordSwap.HANDLER_ERROR;
                myHandler.sendMessage(messageE);
            }
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_INFO_5_WHAT) {
                Collection<ListBeanManga> list = (Collection<ListBeanManga>) msg.obj;
                myList.addAll(list);
                indicator.setVisibility(View.GONE);
                toolbar.setSubtitle(getThemeName() + "-" + getOrderName());
            }
            if (msg.what == KeyWordSwap.HANDLER_ERROR) {
                thread.interrupt();

                Snackbar.make(conView, R.string.error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyRun run = new MyRun();
                        run.setOffset(offset);
                        new Thread(run).start();
                        indicator.setVisibility(View.VISIBLE);
                    }
                }).show();
                indicator.setVisibility(View.GONE);
            }
        }

    }
}
