package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.DAO.ObjectControlWithControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.Entity.ObjectControl;
import com.example.mydacha2.Entity.ObjectControlControlPoint;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;

import java.util.List;

public class  ManagementObject extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {
    private List<ObjectControlControlPoint> objectControlControlPoint;
    ImageView picture;
    ObjectControl objectControl;
    ControlPoint selectControlPoint;
    Long getId;
    Long x;
    Long y;
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    assert intent != null;
                    long id = intent.getLongExtra("id", -1);
                    if (id != - 1 ){
                    }
                }
            });
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_object);
        picture = findViewById(R.id.panorama);

        Bundle arguments = getIntent().getExtras();
        getId = arguments.getLong("id");
        AppDatabase db = App.getInstance(this).getDatabase();
        ObjectControlsDAO objectControlsDAO = db.ObjectControlsDAO();
        objectControl = objectControlsDAO.selectId(Math.toIntExact(getId));
        ObjectControlWithControlPointDAO objectControlWithControlPointDAO = db.objectControlWithControlPointDAO();
        objectControlControlPoint = objectControlWithControlPointDAO.selectId(Math.toIntExact(getId));
        TextView textView = findViewById(R.id.textView);
        textView.setText(objectControl.name);
        String imgUrl = objectControl.picture_url;
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Bitmap originalBitmap = BitmapFactory.decodeFile(imgUrl);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 100;

            picture.setImageBitmap(originalBitmap);
        }
        picture.setOnClickListener(this);
        picture.setOnLongClickListener(this);
        picture.setOnTouchListener((v, event) -> {
            x = (long) event.getX();
            y = (long) event.getY();
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_object, menu);
        return true;
    }

    @RequiresApi(api = 33)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            // Toast.makeText(this, getString(R.string.action_settings), Toast.LENGTH_LONG).show();
        } else if (id == R.id.connect) {
            startActivity(new Intent(this, ConnectWiFi.class));
        } else if (id == R.id.home_page) {
            startActivity(new Intent(this, MainActivity.class));
            // Toast.makeText(this, getString(R.string.object_control), Toast.LENGTH_LONG).show();
        }  else if (id == R.id.action_close) {
            this.finishAffinity();
        }  else if (id == R.id.myObjet) {
            startActivity(new Intent(this, MyObject.class));
        }

        return super.onOptionsItemSelected(item);
    }
    private Integer getControlPointByPosition(){
        Integer returnValue = - 1;
        for (ObjectControlControlPoint cp: objectControlControlPoint){
            if(x == cp.position_x){
                if (cp.position_y == y){
                    if(cp.controlPoint.type_point.equals("")) {

                        Toast.makeText(this, "Clicked at x=" + x.toString() + ", y=" + y.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        return returnValue;
    }


    @Override
    public boolean onLongClick(View v) {
        if(getControlPointByPosition() > 0){
            Intent intent = new Intent(this, AddObjectControlWithControlPoint.class);
            intent.putExtra("id_control", selectControlPoint.id_control.longValue());
            intent.putExtra("id_object_point", objectControlControlPoint.get(0).control_point_id);
            intent.putExtra("id_object", getId);
            intent.putExtra("name", selectControlPoint.name);
            intent.putExtra("x", x);
            intent.putExtra("y", y);
            intent.putExtra("nameObject", objectControl.name);
            mStartForResult.launch(intent);

        } else {
            Intent intent = new Intent(this, AddObjectControlWithControlPoint.class);
            intent.putExtra("id_control", - 1L);
            intent.putExtra("id_object_point", - 1L);
            intent.putExtra("id_object", getId);
            intent.putExtra("name", "");
            intent.putExtra("x", x);
            intent.putExtra("y", y);
            intent.putExtra("nameObject", objectControl.name);
            mStartForResult.launch(intent);
            //Toast.makeText(this, "Clicked at x=" + x.toString() + ", y=" + y.toString(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(getControlPointByPosition() > 0){

        }
    }
}