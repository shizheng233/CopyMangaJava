package com.shicheeng.copymanga.data;

import com.google.gson.JsonArray;

public class MangaRankOutsideBean {
    private String titleMange;
    private JsonArray arrayOnMange;

    public JsonArray getArrayOnMange() {
        return arrayOnMange;
    }

    public void setArrayOnMange(JsonArray arrayOnMange) {
        this.arrayOnMange = arrayOnMange;
    }

    public String getTitleMange() {
        return titleMange;
    }

    public void setTitleMange(String titleMange) {
        this.titleMange = titleMange;
    }
}
