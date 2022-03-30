package com.shicheeng.copymanga.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.data.ChipTextBean;

import java.util.List;

public class MangaInfoChipperAdapter extends
        RecyclerView.Adapter<MangaInfoChipperAdapter.MeViewHolder> {

    List<ChipTextBean> texts;
    int resId;
    private Chip chip1;

    public MangaInfoChipperAdapter(List<ChipTextBean> list){
        this.texts = list;
    }

    public void setIcon(int resId){
        this.resId = resId;
    }

    @NonNull
    @Override
    public MeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chip_1,parent,false);

        return new MeViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MeViewHolder holder, int position) {
        holder.chip.setText(texts.get(position).getText());
        if (resId != 0){
            holder.chip.setChipIcon(holder.itemView.getContext().getDrawable(resId));
        }else {
            holder.chip.setChipIcon(holder.itemView.getContext().getDrawable(R.drawable.ic_manga_author));
        }


    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    public  class MeViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public MeViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip_chip);
        }
    }



}
