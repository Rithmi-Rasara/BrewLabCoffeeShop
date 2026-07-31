package com.nibm.brewlab.Admin.Inventory;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.nibm.brewlab.Admin.Inventory.AddInventoryActivity;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> implements Filterable {

    private List<Inventory> list;
    private ArrayList<Inventory> fullList;


    FirebaseFirestore db;

    public InventoryAdapter(List<Inventory> list) {

        this.list = list;

        this.fullList = new ArrayList<>();
        this.fullList.addAll(list);

        db = FirebaseFirestore.getInstance();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Inventory item = list.get(position);

        holder.tvName.setText(item.getName());
        holder.tvQty.setText("Quantity : " + item.getQuantity());

        if (item.getQuantity() <= 5) {

            holder.tvStockStatus.setText("⚠ Low Stock");
            holder.tvStockStatus.setTextColor(Color.RED);

        } else if (item.getQuantity() <= 10) {

            holder.tvStockStatus.setText("⚠ Medium Stock");
            holder.tvStockStatus.setTextColor(Color.parseColor("#FFA500"));

        } else {

            holder.tvStockStatus.setText("✓ Available");
            holder.tvStockStatus.setTextColor(Color.parseColor("#4CAF50"));
        }

        holder.btnUpdate.setOnClickListener(v -> {

            Intent intent = new Intent(holder.itemView.getContext(), AddInventoryActivity.class);

            intent.putExtra("id", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("quantity", String.valueOf(item.getQuantity()));

            holder.itemView.getContext().startActivity(intent);

        });

        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(holder.itemView.getContext()).setTitle("Delete Inventory").setMessage("Delete " + item.getName() + " ?").setPositiveButton("Delete", (dialog, which) -> {

                db.collection("Inventory").document(item.getId()).delete().addOnSuccessListener(unused -> {

                    int currentPosition = holder.getAdapterPosition();

                    if (currentPosition != RecyclerView.NO_POSITION) {

                        list.remove(currentPosition);

                        notifyItemRemoved(currentPosition);

                    }

                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e ->

                        Toast.makeText(holder.itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());

            }).setNegativeButton("Cancel", null).show();

        });

    }

    @Override
    public Filter getFilter() {

        return inventoryFilter;
    }

    private final Filter inventoryFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Inventory> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {

                filteredList.addAll(fullList);

            } else {

                String text = constraint.toString().toLowerCase().trim();

                for (Inventory item : fullList) {

                    if (item.getName().toLowerCase().contains(text) || String.valueOf(item.getQuantity()).contains(text)) {

                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            list.clear();

            if (results.values != null) {
                list.addAll((ArrayList<Inventory>) results.values);
            }

            notifyDataSetChanged();
        }
    };

    public void updateFullList() {

        fullList.clear();

        fullList.addAll(list);
    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQty;
        TextView tvStockStatus;

        Button btnUpdate, btnDelete;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvName = itemView.findViewById(R.id.tvItemName);

            tvQty = itemView.findViewById(R.id.tvItemQty);

            tvStockStatus = itemView.findViewById(R.id.tvStockStatus);

            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}