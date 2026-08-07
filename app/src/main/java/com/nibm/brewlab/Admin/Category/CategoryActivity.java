package com.nibm.brewlab.Admin.Category;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoryAdapter adapter;

    ArrayList<CategoryModel> list = new ArrayList<>();
    ArrayList<CategoryModel> filteredList = new ArrayList<>();

    Button btnAddCategory;
    EditText edtSearch;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerView);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        edtSearch = findViewById(R.id.edtSearch);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        db = FirebaseFirestore.getInstance();

        adapter = new CategoryAdapter(
                this,
                filteredList
        );

        recyclerView.setAdapter(adapter);

        loadCategories();

        btnAddCategory.setOnClickListener(v ->
                showAddDialog()
        );

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                filterCategory(s.toString());

            }

            @Override
            public void afterTextChanged(
                    Editable s
            ) {

            }

        });

    }

    private void loadCategories() {

        db.collection("Categories")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) {
                        return;
                    }

                    list.clear();

                    for (DocumentSnapshot doc : value) {

                        CategoryModel model =
                                doc.toObject(CategoryModel.class);

                        if (model != null) {

                            model.setId(doc.getId());

                            list.add(model);

                        }

                    }

                    filteredList.clear();
                    filteredList.addAll(list);

                    adapter.notifyDataSetChanged();

                });

    }

    private void filterCategory(String text) {

        filteredList.clear();

        if (text.isEmpty()) {

            filteredList.addAll(list);

        } else {

            for (CategoryModel model : list) {

                if (model.getName()
                        .toLowerCase()
                        .contains(text.toLowerCase())) {

                    filteredList.add(model);

                }

            }

        }

        adapter.notifyDataSetChanged();

    }

    private void showAddDialog() {

        View view = LayoutInflater.from(this)
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

            if (name.isEmpty()) {

                edtName.setError(
                        "Enter category name"
                );

                return;

            }

            HashMap<String, Object> map =
                    new HashMap<>();

            map.put(
                    "name",
                    name
            );

            db.collection("Categories")
                    .add(map)

                    .addOnSuccessListener(documentReference -> {

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