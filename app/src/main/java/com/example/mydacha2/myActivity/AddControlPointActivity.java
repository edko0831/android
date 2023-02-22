package com.example.mydacha2.myActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.ControlPointRepository;
import com.example.mydacha2.supportclass.MyListTypePoint;
import com.google.android.material.textfield.TextInputLayout;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddControlPointActivity extends AppCompatActivity implements View.OnClickListener {
    TextView id;
    EditText name;
    EditText description;
    EditText picture;
    EditText execute_code;
    private ControlPoint controlPoint = new ControlPoint();
    ControlPointDAO controlPointDAO;
    TextView textView;
    private static final String LOG_TAG = "AddControlPointActivity";
    private static final int MY_REQUEST_CODE_PERMISSION = 10;
    TextInputLayout textName;
    List<String> spinnerList = new ArrayList<>();
    MaterialBetterSpinner spinnerTypePoint;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null) {
                    Intent intent  = result.getData();
                    assert intent != null;
                    Uri fileUri = intent.getData();
                    Log.i(LOG_TAG, "Uri: " + fileUri);

                    String filePath = null;
                    try {
                        filePath = AddObjectActivity.FileUtils.getPath(this, fileUri);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Error: " + e);
                        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                    if (filePath != null) {
                        this.picture.setText(filePath);
                    } else {
                        this.picture.setText(fileUri.getPath());
                    }
                }
            });

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textName.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_point);

        ImageButton button = findViewById(R.id.imageButton_open);
        button.setBackgroundResource(R.drawable.icons8_three_dots_94_background);
        button.setImageResource(R.mipmap.icons8_three_dots_94_foreground);
        button.setOnClickListener(v -> askPermissionAndBrowseFile());

        id = findViewById(R.id.textView_id);
        name = findViewById(R.id.textEdit_name);
        description = findViewById(R.id.textEdit_description);
        picture = findViewById(R.id.textEdit_picture);
        textView = findViewById(R.id.textView_object);
        textView.setText(R.string.object_control_add);
        textName =  findViewById(R.id.text_name);
        execute_code = findViewById(R.id.text_executable_code);
        name.addTextChangedListener(textWatcher);

        LinearLayout linearButton = findViewById(R.id.linearLayoutButton);
        View viewButton = getLayoutInflater().inflate(R.layout.buttonlayout, null);
        linearButton.addView(viewButton);

        Button buttonSave = viewButton.findViewById(R.id.buttonSave);
        Button buttonCancel = viewButton.findViewById(R.id.buttonCancel);
        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        ControlPointRepository controlPointRepository = new ControlPointRepository(this);
        controlPointDAO = controlPointRepository.getControlPoint().controlPointDAO();

       Bundle arguments = getIntent().getExtras();

        if(arguments != null){
            setFields(arguments);
        }

        for (MyListTypePoint myListTypePoint: MyListTypePoint.getListTypePoint()){
            spinnerList.add( myListTypePoint.getPoint());
        }
        
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinnerTypePoint = findViewById(R.id.spinner_type_point);
        spinnerTypePoint.setAdapter(arrayAdapter);
    }

    private void askPermissionAndBrowseFile()  {

        int permisson = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permisson != PackageManager.PERMISSION_GRANTED) {
            // If don't have permission so prompt the user.
            this.requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE_PERMISSION
            );
        }
        doBrowseFile();
    }
    private void doBrowseFile() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("image/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        mStartForResult.launch(chooseFileIntent);
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
        long getId = arguments.getLong("id");
        controlPoint = controlPointDAO.selectId((int) getId);
        textView.setText(R.string.object_control_update);
        id.setText(controlPoint.id.toString());
        name.setText(controlPoint.name);
        description.setText(controlPoint.description);
        picture.setText(controlPoint.picture_url);
        spinnerTypePoint.setText(controlPoint.type_point);
        execute_code.setText(controlPoint.executable_code);
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

        if(name.getText().toString().isEmpty()){
            textName.setError(getResources().getString(R.string.not_name));
            return false;
        } else {
            textName.setErrorEnabled(false);
        }

        if( spinnerTypePoint.getText().toString().isEmpty()){
            spinnerTypePoint.setError(getResources().getString(R.string.not_name));
            return false;
        }

        if (!id.getText().toString().isEmpty()) {
            controlPoint.id = Long.parseLong(id.getText().toString());
        }
        controlPoint.name = name.getText().toString();
        controlPoint.description = description.getText().toString();
        controlPoint.picture_url = picture.getText().toString();
        controlPoint.type_point = spinnerTypePoint.getText().toString();
        controlPoint.executable_code = execute_code.getText().toString();

        if (controlPoint.id == null) {
            controlPointDAO.insert(controlPoint);
            Intent intentResult = new Intent();
            setResult(RESULT_OK, intentResult);
        } else {
            controlPointDAO.update(controlPoint);
            Intent intentResult = new Intent();
            intentResult.putExtra("id", controlPoint.id);
            setResult(RESULT_OK, intentResult);
        }

        return true;
    }

}