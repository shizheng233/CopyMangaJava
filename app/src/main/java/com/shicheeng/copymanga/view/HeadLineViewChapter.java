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

import org.w3c.dom.Text;

public class HeadLineViewChapter extends LinearLayout {

    private TextView textView;

    private void init(Context context){
        View.inflate(context, R.layout.manga_headline_2,this);
        textView = findViewById(R.id.text_title);
    }

    public HeadLineViewChapter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        @SuppressLint("Recycle") TypedArray typedArray =
                context.obtainStyledAttributes(attrs,R.styleable.HeadLineViewChapter);
        String title = typedArray.getString(R.styleable.HeadLineViewChapter_headChapterTitle);
        setTitle(title);
    }

    public HeadLineViewChapter(Context context){
        super(context);
        init(context);
    }

    public void setTitle(String string){
        textView.setText(string);
    }

    public void setTitle(int resId){
        textView.setText(resId);
    }

    public String getTitle(){
        return textView.getText().toString();
    }

}
