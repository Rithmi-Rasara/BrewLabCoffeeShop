package com.nibm.brewlab.Admin.Inventory;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.HashMap;
import java.util.Map;

public class AddInventoryActivity extends AppCompatActivity {

    private EditText etName, etQty;
    private Button btnAdd;
    private boolean isUpdate = false;
    private String documentId = "";

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_inventory);

        if(getSupportActionBar()!=null){

            getSupportActionBar().hide();

        }

        etName = findViewById(R.id.etItemName);
        etQty = findViewById(R.id.etItemQty);

        btnAdd = findViewById(R.id.btnAddItem);

        db = FirebaseFirestore.getInstance();

        if (getIntent().hasExtra("id")) {

            isUpdate = true;
            documentId = getIntent().getStringExtra("id");

            etName.setText(getIntent().getStringExtra("name"));
            etQty.setText(getIntent().getStringExtra("quantity"));

            btnAdd.setText("Update Item");
        }

        btnAdd.setOnClickListener(v -> addItem());

    }
    private void addItem() {

        String name = etName.getText().toString().trim();
        String qtyText = etQty.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(qtyText)) {

            Toast.makeText(this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;

        try {

            quantity = Integer.parseInt(qtyText);

        } catch (Exception e) {

            Toast.makeText(this,
                    "Invalid Quantity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("quantity", quantity);

        if (isUpdate) {

            db.collection("Inventory")
                    .document(documentId)
                    .update(item)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Inventory Updated",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                    })
                    .addOnFailureListener(e ->

                            Toast.makeText(
                                    this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show());

        } else {

            db.collection("Inventory")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                this,
                                "Inventory Item Added",
                                Toast.LENGTH_SHORT
                        ).show();

                        if (quantity <= 10) {

                            Toast.makeText(
                                    this,
                                    "⚠ Low Stock Alert!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                        finish();

                    })
                    .addOnFailureListener(e ->

                            Toast.makeText(
                                    this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show());
        }
    }
}