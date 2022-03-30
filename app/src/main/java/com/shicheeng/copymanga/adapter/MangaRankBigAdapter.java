package com.shicheeng.copymanga.adapter;

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

import java.util.List;

public class MangaRankBigAdapter extends RecyclerView.Adapter<MangaRankBigAdapter.NewViewHolder> {

    private final List<ListBeanManga> list;


    public MangaRankBigAdapter(List<ListBeanManga> list) {
        this.list = list;
    }



    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_manga_list_item, parent, false);
        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        holder.textViewTitle.setText(list.get(position).getNameManga());
        holder.textViewAuthor.setText(list.get(position).getAuthorManga());
        Glide.with(holder.imageView.getContext())
                .load(list.get(position).getUrlCoverManga())
                .into(holder.imageView);
        holder.cardView.setOnClickListener(view -> {
            Log.i("SC_016"," "+position);
            Intent intent = new Intent();
            intent.setClass(holder.cardView.getContext(), MangaInfoActivity.class);
            intent.putExtra(KeyWordSwap.PATH_WORD_TYPE,list.get(position).getPathWordManga());
            Log.i("SC_!001"," "+list.get(position).getPathWordManga());
            holder.cardView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class NewViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor;
        ImageView imageView;
        MaterialCardView cardView;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.manga_list_text_title);
            textViewAuthor = itemView.findViewById(R.id.manga_list_author_text);
            imageView = itemView.findViewById(R.id.manga_list_image_view);
            cardView = itemView.findViewById(R.id.manga_list_card_click);
        }
    }

}
