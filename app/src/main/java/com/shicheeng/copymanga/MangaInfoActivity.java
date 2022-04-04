package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.adapter.MangaInfoChapterAdapter;
import com.shicheeng.copymanga.adapter.MangaInfoChipperAdapter;
import com.shicheeng.copymanga.data.ChipTextBean;
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean;
import com.shicheeng.copymanga.dialog.BottomDialogFragment;
import com.shicheeng.copymanga.json.MangaInfoJson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MangaInfoActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private MaterialToolbar mangaToolbar;
    private ImageView mangaCoverBack, mangaCoverOut;
    private RecyclerView recyclerViewMangaChapter, recyclerViewChip, recyclerViewChipTheme;
    private LinearProgressIndicator linearProgressIndicator;
    private MyNewHandler myNewHandler;
    private TextView textTitle, textAuthor, textDetail, textChapter;
    private NestedScrollView nestedScrollView;
    private Thread thread;
    private MaterialCardView mangaCard;
    private ExtendedFloatingActionButton extendedFAB;
    private String coverUrlQ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_info);

        //初始化控件
        mangaToolbar = findViewById(R.id.info_toolbar);
        mangaCoverBack = findViewById(R.id.background_cover);
        mangaCoverOut = findViewById(R.id.small_cover);
        recyclerViewMangaChapter = findViewById(R.id.recycler_manga_info);
        linearProgressIndicator = findViewById(R.id.linear_progress_bar);
        textAuthor = findViewById(R.id.mangaAuthorText);
        textTitle = findViewById(R.id.mangaTitleText);
        textDetail = findViewById(R.id.mangaDetailText);
        textChapter = findViewById(R.id.text_chapter);
        nestedScrollView = findViewById(R.id.big_scroll_view);
        recyclerViewChip = findViewById(R.id.recycler_manga_info_chip);
        recyclerViewChipTheme = findViewById(R.id.recycler_manga_info_chip_theme);
        mangaCard = findViewById(R.id.card_manga);
        extendedFAB = findViewById(R.id.expand_fab_info);


        //初始化一堆逻辑 创建竖直列表布局
        setSupportActionBar(mangaToolbar);
        Intent intent = getIntent();
        String pathWord = intent.getStringExtra(KeyWordSwap.PATH_WORD_TYPE);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        textChapter.setText(R.string.loading);

        linearProgressIndicator.setVisibility(View.VISIBLE);
        recyclerViewMangaChapter.setLayoutManager(layoutManager);
        mangaToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        myNewHandler = new MyNewHandler(this);
        myNewHandler.setLinearProgressIndicator(linearProgressIndicator);
        myNewHandler.setMangaToolbar(mangaToolbar);
        myNewHandler.setMangaCoverBack(mangaCoverBack);
        myNewHandler.setMangaCoverOut(mangaCoverOut);
        myNewHandler.setRecyclerViewMangaChapter(recyclerViewMangaChapter);
        myNewHandler.setScrollView(nestedScrollView);
        myNewHandler.setPathWord(pathWord);

        MyRun myRun = new MyRun();
        myRun.setPathWord(pathWord);
        thread = new Thread(myRun);
        executorService.submit(thread);
        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        executorService.shutdown();
    }

    private String getFileSave() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(KeyWordSwap.FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        }
        return stringBuilder.toString();
    }

    public String getCoverUrlQ() {
        return coverUrlQ;
    }

    public void setCoverUrlQ(String coverUrlQ) {
        this.coverUrlQ = coverUrlQ;
    }

    //创建内部类将方法放入里面
    private class MangaInfoGo {

        /**
         * 将数据放入至List
         *
         * @array jsonArray
         */
        public void setMangaInfoOn(RecyclerView recyclerView, JsonArray array) {
            //啊哈哈哈，数据来咯
            List<MangaInfoChapterDataBean> infoList = new ArrayList<>();
            //遍历数据
            for (int i = 0; i < array.size(); i++) {
                MangaInfoChapterDataBean bean = new MangaInfoChapterDataBean();
                JsonObject jsonObject_1 = array.get(i).getAsJsonObject();
                String mangaChapterTitle = jsonObject_1.get("name").getAsString();
                String mangaChapterTime = jsonObject_1.get("datetime_created").getAsString();
                String mangaChapterUUid = jsonObject_1.get("uuid").getAsString();
                String mangaChapterPathWord = jsonObject_1.get("comic_path_word").getAsString();
                bean.setChapterTime(mangaChapterTime);
                bean.setChapterTitle(mangaChapterTitle);
                bean.setUuidText(mangaChapterUUid);
                bean.setPathWord(mangaChapterPathWord);
                infoList.add(bean);
            }
            MangaInfoChapterAdapter adapter = new MangaInfoChapterAdapter(infoList);
            adapter.setOnItemClickListener(new MangaInfoChapterAdapter.OnItemClickListener() {
                @Override
                public void OnItem(int position) {
                    Intent intent333 = new Intent();
                    intent333.setClass(recyclerView.getContext(), MangaReaderActivity.class);
                    intent333.putExtra(KeyWordSwap.PATH_WORD_TYPE, infoList.get(position).getPathWord());
                    intent333.putExtra(KeyWordSwap.UUID_WORD_TYPE, infoList.get(position).getUuidText());
                    intent333.putExtra(KeyWordSwap.CHAPTER_TYPE, infoList.get(position).getChapterTitle());
                    intent333.putExtra(KeyWordSwap.TITLE_TYPE, MangaInfoActivity.this.mangaToolbar.getTitle());
                    intent333.putExtra(KeyWordSwap.COVER_URL_TYPE,getCoverUrlQ());
                    recyclerView.getContext().startActivity(intent333);
                }
            });
            recyclerView.setAdapter(adapter);
        }

    }

    //背景Run
    private class MyRun implements Runnable {

        String pathWord;

        public void setPathWord(String pathWord) {
            this.pathWord = pathWord;
        }

        @Override
        public void run() {
            Message message_1 = new Message();
            message_1.what = KeyWordSwap.HANDLER_INFO_1_WHAT;
            try {
                message_1.obj = MangaInfoJson.getMangaInfo(pathWord, getApplicationContext());
                myNewHandler.sendMessage(message_1);
            } catch (IOException | ExecutionException | InterruptedException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message_3 = new Message();
                message_3.what = KeyWordSwap.HANDLER_ERROR;
                myNewHandler.sendMessage(message_3);
            }
            Message message_2 = new Message();
            message_2.what = KeyWordSwap.HANDLER_INFO_2_WHAT;
            try {
                message_2.obj = MangaInfoJson.getMangaContent(pathWord);
                myNewHandler.sendMessage(message_2);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message_3 = new Message();
                message_3.what = KeyWordSwap.HANDLER_ERROR;
                myNewHandler.sendMessage(message_3);
            }
        }

    }

    private class MyNewHandler extends Handler {

        private final WeakReference<Activity> reference;

        //直接复制
        private MaterialToolbar mangaToolbar;
        private ImageView mangaCoverBack, mangaCoverOut;
        private RecyclerView recyclerViewMangaChapter;
        private LinearProgressIndicator linearProgressIndicator;
        private NestedScrollView scrollView;
        private String pathWord;
        private String title1;
        private String coverUrl1;

        public MyNewHandler(Activity activity) {
            // 使用WeakReference弱引用持有Activity实例
            reference = new WeakReference<Activity>(activity);

        }

        //传入控件
        public void setLinearProgressIndicator(LinearProgressIndicator linearProgressIndicator) {
            this.linearProgressIndicator = linearProgressIndicator;
        }


        public void setMangaToolbar(MaterialToolbar mangaToolbar) {
            this.mangaToolbar = mangaToolbar;
        }

        public void setMangaCoverBack(ImageView mangaCoverBack) {
            this.mangaCoverBack = mangaCoverBack;
        }

        public void setMangaCoverOut(ImageView mangaCoverOut) {
            this.mangaCoverOut = mangaCoverOut;
        }

        public void setRecyclerViewMangaChapter(RecyclerView recyclerViewMangaChapter) {
            this.recyclerViewMangaChapter = recyclerViewMangaChapter;
        }


        public void setPathWord(String pathWord) {
            this.pathWord = pathWord;
        }


        public void setScrollView(NestedScrollView scrollView) {
            this.scrollView = scrollView;
        }

        @SuppressLint({"StringFormatMatches", "SetTextI18n", "UseCompatLoadingForDrawables"})
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KeyWordSwap.HANDLER_INFO_1_WHAT:
                    HashMap<Integer, Object> hashMap = (HashMap<Integer, Object>) msg.obj;
                    Bitmap url = (Bitmap) hashMap.get(3);
                    List<ChipTextBean> author = (List<ChipTextBean>) hashMap.get(2);
                    String title = (String) hashMap.get(1);
                    String detail = (String) hashMap.get(4);
                    String display = (String) hashMap.get(5);
                    Bitmap blurBitmap = ImageUtil.toBlur(url, 10);
                    List<ChipTextBean> themes = (List<ChipTextBean>) hashMap.get(6);
                    String aliasName = (String) hashMap.get(7);
                    String coverUrl = (String) hashMap.get(8);
                    mangaCoverOut.setImageBitmap(url);
                    mangaCoverBack.setImageBitmap(blurBitmap);
                    mangaToolbar.setTitle(title);
                    mangaToolbar.setSubtitle(display);
                    textAuthor.setText(aliasName);
                    MangaInfoChipperAdapter adapterChip = new MangaInfoChipperAdapter(author);
                    LinearLayoutManager manager =
                            new LinearLayoutManager(getApplicationContext(),
                                    LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewChip.setLayoutManager(manager);
                    recyclerViewChip.setAdapter(adapterChip);
                    MangaInfoChipperAdapter adapterThemes = new MangaInfoChipperAdapter(themes);
                    LinearLayoutManager manager_1 =
                            new LinearLayoutManager(getApplicationContext(),
                                    LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewChipTheme.setLayoutManager(manager_1);
                    adapterThemes.setIcon(R.drawable.ic_manga_tag);
                    recyclerViewChipTheme.setAdapter(adapterThemes);
                    textTitle.setText(title);
                    this.title1 = title;
                    this.coverUrl1 = coverUrl;
                    setCoverUrlQ(coverUrl);
                    int bitColor = ImageUtil.getOneColor(blurBitmap);
                    int antiColor = ImageUtil.getAntiColor(bitColor);
                    textTitle.setTextColor(antiColor);
                    textAuthor.setTextColor(antiColor);
                    textDetail.setTextColor(antiColor);
                    textDetail.setText(detail);
                    mangaCard.setOnClickListener(view -> {
                        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
                        bottomDialogFragment.setDetail(detail);
                        bottomDialogFragment.setTitle(title);
                        bottomDialogFragment.setsTitle("别名：" + aliasName);
                        bottomDialogFragment.show(getSupportFragmentManager(), "SHIHCHEENG_DIALOG_1");
                    });
                    break;

                case KeyWordSwap.HANDLER_INFO_2_WHAT:
                    File file = new File(getFilesDir(), KeyWordSwap.FILE_NAME);
                    JsonObject obj = (JsonObject) msg.obj;
                    JsonArray array = obj.getAsJsonArray("list");
                    MangaInfoGo go = new MangaInfoGo();
                    go.setMangaInfoOn(recyclerViewMangaChapter, array);
                    String text = getString(R.string.chapter, obj.get("total").getAsInt());
                    textChapter.setText(text);
                    scrollView.setVisibility(View.VISIBLE);
                    linearProgressIndicator.setVisibility(View.GONE);
                    JsonObject object2 = array.get(0).getAsJsonObject();
                    if (file.exists()) {
                        JsonArray array1 = JsonParser.parseString(getFileSave()).getAsJsonArray();
                        for (int q = 0; q < array1.size(); q++) {
                            if (array1.get(q).getAsJsonObject().get("pathWord").getAsString()
                                    .equals(object2.get("comic_path_word").getAsString())) {
                                int finalQ = q;
                                extendedFAB.setOnClickListener(view -> {
                                    Intent intent1 = new Intent();
                                    intent1.setClass(extendedFAB.getContext(), MangaReaderActivity.class);
                                    intent1.putExtra(KeyWordSwap.PATH_WORD_TYPE, object2.get("comic_path_word")
                                            .getAsString());
                                    intent1.putExtra(KeyWordSwap.UUID_WORD_TYPE, array1.get(finalQ)
                                            .getAsJsonObject().get("uuid").getAsString());
                                    intent1.putExtra(KeyWordSwap.TITLE_TYPE, title1);
                                    intent1.putExtra(KeyWordSwap.COVER_URL_TYPE,coverUrl1);
                                    intent1.putExtra(KeyWordSwap.CHAPTER_TYPE, array1.get(finalQ)
                                            .getAsJsonObject().get("chapter").getAsString());

                                    startActivity(intent1);
                                });
                                extendedFAB.setText(R.string.keep_reading);
                            }
                        }
                    } else {
                        extendedFAB.setOnClickListener(view -> {
                            Intent intent1 = new Intent();
                            intent1.setClass(extendedFAB.getContext(), MangaReaderActivity.class);
                            intent1.putExtra(KeyWordSwap.PATH_WORD_TYPE, object2.get("comic_path_word")
                                    .getAsString());
                            intent1.putExtra(KeyWordSwap.UUID_WORD_TYPE, object2.get("uuid")
                                    .getAsString());
                            intent1.putExtra(KeyWordSwap.TITLE_TYPE, title1);
                            intent1.putExtra(KeyWordSwap.COVER_URL_TYPE,coverUrl1);
                            intent1.putExtra(KeyWordSwap.CHAPTER_TYPE, object2.get("name").getAsString());
                            startActivity(intent1);
                        });
                    }

                    extendedFAB.setVisibility(View.VISIBLE);
                    break;

                case KeyWordSwap.HANDLER_ERROR:
                    Snackbar.make(scrollView, R.string.error, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, view -> {
                                thread.interrupt();
                                MyRun run = new MyRun();
                                run.setPathWord(pathWord);
                                new Thread(run).start();
                                linearProgressIndicator.setVisibility(View.VISIBLE);
                            }).show();
                    linearProgressIndicator.setVisibility(View.GONE);

            }
        }


    }

}
