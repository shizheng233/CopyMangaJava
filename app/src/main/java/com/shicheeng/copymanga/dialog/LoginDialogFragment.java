package com.shicheeng.copymanga.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.PersonalDataActivity;
import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.apiName;

import java.io.IOException;
import java.util.Objects;

public class LoginDialogFragment extends BottomSheetDialogFragment {


    private CircularProgressIndicator indicator;
    private MyNewHandler handler;
    private String authorization;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_layout,container,false);
        TextInputLayout inputLayout = view.findViewById(R.id.text_input_token);
        MaterialButton input_btn = view.findViewById(R.id.input_layout_login_btn);
        handler = new MyNewHandler();
        indicator = view.findViewById(R.id.input_layout_login_indicator);
        indicator.setVisibility(View.GONE);
        input_btn.setOnClickListener(view1 -> {
            if (Objects.requireNonNull(inputLayout.getEditText()).getText().toString().isEmpty()){
                inputLayout.setError(getString(R.string.error_non_text));
                return;
            }
            new Thread(new MyNewRunnable()).start();
            setAuthorization(Objects.requireNonNull(inputLayout.getEditText()).getText().toString());
            indicator.setVisibility(View.VISIBLE);
        });

        return view;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    private class MyNewRunnable implements Runnable{

        @Override
        public void run() {
            Message message = new Message();
            message.what = KeyWordSwap.HANDLER_INFO_1_WHAT;
            try {
                message.obj = apiName.mangaUserinfoGet(authorization);
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyNewHandler extends Handler{
        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_INFO_1_WHAT) {
                String s = (String) msg.obj;
                JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();
                if (jsonObject.get("code").getAsInt() == 200){
                    Intent intent = new Intent();
                    intent.setClass(getContext(), PersonalDataActivity.class);
                    intent.putExtra(KeyWordSwap.A_INFO,jsonObject.toString());
                    intent.putExtra(KeyWordSwap.INTENT_KEY_JSON,0);
                    intent.putExtra(KeyWordSwap.B_INFO,authorization);
                    SharedPreferences preferences = requireContext().getSharedPreferences(
                                            KeyWordSwap.ONLY_ONE_KEY_AUTHORIZATION,
                                            Context.MODE_PRIVATE);
                    preferences.edit().putString(KeyWordSwap.FILE_AUTHORIZATION,getAuthorization()).apply();
                    requireContext().startActivity(intent);
                    dismiss();
                }
            }
        }
    }

}
