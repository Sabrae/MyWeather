package com.example.evgeniy.test.Activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.ahmadrosid.svgloader.SvgLoader;
import com.example.evgeniy.test.Objects.City;
import com.example.evgeniy.test.Objects.Data;
import com.example.evgeniy.test.R;
import com.example.evgeniy.test.SQL.DBHelper;
import com.example.evgeniy.test.Service.CityUpdater;
import com.example.evgeniy.test.Tasks.GetCityes;
import com.example.evgeniy.test.Tasks.GetWeather;

import java.util.ArrayList;

import static com.example.evgeniy.test.Enums.ErrorType.NoInternet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetCityes.Callback, GetWeather.Callback {

    private static final String LOG_TAG = "MainActivity";
    public static final String yakey = "5619d903-f2b3-4abe-a213-f2c8aa162036";
    public static final String yaUrl = "https://api.weather.yandex.ru/v1/forecast?";//url api погоды lat after lon
    private static final String geoUrl = "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=";//url api погоды

    private boolean isOpenWeatherAct = false;
    private String lastText = "";

    private AutoCompleteTextView etCity;
    private ProgressDialog pg;
    private RadioButton rbKelvin;
    private RadioButton rbCelsius;
    private RadioButton rbFaringeyt;
    private ProgressBar pbLoading;

    private Button btnGetWeather;
    private Button btnUnselect;

    private City SelectedCity;

    public Context context = this;

    private ArrayList<City> FindCities = new ArrayList<>();
    private GetCityes gC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        etCity = findViewById(R.id.etCity);
        rbKelvin = findViewById(R.id.rbTempInKelvin);
        rbCelsius = findViewById(R.id.rbTempInC);
        rbFaringeyt = findViewById(R.id.rbTempInF);
        pbLoading = findViewById(R.id.pbLoading);

        btnGetWeather = findViewById(R.id.btnGetWeather);
        btnGetWeather.setOnClickListener(this);
        btnUnselect = findViewById(R.id.btnUnSelect);
        btnUnselect.setOnClickListener(this);
        if (!isMyServiceRunning(CityUpdater.class)) {
            btnUnselect.setEnabled(false);
        } else {
            btnUnselect.setEnabled(true);
        }


        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!lastText.equals(etCity.getText().toString().trim())) {
                    if (gC != null)
                        gC.cancel(true);
                    gC = new GetCityes(geoUrl, etCity, context, MainActivity.this);
                    gC.execute();
                    lastText = etCity.getText().toString().trim();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        etCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etCity.setCursorVisible(false);
                View v = getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                etCity.dismissDropDown();
            }
        });

        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCity.setCursorVisible(true);
            }
        });

        pg = new ProgressDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isMyServiceRunning(CityUpdater.class)) {
            btnUnselect.setEnabled(false);
        } else {
            btnUnselect.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetWeather: {
                //получение координат города
                String lat = "", lon = "";
                for (City city : FindCities) {
                    if (city.name.equals(etCity.getText().toString())) {
                        lat = city.lat;
                        lon = city.lon;
                        SelectedCity = city;
                        break;
                    }
                }
                GetWeather weather = new GetWeather(pbLoading, btnGetWeather, rbCelsius, rbKelvin, rbFaringeyt, yaUrl, yakey, this);
                weather.execute(lat, lon);
            }
            break;
            case R.id.btnUnSelect: {
                stopService(new Intent(this, CityUpdater.class));
                if (!isMyServiceRunning(CityUpdater.class)) {
                    v.setEnabled(false);
                } else {
                    v.setEnabled(true);
                }
            }
            break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy: ");
        if (isOpenWeatherAct)
            SvgLoader.pluck().close();
    }

    @Override
    public void onComplete(Data result) {
        Log.d(LOG_TAG, "onComplete: " + result.errorType);
        result.fullName = etCity.getText().toString();
        result.lat = SelectedCity.lat;
        result.lon = SelectedCity.lon;
        if (result.err.equals("")) {
            Intent i = new Intent(this, Wheather.class);
            //передача фактических данных о погоде
            i.putExtra("fact", result);

            //передача массива о предстоящей погоде
            Parcelable[] output = new Parcelable[result.forecasts.size()];
            for (int j = result.forecasts.size() - 1; j >= 0; --j) {
                output[j] = result.forecasts.get(j);
            }
            //Log.d(LOG_TAG, "onClick: output.length " + output.length);
            i.putExtra("forecasts", output);
            //запуск активити
            isOpenWeatherAct = true;
            startActivity(i);
        } else {
            //подключение к БД
            DBHelper dbHelper = new DBHelper(this, "Weather", null, DBHelper.DBVersion);
            Data dbFact = dbHelper.getWeatherFromBD(dbHelper.getReadableDatabase(), result.fullName);
            dbHelper.close();
            if (dbFact != null && result.errorType == NoInternet) {
                Intent i = new Intent(this, WeatherFromDB.class);
                i.putExtra("fact", dbFact);
                startActivity(i);
            } else {
                Intent i = new Intent(this, ErrorActivity.class);
                result.err = result.err + "\n Записей в БД не обнаружено";
                i.putExtra("err", result);
                startActivity(i);
            }
        }
    }

    @Override
    public void onComplete(ArrayList<City> result) {
        if (!result.equals(FindCities)) {
            ArrayList<String> citesName = new ArrayList<>();
            for (City city : result) {
                citesName.add(city.name);
            }
            String[] names = new String[result.size()];
            names = citesName.toArray(names);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, names);
            etCity.setAdapter(adapter);
            etCity.showDropDown();
            FindCities = result;
        } else {
            etCity.dismissDropDown();
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
