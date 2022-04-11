package com.shicheeng.copymanga.util;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.shicheeng.copymanga.KeyWordSwap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    private final Context context;

    public FileUtil(Context context) {
        this.context = context;
    }

    public String getFileSave() {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(KeyWordSwap.FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        }
        return stringBuilder.toString();
    }

    public void setFileSave(String text) {

        try {
            FileOutputStream fos = context.openFileOutput(KeyWordSwap.FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Log.i("SSS", "kaishi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
