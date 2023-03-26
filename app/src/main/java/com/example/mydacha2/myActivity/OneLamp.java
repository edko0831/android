package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.ActionsJason.MySwitch;
import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.example.mydacha2.supportclass.MyMQTTClient;
import com.example.mydacha2.supportclass.MyMqttConnectOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;

public class OneLamp extends AppCompatActivity {
    private TextView textViewTamer;
    private CountDownTimer countDownTimer = null;
    private ImageButton imageButton;
    private long set_timer;
    private long step_timer;
    private ImageView imageViewLamp;
    private boolean on_off;
    private String topic;
    private MyMQTTClient myMQTTClient;
    private MyMqttConnectOptions myMqttConnectOptions;
    private MySwitch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_lamp);

        set_timer = getResources().getInteger(R.integer.set_timer);
        step_timer = getResources().getInteger(R.integer.step_timer);

        on_off = true;
        imageButton = findViewById(R.id.imageButtonSet);
        imageViewLamp = findViewById(R.id.imageViewLamp);
        textViewTamer = findViewById(R.id.textViewTamer);
        TextView textViewPoint = findViewById(R.id.textView_point);
        SharedPreferences sharedPreferences = getSharedPreferences("myDacha", MODE_PRIVATE);

        imageButton.setOnClickListener(this::onClickButton);

        Bundle arguments = getIntent().getExtras();
        long getId = arguments.getLong("id");
        AppDatabase db = App.getInstance(this).getDatabase();
        ControlPointDAO controlPointDAO = db.controlPointDAO();
        ControlPoint controlPoint = controlPointDAO.selectId((int) getId);
        textViewPoint.setText(controlPoint.name);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mySwitch = new MySwitch();
     //   controlPoint.executable_code = "{'on':'Мурзик', 'off':'Мурзик2', 'tameOn':-16777216,'tameOff':8}";

        if(!controlPoint.executable_code.isEmpty()) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            mySwitch = gson.fromJson(controlPoint.executable_code, MySwitch.class);
        }

        topic = controlPoint.topic;
        myMqttConnectOptions = new MyMqttConnectOptions();
        myMqttConnectOptions.setSubscriptionTopic(controlPoint.topic);
        String serverURI = "tcp://" + sharedPreferences.getString("ipNodeServer", "") + ":" + sharedPreferences.getString("port", "");
        myMqttConnectOptions.setServerUri(serverURI);
        myMqttConnectOptions.setUsername(sharedPreferences.getString("userNodeServer", ""));
        myMqttConnectOptions.setPassword(sharedPreferences.getString("passwordNodeServer", ""));
        myMqttConnectOptions.setSubscriptionTopic(topic);

        startMqtt();
    }

    private void startMqtt(){
        myMQTTClient = new MyMQTTClient(getApplicationContext(), myMqttConnectOptions);
        myMQTTClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {}

            @Override
            public void connectionLost(Throwable throwable) {}

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
              Toast.makeText(OneLamp.this,mqttMessage.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
    }

     public void onClickButton(View v) {
        if (v.getId() == R.id.imageButtonSet) {
            if (on_off) {
                on_off = false;
                imageButton.setImageResource(R.mipmap.icons8_on_94_foreground);
                myMQTTClient.published("on", topic);
                countDownTimer = new CountDownTimer(set_timer, step_timer) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        textViewTamer.setText(Long.toString(millisUntilFinished / step_timer));
                    }

                    @Override
                    public void onFinish() {
                        imageButton.setImageResource(R.mipmap.icons8_off_94_foreground);
                        imageViewLamp.setImageResource(R.drawable.lamp_off);
                        on_off = true;
                        myMQTTClient.published("off", topic);
                    }
                }.start();

                imageViewLamp.setImageResource(R.drawable.lamp_on);
            } else {
                on_off = true;
                textViewTamer.setText("");
                imageButton.setImageResource(R.mipmap.icons8_off_94_foreground);
                imageViewLamp.setImageResource(R.drawable.lamp_off);
                countDownTimer.cancel();
                myMQTTClient.published("off", topic);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myMQTTClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
             return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return  true;
    }

}
