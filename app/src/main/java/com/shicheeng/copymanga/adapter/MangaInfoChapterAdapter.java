package com.shicheeng.copymanga.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean;

import java.util.List;

public class MangaInfoChapterAdapter extends RecyclerView.Adapter<MangaInfoChapterAdapter.MyViewHolder> {

    private final List<MangaInfoChapterDataBean> beanList;
    private OnItemClickListener onItemClickListener;

    public MangaInfoChapterAdapter(List<MangaInfoChapterDataBean> myList) {
        this.beanList = myList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MangaInfoChapterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent
            , int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chapter
                , parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaInfoChapterAdapter.MyViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        holder.tvTitle.setText(beanList.get(position).getChapterTitle());
        holder.tvTime.setText(beanList.get(position).getChapterTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    public interface OnItemClickListener {
        void OnItem(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime;
        ImageView imageViewDownload;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.textTime_1);
            tvTitle = itemView.findViewById(R.id.textTitle_1);
            imageViewDownload = itemView.findViewById(R.id.image_download);
        }
    }

}
