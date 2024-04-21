package com.example.pocasie;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.example.pocasie.database.Datapoint;
import com.example.pocasie.databinding.DateBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import lib.AdvancedAdapter;
import lib.InfiniteAdapter;
import lib.ViewBindingHolder;

public class DateViewHolder extends ViewBindingHolder<Integer> {

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd. MM. YYYY");

    DateBinding binding;

    LocalDate date;

    Factory factory;

    ArrayList<Datapoint> datapoints;

    AdvancedAdapter<Datapoint> datapoints_adapter;

    public DateViewHolder(ViewBinding _binding, Factory factory) {
        super(_binding);
        this.factory = factory;
        binding = (DateBinding)_binding;
        datapoints_adapter = new AdvancedAdapter<>(new ArrayList<>(), new DatapointViewHolder.Factory(factory.context));
        binding.datapoints.setAdapter(datapoints_adapter);
        binding.datapoints.setLayoutManager(new LinearLayoutManager(null));
    }

    public void refresh() {
        datapoints_adapter.notifyDataSetChanged();
    }


    CompletableFuture<ArrayList<Datapoint>> future_result;

    @Override
    protected void bind(Integer position) {
        date = factory.date_now.plusDays(position - InfiniteAdapter.MIDDLE_POSITION);

        binding.date.setText(date.format(dateFormat));

        if (future_result != null) {
            future_result.cancel(false);
        }

        datapoints = factory.getter.getCached(date);
        if (datapoints != null) {
            bindData();
            return;
        }

        binding.datapoints.setVisibility(View.INVISIBLE);

        future_result = factory.getter.fetch(date);
        future_result.whenComplete((data, error) -> {
            if (error != null) {
                Log.e("Getter error", error.getMessage(), error);
            }

            datapoints = data;
            factory.context.runOnUiThread(() -> {
                bindData();
            });
        });


    }

    protected void bindData() {
        datapoints_adapter.setSource(datapoints);
        binding.datapoints.setVisibility(View.VISIBLE);
    }

    public static class Factory extends ViewBindingHolder.Factory {

        @Override
        protected ViewBinding inflate(LayoutInflater inflater, ViewGroup parent) {
            return DateBinding.inflate(inflater, parent, false);
        }

        LocationActivity context;
        LocalDate date_now;
        DatapointStore getter;

        @Override
        protected ViewBindingHolder create(ViewBinding binding) {
            return new DateViewHolder(binding, this);
        }

        public Factory(LocationActivity context, LocalDate date_now, DatapointStore getter) {
            this.context = context;
            this.date_now = date_now;
            this.getter = getter;
        }
    }


}
