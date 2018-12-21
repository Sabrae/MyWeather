package com.example.evgeniy.test.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.evgeniy.test.Objects.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class GetCityes extends AsyncTask<Void, Void, ArrayList<City>> {

    public interface Callback {
        void onComplete(ArrayList<City> result);
    }

    private String LOG_TAG = "GetCityes";

    private String geoUrl;
    private AutoCompleteTextView etCity;
    private Context context;

    private Callback callback;

    public GetCityes(String geoUrl, AutoCompleteTextView etCity, Context context, Callback callback)
    {
        this.callback = callback;
        this.context = context;
        this.etCity = etCity;
        this.geoUrl = geoUrl;
    }

    @Override
    protected ArrayList<City> doInBackground(Void... voids) {
        StringBuilder geoJSon = new StringBuilder();
        BufferedReader reader = null;
        try {
            URL url = new URL(geoUrl + etCity.getText().toString() + "&kind=locality");
            //Log.d(LOG_TAG, "doInBackground: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String s;
                while ((s = reader.readLine()) != null) {
                    geoJSon.append(s);
                }
            } else {
                Log.e(LOG_TAG, "responseCode OW = " + responseCode);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Ошибка " + Arrays.toString(e.getStackTrace()));
        }
        if (!geoJSon.toString().equals("")) {
            ArrayList<City> cities = new ArrayList<>();
            String lat = "", lon = "", point = "";
            try {
                JSONArray featureMember = new JSONObject(geoJSon.toString()).getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember");
                for (int i = 0; i < featureMember.length(); i++) {
                    City city = new City();
                    JSONObject GeoObject = featureMember.getJSONObject(i).getJSONObject("GeoObject");
                    //Log.d(LOG_TAG, "GeoObject: " + GeoObject.toString());
                    point = GeoObject.getJSONObject("Point").getString("pos");
                    lon = point.split(" ")[0];
                    lat = point.split(" ")[1];
                    city.name = GeoObject.getString("name") + ", " + GeoObject.getString("description");
                    city.lat = lat;
                    city.lon = lon;
                    cities.add(city);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException " + e.getMessage());
                Log.d(LOG_TAG, "Json: " + geoJSon);
            }
            return cities;
        } else
            return null;
    }

    @Override
    protected void onPostExecute(ArrayList<City> result) {
        super.onPostExecute(result);
        if (result != null) {
            callback.onComplete(result);
        }
    }
}
