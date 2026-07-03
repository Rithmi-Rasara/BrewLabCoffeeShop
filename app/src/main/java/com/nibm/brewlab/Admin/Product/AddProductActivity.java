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

import com.nibm.brewlab.R;

public class AddProductActivity extends AppCompatActivity {

    EditText edtName, edtPrice, edtCategory, edtDesc;
    Button btnAdd, btnSelectImage;
    ImageView imgProduct;

    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtCategory = findViewById(R.id.edtCategory);
        edtDesc = findViewById(R.id.edtDesc);

        btnAdd = findViewById(R.id.btnAdd);
        btnSelectImage = findViewById(R.id.btnChooseImage);

        imgProduct = findViewById(R.id.imgProduct);

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnAdd.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String price = edtPrice.getText().toString().trim();
            String category = edtCategory.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Name & Price required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select product image", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "Product Added Successfully",
                    Toast.LENGTH_SHORT).show();

            clearFields();
        });
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {

                            imageUri = result.getData().getData();
                            imgProduct.setImageURI(imageUri);
                        }
                    });

    private void clearFields() {
        edtName.setText("");
        edtPrice.setText("");
        edtCategory.setText("");
        edtDesc.setText("");

        imgProduct.setImageResource(R.drawable.ic_image);
        imageUri = null;
    }
}