package com.example.mydacha2.supportclass;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mydacha2.MainActivity;

import java.util.List;


public class WifiBroadcastReceiver11 extends BroadcastReceiver {
    private static final String TAG = "WiFi connect";
    private List<ScanResult> availNetworks;
    private WifiManager wifiManager;
    Context myContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");

        myContext = context;

        Toast.makeText(context, "Scan Complete!", Toast.LENGTH_SHORT).show();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

        if (ok) {
            Log.d(TAG, "Scan OK");
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            scanSuccess();
        } else {
            Log.d(TAG, "Scan not OK");
        }

    }

    private void scanSuccess() {
       // availNetworks = wifiManager.getScanResults();
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public List<ScanResult> getAvailNetworks() {
        WifiManager wmgr = wifiManager = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);

        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return availNetworks;
        }
        availNetworks = wmgr.getScanResults();

        return availNetworks;
    }

    public void doStartScanWifi() {
        this.wifiManager.startScan();
    }

    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }
}
