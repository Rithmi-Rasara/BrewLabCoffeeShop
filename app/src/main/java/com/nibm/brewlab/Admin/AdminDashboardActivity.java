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
import com.nibm.brewlab.Admin.Delivery.ManageDeliveryActivity;
import com.nibm.brewlab.Admin.Inventory.InventoryActivity;
import com.nibm.brewlab.Admin.Orders.Order;
import com.nibm.brewlab.Admin.Orders.OrdersActivity;
import com.nibm.brewlab.Admin.Orders.OrdersAdapter;
import com.nibm.brewlab.Admin.Product.ManageProductsActivity;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders, recyclerStock, recyclerFeedback;

    private LinearLayout addProduct;
    private LinearLayout manageOrders;
    private LinearLayout manageCategories;
    private LinearLayout viewCustomers;
    private LinearLayout manageDelivery;
    private LinearLayout manageInventory;

    private TextView txtAdminName;

    private FirebaseAuth mAuth;
    private DatabaseReference ordersRef, stockRef, userRef;

    private ArrayList<Order> orderList;
    private ArrayList<Product> lowStockList;

    private OrdersAdapter ordersAdapter;
    private LowStockAdapter lowStockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupAdapters();

        loadAdminName();
        loadRecentOrders();
        loadLowStock();

        setupClicks();
    }

    private void initViews() {

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

        // Inventory Button
        manageInventory = findViewById(R.id.manageInventory);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerFeedback.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        lowStockList = new ArrayList<>();
    }

    private void setupAdapters() {

        ordersAdapter = new OrdersAdapter(orderList);
        lowStockAdapter = new LowStockAdapter(lowStockList);

        recyclerOrders.setAdapter(ordersAdapter);
        recyclerStock.setAdapter(lowStockAdapter);
    }

    private void loadRecentOrders() {

        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        ordersRef.limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        orderList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Order order = ds.getValue(Order.class);

                            if (order != null) {
                                orderList.add(order);
                            }
                        }

                        ordersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadLowStock() {

        stockRef = FirebaseDatabase.getInstance().getReference("Products");

        stockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lowStockList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Product product = ds.getValue(Product.class);

                    if (product != null && product.getStock() != null) {

                        try {

                            int stockQty =
                                    Integer.parseInt(product.getStock());

                            if (stockQty < 10) {
                                lowStockList.add(product);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                lowStockAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadAdminName() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            txtAdminName.setText("Administrator");
            return;
        }

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);

        userRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        String name =
                                snapshot.child("name")
                                        .getValue(String.class);

                        txtAdminName.setText(
                                (name != null && !name.isEmpty())
                                        ? name
                                        : "Administrator"
                        );
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        txtAdminName.setText("Administrator");
                    }
                });
    }

    private void setupClicks() {

        addProduct.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        ManageProductsActivity.class)));

        manageOrders.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        OrdersActivity.class)));

        manageCategories.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        CategoryActivity.class)));

        viewCustomers.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        CustomersActivity.class)));

        manageDelivery.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        ManageDeliveryActivity.class)));

        // Inventory Page
        manageInventory.setOnClickListener(v ->
                startActivity(new Intent(
                        AdminDashboardActivity.this,
                        InventoryActivity.class)));
    }
}

