package com.example.evgeniy.test.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Day implements Parcelable {
    public String date;
    public String TempAVGDay;
    public String TempAVGNight;
    public String iconUrlDay;
    public String iconUrlNight;

    @Override
    public String toString() {
        return "date " + date + " AVGDay " + TempAVGDay + " AVGNight " + TempAVGNight + " iconUrlDay " + iconUrlDay + " iconUrlNight " + iconUrlNight;
    }

    public Day() {
        date = "";
        TempAVGDay = "";
        TempAVGNight = "";
        iconUrlDay = "";
        iconUrlNight = "";
    }

    private Day(Parcel in) {
        date = in.readString();
        TempAVGDay = in.readString();
        TempAVGNight = in.readString();
        iconUrlDay = in.readString();
        iconUrlNight = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(TempAVGDay);
        dest.writeString(TempAVGNight);
        dest.writeString(iconUrlDay);
        dest.writeString(iconUrlNight);
    }

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {

        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
