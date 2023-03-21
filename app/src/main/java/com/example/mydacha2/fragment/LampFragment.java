package com.example.mydacha2.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mydacha2.R;

//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;


public class LampFragment extends Fragment implements View.OnClickListener{

    private TextView textViewTamer;
    private CountDownTimer countDownTimer = null;
    private Button buttonSet;
    private final long set_timer = 960000;
    private final long step_timer = 60000;
    private ImageView imageViewLamp;
 //   private Request request;
 //   private OkHttpClient client;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  client = new OkHttpClient();

   }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSet =  view.findViewById(R.id.buttonSet);
        imageViewLamp = view.findViewById(R.id.imageViewLamp);
        textViewTamer = view.findViewById(R.id.textViewTamer);
        sharedPreferences = getActivity().getSharedPreferences("myDacha", MODE_PRIVATE);

        buttonSet.setOnClickListener(this);
        buttonSet.setText("ВКЛЮЧИТЬ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
           return inflater.inflate(R.layout.fragment_lamp, container, false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSet:

                if (buttonSet.getText().toString() == "ВКЛЮЧИТЬ") {
                    buttonSet.setText("ВЫКЛЮЧИТЬ");
                    buttonSet.setBackgroundResource(R.color.purple_200);
                    post(sharedPreferences.getString("ipNodeServer", ""), "lll");
                    countDownTimer = new CountDownTimer(set_timer, step_timer) {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            textViewTamer.setText(Long.toString(millisUntilFinished / step_timer));
                        }

                        @Override
                        public void onFinish() {
                            buttonSet.setText("ВКЛЮЧИТЬ");
                            textViewTamer.setText("");
                            buttonSet.setBackgroundResource(R.color.purple_700);

                        }
                    }.start();

                    imageViewLamp.setImageResource(R.drawable.lamp_on);
                } else {
                    buttonSet.setText("ВКЛЮЧИТЬ");
                    textViewTamer.setText("");
                    buttonSet.setBackgroundResource(R.color.purple_700);
                    imageViewLamp.setImageResource(R.drawable.lamp_off);
                    countDownTimer.cancel();
                    post(sharedPreferences.getString("ipNodeServer", ""), "lll");

                }
                break;
            default:
                break;
        }
    }

    private void post(String url, String post){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {/*
                request = new Request.Builder()
                        .url(String.format("http://%s/%s", url, post))
                        .build();
                try {
                        Response response = client.newCall(request).execute();
                        if(response.isSuccessful()) {
                            String resultText = response.body().string();
                        }
                } catch (IOException e){

                }
                */

            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}