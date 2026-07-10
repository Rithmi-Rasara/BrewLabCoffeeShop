package com.nibm.brewlab.Admin.Customers;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


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



        holder.btnView.setOnClickListener(v->{


            Intent intent =
                    new Intent(context,CustomerDetailsActivity.class);


            intent.putExtra("name",customer.name);

            intent.putExtra("email",customer.email);

            intent.putExtra("phone",customer.phone);



            context.startActivity(intent);


        });


    }





    @Override
    public int getItemCount(){

        return list.size();

    }






    public static class ViewHolder extends RecyclerView.ViewHolder{


        TextView tvName,tvEmail;

        Button btnView;



        public ViewHolder(@NonNull View itemView){

            super(itemView);



            tvName =
                    itemView.findViewById(R.id.tvCustomerName);


            tvEmail =
                    itemView.findViewById(R.id.tvCustomerEmail);


            btnView =
                    itemView.findViewById(R.id.btnView);


        }


    }

}