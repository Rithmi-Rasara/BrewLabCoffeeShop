package com.nibm.brewlab.Customer.Orders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.Customer.Cart.CartItem;
import com.nibm.brewlab.Customer.Cart.CartManager;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlaceOrderActivity extends AppCompatActivity {

    // NEW
    EditText edtOrderId, edtCustomerId;

    EditText edtAddress;
    TextView txtOrderTotal;
    Button btnPlaceOrder;

    double cartTotal = 0;
    String itemsSummary = "";
    Map<String, Long> itemsData = new HashMap<>();

    // NEW: only these cart keys (checkbox-selected in CartActivity) are
    // included in this order and cleared from the cart afterwards. If this
    // activity is opened without the extra (e.g. old entry point), every
    // cart item is treated as selected so nothing silently breaks.
    Set<String> selectedCartKeys;

    FirebaseAuth auth;
    DatabaseReference ordersRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // NEW
        edtOrderId = findViewById(R.id.edtOrderId);
        edtCustomerId = findViewById(R.id.edtCustomerId);

        edtAddress = findViewById(R.id.edtAddress);
        txtOrderTotal = findViewById(R.id.txtOrderTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        auth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // NEW: grab the keys CartActivity sent, if any
        List<String> keysExtra = getIntent().getStringArrayListExtra("selectedCartKeys");
        selectedCartKeys = (keysExtra != null) ? new HashSet<>(keysExtra) : null;

        loadCartSummary();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadCartSummary() {

        CartManager.getCartRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StringBuilder summary = new StringBuilder();
                cartTotal = 0;
                itemsData.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    // NEW: skip items that weren't checkbox-selected in the cart
                    if (selectedCartKeys != null && !selectedCartKeys.contains(child.getKey())) {
                        continue;
                    }

                    CartItem item = child.getValue(CartItem.class);

                    if (item != null) {

                        summary.append(item.getName())
                                .append(" (")
                                .append(item.getSize() != null ? item.getSize() : "Medium");

                        if (item.getAddOns() != null && !item.getAddOns().equalsIgnoreCase("None")) {
                            summary.append(", +").append(item.getAddOns());
                        }

                        summary.append(") x")
                                .append(item.getQuantity())
                                .append(", ");

                        long existingQty = itemsData.containsKey(item.getProductId())
                                ? itemsData.get(item.getProductId()) : 0;
                        itemsData.put(item.getProductId(), existingQty + item.getQuantity());

                        try {
                            cartTotal += Double.parseDouble(item.getPrice()) * item.getQuantity();
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                itemsSummary = summary.toString();

                if (itemsSummary.endsWith(", ")) {
                    itemsSummary = itemsSummary.substring(0, itemsSummary.length() - 2);
                }

                txtOrderTotal.setText("Total: Rs. " + String.format("%.2f", cartTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlaceOrderActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeOrder() {

        // NEW
        String orderId = edtOrderId.getText().toString().trim();
        String customerId = edtCustomerId.getText().toString().trim();

        String address = edtAddress.getText().toString().trim();
        String payment = "Cash on Delivery";

        // NEW
        if (orderId.isEmpty()) {
            edtOrderId.setError("Enter an Order ID");
            edtOrderId.requestFocus();
            return;
        }

        // Firebase Realtime Database keys cannot contain '.', '#', '$', '[', ']', '/' or whitespace.
        if (orderId.matches(".*[.#$\\[\\]/\\s].*")) {
            edtOrderId.setError("No spaces or . # $ [ ] / characters allowed");
            edtOrderId.requestFocus();
            return;
        }

        // NEW
        if (customerId.isEmpty()) {
            edtCustomerId.setError("Enter a Customer ID");
            edtCustomerId.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            edtAddress.setError("Enter delivery address");
            edtAddress.requestFocus();
            return;
        }

        if (itemsData.isEmpty()) {
            Toast.makeText(this, "No items selected to order", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPlaceOrder.setEnabled(false);

        // NEW: make sure this Order ID isn't already used before writing anything
        ordersRef.child(orderId).get().addOnSuccessListener(existingSnap -> {

            if (existingSnap.exists()) {
                btnPlaceOrder.setEnabled(true);
                edtOrderId.setError("This Order ID is already used, pick another one");
                edtOrderId.requestFocus();
                return;
            }

            String uid = auth.getCurrentUser().getUid();

            usersRef.child(uid).get().addOnSuccessListener(snapshot -> {

                String nameFromDb = snapshot.child("name").getValue(String.class);
                final String customerName = (nameFromDb != null) ? nameFromDb : "Customer";

                String paymentLabel = payment;

                CustomerOrder order = new CustomerOrder(
                        uid,
                        customerName,
                        String.format("%.2f", cartTotal),
                        "Pending",
                        paymentLabel,
                        address,
                        System.currentTimeMillis(),
                        itemsSummary,
                        itemsData
                );

                // NEW
                order.setOrderId(orderId);
                order.setCustomerId(customerId);

                ordersRef.child(orderId).setValue(order).addOnSuccessListener(unused -> {

                    // Loyalty points: 1 point per Rs. 100 spent
                    // Written straight into Firestore "Loyalty" (same doc admin's
                    // Manage Loyalty screen and the dashboard/profile cards use)
                    // so points AND level stay in sync everywhere - Realtime DB
                    // "loyaltyPoints" was a separate, disconnected value.
                    long earnedPoints = (long) (cartTotal / 100);

                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("Loyalty").document(uid).get()
                            .addOnSuccessListener(loyaltySnap -> {

                                Long currentPoints = (loyaltySnap != null && loyaltySnap.exists())
                                        ? loyaltySnap.getLong("points") : null;
                                long updatedPoints = (currentPoints != null ? currentPoints : 0) + earnedPoints;
                                String updatedLevel = com.nibm.brewlab.Admin.Loyalty.Loyalty
                                        .calculateLevel((int) updatedPoints);

                                java.util.HashMap<String, Object> loyaltyData = new java.util.HashMap<>();
                                loyaltyData.put("name", customerName);
                                loyaltyData.put("email", auth.getCurrentUser().getEmail());
                                loyaltyData.put("points", updatedPoints);
                                loyaltyData.put("level", updatedLevel);

                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                        .collection("Loyalty").document(uid)
                                        .set(loyaltyData, com.google.firebase.firestore.SetOptions.merge());
                            });

                    // NEW: only clear the items that were actually part of this
                    // order, so anything left unselected stays in the cart.
                    if (selectedCartKeys != null) {
                        for (String key : selectedCartKeys) {
                            CartManager.removeItem(key);
                        }
                    } else {
                        CartManager.clearCart();
                    }

                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(PlaceOrderActivity.this, MyOrdersActivity.class));
                    finish();

                }).addOnFailureListener(e -> {
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

            }).addOnFailureListener(e -> {
                btnPlaceOrder.setEnabled(true);
                Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });

        }).addOnFailureListener(e -> {
            btnPlaceOrder.setEnabled(true);
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}