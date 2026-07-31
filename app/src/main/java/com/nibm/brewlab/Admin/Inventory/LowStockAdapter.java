package com.nibm.brewlab.Admin.Inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.Admin.Inventory.Inventory;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.ViewHolder> {

    private ArrayList<Inventory> inventoryList;

    public LowStockAdapter(ArrayList<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.low_stock_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Inventory item = inventoryList.get(position);

        holder.name.setText(item.getName());

        holder.stock.setText("Stock : " + item.getQuantity());

    }


    @Override
    public int getItemCount() {

        return inventoryList.size();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView stock;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            name = itemView.findViewById(R.id.itemName);
            stock = itemView.findViewById(R.id.itemStock);

        }
    }
}