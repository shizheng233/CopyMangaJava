package com.shicheeng.copymanga.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.data.MangaSortBean;

import java.util.List;

public class PopMenuAdapter extends BaseAdapter {

    List<MangaSortBean> beans;

    public PopMenuAdapter(List<MangaSortBean> list) {
        this.beans = list;
    }


    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int i) {
        return beans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_popup_window_item, viewGroup, false);
        TextView textView = view.findViewById(R.id.text_list_item);
        textView.setText(beans.get(i).getPathName());

        return view;
    }
}