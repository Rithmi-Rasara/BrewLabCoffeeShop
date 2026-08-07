package com.nibm.brewlab.Admin.Product;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    Context context;
    ArrayList<Product> productList;
    ArrayList<Product> productListFull;

    FirebaseFirestore db;

    public ProductAdapter(Context context, ArrayList<Product> productList) {

        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.name.setText(product.getName());
        holder.price.setText("Rs. " + product.getPrice());
        holder.category.setText(product.getCategory());

        String image = product.getImageUri();

        if (image != null && !image.isEmpty()) {

            Glide.with(context).load(image).placeholder(R.drawable.cappuccino).error(R.drawable.cappuccino).into(holder.productImage);

        } else {

            Glide.with(context).load(R.drawable.cappuccino).into(holder.productImage);
        }

        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context).setTitle("Delete Product").setMessage("Are you sure you want to delete this product?").setPositiveButton("Delete", (dialog, which) -> {

                db.collection("Products").document(product.getId()).delete().addOnSuccessListener(unused -> Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show());

            }).setNegativeButton("Cancel", null).show();

        });


        holder.btnUpdate.setOnClickListener(v -> {

            Intent intent = new Intent(context, UpdateProductActivity.class);

            intent.putExtra("id", product.getId());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("category", product.getCategory());
            intent.putExtra("desc", product.getDesc());
            intent.putExtra("imageUri", product.getImageUri());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, category;
        ImageView productImage;
        Button btnUpdate, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            category = itemView.findViewById(R.id.productCategory);
            productImage = itemView.findViewById(R.id.productImage);

            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void updateFullList() {

        productListFull.clear();
        productListFull.addAll(productList);

    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private final Filter productFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Product> filtered = new ArrayList<>();

            if (productListFull.isEmpty()) {
                productListFull.addAll(productList);
            }

            if (constraint == null || constraint.length() == 0) {

                filtered.addAll(productListFull);

            } else {

                String text = constraint.toString().toLowerCase().trim();

                for (Product p : productListFull) {

                    if (p.getName().toLowerCase().contains(text) || p.getCategory().toLowerCase().contains(text)) {

                        filtered.add(p);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtered;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            productList.clear();
            productList.addAll((ArrayList<Product>) results.values);
            notifyDataSetChanged();
        }
    };
}