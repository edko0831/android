package com.example.mydacha2.supportclass;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;


public class MyMQTTClient {
    MqttAndroidClient mqttClient;
    Context context;
    public MyMQTTClient(Context context, String serverURI, String clientID) {
        this.context = context;
         mqttClient = new MqttAndroidClient(this.context, serverURI, clientID);
    }

    public MqttAndroidClient getMqttClient(){
        return mqttClient;
    }
   /*
    public connect(String  username, String password, IMqttActionListener cbConnect,
                   MqttCallback cbClient) {
        mqttClient.setCallback(cbClient);

        options = MqttConnectOptions();
        options.userName = username;
        options.password = password.toCharArray();

        try {
  //          mqttClient.connect(options, null, cbConnect)
        } catch (MqttException e) {
            e.printStackTrace();
        }

    } */
}
