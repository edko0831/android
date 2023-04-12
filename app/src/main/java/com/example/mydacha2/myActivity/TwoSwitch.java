package com.example.mydacha2.myActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.mydacha2.DAO.ControlPointDAO;
import com.example.mydacha2.Entity.ControlPoint;
import com.example.mydacha2.R;
import com.example.mydacha2.fragment.TV_remoteFragment;
import com.example.mydacha2.fragment.ThermometerFragment;
import com.example.mydacha2.fragment.TwoSwitchFragment;
import com.example.mydacha2.repository.App;
import com.example.mydacha2.roomdatabase.AppDatabase;

import java.util.Objects;

public class TwoSwitch extends AppCompatActivity {
    String basicTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_switch);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle arguments = getIntent().getExtras();
        long getId = arguments.getLong("id");
        basicTopic  = arguments.getString("basicTopic");
        Float value  = arguments.getFloat("value", 0F);
        AppDatabase db = App.getInstance(this).getDatabase();
        ControlPointDAO controlPointDAO = db.controlPointDAO();
        ControlPoint controlPoint = controlPointDAO.selectId((int) getId);

        selectionFragment(controlPoint, value);

    }

    private void selectionFragment(ControlPoint controlPoint, Float value) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);

        String type_point = controlPoint.type_point;
        if(type_point.equals(getResources().getString(R.string.thermometer)) ||
           type_point.equals(getResources().getString(R.string.barometer)) ||
           type_point.equals(getResources().getString(R.string.gas_sensor))){
            Intent intent = new Intent(this, TwoSwitch.class);
            intent.putExtra("id", controlPoint.id_control);
            intent.putExtra("value", value);

            ThermometerFragment thermometerFragment = new ThermometerFragment(controlPoint);

            Bundle bundle = new Bundle();

            bundle.putString("basicTopic", basicTopic);
            bundle.putFloat("id", controlPoint.id_control);
            bundle.putFloat("value", value);

            thermometerFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayoutTwoSwitch, thermometerFragment);

        } else if(type_point.equals(getResources().getString(R.string.socket))){
            Intent intent = new Intent(this, OneSwitch.class);
            intent.putExtra("id", controlPoint.id_control);

        } else if(type_point.equals(getResources().getString(R.string.two_lamp))){
            Intent intent = new Intent(this, TwoSwitch.class);
            intent.putExtra("id", controlPoint.id_control);
            SharedPreferences sharedPreferences = getSharedPreferences("myDacha", MODE_PRIVATE);
            TwoSwitchFragment twoSwitchFragment = new TwoSwitchFragment(controlPoint, R.drawable.lamp_on, R.drawable.lamp_off);
            String serverURI = "tcp://" + sharedPreferences.getString("ipNodeServer", "") + ":" + sharedPreferences.getString("port", "");

            Bundle bundle = new Bundle();
            bundle.putString("serverURI", serverURI);
            bundle.putString("basicTopic", basicTopic);

            twoSwitchFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayoutTwoSwitch, twoSwitchFragment);

        }  else if(type_point.equals(getResources().getString(R.string.tv))) {
            Intent intent = new Intent(this, TwoSwitch.class);
            intent.putExtra("id", controlPoint.id_control);
            intent.putExtra("value", value);

            TV_remoteFragment tv_remoteFragment = new TV_remoteFragment(controlPoint);

            Bundle bundle = new Bundle();

            bundle.putString("basicTopic", basicTopic);
            bundle.putFloat("id", controlPoint.id_control);
            bundle.putFloat("value", value);

            tv_remoteFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayoutTwoSwitch, tv_remoteFragment);
        }

        fragmentTransaction.commit();
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