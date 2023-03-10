package com.example.mydacha2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.mydacha2.fragment.MainActivityNewFragment;
import com.example.mydacha2.myActivity.ConnectWiFi;
import com.example.mydacha2.myActivity.ListControlPointActivity;
import com.example.mydacha2.myActivity.MyObject;
import com.example.mydacha2.myActivity.SettingActivity;
import com.example.mydacha2.supportclass.MyClickListener;
import com.example.mydacha2.supportclass.MyListMain;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class MainActivity extends AppCompatActivity implements MyClickListener {
    private static final int PERMISSIONS_REQUEST_CODE = 1234;

    @RequiresApi(api = Build.VERSION_CODES.Q)
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
        SharedPreferences sharedPreferences = getSharedPreferences("myDacha", MODE_PRIVATE);
        String ipNodeServer = sharedPreferences.getString("ipNodeServer", "");
        if (ipNodeServer.isEmpty()) {
            return;
        }
        String ssid = sharedPreferences.getString("nameNodeServer", "");
        String password = sharedPreferences.getString("passwordNodeServer", "");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // ?????????????? ?????????????????? ConnectivityManager
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            // ???????????????? ???????????????????? ?? ?????????????? ???????????????? ??????????????????????
            Network network = connManager.getActiveNetwork();
            NetworkCapabilities capabilities = connManager.getNetworkCapabilities(network);

            // ??????????????????, ?????????????????? ???? ?????????????? ?? Wi-Fi
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // ???????? ????, ???????????????? ???????????????????? ?? ?????????????? ???????? Wi-Fi
                WifiInfo wifiInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
                String ssidConnect = wifiInfo.getSSID();
                if (ssidConnect.equals("\"" + ssid + "\"")){
                    return;
                }
            } else {
                // ???????? ?????????????? ???? ?????????????????? ?? Wi-Fi, ?????????????? ?????????????????? ???? ????????????
                Toast.makeText(MainActivity.this, "Phone is not connected to Wi-Fi", Toast.LENGTH_LONG).show();
            }

            // ?????????????????? ?????????????? ???????????????????? ???? ?????????????????????????? Wi-Fi
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ???????? ???????????????????? ??????, ?????????????????????? ??????
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            }

            // ?????????????? ?????????????????? WifiManager
            //WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // ???????????????? Wi-Fi (???????? ?????? ???? ??????????????)
            //if (!wifiManager.isWifiEnabled()) {
            //    wifiManager.setWifiEnabled(true);
            //}

            // ?????????????? ?????????????????? WifiNetworkSpecifier.Builder ?????? ?????????????????????? ?? ????????
            WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();

            // ?????????????????????????? SSID ?? ???????????? ?????? ????????
            builder.setSsid(ssid);
            builder.setWpa2Passphrase(password);

            // ?????????????? ?????????????????? WifiNetworkSpecifier
            WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

            // ?????????????? ?????????????????? NetworkRequest.Builder
            NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();

            // ?????????????????????????? ?????? ????????????????????
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

            // ?????????????????????????? ???????????????????????? ????????
            networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);

            // ?????????????? ?????????????????? ConnectivityManager
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            // ?????????????????????? ?????????????????????? ?? ????????
            connectivityManager.requestNetwork(networkRequestBuilder.build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    Toast.makeText(MainActivity.this, "?????????????????????? ??????????????", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onUnavailable() {
                    Toast.makeText(MainActivity.this, "?????????????????????? ???? ??????????????", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // ?????????????? ?????????????????? WifiManager
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // ???????????????? ???????????????????? ?? ?????????????? ?????????????????????? Wi-Fi
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            // ?????????????? ???????????????????? ???? ??????????
            String ssidConnect = wifiInfo.getSSID();

            if(ssidConnect.equals(ssid)){ return; }

            WifiConfiguration wifiConfig = new WifiConfiguration();

            wifiConfig.SSID =  "\"" + ssid + "\"";
            wifiConfig.preSharedKey = "\""+ password +"\"";

            wifiManager.addNetwork(wifiConfig);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration config : list ) {
                if(config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(config.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            Intent listControlPointActivity = new Intent(this, ListControlPointActivity.class);
            startActivity(listControlPointActivity);
          //  Toast.makeText(this, getString(R.string.lamp_blank_fragment), Toast.LENGTH_LONG).show();
        } else if (id == R.id.myObjet) {
            Intent intent1 = new Intent(this, MyObject.class);
            startActivity(intent1);
         //   Toast.makeText(this, getString(R.string.object_control), Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_close) {
            // ?????????????? ?????????????????? WifiManager
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // ?????????????????? ?????????????? ???????????????????? Wi-Fi
            wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
            wifiManager.disconnect();

            this.finishAffinity();
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
    //            new MyListMain(4L, "Dialer", android.R.drawable.ic_dialog_dialer),
    //            new MyListMain(5L, "Alert", android.R.drawable.ic_dialog_alert),
    //            new MyListMain(6L, "Map", android.R.drawable.ic_dialog_map)
        };
    }
}