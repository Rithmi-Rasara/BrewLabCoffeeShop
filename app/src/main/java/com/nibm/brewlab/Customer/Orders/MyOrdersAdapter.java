package com.nibm.brewlab.Customer.Orders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nibm.brewlab.Admin.Product.Product;
import com.nibm.brewlab.Customer.Cart.CartActivity;
import com.nibm.brewlab.Customer.Cart.CartManager;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        String status = order.getStatus() == null ? "Pending" : order.getStatus();

        holder.txtItems.setText(order.getItemsSummary());
        holder.txtTotal.setText("Rs. " + order.getTotalAmount());
        holder.txtDate.setText(DateFormat.format("dd MMM yyyy, hh:mm a", order.getTimestamp()));

        // NEW: clear status label + colour
        holder.txtStatus.setText(statusLabel(status));
        holder.txtStatus.setTextColor(Color.WHITE);
        holder.txtStatus.setBackgroundResource(statusBackground(status));

        // NEW: elapsed time since order was placed
        holder.txtElapsed.setText(elapsedTimeText(order.getTimestamp()));

        // NEW: "Track on Map" button, only visible while order is "Out for Delivery"
        if ("Out for Delivery".equalsIgnoreCase(status)) {
            holder.btnTrackMap.setVisibility(View.VISIBLE);
            holder.btnTrackMap.setOnClickListener(v -> {
                Intent intent = new Intent(context, DeliveryTrackingActivity.class);
                intent.putExtra("orderId", order.getId());
                context.startActivity(intent);
            });
        } else {
            holder.btnTrackMap.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });

        holder.btnReorder.setOnClickListener(v -> reorder(order));
    }

    // NEW: friendly status text — Preparing / Ready / On the way / Delivered
    private String statusLabel(String status) {
        switch (status) {
            case "Pending":
                return "Pending";
            case "Preparing":
                return "Preparing";
            case "Ready":
                return "Ready";
            case "Out for Delivery":
                return "On the way 🚴";
            case "Delivered":
                return "Delivered ✓";
            default:
                return status;
        }
    }

    // NEW: colour-coded background per status (add these drawables — see notes)
    private int statusBackground(String status) {
        switch (status) {
            case "Preparing":
                return R.drawable.bg_status_preparing;   // yellow/orange
            case "Ready":
                return R.drawable.bg_status_ready;       // green
            case "Out for Delivery":
                return R.drawable.bg_status_on_the_way;  // blue
            case "Delivered":
                return R.drawable.bg_status_delivered;   // grey
            default:
                return R.drawable.bg_status_pending;     // brown/default
        }
    }

    // NEW: "5 mins ago" / "2 hours ago" style text
    private String elapsedTimeText(long orderTimestamp) {
        long diffMs = System.currentTimeMillis() - orderTimestamp;
        if (diffMs < 0) diffMs = 0;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs);
        long hours = TimeUnit.MILLISECONDS.toHours(diffMs);

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " min" + (minutes == 1 ? "" : "s") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else {
            long days = TimeUnit.MILLISECONDS.toDays(diffMs);
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        }
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

        context.startActivity(new Intent(context, CartActivity.class));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtItems, txtTotal, txtStatus, txtDate, txtElapsed;
        Button btnReorder, btnTrackMap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtItems = itemView.findViewById(R.id.txtOrderItems);
            txtTotal = itemView.findViewById(R.id.txtOrderTotal);
            txtStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtDate = itemView.findViewById(R.id.txtOrderDate);
            txtElapsed = itemView.findViewById(R.id.txtOrderElapsed);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            btnTrackMap = itemView.findViewById(R.id.btnTrackMap);
        }
    }
}
