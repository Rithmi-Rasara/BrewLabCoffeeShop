package com.nibm.brewlab.Admin.Customers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.brewlab.R;

public class CustomerDetailsActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone;
    Button btnApprove, btnReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        tvName.setText(getIntent().getStringExtra("name"));
        tvEmail.setText(getIntent().getStringExtra("email"));
        tvPhone.setText(getIntent().getStringExtra("phone"));

        btnApprove.setOnClickListener(v ->
                Toast.makeText(this, "Customer Approved", Toast.LENGTH_SHORT).show());

        btnReject.setOnClickListener(v ->
                Toast.makeText(this, "Customer Rejected", Toast.LENGTH_SHORT).show());
    }
}