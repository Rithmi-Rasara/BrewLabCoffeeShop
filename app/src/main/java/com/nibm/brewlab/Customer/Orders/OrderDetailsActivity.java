package com.nibm.brewlab.Customer.Orders;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.Customer.Cart.CartManager;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView txtTotal, txtPayment, txtAddress, txtDate, txtStatus, txtTracking, txtDeliveryPerson;
    LinearLayout itemsContainer;
    android.widget.Button btnReorderSelected;

    FrameLayout mapContainer;
    MapView mapView;
    GoogleMap googleMap;
    TextView btnNavigate;

    String orderId;
    CustomerOrder currentOrder;
    ValueEventListener orderListener;
    DatabaseReference ordersRef;

    private static final LatLng DEFAULT_LOCATION = new LatLng(6.0535, 80.2210);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Keeps the item + its checkbox together so we know exactly what
    // was ticked when "Reorder Selected Items" is pressed.
    private final List<OrderLineItem> currentItems = new ArrayList<>();
    private final List<CheckBox> currentCheckboxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtTotal = findViewById(R.id.txtDetailTotal);
        txtPayment = findViewById(R.id.txtDetailPayment);
        txtAddress = findViewById(R.id.txtDetailAddress);
        txtDate = findViewById(R.id.txtDetailDate);
        txtStatus = findViewById(R.id.txtDetailStatus);
        txtTracking = findViewById(R.id.txtDetailTracking);
        txtDeliveryPerson = findViewById(R.id.txtDeliveryPerson);
        itemsContainer = findViewById(R.id.itemsContainer);
        btnReorderSelected = findViewById(R.id.btnReorderSelected);

        mapContainer = findViewById(R.id.mapContainer);
        mapView = findViewById(R.id.mapView);
        btnNavigate = findViewById(R.id.btnNavigate);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        btnNavigate.setOnClickListener(v -> openNavigation());

        orderId = getIntent().getStringExtra("orderId");

        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        loadOrder();

        btnReorderSelected.setOnClickListener(v -> reorderSelected());
    }

    private void loadOrder() {

        orderListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (isFinishing() || isDestroyed()) return;

                CustomerOrder order = snapshot.getValue(CustomerOrder.class);

                if (order == null) {
                    Toast.makeText(OrderDetailsActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                order.setId(orderId);
                currentOrder = order;

                txtTotal.setText("Rs. " + order.getTotalAmount());
                txtPayment.setText(order.getPaymentMethod());
                txtAddress.setText(order.getDeliveryAddress());
                txtDate.setText(DateFormat.format("dd MMM yyyy, hh:mm a", order.getTimestamp()));
                txtStatus.setText(order.getStatus());
                txtTracking.setText(buildTrackingText(order));

                boolean outForDelivery = "Out for Delivery".equalsIgnoreCase(order.getStatus());

                if (outForDelivery && order.getDeliveryPersonName() != null) {
                    txtDeliveryPerson.setVisibility(View.VISIBLE);
                    txtDeliveryPerson.setText("🛵 Delivery Person: " + order.getDeliveryPersonName());
                } else {
                    txtDeliveryPerson.setVisibility(View.GONE);
                }

                mapContainer.setVisibility(outForDelivery ? View.VISIBLE : View.GONE);

                if (outForDelivery) {
                    placeMapMarker(order.getDeliveryAddress());
                }

                renderItems(order.getItems());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(OrderDetailsActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        ordersRef.child(orderId).addValueEventListener(orderListener);
    }

    // Geocodes the delivery address into a map pin - the same way the
    // Delivery module shows it on the courier's screen, so the customer
    // sees exactly where their order is headed.
    private void placeMapMarker(String address) {

        if (address == null || executor.isShutdown()) return;

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
                if (isFinishing() || isDestroyed() || googleMap == null) return;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(finalTarget)
                        .title("Delivery destination"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(finalTarget, 15f));
            });
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

    // Builds one checkable row per item that was in this order. Older
    // orders placed before this feature was added won't have structured
    // item data, so we show a friendly message instead.
    private void renderItems(List<OrderLineItem> items) {

        itemsContainer.removeAllViews();
        currentItems.clear();
        currentCheckboxes.clear();

        if (items == null || items.isEmpty()) {

            TextView txtNone = new TextView(this);
            txtNone.setText("Item details aren't available for this older order.");
            txtNone.setTextColor(0xFFBBBBBB);
            itemsContainer.addView(txtNone);
            btnReorderSelected.setEnabled(false);
            return;
        }

        for (OrderLineItem item : items) {

            View row = LayoutInflater.from(this)
                    .inflate(R.layout.item_order_detail_line, itemsContainer, false);

            CheckBox checkBox = row.findViewById(R.id.checkLineItem);
            TextView txtName = row.findViewById(R.id.txtLineName);
            TextView txtCustom = row.findViewById(R.id.txtLineCustomization);
            TextView txtBrewTime = row.findViewById(R.id.txtLineBrewTime);
            TextView txtPrice = row.findViewById(R.id.txtLinePrice);

            txtName.setText(item.getName() + " x" + item.getQuantity());

            StringBuilder custom = new StringBuilder();
            if (item.getSize() != null) custom.append(item.getSize());
            if (item.getSugarLevel() != null) custom.append(" | ").append(item.getSugarLevel());
            if (item.getAddOns() != null && !item.getAddOns().equalsIgnoreCase("None")) {
                custom.append(" | +").append(item.getAddOns());
            }
            txtCustom.setText(custom.toString());

            txtBrewTime.setText("⏱ Brew time: ~" + item.getBrewTimeMinutes() + " mins");

            double lineTotal = 0;
            try {
                lineTotal = Double.parseDouble(item.getPrice()) * item.getQuantity();
            } catch (NumberFormatException ignored) {
            }
            txtPrice.setText("Rs. " + String.format("%.2f", lineTotal));

            itemsContainer.addView(row);

            currentItems.add(item);
            currentCheckboxes.add(checkBox);
        }
    }

    // Only the items whose checkbox is ticked get added back to the cart -
    // this is the per-item reorder the customer asked for, instead of
    // re-adding the whole order at once.
    private void reorderSelected() {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");

        int selectedCount = 0;

        for (int i = 0; i < currentItems.size(); i++) {

            if (!currentCheckboxes.get(i).isChecked()) continue;

            selectedCount++;
            OrderLineItem item = currentItems.get(i);

            productsRef.child(item.getProductId()).get().addOnSuccessListener(snapshot -> {

                Product product = snapshot.getValue(Product.class);

                if (product != null) {
                    product.setId(item.getProductId());

                    double extra = 0;
                    try {
                        extra = Double.parseDouble(item.getPrice()) - Double.parseDouble(product.getPrice());
                    } catch (NumberFormatException ignored) {
                    }

                    for (int q = 0; q < item.getQuantity(); q++) {
                        CartManager.addToCart(OrderDetailsActivity.this, product,
                                item.getSize(), item.getSugarLevel(), item.getAddOns(), extra);
                    }
                }
            });
        }

        if (selectedCount == 0) {
            Toast.makeText(this, "Select at least one item to reorder", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Selected items added to cart", Toast.LENGTH_LONG).show();
        }
    }

    // Matches the real status/stage values the Delivery module writes:
    // "Pending" -> "Out for Delivery" (stage: Accepted / Picked Up) -> "Delivered"
    private String buildTrackingText(CustomerOrder order) {

        String status = order.getStatus();
        String stage = order.getStage();

        if (status == null) status = "Pending";

        switch (status) {
            case "Pending":
                return "Order received. Waiting for the shop to start preparing.";
            case "Out for Delivery":
                if ("Picked Up".equalsIgnoreCase(stage)) {
                    return "Picked up! Your order is on the way to you.";
                }
                return "A delivery person accepted your order and is heading to pick it up.";
            case "Delivered":
                return "Order delivered. Enjoy your coffee!";
            default:
                return status;
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
            if (orderId != null && orderListener != null && ordersRef != null) {
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
