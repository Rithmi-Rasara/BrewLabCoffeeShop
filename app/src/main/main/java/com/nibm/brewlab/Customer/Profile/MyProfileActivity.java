package com.nibm.brewlab.Customer.Profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.nibm.brewlab.R;

import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    EditText edtName, edtPhone;
    TextView txtEmail, txtLoyaltyPoints;
    Button btnSave;

    FirebaseAuth auth;
    DocumentReference userRef;

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

        userRef = FirebaseFirestore.getInstance().collection("Users").document(uid);

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {

        userRef.get().addOnSuccessListener(snapshot -> {

            String name = snapshot.getString("name");
            String phone = snapshot.getString("phone");
            String email = snapshot.getString("email");
            Long points = snapshot.getLong("loyaltyPoints");

            edtName.setText(name != null ? name : "");
            edtPhone.setText(phone != null ? phone : "");
            txtEmail.setText(email != null ? email : auth.getCurrentUser().getEmail());
            txtLoyaltyPoints.setText(String.valueOf(points != null ? points : 0));

        }).addOnFailureListener(e ->
                Toast.makeText(MyProfileActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void saveProfile() {

        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Enter name");
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("phone", phone);

        // merge = true so this doesn't wipe out fields Admin/Signup set
        // (role, status, loyaltyPoints, etc.)
        userRef.set(update, SetOptions.merge())
                .addOnSuccessListener(unused -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
