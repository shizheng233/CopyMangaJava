package com.shicheeng.copymanga.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shicheeng.copymanga.ListBeanManga;
import com.shicheeng.copymanga.R;

import java.util.List;

public class RankMangaAdapter extends RecyclerView.Adapter<RankMangaAdapter.VH> {

    private final List<ListBeanManga> myList;
    private OnItemClickListener onItemClickListener;

    public RankMangaAdapter(List<ListBeanManga> mangaList) {
        this.myList = mangaList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_rank_manga_detail, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, @SuppressLint("RecyclerView") int position) {
        holder.textName.setText(myList.get(position).getNameManga());
        holder.textAuthor.setText(myList.get(position).getAuthorManga());
        Glide.with(holder.itemView)
                .load(myList.get(position).getUrlCoverManga())
                .into(holder.imageCover);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private TextView textName,textAuthor;
        private ImageView imageCover;

        public VH(@NonNull View itemView) {
            super(itemView);
            textAuthor = itemView.findViewById(R.id.rank_manga_author);
            textName = itemView.findViewById(R.id.rank_manga_name);
            imageCover = itemView.findViewById(R.id.cover_rank);

        }
    }

    interface OnItemClickListener{
        void OnItem(int position);
    }
}
