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
    @Query(value = "Select * From object_control_with_control_point a " +
                      " Inner Join control_point c on a.control_point_id = c.id_control Where a.object_control_id = :id_object")
    List<ObjectControlControlPoint> selectId(int id_object);

    @Insert
    void insert(ObjectControlWithControlPoint items);
    @Update
    void update(ObjectControlWithControlPoint items);

    @Delete
    void delete(ObjectControlWithControlPoint item);

    @Query("DELETE FROM object_control_with_control_point " +
            " Where id_object_point = :id_object_point")
    void deleteId(int id_object_point);

    @Query("Update object_control_with_control_point Set " +
            " control_point_id = :control_point_id," +
            " position_x = :position_x," +
            " position_y = :position_y" +
            " Where id_object_point = :id_object_point")
    void updateId( long control_point_id, long position_x, long position_y, long id_object_point);

    @Query("Select count(*) From object_control_with_control_point ")
    int count();
}
