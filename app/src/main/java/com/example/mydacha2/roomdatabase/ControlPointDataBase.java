package com.example.mydacha2.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;


@Database(entities = {ControlPoint.class}, version = 1, exportSchema = false)
public abstract class ControlPointDataBase  extends RoomDatabase {
    public abstract ControlPointDAO controlPointDAO();
}
