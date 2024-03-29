package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.ActionsJason.MySwitch;
import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.example.mydacha2.supportclass.MyMQTTClientNew;
import com.example.mydacha2.supportclass.MyMqttConnectOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;

public class OneSwitch extends AppCompatActivity {
    private TextView textViewTamer;
    private CountDownTimer countDownTimer = null;
    private ImageButton imageButton;
    private ImageView imageViewLamp;
    private boolean on_off;
    private String myTopic;
    private MySwitch mySwitch;
    private int my_on;
    private int my_off;
    MyMqttConnectOptions myMqttConnectOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_lamp);

        on_off = true;
        imageButton = findViewById(R.id.imageButtonSet);
        imageViewLamp = findViewById(R.id.imageViewLamp);

        textViewTamer = findViewById(R.id.textViewTamer);
        TextView textViewPoint = findViewById(R.id.textView_point);

        imageButton.setOnClickListener(this::onClickButton);

        Bundle arguments = getIntent().getExtras();
        long getId = arguments.getLong("id");
        my_on  = arguments.getInt("on");
        my_off  = arguments.getInt("off");
        String basicTopic = arguments.getString("basicTopic", "");

        AppDatabase db = App.getInstance(this).getDatabase();
        ControlPointDAO controlPointDAO = db.controlPointDAO();
        ControlPoint controlPoint = controlPointDAO.selectId((int) getId);
        textViewPoint.setText(controlPoint.name);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mySwitch = new MySwitch();
        imageViewLamp.setImageResource(my_off);

        if(!controlPoint.executable_code.isEmpty()) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            mySwitch = gson.fromJson(controlPoint.executable_code, MySwitch.class);
        }
        myMqttConnectOptions = MyMqttConnectOptions.getMqttConnectOptions("","","");
        myTopic = basicTopic + controlPoint.topic;
        MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                .setCallback(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean b, String s) {}

                    @Override
                    public void connectionLost(Throwable throwable) {}

                    @Override
                    public void messageArrived(String topic, MqttMessage mqttMessage) {
                        if(myTopic.equals(topic)) {
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                            MySwitch mySwitchTemp = gson.fromJson(mqttMessage.toString(), MySwitch.class);
                            setImageMQTT(mySwitchTemp);
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
                });

        MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                .published("{\"value\":\"get value\"}", myTopic);
        MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                .subscribeToTopic(myTopic);

    }

    public void onClickButton(View v) {
        if (v.getId() == R.id.imageButtonSet) {
            if (on_off) {
                String action;
                if (mySwitch.action != null){
                    action = mySwitch.action;
                } else {
                    action ="on";
                }
                setImage(action);
                String messege = "{\"action\"=\"on\"}";
                MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                        .published(messege, myTopic);
                if (null != mySwitch.tameOff){
                    long set_timer = mySwitch.tameOff * 64000;
                    long step_timer = getResources().getInteger(R.integer.step_timer);

                    countDownTimer = new CountDownTimer(set_timer, step_timer) {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            textViewTamer.setText(Long.toString(millisUntilFinished / step_timer));
                        }

                        @Override
                        public void onFinish() {
                            String action;
                            if (mySwitch.action != null){
                                action = mySwitch.action;
                            } else {
                                action ="off";
                            }
                            setImage(action);
                            String messege = "{\"action\"=\"off\"}";
                            MyMQTTClientNew.getInstance(OneSwitch.this, myMqttConnectOptions)
                                    .published(messege, myTopic);
                       }
                    }.start();
                }

            } else {
                String action;
                if (mySwitch.action != null){
                    action = mySwitch.action;
                } else {
                    action ="off";
                }
                setImage(action);
                String messege = "{\"action\"=\"off\"}";
                MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                        .published(messege, myTopic);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        }
    }

    private void setImageMQTT(MySwitch mySwitchTemp){

        if(mySwitchTemp.action != null ){
            if (mySwitchTemp.action.equals("on")){
                on_off = false;
                imageButton.setImageResource(R.mipmap.icons8_on_94_foreground);
                imageViewLamp.setImageResource(my_on);
            } else{
                textViewTamer.setText("");
                imageButton.setImageResource(R.mipmap.icons8_off_94_foreground);
                imageViewLamp.setImageResource(my_off);
                on_off = true;
            }
        }
    }

    private void setImage(String message){

        if(message.equals("on")){
            on_off = false;
            imageButton.setImageResource(R.mipmap.icons8_on_94_foreground);
            imageViewLamp.setImageResource(my_on);

        } else if (message.equals("off")){
            textViewTamer.setText("");
            imageButton.setImageResource(R.mipmap.icons8_off_94_foreground);
            imageViewLamp.setImageResource(my_off);
            on_off = true;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
