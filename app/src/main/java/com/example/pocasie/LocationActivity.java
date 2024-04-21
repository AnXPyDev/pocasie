package com.example.pocasie;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.example.pocasie.database.Datapoint;
import com.example.pocasie.database.Location;
import com.example.pocasie.database.WeatherDatabase;
import com.example.pocasie.databinding.ActivityLocationBinding;

import java.time.LocalDate;

import lib.InfiniteAdapter;

public class LocationActivity extends AppCompatActivity implements DatapointManager{

    ActivityLocationBinding binding;

    WeatherDatabase db;

    Location location;

    InfiniteAdapter dates_adapter;
    LinearLayoutManager layoutManager;

    LocalDate date_now = LocalDate.now();
    DatapointStore datapoint_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        db = new WeatherDatabase(this);

        location = new Location();

        int location_id = intent.getIntExtra("location_id", -1);

        if (location_id == -1) {
            finish();
            return;
        }

        location.setID(location_id);
        db.pull(location);

        datapoint_store = new DatapointStore(db, location);

        binding.name.setText(location.name);

        dates_adapter = new InfiniteAdapter(new DateViewHolder.Factory(this, date_now, datapoint_store));
        binding.dates.setAdapter(dates_adapter);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        binding.dates.setLayoutManager(layoutManager);

        SnapHelper snap = new PagerSnapHelper();
        snap.attachToRecyclerView(binding.dates);

        selectDate(date_now);

        binding.selectDate.setOnClickListener((view) -> selectDate());
        binding.add.setOnClickListener((view) -> createDatapoint());
    }


    protected void selectDate(LocalDate date) {
        int diff = (int)(date.toEpochDay() - date_now.toEpochDay());
        binding.dates.scrollToPosition(InfiniteAdapter.MIDDLE_POSITION + diff);
    }

    public LocalDate getSelectedDate() {
        return date_now.plusDays(layoutManager.findFirstCompletelyVisibleItemPosition() - InfiniteAdapter.MIDDLE_POSITION);
    }


    protected void selectDate() {
        LocalDate selectedDate = getSelectedDate();

        DatePickerDialog picker = new DatePickerDialog(this);
        picker.show();
        picker.getDatePicker().updateDate(selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());

        picker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectDate(LocalDate.of(year, month + 1, dayOfMonth));
            }
        });
    }

    protected DateViewHolder getHolder(Datapoint datapoint) {
        int pos = InfiniteAdapter.MIDDLE_POSITION + (int)(datapoint.date.toEpochDay() - date_now.toEpochDay());

        return (DateViewHolder)dates_adapter.getHolderAt(pos);
    }

    protected void updateDatapoint(Datapoint old_datapoint, Datapoint datapoint) {
        if (!old_datapoint.date.equals(datapoint.date)) {
            removeOldDatapoint(old_datapoint, datapoint);
            addDatapoint(datapoint);
            return;
        }

        datapoint_store.editDatapoint(datapoint);
        DateViewHolder holder = getHolder(datapoint);
        if (holder == null) {
            return;
        }
        holder.refresh();
    }

    protected void addDatapoint(Datapoint datapoint) {
        datapoint_store.addDatapoint(datapoint);
        DateViewHolder holder = getHolder(datapoint);
        Log.i("IDK" , "Add to Holder " + String.valueOf(holder));
        if (holder == null) {
            return;
        }
        holder.refresh();
    }

    protected void removeOldDatapoint(Datapoint old_datapoint, Datapoint datapoint) {
        Integer x = datapoint_store.removeOldDatapoint(old_datapoint, datapoint);
        DateViewHolder holder = getHolder(old_datapoint);
        if (holder == null) {
            return;
        }
        holder.refresh();
    }

    protected void removeDatapoint(Datapoint datapoint) {
        Integer x = datapoint_store.removeDatapoint(datapoint);
        DateViewHolder holder = getHolder(datapoint);
        if (holder == null) {
            return;
        }
        holder.refresh();
    }

    @Override
    public void longPress(Datapoint datapoint) {
        Datapoint old_datapoint = datapoint.copy();
        DatapointEditor editor = new DatapointEditor(this, DatapointEditor.Mode.EDIT, datapoint, new DatapointEditor.Handler() {
            @Override
            public void handle() {
                updateDatapoint(old_datapoint, datapoint);
            }

            @Override
            public void delete() {
                removeDatapoint(datapoint);

            }
        });
        editor.show();
    }

    public void createDatapoint() {
        Datapoint datapoint = new Datapoint();
        datapoint.location_id = location.getID();
        datapoint.date = getSelectedDate();
        DatapointEditor editor = new DatapointEditor(this, DatapointEditor.Mode.CREATE, datapoint, new DatapointEditor.Handler() {
            @Override
            public void handle() {
                addDatapoint(datapoint);
            }

            @Override
            public void delete() {

            }
        });
        editor.show();
    }
}