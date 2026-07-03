package com.nibm.brewlab.Admin.Category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoryAdapter adapter;
    ArrayList<CategoryModel> list;

    Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerView);
        btnAddCategory = findViewById(R.id.btnAddCategory);

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
    }

    private void showAddDialog() {

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
}