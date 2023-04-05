package com.example.mydacha2.Entity;


import androidx.room.Relation;

public class ObjectControlControlPoint {
    public Long id_object_point;
    public Long object_id;
    public Long point_id;

    @Relation(parentColumn = "point_id", entityColumn = "id_control")
    public ControlPoint controlPoint;

    public Long position_x;
    public Long position_y;
}
