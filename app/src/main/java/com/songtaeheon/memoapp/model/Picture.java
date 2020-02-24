package com.songtaeheon.memoapp.model;

import java.io.Serializable;

public class Picture implements Serializable {
    private String uri;
    private long id;//db id

    public Picture(String uri){
        this.uri = uri;
        this.id = -1;
    }

    public Picture(int id, String uri){
        this.uri = uri;
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public long getId() {
        return id;
    }
}
