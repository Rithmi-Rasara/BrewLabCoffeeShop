package com.nibm.brewlab.Customer.Orders;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.List;

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
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
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
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtItems, txtTotal, txtStatus, txtDate, txtBrewTime, txtPrepStarted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtItems = itemView.findViewById(R.id.txtOrderItems);
            txtTotal = itemView.findViewById(R.id.txtOrderTotal);
            txtStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtDate = itemView.findViewById(R.id.txtOrderDate);
            txtBrewTime = itemView.findViewById(R.id.txtOrderBrewTime);
            txtPrepStarted = itemView.findViewById(R.id.txtOrderPrepStarted);
        }
    }
}
