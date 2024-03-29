package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private CheckBox checkBox;
    private CheckBox checkBoxMQTT;
    private CheckBox checkCity;
    private EditText editTextPassword;
    private EditText editTextPasswordMQTT;
    private EditText editTexIP;
    private EditText editPort;
    private EditText editTextWifiNet;
    private EditText editUserName;
    private EditText editCity;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);

        LinearLayout linearButton = findViewById(R.id.linearLayoutButton);
        @SuppressLint("InflateParams") View viewButton = getLayoutInflater().inflate(R.layout.buttonlayout, null);
        linearButton.addView(viewButton);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextPasswordMQTT = findViewById(R.id.editTextPasswordMQTT);
        editTexIP = findViewById(R.id.editTexIP);
        editTextWifiNet = findViewById(R.id.editText_wifi_net);
        editUserName = findViewById(R.id.editUserName);
        editPort = findViewById(R.id.editPort);
        editCity = findViewById(R.id.editTextCity);

        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        checkBox = findViewById(R.id.onpass);
        checkBoxMQTT = findViewById(R.id.checkBoxMQTT);
        checkCity = findViewById(R.id.checkCity);

        TextView textView = findViewById(R.id.textView_object);
        textView.setText(R.string.setting_page);

        sharedPreferences =  getSharedPreferences("myDacha", MODE_PRIVATE);
        getValues();

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
        checkBoxMQTT.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                editTextPasswordMQTT.setTransformationMethod(null);
                checkBoxMQTT.setButtonDrawable(R.mipmap.icons8_show_48_1_foreground);
            }
            else {
                checkBoxMQTT.setButtonDrawable(R.mipmap.icons8_not_show_48_1_foreground);
                editTextPasswordMQTT.setTransformationMethod(new PasswordTransformationMethod());
            }
            editTextPasswordMQTT.setSelection(editTextPasswordMQTT.length());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_setting, menu);
        return true;
    }

    @RequiresApi(api = 33)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.myObjet) {
            startActivity(new Intent(this, MyObject.class));
        } else if (id == R.id.connect) {
            startActivity(new Intent(this, ConnectWiFi.class));
        } else if (id == R.id.home_page) {
            startActivity(new Intent(this, MainActivity.class));
        }else if (id == R.id.action_close) {
            this.finishAffinity();
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
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!editTextWifiNet.getText().toString().isEmpty()) {
            editor.putString("nameNodeServer", editTextWifiNet.getText().toString());
        } else {
            sendMessege("Имя сети");
            return false;
        }

        if(editTexIP.getText().toString().isEmpty()){
            sendMessege("IP адрес");
            return false;
        }
        else {
            editor.putString("ipNodeServer", editTexIP.getText().toString());
        }

        if(editUserName.getText().toString().isEmpty()){
            sendMessege("Имя пользователя");
            return false;
        }
        else {
            editor.putString("userNodeServer", editUserName.getText().toString());
        }

        if(editTextPassword.getText().toString().isEmpty()){
            sendMessege("Пароль");
            return false;
        }
        else {
            editor.putString("passwordNodeServer", editTextPassword.getText().toString());
        }

        if(editTextPasswordMQTT.getText().toString().isEmpty()){
            sendMessege("Пароль MQTT");
            return false;
        }
        else {
            editor.putString("passwordMQTT", editTextPasswordMQTT.getText().toString());
        }

        editor.putString("port", editPort.getText().toString());
        editor.putString("city", editCity.getText().toString());
        editor.putBoolean("checkCity", checkCity.isChecked());
        editor.apply();
        return true;
    }

    private void sendMessege(String messege ) {
        Toast.makeText(SettingActivity.this, " Не заполнено поле " +  messege, Toast.LENGTH_LONG).show();

    }

    private void getValues(){
        String nameNodeServer = sharedPreferences.getString("nameNodeServer", "");
        String ipNodeServer = sharedPreferences.getString("ipNodeServer", "");
        String userNodeServer = sharedPreferences.getString("userNodeServer", "");
        String passwordNodeServer = sharedPreferences.getString("passwordNodeServer", "");
        String passwordMQTT = sharedPreferences.getString("passwordMQTT", "");
        String port = sharedPreferences.getString("port", "");
        String city = sharedPreferences.getString("city", "");
        boolean mCheckCity = sharedPreferences.getBoolean("checkCity", false);

        editTextWifiNet.setText(nameNodeServer);
        editTexIP.setText(ipNodeServer);
        editPort.setText(port);
        editUserName.setText(userNodeServer);
        editTextPassword.setText(passwordNodeServer);
        editTextPasswordMQTT.setText(passwordMQTT);
        editCity.setText(city);
        checkCity.setChecked(mCheckCity);
    }
}