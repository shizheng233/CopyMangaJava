package com.shicheeng.copymanga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.adapter.MangaListAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MangaHistoryActivity extends AppCompatActivity {

    File file;
    RecyclerView recyclerView;
    TextView textViewHistoryTitle, textViewChapterName;
    ImageView imageViewCover;
    LinearLayout layoutClick;
    MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_history);
        file = new File(getFilesDir(), KeyWordSwap.FILE_NAME);
        if (!file.exists()) {
            return;
        }
        recyclerView = findViewById(R.id.manga_list_history_main);
        textViewChapterName = findViewById(R.id.manga_chapter_history_main);
        textViewHistoryTitle = findViewById(R.id.manga_title_history_main);
        imageViewCover = findViewById(R.id.image_cover_history);
        layoutClick = findViewById(R.id.head_main_history);
        materialToolbar = findViewById(R.id.manga_history_bar_main);

        JsonArray array1 = JsonParser.parseString(getFileSave()).getAsJsonArray();
        List<ListBeanManga> mangaList = new ArrayList<>();
        JsonObject object2 = array1.get(array1.size() - 1).getAsJsonObject();
        Glide.with(this).load(object2.get("coverUrl").getAsString()).into(imageViewCover);
        textViewHistoryTitle.setText(object2.get("nameManga").getAsString());
        textViewChapterName.setText(getString(R.string.watching,object2.get("chapter").getAsString()));
        for (int i = array1.size() - 2; i >= 0; i--) {
            JsonObject object1 = array1.get(i).getAsJsonObject();
            ListBeanManga listBeanManga = new ListBeanManga();
            listBeanManga.setAuthorManga(object1.get("chapter").getAsString());
            listBeanManga.setUrlCoverManga(object1.get("coverUrl").getAsString());
            listBeanManga.setNameManga(object1.get("nameManga").getAsString());
            listBeanManga.setPathWordManga(object1.get("pathWord").getAsString());
            mangaList.add(listBeanManga);
        }
        MangaListAdapter adapter = new MangaListAdapter(mangaList);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this,2,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        layoutClick.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(this,MangaInfoActivity.class);
            intent.putExtra(KeyWordSwap.PATH_WORD_TYPE,object2.get("pathWord").getAsString());
            startActivity(intent);
        });
        materialToolbar.setSubtitle(R.string.data_info);
        materialToolbar.setNavigationOnClickListener(view -> finish());
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

}
