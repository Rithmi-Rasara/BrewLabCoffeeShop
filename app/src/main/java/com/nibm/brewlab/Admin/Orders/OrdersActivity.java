package com.nibm.brewlab.Admin.Orders;

<<<<<<< HEAD
import android.content.Intent;
=======
>>>>>>> origin/develop
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
=======
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> origin/develop
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
<<<<<<< HEAD
    ArrayList<Order> orderList;

    Button pendingOrders;
    Button doneOrders;
=======

    ArrayList<Order> orderList;

    OrdersAdapter ordersAdapter;

    Button allOrders, pendingOrders, doneOrders;

    FirebaseFirestore db;
>>>>>>> origin/develop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

<<<<<<< HEAD
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.ordersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingOrders = findViewById(R.id.btnPending);
        doneOrders = findViewById(R.id.btnCompleted);

        orderList = new ArrayList<>();

        orderList.add(new Order("1001", "Pending"));
        orderList.add(new Order("1002", "Pending"));
        orderList.add(new Order("1003", "Done"));

        OrdersAdapter adapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(adapter);

        pendingOrders.setOnClickListener(v -> {
            Intent intent = new Intent(OrdersActivity.this, PendingOrdersActivity.class);
            startActivity(intent);
        });

        doneOrders.setOnClickListener(v -> {
            Intent intent = new Intent(OrdersActivity.this, DoneOrdersActivity.class);
            startActivity(intent);
        });
    }
=======
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(
                R.id.ordersRecyclerView
        );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        allOrders = findViewById(
                R.id.btnAll
        );

        pendingOrders = findViewById(
                R.id.btnPending
        );

        doneOrders = findViewById(
                R.id.btnComplete
        );

        orderList = new ArrayList<>();

        ordersAdapter = new OrdersAdapter(
                orderList
        );

        recyclerView.setAdapter(
                ordersAdapter
        );

        db = FirebaseFirestore.getInstance();

        loadOrders("ALL");

        allOrders.setOnClickListener(v -> {

            loadOrders("ALL");

        });

        pendingOrders.setOnClickListener(v -> {

            loadOrders("Pending");

        });

        doneOrders.setOnClickListener(v -> {

            loadOrders("Completed");

        });

    }

    private void loadOrders(String filter){

        db.collection("Orders")
                .get()

                .addOnSuccessListener(queryDocumentSnapshots -> {

                    orderList.clear();

                    for(DocumentSnapshot doc :
                            queryDocumentSnapshots.getDocuments()){

                        Order order =
                                doc.toObject(Order.class);

                        if(order != null){

                            order.setId(
                                    doc.getId()
                            );

                            String status =
                                    order.getOrderStatus();

                            if(filter.equals("ALL")){

                                orderList.add(order);

                            }

                            else if(filter.equals("Pending")){

                                if(status != null &&
                                        status.equalsIgnoreCase("Pending")){

                                    orderList.add(order);

                                }

                            }

                            else if(filter.equals("Completed")){

                                if(status != null &&
                                        status.equalsIgnoreCase("Completed")){

                                    orderList.add(order);

                                }

                            }

                        }

                    }

                    ordersAdapter.notifyDataSetChanged();

                });

    }

>>>>>>> origin/develop
}