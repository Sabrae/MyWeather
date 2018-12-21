package com.example.evgeniy.test.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.evgeniy.test.Objects.Data;
import com.example.evgeniy.test.R;
import com.example.evgeniy.test.SQL.DBHelper;

public class WeatherFromDB extends AppCompatActivity {

    private static final String LOG_TAG = "WheatherFromBD";

    private TextView twTemp;
    private TextView tvPressure;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private TextView tvCloudiness;
    private TextView tvCityName;
    private TextView tvFeelingTemp;
    private TextView tvCondition;
    private TextView tvWindDir;
    private LinearLayout LL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(LOG_TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet);

        Intent intent = getIntent();
        Data fact = intent.getParcelableExtra("fact");
        Log.d(LOG_TAG, "onCreate: fact " + fact.fullName + " " + fact.errorType);
        twTemp = findViewById(R.id.tvTemp);
        tvPressure = findViewById(R.id.tvPressure);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvCloudiness = findViewById(R.id.tvCloudiness);
        tvCityName = findViewById(R.id.tvCityName);
        tvFeelingTemp = findViewById(R.id.tvFeelingTemp);
        tvCondition = findViewById(R.id.tvCondition);
        tvWindDir = findViewById(R.id.tvWindDir);

        LL = findViewById(R.id.LL);

        Log.d(LOG_TAG, "onCreate: fact x2 " + fact.toString());

        setWeather(fact);
    }


    private void setWeather(Data fact) {
        tvCityName.setText(fact.City);
        tvFeelingTemp.setText(fact.feels_like);
        tvWindSpeed.setText(fact.wind_speed);
        tvPressure.setText(fact.pressure_mm);
        tvHumidity.setText(fact.humidity);
        twTemp.setText(fact.temp);
        tvCloudiness.setText(fact.Cloudnes);

        switch (fact.daytime) {
            case "n":
                LL.setBackgroundResource(R.color.dark_blue);
                break;
            case "d":
                LL.setBackgroundResource(R.color.light_blue);
                break;
            default: {
                Log.d(LOG_TAG, "Цвет не распознан " + fact.daytime);
                LL.setBackgroundResource(R.color.light_blue);
            }
            break;

        }

        switch (fact.wind_dir) {
            case "nw":
                tvWindDir.setText("СЗ");
                break;
            case "n":
                tvWindDir.setText("С");
                break;
            case "ne":
                tvWindDir.setText("СВ");
                break;
            case "e":
                tvWindDir.setText("В");
                break;
            case "se":
                tvWindDir.setText("ЮВ");
                break;
            case "s":
                tvWindDir.setText("Ю");
                break;
            case "sn":
                tvWindDir.setText("ЮЗ");
                break;
            case "w":
                tvWindDir.setText("З");
                break;
            case "c":
                tvWindDir.setText("штиль");
                break;
            default: {
                Log.e(LOG_TAG, "Не найдено направление " + fact.wind_dir);
                tvWindDir.setText("Неопределено");
            }
            break;
        }
        tvCondition.setText("");
        tvCondition.setVisibility(View.VISIBLE);
        switch (fact.condition) {
            case "clear":
                tvCondition.setText(R.string.clear);
                break;
            case "partly-cloudy":
                tvCondition.setText(R.string.partly_cloudy);
                break;
            case "cloudy":
                tvCondition.setText(R.string.cloudy);
                break;
            case "overcast":
                tvCondition.setText(R.string.overcast);
                break;
            case "partly-cloudy-and-light-rain":
                tvCondition.setText(R.string.partly_cloudy_and_light_rain);
                break;
            case "partly-cloudy-and-rain":
                tvCondition.setText(R.string.partly_cloudy_and_rain);
                break;
            case "overcast-and-rain":
                tvCondition.setText(R.string.overcast_and_rain);
                break;
            case "overcast-thunderstorms-with-rain":
                tvCondition.setText(R.string.overcast_thunderstorms_with_rain);
                break;
            case "cloudy-and-light-rain":
                tvCondition.setText(R.string.cloudy_and_light_rain);
                break;
            case "overcast-and-light-rain":
                tvCondition.setText(R.string.overcast_and_light_rain);
                break;
            case "cloudy-and-rain":
                tvCondition.setText(R.string.cloudy_and_rain);
                break;
            case "overcast-and-wet-snow":
                tvCondition.setText(R.string.overcast_and_wet_snow);
                break;
            case "partly-cloudy-and-light-snow":
                tvCondition.setText(R.string.partly_cloudy_and_light_snow);
                break;
            case "partly-cloudy-and-snow":
                tvCondition.setText(R.string.partly_cloudy_and_snow);
                break;
            case "overcast-and-snow":
                tvCondition.setText(R.string.overcast_and_snow);
                break;
            case "overcast-and-light-snow":
                tvCondition.setText(R.string.overcast_and_light_snow);
                break;
            case "cloudy-and-light-snow":
                tvCondition.setText(R.string.cloudy_and_light_snow);
                break;
            case "cloudy-and-snow":
                tvCondition.setText(R.string.cloudy_and_snow);
                break;
            default: {
                Log.e(LOG_TAG, "Не найден файл ресурсов " + fact.condition);
                tvCondition.setVisibility(View.GONE);
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
