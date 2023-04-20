package com.example.mydacha2.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlWithControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.DAO.UsersDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.Entity.ObjectControl;
import com.example.mydacha2.Entity.ObjectControlWithControlPoint;
import com.example.mydacha2.Entity.Users;

import io.reactivex.rxjava3.annotations.NonNull;

@Database(entities = {
        ControlPoint.class,
        ObjectControl.class,
        ObjectControlWithControlPoint.class,
        Users.class}, version = 3, exportSchema = false)
public abstract class AppDatabase  extends RoomDatabase {

    public abstract ControlPointDAO controlPointDAO();
    public abstract ObjectControlsDAO ObjectControlsDAO();
    public abstract ObjectControlWithControlPointDAO objectControlWithControlPointDAO();
    public abstract UsersDAO usersDAO();

        public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE users " +
                    " ( userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "   displayName TEXT, " +
                    "   password TEXT," +
                    "   roleId INTEGER)" );
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(" Insert into users (displayName, password, roleId) values ('admin', 'admin', 1); ");
        }
    };

    /*
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(" ALTER TABLE control_point ADD COLUMN userId INTEGER; ");
        }
    };

    public static final Migration MIGRATION_5_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE object_with_point" +
                    " (id_object_point INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " object_id INTEGER, point_id INTEGER," +
                    " position_x REAL, position_y REAL)");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(" ALTER TABLE control_point ADD COLUMN topic TEXT");
        }
    };

 */
}