package com.insoft.tabusearch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

public class PathActivity extends AppCompatActivity {
    private Button btncari;
    private TextView etuser, ettujuan;
    private CarmenFeature home;
    private CarmenFeature work;
    private LatLng userlatlng;
    private int id_tujuan;
    private boolean is_algo_tabu = false;
    private boolean is_algo_sahc = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        btncari = findViewById(R.id.btncari);
        etuser = findViewById(R.id.etuser);
        ettujuan = findViewById(R.id.ettujuan);

        btncari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PathActivity.this, OptimizationActivity.class);
                intent.putExtra("userlat", String.valueOf(userlatlng.getLatitude()));
                intent.putExtra("userlong", String.valueOf(userlatlng.getLongitude()));
                intent.putExtra("id_tujuan", id_tujuan);
                startActivity(intent);
            }
        });

        etuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .country("ID")
                                .build(PlaceOptions.MODE_CARDS))
                        .build(PathActivity.this);

                startActivityForResult(intent, 123);
            }
        });

        ettujuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PathActivity.this, WisataActivity.class);
                startActivityForResult(intent, 196);
            }
        });



    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_ninjas:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 123) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            etuser.setText(feature.placeName());
            userlatlng = new LatLng(((Point) feature.geometry()).latitude(),
                    ((Point) feature.geometry()).longitude());
//            Toast.makeText(this, userlatlng.getLatitude()+","+userlatlng.getLongitude(), Toast.LENGTH_LONG).show();
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == 196) {
            String nama_wisata = data.getStringExtra("nama_wisata");
            ettujuan.setText(nama_wisata);
            id_tujuan = data.getIntExtra("id_wisata", 0);
        }
    }
}