package com.innoteam.atomodappinstalldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Yoshua on 2/26/15.
 */

public class Networkreceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "NETWORKACTIVITY";
    private static TelephonyManager telephonyManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Network intent received");


        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo[] network = cm.getAllNetworkInfo();

        Log.v(TAG, "action: " + intent.getAction());
        Log.v(TAG, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.v(TAG, "key [" + key + "]: " + extras.get(key));
            }
        }
        else {
            Log.v(TAG, "No extras");
        }

        for (int i= 0; i<network.length;i++){
            Log.v(TAG, network[i].toString());

        }
    }

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onCallForwardingIndicatorChanged(boolean cfi) {}
        public void onCallStateChanged(int state, String incomingNumber) {}
        public void onCellLocationChanged(CellLocation location) {}
        public void onDataActivity(int direction) {}
        public void onDataConnectionStateChanged(int state) {}
        public void onMessageWaitingIndicatorChanged(boolean mwi) {}
        public void onServiceStateChanged(ServiceState serviceState) {}
        public void onSignalStrengthChanged(int asu) {}
    };

}