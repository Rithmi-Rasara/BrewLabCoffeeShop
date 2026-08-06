package com.nibm.brewlab.Admin.Orders;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nibm.brewlab.R;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class PendingOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    OrdersAdapter adapter;
    ArrayList<Order> orderList;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerPending);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("Orders");

        ref.orderByChild("status").equalTo("Pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        orderList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Order order = ds.getValue(Order.class);
                            orderList.add(order);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }
}