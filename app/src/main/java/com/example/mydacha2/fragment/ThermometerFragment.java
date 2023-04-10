package com.example.mydacha2.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mydacha2.ActionsJason.MyValueControl;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.R;
import com.example.mydacha2.supportclass.MyMQTTClient;
import com.example.mydacha2.supportclass.MyMqttConnectOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.InputStream;

public class ThermometerFragment extends Fragment {
    private TextView textViewTemperature;
    private ImageView imageTemperatura;

    ControlPoint controlPoint;
    private MyMqttConnectOptions myMqttConnectOptions;

    public ThermometerFragment(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thermometr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textViewPoint = view.findViewById(R.id.textView_point);
        textViewPoint.setText(controlPoint.name);

        textViewTemperature= view.findViewById(R.id.textViewTemperature);
        imageTemperatura = view.findViewById(R.id.imageTemperatura);

        myMqttConnectOptions = new MyMqttConnectOptions();
        Bundle bundle = this.getArguments();
        assert bundle != null;
        String serverURI = bundle.getString("serverURI", "");
        String basicTopic = bundle.getString("basicTopic", "");
        myMqttConnectOptions.setServerUri(serverURI);
        myMqttConnectOptions.setUsername(bundle.getString("userNodeServer", ""));
        myMqttConnectOptions.setPassword(bundle.getString("passwordMQTT", ""));

        String topic = basicTopic + controlPoint.topic;
        myMqttConnectOptions.setSubscriptionTopic(topic);
        try {
            if (controlPoint.type_point.equals(getResources().getString(R.string.thermometer))) {
                    setImage(20F);
            } else if (controlPoint.type_point.equals(getResources().getString(R.string.barometer))) {
                    setImageBarometer(1000F);
            }
        } catch (IOException e) {
        e.printStackTrace();
    }

        startMqtt();
    }

     private void startMqtt() {
        MyMQTTClient myMQTTClient = new MyMQTTClient(getActivity(), myMqttConnectOptions);
        myMQTTClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {}

            @Override
            public void connectionLost(Throwable throwable) {}

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws IOException {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                MyValueControl myValueControl = gson.fromJson(mqttMessage.toString(), MyValueControl.class);
                if (controlPoint.type_point.equals(getResources().getString(R.string.thermometer))) {
                    setImage(myValueControl.value);
                } else if (controlPoint.type_point.equals(getResources().getString(R.string.barometer))) {
                    setImageBarometer(myValueControl.value);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });
    }

     @SuppressLint("SetTextI18n")
     private void setImage(Float value) throws IOException {
         String filename = "temperatura_cold.png";
         String degree = " \u00B0C";
        if(value <= - 10){
            textViewTemperature.setText(value + degree);
            textViewTemperature.setTextColor(Color.parseColor("#FF3700B3"));
            filename = "temperatura_cold.png";
        } else if (value > - 10 && value <= 10){
            textViewTemperature.setTextColor(Color.parseColor("#FF0072FF"));
            textViewTemperature.setText(value + degree);
            filename = "temperatura_chilly.jpg";
        } else if (value > 10 && value <= 27){
            textViewTemperature.setText(value + degree);
            textViewTemperature.setTextColor(Color.parseColor("#FF40F100"));
            filename = "temperatura_warmly.jpg";
        } else if (value > 27){
            textViewTemperature.setText(value + degree);
            textViewTemperature.setTextColor(Color.parseColor("#FFFF0000"));
            filename = "temperatura_hot.jpg";
        }

         try(InputStream inputStream = getActivity()
                                         .getApplicationContext()
                                         .getAssets()
                                         .open(filename)) {

             Drawable drawable = Drawable.createFromStream(inputStream, null);
             imageTemperatura.setImageDrawable(drawable);
             imageTemperatura.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
         }
         catch (IOException e){
             e.printStackTrace();
         }

     }

    private void setImageBarometer(Float value) throws IOException {
        String filename = "temperatura_cold.png";
        String degree = " hPa";

        textViewTemperature.setText(value + degree);
        textViewTemperature.setTextColor(Color.parseColor("#FF000000"));
        filename = "barometer.jpg";

        try(InputStream inputStream = getActivity()
                .getApplicationContext()
                .getAssets()
                .open(filename)) {

            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageTemperatura.setImageDrawable(drawable);
            imageTemperatura.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}