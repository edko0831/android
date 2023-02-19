package com.example.mydacha2.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.mydacha2.roomdatabase.ObjectControlDataBase;

public class ObjectControlRepository {

    private final ObjectControlDataBase ObjectControl;

    public ObjectControlRepository(Context context) {
        String DB_NAME = "db_myDacha";
        ObjectControl = Room.databaseBuilder(context, ObjectControlDataBase.class, DB_NAME)
            //    .addMigrations(ObjectControlDataBase.MIGRATION_1_2)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public ObjectControlDataBase getObjectControl() {
        return ObjectControl;
    }

}
