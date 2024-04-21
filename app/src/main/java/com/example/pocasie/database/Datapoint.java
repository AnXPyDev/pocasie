package com.example.pocasie.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import orm.Database;
import orm.Model;
import orm.ModelFactory;

public class Datapoint implements Model {

    public static final String[] weather_const = {
            "clear", "cloudy", "rain", "heavy rain", "drizzle", "thunderstorm", "fog", "snow", "heavy snow"
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
