package com.shicheeng.copymanga.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shicheeng.copymanga.R;

public class BottomDialogFragment extends BottomSheetDialogFragment {

    private String title,sTitle,detail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_tab,container,false);
        TextView textViewTitle = view.findViewById(R.id.text_dialog_title);
        TextView textViewSTitle = view.findViewById(R.id.text_dialog_al);
        TextView textViewDetail = view.findViewById(R.id.text_dialog_detail);
        textViewDetail.setText(this.getDetail());
        textViewTitle.setText(this.getTitle());
        textViewSTitle.setText(this.getsTitle());

        return view;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
