package com.nibm.brewlab.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.DeliveryOrderAdapter;
import com.nibm.brewlab.DeliveryProfileActivity;
import com.nibm.brewlab.LoginActivity;
import com.nibm.brewlab.R;
import com.nibm.brewlab.model.Order;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDashboardActivity extends AppCompatActivity {

    TextView txtDeliveryName, txtAvatarInitial, txtAssignedCount, txtDeliveredCount, txtNoRecentOrders;
    RecyclerView recyclerRecentOrders;
    View searchBarDashboard, btnAssignedOrders, btnDeliveryHistory, btnProfile, btnLogoutCard;
    android.widget.ImageButton btnLogout;

    List<Order> recentOrders = new ArrayList<>();
    DeliveryOrderAdapter recentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();
        loadDeliveryPersonInfo();
        loadOrderStats();

        recentAdapter = new DeliveryOrderAdapter(recentOrders, new DeliveryOrderAdapter.OnOrderActionListener() {
            @Override
            public void onAccept(Order order) {
                updateOrderStatus(order, "accepted");
            }

            @Override
            public void onDecline(Order order) {
                updateOrderStatus(order, "declined");
            }

            @Override
            public void onStartDelivery(Order order) {
                openOrderDelivery(order);
            }

            @Override
            public void onOpenOrder(Order order) {
                if (order.getStatus() != null && !order.getStatus().equals("assigned")
                        && !order.getStatus().equals("delivered") && !order.getStatus().equals("declined")) {
                    openOrderDelivery(order);
                }
            }
        });
        recyclerRecentOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecentOrders.setAdapter(recentAdapter);
        loadRecentOrders();

        searchBarDashboard.setOnClickListener(v -> startActivity(new Intent(this, DeliveryOrdersActivity.class)));

        btnAssignedOrders.setOnClickListener(v -> startActivity(new Intent(this, DeliveryOrdersActivity.class)));

        btnDeliveryHistory.setOnClickListener(v -> startActivity(new Intent(this, DeliveryHistoryActivity.class)));

        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, DeliveryProfileActivity.class)));

        btnLogoutCard.setOnClickListener(v -> confirmLogout());

        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderStats();
        loadRecentOrders();
    }

    private void bindViews() {
        txtDeliveryName = findViewById(R.id.txtDeliveryName);
        txtAvatarInitial = findViewById(R.id.txtAvatarInitial);
        txtAssignedCount = findViewById(R.id.txtAssignedCount);
        txtDeliveredCount = findViewById(R.id.txtDeliveredCount);
        txtNoRecentOrders = findViewById(R.id.txtNoRecentOrders);
        recyclerRecentOrders = findViewById(R.id.recyclerRecentOrders);
        searchBarDashboard = findViewById(R.id.searchBarDashboard);
        btnAssignedOrders = findViewById(R.id.btnAssignedOrders);
        btnDeliveryHistory = findViewById(R.id.btnDeliveryHistory);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogoutCard = findViewById(R.id.btnLogoutCard);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadDeliveryPersonInfo() {
        txtDeliveryName.setText("Delivery Partner");
        txtAvatarInitial.setText("D");
    }

    private void loadOrderStats() {
        int assigned = 0;
        int delivered = 0;
        for (Order order : DeliveryTestData.getOrders()) {
            String status = order.getStatus();
            if (status == null) continue;
            if (status.equals("delivered")) {
                delivered++;
            } else if (!status.equals("declined")) {
                assigned++;
            }
        }
        txtAssignedCount.setText(String.valueOf(assigned));
        txtDeliveredCount.setText(String.valueOf(delivered));
    }

    private void loadRecentOrders() {
        recentOrders.clear();
        for (Order order : DeliveryTestData.getOrders()) {
            if (order.getStatus() != null
                    && !"delivered".equals(order.getStatus())
                    && !"declined".equals(order.getStatus())) {
                recentOrders.add(order);
            }
            if (recentOrders.size() >= 3) break;
        }
        if (recentAdapter != null) {
            recentAdapter.notifyDataSetChanged();
        }
        txtNoRecentOrders.setVisibility(recentOrders.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerRecentOrders.setVisibility(recentOrders.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void updateOrderStatus(Order order, String newStatus) {
        order.setStatus(newStatus);
        loadOrderStats();
        loadRecentOrders();
        Toast.makeText(this,
                newStatus.equals("accepted") ? "Order accepted" : "Order declined",
                Toast.LENGTH_SHORT).show();
    }

    private void openOrderDelivery(Order order) {
        Intent intent = new Intent(this, OrderDeliveryActivity.class);
        intent.putExtra("orderId", order.getOrderId());
        startActivity(intent);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}