package com.nibm.brewlab.Admin.Customers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CustomersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Customer> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.customerRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        list.add(new Customer("Kasun Perera", "kasun@gmail.com", "0771234567"));
        list.add(new Customer("Nimal Silva", "nimal@gmail.com", "0719876543"));
        list.add(new Customer("Saman Kumara", "saman@gmail.com", "0756543210"));
        list.add(new Customer("Amal Perera", "amal@gmail.com", "0767891234"));

        CustomersAdapter adapter = new CustomersAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }
}