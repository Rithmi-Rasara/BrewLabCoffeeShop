package com.nibm.brewlab.Admin.Customers;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;


import java.util.ArrayList;



public class CustomersActivity extends AppCompatActivity {


    RecyclerView recyclerView;

    ArrayList<Customer> list;

    CustomersAdapter adapter;

    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);


        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }



        recyclerView = findViewById(R.id.customerRecycler);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );



        list = new ArrayList<>();


        adapter = new CustomersAdapter(
                this,
                list
        );


        recyclerView.setAdapter(adapter);



        db = FirebaseFirestore.getInstance();


        loadCustomers();

    }




    private void loadCustomers(){


        db.collection("Users")

                .addSnapshotListener((value,error)->{


                    if(error != null){

                        Toast.makeText(this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        return;
                    }



                    if(value == null){
                        return;
                    }



                    list.clear();



                    for(DocumentSnapshot doc : value.getDocuments()){


                        Customer customer =
                                doc.toObject(Customer.class);



                        if(customer != null){


                            customer.id = doc.getId();

                            list.add(customer);

                        }

                    }



                    adapter.notifyDataSetChanged();


                });


    }

}