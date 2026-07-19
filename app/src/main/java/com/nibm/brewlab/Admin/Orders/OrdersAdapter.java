package com.nibm.brewlab.Admin.Orders;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
<<<<<<< HEAD
=======
import android.graphics.Color;

import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> origin/develop

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD

=======
>>>>>>> origin/develop
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

<<<<<<< HEAD
    ArrayList<Order> orderList;
=======
    private ArrayList<Order> orderList;
>>>>>>> origin/develop

    public OrdersAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order order = orderList.get(position);

<<<<<<< HEAD
        holder.txtOrderId.setText(order.id);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
            intent.putExtra("orderId", order.id);
            v.getContext().startActivity(intent);
        });
=======
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

        holder.txtStatus.setText(order.getOrderStatus());
>>>>>>> origin/develop

        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
<<<<<<< HEAD
            intent.putExtra("orderId", order.id);
            v.getContext().startActivity(intent);
=======
            intent.putExtra("customer",
                    order.getCustomerName());


            intent.putExtra("total",
                    order.getTotalAmount());


            intent.putExtra("address",
                    order.getDeliveryAddress());


            intent.putExtra("status",
                    order.getOrderStatus());
            v.getContext().startActivity(intent);

>>>>>>> origin/develop
        });

        holder.btnDelete.setOnClickListener(v -> {

<<<<<<< HEAD
            orderList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderList.size());

=======
            FirebaseFirestore.getInstance()
                    .collection("Orders")
                    .document(order.getId())
                    .delete();
>>>>>>> origin/develop
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

<<<<<<< HEAD
        TextView txtOrderId;
        Button btnView, btnUpdate, btnDelete;
=======
        TextView txtOrderId, txtCustomer, txtAmount, txtStatus;
        Button btnView, btnDelete;
>>>>>>> origin/develop

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderId = itemView.findViewById(R.id.txtOrderId);
<<<<<<< HEAD
            btnView = itemView.findViewById(R.id.btnView);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
=======
            txtCustomer = itemView.findViewById(R.id.txtCustomer);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnView = itemView.findViewById(R.id.btnView);
>>>>>>> origin/develop
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}