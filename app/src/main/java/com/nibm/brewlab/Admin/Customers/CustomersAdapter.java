package com.nibm.brewlab.Admin.Customers;

import android.content.Context;
<<<<<<< HEAD
import android.content.Intent;
=======
>>>>>>> origin/develop
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
<<<<<<< HEAD
=======
import android.widget.Toast;
>>>>>>> origin/develop

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
=======
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> origin/develop
import com.nibm.brewlab.R;

import java.util.ArrayList;

<<<<<<< HEAD
public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {

    Context context;
    ArrayList<Customer> list;

    public CustomersAdapter(Context context, ArrayList<Customer> list) {
        this.context = context;
        this.list = list;
=======
public class CustomersAdapter
        extends RecyclerView.Adapter<CustomersAdapter.ViewHolder>{

    Context context;

    ArrayList<Customer> list;

    FirebaseFirestore db;

    public CustomersAdapter(Context context, ArrayList<Customer> list){

        this.context=context;
        this.list=list;

        db = FirebaseFirestore.getInstance();

>>>>>>> origin/develop
    }

    @NonNull
    @Override
<<<<<<< HEAD
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.customer_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
=======
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
>>>>>>> origin/develop

        Customer customer = list.get(position);

        holder.tvName.setText(customer.name);
<<<<<<< HEAD
        holder.tvEmail.setText(customer.email);

        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(context, CustomerDetailsActivity.class);

            intent.putExtra("name", customer.name);
            intent.putExtra("email", customer.email);
            intent.putExtra("phone", customer.phone);

            context.startActivity(intent);
=======

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
>>>>>>> origin/develop

        });

    }

    @Override
<<<<<<< HEAD
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvEmail = itemView.findViewById(R.id.tvCustomerEmail);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
=======
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

>>>>>>> origin/develop
}