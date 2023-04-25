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
                new MyListTypePoint(1L, context.getString(R.string.lamp), R.mipmap.lamp_off_foreground),
                new MyListTypePoint(2L, context.getString(R.string.two_lamp), R.mipmap.two_lamp_icn_foreground),
                new MyListTypePoint(3L, context.getString(R.string.socket), R.mipmap.elektricheskaya_rozetka_foreground),
                new MyListTypePoint(4L, context.getString(R.string.thermometer), R.mipmap.temperatura_ic_foreground),
                new MyListTypePoint(5L, context.getString(R.string.barometer), R.mipmap.baromet_ic_foreground),
                new MyListTypePoint(6L, context.getString(R.string.tv), R.mipmap.television_foreground),
                new MyListTypePoint(7L, context.getString(R.string.conditioner), R.mipmap.kondicioner_icn_foreground),
                new MyListTypePoint(8L, context.getString(R.string.gas_sensor), R.mipmap.gas_ic_foreground),
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
