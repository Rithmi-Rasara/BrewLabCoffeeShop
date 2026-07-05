package com.nibm.brewlab.Admin.Product;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>
        implements Filterable {

    Context context;
    ArrayList<Product> productList;
    ArrayList<Product> productListFull;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;

        this.productListFull = new ArrayList<>();
        this.productListFull.addAll(productList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.name.setText(product.getName());
        holder.price.setText("Rs. " + product.getPrice());
        holder.category.setText(product.getCategory());

        String imageName = product.getImageUri();

        int imageResId = context.getResources().getIdentifier(
                imageName,
                "drawable",
                context.getPackageName()
        );

        if (imageResId != 0) {
            holder.productImage.setImageResource(imageResId);
        } else {
            holder.productImage.setImageResource(R.drawable.cappuccino);
        }

        holder.btnDelete.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {

                Product removed = productList.get(pos);

                productList.remove(pos);
                productListFull.remove(removed);

                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, productList.size());

                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnUpdate.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {

                Product p = productList.get(pos);

                Intent intent = new Intent(context, AddProductActivity.class);

                intent.putExtra("id", p.getId());
                intent.putExtra("name", p.getName());
                intent.putExtra("price", p.getPrice());
                intent.putExtra("category", p.getCategory());
                intent.putExtra("desc", p.getDesc());

                context.startActivity(intent);
            }
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

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private final Filter productFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Product> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productListFull);
            } else {

                String filter = constraint.toString().toLowerCase().trim();

                for (Product p : productListFull) {
                    if (p.getName().toLowerCase().contains(filter)) {
                        filteredList.add(p);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
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