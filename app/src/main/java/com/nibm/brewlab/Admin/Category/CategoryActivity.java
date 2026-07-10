package com.nibm.brewlab.Admin.Category;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    CategoryAdapter adapter;

    ArrayList<CategoryModel> list;

    Button btnAddCategory;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerView);

        btnAddCategory = findViewById(R.id.btnAddCategory);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();

        adapter = new CategoryAdapter(
                this,
                list
        );

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadCategories();

        btnAddCategory.setOnClickListener(v -> {

            showAddDialog();

        });
    }

    private void loadCategories(){

        db.collection("Categories")

                .addSnapshotListener((value,error)->{
                    if(error != null || value == null){

                        return;

                    }

                    list.clear();

                    for(DocumentSnapshot doc:value.getDocuments()){

                        CategoryModel model =
                                doc.toObject(CategoryModel.class);

                        if(model != null){


                            model.setId(
                                    doc.getId()
                            );


                            list.add(model);

                        }
                    }

                    adapter.notifyDataSetChanged();

                });
    }

    private void showAddDialog(){

        View view =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_add_category,
                                null
                        );

        EditText edtName =
                view.findViewById(
                        R.id.edtCategoryName
                );

        Button btnSave =
                view.findViewById(
                        R.id.btnSave
                );

        Button btnCancel =
                view.findViewById(
                        R.id.btnCancel
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .create();

        btnCancel.setOnClickListener(v ->
                dialog.dismiss()
        );


        btnSave.setOnClickListener(v -> {

            String name =
                    edtName.getText()
                            .toString()
                            .trim();

            if(name.isEmpty()){

                edtName.setError(
                        "Enter category name"
                );

                return;

            }

            String id =
                    db.collection("Categories")
                            .document()
                            .getId();

            CategoryModel category =
                    new CategoryModel(
                            id,
                            name
                    );


            db.collection("Categories")
                    .document(id)
                    .set(category)

                    .addOnSuccessListener(unused -> {


                        Toast.makeText(
                                this,
                                "Category Added",
                                Toast.LENGTH_SHORT
                        ).show();



                        dialog.dismiss();


                    })


                    .addOnFailureListener(e -> {


                        Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();


                    });



        });



        dialog.show();


    }



}