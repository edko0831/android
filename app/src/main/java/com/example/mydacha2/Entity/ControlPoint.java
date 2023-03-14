package com.example.mydacha2.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "control_point")
public class ControlPoint {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public Long id_control;
    public String name;
    public String description;
    public String type_point;
    public String picture_url;
    public String executable_code;

    @Override
    public String toString(){
        return name;
    }
}
