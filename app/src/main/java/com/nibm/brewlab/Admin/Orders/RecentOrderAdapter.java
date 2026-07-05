package com.nibm.brewlab.Admin.Orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.List;

public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.ViewHolder> {

    private List<RecentOrder> orderList;

    public RecentOrderAdapter(List<RecentOrder> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_order_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RecentOrder order = orderList.get(position);

        holder.txtCustomer.setText(order.getCustomerName());
        holder.txtAmount.setText("Rs. " + order.getTotalAmount());
        holder.txtStatus.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtCustomer, txtAmount, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomer = itemView.findViewById(R.id.txtCustomer);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}