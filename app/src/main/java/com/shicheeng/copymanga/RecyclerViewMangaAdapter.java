package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewMangaAdapter extends RecyclerView.Adapter<RecyclerViewMangaAdapter.MangaViewHolder> {

    private final List<ListBeanManga> mangaList;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewMangaAdapter(List<ListBeanManga> list) {
        this.mangaList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //创建ViewHolder
    @NonNull
    @Override
    public RecyclerViewMangaAdapter.MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                       int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_manga_detail, parent, false);
        return new MangaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewMangaAdapter.MangaViewHolder holder
            , @SuppressLint("RecyclerView") int position) {
        holder.mangaTitle.setText(mangaList.get(position).getNameManga());
        holder.mangaAuthor.setText(mangaList.get(position).getAuthorManga());
        Glide.with(holder.itemView)
                .load(mangaList.get(position).getUrlCoverManga())
                .centerCrop().
                into(holder.mangaCover);
        holder.itemView.setOnClickListener(view -> {
            onItemClickListener.onItem(position);

        });

    }

    @Override
    public int getItemCount() {
        return mangaList.size();
    }

    public class MangaViewHolder extends RecyclerView.ViewHolder {

        private TextView mangaTitle,mangaAuthor;
        private ImageView mangaCover;
        public MangaViewHolder(@NonNull View itemView) {
            super(itemView);
            mangaAuthor = itemView.findViewById(R.id.author_manga_item);
            mangaTitle = itemView.findViewById(R.id.title_manga_item);
            mangaCover = itemView.findViewById(R.id.cover_image);
        }
    }

    interface OnItemClickListener{
       void onItem(int position);
    }

}
