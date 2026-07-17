package com.nibm.brewlab.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.Delivery.History.DeliveryHistoryActivity;
import com.nibm.brewlab.Delivery.Orders.AssignedOrdersActivity;
import com.nibm.brewlab.Delivery.Profile.DeliveryProfileActivity;
import com.nibm.brewlab.LoginActivity;
import com.nibm.brewlab.R;

public class DeliveryDashboardActivity extends AppCompatActivity {

    TextView txtDeliveryName, txtAssignedCount, txtCompletedCount, imgLogout;
    MaterialCardView cardAssignedOrders, cardDeliveryHistory, cardMyProfile;

    FirebaseAuth auth;
    DatabaseReference usersRef, ordersRef;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtDeliveryName = findViewById(R.id.txtDeliveryName);
        txtAssignedCount = findViewById(R.id.txtAssignedCount);
        txtCompletedCount = findViewById(R.id.txtCompletedCount);

        cardAssignedOrders = findViewById(R.id.cardAssignedOrders);
        cardDeliveryHistory = findViewById(R.id.cardDeliveryHistory);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        imgLogout = findViewById(R.id.imgLogout);

        auth = FirebaseAuth.getInstance();
        myUid = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        loadDeliveryPersonName();
        loadAssignedCount();
        loadCompletedCount();

        cardAssignedOrders.setOnClickListener(v ->
                startActivity(new Intent(this, AssignedOrdersActivity.class)));

        cardDeliveryHistory.setOnClickListener(v ->
                startActivity(new Intent(this, DeliveryHistoryActivity.class)));

        cardMyProfile.setOnClickListener(v ->
                startActivity(new Intent(this, DeliveryProfileActivity.class)));

        imgLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssignedCount();
        loadCompletedCount();
    }

    private void loadDeliveryPersonName() {

        String uid = auth.getCurrentUser().getUid();

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("name").getValue(String.class);
                txtDeliveryName.setText(name != null ? name : "Delivery Person");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeliveryDashboardActivity.this,
                        "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAssignedCount() {

        Query query = ordersRef.orderByChild("deliveryPersonUid").equalTo(myUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long count = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String status = child.child("status").getValue(String.class);
                    if ("Out for Delivery".equalsIgnoreCase(status)) {
                        count++;
                    }
                }
                txtAssignedCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Fail silently - not critical for the dashboard summary
            }
        });
    }

    private void loadCompletedCount() {

        Query query = ordersRef.orderByChild("deliveryPersonUid").equalTo(myUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long count = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String status = child.child("status").getValue(String.class);
                    if ("Delivered".equalsIgnoreCase(status)) {
                        count++;
                    }
                }
                txtCompletedCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Fail silently - not critical for the dashboard summary
            }
        });
    }
}
