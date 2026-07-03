package com.nibm.brewlab.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.DeliveryOrderAdapter;
import com.nibm.brewlab.R;
import com.nibm.brewlab.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerOrders;
    TextView txtNoOrders, tabAll, tabPending, tabActive, tabDone;
    EditText edtSearchOrders;
    android.widget.ImageButton btnBack;

    List<Order> allOrders = new ArrayList<>();
    List<Order> filteredOrders = new ArrayList<>();
    DeliveryOrderAdapter adapter;

    String currentTab = "ALL";
    String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();

        adapter = new DeliveryOrderAdapter(filteredOrders, new DeliveryOrderAdapter.OnOrderActionListener() {
            @Override
            public void onAccept(Order order) { updateStatus(order, "accepted"); }

            @Override
            public void onDecline(Order order) { updateStatus(order, "declined"); }

            @Override
            public void onStartDelivery(Order order) { openOrderDelivery(order); }

            @Override
            public void onOpenOrder(Order order) {
                String status = order.getStatus();
                if (status != null && !status.equals("assigned")
                        && !status.equals("delivered") && !status.equals("declined")) {
                    openOrderDelivery(order);
                }
            }
        });
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(adapter);

        loadOrders();

        btnBack.setOnClickListener(v -> finish());

        tabAll.setOnClickListener(v -> selectTab("ALL"));
        tabPending.setOnClickListener(v -> selectTab("PENDING"));
        tabActive.setOnClickListener(v -> selectTab("ACTIVE"));
        tabDone.setOnClickListener(v -> selectTab("DONE"));

        edtSearchOrders.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().toLowerCase(Locale.getDefault());
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Pick up any status changes made on OrderDeliveryActivity
        loadOrders();
    }

    private void bindViews() {
        recyclerOrders = findViewById(R.id.recyclerOrders);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        edtSearchOrders = findViewById(R.id.edtSearchOrders);
        btnBack = findViewById(R.id.btnBack);
        tabAll = findViewById(R.id.tabAll);
        tabPending = findViewById(R.id.tabPending);
        tabActive = findViewById(R.id.tabActive);
        tabDone = findViewById(R.id.tabDone);
    }

    private void selectTab(String tab) {
        currentTab = tab;
        tabAll.setBackgroundResource(tab.equals("ALL") ? R.drawable.tab_selected_bg : R.drawable.tab_unselected_bg);
        tabPending.setBackgroundResource(tab.equals("PENDING") ? R.drawable.tab_selected_bg : R.drawable.tab_unselected_bg);
        tabActive.setBackgroundResource(tab.equals("ACTIVE") ? R.drawable.tab_selected_bg : R.drawable.tab_unselected_bg);
        tabDone.setBackgroundResource(tab.equals("DONE") ? R.drawable.tab_selected_bg : R.drawable.tab_unselected_bg);
        applyFilter();
    }

    private void loadOrders() {
        allOrders.clear();
        allOrders.addAll(DeliveryTestData.getOrders());
        applyFilter();
    }

    private void applyFilter() {
        filteredOrders.clear();
        for (Order order : allOrders) {
            String status = order.getStatus() == null ? "assigned" : order.getStatus();
            boolean matchesTab;

            switch (currentTab) {
                case "PENDING":
                    matchesTab = status.equals("assigned");
                    break;
                case "ACTIVE":
                    matchesTab = status.equals("accepted") || status.equals("on_the_way")
                            || status.equals("arrived") || status.equals("updated");
                    break;
                case "DONE":
                    matchesTab = status.equals("delivered");
                    break;
                default:
                    matchesTab = true;
            }

            boolean matchesSearch = currentSearch.isEmpty()
                    || (order.getCustomerName() != null && order.getCustomerName().toLowerCase(Locale.getDefault()).contains(currentSearch))
                    || (order.getOrderId() != null && order.getOrderId().toLowerCase(Locale.getDefault()).contains(currentSearch))
                    || (order.getAddress() != null && order.getAddress().toLowerCase(Locale.getDefault()).contains(currentSearch));

            if (matchesTab && matchesSearch) {
                filteredOrders.add(order);
            }
        }
        adapter.notifyDataSetChanged();
        txtNoOrders.setVisibility(filteredOrders.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerOrders.setVisibility(filteredOrders.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void updateStatus(Order order, String newStatus) {
        if (order.getOrderId() == null) return;
        DeliveryTestData.updateStatus(order.getOrderId(), newStatus);
        applyFilter();
        Toast.makeText(this,
                newStatus.equals("accepted") ? "Order accepted" : "Order declined",
                Toast.LENGTH_SHORT).show();
    }

    private void openOrderDelivery(Order order) {
        Intent intent = new Intent(this, OrderDeliveryActivity.class);
        intent.putExtra("orderId", order.getOrderId());
        startActivity(intent);
    }
}