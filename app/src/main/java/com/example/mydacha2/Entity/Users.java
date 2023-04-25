package com.example.mydacha2.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class Users {

    @PrimaryKey(autoGenerate = true)
    public Long userId;
    public String displayName;
    public String password;
    public Long roleId;

    public Users() {
     }

    @Ignore
    public Users(String displayName, String password, Long roleId) {
        this.displayName = displayName;
        this.password = password;
        this.roleId = roleId;
    }
}