package com.nibm.brewlab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.nibm.brewlab.model.Order;

import java.util.List;
import java.util.Locale;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onAccept(Order order);
        void onDecline(Order order);
        void onStartDelivery(Order order);
        void onOpenOrder(Order order);
    }

    private final List<Order> orderList;
    private final OnOrderActionListener listener;

    public DeliveryOrderAdapter(List<Order> orderList, OnOrderActionListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.txtOrderId.setText("#" + order.getOrderId());
        holder.txtCustomerName.setText("Customer: " + order.getCustomerName());
        holder.txtAddress.setText(order.getAddress());
        holder.txtAmount.setText(String.format(Locale.getDefault(), "Rs. %.2f", order.getAmount()));

        String status = order.getStatus() == null ? "assigned" : order.getStatus();

        switch (status) {
            case "assigned":
                holder.txtStatusBadge.setText("Pending");
                holder.txtStatusBadge.setBackgroundResource(R.drawable.badge_pending);
                holder.layoutAcceptDecline.setVisibility(View.VISIBLE);
                holder.btnStartDelivery.setVisibility(View.GONE);
                break;

            case "accepted":
            case "on_the_way":
            case "arrived":
            case "updated":
                holder.txtStatusBadge.setText("Active");
                holder.txtStatusBadge.setBackgroundResource(R.drawable.badge_active);
                holder.layoutAcceptDecline.setVisibility(View.GONE);
                holder.btnStartDelivery.setVisibility(View.VISIBLE);
                holder.btnStartDelivery.setText(
                        status.equals("accepted") ? "Start Delivery" : "Continue Delivery");
                break;

            case "delivered":
                holder.txtStatusBadge.setText("Delivered");
                holder.txtStatusBadge.setBackgroundResource(R.drawable.badge_delivered);
                holder.layoutAcceptDecline.setVisibility(View.GONE);
                holder.btnStartDelivery.setVisibility(View.GONE);
                break;

            case "declined":
                holder.txtStatusBadge.setText("Declined");
                holder.txtStatusBadge.setBackgroundResource(R.drawable.badge_declined);
                holder.layoutAcceptDecline.setVisibility(View.GONE);
                holder.btnStartDelivery.setVisibility(View.GONE);
                break;

            default:
                holder.txtStatusBadge.setText(status);
                holder.txtStatusBadge.setBackgroundResource(R.drawable.badge_pending);
                holder.layoutAcceptDecline.setVisibility(View.GONE);
                holder.btnStartDelivery.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(order);
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (listener != null) listener.onDecline(order);
        });

        holder.btnStartDelivery.setOnClickListener(v -> {
            if (listener != null) listener.onStartDelivery(order);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpenOrder(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtCustomerName, txtAddress, txtAmount, txtStatusBadge;
        View layoutAcceptDecline;
        MaterialButton btnAccept, btnDecline, btnStartDelivery;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatusBadge = itemView.findViewById(R.id.txtStatusBadge);
            layoutAcceptDecline = itemView.findViewById(R.id.layoutAcceptDecline);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnStartDelivery = itemView.findViewById(R.id.btnStartDelivery);
        }
    }
}
