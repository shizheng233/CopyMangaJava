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
import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.data.DataBannerBean;

import java.util.List;

public class BannerMangaAdapter extends RecyclerView.Adapter<BannerMangaAdapter.bannerViewHolder> {

    private final List<DataBannerBean> list;
    private OnItemClickListener onItemClickListener;

    public BannerMangaAdapter(List<DataBannerBean> bannerBeans) {
        this.list = bannerBeans;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public bannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manga_banner_mian, parent, false);

        return new bannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bannerViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        holder.briefText.setText(list.get(position).getBannerBrief());
        Glide.with(holder.itemView)
                .load(list.get(position).getBannerImageUrl())
                .into(holder.bannerImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class bannerViewHolder extends RecyclerView.ViewHolder {

        private final TextView briefText;
        private final ImageView bannerImage;

        public bannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.banner_pic);
            briefText = itemView.findViewById(R.id.brief_text);
        }

    }

     public interface OnItemClickListener{
        void OnItem(int position);
    }

}
