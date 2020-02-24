package com.songtaeheon.memoapp.model;

import java.io.Serializable;

public class Memo implements Serializable {
    private String title;
    private String detail;
    private String thumbnailUri;
    private long id;//db내부 memo id.

    public Memo(){}


    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
