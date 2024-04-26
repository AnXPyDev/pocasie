package com.example.pocasie.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import orm.Database;
import orm.Model;
import orm.ModelFactory;

public class WeatherDatabase extends Database {

    public WeatherDatabase(Context context) {
        super(context, "weather", 8);
    }

    @Override
    protected void create(SQLiteDatabase db) {
        db.execSQL("""
            CREATE TABLE location (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            )
        """);

        db.execSQL("""
            CREATE TABLE datapoint (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                location_id INTEGER NOT NULL,
                date DATE NOT NULL,
                time TIME NOT NULL,
                temperature INTEGER NOT NULL,
                weather INTEGER NOT NULL,
                
                FOREIGN KEY (location_id) REFERENCES location(id)
            );
        """);

        Seed.seed(db);
    }

    @Override
    protected void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS datapoint");
        db.execSQL("DROP TABLE IF EXISTS location");
    }

    protected static ArrayList<Model> extract(Cursor cursor, ModelFactory factory) {
        ArrayList<Model> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Model instance = factory.create();
            instance.pull(cursor);
            list.add(instance);
        }
        return list;
    }

    public ArrayList<Location> fetchLocations() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM location", null);
        ArrayList<Location> result = (ArrayList)extract(cursor, new Location.Factory());
        cursor.close();
        db.close();
        return result;
    };

    public ArrayList<Datapoint> fetchDatapoints(Location location, LocalDate date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM datapoint WHERE location_id=? AND date=?",
                new String[] { location.getID().toString(), date.format(dateFormat) }
        );

        ArrayList<Datapoint> result = (ArrayList)extract(cursor, new Datapoint.Factory());
        cursor.close();
        db.close();
        return result;
    }
}
