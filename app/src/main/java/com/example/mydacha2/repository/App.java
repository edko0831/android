package com.example.mydacha2.repository;


import android.content.Context;

import androidx.room.Room;

import com.example.mydacha2.roomdatabase.AppDatabase;

public class App {

    public static App instance;
    String DB_NAME = "db_myDachaNew";
    private final AppDatabase database;

    private App(Context context) {
        instance = this;
        database = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .addMigrations(AppDatabase.MIGRATION_1_2,
                               AppDatabase.MIGRATION_2_3)
         //                      AppDatabase.MIGRATION_3_4)
        //                      AppDatabase.MIGRATION_4_5)
                .allowMainThreadQueries()
        //       .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized App getInstance(Context context) {
        if(instance == null){
          instance = new App(context);
        }
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
