package com.nibm.brewlab;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeliveryProfileActivity extends AppCompatActivity {

    TextView txtProfileInitial, txtProfileName, txtProfileEmail, txtProfilePhone, txtProfileVehicle;
    android.widget.ImageButton btnBack;

    DatabaseReference usersRef;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtProfileInitial = findViewById(R.id.txtProfileInitial);
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtProfilePhone = findViewById(R.id.txtProfilePhone);
        txtProfileVehicle = findViewById(R.id.txtProfileVehicle);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadProfile();
    }

    private void loadProfile() {
        if (uid == null) return;
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String vehicle = snapshot.child("vehicleNumber").getValue(String.class);

                if (name != null) {
                    txtProfileName.setText(name);
                    txtProfileInitial.setText(name.substring(0, 1).toUpperCase());
                }
                if (email != null) txtProfileEmail.setText(email);
                if (phone != null) txtProfilePhone.setText(phone);
                txtProfileVehicle.setText(vehicle != null ? vehicle : "Not set");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
