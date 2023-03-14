package com.example.mydacha2.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.mydacha2.Entity.ControlPoint;

import java.util.List;

@Dao
public interface ControlPointDAO {
    @Query("Select * From control_point ")
    List<ControlPoint> select();

    @Query(value = "Select * From control_point Where id_control = :id_control")
    ControlPoint selectId(int id_control);

    @Insert
    void insert(ControlPoint items);
    @Update
    void update(ControlPoint items);
    @Delete
    void delete(ControlPoint item);

    @Query(value = "Select * From control_point Where name = :name")
    ControlPoint selectName(String name);
}
