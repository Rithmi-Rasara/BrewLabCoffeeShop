package com.nibm.brewlab.Admin.Loyalty;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ManageLoyaltyActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Loyalty> loyaltyList;

    LoyaltyAdapter adapter;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_loyalty);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerLoyalty);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loyaltyList = new ArrayList<>();

        adapter = new LoyaltyAdapter(
                this,
                loyaltyList
        );

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadLoyaltyCustomers();

    }

    private void loadLoyaltyCustomers(){

        db.collection("Loyalty")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    loyaltyList.clear();

                    for(DocumentSnapshot document :
                            queryDocumentSnapshots){

                        Loyalty loyalty =
                                document.toObject(Loyalty.class);

                        if(loyalty != null){

                            loyalty.setId(
                                    document.getId()
                            );

                            loyaltyList.add(loyalty);

                        }

                    }

                    adapter.notifyDataSetChanged();

                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed : " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }

}