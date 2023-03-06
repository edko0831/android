package com.example.mydacha2.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.Entity.ObjectControl;

import io.reactivex.rxjava3.annotations.NonNull;

@Database(entities = {
        ControlPoint.class,
        ObjectControl.class}, version = 2, exportSchema = false)
public abstract class AppDatabase  extends RoomDatabase {

    public abstract ControlPointDAO controlPointDAO();
    public abstract ObjectControlsDAO ObjectControlsDAO();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE control_point ADD COLUMN executable_code TEXT");
        }
    };
}