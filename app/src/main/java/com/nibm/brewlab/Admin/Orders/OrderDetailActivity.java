package com.nibm.brewlab.Admin.Orders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.brewlab.R;

public class OrderDetailActivity extends AppCompatActivity {

    TextView txtOrderId, txtCustomer, txtTotal, txtStatus;
    Button btnAssign;

    String orderId, customer, total, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtOrderId = findViewById(R.id.txtOrderId);
        txtCustomer = findViewById(R.id.txtCustomer);
        txtTotal = findViewById(R.id.txtTotal);
        txtStatus = findViewById(R.id.txtStatus);
        btnAssign = findViewById(R.id.btnAssign);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        customer = intent.getStringExtra("customer");
        total = intent.getStringExtra("total");
        status = intent.getStringExtra("status");

        txtOrderId.setText("Order ID: " + orderId);
        txtCustomer.setText("Customer: " + customer);
        txtTotal.setText("Total: Rs " + total);
        txtStatus.setText("Status: " + status);

    }
}