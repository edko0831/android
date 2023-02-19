package com.example.mydacha2.supportclass;

import com.example.mydacha2.Entity.ObjectControl;


public class MyListObjectControl {

    private final ObjectControl ObjectControl;

    public MyListObjectControl(ObjectControl ObjectControl) {
        this.ObjectControl = ObjectControl;
    }

    public String getDescription() {
        return ObjectControl.name;
    }

    public int getImgId() {
        return ObjectControl.picture_id;
    }

    public String getImgUrl() {
        return ObjectControl.picture_url;
    }

    public long getId() {
        return ObjectControl.id;
    }
}