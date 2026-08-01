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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.nibm.brewlab.Customer.Cart.CartItem;
import com.nibm.brewlab.Customer.Cart.CartManager;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Orders now live in Firestore ONLY - this is the single source of truth
// that Admin approves/assigns from and Delivery accepts/tracks from
// (confirmed from the real "Orders" documents: address, payment, status,
// stage, total, userId, deliveryPersonId, deliveryPersonName, deliveredAt).
// The structured "items" array is added on top so the customer app can
// still show per-item brew time and let the customer reorder specific
// items - Admin/Delivery just ignore that field since they don't read it.
public class PlaceOrderActivity extends AppCompatActivity {

    EditText edtAddress;
    Spinner spinnerPayment;
    TextView txtOrderTotal;
    Button btnPlaceOrder;

    LinearLayout cardFieldsLayout;
    EditText edtCardNumber, edtCardExpiry, edtCardCvv, edtCardName;

    double cartTotal = 0;
    String itemsSummary = "";
    List<OrderLineItem> lineItems = new ArrayList<>();
    ArrayList<String> selectedCartKeys = new ArrayList<>();

    FirebaseAuth auth;
    FirebaseFirestore firestore;

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
        firestore = FirebaseFirestore.getInstance();

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

        ArrayList<String> passedKeys = getIntent().getStringArrayListExtra("selectedCartKeys");

        if (passedKeys == null || passedKeys.isEmpty()) {
            Toast.makeText(this, "No items selected - go back to Cart and tick items first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        selectedCartKeys = passedKeys;

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

        CartManager.getCartRef().get().addOnSuccessListener(snapshot -> {

            StringBuilder summary = new StringBuilder();
            cartTotal = 0;
            lineItems.clear();

            for (QueryDocumentSnapshot doc : snapshot) {

                // Only include cart lines the customer actually ticked
                // on the Cart screen - the rest stay untouched in the cart.
                if (!selectedCartKeys.contains(doc.getId())) continue;

                CartItem item = doc.toObject(CartItem.class);

                summary.append(item.getName())
                        .append(" (")
                        .append(item.getSize() != null ? item.getSize() : "Medium");

                if (item.getAddOns() != null && !item.getAddOns().equalsIgnoreCase("None")) {
                    summary.append(", +").append(item.getAddOns());
                }

                summary.append(") x")
                        .append(item.getQuantity())
                        .append(", ");

                int brewTime = OrderLineItem.estimateBrewTime(item.getCategory(), item.getSize());

                lineItems.add(new OrderLineItem(
                        item.getProductId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSize(),
                        item.getSugarLevel(),
                        item.getAddOns(),
                        item.getImageUri(),
                        item.getCategory(),
                        brewTime
                ));

                try {
                    cartTotal += Double.parseDouble(item.getPrice()) * item.getQuantity();
                } catch (NumberFormatException ignored) {
                }
            }

            itemsSummary = summary.toString();

            if (itemsSummary.endsWith(", ")) {
                itemsSummary = itemsSummary.substring(0, itemsSummary.length() - 2);
            }

            txtOrderTotal.setText("Total: Rs. " + String.format("%.2f", cartTotal));

        }).addOnFailureListener(e ->
                Toast.makeText(PlaceOrderActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

        if (lineItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (payment.equals("Card Payment") && !validateCardDetails()) {
            return;
        }

        btnPlaceOrder.setEnabled(false);

        String uid = auth.getCurrentUser().getUid();
        DocumentReference userDocRef = firestore.collection("Users").document(uid);

        userDocRef.get().addOnSuccessListener(snapshot -> {

            String rawName = snapshot.getString("name");
            final String customerName = (rawName != null) ? rawName : "Customer";

            String paymentLabel = payment.equals("Card Payment")
                    ? "Card Payment (**** " + edtCardNumber.getText().toString().replaceAll("\\s", "").substring(12) + ")"
                    : payment;

            // Structured line items - only the customer app reads this
            // back (for brew time + per-item reorder); Admin/Delivery use
            // the flat fields below (address/payment/status/total/etc).
            List<Map<String, Object>> itemsForFirestore = new ArrayList<>();
            for (OrderLineItem item : lineItems) {
                Map<String, Object> map = new HashMap<>();
                map.put("productId", item.getProductId());
                map.put("name", item.getName());
                map.put("price", item.getPrice());
                map.put("quantity", item.getQuantity());
                map.put("size", item.getSize());
                map.put("sugarLevel", item.getSugarLevel());
                map.put("addOns", item.getAddOns());
                map.put("imageUri", item.getImageUri());
                map.put("category", item.getCategory());
                map.put("brewTimeMinutes", item.getBrewTimeMinutes());
                itemsForFirestore.add(map);
            }

            Map<String, Object> firestoreOrder = new HashMap<>();
            firestoreOrder.put("userId", uid);
            firestoreOrder.put("customerName", customerName);
            firestoreOrder.put("address", address);
            firestoreOrder.put("payment", paymentLabel);
            firestoreOrder.put("status", "Pending");
            firestoreOrder.put("stage", "");
            firestoreOrder.put("total", cartTotal);
            firestoreOrder.put("timestamp", System.currentTimeMillis());
            firestoreOrder.put("itemsSummary", itemsSummary);
            firestoreOrder.put("items", itemsForFirestore);

            firestore.collection("Orders")
                    .add(firestoreOrder)
                    .addOnSuccessListener(docRef -> {

                        // Loyalty points: 1 point per Rs. 100 spent - merge
                        // so we don't wipe out other profile fields.
                        long earnedPoints = (long) (cartTotal / 100);

                        userDocRef.get().addOnSuccessListener(pointSnap -> {

                            Long current = pointSnap.getLong("loyaltyPoints");
                            long updated = (current != null ? current : 0) + earnedPoints;

                            Map<String, Object> pointsUpdate = new HashMap<>();
                            pointsUpdate.put("loyaltyPoints", updated);
                            userDocRef.set(pointsUpdate, SetOptions.merge());
                        });

                        // Items are intentionally left in the cart after
                        // checkout (customer asked for this) so they can
                        // be checked out again later without re-adding
                        // from the menu.

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
    }
}
