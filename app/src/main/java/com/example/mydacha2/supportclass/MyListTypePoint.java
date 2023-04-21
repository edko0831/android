package com.example.mydacha2.supportclass;

import android.content.Context;

import com.example.mydacha2.R;

public class MyListTypePoint {
    long id;
    String point;
    int icons;

    public MyListTypePoint(long id, String point, int icons) {
        this.id = id;
        this.point = point;
        this.icons = icons;
    }

    public static MyListTypePoint[] getListTypePoint(Context context){
        return new MyListTypePoint[]{
                new MyListTypePoint(1L, context.getString(R.string.lamp), R.mipmap.icons8_object_controls_94_foreground),
                new MyListTypePoint(2L, context.getString(R.string.two_lamp), R.mipmap.icons8_search_satellites_94_foreground),
                new MyListTypePoint(3L, context.getString(R.string.socket), R.mipmap.icons8_gear_94_foreground),
                new MyListTypePoint(4L, context.getString(R.string.thermometer), android.R.drawable.ic_dialog_dialer),
                new MyListTypePoint(5L, context.getString(R.string.barometer), android.R.drawable.ic_dialog_dialer),
                new MyListTypePoint(6L, context.getString(R.string.tv), android.R.drawable.ic_dialog_alert),
                new MyListTypePoint(7L, context.getString(R.string.conditioner), android.R.drawable.ic_dialog_map),
                new MyListTypePoint(8L, context.getString(R.string.gas_sensor), android.R.drawable.ic_dialog_map),
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
