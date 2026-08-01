package com.nibm.brewlab.Customer.Feedback;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    RatingBar ratingBar;
    EditText edtComment;
    Button btnSubmit;

    FirebaseAuth auth;
    CollectionReference feedbackRef;
    DocumentReference userRef;

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
        feedbackRef = FirebaseFirestore.getInstance().collection("Feedback");
        userRef = FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());

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

        userRef.get().addOnSuccessListener(snapshot -> {

            String name = snapshot.getString("name");
            if (name == null) name = "Customer";

            HashMap<String, Object> feedback = new HashMap<>();
            feedback.put("uid", uid);
            feedback.put("customerName", name);
            feedback.put("rating", rating);
            feedback.put("comment", comment);
            feedback.put("timestamp", System.currentTimeMillis());

            feedbackRef.add(feedback);

            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();

            ratingBar.setRating(0);
            edtComment.setText("");

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
