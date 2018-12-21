package com.example.evgeniy.test.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.example.evgeniy.test.Objects.Data;
import com.example.evgeniy.test.Objects.Day;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.example.evgeniy.test.Enums.ErrorType.*;

public class GetWeather extends AsyncTask<String, Void, Data> {

    public interface Callback {
        void onComplete(Data result);
    }

    private String LOG_TAG = "GetWeather";
    private Callback callback;
    private ProgressBar pbLoading;
    private Button btnGetWeather;
    private RadioButton rbCelsius;
    private RadioButton rbKelvin;
    private RadioButton rbFaringeyt;
    private String yaUrl;
    private String yakey;

    public GetWeather(ProgressBar pbLoading, Button btnGetWeather, RadioButton rbCelsius, RadioButton rbKelvin, RadioButton rbFaringeyt, String yaUrl, String yakey, Callback callback) {
        this.callback = callback;
        this.btnGetWeather = btnGetWeather;
        this.pbLoading = pbLoading;
        this.rbCelsius = rbCelsius;
        this.rbFaringeyt = rbFaringeyt;
        this.rbKelvin = rbKelvin;
        this.yakey = yakey;
        this.yaUrl = yaUrl;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (pbLoading != null)
            pbLoading.setVisibility(View.VISIBLE);  //To show ProgressBar
        if (btnGetWeather != null)
            btnGetWeather.setEnabled(false);
    }

    @Override
    protected Data doInBackground(String... params) {
        Data result = new Data();
        result.err = "";
        StringBuilder yaJSon = new StringBuilder();
        BufferedReader reader = null;
        try {
            URL url = new URL(yaUrl + "lat=" + params[0] + "&" + "lon=" + params[1] + "&extra=true");
            Log.d(LOG_TAG, "doInBackground: ya " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Yandex-API-Key", yakey);
            conn.setDoInput(true);
            conn.setReadTimeout(2000);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String s;
                while ((s = reader.readLine()) != null) {
                    yaJSon.append(s);
                }
            } else {
                Log.e(LOG_TAG, "responseCode ya = " + responseCode);
                result.err = "Запрос вернул код ошибки " + responseCode;
                result.errorType = XyeviyRequest;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Ошибка " + Arrays.toString(e.getStackTrace()));
            result.err = "С вашим Интернет соединением не всё так просто\nПроверьте Интернет соединение";
            result.errorType = NoInternet;
        }
        //Log.d(LOG_TAG, "json: " + yaJSon);
        if (!yaJSon.toString().equals("") & result.err.equals("")) {
            try {
                JSONObject obj = new JSONObject(yaJSon.toString());
                //обработка ТЕКУЩЕЙ(Фактической) погоды
                JSONObject fact = obj.getJSONObject("fact");
                result.City = obj.getJSONObject("info").getJSONObject("tzinfo").getString("name").split("/")[1];
                if (rbCelsius == null || rbKelvin == null || rbFaringeyt == null) {
                    result.temp = String.valueOf(fact.getInt("temp")) + "°C";
                    result.feels_like = String.valueOf(fact.getInt("feels_like")) + "°C";
                } else if (rbCelsius.isChecked()) {
                    result.temp = String.valueOf(fact.getInt("temp")) + "°C";
                    result.feels_like = String.valueOf(fact.getInt("feels_like")) + "°C";
                } else if (rbKelvin.isChecked()) {
                    result.temp = String.valueOf(fact.getInt("temp") - 273) + "°K";
                    result.feels_like = String.valueOf(fact.getInt("feels_like") - 273) + "°K";
                } else if (rbFaringeyt.isChecked()) {
                    result.temp = String.valueOf((int) (fact.getInt("temp") * 1.8 + 32)) + "°F";
                    result.feels_like = String.valueOf((int) (fact.getInt("feels_like") * 1.8 + 32)) + "°F";
                }
                result.iconUrl = "https://yastatic.net/weather/i/icons/blueye/color/svg/" + fact.getString("icon") + ".svg";
                result.wind_speed = String.valueOf(fact.getDouble("wind_speed")) + " м/с";
                result.wind_gust = String.valueOf(fact.getDouble("wind_gust")) + " м/с";
                result.wind_dir = fact.getString("wind_dir");
                result.pressure_mm = String.valueOf(fact.getInt("pressure_mm")) + " мм рт. ст.";
                result.humidity = String.valueOf(fact.getInt("humidity")) + " %";
                result.Cloudnes = String.valueOf(fact.getDouble("cloudness") * 100) + " %";//сразу перевод в проценты
                result.condition = fact.getString("condition");
                result.daytime = fact.getString("daytime");
                //Log.d(LOG_TAG, "yaJson pars: " + Data.toString());

                //обработка ПРОГНОЗА погоды
                ArrayList<Day> forecast = new ArrayList<>();
                JSONArray forecasts = obj.getJSONArray("forecasts");
                for (int i = 0; i < forecasts.length(); i++) {
                    JSONObject JSONday = forecasts.getJSONObject(i);
                    Day dayToSet = new Day();
                    dayToSet.date = JSONday.getString("date");
                    JSONObject partsDay = JSONday.getJSONObject("parts").getJSONObject("day");
                    JSONObject partsNight = JSONday.getJSONObject("parts").getJSONObject("night");
                    dayToSet.iconUrlDay = "https://yastatic.net/weather/i/icons/blueye/color/svg/" + partsDay.getString("icon") + ".svg";
                    dayToSet.iconUrlNight = "https://yastatic.net/weather/i/icons/blueye/color/svg/" + partsNight.getString("icon") + ".svg";
                    if (rbCelsius == null || rbKelvin == null || rbFaringeyt == null) {
                        dayToSet.TempAVGDay = String.valueOf(partsDay.getInt("temp_avg")) + "°C";
                        dayToSet.TempAVGNight = String.valueOf(partsNight.getInt("temp_avg")) + "°C";
                    } else if (rbCelsius.isChecked()) {
                        dayToSet.TempAVGDay = String.valueOf(partsDay.getInt("temp_avg")) + "°C";
                        dayToSet.TempAVGNight = String.valueOf(partsNight.getInt("temp_avg")) + "°C";
                    } else if (rbKelvin.isChecked()) {
                        dayToSet.TempAVGDay = String.valueOf(partsDay.getInt("temp_avg") - 273) + "°K";
                        dayToSet.TempAVGNight = String.valueOf(partsNight.getInt("temp_avg") - 273) + "°K";
                    } else if (rbFaringeyt.isChecked()) {
                        dayToSet.TempAVGDay = String.valueOf((int) (partsDay.getInt("temp_avg") * 1.8 + 32)) + "°F";
                        dayToSet.TempAVGNight = String.valueOf((int) (partsDay.getInt("temp_avg") * 1.8 + 32)) + "°F";
                    }
                    forecast.add(dayToSet);
                    //Log.d(LOG_TAG, "doInBackground: " + dayToSet.toString());
                }
                result.forecasts = forecast;
            } catch (JSONException e) {
                Log.w(LOG_TAG, "JSONException " + Arrays.toString(e.getStackTrace()));
                result.err = "Ошибка обработки ответа от Яндекс погоды ";
                result.errorType = JSONParse;
            }


        }
        //для эффектности стопаем поток на 2 сек.
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Data result) {
        super.onPostExecute(result);
        if (pbLoading != null)
            pbLoading.setVisibility(View.GONE);     // To Hide ProgressBar
        if (btnGetWeather != null)
            btnGetWeather.setEnabled(true);
        callback.onComplete(result);

    }
}
