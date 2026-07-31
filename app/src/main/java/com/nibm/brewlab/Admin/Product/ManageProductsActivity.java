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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ManageProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText searchBox;
    FloatingActionButton addBtn;

    ArrayList<Product> productList;
    ProductAdapter adapter;

    FirebaseFirestore db;

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

        adapter = new ProductAdapter(this, productList);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadProducts();

        addBtn.setOnClickListener(v -> {

            Intent intent = new Intent(ManageProductsActivity.this, AddProductActivity.class);

            startActivity(intent);

        });


        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {

        db.collection("Products").addSnapshotListener((value, error) -> {

            if (error != null || value == null) {
                return;
            }

            productList.clear();

            for (DocumentSnapshot doc : value.getDocuments()) {

                Product product = doc.toObject(Product.class);

                if (product != null) {

                    product.setId(doc.getId());

                    productList.add(product);

                }

            }
            adapter.updateFullList();
            adapter.notifyDataSetChanged();
        });

    }

}