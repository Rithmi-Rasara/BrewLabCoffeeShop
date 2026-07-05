package com.nibm.brewlab.Customer.Profile;

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
import com.nibm.brewlab.R;

public class MyProfileActivity extends AppCompatActivity {

    EditText edtName, edtPhone;
    TextView txtEmail, txtLoyaltyPoints;
    Button btnSave;

    FirebaseAuth auth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edtName = findViewById(R.id.edtProfileName);
        edtPhone = findViewById(R.id.edtProfilePhone);
        txtEmail = findViewById(R.id.txtProfileEmail);
        txtLoyaltyPoints = findViewById(R.id.txtLoyaltyPoints);
        btnSave = findViewById(R.id.btnSaveProfile);

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("name").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                Long points = snapshot.child("loyaltyPoints").getValue(Long.class);

                edtName.setText(name != null ? name : "");
                edtPhone.setText(phone != null ? phone : "");
                txtEmail.setText(email != null ? email : auth.getCurrentUser().getEmail());
                txtLoyaltyPoints.setText(String.valueOf(points != null ? points : 0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfileActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveProfile() {

        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Enter name");
            return;
        }

        userRef.child("name").setValue(name);
        userRef.child("phone").setValue(phone);

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
