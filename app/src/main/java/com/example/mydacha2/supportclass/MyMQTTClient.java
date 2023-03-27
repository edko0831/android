package com.example.mydacha2.supportclass;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class MyMQTTClient {
    public MqttAndroidClient mqttAndroidClient;
    private final MqttConnectOptions mqttConnectOptions;
    private final String subscriptionTopic;

    public MyMQTTClient(Context context, MyMqttConnectOptions myMqttConnectOptions){
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(myMqttConnectOptions.getUsername());
        mqttConnectOptions.setPassword(myMqttConnectOptions.getPassword().toCharArray());
        subscriptionTopic = myMqttConnectOptions.getSubscriptionTopic();

        String clientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(context, myMqttConnectOptions.getServerUri(), clientId, Ack.AUTO_ACK);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {}

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
      mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                subscribeToTopic();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w("Mqtt", "Failed to connect to: " +
                        Arrays.toString(mqttConnectOptions.getServerURIs()) + exception.toString());
            }
        });
    }

    private void subscribeToTopic() {
        mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.w("Mqtt","Subscribed!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w("Mqtt", "Subscribed fail!");
            }
        });
    }

    public void published(String message, String topic){
            mqttAndroidClient.publish(topic, message.getBytes(),0,false);
          //  Toast.makeText(context,"Published Message",Toast.LENGTH_SHORT).show();
    }

    public void disconnect(){
            IMqttToken token = mqttAndroidClient.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                //    Toast.makeText(context,"Disconnected!!",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                  //  Toast.makeText(context,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
    }
}