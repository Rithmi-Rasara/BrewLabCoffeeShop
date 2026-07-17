package com.nibm.brewlab.Admin.Customers;

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

public class CustomersAdapter
        extends RecyclerView.Adapter<CustomersAdapter.ViewHolder>{

    Context context;

    ArrayList<Customer> list;

    FirebaseFirestore db;

    public CustomersAdapter(Context context, ArrayList<Customer> list){

        this.context=context;
        this.list=list;

        db = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){

        View view = LayoutInflater.from(context)
                .inflate(R.layout.customer_item,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position){

        Customer customer = list.get(position);

        holder.tvName.setText(customer.name);

        holder.tvEmail.setText(customer.email);

        if(customer.status != null){

            holder.tvStatus.setText(customer.status);

            if(customer.status.equals("Approved") ||
                    customer.status.equals("Rejected")){

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
                    .document(customer.id)
                    .update("status","Approved")
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(context,
                                "Customer Approved",
                                Toast.LENGTH_SHORT).show();

                        holder.btnApprove.setVisibility(View.GONE);
                        holder.btnReject.setVisibility(View.GONE);

                    });

        });

        holder.btnReject.setOnClickListener(v -> {

            db.collection("Users")
                    .document(customer.id)
                    .update("status","Rejected")
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(context,
                                "Customer Rejected",
                                Toast.LENGTH_SHORT).show();

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

        TextView tvName,tvEmail,tvStatus;

        Button btnApprove,btnReject;

        public ViewHolder(@NonNull View itemView){

            super(itemView);

            tvName =
                    itemView.findViewById(R.id.tvCustomerName);

            tvEmail =
                    itemView.findViewById(R.id.tvCustomerEmail);

            tvStatus =
                    itemView.findViewById(R.id.tvStatus);

            btnApprove =
                    itemView.findViewById(R.id.btnApprove);

            btnReject =
                    itemView.findViewById(R.id.btnReject);

        }

    }

}