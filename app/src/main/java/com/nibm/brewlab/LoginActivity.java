package com.nibm.brewlab;
import com.nibm.brewlab.Delivery.DeliveryDashboardActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nibm.brewlab.Admin.AdminDashboardActivity;
import com.nibm.brewlab.Admin.Customers.CustomersActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtSignup;
    private ProgressBar loginLoader;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignup = findViewById(R.id.txtSignup);
        loginLoader = findViewById(R.id.loginLoader);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnLogin.setOnClickListener(v -> loginUser());

        txtSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void loginUser() {

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Enter Email");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Enter Password");
            edtPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("");
        loginLoader.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = auth.getCurrentUser().getUid();

                    databaseReference.child(uid).get()
                            .addOnSuccessListener(snapshot -> {

                                loginLoader.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                btnLogin.setText("Login");

                                if (snapshot.exists()) {

                                    String role = snapshot.child("role").getValue(String.class);

                                    Toast.makeText(LoginActivity.this,
                                            "Login Successful",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = null;

                                    if (role == null) {
                                        Toast.makeText(LoginActivity.this,
                                                "Role not found!",
                                                Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    switch (role.toLowerCase()) {

                                        case "admin":
                                            intent = new Intent(LoginActivity.this,
                                                    AdminDashboardActivity.class);
                                            break;

                                        case "customer":
                                            intent = new Intent(LoginActivity.this,
                                                    CustomersActivity.class);
                                            break;

                                        case "delivery":
                                        case "delivery person":
                                            intent = new Intent(LoginActivity.this,
                                                    DeliveryDashboardActivity.class);
                                            break;

                                        default:
                                            Toast.makeText(LoginActivity.this,
                                                    "Invalid role: " + role,
                                                    Toast.LENGTH_LONG).show();
                                            return;
                                    }

                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "User data not found",
                                            Toast.LENGTH_LONG).show();
                                }

                            })
                            .addOnFailureListener(e -> {

                                loginLoader.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                btnLogin.setText("Login");

                                Log.e("DATABASE_ERROR", e.getMessage(), e);

                                Toast.makeText(LoginActivity.this,
                                        "Database error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });

                })
                .addOnFailureListener(e -> {

                    loginLoader.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");

                    Log.e("LOGIN_ERROR", e.getMessage(), e);

                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}