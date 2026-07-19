package com.nibm.brewlab.Admin.Orders;

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
    OrdersAdapter ordersAdapter;
    Button allOrders, pendingOrders, doneOrders;
    FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

        loadOrders("ALL");

        allOrders.setOnClickListener(v -> loadOrders("ALL"));
        pendingOrders.setOnClickListener(v -> loadOrders("Pending"));
        doneOrders.setOnClickListener(v -> loadOrders("Completed"));
    }

    private void loadOrders(String filter) {

        db.collection("Orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    orderList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                        Order order = new Order();

                        order.setId(doc.getId());
                        order.setUserId(doc.getString("userId"));
                        order.setDeliveryAddress(doc.getString("address"));
                        order.setPaymentMethod(doc.getString("payment"));
                        order.setOrderStatus(doc.getString("status"));

                        Number total = doc.getDouble("total");
                        if (total == null) {
                            total = doc.getLong("total");
                        }

                        if (total != null) {
                            order.setTotalAmount(total.doubleValue());
                        }

                        // තාවකාලිකව Customer Name වෙනුවට User ID දානවා
                        order.setCustomerName(doc.getString("userId"));

                        String status = order.getOrderStatus();

                        if ("ALL".equals(filter)) {

                            orderList.add(order);

                        } else if ("Pending".equals(filter)) {

                            if (status != null && status.equalsIgnoreCase("pending")) {
                                orderList.add(order);
                            }

                        } else if ("Completed".equals(filter)) {

                            if (status != null && status.equalsIgnoreCase("completed")) {
                                orderList.add(order);
                            }
                        }
                    }

                    ordersAdapter.notifyDataSetChanged();
                });
    }
}