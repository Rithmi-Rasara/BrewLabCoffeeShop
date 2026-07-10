package com.nibm.brewlab.Admin.Category;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Product> productList;
    private CategoryProductAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new CategoryProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        String category = getIntent().getStringExtra("category");

        if (category != null) {
            loadProducts(category);
        }
    }

    private void loadProducts(String category) {

        db.collection("Products")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    productList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        Product product = doc.toObject(Product.class);
                        product.setId(doc.getId());

                        productList.add(product);
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    // Optional: Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }
}