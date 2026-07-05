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

public class CartActivity extends AppCompatActivity implements CartAdapter.CartActionListener {

    RecyclerView recyclerView;
    TextView txtTotal, txtEmptyCart;
    Button btnCheckout;

    ArrayList<CartItem> cartList;
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
        txtEmptyCart = findViewById(R.id.txtEmptyCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        cartList = new ArrayList<>();
        adapter = new CartAdapter(this, cartList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {

            if (cartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            startActivity(new Intent(CartActivity.this, PlaceOrderActivity.class));
        });

        loadCart();
    }

    private void loadCart() {

        CartManager.getCartRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cartList.clear();
                double total = 0;

                for (DataSnapshot child : snapshot.getChildren()) {

                    CartItem item = child.getValue(CartItem.class);

                    if (item != null) {
                        item.setCartKey(child.getKey());
                        cartList.add(item);

                        try {
                            total += Double.parseDouble(item.getPrice()) * item.getQuantity();
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                txtTotal.setText("Total: Rs. " + String.format("%.2f", total));
                txtEmptyCart.setVisibility(cartList.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                btnCheckout.setEnabled(!cartList.isEmpty());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
