package com.nibm.brewlab.Admin.Delivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class DeliveryPersonAdapter
        extends RecyclerView.Adapter<DeliveryPersonAdapter.ViewHolder>{

    Context context;

    ArrayList<DeliveryPerson> list;

    FirebaseFirestore db;

    public DeliveryPersonAdapter(Context context,
                                 ArrayList<DeliveryPerson> list){

        this.context = context;
        this.list = list;

        db = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){

        View view = LayoutInflater.from(context)
                .inflate(R.layout.delivery_person_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position){

        DeliveryPerson person = list.get(position);

        holder.tvName.setText(person.name);

        holder.tvVehicle.setText(person.vehicleNumber);

        if(person.phone != null){

            holder.tvPhone.setText(person.phone);

        }

        if(person.status != null){

            holder.tvStatus.setText(person.status);

            if(person.status.equalsIgnoreCase("Approved") ||
                    person.status.equalsIgnoreCase("Rejected")){

                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);

            }else{

                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);

            }

        }else{

            holder.tvStatus.setText("Pending");

        }

        holder.btnApprove.setOnClickListener(v -> {

            db.collection("Users")
                    .document(person.id)
                    .update("status","Approved")
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(context,
                                "Delivery Person Approved",
                                Toast.LENGTH_SHORT).show();

                        holder.tvStatus.setText("Approved");

                        holder.btnApprove.setVisibility(View.GONE);
                        holder.btnReject.setVisibility(View.GONE);

                    });

        });

        holder.btnReject.setOnClickListener(v -> {

            db.collection("Users")
                    .document(person.id)
                    .update("status","Rejected")
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(context,
                                "Delivery Person Rejected",
                                Toast.LENGTH_SHORT).show();

                        holder.tvStatus.setText("Rejected");

                        holder.btnApprove.setVisibility(View.GONE);
                        holder.btnReject.setVisibility(View.GONE);

                    });

        });

    }

    @Override
    public int getItemCount(){

        return list.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        TextView tvVehicle;
        TextView tvPhone;
        TextView tvStatus;

        Button btnApprove;
        Button btnReject;

        public ViewHolder(@NonNull View itemView){

            super(itemView);

            tvName =
                    itemView.findViewById(R.id.tvDeliveryName);

            tvVehicle =
                    itemView.findViewById(R.id.tvDeliveryVehicle);

            tvPhone =
                    itemView.findViewById(R.id.tvDeliveryPhone);

            tvStatus =
                    itemView.findViewById(R.id.tvStatus);

            btnApprove =
                    itemView.findViewById(R.id.btnApprove);

            btnReject =
                    itemView.findViewById(R.id.btnReject);

        }

    }

}