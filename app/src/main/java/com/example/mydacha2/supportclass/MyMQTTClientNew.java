package com.example.mydacha2.supportclass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

public final class MyMQTTClientNew {
    @SuppressLint("StaticFieldLeak")
    private static MyMQTTClientNew instanceMQTT;
    public MqttAndroidClient mqttAndroidClient;
    private final MqttConnectOptions mqttConnectOptions;
    private final Context context;
    private MyMQTTClientNew(Context context, MyMqttConnectOptions myMqttConnectOptions) {
        this.context = context;
        String clientId = MqttClient.generateClientId();

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        String [] serverUri = new String[1];
        serverUri[0] = myMqttConnectOptions.getServerUri();
        mqttConnectOptions.setServerURIs(serverUri);
        mqttConnectOptions.setUserName(myMqttConnectOptions.getUsername());
        myMqttConnectOptions.setClientId(clientId);
        mqttConnectOptions.setPassword(myMqttConnectOptions.getPassword().toCharArray());


        mqttAndroidClient = new MqttAndroidClient(context, myMqttConnectOptions.getServerUri(), clientId, Ack.AUTO_ACK);
        IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Toast.makeText(context,"connected MQTT!!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Toast.makeText(context,"connection MQTT failed!!",Toast.LENGTH_LONG).show();
            }
        });

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
    }

    public static MyMQTTClientNew getInstance(Context context, MyMqttConnectOptions myMqttConnectOptions) {
        if (instanceMQTT == null) instanceMQTT = new MyMQTTClientNew(context, myMqttConnectOptions);
        return instanceMQTT;
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect(String subscriptionTopic){
        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                subscribeToTopic(subscriptionTopic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w("Mqtt", "Failed to connect to: " +
                        Arrays.toString(mqttConnectOptions.getServerURIs()) + exception.toString());
            }
        });
    }

    public void subscribeToTopic(String subscriptionTopic) {
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
        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        mqttAndroidClient.publish(topic, message.getBytes(), 0, false);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Failed to connect to: " +
                                Arrays.toString(mqttConnectOptions.getServerURIs()) + exception.toString());
                    }
                });
     //    Toast.makeText(context,"Published Message",Toast.LENGTH_SHORT).show();
    }

    public void disconnect(){
        IMqttToken token = mqttAndroidClient.disconnect();
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Toast.makeText(context,"Disconnected MQTT!!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Toast.makeText(context,"Could not diconnect!!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
