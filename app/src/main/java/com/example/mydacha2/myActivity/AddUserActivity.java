package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.DAO.UsersDAO;
import com.example.mydacha2.DataClasses.MyRole;
import com.example.mydacha2.Entity.Users;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.google.android.material.textfield.TextInputLayout;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener{
    private CheckBox checkBox;
    private EditText editTextPassword;
    private EditText editUserName;
    private final List<String> spinnerList = new ArrayList<>();
    private MaterialBetterSpinner spinnerTypePoint;
    private TextInputLayout textName;
    private TextInputLayout textInputPassword;
    private TextView id;
    private UsersDAO usersDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView textViewObject = findViewById(R.id.textView_object);
        textViewObject.setText(getString(R.string.user_name));
        editUserName = findViewById(R.id.textEdit_name);
        id  = findViewById(R.id.textView_id);

        LinearLayout linearButton = findViewById(R.id.linearLayoutButton);
        @SuppressLint("InflateParams")
        View viewButton = getLayoutInflater().inflate(R.layout.buttonlayout, null);
        linearButton.addView(viewButton);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        editTextPassword = findViewById(R.id.editTextTextPassword);
        checkBox = findViewById(R.id.onpass);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                editTextPassword.setTransformationMethod(null);
                checkBox.setButtonDrawable(R.mipmap.icons8_show_48_1_foreground);
            }
            else {
                checkBox.setButtonDrawable(R.mipmap.icons8_not_show_48_1_foreground);
                editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
            editTextPassword.setSelection(editTextPassword.length());
        });
        for (MyRole myRole: MyRole.getMyRole()){
            spinnerList.add(myRole.getName());
        }

        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinnerTypePoint = findViewById(R.id.spinner_type_point);
        spinnerTypePoint.setAdapter(arrayAdapter);

        textName =  findViewById(R.id.text_name);
        textInputPassword =  findViewById(R.id.text_input_password);

        AppDatabase db = App.getInstance(this).getDatabase();
        usersDAO = db.usersDAO();

        Bundle arguments = getIntent().getExtras();
        if(arguments != null){
            setFields(arguments);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFields(Bundle arguments) {
        long getId = arguments.getLong("id");
        Users user = usersDAO.selectId((int) getId);
        id.setText(user.userId.toString());
        editUserName.setText(user.displayName);
        editTextPassword.setText(user.password);
        for (MyRole myRole: MyRole.getMyRole()){
            if(myRole.getRoleId() == user.roleId ) {
                spinnerTypePoint.setText(myRole.getName());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation_menu, menu);
        return true;
    }

    @RequiresApi(api = 33)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonSave) {
            if (setValues()) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (id == R.id.buttonCancel) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean setValues() {

        if(editUserName.getText().toString().isEmpty()){
            textName.setError(getResources().getString(R.string.not_name_user));
            return false;
        }

        if(editTextPassword.getText().toString().isEmpty()){
            textInputPassword.setError(getResources().getString(R.string.not_password));
            return false;
        }

        if(spinnerTypePoint.getText().toString().isEmpty()){
            sendMessege("роль");
            return false;
        }
        Users user = new Users();
        if (!id.getText().toString().isEmpty()) {
            user.userId = Long.parseLong(id.getText().toString());
        }

        user.displayName = editUserName.getText().toString();
        user.password = editTextPassword.getText().toString();
        for (MyRole myRole: MyRole.getMyRole()){
            if(myRole.getName().equals(spinnerTypePoint.getText().toString())) {
                user.roleId = myRole.getRoleId();
            }
        }

        if (user.userId == null) {
            usersDAO.insert(user);
            Intent intentResult = new Intent();
            setResult(RESULT_OK, intentResult);
        } else {
            usersDAO.update(user);
            Intent intentResult = new Intent();
            intentResult.putExtra("id", user.userId);
            setResult(RESULT_OK, intentResult);
        }

        return true;
    }

    private void sendMessege(String messege ) {
        Toast.makeText(AddUserActivity.this, " Не заполнено поле " +  messege, Toast.LENGTH_LONG).show();

    }
}