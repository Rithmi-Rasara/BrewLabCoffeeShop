package com.nibm.brewlab.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

    TextView txtDeliveryName, txtPendingCount;
    CardView cardAssignedOrders, cardDeliveryHistory, cardMyProfile;
    ImageView imgLogout;

    FirebaseAuth auth;
    DatabaseReference usersRef, ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtDeliveryName = findViewById(R.id.txtDeliveryName);
        txtPendingCount = findViewById(R.id.txtPendingCount);

        cardAssignedOrders = findViewById(R.id.cardAssignedOrders);
        cardDeliveryHistory = findViewById(R.id.cardDeliveryHistory);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        imgLogout = findViewById(R.id.imgLogout);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        loadDeliveryPersonName();
        loadPendingCount();

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
        loadPendingCount();
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

    private void loadPendingCount() {

        Query query = ordersRef.orderByChild("status").equalTo("Pending");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                txtPendingCount.setText(count + " order(s) waiting for pickup");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Fail silently - not critical for the dashboard summary
            }
        });
    }
}
