package com.shicheeng.copymanga.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.ListBeanManga;
import com.shicheeng.copymanga.MangaInfoActivity;
import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.data.MangaListDataBean;

import java.util.List;

public class MangaListAdapter extends RecyclerView.Adapter<MangaListAdapter.NewViewHolder> {

    List<ListBeanManga> dataList;

    public MangaListAdapter(List<ListBeanManga> list) {
        this.dataList = list;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_manga_list_item, parent, false);

        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        holder.textTitle.setText(dataList.get(position).getNameManga());
        holder.textAuthor.setText(dataList.get(position).getAuthorManga());
        Glide.with(holder.itemView).load(dataList.get(position)
                .getUrlCoverManga())
                .into(holder.image);
        holder.materialCardView.setOnClickListener(view -> {
            Log.i("SC_016"," "+position);
            Intent intent = new Intent();
            intent.setClass(holder.materialCardView.getContext(), MangaInfoActivity.class);
            intent.putExtra(KeyWordSwap.PATH_WORD_TYPE,dataList.get(position).getPathWordManga());
            Log.i("SC_!001"," "+dataList.get(position).getPathWordManga());
            holder.materialCardView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class NewViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textAuthor;
        ImageView image;
        MaterialCardView materialCardView;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            textAuthor = itemView.findViewById(R.id.manga_list_author_text);
            textTitle = itemView.findViewById(R.id.manga_list_text_title);
            image = itemView.findViewById(R.id.manga_list_image_view);
            materialCardView = itemView.findViewById(R.id.manga_list_card_click);

        }
    }

    public interface onItemClickListener{
        void OnItem(int position);
    }

}
