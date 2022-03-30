package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.shicheeng.copymanga.fm.DayRankUFragment;

public class RankMangaActivity extends FragmentActivity {

    private ViewPager2 viewPager;
    private ViewOnClass adapter;
    private BottomNavigationView viewN;
    private LinearProgressIndicator indicator;
    private MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_manga_layout);
        viewPager = findViewById(R.id.view_pager_rank);
        viewN = findViewById(R.id.bottom_navigation);
        indicator = findViewById(R.id.progress_indicator_rank);
        toolbar = findViewById(R.id.rank_manga_toolbar);
        viewN.setSelectedItemId(R.id.page_1);
        toolbar.setSubtitle(R.string.comic_rank);
        adapter = new ViewOnClass(this, KeyWordSwap.DAY_RANK);
        viewPager.setAdapter(adapter);
        toolbar.setTitle(R.string.day_rank);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        viewN.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_1:
                    viewPager.setAdapter(new ViewOnClass(this, KeyWordSwap.DAY_RANK));
                    indicator.setVisibility(View.VISIBLE);
                    toolbar.setTitle(R.string.day_rank);
                    Log.i("TAG_RMA", "dianji");
                    return true;
                case R.id.page_2:
                    viewPager.setAdapter(new ViewOnClass(this, KeyWordSwap.WEEK_RANK));
                    indicator.setVisibility(View.VISIBLE);
                    toolbar.setTitle(R.string.week_rank);
                    return true;
                case R.id.page_3:
                    viewPager.setAdapter(new ViewOnClass(this, KeyWordSwap.MONTH_RANK));
                    indicator.setVisibility(View.VISIBLE);
                    toolbar.setTitle(R.string.month_rank);
                    return true;
                case R.id.page_4:
                    viewPager.setAdapter(new ViewOnClass(this, KeyWordSwap.TOTAL_RANK));
                    indicator.setVisibility(View.VISIBLE);
                    toolbar.setTitle(R.string.all_rank);
                    return true;
            }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ViewOnClass extends FragmentStateAdapter {

        private final String type;

        public ViewOnClass(@NonNull FragmentActivity fragmentActivity, String type) {
            super(fragmentActivity);
            this.type = type;
            Log.i("TAG_RMA", type);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new DayRankUFragment(type, indicator,0);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
}
