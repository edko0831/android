package com.example.mydacha2.myActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydacha2.ActionsJason.MyValueControl;
import com.example.mydacha2.DAO.ObjectControlWithControlPointDAO;
import com.example.mydacha2.DAO.ObjectControlsDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.Entity.ObjectControl;
import com.example.mydacha2.Entity.ObjectControlControlPoint;
import com.example.mydacha2.MainActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class  ManagementObject extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {
    private List<ObjectControlControlPoint> objectControlControlPoint;
    ImageView picture;
    HorizontalScrollView scrollView;
    ObjectControl objectControl;
    ObjectControlWithControlPointDAO objectControlWithControlPointDAO;
    RelativeLayout relativeLayoutPanorama;
    Long getId;
    Long x;
    Long y;
    List<MySubscribes> mySubscribesList = new ArrayList<>();
    MyMqttConnectOptions myMqttConnectOptions;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    assert intent != null;
                    setControlPoint();
                }
            });
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_object);
        picture = findViewById(R.id.panorama);
        scrollView = findViewById(R.id.scrollView);
        relativeLayoutPanorama = findViewById(R.id.relativeLayoutPanorama);

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Bundle arguments = getIntent().getExtras();
        getId = arguments.getLong("id");
        AppDatabase db = App.getInstance(this).getDatabase();
        ObjectControlsDAO objectControlsDAO = db.ObjectControlsDAO();
        objectControl = objectControlsDAO.selectId(Math.toIntExact(getId));
        ObjectControlWithControlPointDAO objectControlWithControlPointDAO = db.objectControlWithControlPointDAO();
        objectControlControlPoint = objectControlWithControlPointDAO.selectId(Math.toIntExact(getId));
        TextView textView = findViewById(R.id.textView);
        textView.setText(objectControl.name);

        picture.setOnClickListener(this);
        picture.setOnLongClickListener(this);
        picture.setOnTouchListener((v, event) -> {
            x = (long) event.getX();
            y = (long) event.getY();
            return false;
        });
        setControlPoint();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedPreferences =  getSharedPreferences("myDacha", MODE_PRIVATE);
        String server = sharedPreferences.getString("ipNodeServer", "");
        String port = sharedPreferences.getString("port", "");
        String serverURI = "tcp://" + server + ":" + port;
        myMqttConnectOptions =  MyMqttConnectOptions.getMqttConnectOptions(serverURI,
                sharedPreferences.getString("userNodeServer", ""),
                sharedPreferences.getString("passwordMQTT", ""));

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                .setCallback(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean b, String s) {}

                    @Override
                    public void connectionLost(Throwable throwable) {}

                    @Override
                    public void messageArrived(String topic, MqttMessage mqttMessage) {
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        MyValueControl myValueControl = gson.fromJson(mqttMessage.toString(), MyValueControl.class);
                        for (MySubscribes mySubscribes: mySubscribesList) {
                            if (mySubscribes.controlPoint.type_point.equals(getResources().getString(R.string.thermometer)) && mySubscribes.topic.equals(topic)) {
                                Float value = myValueControl.value;
                                String temper = myValueControl.value + " \u00B0C";
                                mySubscribes.setValue(value);
                                if(value <= - 10){
                                    mySubscribes.textView.setText(temper);
                                    mySubscribes.textView.setTextColor(Color.parseColor("#FF3700B3"));
                                } else if (value > - 10 && value <= 10){
                                    mySubscribes.textView.setTextColor(Color.parseColor("#FF0072FF"));
                                    mySubscribes.textView.setText(temper);
                                } else if (value > 10 && value <= 27){
                                    mySubscribes.textView.setText(temper);
                                    mySubscribes.textView.setTextColor(Color.parseColor("#FF40F100"));
                                } else if (value > 27){
                                    mySubscribes.textView.setText(temper);
                                    mySubscribes.textView.setTextColor(Color.parseColor("#FFFF0000"));
                                }
                            } else if (mySubscribes.controlPoint.type_point.equals(getResources().getString(R.string.barometer)) && mySubscribes.topic.equals(topic)) {
                                String myValue = myValueControl.value + " hPa";
                                mySubscribes.textView.setText(myValue);
                                mySubscribes.textView.setTextColor(Color.parseColor("#FF000000"));
                                mySubscribes.setValue(myValueControl.value);
                            } else if (mySubscribes.controlPoint.type_point.equals(getResources().getString(R.string.gas_sensor)) && mySubscribes.topic.equals(topic)) {
                                String myValue = myValueControl.value + " dB";
                                mySubscribes.textView.setText(myValue);
                                mySubscribes.textView.setTextColor(Color.parseColor("#FF000000"));
                                mySubscribes.setValue(myValueControl.value);
                            }
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
                });

        for (MySubscribes mySubscribes: mySubscribesList) {
            MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                    .published("{\"value\":\"get value\"}", mySubscribes.topic);
            MyMQTTClientNew.getInstance(this, myMqttConnectOptions)
                    .subscribeToTopic(mySubscribes.topic);
        }
    }

    private void setControlPoint(){
        AppDatabase db = App.getInstance(this).getDatabase();
        objectControlWithControlPointDAO = db.objectControlWithControlPointDAO();
        objectControlControlPoint = objectControlWithControlPointDAO.selectId(Math.toIntExact(getId));
        setButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_object, menu);
        return true;
    }

    @RequiresApi(api = 33)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            // Toast.makeText(this, getString(R.string.action_settings), Toast.LENGTH_LONG).show();
        } else if (id == R.id.connect) {
            startActivity(new Intent(this, ConnectWiFi.class));
        } else if (id == R.id.home_page) {
            startActivity(new Intent(this, MainActivity.class));
            // Toast.makeText(this, getString(R.string.object_control), Toast.LENGTH_LONG).show();
        }  else if (id == R.id.action_close) {
            this.finishAffinity();
        }  else if (id == R.id.myObjet) {
            startActivity(new Intent(this, MyObject.class));
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private Integer getControlPointByPosition(){
        int returnValue = - 1;
        for (ObjectControlControlPoint cp: objectControlControlPoint){
            int delta = getResources().getInteger(R.integer.deltaPosition);
            if(cp.position_x >= x - delta && cp.position_x <= x + delta){
                if (cp.position_y >= y - delta && cp.position_y <= y + delta){
                    returnValue = objectControlControlPoint.indexOf(cp);
                      //  Toast.makeText(this, "Clicked at x=" + x + ", y=" + y, Toast.LENGTH_LONG).show();
                }
            }
        }
        return returnValue;
    }

    @Override
    public boolean onLongClick(View v) {
        Integer poz = getControlPointByPosition();
        if(poz >= 0){
            ObjectControlControlPoint occp = objectControlControlPoint.get(poz);
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.ask_a_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.update, (dialogInterface, i) -> {
                        Intent intent = new Intent(this, AddObjectControlWithControlPoint.class);
                        intent.putExtra("id_control", occp.controlPoint.id_control.longValue());
                        intent.putExtra("id_object_point", occp.point_id);
                        intent.putExtra("id_object", getId);
                        intent.putExtra("name", occp.controlPoint.name);
                        intent.putExtra("x", occp.position_x);
                        intent.putExtra("y", occp.position_y);
                        intent.putExtra("nameObject", objectControl.name);
                        mStartForResult.launch(intent);
                    })
                    .setNegativeButton(R.string.delete, (dialog, id) -> {
                        objectControlWithControlPointDAO.deleteId(Math.toIntExact(occp.id_object_point));
                        setControlPoint();
                        dialog.cancel();
                    })
                    .setNeutralButton(R.string.cancel, (dialog, id) -> dialog.cancel());
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setIcon(R.drawable.icons8_question_mark_64);
            alert.setTitle(R.string.question);
            alert.show();
        } else {
            Intent intent = new Intent(this, AddObjectControlWithControlPoint.class);
            intent.putExtra("id_control", - 1L);
            intent.putExtra("id_object_point", - 1L);
            intent.putExtra("id_object", getId);
            intent.putExtra("name", "");
            intent.putExtra("x", x);
            intent.putExtra("y", y);
            intent.putExtra("nameObject", objectControl.name);
            mStartForResult.launch(intent);
            //Toast.makeText(this, "Clicked at x=" + x.toString() + ", y=" + y.toString(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Integer poz = getControlPointByPosition();
        if(poz >= 0){
            ObjectControlControlPoint occp = objectControlControlPoint.get(poz);
            String type_point = occp.controlPoint.type_point;

            if(type_point.equals(getResources().getString(R.string.lamp))){
                Intent intent = new Intent(this, OneSwitch.class);
                intent.putExtra("id", occp.controlPoint.id_control);
                intent.putExtra("on", R.drawable.lamp_on);
                intent.putExtra("off", R.drawable.lamp_off);
                intent.putExtra("basicTopic", objectControl.basicTopic);
                mStartForResult.launch(intent);
            } else if(type_point.equals(getResources().getString(R.string.socket))){
                Intent intent = new Intent(this, OneSwitch.class);
                intent.putExtra("id", occp.controlPoint.id_control);
                intent.putExtra("on", R.mipmap.elektricheskaya_rozetka_foreground);
                intent.putExtra("off", R.mipmap.elektricheskaya_rozetka_foreground);
                intent.putExtra("basicTopic", objectControl.basicTopic);
                mStartForResult.launch(intent);
            } else if(type_point.equals(getResources().getString(R.string.two_lamp)) ||
                    type_point.equals(getResources().getString(R.string.thermometer)) ||
                    type_point.equals(getResources().getString(R.string.barometer)) ||
                    type_point.equals(getResources().getString(R.string.tv)) ||
                    type_point.equals(getResources().getString(R.string.gas_sensor))){
                Intent intent = new Intent(this, TwoSwitch.class);
                intent.putExtra("id", occp.controlPoint.id_control);
                intent.putExtra("basicTopic", objectControl.basicTopic);
                for (MySubscribes mySubscribes: mySubscribesList) {
                    if(type_point.equals(mySubscribes.controlPoint.type_point)){
                        intent.putExtra("value", mySubscribes.getValue());
                    }
                }

                mStartForResult.launch(intent);
            }
        }

    }

    private void setButtons(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        int i = 1;
        int delta = getResources().getInteger(R.integer.deltaPicture);
        relativeLayoutPanorama.removeAllViewsInLayout();
        String imgUrl = objectControl.picture_url;
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Bitmap originalBitmap = BitmapFactory.decodeFile(imgUrl);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 100;

            picture.setImageBitmap(originalBitmap);
        }
        relativeLayoutPanorama.addView(picture);
        for (ObjectControlControlPoint cp: objectControlControlPoint) {

            TextView textView = new TextView(this);
            textView.setId(200000 + i);
            textView.setText(cp.controlPoint.name);
            textView.setTextColor(Color.parseColor("#6CBF0B"));
            textView.setTextSize(20);
            textView.setHeight(100);
            textView.setLayoutParams(param);
            textView.setPadding(15, 5, 15, 5);
            textView.setX(cp.position_x - delta);
            textView.setY(cp.position_y - delta);

            if (cp.controlPoint.type_point.equals(getResources().getString(R.string.thermometer)) ||
                cp.controlPoint.type_point.equals(getResources().getString(R.string.barometer)) ||
                cp.controlPoint.type_point.equals(getResources().getString(R.string.gas_sensor))
            ) {
                String topic = objectControl.basicTopic + cp.controlPoint.topic;
                mySubscribesList.add(new MySubscribes(topic, textView, cp.controlPoint));
            }
             i++;
            //String name = MyListTypePoint.getListTypePoint(this)[3].getPoint();

            relativeLayoutPanorama.addView(textView);
        }
    }

    public static class MySubscribes {
        public String topic;
        public TextView textView;
        public ControlPoint controlPoint;
        public Float value;

        public MySubscribes(String topic, TextView view, ControlPoint controlPoint) {
            this.topic = topic;
            this.textView = view;
            this.controlPoint = controlPoint;
        }

        public void setValue(Float value) { this.value = value; }

        public Float getValue(){ return this.value;}

    }
}

