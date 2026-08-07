package com.nibm.brewlab.Admin.Delivery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class DeliveryPersonAdapter extends RecyclerView.Adapter<DeliveryPersonAdapter.ViewHolder> {

    Context context;
    ArrayList<DeliveryPerson> list;

    public DeliveryPersonAdapter(Context context, ArrayList<DeliveryPerson> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.delivery_person_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryPerson deliveryPerson = list.get(position);

        holder.tvName.setText(deliveryPerson.name);
        holder.tvVehicle.setText(deliveryPerson.vehicleNumber);

        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(context, DeliveryDetailsActivity.class);

            intent.putExtra("name", deliveryPerson.name);
            intent.putExtra("vehicleNumber", deliveryPerson.vehicleNumber);
            intent.putExtra("phone", deliveryPerson.phone);

            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvVehicle;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvDeliveryName);
            tvVehicle = itemView.findViewById(R.id.tvDeliveryVehicle);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
