package com.shicheeng.copymanga.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.ListBeanManga;
import com.shicheeng.copymanga.MangaInfoActivity;
import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.data.MangaRankOutsideBean;

import java.util.ArrayList;
import java.util.List;

public class MangaRankOutsideAdapter extends RecyclerView.Adapter<MangaRankOutsideAdapter.OViewHolder> {

    private final List<MangaRankOutsideBean> beanOfRankOutside;

    public MangaRankOutsideAdapter(List<MangaRankOutsideBean> list) {
        this.beanOfRankOutside = list;
    }

    @NonNull
    @Override
    public OViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_manga_rank_back, parent, false);

        return new OViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OViewHolder holder, int position) {

        holder.tv.setText(beanOfRankOutside.get(position).getTitleMange());
        JsonArray array = beanOfRankOutside.get(position).getArrayOnMange();
        List<ListBeanManga> myList_1 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ListBeanManga beanManga = new ListBeanManga();
            //推荐组下面的各个jsonObject，因为有第二个jsonOBj，所以需要再次获取一次
            JsonObject jsonObject_1 = array.get(i).getAsJsonObject().getAsJsonObject("comic");
            beanManga.setNameManga(jsonObject_1.get("name").getAsString());
            beanManga.setUrlCoverManga(jsonObject_1.get("cover").getAsString());
            beanManga.setPathWordManga(jsonObject_1.get("path_word").getAsString());
            //获取作者列表，判定是否大于一位作家
            JsonArray mangaAuthorList = jsonObject_1.get("author").getAsJsonArray();
            if (mangaAuthorList.size() > 1) {
                beanManga.setAuthorManga(mangaAuthorList.get(0)
                        .getAsJsonObject()
                        .get("name")
                        .getAsString() + " 等");
            } else {
                beanManga.setAuthorManga(mangaAuthorList.get(0)
                        .getAsJsonObject()
                        .get("name")
                        .getAsString());
            }
            myList_1.add(beanManga);
        }
        RankMangaAdapter adapter_1 = new RankMangaAdapter(myList_1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(),LinearLayoutManager.VERTICAL,false);
        holder.viewR.setLayoutManager(layoutManager);
        holder.viewR.setAdapter(adapter_1);
        adapter_1.setOnItemClickListener(new RankMangaAdapter.OnItemClickListener() {
            @Override
            public void OnItem(int position) {
                Log.i("SC_015"," "+position);
                Intent intent = new Intent();
                intent.setClass(holder.viewR.getContext(), MangaInfoActivity.class);
                intent.putExtra(KeyWordSwap.PATH_WORD_TYPE,myList_1.get(position).getPathWordManga());
                Log.i("SC_!001"," "+myList_1.get(position).getPathWordManga());
                holder.viewR.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beanOfRankOutside.size();
    }

    public class OViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView viewR;
        private final TextView tv;

        public OViewHolder(@NonNull View itemView) {
            super(itemView);
            viewR = itemView.findViewById(R.id.recycler_manga_rank);
            tv = itemView.findViewById(R.id.title_text_manga_rank);

        }
    }
}
