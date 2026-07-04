package com.nibm.brewlab.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.List;

public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.ViewHolder> {

    private List<Product> stockList;

    public LowStockAdapter(List<Product> stockList) {
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_low_stock, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = stockList.get(position);

        holder.txtProductName.setText(product.getName());

        int qty = 0;

        try {
            if (product.getStock() != null) {
                qty = Integer.parseInt(product.getStock());
            }
        } catch (Exception e) {
            qty = 0;
        }

        holder.txtStockQty.setText("Remaining Stock: " + qty);

        if (qty <= 3) {
            holder.txtStockQty.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_red_light)
            );
        } else if (qty <= 10) {
            holder.txtStockQty.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_orange_light)
            );
        } else {
            holder.txtStockQty.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_green_dark)
            );
        }
    }

    @Override
    public int getItemCount() {
        return stockList == null ? 0 : stockList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtStockQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtStockQty = itemView.findViewById(R.id.txtStockQty);
        }
    }
}