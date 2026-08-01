package com.nibm.brewlab.Admin.Delivery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class ManageDeliveryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<DeliveryPerson> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_delivery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.deliveryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        list.add(new DeliveryPerson("Ruwan Jayasuriya", "WP CAB-4521", "0772345678"));
        list.add(new DeliveryPerson("Chamara Bandara", "WP KL-7890", "0713456789"));
        list.add(new DeliveryPerson("Tharindu Fernando", "WP CAM-2231", "0754567890"));
        list.add(new DeliveryPerson("Isuru Madushanka", "WP KX-6612", "0765678901"));

        DeliveryPersonAdapter adapter = new DeliveryPersonAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }
}
