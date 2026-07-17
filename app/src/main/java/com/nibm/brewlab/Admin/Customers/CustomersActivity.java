package com.nibm.brewlab.Admin.Customers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class CustomersActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Customer> list;
    ArrayList<Customer> filteredList;

    CustomersAdapter adapter;

    FirebaseFirestore db;

    EditText searchCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

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

}