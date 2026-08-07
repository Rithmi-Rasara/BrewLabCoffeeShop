package com.nibm.brewlab.Customer.Feedback;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    RatingBar ratingBar;
    EditText edtComment;
    Button btnSubmit;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ratingBar = findViewById(R.id.ratingBar);
        edtComment = findViewById(R.id.edtComment);
        btnSubmit = findViewById(R.id.btnSubmitFeedback);

        auth = FirebaseAuth.getInstance();
        // Feedback is read by the admin dashboard / admin Feedback screen from
        // Firestore, so it must be written to Firestore here too (previously
        // this wrote to the Realtime Database and admin never saw it).
        firestore = FirebaseFirestore.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {

        float rating = ratingBar.getRating();
        String comment = edtComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            edtComment.setError("Please write a comment");
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        usersRef.child(uid).child("name").get().addOnSuccessListener(snapshot -> {

            String name = snapshot.getValue(String.class);
            if (name == null) name = "Customer";

            // Field names must match Admin/Feedback/Feedback.java
            // (customerName, message, rating) so the admin side can parse it.
            HashMap<String, Object> feedback = new HashMap<>();
            feedback.put("uid", uid);
            feedback.put("customerName", name);
            feedback.put("message", comment);
            feedback.put("rating", rating);
            feedback.put("timestamp", System.currentTimeMillis());

            firestore.collection("Feedback")
                    .add(feedback)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                        ratingBar.setRating(0);
                        edtComment.setText("");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
