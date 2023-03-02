package com.example.mydacha2.supportclass;

import com.example.mydacha2.Entity.ControlPoint;

public class MyListControlPoint {
    private final ControlPoint controlPoint;

    public MyListControlPoint(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    public String getDescription() {
        return controlPoint.name;
    }

     public String getImgUrl() {
        return controlPoint.picture_url;
    }

    public long getId() {
        return controlPoint.id;
    }
}
