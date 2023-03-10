package com.example.mydacha2.Entity;


import androidx.room.Relation;

import java.util.List;

public class ObjectControlControlPoint {
    public Long id_object_point;
    public Long object_control_id;
    public Long control_point_id;

    @Relation(parentColumn = "control_point_id", entityColumn = "id_control")
    public ControlPoint controlPoint;

    public Long position_x;
    public Long position_y;
}
