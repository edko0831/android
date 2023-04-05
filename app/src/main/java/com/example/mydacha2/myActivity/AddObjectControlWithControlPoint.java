package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlWithControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.Entity.ObjectControlWithControlPoint;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddObjectControlWithControlPoint extends AppCompatActivity implements View.OnClickListener {

    Long id_control;
    Long id_object_point;
    Long id_object;
    String namePoint;
    Long x;
    Long y;

    TextView id;
    EditText name;
    EditText textEdit_x;
    EditText textEdit_y;

    ControlPointDAO controlPointDAO;
    ObjectControlWithControlPoint objectControlWithControlPoint = new ObjectControlWithControlPoint();
    ObjectControlWithControlPointDAO objectControlWithControlPointDAO;

    TextView textView;
    List<ControlPoint> spinnerList = new ArrayList<>();
    MaterialBetterSpinner spinnerTypePoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object_control_with_control_point);

        Bundle arguments = getIntent().getExtras();

        textView = findViewById(R.id.textView_object);
        textView.setText(R.string.add_point);
        id = findViewById(R.id.textView_id);
        name = findViewById(R.id.textEdit_name);

        LinearLayout linearButton = findViewById(R.id.linearLayoutButton);
        @SuppressLint("InflateParams")
        View viewButton = getLayoutInflater().inflate(R.layout.buttonlayout, null);
        linearButton.addView(viewButton);

        Button buttonSave = viewButton.findViewById(R.id.buttonSave);
        Button buttonCancel = viewButton.findViewById(R.id.buttonCancel);
        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        textEdit_x = findViewById(R.id.textEdit_x);
        textEdit_y = findViewById(R.id.textEdit_y);

        AppDatabase db = App.getInstance(this).getDatabase();
        controlPointDAO = db.controlPointDAO();
        spinnerList = controlPointDAO.select();

        objectControlWithControlPointDAO = db.objectControlWithControlPointDAO();

        ArrayAdapter<ControlPoint> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinnerTypePoint = findViewById(R.id.spinner_type_point);
        spinnerTypePoint.setAdapter(arrayAdapter);

        if(arguments != null){
            setFields(arguments);
        }

    }

     @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonSave) {
            if(setNewControlPoint()) {
                finish();
            }
        } else if (id == R.id.buttonCancel) {
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFields(Bundle arguments) {
        if (arguments.getLong("id_control") < 0){
            id_control = null;
        } else {
            id_control = arguments.getLong("id_control");
        }
        if(arguments.getLong("id_object_point") < 0){
            id_object_point = null;
        } else {
            id_object_point = arguments.getLong("id_object_point");
        }
        id_object = arguments.getLong("id_object");

        namePoint = arguments.getString("name");
        String nameObject = arguments.getString("nameObject");
        x = arguments.getLong("x");
        y = arguments.getLong("y");
        name.setText(nameObject);
        textEdit_x.setText(x.toString());
        textEdit_y.setText(y.toString());

        textEdit_y.setInputType(InputType.TYPE_NULL);
        textEdit_x.setInputType(InputType.TYPE_NULL);
        name.setInputType(InputType.TYPE_NULL);
        if(id_control != null){
            spinnerTypePoint.setText(namePoint);

        }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_object, menu);
        return true;
    }

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
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean setNewControlPoint() {

        if( spinnerTypePoint.getText().toString().isEmpty()){
            spinnerTypePoint.setError(getResources().getString(R.string.not_type_point));
            return false;
        }

        String cp  =  spinnerTypePoint.getText().toString();
        ControlPoint controlPoint = controlPointDAO.selectName(cp);

        objectControlWithControlPoint.id_object_point = id_object_point;
        objectControlWithControlPoint.object_id = id_object;
        objectControlWithControlPoint.point_id = controlPoint.id_control;
        objectControlWithControlPoint.position_x = x;
        objectControlWithControlPoint.position_y = y;

        if (objectControlWithControlPoint.id_object_point == null) {
            objectControlWithControlPointDAO.insert(objectControlWithControlPoint);
            Intent intentResult = new Intent();
            setResult(RESULT_OK, intentResult);
        } else {
            objectControlWithControlPointDAO.updateId(controlPoint.id_control,x, y, id_object_point);
        }

        return true;
    }

}