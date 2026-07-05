package com.nibm.brewlab.Admin.Inventory;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.nibm.brewlab.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddInventoryActivity extends AppCompatActivity {

    private EditText etName, etQty;
    private Button btnAdd;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etName = findViewById(R.id.etItemName);
        etQty = findViewById(R.id.etItemQty);
        btnAdd = findViewById(R.id.btnAddItem);

        databaseReference = FirebaseDatabase.getInstance().getReference("inventory");

        btnAdd.setOnClickListener(v -> addItem());
    }

    private void addItem() {

        String name = etName.getText().toString().trim();
        String qtyStr = etQty.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(qtyStr)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();

        InventoryItem item = new InventoryItem(id, name, quantity);

        databaseReference.child(id).setValue(item)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();

                    if (quantity <= 10) {
                        Toast.makeText(this, "⚠️ Low Stock!", Toast.LENGTH_LONG).show();
                    }

                    etName.setText("");
                    etQty.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
