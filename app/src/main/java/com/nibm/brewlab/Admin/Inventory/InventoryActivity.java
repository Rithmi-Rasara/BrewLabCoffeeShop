package com.nibm.brewlab.Admin.Inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    InventoryAdapter adapter;
    ArrayList<Inventory> list;
    FirebaseFirestore db;
    EditText searchInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerInventory);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        adapter = new InventoryAdapter(list);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        searchInventory = findViewById(R.id.search_inventory);

        searchInventory.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadInventory();

        FloatingActionButton btnAddInventory = findViewById(R.id.btnAddInventory);

        btnAddInventory.setOnClickListener(v -> {

            Intent intent = new Intent(InventoryActivity.this, AddInventoryActivity.class);

            startActivity(intent);
        });
    }

    private void loadInventory() {

        db.collection("Inventory").addSnapshotListener((value, error) -> {

            if (error != null || value == null) {
                return;
            }

            list.clear();

            for (DocumentSnapshot doc : value.getDocuments()) {

                String name = doc.getString("name");

                Object qtyObject = doc.get("quantity");

                int quantity = 0;

                if (qtyObject instanceof Long) {

                    quantity = ((Long) qtyObject).intValue();

                } else if (qtyObject instanceof Double) {

                    quantity = ((Double) qtyObject).intValue();

                } else if (qtyObject instanceof String) {

                    try {

                        quantity = Integer.parseInt(qtyObject.toString());

                    } catch (Exception e) {

                        quantity = 0;
                    }
                }

                Inventory item = new Inventory(doc.getId(), name, quantity);

                list.add(item);
            }

            adapter.updateFullList();
            adapter.notifyDataSetChanged();
        });
    }
}