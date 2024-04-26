package com.example.pocasie;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.example.pocasie.database.Datapoint;
import com.example.pocasie.databinding.DatapointBinding;

import java.time.format.DateTimeFormatter;

import lib.AdvancedAdapter;
import lib.ViewBindingHolder;
import orm.Database;

public class DatapointViewHolder extends ViewBindingHolder<Datapoint> {

    public static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    Factory factory;

    DatapointBinding binding;
    Datapoint datapoint;

    public DatapointViewHolder(Factory factory, ViewBinding _binding) {
        super(_binding);
        binding = (DatapointBinding)_binding;
        this.factory = factory;

        binding.getRoot().setOnLongClickListener((view) -> {
            factory.manager.longPress(this.datapoint);
            return true;
        });
    }

    @Override
    protected void bind(Datapoint datapoint) {
        this.datapoint = datapoint;
        binding.time.setText(datapoint.time.format(timeFormat));
        binding.temperature.setText(String.format("%.2f°C", (double)datapoint.temperature / 100.d));
        binding.weather.setText(Datapoint.weather_const[datapoint.weather]);
        binding.weatherIcon.setImageResource(Datapoint.weahter_icon[datapoint.weather]);
    }

    public static class Factory extends ViewBindingHolder.Factory {

        DatapointManager manager;

        public Factory(DatapointManager manager) {
            this.manager = manager;
        }

        @Override
        protected ViewBinding inflate(LayoutInflater inflater, ViewGroup parent) {
            return DatapointBinding.inflate(inflater, parent, false);
        }

        @Override
        protected ViewBindingHolder create(ViewBinding binding) {
            return new DatapointViewHolder(this, binding);
        }
    }
}
