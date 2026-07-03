package com.nibm.brewlab.Delivery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.DeliveryHistoryAdapter;
import com.nibm.brewlab.R;
import com.nibm.brewlab.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerHistory;
    TextView txtNoHistory;
    EditText edtSearchHistory;
    android.widget.ImageButton btnBack;

    List<Order> allHistory = new ArrayList<>();
    List<Order> filteredHistory = new ArrayList<>();
    DeliveryHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerHistory = findViewById(R.id.recyclerHistory);
        txtNoHistory = findViewById(R.id.txtNoHistory);
        edtSearchHistory = findViewById(R.id.edtSearchHistory);
        btnBack = findViewById(R.id.btnBack);

        adapter = new DeliveryHistoryAdapter(filteredHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setAdapter(adapter);

        loadHistory();

        btnBack.setOnClickListener(v -> finish());

        edtSearchHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        allHistory.clear();
        for (Order order : DeliveryTestData.getOrders()) {
            if ("delivered".equals(order.getStatus())) {
                allHistory.add(order);
            }
        }
        applyFilter(edtSearchHistory.getText().toString().toLowerCase(Locale.getDefault()));
    }

    private void applyFilter(String search) {
        filteredHistory.clear();
        for (Order order : allHistory) {
            boolean matches = search.isEmpty()
                    || (order.getCustomerName() != null && order.getCustomerName().toLowerCase(Locale.getDefault()).contains(search))
                    || (order.getOrderId() != null && order.getOrderId().toLowerCase(Locale.getDefault()).contains(search));
            if (matches) {
                filteredHistory.add(order);
            }
        }
        adapter.notifyDataSetChanged();
        txtNoHistory.setVisibility(filteredHistory.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerHistory.setVisibility(filteredHistory.isEmpty() ? View.GONE : View.VISIBLE);
    }
}