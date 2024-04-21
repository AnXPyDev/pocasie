package com.example.pocasie.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import orm.Model;
import orm.ModelFactory;

public class Location implements Model {
    public static class Factory extends ModelFactory {
        @Override
        public Model create() { return new Location(); }
    }

    public Location() {}

    protected Integer id;
    public String name;

    @Override
    public Integer getID() { return id; }
    @Override
    public void setID(Integer id) { this.id = id; }
    @Override
    public String getTableName() { return "location"; }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("name", name);
        return values;
    }

    @Override
    public void pull(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
    }

    public static ArrayList<Location> fetch(SQLiteDatabase db) {
        ArrayList<Location> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM location", new String[0]);

        while (cursor.moveToNext()) {
            Location location = new Location();
            location.pull(cursor);
            list.add(location);
        }

        cursor.close();

        return list;
    }

    public static ArrayList<Location> fetch(WeatherDatabase db) {
        SQLiteDatabase sdb = db.getReadableDatabase();
        ArrayList<Location> result = fetch(sdb);
        sdb.close();
        return result;
    }
}

