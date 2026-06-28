package com.nibm.brewlab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStartedActivity extends AppCompatActivity {

    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        getSupportActionBar().hide();

        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {

            Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);

            startActivity(intent);

        });

    }
}