package com.example.mydacha2.supportclass;

public class MyListMain {
    private Long id;
    private String name;
    private int imgId;

    public MyListMain(Long id, String name, int imgId) {
        this.id = id;
        this.name = name;
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public int getImage() {
        return imgId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
