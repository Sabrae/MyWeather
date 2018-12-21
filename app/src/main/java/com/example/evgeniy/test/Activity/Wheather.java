package com.example.evgeniy.test.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmadrosid.svgloader.SvgLoader;
import com.example.evgeniy.test.Objects.Data;
import com.example.evgeniy.test.Objects.Day;
import com.example.evgeniy.test.R;
import com.example.evgeniy.test.SQL.DBHelper;
import com.example.evgeniy.test.Service.CityUpdater;

import java.util.ArrayList;
import java.util.Objects;


public class Wheather extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "Wheather";

    private Context context = this;

    private TextView twTemp;
    private TextView tvPressure;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private TextView tvCloudiness;
    private TextView tvCityName;
    private TextView tvFeelingTemp;
    private TextView tvCondition;
    private TextView tvWindDir;

    private Button btnSelect;

    private LinearLayout LL;

    private ImageView ivWeatherType;

    private Data fact;


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wheather);
        Intent intent = getIntent();
        fact = intent.getParcelableExtra("fact");
        if(fact == null)
            fact = new Data();

        Parcelable[] input = Objects.requireNonNull(intent.getExtras()).getParcelableArray("forecasts");
        ArrayList<Day> days = new ArrayList<>();
        if (input != null) {
            for (int i = 0; i < input.length; i++) {
                days.add((Day) input[i]);
            }
        } else {
            Log.e(LOG_TAG, "получен прогноз null");
        }
        //запись данных в БД
        DBHelper dbHelper = new DBHelper(this, "Weather", null, DBHelper.DBVersion);
        dbHelper.setWeatherToBD(dbHelper.getReadableDatabase(), fact);
        dbHelper.close();
        //Log.d(LOG_TAG, "Data = " + fact.toString());

        twTemp = findViewById(R.id.tvTemp);
        tvPressure = findViewById(R.id.tvPressure);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvCloudiness = findViewById(R.id.tvCloudiness);
        tvCityName = findViewById(R.id.tvCityName);
        tvFeelingTemp = findViewById(R.id.tvFeelingTemp);
        ivWeatherType = findViewById(R.id.ivWeatherType);
        tvCondition = findViewById(R.id.tvCondition);
        tvWindDir = findViewById(R.id.tvWindDir);

        btnSelect = findViewById(R.id.btnSelect);

        LL = findViewById(R.id.LL);

        tvCityName.setText(fact.City);
        tvFeelingTemp.setText(fact.feels_like);
        tvWindSpeed.setText(fact.wind_speed);
        tvPressure.setText(fact.pressure_mm);
        tvHumidity.setText(fact.humidity);
        twTemp.setText(fact.temp);
        tvCloudiness.setText(fact.Cloudnes);

        btnSelect.setOnClickListener(this);

        if (isMyServiceRunning(CityUpdater.class)) {
            btnSelect.setEnabled(false);
        }

        int idBGColor = getResources().getIdentifier(fact.daytime, "drawable", "com.example.evgeniy.test");
        if (idBGColor != 0) {
            LL.setBackgroundResource(idBGColor);
        } else {
            Log.d(LOG_TAG, "Цвет не распознан " + fact.daytime);
            LL.setBackgroundResource(R.color.light_blue);
        }

        int idWindDir = getResources().getIdentifier(fact.wind_dir, "drawable", "com.example.evgeniy.test");
        if (idWindDir != 0) {
            tvWindDir.setText(idWindDir);
        } else {
            Log.e(LOG_TAG, "Не найдено направление " + fact.wind_dir);
            tvWindDir.setText("Неопределено");
        }

        SvgLoader.pluck()
                .with(this)
                .setPlaceHolder(R.drawable.empty, R.drawable.empty)
                .load(fact.iconUrl, ivWeatherType);

        tvCondition.setText("");
        tvCondition.setVisibility(View.VISIBLE);
        int idCondition = getResources().getIdentifier(fact.condition, "drawable", "com.example.evgeniy.test");
        if (idCondition != 0)
            tvCondition.setText(idCondition);
        else {
            Log.e(LOG_TAG, "Не найден файл ресурсов " + fact.condition);
            tvCondition.setVisibility(View.GONE);
        }

        //заполнение списка прогноза
        int[] colors = new int[2];
        colors[0] = Color.WHITE;
        colors[1] = Color.LTGRAY;
        LinearLayout linLayout = findViewById(R.id.linLayout);
        LayoutInflater ltInflater = getLayoutInflater();

        for (int i = 0; i < days.size() - 1; i++) {
            Day day = days.get(i);

            View item = ltInflater.inflate(R.layout.list_item, linLayout, false);
            TextView tvDate = item.findViewById(R.id.tvDate);
            tvDate.setText(day.date);

            TextView tvTempDay = item.findViewById(R.id.tvTempDay);
            tvTempDay.setText(day.TempAVGDay);

            TextView tvTempNight = item.findViewById(R.id.tvTempNight);
            tvTempNight.setText(day.TempAVGNight);

            ImageView ivIcon = item.findViewById(R.id.ivIconWeather);
            SvgLoader.pluck()
                    .with(this)
                    .setPlaceHolder(R.drawable.ic_sync_black_24dp, R.drawable.ic_error_outline_black_24dp)
                    .load(day.iconUrlDay, ivIcon);

            item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            item.setBackgroundColor(colors[i % 2]);
            linLayout.addView(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isMyServiceRunning(CityUpdater.class)) {
            Intent intent = new Intent(this, CityUpdater.class);
            intent.putExtra("yaUrl", MainActivity.yaUrl);
            intent.putExtra("yaKey", MainActivity.yakey);
            intent.putExtra("lat", fact.lat);
            intent.putExtra("lon", fact.lon);
            intent.putExtra("CityName", fact.fullName);
            startService(intent);
            v.setEnabled(false);
        } else {
            v.setEnabled(false);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        return false;
    }
}
