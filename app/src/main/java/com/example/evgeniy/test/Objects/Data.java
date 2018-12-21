package com.example.evgeniy.test.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.evgeniy.test.Enums.ErrorType;

import java.util.ArrayList;

public class Data implements Parcelable {
    public String err;
    public ErrorType errorType;
    public String City;
    public String fullName;
    public String temp;
    public String feels_like;
    public String iconUrl;
    public String wind_speed;
    public String wind_gust;
    public String wind_dir;
    public String pressure_mm;
    public String humidity;
    public String Cloudnes;
    public String condition;
    public String daytime;
    public String lat;
    public String lon;

    public ArrayList<Day> forecasts;

    public Data() {
        err = City = fullName = temp = feels_like = iconUrl = wind_speed = wind_gust = wind_dir = pressure_mm = humidity = condition = daytime = "";
        forecasts = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "err " + err + " City " + City + " temp " + temp + " feels like " + feels_like +
                " icon " + iconUrl + " wind speed " + wind_speed + " pressure_mm " + pressure_mm + " humidity " + humidity + " condition " + condition + " daytime " + daytime;
    }

    private Data(Parcel in) {
        err = in.readString();
        City = in.readString();
        fullName = in.readString();
        temp = in.readString();
        feels_like = in.readString();
        iconUrl = in.readString();
        wind_speed = in.readString();
        wind_gust = in.readString();
        wind_dir = in.readString();
        pressure_mm = in.readString();
        humidity = in.readString();
        Cloudnes = in.readString();
        condition = in.readString();
        daytime = in.readString();
        lat = in.readString();
        lon = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(err);
        dest.writeString(City);
        dest.writeString(fullName);
        dest.writeString(temp);
        dest.writeString(feels_like);
        dest.writeString(iconUrl);
        dest.writeString(wind_speed);
        dest.writeString(wind_gust);
        dest.writeString(wind_dir);
        dest.writeString(pressure_mm);
        dest.writeString(humidity);
        dest.writeString(Cloudnes);
        dest.writeString(condition);
        dest.writeString(daytime);
        dest.writeString(lat);
        dest.writeString(lon);

    }

    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {

        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
