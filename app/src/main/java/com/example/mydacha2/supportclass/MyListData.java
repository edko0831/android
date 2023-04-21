package com.example.mydacha2.supportclass;

public class MyListData {
    public Long id;
    public String name;
    public String picture_url;
    public String description;

    public MyListData(Long id, String name, String picture_url, String description) {
        this.id = id;
        this.name = name;
        this.picture_url = picture_url;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public String getDescription() {
        return description;
    }
}
