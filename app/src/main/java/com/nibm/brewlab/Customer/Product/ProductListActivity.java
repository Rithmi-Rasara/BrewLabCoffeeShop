package com.nibm.brewlab.Customer.Product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.Customer.Cart.CartActivity;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText searchBox;
    ImageView imgCart;

    ArrayList<Product> productList;
    CustomerProductAdapter adapter;

    DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerProducts);
        searchBox = findViewById(R.id.searchBox);
        imgCart = findViewById(R.id.imgCart);

        productList = new ArrayList<>();
        adapter = new CustomerProductAdapter(this, productList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        productsRef = FirebaseDatabase.getInstance().getReference("Products");

        imgCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadProducts();
    }

    private void loadProducts() {

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    seedDefaultProducts();
                    return;
                }

                ArrayList<Product> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    if (product != null) {
                        product.setId(child.getKey());
                        list.add(product);
                    }
                }

                adapter.updateList(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductListActivity.this,
                        "Failed to load products: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // Runs only once, the first time the app is used and "Products" node
    // is still empty on Firebase, so the customer screen always has something
    // to show even before the Admin module writes real products to the DB.
    private void seedDefaultProducts() {

        String[][] defaults = {
                {"Cappuccino", "850.00", "Hot Coffee", "Fresh Cappuccino", "cappuccino"},
                {"Latte", "900.00", "Hot Coffee", "Creamy Latte", "latte"},
                {"Espresso", "700.00", "Hot Coffee", "Strong Espresso", "espresso"},
                {"Mocha", "950.00", "Hot Coffee", "Chocolate Mocha", "mocha"},
                {"Americano", "750.00", "Hot Coffee", "Classic Americano", "americano"},
                {"Cold Brew", "800.00", "Cold Coffee", "Refreshing Cold Brew", "cold_brew"},
                {"Iced Latte", "950.00", "Cold Coffee", "Iced Latte", "iced_latte"},
                {"Caramel Frappe", "1200.00", "Frappe", "Caramel Frappe", "caramel_frappe"}
        };

        for (String[] d : defaults) {
            String id = productsRef.push().getKey();
            if (id != null) {
                Product product = new Product(
                        d[0],
                        d[1],
                        d[2],
                        d[3],
                        d[4]
                );

                product.setId(id);
                productsRef.child(id).setValue(product);
            }
        }
    }
}
