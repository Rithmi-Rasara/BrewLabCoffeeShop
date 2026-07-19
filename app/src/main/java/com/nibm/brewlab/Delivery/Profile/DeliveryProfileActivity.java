package com.nibm.brewlab.Delivery.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.LoginActivity;
import com.nibm.brewlab.R;

public class DeliveryProfileActivity extends AppCompatActivity {

    TextView imgBack, txtEmail;
    EditText edtName, edtPhone, edtVehicleNumber;
    Button btnSave, btnLogout;

    FirebaseAuth auth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        imgBack = findViewById(R.id.imgBack);
        txtEmail = findViewById(R.id.txtProfileEmail);
        edtName = findViewById(R.id.edtProfileName);
        edtPhone = findViewById(R.id.edtProfilePhone);
        edtVehicleNumber = findViewById(R.id.edtProfileVehicle);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        imgBack.setOnClickListener(v -> finish());

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("name").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String vehicle = snapshot.child("vehicleNumber").getValue(String.class);

                edtName.setText(name != null ? name : "");
                edtPhone.setText(phone != null ? phone : "");
                edtVehicleNumber.setText(vehicle != null ? vehicle : "");
                txtEmail.setText(email != null ? email : auth.getCurrentUser().getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeliveryProfileActivity.this,
                        "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveProfile() {

        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String vehicle = edtVehicleNumber.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Enter name");
            return;
        }

        userRef.child("name").setValue(name);
        userRef.child("phone").setValue(phone);
        userRef.child("vehicleNumber").setValue(vehicle);

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
