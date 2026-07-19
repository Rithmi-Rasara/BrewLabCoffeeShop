package com.nibm.brewlab.Customer.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.Customer.Orders.PlaceOrderActivity;
import com.nibm.brewlab.R;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.HashSet;
import java.util.Set;
=======
>>>>>>> origin/develop

public class CartActivity extends AppCompatActivity implements CartAdapter.CartActionListener {

    RecyclerView recyclerView;
<<<<<<< HEAD
    TextView txtTotal, txtSelectedTotal, txtEmptyCart;
    Button btnCheckout;

    ArrayList<CartItem> cartList;
    Set<String> selectedKeys;
=======
    TextView txtTotal, txtEmptyCart;
    Button btnCheckout;

    ArrayList<CartItem> cartList;
>>>>>>> origin/develop
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
<<<<<<< HEAD
        txtSelectedTotal = findViewById(R.id.txtSelectedTotal);
=======
>>>>>>> origin/develop
        txtEmptyCart = findViewById(R.id.txtEmptyCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        cartList = new ArrayList<>();
<<<<<<< HEAD
        selectedKeys = new HashSet<>();
        adapter = new CartAdapter(this, cartList, selectedKeys, this);
=======
        adapter = new CartAdapter(this, cartList, this);
>>>>>>> origin/develop

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {

<<<<<<< HEAD
            if (selectedKeys.isEmpty()) {
                Toast.makeText(this, "Select at least one item to checkout", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CartActivity.this, PlaceOrderActivity.class);
            intent.putStringArrayListExtra("selectedCartKeys", new ArrayList<>(selectedKeys));
            startActivity(intent);
=======
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            startActivity(new Intent(CartActivity.this, PlaceOrderActivity.class));
>>>>>>> origin/develop
        });

        loadCart();
    }

    private void loadCart() {

        CartManager.getCartRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cartList.clear();
                double total = 0;

<<<<<<< HEAD
                // Cart items that got removed by the customer shouldn't
                // linger as "selected" for a future checkout.
                Set<String> existingKeys = new HashSet<>();

=======
>>>>>>> origin/develop
                for (DataSnapshot child : snapshot.getChildren()) {

                    CartItem item = child.getValue(CartItem.class);

                    if (item != null) {
                        item.setCartKey(child.getKey());
                        cartList.add(item);
<<<<<<< HEAD
                        existingKeys.add(child.getKey());
=======
>>>>>>> origin/develop

                        try {
                            total += Double.parseDouble(item.getPrice()) * item.getQuantity();
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

<<<<<<< HEAD
                selectedKeys.retainAll(existingKeys);

                adapter.notifyDataSetChanged();

                txtTotal.setText("Cart Total: Rs. " + String.format("%.2f", total));
                txtEmptyCart.setVisibility(cartList.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);

                updateSelectedTotal();
=======
                adapter.notifyDataSetChanged();

                txtTotal.setText("Total: Rs. " + String.format("%.2f", total));
                txtEmptyCart.setVisibility(cartList.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                btnCheckout.setEnabled(!cartList.isEmpty());
>>>>>>> origin/develop
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

<<<<<<< HEAD
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

=======
>>>>>>> origin/develop
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
