package com.example.mydacha2.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.mydacha2.Entity.ObjectControlControlPoint;
import com.example.mydacha2.Entity.ObjectControlWithControlPoint;

import java.util.List;

@Dao
public interface ObjectControlWithControlPointDAO {

    @Transaction
    @Query(value = "Select * From object_with_point a " +
                      " Inner Join control_point c on a.point_id = c.id_control Where a.object_id = :id_object")
    List<ObjectControlControlPoint> selectId(int id_object);

    @Insert
    void insert(ObjectControlWithControlPoint items);
    @Update
    void update(ObjectControlWithControlPoint items);

    @Delete
    void delete(ObjectControlWithControlPoint item);

    @Query("DELETE FROM object_with_point " +
            " Where id_object_point = :id_object_point")
    void deleteId(int id_object_point);

    @Query("UPDATE object_with_point SET " +
            " point_id = :point_id," +
            " position_x = :position_x," +
            " position_y = :position_y" +
            " WHERE id_object_point = :id_object_point")
    void updateId( long point_id, long position_x, long position_y, long id_object_point);

    @Query("Select count(*) From object_with_point ")
    int count();
}
