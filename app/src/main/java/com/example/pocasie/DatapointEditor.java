package com.example.pocasie;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pocasie.database.Datapoint;
import com.example.pocasie.databinding.EditorDatapointBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class DatapointEditor extends Dialog {

    enum Mode {
        CREATE, EDIT
    }

    EditorDatapointBinding binding;

    Handler handler;
    Mode mode;
    Datapoint datapoint;

    Toast toast;


    LocalDate date;
    LocalTime time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditorDatapointBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());


        binding.confirm.setOnClickListener((view) -> tryConfirm());
        binding.cancel.setOnClickListener((view) -> cancel());
        binding.selectDate.setOnClickListener((view) -> selectDate());
        binding.selectTime.setOnClickListener((view) -> selectTime());
        binding.delete.setOnClickListener((view) -> delete());

        String[] weather_options = Arrays.copyOf(Datapoint.weather_const, Datapoint.weather_const.length + 1);
        weather_options[weather_options.length - 1] = "Weather";

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getContext(), R.layout.weather_spinner_item, weather_options) {
            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };

        binding.weather.setAdapter(spinner_adapter);
        binding.weather.setSelection(weather_options.length - 1);

        switch (mode) {
            case CREATE -> {
                binding.title.setText("Create Datapoint");
                binding.deleteContainer.setVisibility(View.GONE);
            }
            case EDIT -> binding.title.setText("Edit Datapoint");
        }

        display();
    }

    protected void display() {
        if (datapoint.date != null) {
            setDate(datapoint.date);
        }
        if (datapoint.time != null) {
            setTime(datapoint.time);
        }
        if (datapoint.temperature != null) {
            binding.temperature.setText(String.format("%.2f", (double)datapoint.temperature / 100.d));
        }
        if (datapoint.weather != null) {
            binding.weather.setSelection(datapoint.weather);
        }
    }

    protected void delete() {
        handler.delete();
        dismiss();
    }

    protected void setDate(LocalDate date) {
        this.date = date;
        binding.date.setText(date.format(DateViewHolder.dateFormat));
    }

    protected void setTime(LocalTime time) {
        this.time = time;
        binding.time.setText(time.format(DatapointViewHolder.timeFormat));
    }

    protected void selectDate() {
        DatePickerDialog picker = new DatePickerDialog(getContext());

        if (date != null) {
            picker.getDatePicker().updateDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        }

        picker.show();

        picker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setDate(LocalDate.of(year, month + 1, dayOfMonth));
            }
        });
    }

    protected void selectTime() {

        int hour = 12;
        int minute = 0;

        if (time != null) {
            hour = time.getHour();
            minute = time.getMinute();
        }

        TimePickerDialog picker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(LocalTime.of(hourOfDay, minute));
            }
        }, hour, minute, true);


        picker.show();

    }

    protected void confirm() throws Exception {
        if (date == null) {
            throw new Exception("Select date");
        }

        if (time == null) {
            throw new Exception("Select time");
        }

        String str_temp = binding.temperature.getText().toString();
        Double temp;
        try {
            temp = Double.valueOf(str_temp);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid temperature format");
        }

        int itemp = (int)(temp.doubleValue() * 100.d);

        if (itemp < Datapoint.temp_min || itemp > Datapoint.temp_max) {
            throw new Exception("Temperature too low/high");
        }

        int weather = binding.weather.getSelectedItemPosition();
        if (weather >= Datapoint.weather_const.length) {
            throw new Exception("Select weather");
        }

        datapoint.temperature = itemp;
        datapoint.weather = weather;
        datapoint.date = date;
        datapoint.time = time;

        this.handler.handle();
        this.dismiss();
    }

    protected void tryConfirm() {
        try {
            confirm();
        } catch (Exception e) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public interface Handler {
        void handle();
        void delete();
    }

    public DatapointEditor(@NonNull Context context, Mode mode, Datapoint datapoint, Handler handler) {
        super(context);
        this.mode = mode;
        this.datapoint = datapoint;
        this.handler = handler;
    }
}
