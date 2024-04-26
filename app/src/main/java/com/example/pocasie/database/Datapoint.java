package com.example.pocasie.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.DrawableRes;

import com.example.pocasie.R;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;

import orm.Database;
import orm.Model;
import orm.ModelFactory;

public class Datapoint implements Model {

    public static final String[] weather_const = {
            "clear", "cloudy", "rain", "heavy rain", "drizzle", "thunderstorm", "snow", "blizzard",
            "fog", "wind"
    };

    public static final @DrawableRes int[] weahter_icon = {
            R.drawable.weather_clear,
            R.drawable.weather_cloudy,
            R.drawable.weather_rain,
            R.drawable.weather_heavy_rain,
            R.drawable.weather_drizzle,
            R.drawable.weather_thunderstorm,
            R.drawable.weather_snow,
            R.drawable.weather_blizzard,
            R.drawable.weather_fog,
            R.drawable.weather_wind
    };

    public static final int temp_min = -10000;
    public static final int temp_max = 10000;

    protected Integer id;
    public Integer location_id;
    public LocalDate date;
    public LocalTime time;
    public Integer temperature;
    public Integer weather;

    @Override
    public Integer getID() {
        return id;
    }

    @Override
    public String getTableName() {
        return "datapoint";
    }

    @Override
    public void setID(Integer id) {
        this.id = id;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("location_id", location_id);
        values.put("date", WeatherDatabase.dateFormat.format(date));
        values.put("time", WeatherDatabase.timeFormat.format(time));
        values.put("temperature", temperature);
        values.put("weather", weather);
        return values;
    }

    @Override
    public void pull(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        location_id = cursor.getInt(cursor.getColumnIndexOrThrow("location_id"));
        temperature = cursor.getInt(cursor.getColumnIndexOrThrow("temperature"));
        weather = cursor.getInt(cursor.getColumnIndexOrThrow("weather"));

        String date_string = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String time_string = cursor.getString(cursor.getColumnIndexOrThrow("time"));

        try {
            date = LocalDate.parse(date_string, Database.dateFormat);
        } catch (DateTimeException e) {
            date = LocalDate.now();
            Log.println(Log.ERROR, "DBDEBUG", e.getMessage());
        }

        try {
            time = LocalTime.parse(time_string, Database.timeFormat);
        } catch (DateTimeException e) {
            time = LocalTime.now();
            Log.println(Log.ERROR, "DBDEBUG", e.getMessage());
        }
    }

    public Datapoint copy() {
        Datapoint other = new Datapoint();
        other.location_id = location_id;
        other.date = date;
        other.time = time;
        other.temperature = temperature;
        other.weather = weather;
        return other;
    }

    public static class Factory extends ModelFactory {
        @Override
        public Model create() { return new Datapoint(); }
    }
}
