package com.nibm.brewlab.Admin.Orders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;

import android.widget.Spinner;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.brewlab.R;

public class OrderDetailActivity extends AppCompatActivity {

    TextView txtOrderId, txtCustomer, txtTotal, txtStatus, txtAddress;

    Button btnAssign, btnBack;

    String orderId;

    FirebaseFirestore db;

    Spinner spDeliveryPerson;
    ArrayList<String> deliveryList = new ArrayList<>();
    ArrayList<String> deliveryIds = new ArrayList<>();
    ArrayAdapter<String> adapter;

    ArrayList<String> deliveryNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        spDeliveryPerson = findViewById(R.id.spDeliveryPerson);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                deliveryList
        );

        spDeliveryPerson.setAdapter(adapter);

        loadDeliveryPersons();

        txtCustomer = findViewById(R.id.txtCustomer);
        txtTotal = findViewById(R.id.txtTotal);
        txtAddress = findViewById(R.id.txtAddress);

        btnAssign = findViewById(R.id.btnAssign);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        orderId = getIntent().getStringExtra("orderId");

        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Order ID Missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String customer =
                getIntent().getStringExtra("customer");

        double total = getIntent().getDoubleExtra("total", 0);

        txtTotal.setText("Total : Rs. " + total);

        String address =
                getIntent().getStringExtra("address");

        txtCustomer.setText(
                "Customer : " + customer
        );

        txtTotal.setText(
                "Total : Rs " + total
        );

        txtAddress.setText(
                "Address : " + address
        );

        btnAssign.setOnClickListener(v -> {

            if (deliveryIds.isEmpty()) {
                Toast.makeText(this,
                        "No delivery person available",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = spDeliveryPerson.getSelectedItemPosition();

            String deliveryId = deliveryIds.get(pos);
            String deliveryName = deliveryList.get(pos);

            assignDelivery(deliveryId, deliveryName);

        });

        btnBack.setOnClickListener(v ->
                finish()
        );

    }

    private void loadDeliveryPersons() {

        FirebaseFirestore.getInstance()
                .collection("Users")
                .whereEqualTo("role","Delivery Person")
                .whereEqualTo("status","Approved")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    deliveryList.clear();
                    deliveryIds.clear();

                    for(DocumentSnapshot doc : queryDocumentSnapshots){

                        deliveryList.add(doc.getString("name"));
                        deliveryIds.add(doc.getId());

                    }

                    adapter.notifyDataSetChanged();

                });

    }

    private void showDeliveryDialog(){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Select Delivery Person");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_single_choice,
                        deliveryNames
                );

        builder.setAdapter(adapter,(dialog,which)->{

            String selectedName =
                    deliveryNames.get(which);

            String selectedId =
                    deliveryIds.get(which);

            assignDelivery(
                    selectedId,
                    selectedName
            );

        });

        builder.show();

    }

    private void assignDelivery(String deliveryId, String deliveryName) {

        FirebaseFirestore.getInstance()
                .collection("Orders")
                .document(orderId)
                .update(
                        "deliveryPersonId", deliveryId,
                        "deliveryPersonName", deliveryName,
                        "status", "Preparing"
                )
                .addOnSuccessListener(unused -> {

                    sendNotification(deliveryId);

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show());

    }

    private void sendNotification(String deliveryId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        java.util.HashMap<String, Object> notification = new java.util.HashMap<>();

        notification.put("title", "New Delivery Assigned");
        notification.put("message", "A new order has been assigned to you.");
        notification.put("orderId", orderId);
        notification.put("deliveryPersonId", deliveryId);
        notification.put("status", "Unread");
        notification.put("type", "Delivery");
        notification.put("time", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(this,
                            "Delivery Assigned Successfully",
                            Toast.LENGTH_SHORT).show();

                    finish();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show());

    }

}