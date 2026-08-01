package com.nibm.brewlab.Admin.Delivery;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.brewlab.R;

public class DeliveryDetailsActivity extends AppCompatActivity {

    TextView tvName, tvVehicle, tvPhone;
    Button btnApprove, btnReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvName = findViewById(R.id.tvName);
        tvVehicle = findViewById(R.id.tvVehicle);
        tvPhone = findViewById(R.id.tvPhone);

        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        tvName.setText(getIntent().getStringExtra("name"));
        tvVehicle.setText(getIntent().getStringExtra("vehicleNumber"));
        tvPhone.setText(getIntent().getStringExtra("phone"));

        btnApprove.setOnClickListener(v ->
                Toast.makeText(this, "Delivery Person Approved", Toast.LENGTH_SHORT).show());

        btnReject.setOnClickListener(v ->
                Toast.makeText(this, "Delivery Person Removed", Toast.LENGTH_SHORT).show());
    }
}
