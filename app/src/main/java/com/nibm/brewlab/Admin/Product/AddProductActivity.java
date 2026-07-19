package com.nibm.brewlab.Admin.Product;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
<<<<<<< HEAD
import android.widget.Toast;

=======
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

>>>>>>> origin/develop
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
=======
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> origin/develop
import com.nibm.brewlab.R;

public class AddProductActivity extends AppCompatActivity {

    EditText edtName, edtPrice, edtCategory, edtDesc;
    Button btnAdd, btnSelectImage;
    ImageView imgProduct;
<<<<<<< HEAD

    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
=======
    TextView txtTitle;

    Button btnUpdate;



    Uri imageUri;
    String oldImage = "";

    boolean isUpdate = false;
    String productId;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "ddsa2doh");

        try {
            MediaManager.get();
        } catch (Exception e) {
            MediaManager.init(this, config);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();
>>>>>>> origin/develop

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtCategory = findViewById(R.id.edtCategory);
        edtDesc = findViewById(R.id.edtDesc);

        btnAdd = findViewById(R.id.btnAdd);
        btnSelectImage = findViewById(R.id.btnChooseImage);
<<<<<<< HEAD

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
=======
        imgProduct = findViewById(R.id.imgProduct);
        txtTitle = findViewById(R.id.txtTitle);
        btnUpdate = findViewById(R.id.btnAdd);

        Intent intent = getIntent();

        if(intent.hasExtra("id")) {

            isUpdate = true;

            txtTitle.setText("Update Product");
            btnUpdate.setText("Update Product");

            productId = intent.getStringExtra("id");

            edtName.setText(intent.getStringExtra("name"));
            edtPrice.setText(intent.getStringExtra("price"));
            edtCategory.setText(intent.getStringExtra("category"));
            edtDesc.setText(intent.getStringExtra("desc"));

            oldImage = intent.getStringExtra("imageUri");

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

        if (name.isEmpty()) {
            edtName.setError("Enter product name");
            return;
        }

        if (price.isEmpty()) {
            edtPrice.setError("Enter price");
            return;
        }

        if (imageUri == null && !isUpdate) {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {

            MediaManager.get().upload(imageUri)
                    .unsigned("brewlab")
                    .callback(new UploadCallback() {

                        @Override
                        public void onStart(String requestId) {
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {

                            String imageUrl = resultData.get("secure_url").toString();

                            saveToFirestore(name, price, category, desc, imageUrl);

                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {

                            Toast.makeText(AddProductActivity.this,
                                    error.getDescription(),
                                    Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }

                    }).dispatch();

        } else {

            saveToFirestore(name, price, category, desc, oldImage);

        }

    }

    private void saveToFirestore(String name,
                                 String price,
                                 String category,
                                 String desc,
                                 String imageUrl) {

        Product product = new Product(
                name,
                price,
                category,
                desc,
                imageUrl
        );

        if (isUpdate) {

            db.collection("Products")
                    .document(productId)
                    .set(product)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });

        } else {

            db.collection("Products")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });

        }

    }

    private final ActivityResultLauncher<Intent> imagePicker =
>>>>>>> origin/develop
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

<<<<<<< HEAD
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {
=======
                        if(result.getResultCode()== Activity.RESULT_OK &&
                                result.getData()!=null) {
>>>>>>> origin/develop

                            imageUri = result.getData().getData();
                            imgProduct.setImageURI(imageUri);
                        }
                    });
<<<<<<< HEAD

    private void clearFields() {
        edtName.setText("");
        edtPrice.setText("");
        edtCategory.setText("");
        edtDesc.setText("");

        imgProduct.setImageResource(R.drawable.ic_image);
        imageUri = null;
    }
=======
>>>>>>> origin/develop
}