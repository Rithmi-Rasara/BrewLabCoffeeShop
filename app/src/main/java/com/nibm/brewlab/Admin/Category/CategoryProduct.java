package com.nibm.brewlab.Admin.Category;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryProduct extends AppCompatActivity {

    TextView txtTitle;
    RecyclerView recyclerProducts;

    ArrayList<Product> productList;
    CategoryProductAdapter adapter;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_products);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtTitle = findViewById(R.id.txtTitle);

        recyclerProducts = findViewById(
                R.id.recyclerProducts
        );

        recyclerProducts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        productList = new ArrayList<>();

        adapter = new CategoryProductAdapter(
                this,
                productList
        );

        recyclerProducts.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        String category =
                getIntent().getStringExtra("category");

        txtTitle.setText(
                category + " Products"
        );

        loadProducts(category);

    }

    private void loadProducts(String category) {

        db.collection("Products")
                .whereEqualTo("category", category)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) {
                        return;
                    }

                    productList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        Product product =
                                doc.toObject(Product.class);

                        if (product != null) {

                            product.setId(
                                    doc.getId()
                            );

                            productList.add(product);

                        }

                    }

                    adapter.notifyDataSetChanged();

                });

    }

}