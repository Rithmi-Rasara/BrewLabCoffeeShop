package com.nibm.brewlab.Admin.Product;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.HashMap;
import java.util.Map;


public class UpdateProductActivity extends AppCompatActivity {


    EditText edtName, edtPrice, edtCategory;

    Button btnSave, btnCancel;

    FirebaseFirestore db;

    String productId;
    String imageUri;
    String desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtCategory = findViewById(R.id.edtCategory);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        db = FirebaseFirestore.getInstance();

        productId = getIntent().getStringExtra("id");

        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String category = getIntent().getStringExtra("category");

        desc = getIntent().getStringExtra("desc");
        imageUri = getIntent().getStringExtra("imageUri");

        edtName.setText(name);
        edtPrice.setText(price);
        edtCategory.setText(category);

        btnSave.setOnClickListener(v -> {

            String updateName = edtName.getText().toString().trim();
            String updatePrice = edtPrice.getText().toString().trim();
            String updateCategory = edtCategory.getText().toString().trim();

            if(updateName.isEmpty() ||
                    updatePrice.isEmpty() ||
                    updateCategory.isEmpty()){

                Toast.makeText(this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Map<String,Object> product = new HashMap<>();

            product.put("name", updateName);
            product.put("price", updatePrice);
            product.put("category", updateCategory);

            product.put("desc", desc);
            product.put("imageUri", imageUri);

            db.collection("Products")
                    .document(productId)
                    .update(product)

                    .addOnSuccessListener(unused -> {


                        Toast.makeText(this,
                                "Product Updated Successfully",
                                Toast.LENGTH_SHORT).show();


                        finish();


                    })

                    .addOnFailureListener(e -> {


                        Toast.makeText(this,
                                "Error : " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();


                    });


        });

        btnCancel.setOnClickListener(v -> {

            finish();

        });


    }
}