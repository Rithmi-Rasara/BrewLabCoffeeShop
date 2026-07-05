package com.nibm.brewlab.Admin.Orders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Order> orderList;

    Button pendingOrders;
    Button doneOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.ordersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingOrders = findViewById(R.id.btnPending);
        doneOrders = findViewById(R.id.btnCompleted);

        orderList = new ArrayList<>();

        orderList.add(new Order("1001", "Kasun", "1500", "Pending"));
        orderList.add(new Order("1002", "Nimal", "2500", "Pending"));
        orderList.add(new Order("1003", "Amal", "3000", "Done"));

        OrdersAdapter adapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(adapter);

        pendingOrders.setOnClickListener(v -> {
            Intent intent = new Intent(OrdersActivity.this, PendingOrdersActivity.class);
            startActivity(intent);
        });

        doneOrders.setOnClickListener(v -> {
            Intent intent = new Intent(OrdersActivity.this, DoneOrdersActivity.class);
            startActivity(intent);
        });
    }
}