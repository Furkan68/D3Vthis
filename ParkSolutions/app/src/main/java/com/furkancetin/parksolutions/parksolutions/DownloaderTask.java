package com.furkancetin.parksolutions.parksolutions;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloaderTask extends AsyncTask<Void, Void, Data> {

    private MainActivity ma;
    private Data data;
    private boolean isBusy;
    private boolean stop;
    private Handler handler;


    public DownloaderTask(MainActivity ma) {
        this.ma = ma;
        handler = new Handler();
        isBusy = false;//this flag to indicate whether your async task completed or not
        stop = false;//this flag to indicate whether your button stop clicked
        startHandler();
    }

    public void startHandler()
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(!isBusy) callAysncTask();

                if(!stop) startHandler();
            }
        }, 10000);
    }

    private void callAysncTask()
    {
        new DownloaderTask(ma).execute();
    }

    public Data getData() {
        data = new Data();
        HttpURLConnection con = null;

        try {

            URL url = new URL("http://furkancetin.nl/data/");
            con = (HttpURLConnection) url.openConnection();
            // Start the query
            con.connect();

            // Read results from the query
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            // Parse to get translated text
            JSONObject jsonObject = new JSONObject(result.toString());


            for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                data.distance = jsonObject.getJSONArray("data").getJSONObject(i).getDouble("distance");
                data.available = jsonObject.getJSONArray("data").getJSONObject(i).getBoolean("available");
            }



        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "IOException", e);
        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "JSONException", e);
        } catch (Exception e) {
            Log.d(MainActivity.LOG_TAG, "Something went wrong... ", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        // All done
        return data;
    }



    @Override
    protected Data doInBackground(Void... params) {
        return getData();
    }

    @Override
    protected void onPostExecute(Data result) {
        ma.setData(result);
    }


}
