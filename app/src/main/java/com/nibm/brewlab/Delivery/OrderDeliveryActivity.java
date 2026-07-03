package com.nibm.brewlab.Delivery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.nibm.brewlab.R;
import com.nibm.brewlab.model.Order;

import java.util.Locale;

public class OrderDeliveryActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView txtOrderTitle, txtCustomerName, txtCustomerPhone, txtCustomerAddress, txtOrderAmount;
    MaterialButton btnStep1Navigate, btnStep2Deliver, btnStep3UpdateStatus, btnStep4MarkDelivered;
    android.widget.ImageButton btnBack;

    String orderId;
    Order currentOrder;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_delivery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        orderId = getIntent().getStringExtra("orderId");

        bindViews();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapCustomerLocation);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        loadOrder();

        btnBack.setOnClickListener(v -> finish());

        btnStep1Navigate.setOnClickListener(v -> handleStep1Navigate());
        btnStep2Deliver.setOnClickListener(v -> handleStep2Deliver());
        btnStep3UpdateStatus.setOnClickListener(v -> handleStep3UpdateStatus());
        btnStep4MarkDelivered.setOnClickListener(v -> handleStep4MarkDelivered());
    }

    private void bindViews() {
        txtOrderTitle = findViewById(R.id.txtOrderTitle);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCustomerPhone = findViewById(R.id.txtCustomerPhone);
        txtCustomerAddress = findViewById(R.id.txtCustomerAddress);
        txtOrderAmount = findViewById(R.id.txtOrderAmount);
        btnStep1Navigate = findViewById(R.id.btnStep1Navigate);
        btnStep2Deliver = findViewById(R.id.btnStep2Deliver);
        btnStep3UpdateStatus = findViewById(R.id.btnStep3UpdateStatus);
        btnStep4MarkDelivered = findViewById(R.id.btnStep4MarkDelivered);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadOrder() {
        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentOrder = DeliveryTestData.findById(orderId);

        if (currentOrder == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindOrderData();
        syncStepButtonsWithStatus();
        placeMapMarker();
    }

    private void bindOrderData() {
        txtOrderTitle.setText("Order #" + currentOrder.getOrderId());
        txtCustomerName.setText(currentOrder.getCustomerName());
        txtCustomerPhone.setText("📞 " + (currentOrder.getCustomerPhone() != null ? currentOrder.getCustomerPhone() : "-"));
        txtCustomerAddress.setText("📍 " + (currentOrder.getAddress() != null ? currentOrder.getAddress() : "-"));
        txtOrderAmount.setText(String.format(Locale.getDefault(), "Rs. %.2f", currentOrder.getAmount()));
    }

    private void syncStepButtonsWithStatus() {
        String status = currentOrder.getStatus() == null ? "accepted" : currentOrder.getStatus();

        boolean step1Done = !status.equals("accepted");
        boolean step2Done = status.equals("arrived") || status.equals("updated") || status.equals("delivered");
        boolean step3Done = status.equals("updated") || status.equals("delivered");
        boolean step4Done = status.equals("delivered");

        btnStep1Navigate.setEnabled(!step4Done);
        btnStep2Deliver.setEnabled(step1Done && !step4Done);
        btnStep3UpdateStatus.setEnabled(step2Done && !step4Done);
        btnStep4MarkDelivered.setEnabled(step3Done && !step4Done);

        if (step4Done) {
            btnStep1Navigate.setText("✓ Navigated");
            btnStep2Deliver.setText("✓ Delivered To Customer");
            btnStep3UpdateStatus.setText("✓ Status Updated");
            btnStep4MarkDelivered.setText("✓ Order Marked Delivered");
        }
    }

    private void placeMapMarker() {
        if (googleMap == null || currentOrder == null) return;
        LatLng customerLocation = new LatLng(currentOrder.getLatitude(), currentOrder.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(customerLocation)
                .title(currentOrder.getCustomerName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 15f));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        placeMapMarker();
    }
    private void handleStep1Navigate() {
        if (currentOrder == null) return;

        Uri navUri = Uri.parse(String.format(Locale.US,
                "google.navigation:q=%f,%f", currentOrder.getLatitude(), currentOrder.getLongitude()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            // Fallback to a plain geo URI if Google Maps app isn't installed
            Uri geoUri = Uri.parse(String.format(Locale.US,
                    "geo:%f,%f?q=%f,%f", currentOrder.getLatitude(), currentOrder.getLongitude(),
                    currentOrder.getLatitude(), currentOrder.getLongitude()));
            startActivity(new Intent(Intent.ACTION_VIEW, geoUri));
        }

        DeliveryTestData.updateStatus(orderId, "on_the_way");
        syncStepButtonsWithStatus();
    }

    private void handleStep2Deliver() {
        new AlertDialog.Builder(this)
                .setTitle("Deliver Order")
                .setMessage("Confirm that you have handed over the order to the customer?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    DeliveryTestData.updateStatus(orderId, "arrived");
                    syncStepButtonsWithStatus();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleStep3UpdateStatus() {
        new AlertDialog.Builder(this)
                .setTitle("Update Delivery Status")
                .setMessage("Mark this delivery as confirmed and ready to close?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    DeliveryTestData.updateStatus(orderId, "updated");
                    syncStepButtonsWithStatus();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleStep4MarkDelivered() {
        new AlertDialog.Builder(this)
                .setTitle("Mark Order Delivered")
                .setMessage("This will complete the delivery and move the order to your delivery history.")
                .setPositiveButton("Mark Delivered", (dialog, which) -> {
                    DeliveryTestData.updateStatus(orderId, "delivered");
                    currentOrder.setDeliveredAt(System.currentTimeMillis());
                    Toast.makeText(this, "Order marked as delivered 🎉", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}