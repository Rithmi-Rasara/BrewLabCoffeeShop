package com.nibm.brewlab.Admin.Product;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

public class UpdateProductActivity extends AppCompatActivity {

    TextView txtTitle;
    EditText edtName, edtPrice, edtCategory, edtDesc;
    Button btnUpdate, btnChooseImage;
    ImageView imgProduct;

    FirebaseFirestore db;

    Uri imageUri;
    String productId;
    String oldImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();

        txtTitle = findViewById(R.id.txtTitle);

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtCategory = findViewById(R.id.edtCategory);
        edtDesc = findViewById(R.id.edtDesc);

        btnUpdate = findViewById(R.id.btnAdd);
        btnChooseImage = findViewById(R.id.btnChooseImage);

        imgProduct = findViewById(R.id.imgProduct);

        txtTitle.setText("Update Product");
        btnUpdate.setText("Update Product");


        Intent intent = getIntent();

        productId = intent.getStringExtra("id");

        edtName.setText(intent.getStringExtra("name"));
        edtPrice.setText(intent.getStringExtra("price"));
        edtCategory.setText(intent.getStringExtra("category"));
        edtDesc.setText(intent.getStringExtra("desc"));

        oldImage = intent.getStringExtra("imageUri");

        if(oldImage != null && !oldImage.isEmpty()){

            imageUri = Uri.parse(oldImage);
            imgProduct.setImageURI(imageUri);

        }


        btnChooseImage.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            imagePicker.launch(i);

        });


        btnUpdate.setOnClickListener(v -> updateProduct());

    }


    private void updateProduct(){

        String name = edtName.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();


        if(name.isEmpty()){

            edtName.setError("Enter product name");
            return;

        }


        if(price.isEmpty()){

            edtPrice.setError("Enter price");
            return;

        }


        String image = imageUri != null
                ? imageUri.toString()
                : oldImage;


        Product product = new Product(
                name,
                price,
                category,
                desc,
                image
        );


        db.collection("Products")
                .document(productId)
                .set(product)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Updated Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }


    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if(result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null){

                            imageUri = result.getData().getData();

                            imgProduct.setImageURI(imageUri);

                        }

                    });
}