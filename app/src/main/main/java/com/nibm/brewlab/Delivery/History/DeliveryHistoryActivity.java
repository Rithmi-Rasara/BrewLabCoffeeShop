package com.nibm.brewlab.Delivery.History;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.Delivery.Orders.DeliveryOrder;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.Collections;

public class DeliveryHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtEmpty, imgBack;

    ArrayList<DeliveryOrder> historyList;
    DeliveryHistoryAdapter adapter;

    DatabaseReference ordersRef;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerDeliveryHistory);
        txtEmpty = findViewById(R.id.txtEmptyHistory);
        imgBack = findViewById(R.id.imgBack);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        historyList = new ArrayList<>();
        adapter = new DeliveryHistoryAdapter(this, historyList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        imgBack.setOnClickListener(v -> finish());

        loadHistory();
    }

    private void loadHistory() {

        Query query = ordersRef.orderByChild("deliveryPersonUid").equalTo(myUid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                historyList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    DeliveryOrder order = child.getValue(DeliveryOrder.class);
                    if (order == null) continue;

                    if ("Delivered".equalsIgnoreCase(order.getStatus())) {
                        order.setId(child.getKey());
                        historyList.add(order);
                    }
                }

                Collections.sort(historyList,
                        (o1, o2) -> Long.compare(o2.getDeliveredAt(), o1.getDeliveredAt()));

                adapter.notifyDataSetChanged();
                txtEmpty.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeliveryHistoryActivity.this,
                        "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
