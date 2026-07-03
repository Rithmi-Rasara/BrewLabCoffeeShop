package com.nibm.brewlab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DeliveryHistoryAdapter extends RecyclerView.Adapter<DeliveryHistoryAdapter.HistoryViewHolder> {

    private final List<Order> historyList;

    public DeliveryHistoryAdapter(List<Order> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order order = historyList.get(position);

        holder.txtOrderId.setText("#" + order.getOrderId());
        holder.txtCustomerName.setText("Customer: " + order.getCustomerName());
        holder.txtAmount.setText(String.format(Locale.getDefault(), "Rs. %.2f", order.getAmount()));

        if (order.getDeliveredAt() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  h:mm a", Locale.getDefault());
            holder.txtDeliveredDate.setText("Delivered on: " + sdf.format(order.getDeliveredAt()));
        } else {
            holder.txtDeliveredDate.setText("Delivered");
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtCustomerName, txtDeliveredDate, txtAmount;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtDeliveredDate = itemView.findViewById(R.id.txtDeliveredDate);
            txtAmount = itemView.findViewById(R.id.txtAmount);
        }
    }
}
