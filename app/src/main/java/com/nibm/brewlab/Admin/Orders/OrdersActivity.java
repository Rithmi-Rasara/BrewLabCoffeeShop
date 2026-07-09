package com.nibm.brewlab.Admin.Orders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Order> orderList;

    Button pendingOrders, doneOrders;

    OrdersAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        recyclerView = findViewById(R.id.ordersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingOrders = findViewById(R.id.btnPending);
        doneOrders = findViewById(R.id.btnCompleted);

        orderList = new ArrayList<>();
        adapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadOrders();

        pendingOrders.setOnClickListener(v ->
                startActivity(new Intent(this, PendingOrdersActivity.class)));

        doneOrders.setOnClickListener(v ->
                startActivity(new Intent(this, DoneOrdersActivity.class)));
    }

    private void loadOrders() {

        db.collection("Orders")
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    orderList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        Order order = doc.toObject(Order.class);

                        if (order != null) {
                            orderList.add(order);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}