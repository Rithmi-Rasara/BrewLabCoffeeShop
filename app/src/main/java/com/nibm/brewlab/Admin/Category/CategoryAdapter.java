package com.nibm.brewlab.Admin.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<CategoryModel> list;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryModel model = list.get(position);

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
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgIcon;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtCategory);
            imgIcon = itemView.findViewById(R.id.imgCategory);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}