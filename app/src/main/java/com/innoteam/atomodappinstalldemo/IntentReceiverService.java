package com.innoteam.atomodappinstalldemo;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Yoshua on 2/26/15.
 */
public class IntentReceiverService extends IntentService {

    private static String TAG = "IntentReceiverService";
    public IntentReceiverService() {
        super("IntentReceiverService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");

    }

    @Override
    protected void onHandleIntent(Intent intent) {



    }
}
