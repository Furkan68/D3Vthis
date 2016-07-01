package com.furkancetin.parksolutions.parksolutions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static String LOG_TAG = "MA";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    private DownloaderTask _mTask;
    private TextView _available;
    private FrameLayout _secondPanel;
    private TextView _boxTitle;
    private ImageView _parkGreen;
    private ImageView _parkRed;
    private Button _requestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationService.class);
            startService(intent);
        }

        if (isOnline()) {
            _mTask = new DownloaderTask(this);
            _mTask.execute();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_internet_title))
                    .setMessage(getString(R.string.dialog_internet_text))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        final AlertDialog x = CreateDialog();
        _requestButton = (Button) findViewById(R.id.park_request_button);
        _requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                x.show();

            }
        });

    }

    private AlertDialog CreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                setRequest();
            }
        });

        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void setRequest() {
        HttpURLConnection con = null;
        try {
            URL url = new URL("http://furkancetin.nl/data/?requested=true");
            con = (HttpURLConnection) url.openConnection();
            con.connect();
            con.getInputStream();

        } catch (Exception ex) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void setData(Data data) {
        _available = (TextView) findViewById(R.id.available_places);
        _secondPanel = (FrameLayout) findViewById(R.id.secondPanel);
        _boxTitle = (TextView) findViewById(R.id.box_title);
        _parkGreen = (ImageView) findViewById(R.id.park_green);
        _parkRed = (ImageView) findViewById(R.id.park_red);

//        Log.d(LOG_TAG, Boolean.toString(data.available));
//        Log.d(LOG_TAG, Double.toString(data.distance));

        if (data.available) {
            _boxTitle.setText(R.string.park_available);
            _parkGreen.setAlpha(1f);
            _parkRed.setAlpha(0.1f);
            _available.setText("1");
            _secondPanel.setBackgroundColor(getResources().getColor(R.color.orange));


            final Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr=51.914785,4.435846"));
            _parkGreen.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(intent);
                }
            });

        } else {
            _boxTitle.setText(R.string.park_unavailable);
            _parkGreen.setAlpha(0.1f);
            _parkRed.setAlpha(1f);
            _available.setText("0");
            _secondPanel.setBackgroundColor(getResources().getColor(R.color.red));

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}