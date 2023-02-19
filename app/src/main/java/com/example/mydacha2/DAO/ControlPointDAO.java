package com.example.mydacha2.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mydacha2.Entity.СontrolPoint;

import java.util.List;

@Dao
public interface ControlPointDAO {
    @Query("Select * From control_point ")
    List<СontrolPoint> select();

    @Query(value = "Select * From control_point Where id = :id")
    СontrolPoint selectId(int id);

    @Insert
    void insert(СontrolPoint items);
    @Update
    void update(СontrolPoint items);
    @Delete
    void delete(СontrolPoint item);

}
