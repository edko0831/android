package com.example.mydacha2.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlWithControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.Entity.ObjectControl;
import com.example.mydacha2.Entity.ObjectControlWithControlPoint;

import io.reactivex.rxjava3.annotations.NonNull;

@Database(entities = {
        ControlPoint.class,
        ObjectControl.class,
        ObjectControlWithControlPoint.class}, version = 1, exportSchema = false)
public abstract class AppDatabase  extends RoomDatabase {

    public abstract ControlPointDAO controlPointDAO();
    public abstract ObjectControlsDAO ObjectControlsDAO();
    public abstract ObjectControlWithControlPointDAO objectControlWithControlPointDAO();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE control_point " +
                    " ( id_control INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "   name TEXT, " +
                    "   description TEXT, " +
                    "   type_point TEXT, " +
                    "   picture_url TEXT )");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE control_point ADD COLUMN executable_code TEXT");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE object_with_point" +
                    " (id_object_point INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " object_id INTEGER, point_id INTEGER," +
                    " position_x REAL, position_y REAL)");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(" ALTER TABLE control_point ADD COLUMN topic TEXT");
        }
    };
}