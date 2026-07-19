package com.nibm.brewlab.Admin.Feedback;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Feedback> list;

    FeedbackAdapter adapter;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        recyclerView = findViewById(R.id.recyclerFeedback);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        adapter = new FeedbackAdapter(list);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadFeedback();
    }

    private void loadFeedback() {

        db.collection("Feedback")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null)
                        return;

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        Feedback feedback = doc.toObject(Feedback.class);

                        if (feedback != null) {

                            feedback.setId(doc.getId());

                            list.add(feedback);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}