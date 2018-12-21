package com.example.evgeniy.test.Threads;

import android.os.AsyncTask;
import android.util.Log;

import com.example.evgeniy.test.Tasks.GetWeather.Callback;
import com.example.evgeniy.test.Tasks.GetWeather;

import java.util.concurrent.TimeUnit;

public class MyRun extends Thread {
    private String yaUrl;
    private String yaKey;
    private String lat;
    private String lon;
    private Callback callback;

    private GetWeather weather;

    private boolean work = true;
    private String LOG_TAG = "MYRUN";

    public MyRun(String yaUrl, String yaKey, Callback callback, String lat, String lon) {
        this.yaKey = yaKey;
        this.yaUrl = yaUrl;
        this.callback = callback;
        this.lat = lat;
        this.lon = lon;
    }

    public void run() {
        while (work) {
            weather = new GetWeather(null, null, null, null, null, yaUrl, yaKey, callback);
            weather.execute(lat, lon);
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "run: ");
        }

    }

    public void Stop() {
        if (weather.getStatus() == AsyncTask.Status.RUNNING)
            weather.cancel(false);
        work = false;
        Log.d(LOG_TAG, "Stop thread: ");
    }
}
