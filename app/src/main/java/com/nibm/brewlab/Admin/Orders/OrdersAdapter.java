package com.nibm.brewlab.Admin.Orders;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;

import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private ArrayList<Order> orderList;

    public OrdersAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order order = orderList.get(position);

        holder.txtOrderId.setText("Order ID : " + order.getId());
        holder.txtCustomer.setText("Customer : " + order.getCustomerName());
        holder.txtAmount.setText("Rs. " + order.getTotalAmount());

        String status = order.getOrderStatus();

        holder.txtStatus.setText(status);

        if ("Pending".equalsIgnoreCase(status)) {

            holder.txtStatus.setTextColor(Color.RED);

        } else if ("Preparing".equalsIgnoreCase(status)) {

            holder.txtStatus.setTextColor(Color.parseColor("#FFA500"));

        } else if ("Delivered".equalsIgnoreCase(status)) {

            holder.txtStatus.setTextColor(Color.GREEN);
        }

        if ("Preparing".equalsIgnoreCase(status) ||
                "Delivered".equalsIgnoreCase(status) || "Out for Delivery".equalsIgnoreCase(status)) {

            holder.btnView.setVisibility(View.GONE);

        } else {

            holder.btnView.setVisibility(View.VISIBLE);
        }


        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);

            intent.putExtra("orderId", order.getId());
            intent.putExtra("customer", order.getCustomerName());
            intent.putExtra("total", order.getTotalAmount());
            intent.putExtra("address", order.getDeliveryAddress());
            intent.putExtra("status", order.getOrderStatus());

            v.getContext().startActivity(intent);

        });


        holder.btnDelete.setOnClickListener(v -> {

            FirebaseDatabase.getInstance()
                    .getReference("Orders")
                    .child(order.getId())
                    .removeValue();

        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtOrderId, txtCustomer, txtAmount, txtStatus;
        Button btnView, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtCustomer = itemView.findViewById(R.id.txtCustomer);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnView = itemView.findViewById(R.id.btnView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}