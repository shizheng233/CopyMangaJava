package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersonalDataActivity extends AppCompatActivity {


    ImageView avatarView;
    TextView textViewNickName, textViewUserName;
    MyHandler handler;
    MaterialToolbar toolbar;
    private RecyclerView recyclerViewCollect;
    private RecyclerView recyclerViewBrowse;
    private boolean isLoginOut;
    private String authorization;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.persional_data);
        avatarView = findViewById(R.id.img_avatar_person);
        textViewNickName = findViewById(R.id.text_personal_nickname);
        textViewUserName = findViewById(R.id.text_personal_username);
        recyclerViewCollect = findViewById(R.id.collect_recycler);
        recyclerViewBrowse = findViewById(R.id.browse_recycler);
        toolbar = findViewById(R.id.personal_tool_bar);
        handler = new MyHandler();

        //检查数据 获取数据
        if (getIntent().getIntExtra(KeyWordSwap.INTENT_KEY_JSON, 0) == 0) {
            JsonObject jsonObject = JsonParser
                    .parseString(getIntent().getStringExtra(KeyWordSwap.A_INFO))
                    .getAsJsonObject().getAsJsonObject("results");
            textViewUserName.setText(jsonObject.get("username").getAsString());
            textViewNickName.setText(jsonObject.get("nickname").getAsString());
            Glide.with(this).load(jsonObject.get("avatar").getAsString())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(avatarView);
            authorization = getIntent().getStringExtra(KeyWordSwap.B_INFO);
            new Thread(new MyNewRunnable4List(authorization)).start();
        } else {
            authorization = getIntent().getStringExtra(KeyWordSwap.B_INFO);
            Log.i("TAG_A", "onCreate: " + authorization);
            new Thread(new MyNewRunnable(authorization)).start();
        }

        toolbar.setNavigationOnClickListener(view -> finish());


    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new MyNewRunnable(authorization)).start();
    }

    private class MyNewRunnable implements Runnable {

        private final String string;

        /**
         * 该runnable可以获取到用户数据、收藏列表和网页浏览数据
         *
         * @param c 输入用户认证 authorization
         */
        public MyNewRunnable(String c) {
            this.string = c;
        }

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_1_WHAT;
            try {
                message.obj = apiName.mangaUserinfoGet(string);
                handler.sendMessage(message);

                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_INFO_2_WHAT;
                message1.obj = apiName.mangaCollectApi(string);
                handler.sendMessage(message1);

                Message message2 = new Message();
                message2.what = KeyWordSwap.HANDLER_INFO_3_WHAT;
                message2.obj = apiName.mangaBrowsesGet(string);
                handler.sendMessage(message2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class MyNewRunnable4List implements Runnable {

        private final String string;

        /**
         * 该runnable可以获取到用户收藏列表，网页浏览列表
         *
         * @param c 输入authorization
         */
        public MyNewRunnable4List(String c) {
            this.string = c;
        }

        @Override
        public void run() {
            try {
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_INFO_2_WHAT;
                message1.obj = apiName.mangaCollectApi(string);
                handler.sendMessage(message1);
                Message message2 = new Message();
                message2.what = KeyWordSwap.HANDLER_INFO_3_WHAT;
                message2.obj = apiName.mangaBrowsesGet(string);
                handler.sendMessage(message2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class MyHandler extends Handler {
        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_INFO_1_WHAT) {

                String object = (String) msg.obj;
                JsonObject jsonObe1 = JsonParser.parseString(object).getAsJsonObject();
                Log.i("TAG((SASAS", "handleMessage: " + jsonObe1);
                if (jsonObe1.get("code").getAsInt() == 401) {

                    Toast.makeText(getApplicationContext(),
                            R.string.error_token_out_date,
                            Toast.LENGTH_LONG)
                            .show();
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            KeyWordSwap.ONLY_ONE_KEY_AUTHORIZATION,
                            Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove(KeyWordSwap.FILE_AUTHORIZATION).apply();
                    finish();
                    isLoginOut = true;
                    return;
                } else if (jsonObe1.get("code").getAsInt() == 200) {
                    JsonObject object1 = jsonObe1.getAsJsonObject("results");
                    textViewUserName.setText(object1.get("username").getAsString());
                    textViewNickName.setText(object1.get("nickname").getAsString());
                    Glide.with(getApplicationContext()).load(object1.get("avatar").getAsString())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(avatarView);
                    isLoginOut = false;
                }


            }
            if (msg.what == KeyWordSwap.HANDLER_INFO_2_WHAT) {
                if (isLoginOut) {
                    return;
                }
                List<ListBeanManga> list = new ArrayList<>();
                String s2 = (String) msg.obj;
                JsonObject jsonObject = JsonParser.parseString(s2)
                        .getAsJsonObject().getAsJsonObject("results");
                Log.i("TAG_JSON", "handleMessage: " + jsonObject);
                JsonArray array1 = jsonObject.getAsJsonArray("list");
                int q = Math.min(array1.size(), 4);
                for (int i = 0; i < q; i++) {
                    JsonObject jsonObject1 = array1.get(i).getAsJsonObject().get("comic")
                            .getAsJsonObject();
                    ListBeanManga manga = new ListBeanManga();
                    manga.setNameManga(jsonObject1.get("name").getAsString());
                    manga.setAuthorManga(jsonObject1.get("last_chapter_name").getAsString());
                    manga.setUrlCoverManga(jsonObject1.get("cover").getAsString());
                    manga.setPathWordManga(jsonObject1.get("path_word").getAsString());
                    list.add(manga);
                }
                RecyclerViewMangaAdapter adapter = new RecyclerViewMangaAdapter(list);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
                        , RecyclerView.HORIZONTAL
                        , false);
                recyclerViewCollect.setLayoutManager(layoutManager);
                recyclerViewCollect.setAdapter(adapter);
                adapter.setOnItemClickListener(position -> {
                    Intent intent = new Intent();
                    intent.putExtra(KeyWordSwap.PATH_WORD_TYPE, list.get(position).getPathWordManga());
                    intent.setClass(getApplicationContext(), MangaInfoActivity.class);
                    startActivity(intent);
                });
            }
            if (msg.what == KeyWordSwap.HANDLER_INFO_3_WHAT) {
                if (isLoginOut) {
                    return;
                }
                List<ListBeanManga> list = new ArrayList<>();
                String s3 = (String) msg.obj;
                JsonObject jsonObject1 = JsonParser.parseString(s3).getAsJsonObject().getAsJsonObject("results");
                JsonArray jsonArray = jsonObject1.getAsJsonArray("list");
                int q = Math.min(jsonArray.size(), 4);
                for (int i = 0; i < q; i++) {
                    JsonObject jsonObject2 = jsonArray.get(i)
                            .getAsJsonObject().getAsJsonObject("comic");
                    ListBeanManga manga = new ListBeanManga();
                    manga.setPathWordManga(jsonObject2.get("path_word").getAsString());
                    manga.setNameManga(jsonObject2.get("name").getAsString());
                    manga.setUrlCoverManga(jsonObject2.get("cover").getAsString());
                    JsonArray array2 = jsonObject2.get("author").getAsJsonArray();
                    String author;
                    if (array2.size() == 1) {
                        author = array2.get(0).getAsJsonObject().get("name").getAsString();
                    } else {
                        author = array2.get(0).getAsJsonObject().get("name").getAsString() + "等";
                    }
                    manga.setAuthorManga(author);
                    list.add(manga);
                }
                RecyclerViewMangaAdapter adapter = new RecyclerViewMangaAdapter(list);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
                        , RecyclerView.HORIZONTAL
                        , false);
                recyclerViewBrowse.setAdapter(adapter);
                recyclerViewBrowse.setLayoutManager(layoutManager);
                adapter.setOnItemClickListener(position -> {
                    Intent intent = new Intent();
                    intent.putExtra(KeyWordSwap.PATH_WORD_TYPE, list.get(position).getPathWordManga());
                    intent.setClass(getApplicationContext(), MangaInfoActivity.class);
                    startActivity(intent);
                });
            }
        }
    }

}
