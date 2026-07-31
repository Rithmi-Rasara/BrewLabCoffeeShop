package com.nibm.brewlab.Admin;

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

    private ArrayList<Inventory> stockList;


    public LowStockAdapter(ArrayList<Inventory> lowStockList) {
        this.stockList = lowStockList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_low_stock, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Inventory item = stockList.get(position);


        holder.txtProductName.setText(item.getName());


        int qty = item.getQuantity();


        holder.txtStockQty.setText("Remaining Stock: " + qty);


        if (qty <= 3) {

            holder.txtStockQty.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_light));

        } else if (qty <= 10) {

            holder.txtStockQty.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_orange_light));

        } else {

            holder.txtStockQty.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        }

    }


    @Override
    public int getItemCount() {

        return stockList.size();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView txtProductName;
        TextView txtStockQty;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            txtProductName = itemView.findViewById(R.id.txtProductName);


            txtStockQty = itemView.findViewById(R.id.txtStockQty);

        }
    }
}