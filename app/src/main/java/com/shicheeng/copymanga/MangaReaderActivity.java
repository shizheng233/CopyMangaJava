package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.bean.JsonABean;
import com.shicheeng.copymanga.data.MangaReadJson;
import com.shicheeng.copymanga.fm.MangaReaderFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MangaReaderActivity extends FragmentActivity {

    private boolean activeBar;
    private final Handler mHideHandler = new Handler();
    private MyHandler myHandler;
    private MaterialToolbar toolbar;
    private CoordinatorLayout layout;
    private AppBarLayout layout2;
    private View view_root;
    private boolean onlyOnePage ;

    //从 Android Studio 复制过来的全屏代码
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                view_root.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                view_root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            }
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            layout2.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            view_root.setVisibility(View.VISIBLE);
        }
    };//结束

    //变量声明
    private String title;
    private String pathWord2;
    private String subtitle;
    private String uuid;
    private boolean isLast;
    private ExtendedFloatingActionButton ext_fab;
    private CircularProgressIndicator indicator;
    private ViewPager2 view2;
    private boolean mVisible;
    private String nextUuid;
    private String pathWord;
    private String chapterName;
    private String coverUrlIn;
    private String thisUuid;


    //Main
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_reader_layout);
        toolbar = findViewById(R.id.manga_reader_toolbar);
        layout = findViewById(R.id.manga_reader_root);
        layout2 = findViewById(R.id.appbar_reader);
        view_root = this.getWindow().getDecorView();
        view2 = findViewById(R.id.manga_reader_view);
        indicator = findViewById(R.id.c_bar_main);
        ext_fab = findViewById(R.id.exp_reader_fab);


        Intent intent = getIntent();
        title = intent.getStringExtra(KeyWordSwap.TITLE_TYPE);
        subtitle = intent.getStringExtra(KeyWordSwap.CHAPTER_TYPE);
        uuid = intent.getStringExtra(KeyWordSwap.UUID_WORD_TYPE);
        pathWord2 = intent.getStringExtra(KeyWordSwap.PATH_WORD_TYPE);
        coverUrlIn = intent.getStringExtra(KeyWordSwap.COVER_URL_TYPE);

        activeBar = (getApplicationContext().getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_YES) != 0;

        new Thread(new MyRun(pathWord2, uuid)).start();
        //Log.i("TAG_OOO", "onCreate: "+uuid);

        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);

        setPathWord(pathWord2);
        setThisUuid(uuid);

        myHandler = new MyHandler();

        mVisible = true;

        toolbar.setNavigationOnClickListener(view -> finish());

        ext_fab.setOnClickListener(view -> {
            new Thread(new MyRun(pathWord2,getNextUuid())).start();
            indicator.setVisibility(View.VISIBLE);
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (view2.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            view2.setCurrentItem(view2.getCurrentItem() - 1);
        }
    }


    private void toggle() {

        if (mVisible) {
            hide();
        } else {
            show();
        }

    }


    private void hide() {
        Log.i("TAG_MBA", "HIDE");
        layout2.setVisibility(View.GONE);
        mVisible = false;
        if (!isLast) {
            ext_fab.hide();
        }
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, 300);
    }

    private void show() {

        if (Build.VERSION.SDK_INT >= 30) {
            view_root.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            if (activeBar) {
                view_root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            } else {
                view_root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }

        }
        if (!isLast) {
            ext_fab.show();
        }

        mVisible = true;
        layout2.setVisibility(View.VISIBLE);

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, 300);

    }

    public String getNextUuid() {
        return nextUuid;
    }

    public void setNextUuid(String nextUuid) {
        this.nextUuid = nextUuid;
    }

    public String getPathWord() {
        return pathWord;
    }

    public void setPathWord(String pathWord) {
        this.pathWord = pathWord;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }



    @Override
    protected void onDestroy() {
        File file = new File(getFilesDir(), KeyWordSwap.FILE_NAME);
        if (file.exists()) {
            Gson gson = new Gson();
            String json = getFileSave();
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            JsonABean jsonABean = new JsonABean(title, subtitle, pathWord2, getThisUuid(), coverUrlIn);
            String json2 = gson.toJson(jsonABean);
            JsonElement element = JsonParser.parseString(json2).getAsJsonObject();
            for (int i = 0; i < array.size(); i++) {
                if (element.getAsJsonObject().get("nameManga")
                        .getAsString().equals(array.get(i).getAsJsonObject()
                                .get("nameManga").getAsString())) {
                    array.remove(i);

                }
            }
            array.add(element);
            Log.i("TAG_111", "sss" + array);
            setFileSave(array.toString());
        } else if (!file.exists()) {
            Gson gson = new Gson();
            JsonArray jsonArray = new JsonArray();
            JsonABean jsonABean = new JsonABean(title, subtitle, pathWord2, getThisUuid(), coverUrlIn);
            String json2 = gson.toJson(jsonABean);
            JsonElement element = JsonParser.parseString(json2).getAsJsonObject();
            Log.i("TAG_QQQQQ", "onDestroy: " + json2);
            jsonArray.add(element);
            Log.i("TAG_QQQQQK", "onDestroy: " + jsonArray);
            String text = jsonArray.toString();
            Log.i("TAG_QQQ2QQK", "onDestroy: " + text);
            setFileSave(text);
        }


        super.onDestroy();
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

    private void setFileSave(String text) {

        try {
            FileOutputStream fos = openFileOutput(KeyWordSwap.FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Log.i("SSS", "kaishi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getThisUuid() {
        return thisUuid;
    }

    public void setThisUuid(String thisUuid) {
        this.thisUuid = thisUuid;
    }


    //该viewpager的图片
    private static class FragmentAdapter extends FragmentStateAdapter {

        private final HashMap<Integer, String> bitmapHashMap;
        private final View rootV;
        private onAdapterClickListener onAdapterClickListener;

        public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, HashMap<Integer, String> hashMap, View root) {
            super(fragmentActivity);
            this.bitmapHashMap = hashMap;
            this.rootV = root;
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            MangaReaderFragment mangaReaderFragment = new MangaReaderFragment();
            mangaReaderFragment.setOnFragmentClickListener(view -> onAdapterClickListener.onClick(view));
            mangaReaderFragment.setUrl(bitmapHashMap.get(position));

            return mangaReaderFragment;
        }

        @Override
        public int getItemCount() {
            return bitmapHashMap.size();
        }


        public void setOnAdapterClickListener
                (FragmentAdapter.onAdapterClickListener onAdapterClickListener) {
            this.onAdapterClickListener = onAdapterClickListener;
        }

        interface onAdapterClickListener {
            void onClick(View v);
        }


    }

    private class MyHandler extends Handler {

        private int po;


        public int getPo() {
            return po;
        }

        public void setPo(int po) {
            this.po = po;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //截取最重要的，对于漫画图片的消息
            if (msg.what == KeyWordSwap.HANDLER_INFO_7_WHAT) {

                HashMap<Integer, String> bitmapHashMap = (HashMap<Integer, String>) msg.obj;
                FragmentAdapter adapter =
                        new FragmentAdapter(MangaReaderActivity.this,
                                bitmapHashMap, view2);
                view2.setAdapter(adapter);
                view2.setKeepScreenOn(true);
                view2.setOffscreenPageLimit(1);
                indicator.setVisibility(View.GONE);
                adapter.setOnAdapterClickListener(v -> {
                    toggle();
                    //Log.i("TAG_MBA", "DIANJI");
                });

                setPo(adapter.getItemCount());

                view2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                        if (!mVisible && !isLast) {
                            if ((position + 1) == adapter.getItemCount()) {
                                ext_fab.show();
                            } else {
                                ext_fab.hide();
                            }
                        }
                    }
                });

            }

            //截取下一章uuid的消息，当没有下一章的时候返回消息
            if (msg.what == KeyWordSwap.HANDLER_INFO_8_WHAT) {
                String nextUid = (String) msg.obj;

                if (nextUid.equals(KeyWordSwap.NON_JSON)) {
                    isLast = true;
                    ext_fab.hide();
                }else {
                    setNextUuid(nextUid);
                }
                ext_fab.setOnClickListener(view -> {
                    new Thread(new MyRun(getPathWord(), getNextUuid())).start();
                    new Thread(new My2Run(getPathWord(), getNextUuid())).start();
                    indicator.setVisibility(View.VISIBLE);
                });
                view2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        ext_fab.setOnClickListener(view -> {
                            new Thread(new MyRun(getPathWord(), getNextUuid())).start();
                            new Thread(new My2Run(getPathWord(), getNextUuid())).start();
                            indicator.setVisibility(View.VISIBLE);
                        });
                        if ((getPo() - 1) == position) {
                            if (nextUid.equals(KeyWordSwap.NON_JSON)) {
                                Snackbar.make(view_root, R.string.non_next, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.exit, view -> finish()).show();
                                ext_fab.hide();
                            } else {

                                ext_fab.setOnClickListener(view -> {
                                    new Thread(new MyRun(getPathWord(), getNextUuid())).start();
                                    new Thread(new My2Run(getPathWord(), getNextUuid())).start();
                                    Log.i("UUID", getNextUuid());
                                    Log.i("PW", getPathWord());
                                    indicator.setVisibility(View.VISIBLE);
                                });
                            }
                        }


                    }
                });


            }

            //截取章节名字的message
            if (msg.what == KeyWordSwap.HANDLER_INFO_9_WHAT) {
                String name = (String) msg.obj;
                setChapterName(name);
                subtitle = name;
                toolbar.setSubtitle(name);

            }

            //截取错误的消息
            if (msg.what == KeyWordSwap.HANDLER_ERROR) {
                Snackbar.make(view_root, R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, view -> {
                            new Thread(new MyRun(getPathWord(), getNextUuid())).start();
                        }).show();
            }

        }
    }

    //第一个runnable，用于本章获取图片
    private class MyRun implements Runnable {

        private final String pathWord;
        private final String uuid;

        private MyRun(String pathWord, String uuid) {
            this.pathWord = pathWord;
            this.uuid = uuid;
        }


        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_7_WHAT;
            try {
                message.obj = MangaReadJson.getPicBitmap(pathWord, uuid);
                myHandler.sendMessage(message);
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_INFO_8_WHAT;
                message1.obj = MangaReadJson.getNextUUID(pathWord, uuid);
                myHandler.sendMessage(message1);

            } catch (IOException | ExecutionException | InterruptedException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_ERROR;
                myHandler.sendMessage(message1);
            }
        }
    }

    //第二个Runnable,用于获取下一章的名字
    private class My2Run implements Runnable {

        private final String pathWord;
        private final String uuid;

        private My2Run(String pathWord, String uuid) {
            this.pathWord = pathWord;
            this.uuid = uuid;
            setThisUuid(uuid);
        }

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_9_WHAT;
            try {
                message.obj = MangaReadJson.getNextChapterName(pathWord, uuid);
                myHandler.sendMessage(message);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_ERROR;
                myHandler.sendMessage(message1);
            }
        }
    }


}
