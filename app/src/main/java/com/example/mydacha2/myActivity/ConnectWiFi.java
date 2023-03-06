package com.example.mydacha2.myActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mydacha2.MainActivity;
import com.example.mydacha2.R;
import com.example.mydacha2.fragment.ConnectFragment;

import java.util.List;

public class ConnectWiFi extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "WiFi connect";
    FrameLayout frameLayout;
    ConnectFragment connectFragment;

    private WifiManager wifiManager;
    public List<ScanResult> availNetworks;
    private static final int MY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wi_fi);
        frameLayout = findViewById(R.id.frameLayout);
        TextView textView = findViewById(R.id.textView_object);
        textView.setText(R.string.connectWiFi_page);

        this.wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        connectFragment = new ConnectFragment(this);
        setFragment(connectFragment);

    }

    private void askAndStartScanWifi() {
         int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Check for permissions
        if (permission1 != PackageManager.PERMISSION_GRANTED) {

            Log.d(LOG_TAG, "Requesting Permissions");

            // Request permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    }, MY_REQUEST_CODE);
            return;
        }
        Log.d(LOG_TAG, "Permissions Already Granted");

        this.wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();
        showNetworks(results);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_close) {
            this.finishAffinity();
        } else if (id == R.id.myObjet) {
            Intent intent1 = new Intent(this, MyObject.class);
            startActivity(intent1);
            //  Toast.makeText(ConnectWiFi.this, getString(R.string.object_control), Toast.LENGTH_LONG).show();
        } else if (id == R.id.home_page) {
            startActivity(new Intent(this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(LOG_TAG, "onRequestPermissionsResult");

        if (requestCode == MY_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.d(LOG_TAG, "Permission Granted: " + permissions[0]);

                // Start Scan Wifi.
                this.wifiManager.startScan();
            } else {
                // Permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.d(LOG_TAG, "Permission Denied: " + permissions[0]);
            }
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
         askAndStartScanWifi();
       // detectWifi();
    }

    // Define class to listen to broadcasts
    class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive()");

            //   Toast.makeText(ConnectWiFi.this, "Scan Complete!", Toast.LENGTH_SHORT).show();

            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (ok) {
                Log.d(LOG_TAG, "Scan OK");
                if (ActivityCompat.checkSelfPermission(ConnectWiFi.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                wifiManager.startScan();
                ConnectWiFi.this.availNetworks = wifiManager.getScanResults();
                ConnectWiFi.this.showNetworks(availNetworks);

            } else {
                Log.d(LOG_TAG, "Scan not OK");
            }
        }
    }


    public void showNetworks(List<ScanResult> availNetworks) {
        this.availNetworks = availNetworks;
        if ((long) availNetworks.size() > 0) {
            connectFragment.showNetworks(availNetworks);
         }
    }

    public void connectToNetwork(String networkCapabilities, String networkSSID) {
        Toast.makeText(this, "Connecting to network: " + networkSSID, Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences("myDacha", MODE_PRIVATE);
        String networkPass = sharedPreferences.getString("passwordNodeServer", "");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Проверяем наличие разрешения на использование Wi-Fi
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Если разрешения нет, запрашиваем его
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_CODE);
            }

            // Создаем экземпляр WifiManager
            // WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // Включаем Wi-Fi (если еще не включен)
            //  if (!wifiManager.isWifiEnabled()) {
            //     wifiManager.setWifiEnabled(true);
            //}

            // Создаем экземпляр WifiNetworkSpecifier.Builder для подключения к сети
            WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();

            // Устанавливаем SSID и пароль для сети
            builder.setSsid(networkSSID);
            builder.setWpa2Passphrase(networkPass);

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
                public void onAvailable(@io.reactivex.rxjava3.annotations.NonNull Network network) {
                    Toast.makeText(ConnectWiFi.this, "Подключение успешно", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onUnavailable() {
                    Toast.makeText(ConnectWiFi.this, "Подключение не удалось", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\"" + networkSSID + "\"";

            if (networkCapabilities.toUpperCase().contains("WEP")) { // WEP Network.
                Toast.makeText(this, "WEP Network", Toast.LENGTH_SHORT).show();

                wifiConfig.wepKeys[0] = "\"" + networkPass + "\"";
                wifiConfig.wepTxKeyIndex = 0;
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            } else if (networkCapabilities.toUpperCase().contains("WPA")) { // WPA Network
                Toast.makeText(this, "WPA Network", Toast.LENGTH_SHORT).show();
                wifiConfig.preSharedKey = "\"" + networkPass + "\"";
            } else { // OPEN Network.
                Toast.makeText(this, "OPEN Network", Toast.LENGTH_SHORT).show();
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }

            this.wifiManager.addNetwork(wifiConfig);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            List<WifiConfiguration> list = this.wifiManager.getConfiguredNetworks();
            for (WifiConfiguration config : list) {
                if (config.SSID != null && config.SSID.equals("\"" + networkSSID + "\"")) {
                    this.wifiManager.disconnect();
                    this.wifiManager.enableNetwork(config.networkId, true);
                    this.wifiManager.reconnect();
                    break;
                }
            }
        }
    }
}