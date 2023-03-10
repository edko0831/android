package com.example.mydacha2.Entity;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "object_control_with_control_point" //,
 //   foreignKeys = {@ForeignKey(entity = ObjectControl.class, parentColumns = "id", childColumns = "object_control_id"),
 //                  @ForeignKey(entity = ControlPoint.class, parentColumns = "id", childColumns = "control_point_id")}
)
public class ObjectControlWithControlPoint {
    @PrimaryKey(autoGenerate = true)
    public Long id_object_point;
    public Long object_control_id;
    public Long control_point_id;
    public Long position_x;
    public Long position_y;
}
