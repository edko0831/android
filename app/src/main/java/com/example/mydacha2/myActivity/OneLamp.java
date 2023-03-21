package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.R;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;
import com.example.mydacha2.supportclass.MyMQTTClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class OneLamp extends AppCompatActivity {
    private TextView textViewTamer;
    private CountDownTimer countDownTimer = null;
    private ImageButton imageButton;
    private long set_timer;
    private long step_timer;
    private ImageView imageViewLamp;
    private boolean on_off;
    private MqttAndroidClient clientMqtt;
    private  String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_lamp);

        set_timer = getResources().getInteger(R.integer.set_timer);
        step_timer = getResources().getInteger(R.integer.step_timer);
      //  OkHttpClient client = new OkHttpClient();

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
        topic = controlPoint.topic;

        String clientId = MqttClient.generateClientId();

        String ipNodeServer = sharedPreferences.getString("ipNodeServer", "");
        String port = sharedPreferences.getString("port", "");
        String userName = sharedPreferences.getString("userNodeServer", "");
        String password = sharedPreferences.getString("passwordNodeServer", "");
        //String serverURI = "tcp://" + ipNodeServer + ":" + port;

       // client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.mqttdashboard.com:1883",clientId);
        //client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.43.41:1883",clientId);


        String serverURI = "tcp://broker.mqttdashboard.com:1883";

        clientMqtt = new MyMQTTClient(this, serverURI, clientId).getMqttClient();

        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(3000);
            connOpts.setKeepAliveInterval(10 * 60);
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());

        //  clientMqtt.setCallback((MqttCallback) OneLamp.this);
            clientMqtt.connect(connOpts, this, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    try {
                        clientMqtt.subscribe(topic,0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(OneLamp.this,"connected!!",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(OneLamp.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        clientMqtt.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
              // subText.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void onClickButton(View v) {
        if (v.getId() == R.id.imageButtonSet) {
            if (on_off) {
                on_off = false;
                imageButton.setImageResource(R.mipmap.icons8_on_94_foreground);
                published("on");
                //post(sharedPreferences.getString("ipNodeServer", ""), "lll");
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
                        published("off");
                    }
                }.start();

                imageViewLamp.setImageResource(R.drawable.lamp_on);
            } else {
                on_off = true;
                textViewTamer.setText("");
                imageButton.setImageResource(R.mipmap.icons8_off_94_foreground);
                imageViewLamp.setImageResource(R.drawable.lamp_off);
                countDownTimer.cancel();
                published("off");
                //post(sharedPreferences.getString("ipNodeServer", ""), "lll");

            }
        }
    }

    private void post(String url, String post){
/*
        Runnable runnable = () -> {
            request = new Request.Builder()
                    .url(String.format("http://%s/%s", url, post))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()) {
                    String resultText = response.body().string();
                }
            } catch (IOException ignored){

            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

 */

     }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void published(String message){
        try {
            clientMqtt.publish(topic, message.getBytes(),0,false);
            Toast.makeText(this,"Published Message",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription(){
        try{
            clientMqtt.subscribe(topic, 0);

        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v){
        try {
            IMqttToken token = clientMqtt.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(OneLamp.this,"connected!!",Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(OneLamp.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconnect(){

        try {
            IMqttToken token = clientMqtt.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(OneLamp.this,"Disconnected!!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(OneLamp.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
