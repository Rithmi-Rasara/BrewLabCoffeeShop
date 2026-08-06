package com.nibm.brewlab.Admin.Orders;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Order> orderList;
    OrdersAdapter ordersAdapter;
    Button allOrders, pendingOrders, doneOrders;
    DatabaseReference ordersRef;
    String currentFilter = "ALL";
    ValueEventListener activeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.ordersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allOrders = findViewById(R.id.btnAll);
        pendingOrders = findViewById(R.id.btnPending);
        doneOrders = findViewById(R.id.btnComplete);

        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(ordersAdapter);

        // Orders live in Realtime Database - same place Customer/Delivery use.
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        loadOrders("ALL");

        allOrders.setOnClickListener(v -> loadOrders("ALL"));
        pendingOrders.setOnClickListener(v -> loadOrders("Pending"));
        doneOrders.setOnClickListener(v -> loadOrders("Delivered"));
    }

    private void loadOrders(String filter) {

        currentFilter = filter;

        if (activeListener != null) {
            ordersRef.removeEventListener(activeListener);
        }

        activeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    Order order = OrderMapper.fromSnapshot(child);
                    String status = order.getOrderStatus();

                    if ("ALL".equals(currentFilter)) {

                        orderList.add(order);

                    } else if ("Pending".equals(currentFilter)) {

                        if (status != null && status.equalsIgnoreCase("Pending")) {
                            orderList.add(order);
                        }

                    } else if ("Delivered".equals(currentFilter)) {

                        if (status != null && status.equalsIgnoreCase("Delivered")) {
                            orderList.add(order);
                        }
                    }
                }

                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        ordersRef.addValueEventListener(activeListener);
    }
}
