package com.shicheeng.copymanga.data;

public class DataBannerBean {
    private String bannerImageUrl;
    private String bannerBrief;
    private String uuidManga;

    public DataBannerBean() {
    }

    public String getBannerBrief() {
        return bannerBrief;
    }

    public void setBannerBrief(String bannerBrief) {
        this.bannerBrief = bannerBrief;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getUuidManga() {
        return uuidManga;
    }

    public void setUuidManga(String uuidManga) {
        this.uuidManga = uuidManga;
    }
}
