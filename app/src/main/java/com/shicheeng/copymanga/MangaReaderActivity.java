package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

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
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.data.MangaReadJson;
import com.shicheeng.copymanga.fm.MangaReaderFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MangaReaderActivity extends FragmentActivity {

    private final Handler mHideHandler = new Handler();
    private MyHandler myHandler;
    private MaterialToolbar toolbar;
    private CoordinatorLayout layout;
    private AppBarLayout layout2;
    private View view_root;
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
    };
    private boolean isLast;
    private ExtendedFloatingActionButton ext_fab;
    private CircularProgressIndicator indicator;
    private ViewPager2 view2;
    private boolean mVisible;
    private String nextUuid;
    private String pathWord;
    private String chapterName;


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
        String title = intent.getStringExtra(KeyWordSwap.TITLE_TYPE);
        String subtitle = intent.getStringExtra(KeyWordSwap.CHAPTER_TYPE);
        String uuid = intent.getStringExtra(KeyWordSwap.UUID_WORD_TYPE);
        String pathWord = intent.getStringExtra(KeyWordSwap.PATH_WORD_TYPE);

        new Thread(new MyRun(pathWord, uuid)).start();

        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);

        setPathWord(pathWord);

        myHandler = new MyHandler();

        mVisible = true;

        toolbar.setNavigationOnClickListener(view -> finish());


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
            view_root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
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

            if (msg.what == KeyWordSwap.HANDLER_INFO_8_WHAT) {
                String nextUid = (String) msg.obj;
                if (nextUid.equals(KeyWordSwap.NON_JSON)) {
                    isLast = true;
                    ext_fab.hide();
                }
                view2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        if ((getPo() - 1) == position) {
                            if (nextUid.equals(KeyWordSwap.NON_JSON)) {
                                Snackbar.make(view_root, R.string.non_next, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.exit, view -> finish()).show();
                                ext_fab.hide();
                            } else {
                                setNextUuid(nextUid);
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

            if (msg.what == KeyWordSwap.HANDLER_INFO_9_WHAT) {
                String name = (String) msg.obj;
                setChapterName(name);
                toolbar.setSubtitle(name);
            }

            if (msg.what == KeyWordSwap.HANDLER_ERROR) {
                Snackbar.make(view_root, R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, view -> {
                            new Thread(new MyRun(getPathWord(), getNextUuid())).start();
                            new Thread(new My2Run(getPathWord(), getNextUuid())).start();
                        }).show();
            }

        }
    }

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

    private class My2Run implements Runnable {

        private final String pathWord;
        private final String uuid;

        private My2Run(String pathWord, String uuid) {
            this.pathWord = pathWord;
            this.uuid = uuid;
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
