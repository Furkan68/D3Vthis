package com.furkancetin.parksolutions.parksolutions;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


public class RegistrationService extends IntentService {

    private static final String TAG = "RegistrationService";
    private String _registrationToken;
    private InstanceID _myID;


    public RegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        _myID = InstanceID.getInstance(this);
        try {

            _registrationToken = _myID.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
            );

            Log.d("Registration Token", _registrationToken);

            GcmPubSub subscription = GcmPubSub.getInstance(this);
            subscription.subscribe(_registrationToken, "/topics/my_little_topic", null);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

        }

    }




}