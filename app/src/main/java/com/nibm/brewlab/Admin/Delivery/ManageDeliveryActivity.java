package com.nibm.brewlab.Admin.Delivery;

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

public class ManageDeliveryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<DeliveryPerson> list;
    ArrayList<DeliveryPerson> filteredList;

    DeliveryPersonAdapter adapter;

    FirebaseFirestore db;

    EditText searchDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_delivery);

        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        recyclerView = findViewById(R.id.deliveryRecycler);

        searchDelivery = findViewById(R.id.search_delivery);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new DeliveryPersonAdapter(
                this,
                filteredList
        );

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadDeliveryPersons();

        searchDelivery.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,int start,int count,int after){}

            @Override
            public void onTextChanged(
                    CharSequence s,int start,int before,int count){

                filterDelivery(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s){}

        });

    }

    private void loadDeliveryPersons(){

        db.collection("Users")
                .whereEqualTo("role","Delivery Person")
                .addSnapshotListener((value,error)->{

                    if(error!=null){

                        Toast.makeText(
                                this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if(value==null)
                        return;

                    list.clear();

                    for(QueryDocumentSnapshot doc:value){

                        DeliveryPerson person =
                                doc.toObject(
                                        DeliveryPerson.class
                                );

                        person.id = doc.getId();

                        list.add(person);

                    }

                    filteredList.clear();
                    filteredList.addAll(list);

                    adapter.notifyDataSetChanged();

                });

    }

    private void filterDelivery(String text){

        filteredList.clear();

        if(text.isEmpty()){

            filteredList.addAll(list);

        }else{

            for(DeliveryPerson person:list){

                if(person.name
                        .toLowerCase()
                        .contains(text.toLowerCase())){

                    filteredList.add(person);

                }

            }

        }

        adapter.notifyDataSetChanged();

    }

}