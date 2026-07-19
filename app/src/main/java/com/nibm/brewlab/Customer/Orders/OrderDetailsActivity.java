package com.nibm.brewlab.Customer.Orders;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.R;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView txtItems, txtTotal, txtPayment, txtAddress, txtDate, txtStatus, txtTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtItems = findViewById(R.id.txtDetailItems);
        txtTotal = findViewById(R.id.txtDetailTotal);
        txtPayment = findViewById(R.id.txtDetailPayment);
        txtAddress = findViewById(R.id.txtDetailAddress);
        txtDate = findViewById(R.id.txtDetailDate);
        txtStatus = findViewById(R.id.txtDetailStatus);
        txtTracking = findViewById(R.id.txtDetailTracking);

        String orderId = getIntent().getStringExtra("orderId");

        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseDatabase.getInstance().getReference("Orders").child(orderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        CustomerOrder order = snapshot.getValue(CustomerOrder.class);

                        if (order == null) {
                            Toast.makeText(OrderDetailsActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        txtItems.setText(order.getItemsSummary());
                        txtTotal.setText("Rs. " + order.getTotalAmount());
                        txtPayment.setText(order.getPaymentMethod());
                        txtAddress.setText(order.getDeliveryAddress());
                        txtDate.setText(DateFormat.format("dd MMM yyyy, hh:mm a", order.getTimestamp()));
                        txtStatus.setText(order.getStatus());
                        txtTracking.setText(buildTrackingText(order.getStatus()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrderDetailsActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String buildTrackingText(String status) {

        if (status == null) status = "Pending";

        switch (status) {
            case "Pending":
                return "Order received. Waiting for the shop to start preparing.";
            case "Preparing":
                return "Your coffee is being brewed right now.";
            case "On the way":
                return "Order picked up by delivery. On the way to you.";
            case "Delivered":
                return "Order delivered. Enjoy your coffee!";
            default:
                return status;
        }
    }
}
