package com.shicheeng.copymanga.fm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.HotMangaActivity;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.ListBeanManga;
import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.adapter.MangaRankBigAdapter;
import com.shicheeng.copymanga.json.MangaRankJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DayRankUFragment extends Fragment {
    private final List<ListBeanManga> list = new ArrayList<>();
    private final MangaRankBigAdapter adapter = new MangaRankBigAdapter(list);
    private final String type;
    private int offset = 0;
    private MyHandler handler;
    private final LinearProgressIndicator indicator;

    public DayRankUFragment(String type, LinearProgressIndicator indicator, int offset1) {
        this.type = type;
        //Log.i("YYY",type);
        this.indicator = indicator;
        this.offset = offset1;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rank_manga_fragment, container, false);
        RecyclerView viewRecRank = view.findViewById(R.id.recycler_fragment_rank);
        viewRecRank.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2,
                RecyclerView.VERTICAL, false);
        viewRecRank.setLayoutManager(layoutManager);
        handler = new MyHandler();
        viewRecRank.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSliding = false;

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
                        Log.i("DRUF",offset+"");
                        new Thread(new MyRun()).start();
                        indicator.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSliding = dy > 0;
            }

        });

        new Thread(new MyRun()).start();
        return view;
    }

    private class MyRun implements Runnable {

        @Override
        public void run() {
            Message message = new Message();
            try {
                message.obj = MangaRankJson.rankGet(offset, type);
                message.what = KeyWordSwap.HANDLER_INFO_6_WHAT;
                handler.sendMessage(message);
                Log.i("TAG_DRUF", "HASH");
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message1 = new Message();
                message1.what = KeyWordSwap.HANDLER_ERROR;
                handler.sendMessage(message1);

            }
        }
    }

    private class MyHandler extends Handler {


        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_INFO_6_WHAT) {
                Collection<ListBeanManga> mangas = (Collection<ListBeanManga>) msg.obj;
                list.addAll(mangas);
                indicator.setVisibility(View.GONE);
            }
            if (msg.what == KeyWordSwap.HANDLER_ERROR) {
                Snackbar.make(requireView(), R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, view -> {
                            new Thread(new MyRun()).start();
                            indicator.setVisibility(View.INVISIBLE);
                        })
                        .show();
                indicator.setVisibility(View.GONE);
            }

        }
    }


}
