package com.example.mydacha2.fragment;


import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mydacha2.R;
import com.example.mydacha2.myActivity.ConnectWiFi;

import java.util.List;


public class ConnectFragment extends Fragment {

    private LinearLayout linearLayoutScanResults;
    private TextView textViewScanResults;
    private final View.OnClickListener myButtonClickListener;

    public ConnectFragment(View.OnClickListener myButtonClickListener) {
        this.myButtonClickListener = myButtonClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonSet = view.findViewById(R.id.buttonSet);
        linearLayoutScanResults = (LinearLayout) view.findViewById(R.id.linearLayout_scanResults);
        textViewScanResults = (TextView) view.findViewById(R.id.textView_scanResults);
        buttonSet.setOnClickListener(myButtonClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false);

    }

    public void showNetworks(List<ScanResult> results) {
        this.linearLayoutScanResults.removeAllViews();

        for( final ScanResult result: results)  {
            final String networkCapabilities = result.capabilities;
            final String networkSSID = result.SSID; // Network Name.

            Button button = new Button(getActivity());

            button.setText(networkSSID + " ("+networkCapabilities+")");
            this.linearLayoutScanResults.addView(button);

            button.setOnClickListener(v -> {
                String networkCapabilities1 = result.capabilities;
                try {
                    ((ConnectWiFi) getActivity()).connectToNetwork(networkCapabilities1, networkSSID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void showNetworksDetails(List<ScanResult> results)  {

        this.textViewScanResults.setText("");
        StringBuilder sb = new StringBuilder();
        sb.append("Result Count: " + results.size());

        for(int i = 0; i < results.size(); i++ )  {
            ScanResult result = results.get(i);
            sb.append("\n\n  --------- Network " + i + "/" + results.size() + " ---------");

            sb.append("\n result.capabilities: " + result.capabilities);
            sb.append("\n result.SSID: " + result.SSID); // Network Name.

            sb.append("\n result.BSSID: " + result.BSSID);
            sb.append("\n result.frequency: " + result.frequency);
            sb.append("\n result.level: " + result.level);

            sb.append("\n result.describeContents(): " + result.describeContents());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // Level 17, Android 4.2
                sb.append("\n result.timestamp: " + result.timestamp);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Level 23, Android 6.0
                sb.append("\n result.centerFreq0: " + result.centerFreq0);
                sb.append("\n result.centerFreq1: " + result.centerFreq1);
                sb.append("\n result.venueName: " + result.venueName);
                sb.append("\n result.operatorFriendlyName: " + result.operatorFriendlyName);
                sb.append("\n result.channelWidth: " + result.channelWidth);
                sb.append("\n result.is80211mcResponder(): " + result.is80211mcResponder());
                sb.append("\n result.isPasspointNetwork(): " + result.isPasspointNetwork() );
            }
        }
        this.textViewScanResults.setText(sb.toString());
    }

    public void showNetworks1(List<WifiConfiguration> results) {
        this.linearLayoutScanResults.removeAllViews();

        for( final WifiConfiguration result: results)  {
            final Integer networkCapabilities = result.networkId;
            final String networkSSID = result.SSID; // Network Name.

            Button button = new Button(getActivity());

            button.setText(networkSSID + " ("+ networkCapabilities.toString() +")");
            this.linearLayoutScanResults.addView(button);

            button.setOnClickListener(v -> {
                int networkCapabilities1 = result.networkId;
                try {
                 //   ((ConnectWiFi) getActivity()).connectToNetwork(networkCapabilities1, networkSSID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void showNetworksDetails1(List<WifiConfiguration> results)  {
/*
        this.textViewScanResults.setText("");
        StringBuilder sb = new StringBuilder();
        sb.append("Result Count: " + results.size());

        for(int i = 0; i < results.size(); i++ )  {
            ScanResult result = results.get(i);
            sb.append("\n\n  --------- Network " + i + "/" + results.size() + " ---------");

            sb.append("\n result.capabilities: " + result.capabilities);
            sb.append("\n result.SSID: " + result.SSID); // Network Name.

            sb.append("\n result.BSSID: " + result.BSSID);
            sb.append("\n result.frequency: " + result.frequency);
            sb.append("\n result.level: " + result.level);

            sb.append("\n result.describeContents(): " + result.describeContents());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // Level 17, Android 4.2
                sb.append("\n result.timestamp: " + result.timestamp);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Level 23, Android 6.0
                sb.append("\n result.centerFreq0: " + result.centerFreq0);
                sb.append("\n result.centerFreq1: " + result.centerFreq1);
                sb.append("\n result.venueName: " + result.venueName);
                sb.append("\n result.operatorFriendlyName: " + result.operatorFriendlyName);
                sb.append("\n result.channelWidth: " + result.channelWidth);
                sb.append("\n result.is80211mcResponder(): " + result.is80211mcResponder());
                sb.append("\n result.isPasspointNetwork(): " + result.isPasspointNetwork() );
            }
        }
        this.textViewScanResults.setText(sb.toString());

 */
    }


}