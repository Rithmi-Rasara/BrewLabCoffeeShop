package com.nibm.brewlab.Admin.Category;

import android.os.Bundle;
<<<<<<< HEAD
=======
import android.text.Editable;
import android.text.TextWatcher;
>>>>>>> origin/develop
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;
=======
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.HashMap;
>>>>>>> origin/develop

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoryAdapter adapter;
<<<<<<< HEAD
    ArrayList<CategoryModel> list;

    Button btnAddCategory;
=======

    ArrayList<CategoryModel> list = new ArrayList<>();
    ArrayList<CategoryModel> filteredList = new ArrayList<>();

    Button btnAddCategory;
    EditText edtSearch;

    FirebaseFirestore db;
>>>>>>> origin/develop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerView);
        btnAddCategory = findViewById(R.id.btnAddCategory);
<<<<<<< HEAD

        list = new ArrayList<>();

        list.add(new CategoryModel("Coffee", R.drawable.ic_coffee));
        list.add(new CategoryModel("Tea", R.drawable.ic_tea));
        list.add(new CategoryModel("Cold Drinks", R.drawable.ic_cold_drink));
        list.add(new CategoryModel("Cakes", R.drawable.ic_cake));
        list.add(new CategoryModel("Snacks", R.drawable.ic_snack));
        list.add(new CategoryModel("Desserts", R.drawable.ic_desert));

        adapter = new CategoryAdapter(this, list);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        btnAddCategory.setOnClickListener(v -> showAddDialog());
=======
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

>>>>>>> origin/develop
    }

    private void showAddDialog() {

<<<<<<< HEAD
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_category, null);

        EditText edtName = dialogView.findViewById(R.id.edtCategoryName);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();


        btnCancel.setOnClickListener(v -> dialog.dismiss());


        btnSave.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();

            if (name.isEmpty()) {
                edtName.setError("Enter category name");
                return;
            }

            list.add(new CategoryModel(name, R.drawable.ic_coffee));
            adapter.notifyItemInserted(list.size() - 1);

            recyclerView.scrollToPosition(list.size() - 1);

            Toast.makeText(this,
                    "Category Added",
                    Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }
=======
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

>>>>>>> origin/develop
}