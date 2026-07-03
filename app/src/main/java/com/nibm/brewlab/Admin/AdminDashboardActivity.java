package com.nibm.brewlab.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.nibm.brewlab.Admin.Category.CategoryActivity;
import com.nibm.brewlab.Admin.Customers.CustomersActivity;
import com.nibm.brewlab.Admin.Delivery.DeliveryDetailsActivity;
import com.nibm.brewlab.Admin.Delivery.DeliveryPerson;
import com.nibm.brewlab.Admin.Delivery.ManageDeliveryActivity;
import com.nibm.brewlab.Admin.Orders.OrdersActivity;
import com.nibm.brewlab.Admin.Product.ManageProductsActivity;
import com.nibm.brewlab.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders, recyclerStock, recyclerFeedback;

    private LinearLayout addProduct,
            manageOrders,
            manageCategories,
            viewCustomers,
            manageDelivery;

    private TextView txtAdminName;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        txtAdminName = findViewById(R.id.txtAdminName);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerStock = findViewById(R.id.recyclerStock);
        recyclerFeedback = findViewById(R.id.recyclerFeedback);

        addProduct = findViewById(R.id.addProduct);
        manageOrders = findViewById(R.id.manageOrders);
        manageCategories = findViewById(R.id.manageCategories);
        viewCustomers = findViewById(R.id.viewCustomers);
        manageDelivery = findViewById(R.id.manageDelivery);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerFeedback.setLayoutManager(new LinearLayoutManager(this));

        if (mAuth.getCurrentUser() != null) {

            String uid = mAuth.getCurrentUser().getUid();

            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        String name = snapshot.child("name").getValue(String.class);

                        if (name != null && !name.isEmpty()) {
                            txtAdminName.setText(name);
                        } else {
                            txtAdminName.setText("Administrator");
                        }

                    } else {
                        txtAdminName.setText("Administrator");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        addProduct.setOnClickListener(v ->
                startActivity(new Intent(this, ManageProductsActivity.class)));

        manageOrders.setOnClickListener(v ->
                startActivity(new Intent(this, OrdersActivity.class)));

        manageCategories.setOnClickListener(v ->
                startActivity(new Intent(this, CategoryActivity.class)));

        viewCustomers.setOnClickListener(v ->
                startActivity(new Intent(this, CustomersActivity.class)));

        manageDelivery.setOnClickListener(v ->
                startActivity(new Intent(this, ManageDeliveryActivity.class)));
    }
}