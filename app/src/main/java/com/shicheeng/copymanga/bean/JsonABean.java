package com.shicheeng.copymanga.bean;

public class JsonABean {

    public String nameManga;
    public String chapter;
    public String pathWord;
    public String uuid;
    public String coverUrl;


    public JsonABean(String nameManga,String chapter,String pathWord,String uuid,String coverUrl){
        this.chapter = chapter;
        this.nameManga = nameManga;
        this.pathWord = pathWord;
        this.uuid = uuid;
        this.coverUrl = coverUrl;
    }


}
