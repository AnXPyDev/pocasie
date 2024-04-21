package com.example.pocasie;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.RenderScript;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pocasie.database.Location;
import com.example.pocasie.database.WeatherDatabase;
import com.example.pocasie.databinding.ActivityMainBinding;

import java.util.ArrayList;

import lib.AdvancedAdapter;

public class MainActivity extends AppCompatActivity implements LocationManager {
    ActivityMainBinding binding;

    AdvancedAdapter<Location> locations_adapter;
    WeatherDatabase db;
    ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new WeatherDatabase(this);
        locations = db.fetchLocations();

        locations_adapter = new AdvancedAdapter<>(locations, new LocationViewHolder.Factory(this));
        binding.locations.setAdapter(locations_adapter);
        binding.locations.setLayoutManager(new LinearLayoutManager(this));
        binding.add.setOnClickListener((view) -> addLocation());
    }

    protected void addLocation() {
        Location location = new Location();
        LocationEditor editor = new LocationEditor(this, location, LocationEditor.Mode.CREATE, new LocationEditor.Handler() {
            @Override
            public void handle() {
                db.push(location);
                locations.add(location);
                locations_adapter.notifyItemInserted(locations.size() - 1);
            }

            @Override
            public void delete() {}
        });

        editor.show();
    }

    @Override
    public void open(Location location) {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("location_id", location.getID());
        startActivity(intent);
    }

    @Override
    public void longPress(Location location) {
        LocationEditor editor = new LocationEditor(this, location, LocationEditor.Mode.EDIT, new LocationEditor.Handler() {
            @Override
            public void handle() {
                db.push(location);
                locations_adapter.notifyItemChanged(locations.indexOf(location));
            }

            @Override
            public void delete() {
                db.delete(location);
                int pos = locations.indexOf(location);
                locations.remove(location);
                locations_adapter.notifyItemRemoved(pos);
            }
        });

        editor.show();
    }
}