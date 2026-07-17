package com.nibm.brewlab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    Spinner spinnerRole;
    Button btnSignup;
    TextView txtLogin;

    FirebaseAuth auth;

    FirebaseFirestore firestore;

    DatabaseReference databaseReference;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.etConfirmPassword);

        spinnerRole = findViewById(R.id.spinnerRole);

        btnSignup = findViewById(R.id.btnSignup);
        txtLogin = findViewById(R.id.txtLogin);


        auth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("Users");


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);


        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }


        String[] roles = {
                "Customer",
                "Admin",
                "Delivery Person"
        };


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        roles
                );


        spinnerRole.setAdapter(adapter);



        btnSignup.setOnClickListener(v -> {


            String name =
                    edtName.getText().toString().trim();

            String email =
                    edtEmail.getText().toString().trim();

            String phone =
                    edtPhone.getText().toString().trim();

            String password =
                    edtPassword.getText().toString().trim();

            String confirmPassword =
                    edtConfirmPassword.getText().toString().trim();

            String role =
                    spinnerRole.getSelectedItem().toString();



            if(name.isEmpty()){
                edtName.setError("Enter Name");
                return;
            }


            if(email.isEmpty()){
                edtEmail.setError("Enter Email");
                return;
            }


            if(phone.isEmpty()){
                edtPhone.setError("Enter Phone");
                return;
            }


            if(password.length()<6){
                edtPassword.setError("Minimum 6 Characters");
                return;
            }


            if(!password.equals(confirmPassword)){
                edtConfirmPassword.setError("Passwords do not match");
                return;
            }



            progressDialog.show();



            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {


                        if(task.isSuccessful()){


                            String uid =
                                    auth.getCurrentUser().getUid();



                            HashMap<String,Object> user =
                                    new HashMap<>();


                            user.put("uid",uid);
                            user.put("name",name);
                            user.put("email",email);
                            user.put("phone",phone);
                            user.put("role",role);
                            user.put("status","Pending");



                            // Save Firestore
                            firestore.collection("Users")
                                    .document(uid)
                                    .set(user);



                            // Save Realtime Database
                            databaseReference
                                    .child(uid)
                                    .setValue(user)
                                    .addOnCompleteListener(dbTask -> {


                                        progressDialog.dismiss();


                                        if(dbTask.isSuccessful()){


                                            Toast.makeText(
                                                    SignupActivity.this,
                                                    "Account Created Successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();



                                            startActivity(
                                                    new Intent(
                                                            SignupActivity.this,
                                                            LoginActivity.class
                                                    )
                                            );


                                            finish();


                                        }

                                    });



                        }
                        else{


                            progressDialog.dismiss();


                            Toast.makeText(
                                    SignupActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    });


        });



        txtLogin.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            SignupActivity.this,
                            LoginActivity.class
                    )
            );

            finish();

        });


    }
}