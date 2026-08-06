package com.nibm.brewlab.Customer.Orders;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.Collections;

public class MyOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtEmptyOrders;

    ArrayList<CustomerOrder> orderList;
    MyOrdersAdapter adapter;

    CollectionReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerMyOrders);
        txtEmptyOrders = findViewById(R.id.txtEmptyOrders);

        orderList = new ArrayList<>();
        adapter = new MyOrdersAdapter(this, orderList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseFirestore.getInstance().collection("Orders");

        loadOrders();
    }

    private void loadOrders() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Live listener - status/stage changes made by Admin or Delivery
        // (accepted, out for delivery, delivered) show up here instantly.
        ordersRef.whereEqualTo("userId", uid)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        Toast.makeText(MyOrdersActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (snapshot == null) return;

                    orderList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        CustomerOrder order = CustomerOrder.fromFirestore(doc);
                        orderList.add(order);

                        // The shop marks an order "Preparing" (or beyond) once
                        // they start making it. The first time this app sees
                        // that, it stamps the exact moment as the prep-start
                        // time so it can be shown on the order card.
                        boolean preparationHasStarted = order.getStatus() != null
                                && !order.getStatus().equals("Pending");

                        if (preparationHasStarted && order.getPreparationStartedAt() == 0) {
                            ordersRef.document(order.getId())
                                    .update("preparationStartedAt", System.currentTimeMillis());
                        }
                    }

                    Collections.sort(orderList,
                            (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));

                    adapter.notifyDataSetChanged();

                    txtEmptyOrders.setVisibility(orderList.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                });
    }
}
