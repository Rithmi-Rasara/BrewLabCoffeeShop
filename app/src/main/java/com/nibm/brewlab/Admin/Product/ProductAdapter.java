package com.nibm.brewlab.Admin.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        this.productListFull = new ArrayList<>(productList);
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

        // Load Product Image
        if (product.getImageUri() != null && !product.getImageUri().isEmpty()) {

            int imageResId = context.getResources().getIdentifier(
                    product.getImageUri(),
                    "drawable",
                    context.getPackageName()
            );

            if (imageResId != 0) {
                holder.productImage.setImageResource(imageResId);
            } else {
                holder.productImage.setImageResource(R.drawable.cappuccino);
            }

        } else {
            holder.productImage.setImageResource(R.drawable.cappuccino);
        }

        // Update Button
        holder.btnUpdate.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            View dialogView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_update_product, null);

            builder.setView(dialogView);

            EditText edtName = dialogView.findViewById(R.id.edtName);
            EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
            EditText edtCategory = dialogView.findViewById(R.id.edtCategory);

            Button btnSave = dialogView.findViewById(R.id.btnSave);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);

            edtName.setText(product.getName());
            edtPrice.setText(product.getPrice());
            edtCategory.setText(product.getCategory());

            AlertDialog dialog = builder.create();

            btnCancel.setOnClickListener(view -> dialog.dismiss());

            btnSave.setOnClickListener(view -> {

                product.setName(edtName.getText().toString().trim());
                product.setPrice(edtPrice.getText().toString().trim());
                product.setCategory(edtCategory.getText().toString().trim());

                notifyItemChanged(holder.getAdapterPosition());

                Toast.makeText(context,
                        "Product Updated Successfully",
                        Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            });

            dialog.show();
        });

        // Delete Button
        holder.btnDelete.setOnClickListener(v -> {

            int adapterPosition = holder.getAdapterPosition();

            if (adapterPosition != RecyclerView.NO_POSITION) {

                Product deletedProduct = productList.get(adapterPosition);

                productList.remove(adapterPosition);
                productListFull.remove(deletedProduct);

                notifyItemRemoved(adapterPosition);

                Toast.makeText(context,
                        "Product Deleted",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView name, price, category;
        Button btnUpdate, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);

            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            category = itemView.findViewById(R.id.productCategory);

            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void updateList(ArrayList<Product> newList) {

        productList.clear();
        productList.addAll(newList);

        productListFull.clear();
        productListFull.addAll(newList);

        notifyDataSetChanged();
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

                String filterPattern =
                        constraint.toString().toLowerCase().trim();

                for (Product item : productListFull) {

                    if (item.getName() != null &&
                            item.getName().toLowerCase().contains(filterPattern)) {

                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            productList.clear();
            productList.addAll((ArrayList<Product>) results.values);

            notifyDataSetChanged();
        }
    };
}