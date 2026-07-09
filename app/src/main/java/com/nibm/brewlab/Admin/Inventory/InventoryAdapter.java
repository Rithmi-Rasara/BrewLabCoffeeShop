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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

public class InventoryAdapter
        extends RecyclerView.Adapter<InventoryAdapter.ViewHolder>
        implements Filterable {

    private List<InventoryItem> list;
    private ArrayList<InventoryItem> fullList;


    FirebaseFirestore db;

    public InventoryAdapter(List<InventoryItem> list) {

        this.list = list;

        this.fullList = new ArrayList<>();
        this.fullList.addAll(list);

        db = FirebaseFirestore.getInstance();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        InventoryItem item = list.get(position);

        holder.tvName.setText(item.getName());

        holder.tvQty.setText(
                "Quantity : " + item.getQuantity()
        );

        if(item.getQuantity() <= 10){

            holder.tvStockStatus.setText("⚠ Low Stock");

            holder.tvStockStatus.setTextColor(
                    Color.RED
            );

            holder.itemView.setBackgroundColor(
                    Color.parseColor("#3A1717")
            );

        }
        else{

            holder.tvStockStatus.setText("✓ Available");

            holder.tvStockStatus.setTextColor(
                    Color.GREEN
            );

            holder.itemView.setBackgroundColor(
                    Color.parseColor("#120B08")
            );

        }

        holder.btnDelete.setOnClickListener(v -> {

            db.collection("Inventory")
                    .document(item.getId())
                    .delete()

                    .addOnSuccessListener(unused -> {

                        list.remove(position);

                        notifyItemRemoved(position);

                        Toast.makeText(
                                holder.itemView.getContext(),
                                "Deleted Successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                    })

                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                holder.itemView.getContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });
    }

    @Override
    public Filter getFilter() {

        return inventoryFilter;
    }

    private final Filter inventoryFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<InventoryItem> filteredList =
                    new ArrayList<>();

            if(constraint == null || constraint.length() == 0){

                filteredList.addAll(fullList);

            }
            else{

                String text =
                        constraint.toString()
                                .toLowerCase()
                                .trim();

                for(InventoryItem item : fullList){

                    if(item.getName()
                            .toLowerCase()
                            .contains(text)){

                        filteredList.add(item);

                    }
                }
            }
            FilterResults results = new FilterResults();

            results.values = filteredList;

            return results;

        }
        @Override
        protected void publishResults(
                CharSequence constraint,
                FilterResults results) {

            list.clear();

            list.addAll(
                    (ArrayList<InventoryItem>) results.values
            );

            notifyDataSetChanged();

        }
    };
    public void updateFullList(){

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

        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvItemName
            );

            tvQty = itemView.findViewById(
                    R.id.tvItemQty
            );

            tvStockStatus = itemView.findViewById(
                    R.id.tvStockStatus
            );

            btnDelete = itemView.findViewById(
                    R.id.btnDelete
            );
        }
    }
}