package com.nibm.brewlab.Delivery.Orders;

import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AssignedOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtEmpty;
    TextView imgBack;

    ArrayList<DeliveryOrder> orderList;
    AssignedOrdersAdapter adapter;

    DatabaseReference ordersRef;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerAssignedOrders);
        txtEmpty = findViewById(R.id.txtEmptyAssignedOrders);
        imgBack = findViewById(R.id.imgBack);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        orderList = new ArrayList<>();
        adapter = new AssignedOrdersAdapter(this, orderList, myUid);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        imgBack.setOnClickListener(v -> finish());

        loadMyName();
        loadOrders();
    }

    private void loadMyName() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(myUid).child("name").get().addOnSuccessListener(snapshot -> {
                    String name = snapshot.getValue(String.class);
                    if (name != null) adapter.setMyName(name);
                });
    }
    private void loadOrders() {

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    DeliveryOrder order;
                    try {
                        order = child.getValue(DeliveryOrder.class);
                    } catch (Exception e) {

                        continue;
                    }
                    if (order == null) continue;

                    order.setId(child.getKey());
                    String status = order.getStatus();

                    // NOTE: "Pending" = just placed by customer, still
                    // waiting for admin approval - should NOT show here yet.
                    // Only "Preparing" (admin approved it) is open for any
                    // delivery person to accept.
                    boolean isNewRequest = "Preparing".equalsIgnoreCase(status);

                    boolean isMyActiveDelivery = "Out for Delivery".equalsIgnoreCase(status)
                            && myUid.equals(order.getDeliveryPersonUid());

                    if (isNewRequest || isMyActiveDelivery) {
                        orderList.add(order);
                    }
                }
                Collections.sort(orderList, (o1, o2) -> {
                    boolean o1Active = "Out for Delivery".equalsIgnoreCase(o1.getStatus());
                    boolean o2Active = "Out for Delivery".equalsIgnoreCase(o2.getStatus());
                    if (o1Active != o2Active) {
                        return o1Active ? -1 : 1;
                    }
                    return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                });

                adapter.notifyDataSetChanged();
                txtEmpty.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignedOrdersActivity.this,
                        "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}