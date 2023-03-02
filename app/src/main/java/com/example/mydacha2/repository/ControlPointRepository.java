package com.example.mydacha2.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.mydacha2.roomdatabase.ControlPointDataBase;


public class ControlPointRepository {

    private final ControlPointDataBase controlPoint;

    public ControlPointRepository(Context context) {
        String DB_NAME = "db_myDacha";
        controlPoint = Room.databaseBuilder(context, ControlPointDataBase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public ControlPointDataBase getControlPoint() {
        return controlPoint;
    }
}

