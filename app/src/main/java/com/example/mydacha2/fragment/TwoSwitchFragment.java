package com.example.mydacha2.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mydacha2.ActionsJason.MyTwoSwitch;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.R;
import com.example.mydacha2.supportclass.MyMQTTClient;
import com.example.mydacha2.supportclass.MyMqttConnectOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TwoSwitchFragment extends Fragment {
    private boolean on_off1;
    private boolean on_off2;
    private TextView textViewTamer1;
    private TextView textViewTamer2;
    private CountDownTimer countDownTimer = null;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageView imageViewLamp1;
    private ImageView imageViewLamp2;
    private final int my_on;
    private final int my_off;
    private String topic;
    private MyTwoSwitch myTwoSwitch = new MyTwoSwitch();
    ControlPoint controlPoint;
    private MyMQTTClient myMQTTClient;
    private MyMqttConnectOptions myMqttConnectOptions;

    public TwoSwitchFragment(ControlPoint controlPoint, int my_on, int my_of) {
        this.my_off = my_of;
        this.my_on = my_on;
        this.controlPoint = controlPoint;

     }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two_switch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        on_off1 = true;
        on_off2 = true;

        imageButton1 = view.findViewById(R.id.imageButtonSet1);
        imageButton2 = view.findViewById(R.id.imageButtonSet2);

        imageViewLamp1 = view.findViewById(R.id.imageViewLamp1);
        imageViewLamp2 = view.findViewById(R.id.imageViewLamp2);

        textViewTamer1 = view.findViewById(R.id.textViewTamer1);
        textViewTamer2 = view.findViewById(R.id.textViewTamer2);
        TextView textViewPoint = view.findViewById(R.id.textView_point);
        textViewPoint.setText(controlPoint.name);

        imageButton1.setOnClickListener(this::onClickButton1);
        imageButton2.setOnClickListener(this::onClickButton2);

        if(!controlPoint.executable_code.isEmpty()) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            myTwoSwitch = gson.fromJson(controlPoint.executable_code, MyTwoSwitch.class);
        }


        myMqttConnectOptions = new MyMqttConnectOptions();
        myMqttConnectOptions.setSubscriptionTopic(controlPoint.topic);
        Bundle bundle = this.getArguments();
        assert bundle != null;
        String serverURI = bundle.getString("serverURI", "");
        String basicTopic = bundle.getString("basicTopic", "");
        myMqttConnectOptions.setServerUri(serverURI);
        myMqttConnectOptions.setUsername(bundle.getString("userNodeServer", ""));
        myMqttConnectOptions.setPassword(bundle.getString("passwordMQTT", ""));

        topic = basicTopic + controlPoint.topic;
        myMqttConnectOptions.setSubscriptionTopic(topic);

        startMqtt();
    }

    private void startMqtt() {
        myMQTTClient = new MyMQTTClient(getActivity(), myMqttConnectOptions);
        myMQTTClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {}

            @Override
            public void connectionLost(Throwable throwable) {}

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                MyTwoSwitch myTwoSwitchTemp = gson.fromJson(mqttMessage.toString(), MyTwoSwitch.class);
                if (myTwoSwitchTemp.off != null ){
                    setImage1(myTwoSwitchTemp.off);
                } else if(myTwoSwitchTemp.on != null){
                    setImage1(myTwoSwitchTemp.on);
                }

                if (myTwoSwitchTemp.off_2 != null){
                    setImage2(myTwoSwitchTemp.off_2);
                } else if(myTwoSwitchTemp.on_2 != null){
                    setImage2(myTwoSwitchTemp.on_2);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
    }

    public void onClickButton1(View v) {
        if (v.getId() == R.id.imageButtonSet1) {
            if (on_off1) {
                String action;
                if (myTwoSwitch.on != null){
                    action = myTwoSwitch.on;
                } else {
                    action ="on";
                }
                setImage1(action);

                String messege = "{\"on\"=\"on\"}";
                myMQTTClient.published(messege, topic);
                if (null != myTwoSwitch.tameOff){
                    long set_timer = myTwoSwitch.tameOff * 64000;
                    long step_timer = getResources().getInteger(R.integer.step_timer);

                    countDownTimer = new CountDownTimer(set_timer, step_timer) {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            textViewTamer1.setText(Long.toString(millisUntilFinished / step_timer));
                        }

                        @Override
                        public void onFinish() {
                            String action;
                            if (myTwoSwitch.off != null){
                                action = myTwoSwitch.off;
                            } else {
                                action ="off";
                            }
                            setImage1(action);
                            String messege = "{\"off\"=\"off\"}";
                            myMQTTClient.published(messege, topic);
                        }
                    }.start();
                }

            } else {
                String action;
                if (myTwoSwitch.off != null){
                    action = myTwoSwitch.off;
                } else {
                    action ="off";
                }
                setImage1(action);

                String messege = "{\"off\"=\"off\"}";
                myMQTTClient.published(messege, topic);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        }
    }

    public void onClickButton2(View v) {
        if (v.getId() == R.id.imageButtonSet2) {
            if (on_off2) {
                String action;
                if (myTwoSwitch.on_2 != null){
                    action = myTwoSwitch.on_2;
                } else {
                    action ="on";
                }
                setImage2(action);

                String messege = "{\"on_2\"=\"on\"}";
                myMQTTClient.published(messege, topic);
                if (null != myTwoSwitch.tameOff_2){
                    long set_timer = myTwoSwitch.tameOff_2 * 64000;
                    long step_timer = getResources().getInteger(R.integer.step_timer);

                    countDownTimer = new CountDownTimer(set_timer, step_timer) {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            textViewTamer2.setText(Long.toString(millisUntilFinished / step_timer));
                        }

                        @Override
                        public void onFinish() {
                            String action;
                            if (myTwoSwitch.off_2 != null){
                                action = myTwoSwitch.off_2;
                            } else {
                                action ="off";
                            }
                            setImage1(action);
                            String messege = "{\"off_2\"=\"off\"}";
                            myMQTTClient.published(messege, topic);
                        }
                    }.start();
                }

            } else {
                String action;
                if (myTwoSwitch.off_2 != null){
                    action = myTwoSwitch.off_2;
                } else {
                    action ="off";
                }
                setImage2(action);
                String messege = "{\"off_2\"=\"off\"}";
                myMQTTClient.published(messege, topic);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        }
    }

    private void setImage1(String action){
        if(action.equals("on")){
            on_off1 = false;
            imageButton1.setImageResource(R.mipmap.icons8_on_94_foreground);
            imageViewLamp1.setImageResource(my_on);

        } else if (action.equals("off")){
            textViewTamer1.setText("");
            imageButton1.setImageResource(R.mipmap.icons8_off_94_foreground);
            imageViewLamp1.setImageResource(my_off);
            on_off1 = true;
        }
    }

    private void setImage2(String action){
        if(action.equals("on")){
            on_off2 = false;
            imageButton2.setImageResource(R.mipmap.icons8_on_94_foreground);
            imageViewLamp2.setImageResource(my_on);

        } else if (action.equals("off")){
            textViewTamer2.setText("");
            imageButton2.setImageResource(R.mipmap.icons8_off_94_foreground);
            imageViewLamp2.setImageResource(my_off);
            on_off2 = true;
        }
    }

}