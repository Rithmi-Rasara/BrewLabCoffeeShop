package com.nibm.brewlab.Admin.Product;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

public class AddProductActivity extends AppCompatActivity {

    EditText edtName, edtPrice, edtCategory, edtDesc;
    Button btnAdd, btnSelectImage;
    ImageView imgProduct;

    Uri imageUri;
    String oldImage = "";

    boolean isUpdate = false;
    String productId;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtCategory = findViewById(R.id.edtCategory);
        edtDesc = findViewById(R.id.edtDesc);

        btnAdd = findViewById(R.id.btnAdd);
        btnSelectImage = findViewById(R.id.btnChooseImage);
        imgProduct = findViewById(R.id.imgProduct);

        Intent intent = getIntent();

        if (intent.hasExtra("id")) {

            isUpdate = true;
            btnAdd.setText("Update Product");

            productId = intent.getStringExtra("id");

            edtName.setText(intent.getStringExtra("name"));
            edtPrice.setText(intent.getStringExtra("price"));
            edtCategory.setText(intent.getStringExtra("category"));
            edtDesc.setText(intent.getStringExtra("desc"));
            oldImage = intent.getStringExtra("imageUri");

            if (oldImage != null && !oldImage.isEmpty()) {
                imageUri = Uri.parse(oldImage);
                imgProduct.setImageURI(imageUri);
            }
        }

        btnSelectImage.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            imagePicker.launch(i);
        });

        btnAdd.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {

        String name = edtName.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Name & Price required", Toast.LENGTH_SHORT).show();
            return;
        }

        String image;

        if (imageUri != null) {
            image = imageUri.toString();
        } else {
            image = oldImage;
        }

        Product product = new Product(
                name,
                price,
                category,
                desc,
                image,
                "0"
        );

        if (isUpdate) {

            db.collection("Products")
                    .document(productId)
                    .set(product)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

        } else {

            db.collection("Products")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {

                            imageUri = result.getData().getData();
                            imgProduct.setImageURI(imageUri);
                        }
                    });
}