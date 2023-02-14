package com.shicheeng.copymanga.data;

public class ListBeanManga {


    private String nameManga;
    private String authorManga;
    private String urlCoverManga;
    private String pathWordManga;

    public ListBeanManga() {

    }

    public ListBeanManga(String nameManga, String authorManga, String urlCoverManga, String pathWordManga) {
        this.authorManga = authorManga;
        this.nameManga = nameManga;
        this.urlCoverManga = urlCoverManga;
        this.pathWordManga = pathWordManga;
    }

    public String getAuthorManga() {
        return authorManga;
    }

    public void setAuthorManga(String authorManga) {
        this.authorManga = authorManga;
    }

    public String getNameManga() {
        return nameManga;
    }

    public void setNameManga(String nameManga) {
        this.nameManga = nameManga;
    }

    public String getUrlCoverManga() {
        return urlCoverManga;
    }

    public void setUrlCoverManga(String urlCoverManga) {
        this.urlCoverManga = urlCoverManga;
    }

    public String getPathWordManga() {
        return pathWordManga;
    }

    public void setPathWordManga(String pathWordManga) {
        this.pathWordManga = pathWordManga;
    }

}
