package com.nibm.brewlab.Delivery.Orders;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nibm.brewlab.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssignedOrdersAdapter extends RecyclerView.Adapter<AssignedOrdersAdapter.ViewHolder> {

    Context context;
    ArrayList<DeliveryOrder> orderList;
    String myUid;
    String myName = "Delivery Person";

    DatabaseReference ordersRef;
    DatabaseReference usersRef;

    public AssignedOrdersAdapter(Context context, ArrayList<DeliveryOrder> orderList, String myUid) {
        this.context = context;
        this.orderList = orderList;
        this.myUid = myUid;
        this.ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        this.usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.child(myUid).child("name").get().addOnSuccessListener(snapshot -> {
            String name = snapshot.getValue(String.class);
            if (name != null) myName = name;
        });
    }

    public void setMyName(String name) {
        if (name != null) myName = name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assigned_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryOrder order = orderList.get(position);

        holder.txtCustomerName.setText(order.getCustomerName());
        holder.txtAddress.setText(order.getDeliveryAddress());
        holder.txtItems.setText(order.getItemsSummary());
        holder.txtTotal.setText("Rs. " + order.getTotalAmount());
        holder.txtDate.setText(DateFormat.format("dd MMM yyyy, hh:mm a", order.getTimestamp()));

        boolean isPending = "Pending".equalsIgnoreCase(order.getStatus());

        boolean isMyActiveDelivery = "Out for Delivery".equalsIgnoreCase(order.getStatus())
                && myUid.equals(order.getDeliveryPersonUid());

        if (isMyActiveDelivery) {
            holder.txtStatusBadge.setText("Out for Delivery");
            holder.txtStatusBadge.setTextColor(0xFF6FCF97);
        } else {
            holder.txtStatusBadge.setText("New Request");
            holder.txtStatusBadge.setTextColor(0xFFD89A5C);
        }

        holder.layoutAcceptReject.setVisibility(isPending ? View.VISIBLE : View.GONE);

        holder.btnReject.setOnClickListener(v ->
                Toast.makeText(context, "Skipped. It'll stay available for other riders.", Toast.LENGTH_SHORT).show());

        holder.btnAccept.setOnClickListener(v -> {

            holder.btnAccept.setEnabled(false);
            holder.btnReject.setEnabled(false);

            Map<String, Object> updates = new HashMap<>();
            updates.put("status", "Out for Delivery");
            updates.put("stage", "Accepted");
            updates.put("deliveryPersonUid", myUid);
            updates.put("deliveryPersonName", myName);
            updates.put("acceptedAt", System.currentTimeMillis());

            ordersRef.child(order.getId()).updateChildren(updates)
                    .addOnSuccessListener(unused -> {
                        Intent intent = new Intent(context, DeliveryOrderDetailActivity.class);
                        intent.putExtra("orderId", order.getId());
                        context.startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        holder.btnAccept.setEnabled(true);
                        holder.btnReject.setEnabled(true);
                        Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeliveryOrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtCustomerName, txtAddress, txtItems, txtTotal, txtDate, txtStatusBadge;
        LinearLayout layoutAcceptReject;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtItems = itemView.findViewById(R.id.txtItems);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatusBadge = itemView.findViewById(R.id.txtStatusBadge);
            layoutAcceptReject = itemView.findViewById(R.id.layoutAcceptReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}