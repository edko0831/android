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
            // Создаем экземпляр ConnectivityManager
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            // Получаем информацию о текущем активном подключении
            Network network = connManager.getActiveNetwork();
            NetworkCapabilities capabilities = connManager.getNetworkCapabilities(network);

            // Проверяем, подключен ли телефон к Wi-Fi
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // Если да, получаем информацию о текущей сети Wi-Fi
                WifiInfo wifiInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
                String ssidConnect = wifiInfo.getSSID();
                if (ssidConnect.equals("\"" + ssid + "\"")){
                    return;
                }
            } else {
                // Если телефон не подключен к Wi-Fi, выводим сообщение об ошибке
                Toast.makeText(MainActivity.this, "Phone is not connected to Wi-Fi", Toast.LENGTH_LONG).show();
            }

            // Проверяем наличие разрешения на использование Wi-Fi
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Если разрешения нет, запрашиваем его
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            }

            // Создаем экземпляр WifiManager
            //WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // Включаем Wi-Fi (если еще не включен)
            //if (!wifiManager.isWifiEnabled()) {
            //    wifiManager.setWifiEnabled(true);
            //}

            // Создаем экземпляр WifiNetworkSpecifier.Builder для подключения к сети
            WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();

            // Устанавливаем SSID и пароль для сети
            builder.setSsid(ssid);
            builder.setWpa2Passphrase(password);

            // Создаем экземпляр WifiNetworkSpecifier
            WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

            // Создаем экземпляр NetworkRequest.Builder
            NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();

            // Устанавливаем тип соединения
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

            // Устанавливаем спецификацию сети
            networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);

            // Создаем экземпляр ConnectivityManager
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            // Запрашиваем подключение к сети
            connectivityManager.requestNetwork(networkRequestBuilder.build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    Toast.makeText(MainActivity.this, "Подключение успешно", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onUnavailable() {
                    Toast.makeText(MainActivity.this, "Подключение не удалось", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Создаем экземпляр WifiManager
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // Получаем информацию о текущем подключении Wi-Fi
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            // Выводим информацию на экран
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

    @RequiresApi(api = 33)
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
            // Создаем экземпляр WifiManager
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // Отключаем текущее соединение Wi-Fi
            wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
            wifiManager.disconnect();

            this.finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = 33)
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