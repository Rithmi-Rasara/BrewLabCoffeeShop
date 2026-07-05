package com.nibm.brewlab.Customer.Orders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity {

    EditText edtAddress;
    Spinner spinnerPayment;
    TextView txtOrderTotal;
    Button btnPlaceOrder;

    LinearLayout cardFieldsLayout;
    EditText edtCardNumber, edtCardExpiry, edtCardCvv, edtCardName;

    double cartTotal = 0;
    String itemsSummary = "";
    Map<String, Long> itemsData = new HashMap<>();

    FirebaseAuth auth;
    DatabaseReference ordersRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edtAddress = findViewById(R.id.edtAddress);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        txtOrderTotal = findViewById(R.id.txtOrderTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        cardFieldsLayout = findViewById(R.id.cardFieldsLayout);
        edtCardName = findViewById(R.id.edtCardName);
        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtCardExpiry = findViewById(R.id.edtCardExpiry);
        edtCardCvv = findViewById(R.id.edtCardCvv);

        auth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        String[] paymentOptions = {"Cash on Delivery", "Card Payment"};
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, paymentOptions);
        spinnerPayment.setAdapter(paymentAdapter);

        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cardFieldsLayout.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cardFieldsLayout.setVisibility(View.GONE);
            }
        });

        formatCardNumberInput();
        formatExpiryInput();

        loadCartSummary();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    // Auto-inserts a space every 4 digits while typing the card number.
    private void formatCardNumberInput() {

        edtCardNumber.addTextChangedListener(new TextWatcher() {
            boolean editing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (editing) return;
                editing = true;

                String digits = s.toString().replaceAll("[^0-9]", "");
                if (digits.length() > 16) digits = digits.substring(0, 16);

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ");
                    formatted.append(digits.charAt(i));
                }

                s.replace(0, s.length(), formatted.toString());
                editing = false;
            }
        });
    }

    // Auto-inserts "/" after 2 digits for MM/YY expiry.
    private void formatExpiryInput() {

        edtCardExpiry.addTextChangedListener(new TextWatcher() {
            boolean editing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (editing) return;
                editing = true;

                String digits = s.toString().replaceAll("[^0-9]", "");
                if (digits.length() > 4) digits = digits.substring(0, 4);

                String formatted = digits;
                if (digits.length() > 2) {
                    formatted = digits.substring(0, 2) + "/" + digits.substring(2);
                }

                s.replace(0, s.length(), formatted);
                editing = false;
            }
        });
    }

    private void loadCartSummary() {

        CartManager.getCartRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StringBuilder summary = new StringBuilder();
                cartTotal = 0;
                itemsData.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

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

                        // Quantities are combined per product so "Reorder" can
                        // add the right total amount back to the cart later
                        // (reorder uses default customization, see MyOrdersAdapter).
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

    // Simple client-side validation only - this is NOT a real payment
    // gateway. No card data is stored or sent anywhere; it's just enough
    // validation to demonstrate the card-payment flow for the assessment.
    private boolean validateCardDetails() {

        String name = edtCardName.getText().toString().trim();
        String number = edtCardNumber.getText().toString().replaceAll("\\s", "");
        String expiry = edtCardExpiry.getText().toString().trim();
        String cvv = edtCardCvv.getText().toString().trim();

        if (name.isEmpty()) {
            edtCardName.setError("Enter name on card");
            edtCardName.requestFocus();
            return false;
        }

        if (number.length() != 16) {
            edtCardNumber.setError("Card number must be 16 digits");
            edtCardNumber.requestFocus();
            return false;
        }

        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            edtCardExpiry.setError("Use MM/YY format");
            edtCardExpiry.requestFocus();
            return false;
        }

        if (cvv.length() != 3) {
            edtCardCvv.setError("CVV must be 3 digits");
            edtCardCvv.requestFocus();
            return false;
        }

        return true;
    }

    private void placeOrder() {

        String address = edtAddress.getText().toString().trim();
        String payment = spinnerPayment.getSelectedItem().toString();

        if (address.isEmpty()) {
            edtAddress.setError("Enter delivery address");
            edtAddress.requestFocus();
            return;
        }

        if (itemsData.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (payment.equals("Card Payment") && !validateCardDetails()) {
            return;
        }

        btnPlaceOrder.setEnabled(false);

        String uid = auth.getCurrentUser().getUid();

        usersRef.child(uid).get().addOnSuccessListener(snapshot -> {

            String customerName = snapshot.child("name").getValue(String.class);
            if (customerName == null) customerName = "Customer";

            String orderId = ordersRef.push().getKey();

            String paymentLabel = payment.equals("Card Payment")
                    ? "Card Payment (**** " + edtCardNumber.getText().toString().replaceAll("\\s", "").substring(12) + ")"
                    : payment;

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

            if (orderId != null) {

                ordersRef.child(orderId).setValue(order).addOnSuccessListener(unused -> {

                    // Loyalty points: 1 point per Rs. 100 spent
                    long earnedPoints = (long) (cartTotal / 100);
                    usersRef.child(uid).child("loyaltyPoints").get().addOnSuccessListener(pointSnap -> {

                        Long current = pointSnap.getValue(Long.class);
                        long updated = (current != null ? current : 0) + earnedPoints;
                        usersRef.child(uid).child("loyaltyPoints").setValue(updated);
                    });

                    CartManager.clearCart();

                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(PlaceOrderActivity.this, MyOrdersActivity.class));
                    finish();

                }).addOnFailureListener(e -> {
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

        }).addOnFailureListener(e -> {
            btnPlaceOrder.setEnabled(true);
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
