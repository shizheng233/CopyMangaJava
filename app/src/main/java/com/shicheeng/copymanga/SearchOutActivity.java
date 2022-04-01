package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.adapter.MangaListAdapter;
import com.shicheeng.copymanga.json.SearchMangaJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchOutActivity extends AppCompatActivity {

    private String textIn;
    private MaterialToolbar toolbar;
    private SearchView searchView;
    private List<ListBeanManga> list = new ArrayList<>();
    private MangaListAdapter adapter = new MangaListAdapter(list);
    private LinearProgressIndicator indicator;
    private MyHandler handler;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tab);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            // Log.i("TAG_IN",query);
            textIn = intent.getStringExtra(SearchManager.QUERY);
        }
        searchView = findViewById(R.id.search_layout_01);
        toolbar = findViewById(R.id.tool_bar_search);
        indicator = findViewById(R.id.ind_search);
        recyclerView = findViewById(R.id.recycler_search);
        handler = new MyHandler();

        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        GridLayoutManager manager =
                new GridLayoutManager(this,2,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        searchView.setQuery(textIn,false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //重新请求
                Log.i("_AAA", "onQueryTextSubmit: "+query);
                new Thread(new MyRun(query)).start();
                indicator.setVisibility(View.VISIBLE);
                list = new ArrayList<>();
                adapter = new MangaListAdapter(list);
                recyclerView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        new Thread(new MyRun(textIn)).start();
    }

    private class MyRun implements Runnable{

        private String name;

        public MyRun(String name){
            this.name = name;
        }

        @Override
        public void run() {
            try {
                Message message = new Message();
                message.what = KeyWordSwap.HANDLER_INFO_10_WHAT;
                message.obj = SearchMangaJson.toGetManga(name);
                handler.sendMessage(message);
            } catch (IOException| JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyHandler extends Handler{

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_INFO_10_WHAT){
                Collection<ListBeanManga> mangas = (Collection<ListBeanManga>) msg.obj;
                list.addAll(mangas);
                recyclerView.setAdapter(adapter);
                indicator.setVisibility(View.GONE);
            }
        }
    }

}
