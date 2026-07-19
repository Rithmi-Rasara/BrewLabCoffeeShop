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
<<<<<<< HEAD
=======
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> origin/develop
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ManageProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText searchBox;
    FloatingActionButton addBtn;

    ArrayList<Product> productList;
    ProductAdapter adapter;

<<<<<<< HEAD
=======
    FirebaseFirestore db;

>>>>>>> origin/develop
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

<<<<<<< HEAD
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();

        productList.add(new Product("Cappuccino", "850.00", "Hot Coffee", "Fresh Cappuccino", "cappuccino"));
        productList.add(new Product("Latte", "900.00", "Hot Coffee", "Creamy Latte", "latte"));
        productList.add(new Product("Espresso", "700.00", "Hot Coffee", "Strong Espresso", "espresso"));
        productList.add(new Product("Mocha", "950.00", "Hot Coffee", "Chocolate Mocha", "mocha"));
        productList.add(new Product("Americano", "750.00", "Hot Coffee", "Classic Americano", "americano"));
        productList.add(new Product("Cold Brew", "800.00", "Cold Coffee", "Refreshing Cold Brew", "cold_brew"));
        productList.add(new Product("Iced Latte", "950.00", "Cold Coffee", "Iced Latte", "iced_latte"));
        productList.add(new Product("Caramel Frappé", "1200.00", "Frappé", "Caramel Frappé", "caramel_frappe"));

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
=======
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        productList = new ArrayList<>();

        adapter = new ProductAdapter(
                this,
                productList
        );

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadProducts();

        addBtn.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageProductsActivity.this,
                    AddProductActivity.class
            );

            startActivity(intent);

        });

        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                adapter.getFilter()
                        .filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    private void loadProducts() {

        db.collection("Products")
                .addSnapshotListener((value, error) -> {

                    if(error != null || value == null){
                        return;
                    }

                    productList.clear();

                    for(DocumentSnapshot doc : value.getDocuments()){

                        Product product =
                                doc.toObject(Product.class);

                        if(product != null){

                            product.setId(
                                    doc.getId()
                            );

                            productList.add(product);

                        }

                    }
                    adapter.updateFullList();
                    adapter.notifyDataSetChanged();
                });

    }

>>>>>>> origin/develop
}