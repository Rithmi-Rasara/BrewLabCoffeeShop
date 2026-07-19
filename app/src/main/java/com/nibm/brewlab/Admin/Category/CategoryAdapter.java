package com.nibm.brewlab.Admin.Category;

import android.content.Context;
<<<<<<< HEAD
=======
import android.content.Intent;
>>>>>>> origin/develop
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
<<<<<<< HEAD
import android.widget.ImageView;
=======
>>>>>>> origin/develop
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
=======
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> origin/develop
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<CategoryModel> list;
<<<<<<< HEAD
=======
    FirebaseFirestore db;
>>>>>>> origin/develop

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list) {
        this.context = context;
        this.list = list;
<<<<<<< HEAD
=======
        db = FirebaseFirestore.getInstance();
>>>>>>> origin/develop
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

<<<<<<< HEAD
        View view = LayoutInflater.from(parent.getContext())
=======
        View view = LayoutInflater.from(context)
>>>>>>> origin/develop
                .inflate(R.layout.item_category, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryModel model = list.get(position);

<<<<<<< HEAD
        holder.txtName.setText(model.getName() != null ? model.getName() : "");

        if (model.getImage() != 0) {
            holder.imgIcon.setImageResource(model.getImage());
        }

        holder.btnEdit.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                showEditDialog(list.get(pos), pos);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {

                list.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, list.size());

                Toast.makeText(v.getContext(),
                        "Category Deleted",
                        Toast.LENGTH_SHORT).show();
            }
        });
=======
        holder.txtName.setText(model.getName());

        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(context, CategoryProduct.class);
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

>>>>>>> origin/develop
    }

    @Override
    public int getItemCount() {
<<<<<<< HEAD
        return list != null ? list.size() : 0;
    }

    // ================= EDIT DIALOG =================
    private void showEditDialog(CategoryModel model, int position) {

        if (context == null) return;

        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_category, null);

        EditText edtName = dialogView.findViewById(R.id.edtCategoryName);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        edtName.setText(model.getName() != null ? model.getName() : "");

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnUpdate.setOnClickListener(v -> {

            String newName = edtName.getText().toString().trim();

            if (newName.isEmpty()) {
                edtName.setError("Enter name");
                return;
            }

            model.setName(newName);
            notifyItemChanged(position);

            Toast.makeText(context,
                    "Updated Successfully",
                    Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }

    // ================= VIEW HOLDER =================
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgIcon;
        Button btnEdit, btnDelete;
=======
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        Button btnEdit, btnDelete, btnView;
>>>>>>> origin/develop

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtCategory);
<<<<<<< HEAD
            imgIcon = itemView.findViewById(R.id.imgCategory);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
=======
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnView = itemView.findViewById(R.id.btnView);
>>>>>>> origin/develop
        }
    }
}