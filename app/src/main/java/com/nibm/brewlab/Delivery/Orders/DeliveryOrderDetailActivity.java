package com.nibm.brewlab.Delivery.Orders;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DeliveryOrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView imgBack, txtStatusBadge;
    TextView txtCustomerName, txtAddress, txtItems, txtTotal, txtPayment, txtDate;
    LinearLayout layoutAcceptReject, layoutInProgressActions, layoutProgress;
    Button btnAccept, btnReject, btnMarkPickedUp, btnMarkDelivered;
    View segAccepted, segPickedUp, segDelivered;
    TextView txtProgressStatus;
    TextView txtDeliveredBanner;
    MapView mapView;
    FrameLayout mapContainer;
    GoogleMap googleMap;
    TextView btnNavigate;

    FirebaseAuth auth;
    DatabaseReference ordersRef, usersRef;
    String orderId;
    String myUid, myName = "Delivery Person";

    DeliveryOrder currentOrder;

    ValueEventListener orderListener;

    private static final LatLng DEFAULT_LOCATION = new LatLng(6.0535, 80.2210);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_order_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        imgBack = findViewById(R.id.imgBack);
        txtStatusBadge = findViewById(R.id.txtStatusBadge);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtAddress = findViewById(R.id.txtAddress);
        txtItems = findViewById(R.id.txtItems);
        txtTotal = findViewById(R.id.txtTotal);
        txtPayment = findViewById(R.id.txtPayment);
        txtDate = findViewById(R.id.txtDate);

        layoutAcceptReject = findViewById(R.id.layoutAcceptReject);
        layoutInProgressActions = findViewById(R.id.layoutInProgressActions);
        layoutProgress = findViewById(R.id.layoutProgress);
        segAccepted = findViewById(R.id.segAccepted);
        segPickedUp = findViewById(R.id.segPickedUp);
        segDelivered = findViewById(R.id.segDelivered);
        txtProgressStatus = findViewById(R.id.txtProgressStatus);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        btnMarkPickedUp = findViewById(R.id.btnMarkPickedUp);
        btnMarkDelivered = findViewById(R.id.btnMarkDelivered);
        txtDeliveredBanner = findViewById(R.id.txtDeliveredBanner);

        mapContainer = findViewById(R.id.mapContainer);
        mapView = findViewById(R.id.mapView);
        btnNavigate = findViewById(R.id.btnNavigate);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnNavigate.setOnClickListener(v -> openNavigation());

        auth = FirebaseAuth.getInstance();
        myUid = auth.getCurrentUser().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        orderId = getIntent().getStringExtra("orderId");

        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imgBack.setOnClickListener(v -> finish());

        loadMyName();
        loadOrder();

        btnAccept.setOnClickListener(v -> acceptDelivery());
        btnReject.setOnClickListener(v -> {
            Toast.makeText(this, "Skipped. It will stay available for other requests.", Toast.LENGTH_SHORT).show();
            finish();
        });
        btnMarkPickedUp.setOnClickListener(v -> markPickedUp());
        btnMarkDelivered.setOnClickListener(v -> markDelivered());
    }

    private void loadMyName() {
        usersRef.child(myUid).child("name").get().addOnSuccessListener(snapshot -> {
            String name = snapshot.getValue(String.class);
            if (name != null) myName = name;
        });
    }

    private void loadOrder() {

        orderListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (isFinishing() || isDestroyed()) return;

                DeliveryOrder order;
                try {
                    order = snapshot.getValue(DeliveryOrder.class);
                } catch (Exception e) {
                    Toast.makeText(DeliveryOrderDetailActivity.this,
                            "This order's data is malformed and can't be opened.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                if (order == null) {
                    Toast.makeText(DeliveryOrderDetailActivity.this,
                            "This order is no longer available", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                order.setId(orderId);
                currentOrder = order;
                bindOrder(order);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(DeliveryOrderDetailActivity.this,
                        "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        ordersRef.child(orderId).addValueEventListener(orderListener);
    }
    private void bindOrder(DeliveryOrder order) {

        txtCustomerName.setText(order.getCustomerName());
        txtAddress.setText(order.getDeliveryAddress());
        txtItems.setText(order.getItemsSummary());
        txtTotal.setText("Rs. " + order.getTotalAmount());
        txtPayment.setText(order.getPaymentMethod());
        txtDate.setText(DateFormat.format("dd MMM yyyy, hh:mm a", order.getTimestamp()));

        boolean isPending = "Pending".equalsIgnoreCase(order.getStatus());
        boolean isMine = myUid.equals(order.getDeliveryPersonUid());
        boolean isOutForDelivery = "Out for Delivery".equalsIgnoreCase(order.getStatus()) && isMine;
        boolean isDelivered = "Delivered".equalsIgnoreCase(order.getStatus());

        String stage = order.getStage();
        if (isDelivered) {
            stage = "Delivered";
        } else if (isOutForDelivery && stage == null) {
            stage = "Accepted";
        }
        boolean isPickedUp = "Picked Up".equalsIgnoreCase(stage) || isDelivered;

        if (isDelivered) {
            txtStatusBadge.setText("Delivered ✓");
            txtStatusBadge.setTextColor(0xFF6FCF97);
        } else if (isOutForDelivery) {
            txtStatusBadge.setText(isPickedUp ? "Picked Up" : "Out for Delivery");
            txtStatusBadge.setTextColor(0xFF6FCF97);
        } else {
            txtStatusBadge.setText("New Request");
            txtStatusBadge.setTextColor(0xFFD89A5C);
        }

        layoutAcceptReject.setVisibility(isPending ? View.VISIBLE : View.GONE);
        layoutInProgressActions.setVisibility(isOutForDelivery ? View.VISIBLE : View.GONE);
        layoutProgress.setVisibility((isOutForDelivery || isDelivered) ? View.VISIBLE : View.GONE);
        mapContainer.setVisibility(isOutForDelivery ? View.VISIBLE : View.GONE);
        txtDeliveredBanner.setVisibility(isDelivered ? View.VISIBLE : View.GONE);

        if (isOutForDelivery || isDelivered) {
            updateProgress(isPickedUp, isDelivered);
        }

        if (isOutForDelivery) {
            btnMarkPickedUp.setVisibility(isPickedUp ? View.GONE : View.VISIBLE);
            btnMarkDelivered.setVisibility(isPickedUp ? View.VISIBLE : View.GONE);
        }

        if (isDelivered && order.getDeliveredAt() > 0) {
            txtDeliveredBanner.setText("Delivered on "
                    + DateFormat.format("dd MMM yyyy, hh:mm a", order.getDeliveredAt()));
        }

        if (isOutForDelivery) {
            placeMapMarker(order.getDeliveryAddress());
        }
    }

    private void updateProgress(boolean isPickedUp, boolean isDelivered) {

        int gold = 0xFFD89A5C;
        int muted = 0xFF3A2A22;

        segAccepted.setBackgroundColor(gold);
        segPickedUp.setBackgroundColor(isPickedUp ? gold : muted);
        segDelivered.setBackgroundColor(isDelivered ? gold : muted);

        if (isDelivered) {
            txtProgressStatus.setText("Delivered to customer");
            txtProgressStatus.setTextColor(0xFF6FCF97);
        } else if (isPickedUp) {
            txtProgressStatus.setText("Picked up, on the way to customer");
            txtProgressStatus.setTextColor(gold);
        } else {
            txtProgressStatus.setText("Accepted, heading to pickup");
            txtProgressStatus.setTextColor(gold);
        }
    }

    private void acceptDelivery() {

        btnAccept.setEnabled(false);
        btnReject.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Out for Delivery");
        updates.put("stage", "Accepted");
        updates.put("deliveryPersonUid", myUid);
        updates.put("deliveryPersonName", myName);
        updates.put("acceptedAt", System.currentTimeMillis());

        ordersRef.child(orderId).updateChildren(updates)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Delivery accepted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    btnAccept.setEnabled(true);
                    btnReject.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void markPickedUp() {

        btnMarkPickedUp.setEnabled(false);

        ordersRef.child(orderId).child("stage").setValue("Picked Up")
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Order picked up. Head to the customer!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    btnMarkPickedUp.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void markDelivered() {

        btnMarkDelivered.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Delivered");
        updates.put("stage", "Delivered");
        updates.put("deliveredAt", System.currentTimeMillis());

        ordersRef.child(orderId).updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Order marked as delivered. Great job!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnMarkDelivered.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void openNavigation() {

        if (currentOrder == null || currentOrder.getDeliveryAddress() == null) return;

        String address = currentOrder.getDeliveryAddress();

        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query="
                    + Uri.encode(address));
            startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }

    private void placeMapMarker(String address) {

        if (executor.isShutdown()) return;

        executor.execute(() -> {

            LatLng target = DEFAULT_LOCATION;

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> results = geocoder.getFromLocationName(address, 1);
                if (results != null && !results.isEmpty()) {
                    Address a = results.get(0);
                    target = new LatLng(a.getLatitude(), a.getLongitude());
                }
            } catch (Exception ignored) {
                // Network/geocoder unavailable - keep the fallback location
            }

            LatLng finalTarget = target;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (googleMap != null) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions()
                            .position(finalTarget)
                            .title(txtCustomerName.getText().toString()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(finalTarget, 15f));
                }
            });
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
            if (!success) {
                android.util.Log.e("MAP_STYLE", "Style parsing failed");
            }
        } catch (android.content.res.Resources.NotFoundException e) {
            android.util.Log.e("MAP_STYLE", "Style resource not found: " + e.getMessage());
        }

        googleMap.setOnMapClickListener(latLng -> openNavigation());
        googleMap.setOnMarkerClickListener(marker -> {
            openNavigation();
            return true;
        });

        if (currentOrder != null && "Out for Delivery".equalsIgnoreCase(currentOrder.getStatus())) {
            placeMapMarker(currentOrder.getDeliveryAddress());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (orderId != null && orderListener != null) {
                ordersRef.child(orderId).removeEventListener(orderListener);
            }
            mapView.onDestroy();
            executor.shutdown();
        } finally {
            super.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}