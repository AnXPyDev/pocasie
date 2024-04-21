package com.example.pocasie;

import android.provider.ContactsContract;
import android.util.Log;

import com.example.pocasie.database.Datapoint;
import com.example.pocasie.database.Location;
import com.example.pocasie.database.WeatherDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatapointStore {


    WeatherDatabase db;
    Location location;

    ConcurrentHashMap<LocalDate, ArrayList<Datapoint>> cache = new ConcurrentHashMap<>();
    Executor executor = Executors.newSingleThreadExecutor();


    public DatapointStore(WeatherDatabase db, Location location) {
        this.db = db;
        this.location = location;
    }

    public static void sortDatapoints(ArrayList<Datapoint> datapoints) {
        datapoints.sort(Comparator.comparing(datapoint -> datapoint.time));
    }

    ArrayList<Datapoint> getCached(LocalDate date) {
        return cache.get(date);
    }

    CompletableFuture<ArrayList<Datapoint>> fetch(LocalDate date) {
        CompletableFuture<ArrayList<Datapoint>> result = new CompletableFuture<>();

        executor.execute(() -> {
            ArrayList<Datapoint> data = db.fetchDatapoints(location, date);
            sortDatapoints(data);
            cache.put(date, data);
            result.complete(data);
        });

        return result;
    }


    public Integer addDatapoint(Datapoint datapoint) {
        db.push(datapoint);

        ArrayList<Datapoint> cached = getCached(datapoint.date);
        if (cached != null) {
            cached.add(datapoint);
            sortDatapoints(cached);
            return cached.indexOf(datapoint);
        }
        return null;
    }

    public Integer editDatapoint(Datapoint datapoint) {
        db.push(datapoint);

        ArrayList<Datapoint> cached = getCached(datapoint.date);

        if (cached != null) {
            if (!cached.contains(datapoint)) {
                return null;
            }
            int pos = cached.indexOf(datapoint);
            sortDatapoints(cached);
            return pos;
        }

        return null;
    }

    public Integer removeDatapoint(Datapoint datapoint) {
        db.delete(datapoint);

        ArrayList<Datapoint> cached = getCached(datapoint.date);

        if (cached != null) {
            if (!cached.contains(datapoint)) {
                return null;
            }
            int pos = cached.indexOf(datapoint);
            cached.remove(datapoint);
            return pos;
        }

        return null;
    }

    public Integer removeOldDatapoint(Datapoint old_datapoint, Datapoint datapoint) {
        ArrayList<Datapoint> cached = getCached(old_datapoint.date);

        if (cached != null) {
            if (!cached.contains(datapoint)) {
                return null;
            }
            int pos = cached.indexOf(datapoint);
            cached.remove(datapoint);
            return pos;
        }

        return null;
    }

}
