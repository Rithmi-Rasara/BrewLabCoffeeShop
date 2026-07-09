package com.nibm.brewlab.Admin.Category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<CategoryModel> list;
    FirebaseFirestore db;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list) {
        this.context = context;
        this.list = list;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_category, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryModel model = list.get(position);

        holder.txtName.setText(model.getName());

        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(context, CategoryProductsActivity.class);
            intent.putExtra("category", model.getName());
            context.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_edit_category, null);

            EditText edtName = view.findViewById(R.id.edtCategoryName);
            Button btnUpdate = view.findViewById(R.id.btnUpdate);

            edtName.setText(model.getName());

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .create();

            btnUpdate.setOnClickListener(x -> {

                String name = edtName.getText().toString().trim();

                if (name.isEmpty()) {
                    edtName.setError("Enter category");
                    return;
                }

                db.collection("Categories")
                        .document(model.getId())
                        .update("name", name)
                        .addOnSuccessListener(unused -> {

                            model.setName(name);
                            notifyItemChanged(position);

                            Toast.makeText(context,
                                    "Updated Successfully",
                                    Toast.LENGTH_SHORT).show();

                            dialog.dismiss();

                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(context,
                                        e.getMessage(),
                                        Toast.LENGTH_SHORT).show());

            });

            dialog.show();

        });

        holder.btnDelete.setOnClickListener(v -> {

            db.collection("Categories")
                    .document(model.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(context,
                                "Category Deleted",
                                Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        Button btnEdit, btnDelete, btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtCategory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}