package com.nibm.brewlab.Admin.Customers;

import android.os.Bundle;
<<<<<<< HEAD
=======
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
>>>>>>> origin/develop

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
=======
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
>>>>>>> origin/develop
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CustomersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
<<<<<<< HEAD
    ArrayList<Customer> list;
=======

    ArrayList<Customer> list;
    ArrayList<Customer> filteredList;

    CustomersAdapter adapter;

    FirebaseFirestore db;

    EditText searchCustomer;
>>>>>>> origin/develop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

<<<<<<< HEAD
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
=======
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        recyclerView = findViewById(R.id.customerRecycler);

        searchCustomer = findViewById(R.id.search_customer);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new CustomersAdapter(
                this,
                filteredList
        );

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadCustomers();

        searchCustomer.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(
                    CharSequence s, int st, int c, int a) {
            }

            public void onTextChanged(
                    CharSequence s, int st, int b, int c) {

                filterCustomers(s.toString());

            }

            public void afterTextChanged(Editable e) {
            }

        });

    }

    private void loadCustomers() {

        db.collection("Users")
                .whereEqualTo("role", "Customer")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {

                        Toast.makeText(
                                this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    list.clear();

                    for (QueryDocumentSnapshot doc : value) {

                        Customer customer =
                                doc.toObject(Customer.class);

                        customer.id = doc.getId();

                        list.add(customer);

                    }

                    filteredList.clear();
                    filteredList.addAll(list);

                    adapter.notifyDataSetChanged();

                });

    }

    private void filterCustomers(String text) {

        filteredList.clear();

        if (text.isEmpty()) {

            filteredList.addAll(list);

        } else {

            for (Customer customer : list) {

                if (customer.name
                        .toLowerCase()
                        .contains(text.toLowerCase())) {

                    filteredList.add(customer);

                }

            }

        }

        adapter.notifyDataSetChanged();

    }

>>>>>>> origin/develop
}