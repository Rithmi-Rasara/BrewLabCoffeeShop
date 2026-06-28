package com.nibm.brewlab;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtSignup;
    ProgressBar loginLoader;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

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

        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty()) {
                edtEmail.setError("Enter Email");
                return;
            }

            if (password.isEmpty()) {
                edtPassword.setError("Enter Password");
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("");
            loginLoader.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        loginLoader.setVisibility(View.GONE);
                        btnLogin.setText("Login");
                        btnLogin.setEnabled(true);

                        if (task.isSuccessful()) {

                            String uid = auth.getCurrentUser().getUid();

                            databaseReference.child(uid)
                                    .get()
                                    .addOnCompleteListener(dataTask -> {

                                        if (dataTask.isSuccessful() && dataTask.getResult().exists()) {

                                            String role = dataTask.getResult()
                                                    .child("role")
                                                    .getValue(String.class);

                                            Toast.makeText(LoginActivity.this,
                                                    "Login Successful",
                                                    Toast.LENGTH_SHORT).show();

                                            Intent intent;

                                            if (role != null && role.equals("admin")) {
                                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            } else {
                                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            }

                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "User data not found",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(LoginActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        txtSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }
}