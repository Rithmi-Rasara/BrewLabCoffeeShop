package com.nibm.brewlab.Customer.Orders;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtEmptyOrders;

    ArrayList<CustomerOrder> orderList;
    MyOrdersAdapter adapter;

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

        loadOrders();
    }

    private void loadOrders() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = ordersRef.orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    CustomerOrder order = child.getValue(CustomerOrder.class);
                    if (order != null) {
                        order.setId(child.getKey());
                        orderList.add(order);
<<<<<<< HEAD

                        // The shop marks an order "Preparing" (or beyond) once
                        // they start making it. The first time this app sees
                        // that, it stamps the exact moment as the prep-start
                        // time so it can be shown on the order card.
                        boolean preparationHasStarted = order.getStatus() != null
                                && !order.getStatus().equals("Pending");

                        if (preparationHasStarted && order.getPreparationStartedAt() == 0) {
                            ordersRef.child(order.getId())
                                    .child("preparationStartedAt")
                                    .setValue(System.currentTimeMillis());
                        }
=======
>>>>>>> origin/develop
                    }
                }

                Collections.sort(orderList,
                        (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));

                adapter.notifyDataSetChanged();

                txtEmptyOrders.setVisibility(orderList.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyOrdersActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
