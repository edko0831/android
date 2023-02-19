package com.example.mydacha2.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "control_point")
public class СontrolPoint {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    public String name;
    public String description;
    public int imgId;
    public String ipaddress;


}
