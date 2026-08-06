package com.nibm.brewlab.Customer.Orders;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.R;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeliveryTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TextView txtTrackingStatus;
    private String orderId;

    private DatabaseReference orderRef;
    private ValueEventListener orderListener;

    // Same shop location used on the Delivery side's default marker
    private static final LatLng SHOP_LOCATION = new LatLng(6.0535, 80.2210);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_tracking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtTrackingStatus = findViewById(R.id.txtTrackingStatus);

        orderId = getIntent().getStringExtra("orderId");

        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        listenForOrder();
    }

    private void listenForOrder() {

        orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);

        orderListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                CustomerOrder order = snapshot.getValue(CustomerOrder.class);
                if (order == null) return;

                String status = order.getStatus();

                if (!"Out for Delivery".equalsIgnoreCase(status)) {
                    txtTrackingStatus.setText("Order status: " + status);
                    if ("Delivered".equalsIgnoreCase(status)) {
                        Toast.makeText(DeliveryTrackingActivity.this,
                                "Order delivered!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                txtTrackingStatus.setText("Order is on the way to you");
                drawRoute(order.getDeliveryAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeliveryTrackingActivity.this,
                        "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        orderRef.addValueEventListener(orderListener);
    }

    private void drawRoute(String address) {

        if (address == null || executor.isShutdown()) return;

        executor.execute(() -> {

            LatLng destination = null;

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> results = geocoder.getFromLocationName(address, 1);
                if (results != null && !results.isEmpty()) {
                    Address a = results.get(0);
                    destination = new LatLng(a.getLatitude(), a.getLongitude());
                }
            } catch (Exception ignored) {
                // Network/geocoder unavailable
            }

            LatLng finalDestination = destination;

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed() || map == null) return;

                if (finalDestination == null) {
                    txtTrackingStatus.setText("Order is on the way (location unavailable)");
                    return;
                }

                map.clear();

                map.addMarker(new MarkerOptions()
                        .position(SHOP_LOCATION)
                        .title("BrewLab Coffee Shop")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                map.addMarker(new MarkerOptions()
                        .position(finalDestination)
                        .title("Your delivery address")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                map.addPolyline(new PolylineOptions()
                        .add(SHOP_LOCATION, finalDestination)
                        .width(8f)
                        .color(0xFF3B82F6));

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(SHOP_LOCATION)
                        .include(finalDestination)
                        .build();

                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderRef != null && orderListener != null) {
            orderRef.removeEventListener(orderListener);
        }
        executor.shutdown();
    }
}