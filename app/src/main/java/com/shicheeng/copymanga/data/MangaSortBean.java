package com.shicheeng.copymanga.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MangaSortBean implements Parcelable {
    private String pathName;
    private String pathWord;

    public MangaSortBean(String pathName, String pathWord) {
        this.pathName = pathName;
        this.pathWord = pathWord;
    }

    public MangaSortBean() {

    }

    protected MangaSortBean(Parcel in) {
        pathName = in.readString();
        pathWord = in.readString();
    }

    public static final Creator<MangaSortBean> CREATOR = new Creator<MangaSortBean>() {
        @Override
        public MangaSortBean createFromParcel(Parcel in) {
            return new MangaSortBean(in);
        }

        @Override
        public MangaSortBean[] newArray(int size) {
            return new MangaSortBean[size];
        }
    };

    public String getPathWord() {
        return pathWord;
    }

    public void setPathWord(String pathWord) {
        this.pathWord = pathWord;
    }


    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(pathName);
        dest.writeString(pathWord);
    }
}
