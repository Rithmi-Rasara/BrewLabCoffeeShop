package com.nibm.brewlab.Admin.Inventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> list;

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

        list.add(new InventoryItem("1", "Coffee Beans", 50));
        list.add(new InventoryItem("2", "Milk", 8));
        list.add(new InventoryItem("3", "Sugar", 25));
        list.add(new InventoryItem("4", "Chocolate Syrup", 5));
        list.add(new InventoryItem("5", "Paper Cups", 100));

        adapter = new InventoryAdapter(list);
        recyclerView.setAdapter(adapter);

        FloatingActionButton btnAddInventory =
                findViewById(R.id.btnAddInventory);

        btnAddInventory.setOnClickListener(v ->
                startActivity(new Intent(
                        InventoryActivity.this,
                        AddInventoryActivity.class
                )));
    }
}