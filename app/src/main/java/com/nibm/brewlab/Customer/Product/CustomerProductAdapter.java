package com.nibm.brewlab.Customer.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.Customer.Cart.CartManager;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ViewHolder>
        implements Filterable {

    Context context;
    ArrayList<Product> productList;
    ArrayList<Product> productListFull;

    public CustomerProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.name.setText(product.getName());
        holder.price.setText("Rs. " + product.getPrice());
        holder.category.setText(product.getCategory());

        int imageResId = context.getResources().getIdentifier(
                product.getImageUri(), "drawable", context.getPackageName());

        if (imageResId != 0) {
            holder.image.setImageResource(imageResId);
        } else {
            holder.image.setImageResource(R.drawable.cappuccino);
        }

        holder.btnAddCart.setOnClickListener(v -> showCustomizeDialog(product));
    }

    private void showCustomizeDialog(Product product) {

        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_customize_product, null);

        TextView txtTitle = dialogView.findViewById(R.id.txtCustomizeTitle);
        RadioGroup radioSize = dialogView.findViewById(R.id.radioGroupSize);
        RadioGroup radioSugar = dialogView.findViewById(R.id.radioGroupSugar);
        CheckBox checkExtraShot = dialogView.findViewById(R.id.checkExtraShot);
        CheckBox checkWhippedCream = dialogView.findViewById(R.id.checkWhippedCream);
        CheckBox checkCinnamon = dialogView.findViewById(R.id.checkCinnamon);
        TextView txtTotal = dialogView.findViewById(R.id.txtCustomizeTotal);
        Button btnCancel = dialogView.findViewById(R.id.btnCustomizeCancel);
        Button btnAdd = dialogView.findViewById(R.id.btnCustomizeAdd);

        double basePrice;
        try {
            basePrice = Double.parseDouble(product.getPrice());
        } catch (NumberFormatException e) {
            basePrice = 0;
        }
        final double finalBasePrice = basePrice;

        txtTitle.setText("Customize " + product.getName());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Runnable updateTotal = () -> {

            double extra = 0;

            int sizeId = radioSize.getCheckedRadioButtonId();
            if (sizeId == R.id.radioSmall) extra -= 50;
            else if (sizeId == R.id.radioLarge) extra += 100;

            if (checkExtraShot.isChecked()) extra += 100;
            if (checkWhippedCream.isChecked()) extra += 80;
            if (checkCinnamon.isChecked()) extra += 30;

            txtTotal.setText("Item Total: Rs. " + String.format("%.2f", finalBasePrice + extra));
        };

        radioSize.setOnCheckedChangeListener((group, checkedId) -> updateTotal.run());
        checkExtraShot.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotal.run());
        checkWhippedCream.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotal.run());
        checkCinnamon.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotal.run());

        updateTotal.run();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {

            String size;
            double extra = 0;

            int sizeId = radioSize.getCheckedRadioButtonId();
            if (sizeId == R.id.radioSmall) {
                size = "Small";
                extra -= 50;
            } else if (sizeId == R.id.radioLarge) {
                size = "Large";
                extra += 100;
            } else {
                size = "Medium";
            }

            String sugar;
            int sugarId = radioSugar.getCheckedRadioButtonId();
            if (sugarId == R.id.radioNoSugar) sugar = "No Sugar";
            else if (sugarId == R.id.radioLessSugar) sugar = "Less Sugar";
            else if (sugarId == R.id.radioExtraSugar) sugar = "Extra Sugar";
            else sugar = "Normal Sugar";

            StringBuilder addOns = new StringBuilder();
            if (checkExtraShot.isChecked()) {
                addOns.append("Extra Shot, ");
                extra += 100;
            }
            if (checkWhippedCream.isChecked()) {
                addOns.append("Whipped Cream, ");
                extra += 80;
            }
            if (checkCinnamon.isChecked()) {
                addOns.append("Cinnamon, ");
                extra += 30;
            }

            String addOnsText = addOns.length() > 0
                    ? addOns.substring(0, addOns.length() - 2)
                    : "None";

            CartManager.addToCart(context, product, size, sugar, addOnsText, extra);

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return productList.size();
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

                String pattern = constraint.toString().toLowerCase().trim();

                for (Product item : productListFull) {
                    if ((item.getName() != null && item.getName().toLowerCase().contains(pattern)) ||
                            (item.getCategory() != null && item.getCategory().toLowerCase().contains(pattern))) {
                        filteredList.add(item);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, price, category;
        Button btnAddCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            category = itemView.findViewById(R.id.productCategory);
            btnAddCart = itemView.findViewById(R.id.btnAddCart);
        }
    }
}
