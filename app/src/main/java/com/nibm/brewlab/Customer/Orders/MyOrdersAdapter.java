package com.nibm.brewlab.Customer.Orders;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.TextView;
=======
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
>>>>>>> origin/develop

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.List;
=======
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.Customer.Cart.CartManager;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.Map;
>>>>>>> origin/develop

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    Context context;
    ArrayList<CustomerOrder> orderList;

    public MyOrdersAdapter(Context context, ArrayList<CustomerOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CustomerOrder order = orderList.get(position);

        holder.txtItems.setText(order.getItemsSummary());
        holder.txtTotal.setText("Rs. " + order.getTotalAmount());
        holder.txtStatus.setText(order.getStatus());
        holder.txtDate.setText(DateFormat.format("dd MMM yyyy, hh:mm a", order.getTimestamp()));
<<<<<<< HEAD
        holder.txtBrewTime.setText(buildBrewTimeText(order.getItems()));

        if (order.getPreparationStartedAt() > 0) {
            holder.txtPrepStarted.setVisibility(View.VISIBLE);
            holder.txtPrepStarted.setText("🔥 Preparation Started: "
                    + DateFormat.format("dd MMM yyyy, hh:mm a", order.getPreparationStartedAt()));
        } else {
            holder.txtPrepStarted.setVisibility(View.GONE);
        }

        // Tapping an order opens Order Details, where each item can be
        // individually ticked and reordered - see OrderDetailsActivity.
=======

>>>>>>> origin/develop
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
<<<<<<< HEAD
    }

    // Items are prepared side by side, so the order is "ready" roughly
    // when its slowest item finishes - hence the max, not the sum.
    private String buildBrewTimeText(List<OrderLineItem> items) {

        if (items == null || items.isEmpty()) {
            return "⏱ Brew time: -";
        }

        int maxMinutes = 0;
        for (OrderLineItem item : items) {
            maxMinutes = Math.max(maxMinutes, item.getBrewTimeMinutes());
        }

        return "⏱ Brew time: ~" + maxMinutes + " mins";
=======

        holder.btnReorder.setOnClickListener(v -> reorder(order));
    }

    private void reorder(CustomerOrder order) {

        Map<String, Long> items = order.getItemsData();

        if (items == null || items.isEmpty()) {
            Toast.makeText(context, "No items found for this order", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");

        for (Map.Entry<String, Long> entry : items.entrySet()) {

            String productId = entry.getKey();

            productsRef.child(productId).get().addOnSuccessListener(snapshot -> {

                Product product = snapshot.getValue(Product.class);

                if (product != null) {
                    product.setId(productId);
                    CartManager.addToCart(context, product);
                }
            });
        }

        Toast.makeText(context, "Items added to cart (default size/sugar - customize again if needed)",
                Toast.LENGTH_LONG).show();
>>>>>>> origin/develop
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

<<<<<<< HEAD
        TextView txtItems, txtTotal, txtStatus, txtDate, txtBrewTime, txtPrepStarted;
=======
        TextView txtItems, txtTotal, txtStatus, txtDate;
        Button btnReorder;
>>>>>>> origin/develop

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtItems = itemView.findViewById(R.id.txtOrderItems);
            txtTotal = itemView.findViewById(R.id.txtOrderTotal);
            txtStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtDate = itemView.findViewById(R.id.txtOrderDate);
<<<<<<< HEAD
            txtBrewTime = itemView.findViewById(R.id.txtOrderBrewTime);
            txtPrepStarted = itemView.findViewById(R.id.txtOrderPrepStarted);
=======
            btnReorder = itemView.findViewById(R.id.btnReorder);
>>>>>>> origin/develop
        }
    }
}
