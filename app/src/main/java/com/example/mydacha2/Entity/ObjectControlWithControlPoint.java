package com.example.mydacha2.Entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "object_with_point" //,
 //   foreignKeys = {@ForeignKey(entity = ObjectControl.class, parentColumns = "id", childColumns = "object_control_id"),
 //                  @ForeignKey(entity = ControlPoint.class, parentColumns = "id", childColumns = "control_point_id")}
)
public class ObjectControlWithControlPoint {
    @PrimaryKey(autoGenerate = true)
    public Long id_object_point;
    public Long object_id;
    public Long point_id;
    public Long position_x;
    public Long position_y;
}
