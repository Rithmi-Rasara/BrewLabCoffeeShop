package com.nibm.brewlab.Customer.Cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.Set;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface CartActionListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
        void onSelectionChanged();
    }

    Context context;
    ArrayList<CartItem> cartList;
    CartActionListener listener;
    Set<String> selectedKeys;

    public CartAdapter(Context context, ArrayList<CartItem> cartList, Set<String> selectedKeys, CartActionListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.selectedKeys = selectedKeys;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartItem item = cartList.get(position);

        holder.name.setText(item.getName());
        holder.price.setText("Rs. " + item.getPrice());
        holder.qty.setText(String.valueOf(item.getQuantity()));

        StringBuilder custom = new StringBuilder();
        if (item.getSize() != null) custom.append(item.getSize());
        if (item.getSugarLevel() != null) custom.append(" | ").append(item.getSugarLevel());
        if (item.getAddOns() != null && !item.getAddOns().equalsIgnoreCase("None")) {
            custom.append(" | +").append(item.getAddOns());
        }
        holder.customization.setText(custom.toString());

        int imageResId = context.getResources().getIdentifier(
                item.getImageUri(), "drawable", context.getPackageName());

        if (imageResId != 0) {
            holder.image.setImageResource(imageResId);
        } else {
            holder.image.setImageResource(R.drawable.cappuccino);
        }

        // Clear the listener before setChecked so recycled views don't
        // fire a stale onCheckedChanged while RecyclerView rebinds them.
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.getCartKey() != null && selectedKeys.contains(item.getCartKey()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (item.getCartKey() == null) return;

            if (isChecked) {
                selectedKeys.add(item.getCartKey());
            } else {
                selectedKeys.remove(item.getCartKey());
            }

            listener.onSelectionChanged();
        });

        holder.btnPlus.setOnClickListener(v -> listener.onIncrease(item));
        holder.btnMinus.setOnClickListener(v -> listener.onDecrease(item));
        holder.btnRemove.setOnClickListener(v -> {
            if (item.getCartKey() != null) selectedKeys.remove(item.getCartKey());
            listener.onRemove(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        ImageView image;
        TextView name, price, qty, customization;
        View btnPlus, btnMinus, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkCartItem);
            image = itemView.findViewById(R.id.cartImage);
            name = itemView.findViewById(R.id.cartName);
            price = itemView.findViewById(R.id.cartPrice);
            qty = itemView.findViewById(R.id.cartQty);
            customization = itemView.findViewById(R.id.cartCustomization);

            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
