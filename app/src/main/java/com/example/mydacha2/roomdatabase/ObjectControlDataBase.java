package com.example.mydacha2.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ObjectControl;

@Database(entities = {ObjectControl.class}, version = 1)
public abstract class ObjectControlDataBase extends RoomDatabase {

    public abstract ObjectControlsDAO ObjectControlsDAO();
/*
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE object_control ADD COLUMN picture_url TEXT");
        }
    };

 */
}
