package com.shicheeng.copymanga.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonSyntaxException;
import com.shicheeng.copymanga.KeyWordSwap;
import com.shicheeng.copymanga.R;
import com.shicheeng.copymanga.adapter.PopMenuAdapter;
import com.shicheeng.copymanga.data.MangaSortBean;
import com.shicheeng.copymanga.json.MangaSortJson;

import java.io.IOException;
import java.util.List;

public class SortDialogFragment extends BottomSheetDialogFragment {

    private TextInputLayout menuSort;
    private AutoCompleteTextView autoCompleteTextView_1, autoCompleteTextView_2;
    private TextInputLayout menuOrder;
    private ListPopupWindow listPopupWindowSort, listPopupWindowOrder;
    private MyHandler myHandler;
    private onDialogBottomPopMenuClickListener onDialogBottomPopMenuClickListener;
    private onDialogTopPopMenuClickListener onDialogTopPopMenuClickListener;
    private onButtonClickListener onButtonClickListener;
    private Thread thread;
    private CircularProgressIndicator circularProgressIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sort_tab, container, false);

        menuSort = v.findViewById(R.id.menu_sort);
        menuOrder = v.findViewById(R.id.menu_order);
        autoCompleteTextView_1 = v.findViewById(R.id.auto_1);
        autoCompleteTextView_2 = v.findViewById(R.id.auto_2);
        MaterialButton button = v.findViewById(R.id.btn_filter);
        circularProgressIndicator = v.findViewById(R.id.circle_pro_bar);

        button.setOnClickListener(view -> {
            onButtonClickListener.onClick(view);
            menuOrder.setEndIconActivated(true);
        });

        menuSort.setHint(R.string.label_is_loading);
        menuOrder.setHint(R.string.order);

        listPopupWindowSort =
                new ListPopupWindow(requireContext(), null,
                        androidx.appcompat.R.attr.listPopupWindowStyle);
        listPopupWindowOrder =
                new ListPopupWindow(requireContext(), null,
                        androidx.appcompat.R.attr.listPopupWindowStyle);

        listPopupWindowSort.setAnchorView(menuSort);
        listPopupWindowOrder.setAnchorView(menuOrder);
        List<MangaSortBean> strings_2 = MangaSortJson.getOrder();
        PopMenuAdapter adapter_2 = new PopMenuAdapter(strings_2);
        autoCompleteTextView_2.setText(strings_2.get(1).getPathName());
        listPopupWindowOrder.setAdapter(adapter_2);

        autoCompleteTextView_2.setOnClickListener(view -> listPopupWindowOrder.show());
        menuOrder.setEndIconOnClickListener(view -> {
            listPopupWindowOrder.show();

        });
        listPopupWindowOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onDialogBottomPopMenuClickListener.onItemClick(strings_2, i);
                autoCompleteTextView_2.setText(strings_2.get(i).getPathName());
                listPopupWindowOrder.dismiss();
            }
        });

        myHandler = new MyHandler();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thread = new Thread(new MyRun());
        thread.start();

    }

    public void setOnButtonClickListener
            (SortDialogFragment.onButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public void setOnDialogBottomPopMenuClickListener
            (SortDialogFragment.onDialogBottomPopMenuClickListener
                     onDialogBottomPopMenuClickListener) {
        this.onDialogBottomPopMenuClickListener = onDialogBottomPopMenuClickListener;
    }

    public void setOnDialogTopPopMenuClickListener
            (SortDialogFragment.onDialogTopPopMenuClickListener onDialogTopPopMenuClickListener) {
        this.onDialogTopPopMenuClickListener = onDialogTopPopMenuClickListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    public interface onDialogTopPopMenuClickListener {
        void onItemClick(List<MangaSortBean> list, int position);
    }

    public interface onDialogBottomPopMenuClickListener {
        void onItemClick(List<MangaSortBean> list, int position);
    }

    public interface onButtonClickListener {
        void onClick(View view);
    }

    private class MyRun implements Runnable {
        @Override
        public void run() {
            try {
                List<MangaSortBean> list = MangaSortJson.getSort();
                Message message = new Message();
                message.what = KeyWordSwap.HANDLER_INFO_4_WHAT;
                message.obj = list;
                myHandler.sendMessage(message);

            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = KeyWordSwap.HANDLER_ERROR;
                myHandler.sendMessage(message);
            }
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == KeyWordSwap.HANDLER_INFO_4_WHAT) {
                List<MangaSortBean> strings = (List<MangaSortBean>) msg.obj;
                PopMenuAdapter adapter =
                        new PopMenuAdapter(strings);
                listPopupWindowSort.setAdapter(adapter);
                menuSort.setHint(R.string.sort);
                circularProgressIndicator.setVisibility(View.GONE);
                menuSort.setEndIconOnClickListener(view -> listPopupWindowSort.show());
                autoCompleteTextView_1.setOnClickListener(view -> listPopupWindowSort.show());
                listPopupWindowSort.setOnItemClickListener((adapterView, view, i, l) -> {
                    onDialogTopPopMenuClickListener.onItemClick(strings, i);
                    autoCompleteTextView_1.setText(strings.get(i).getPathName());
                    listPopupWindowSort.dismiss();
                });
            }
            if (msg.what == KeyWordSwap.HANDLER_ERROR) {
                Snackbar.make(requireView(), R.string.error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, view -> {
                            new Thread(new MyRun()).start();
                            circularProgressIndicator.setVisibility(View.VISIBLE);
                        }).show();
                circularProgressIndicator.setVisibility(View.GONE);
            }

        }

    }
}
