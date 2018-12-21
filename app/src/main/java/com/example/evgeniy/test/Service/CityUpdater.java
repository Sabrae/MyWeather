package com.example.evgeniy.test.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.evgeniy.test.Objects.Data;
import com.example.evgeniy.test.R;
import com.example.evgeniy.test.SQL.DBHelper;
import com.example.evgeniy.test.Tasks.GetWeather;
import com.example.evgeniy.test.Threads.MyRun;

import static com.example.evgeniy.test.Enums.ErrorType.NoInternet;

public class CityUpdater extends Service implements GetWeather.Callback {

    public static final String LOG_TAG = "CityUpdater";
    private String CityName;

    private MyRun thread;

    private int notifyCounter = 1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String yaUrl = intent.getStringExtra("yaUrl");
        String yaKey = intent.getStringExtra("yaKey");
        String lat = intent.getStringExtra("lat");
        String lon = intent.getStringExtra("lon");
        CityName = intent.getStringExtra("CityName");

        thread = new MyRun(yaUrl, yaKey, this, lat, lon);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.thread.Stop();
        Log.d(LOG_TAG, "onDestroy service: ");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onComplete(Data result) {
        Log.d(LOG_TAG, "onComplete: " + result.errorType);
        result.fullName = CityName;
        if (result.err.equals("")) {
            DBHelper dbHelper = new DBHelper(this, "Weather", null, DBHelper.DBVersion);
            long id = dbHelper.setWeatherToBD(dbHelper.getReadableDatabase(), result);
            dbHelper.close();
            Log.d(LOG_TAG, "onComplete: " + "скачивание завершено успешно id = " + id);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Погода")
                            .setContentText("Сейчас " + result.temp);

            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notifyCounter, notification);
            notifyCounter++;
        } else {
            if (result.errorType == NoInternet) {
                Log.d(LOG_TAG, "onComplete: " + "скачивание завершено с ошибкой noInternet");
            } else {
                Log.d(LOG_TAG, "onComplete: " + "скачивание завершено с ошибкой " + result.err + " errType " + result.errorType);
            }
        }
    }


}
