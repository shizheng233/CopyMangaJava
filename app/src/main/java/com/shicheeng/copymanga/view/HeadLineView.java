package com.shicheeng.copymanga.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shicheeng.copymanga.R;

public class HeadLineView extends LinearLayout {

    private TextView handLineText;
    private LinearLayout linearLayout;
    private onHeadClickListener onHeadClickListener;

    private void initView(Context context){
        View.inflate(context, R.layout.manga_headline_1,this);
        handLineText = findViewById(R.id.title_id);
        linearLayout = findViewById(R.id.linear_id);
        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onHeadClickListener.onClick(view);
            }
        });
    }

    public HeadLineView(Context context) {
        super(context);
        initView(context);
    }

    public HeadLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        @SuppressLint("Recycle") TypedArray array =
                context.obtainStyledAttributes(attrs,R.styleable.HeadLineView);
        String title = array.getString(R.styleable.HeadLineView_headTitle);
        setHandLineText(title);
    }


    public void setHandLineText(int redID){
        handLineText.setText(redID);
    }

    public void setHandLineText(String text){
        handLineText.setText(text);
    }

    public String getHandLineText(){
        return handLineText.getText().toString();
    }

    public void setOnHeadClickListener(HeadLineView.onHeadClickListener onHeadClickListener) {
        this.onHeadClickListener = onHeadClickListener;
    }

    public interface onHeadClickListener{
        void onClick(View view);
    }

}
