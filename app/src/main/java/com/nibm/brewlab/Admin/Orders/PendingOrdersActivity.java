package com.nibm.brewlab.Admin.Orders;

import android.os.Bundle;

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

public class PendingOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    OrdersAdapter adapter;
    ArrayList<Order> orderList;
    DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        recyclerView = findViewById(R.id.recyclerPending);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(adapter);

        // NOTE: Orders live in Realtime Database (same place Customer places
        // them and Delivery reads them from) - NOT Firestore. Admin used to
        // query Firestore, so new customer orders never showed up here.
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        loadPendingOrders();
    }

    private void loadPendingOrders() {

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    String status = child.child("status").getValue(String.class);

                    // Only orders waiting for admin approval
                    if (status == null || !status.equalsIgnoreCase("Pending")) continue;

                    orderList.add(OrderMapper.fromSnapshot(child));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
