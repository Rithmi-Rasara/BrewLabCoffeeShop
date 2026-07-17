package com.nibm.brewlab.Admin.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CategoryProductAdapter
        extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Product> list;


    public CategoryProductAdapter(Context context,
                                  ArrayList<Product> list) {

        this.context = context;
        this.list = list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {


        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_category_product,
                        parent,
                        false
                );


        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {


        Product product = list.get(position);


        holder.name.setText(
                product.getName()
        );


        holder.price.setText(
                "Rs. " + product.getPrice()
        );


        // Stock is managed from Inventory collection
        holder.stock.setText(
                "Stock : Managed in Inventory"
        );


    }


    @Override
    public int getItemCount() {

        return list.size();

    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {


        TextView name;
        TextView price;
        TextView stock;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            name = itemView.findViewById(
                    R.id.tvProductName
            );


            price = itemView.findViewById(
                    R.id.tvProductPrice
            );


            stock = itemView.findViewById(
                    R.id.tvProductStock
            );

        }
    }
}