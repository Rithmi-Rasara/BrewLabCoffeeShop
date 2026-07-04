package com.nibm.brewlab.Admin.Product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ManageProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText searchBox;
    FloatingActionButton addBtn;

    ArrayList<Product> productList;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerView);
        searchBox = findViewById(R.id.searchBox);
        addBtn = findViewById(R.id.addBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();

        productList.add(new Product("Cappuccino", "850.00", "Hot Coffee", "Fresh Cappuccino", "cappuccino", "image_url_here"));
        productList.add(new Product("1", "Latte", "900.00", "Hot Coffee", "Creamy Latte", "latte"));
        productList.add(new Product("2", "Espresso", "700.00", "Hot Coffee", "Strong Espresso", "espresso"));
        productList.add(new Product("3", "Mocha", "950.00", "Hot Coffee", "Chocolate Mocha", "mocha"));
        productList.add(new Product("4", "Americano", "750.00", "Hot Coffee", "Classic Americano", "americano"));
        productList.add(new Product("5", "Cold Brew", "800.00", "Cold Coffee", "Refreshing Cold Brew", "cold_brew"));
        productList.add(new Product("6", "Iced Latte", "950.00", "Cold Coffee", "Iced Latte", "iced_latte"));
        productList.add(new Product("7", "Caramel Frappé", "1200.00", "Frappé", "Caramel Frappé", "caramel_frappe"));

        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        addBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class)));

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}