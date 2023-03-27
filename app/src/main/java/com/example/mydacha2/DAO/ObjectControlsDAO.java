package com.example.mydacha2.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mydacha2.Entity.ObjectControl;

import java.util.List;

@Dao
public interface ObjectControlsDAO {
    @Query("Select * From object_control ")
    List<ObjectControl> select();

    @Query(value = "Select * From object_control Where id = :id_object")
    ObjectControl selectId(int id_object);

    @Insert
    void insert(ObjectControl items);
    @Update
    void update(ObjectControl items);

    @Delete
    default void delete(ObjectControl item) {

    }

    @Query("DELETE FROM object_control Where id = :id_object")
    void deleteId(int id_object);

    @Query("Select count(*) From object_control ")
    int count();
}
