package com.nibm.brewlab.Customer.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.brewlab.Customer.Orders.PlaceOrderActivity;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartActionListener {

    RecyclerView recyclerView;
    TextView txtTotal, txtSelectedTotal, txtEmptyCart;
    Button btnCheckout;

    ArrayList<CartItem> cartList;
    Set<String> selectedKeys;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerCart);
        txtTotal = findViewById(R.id.txtCartTotal);
        txtSelectedTotal = findViewById(R.id.txtSelectedTotal);
        txtEmptyCart = findViewById(R.id.txtEmptyCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        cartList = new ArrayList<>();
        selectedKeys = new HashSet<>();
        adapter = new CartAdapter(this, cartList, selectedKeys, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {

            if (selectedKeys.isEmpty()) {
                Toast.makeText(this, "Select at least one item to checkout", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CartActivity.this, PlaceOrderActivity.class);
            intent.putStringArrayListExtra("selectedCartKeys", new ArrayList<>(selectedKeys));
            startActivity(intent);
        });

        loadCart();
    }

    private void loadCart() {

        CartManager.getCartRef().addSnapshotListener((snapshot, error) -> {

            if (error != null) {
                Toast.makeText(CartActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot == null) return;

            cartList.clear();
            double total = 0;

            // Cart items that got removed by the customer shouldn't
            // linger as "selected" for a future checkout.
            Set<String> existingKeys = new HashSet<>();

            for (QueryDocumentSnapshot doc : snapshot) {

                CartItem item = doc.toObject(CartItem.class);
                item.setCartKey(doc.getId());
                cartList.add(item);
                existingKeys.add(doc.getId());

                try {
                    total += Double.parseDouble(item.getPrice()) * item.getQuantity();
                } catch (NumberFormatException ignored) {
                }
            }

            selectedKeys.retainAll(existingKeys);

            adapter.notifyDataSetChanged();

            txtTotal.setText("Cart Total: Rs. " + String.format("%.2f", total));
            txtEmptyCart.setVisibility(cartList.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);

            updateSelectedTotal();
        });
    }

    private void updateSelectedTotal() {

        double selectedTotal = 0;

        for (CartItem item : cartList) {

            if (item.getCartKey() != null && selectedKeys.contains(item.getCartKey())) {
                try {
                    selectedTotal += Double.parseDouble(item.getPrice()) * item.getQuantity();
                } catch (NumberFormatException ignored) {
                }
            }
        }

        txtSelectedTotal.setText("Selected: Rs. " + String.format("%.2f", selectedTotal));
        btnCheckout.setEnabled(!selectedKeys.isEmpty());
    }

    @Override
    public void onSelectionChanged() {
        updateSelectedTotal();
    }

    @Override
    public void onIncrease(CartItem item) {
        CartManager.updateQuantity(item.getCartKey(), item.getQuantity() + 1);
    }

    @Override
    public void onDecrease(CartItem item) {
        CartManager.updateQuantity(item.getCartKey(), item.getQuantity() - 1);
    }

    @Override
    public void onRemove(CartItem item) {
        CartManager.removeItem(item.getCartKey());
        Toast.makeText(this, item.getName() + " removed", Toast.LENGTH_SHORT).show();
    }
}
