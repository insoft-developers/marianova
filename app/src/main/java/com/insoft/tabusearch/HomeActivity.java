package com.insoft.tabusearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {
    private CardView cv_masuk, cv_petunjuk, cv_tentang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        cv_masuk = findViewById(R.id.cv_masuk);
        cv_petunjuk = findViewById(R.id.cv_petunjuk);
        cv_tentang = findViewById(R.id.cv_tentang);

        cv_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PathActivity.class);
                startActivity(intent);
            }
        });
    }
}