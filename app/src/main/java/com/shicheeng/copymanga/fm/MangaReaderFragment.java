package com.shicheeng.copymanga.fm;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.R;

import java.util.concurrent.ExecutionException;

public class MangaReaderFragment extends Fragment {


    private SubsamplingScaleImageView imageView;
    private onFragmentClickListener onFragmentClickListener;
    private CircularProgressIndicator indicator;
    private String url;
    private View view;
    private Bitmap bitmap;
    private  MyHandler handler ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.manga_reader_fragment, container, false);
        imageView = view.findViewById(R.id.iv_reader);
        indicator = view.findViewById(R.id.ind_reader);
        new Thread(new RunB()).start();
        handler = new MyHandler();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentClickListener.onClick(view);
            }
        });
        return view;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setOnFragmentClickListener(MangaReaderFragment.onFragmentClickListener onFragmentClickListener) {
        this.onFragmentClickListener = onFragmentClickListener;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }


    public interface onFragmentClickListener {
        void onClick(View view);
    }

    private class RunB implements Runnable{

        @Override
        public void run() {
            try {
                Bitmap bitmap = Glide.with(view).asBitmap().load(url).submit().get();
                Message message = new Message();
                message.what = 123;
                message.obj = bitmap;
                handler.sendMessage(message);
            } catch (ExecutionException | InterruptedException| JsonSyntaxException e) {
                e.printStackTrace();
                Message message2 = new Message();
                message2.what = KeyWordSwap.HANDLER_ERROR;
                handler.sendMessage(message2);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123){
                Bitmap bitmap3 = (Bitmap) msg.obj;
                imageView.setImage(ImageSource.bitmap(bitmap3));
                indicator.setVisibility(View.GONE);
            }
            if (msg.what == KeyWordSwap.HANDLER_ERROR){
                Snackbar.make(view,R.string.error,Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new RunB()).start();
                    }
                }).show();
            }
        }
    }

}
