package com.example.pocasie;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pocasie.database.Location;
import com.example.pocasie.databinding.EditorLocationBinding;

public class LocationEditor extends Dialog {

    enum Mode {
        CREATE, EDIT
    }

    EditorLocationBinding binding;

    Location location;

    Handler handler;
    Mode mode;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditorLocationBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        binding.confirm.setOnClickListener((view) -> tryConfirm());
        binding.cancel.setOnClickListener((view) -> cancel());
        binding.delete.setOnClickListener((view) -> delete());

        switch (mode) {
            case CREATE -> {
                binding.title.setText("Create Location");
                binding.deleteContainer.setVisibility(View.GONE);
            }
            case EDIT -> binding.title.setText("Edit Location");
        }

        display();
    }

    protected void display() {
        if (location.name != null) {
            binding.name.setText(location.name);
        }
    }

    protected void delete() {
        handler.delete();
        dismiss();
    }

    protected void confirm() throws Exception {
        String location_name = binding.name.getText().toString();
        if (location_name.isEmpty()) {
            throw new Exception("Name is empty");
        }

        if (location == null) {
            location = new Location();
        }

        location.name = location_name;
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

    public LocationEditor(@NonNull Context context, Location location, Mode mode, Handler handler) {
        super(context);
        this.location = location;
        this.handler = handler;
        this.mode = mode;
    }
}
