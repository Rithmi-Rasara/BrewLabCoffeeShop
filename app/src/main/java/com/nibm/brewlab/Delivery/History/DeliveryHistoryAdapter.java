package com.nibm.brewlab.Delivery.History;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.Delivery.Orders.DeliveryOrder;
import com.nibm.brewlab.Delivery.Orders.DeliveryOrderDetailActivity;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class DeliveryHistoryAdapter extends RecyclerView.Adapter<DeliveryHistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<DeliveryOrder> historyList;

    public DeliveryHistoryAdapter(Context context, ArrayList<DeliveryOrder> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryOrder order = historyList.get(position);

        holder.txtCustomerName.setText(order.getCustomerName());
        holder.txtAddress.setText(order.getDeliveryAddress());
        holder.txtTotal.setText("Rs. " + order.getTotalAmount());
        holder.txtDeliveredDate.setText("Delivered: "
                + DateFormat.format("dd MMM yyyy, hh:mm a", order.getDeliveredAt()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeliveryOrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtCustomerName, txtAddress, txtTotal, txtDeliveredDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtDeliveredDate = itemView.findViewById(R.id.txtDeliveredDate);
        }
    }
}
