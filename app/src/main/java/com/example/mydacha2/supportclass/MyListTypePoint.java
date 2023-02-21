package com.example.mydacha2.supportclass;

import com.example.mydacha2.R;

public class MyListTypePoint {
    long id;
    String point;
    int icons;

    public MyListTypePoint(long id, String point, int icons) {
        this.id = id;
        this.point =point;
        this.icons = icons;
    }

    public static MyListTypePoint[] getListTypePoint(){
        return new MyListTypePoint[]{
                new MyListTypePoint(1L, "object_control", R.mipmap.icons8_object_controls_94_foreground),
                new MyListTypePoint(2L, "connectWiFi", R.mipmap.icons8_search_satellites_94_foreground),
                new MyListTypePoint(3L, "action_settings", R.mipmap.icons8_gear_94_foreground),
                new MyListTypePoint(4L, "Dialer", android.R.drawable.ic_dialog_dialer),
                new MyListTypePoint(5L, "Alert", android.R.drawable.ic_dialog_alert),
                new MyListTypePoint(6L, "Map", android.R.drawable.ic_dialog_map)
        };

    }

    public long getId() {
        return id;
    }

    public String getPoint() {
        return point;
    }

    public int getIcons() {
        return icons;
    }
}
