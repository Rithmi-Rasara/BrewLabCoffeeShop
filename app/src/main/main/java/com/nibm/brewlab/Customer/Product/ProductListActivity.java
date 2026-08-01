package com.nibm.brewlab.Customer.Product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.Customer.Cart.CartActivity;
import com.nibm.brewlab.R;

import java.util.ArrayList;

// NOTE: Products live in Cloud Firestore, not Realtime Database - this
// matches the Admin module, which manages the product catalog in
// Firestore's "Products" collection. (Orders still also go to Realtime
// Database too, since the Delivery module reads orders from there.)
public class ProductListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText searchBox;
    ImageView imgCart;

    ArrayList<Product> productList;
    CustomerProductAdapter adapter;

    FirebaseFirestore db;
    CollectionReference productsRef;

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

        db = FirebaseFirestore.getInstance();
        productsRef = db.collection("Products");

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

        // Live listener - so newly added/edited products from the Admin
        // app show up here without the customer needing to reopen the screen.
        productsRef.addSnapshotListener((snapshot, error) -> {

            if (error != null) {
                Toast.makeText(ProductListActivity.this,
                        "Failed to load products: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (snapshot == null) return;

            if (snapshot.isEmpty()) {
                seedDefaultProducts();
                return;
            }

            ArrayList<Product> list = new ArrayList<>();

            for (QueryDocumentSnapshot doc : snapshot) {
                Product product = doc.toObject(Product.class);
                product.setId(doc.getId());
                list.add(product);
            }

            adapter.updateList(list);
        });
    }

    // Runs only once, the first time the app is used and "Products"
    // collection is still empty on Firestore, so the customer screen
    // always has something to show even before the Admin module adds
    // real products.
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
            Product product = new Product(d[0], d[1], d[2], d[3], d[4]);
            productsRef.add(product);
        }
    }
}
