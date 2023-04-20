package com.example.mydacha2.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class Users {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public Long userId;
    public String displayName;
    public String password;
    public Long roleId;

    public Users() {
     }

    public Users(String displayName, String password, Long roleId) {
        this.displayName = displayName;
        this.password = password;
        this.roleId = roleId;
    }
}