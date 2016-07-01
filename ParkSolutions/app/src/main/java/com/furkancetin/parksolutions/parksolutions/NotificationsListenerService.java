package com.furkancetin.parksolutions.parksolutions;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class NotificationsListenerService extends GcmListenerService {

    public static String LOG_TAG = "MA";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(LOG_TAG, "From: " + from);
        Log.d(LOG_TAG, "Message: " + message);
        // Handle received message here.
    }
}