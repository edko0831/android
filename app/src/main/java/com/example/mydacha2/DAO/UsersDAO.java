package com.example.mydacha2.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mydacha2.Entity.Users;

import java.util.List;

@Dao
public interface UsersDAO {
    @Query("Select * From users ")
    List<Users> select();

    @Query(value = "Select * From users Where displayName = :displayName and password = :password;")
    Users selectUser(String displayName, String password);

    @Insert
    void insert(Users items);
    @Update
    void update(Users items);
    @Delete
    void delete(Users item);

    @Query(value = "Select * From users Where userId = :intValue;")
    Users selectId(int intValue);

    @Query(value = "Select * From users Where roleId = :roleId;")
    List<Users> selectRole(int roleId);
}
