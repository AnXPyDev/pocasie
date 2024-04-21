package com.example.pocasie;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.example.pocasie.database.Location;
import com.example.pocasie.databinding.LocationBinding;

import lib.ViewBindingHolder;

public class LocationViewHolder extends ViewBindingHolder<Location> {
    LocationBinding binding;

    LocationManager manager;
    Location location;

    @Override
    protected void bind(Location location) {
        this.location = location;
        binding.name.setText(location.name);
    }

    public LocationViewHolder(ViewBinding _binding, LocationManager manager) {
        super(_binding);
        this.binding = (LocationBinding) _binding;
        this.manager = manager;
        binding.getRoot().setOnClickListener((view) -> {
            manager.open(location);
        });

        binding.getRoot().setOnLongClickListener((view) -> {
            manager.longPress(location);
            return true;
        });
    }

    public static class Factory extends ViewBindingHolder.Factory {
        LocationManager manager;

        @Override
        protected ViewBinding inflate(LayoutInflater inflater, ViewGroup parent) {
            return LocationBinding.inflate(inflater, parent, false);
        }
        @Override
        protected ViewBindingHolder create(ViewBinding binding) {
            return new LocationViewHolder(binding, manager);
        }

        public Factory(LocationManager manager) {
            this.manager = manager;
        }
    }
}
