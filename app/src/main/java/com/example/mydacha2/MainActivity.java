package com.example.mydacha2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.mydacha2.fragment.MainActivityNewFragment;
import com.example.mydacha2.myActivity.ConnectWiFi;
import com.example.mydacha2.myActivity.AddControlPointActivity;
import com.example.mydacha2.myActivity.MyObject;
import com.example.mydacha2.myActivity.SettingActivity;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyListMain;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MyClickListener {
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_new);
        MainActivityNewFragment mainActivityNewFragment = new MainActivityNewFragment(this, setMyListMain());

        TextView textView = findViewById(R.id.textView_object);
        textView.setText(R.string.main_page);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_properties, mainActivityNewFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        onConnectWiFi();
    }

    private void onConnectWiFi(){
       wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
         //узнаем включен вайфай  или нет
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

       connectToNetwork();
    }

    private void connectToNetwork() {

        SharedPreferences sharedPreferences =  getSharedPreferences("myDacha", MODE_PRIVATE);

        String ipNodeServer = sharedPreferences.getString("ipNodeServer", "");
        if(ipNodeServer.isEmpty()){
            return;
        }
        String nameNodeServer = sharedPreferences.getString("nameNodeServer", "");
        String networkPass = sharedPreferences.getString("passwordNodeServer", "");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<WifiConfiguration> list = this.wifiManager.getConfiguredNetworks();
        if (list.isEmpty()){return;}
        for( WifiConfiguration config : list ) {
            if (config.SSID != null && config.SSID.equals("\"" + nameNodeServer + "\"")) {
                this.wifiManager.disconnect();
                config.wepKeys[0] = "\"" + networkPass + "\"";
                config.preSharedKey = "\""+ networkPass +"\"";
                this.wifiManager.enableNetwork(config.networkId, true);
                this.wifiManager.reconnect();
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.connect) {
            Intent connectWiFi = new Intent(this, ConnectWiFi.class);
            startActivity(connectWiFi);
        } else if (id == R.id.control_point) {
            Intent controlPointActivity = new Intent(this, AddControlPointActivity.class);
            startActivity(controlPointActivity);
          //  Toast.makeText(this, getString(R.string.lamp_blank_fragment), Toast.LENGTH_LONG).show();
        } else if (id == R.id.myObjet) {
            Intent intent1 = new Intent(this, MyObject.class);
            startActivity(intent1);
         //   Toast.makeText(this, getString(R.string.object_control), Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_close) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Long position) {
        if (position == 3) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        } else if (position == 2) {
            Intent intent1 = new Intent(this, ConnectWiFi.class);
            startActivity(intent1);
        } else if (position == 1) {
            Intent intent1 = new Intent(this, MyObject.class);
            startActivity(intent1);
        }
    }

    @Override
    public void onItemLongClick(Long position) {

    }
    private MyListMain[] setMyListMain() {
        return new MyListMain[]{
                new MyListMain(1L, getString(R.string.object_control), R.mipmap.icons8_object_controls_94_foreground),
                new MyListMain(2L, getString(R.string.connectWiFi), R.mipmap.icons8_search_satellites_94_foreground),
                new MyListMain(3L, getString(R.string.action_settings), R.mipmap.icons8_gear_94_foreground),
                new MyListMain(4L, "Dialer", android.R.drawable.ic_dialog_dialer),
                new MyListMain(5L, "Alert", android.R.drawable.ic_dialog_alert),
                new MyListMain(6L, "Map", android.R.drawable.ic_dialog_map)
        };
    }
}