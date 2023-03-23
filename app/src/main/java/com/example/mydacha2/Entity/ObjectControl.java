package com.example.mydacha2.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "object_control")
public class ObjectControl {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Long id_object;
    public String name;
    public String description;
    public Integer picture_id;
    public String picture_url;

}

