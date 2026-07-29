package com.nibm.brewlab.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.Timestamp;
import java.util.Calendar;

import com.nibm.brewlab.Admin.Feedback.Feedback;
import com.nibm.brewlab.Admin.Feedback.FeedbackAdapter;

import com.nibm.brewlab.Admin.Category.CategoryActivity;
import com.nibm.brewlab.Admin.Customers.CustomersActivity;
import com.nibm.brewlab.Admin.Delivery.ManageDeliveryActivity;
import com.nibm.brewlab.Admin.Inventory.InventoryActivity;
import com.nibm.brewlab.Admin.Loyalty.ManageLoyaltyActivity;
import com.nibm.brewlab.Admin.Orders.Order;
import com.nibm.brewlab.Admin.Orders.OrdersActivity;
import com.nibm.brewlab.Admin.Orders.OrdersAdapter;
import com.nibm.brewlab.Admin.Product.AddProductActivity;
import com.nibm.brewlab.Admin.Product.ManageProductsActivity;
import com.nibm.brewlab.Admin.Inventory.Inventory;
import com.nibm.brewlab.R;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders, recyclerStock, recyclerFeedback;

    private android.widget.ImageView imgLogout;
    ArrayList<Feedback> feedbackList;
    FeedbackAdapter feedbackAdapter;
    private LinearLayout addProduct;
    private LinearLayout manageOrders;
    private LinearLayout manageCategories;
    private LinearLayout viewCustomers;
    private LinearLayout manageDelivery;
    private LinearLayout manageInventory;
    private LinearLayout cardManageLoyalty;

    private TextView txtAdminName;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private ArrayList<Order> orderList;
    private ArrayList<Inventory> lowStockList;

    private OrdersAdapter ordersAdapter;
    private LowStockAdapter lowStockAdapter;
    private TextView txtProductsCount;
    private TextView txtOrdersCount;
    private TextView txtLowStock;
    private TextView txtTodayRevenue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupAdapters();

        loadAdminName();
        loadRecentOrders();
        loadLowStock();

        setupClicks();

        loadProductsCount();
        loadOrdersCount();
        loadLowStockCount();
        calculateTodayRevenue();

        loadFeedback();
    }

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();

        txtAdminName = findViewById(R.id.txtAdminName);

        imgLogout = findViewById(R.id.imgLogout);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerStock = findViewById(R.id.recyclerStock);
        recyclerFeedback = findViewById(R.id.recyclerFeedback);

        addProduct = findViewById(R.id.addProduct);
        manageOrders = findViewById(R.id.manageOrders);
        manageCategories = findViewById(R.id.manageCategories);
        viewCustomers = findViewById(R.id.viewCustomers);
        manageDelivery = findViewById(R.id.manageDelivery);
        manageInventory = findViewById(R.id.manageInventory);
        cardManageLoyalty = findViewById(R.id.manageLoyalty);
        txtProductsCount = findViewById(R.id.txtProductsCount);
        txtOrdersCount = findViewById(R.id.txtOrdersCount);
        txtLowStock = findViewById(R.id.txtLowStock);
        txtTodayRevenue = findViewById(R.id.txtTodayRevenue);
        feedbackList = new ArrayList<>();

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerFeedback.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        lowStockList = new ArrayList<>();
    }

    private void loadProductsCount() {

        FirebaseFirestore.getInstance()
                .collection("Products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->

                        txtProductsCount.setText(
                                String.valueOf(queryDocumentSnapshots.size())
                        )
                );

    }

    private void loadOrdersCount() {

        FirebaseFirestore.getInstance()
                .collection("Orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->

                        txtOrdersCount.setText(
                                String.valueOf(queryDocumentSnapshots.size())
                        )
                );

    }

    private void loadLowStockCount() {

        FirebaseFirestore.getInstance()
                .collection("Inventory")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int low = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        Long qty = doc.getLong("quantity");

                        if (qty != null && qty <= 10) {
                            low++;
                        }

                    }

                    txtLowStock.setText(String.valueOf(low));

                });

    }

    private void calculateTodayRevenue(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());


        db.collection("orders")
                .whereEqualTo("date", today)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {


                    double total = 0;


                    for(DocumentSnapshot document : queryDocumentSnapshots){

                        Double amount =
                                document.getDouble("totalAmount");


                        if(amount != null){
                            total += amount;
                        }

                    }


                    txtTodayRevenue.setText(
                            "Rs. " + String.format("%.2f", total)
                    );


                })
                .addOnFailureListener(e -> {

                    txtTodayRevenue.setText("Rs. 0");

                });

    }
    private void setupAdapters() {

        ordersAdapter = new OrdersAdapter(orderList);
        lowStockAdapter = new LowStockAdapter(lowStockList);

        recyclerOrders.setAdapter(ordersAdapter);
        recyclerStock.setAdapter(lowStockAdapter);

        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerFeedback.setAdapter(feedbackAdapter);
    }

    private void loadRecentOrders() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Orders")
                .limit(10)
                .addSnapshotListener((value, error) -> {

                    if(error != null || value == null){
                        return;
                    }

                    orderList.clear();

                    for(DocumentSnapshot doc : value.getDocuments()){

                        Order order = doc.toObject(Order.class);

                        if(order != null){

                            order.setId(doc.getId());

                            orderList.add(order);
                        }
                    }

                    ordersAdapter.notifyDataSetChanged();
                });
    }

    private void loadLowStock(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Inventory")
                .addSnapshotListener((value,error)->{

                    if(error != null || value == null){
                        return;
                    }

                    lowStockList.clear();


                    for(DocumentSnapshot doc:value){

                        String name = doc.getString("name");

                        Long qty = doc.getLong("quantity");

                        if(qty != null && qty <= 10){

                            Inventory item = new Inventory();

                            item.setId(doc.getId());
                            item.setName(name);
                            item.setQuantity(qty.intValue());

                            lowStockList.add(item);
                        }
                    }

                    lowStockAdapter.notifyDataSetChanged();

                });
    }

    private void loadFeedback() {

        FirebaseFirestore.getInstance()
                .collection("Feedback")
                .limit(10)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null)
                        return;

                    feedbackList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        Feedback feedback = doc.toObject(Feedback.class);

                        if (feedback != null) {
                            feedback.setId(doc.getId());
                            feedbackList.add(feedback);
                        }
                    }

                    feedbackAdapter.notifyDataSetChanged();
                });
    }

    private void loadAdminName() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            txtAdminName.setText("Administrator");
            return;
        }

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);

        userRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        String name =
                                snapshot.child("name")
                                        .getValue(String.class);

                        txtAdminName.setText(
                                (name != null && !name.isEmpty())
                                        ? name
                                        : "Administrator"
                        );
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        txtAdminName.setText("Administrator");
                    }
                });
    }

    private void setupClicks() {

        addProduct.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        ManageProductsActivity.class)));

        manageOrders.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        OrdersActivity.class)));

        manageCategories.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        CategoryActivity.class)));

        viewCustomers.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        CustomersActivity.class)));

        manageDelivery.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        ManageDeliveryActivity.class)));

        manageInventory.setOnClickListener(v ->
                startActivity(new Intent(
                        AdminDashboardActivity.this,
                        InventoryActivity.class)));

        cardManageLoyalty.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this,
                    ManageLoyaltyActivity.class);
            startActivity(intent);
        });

        imgLogout.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(AdminDashboardActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setCancelable(false)

                    .setPositiveButton("Logout", (dialog, which) -> {

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(AdminDashboardActivity.this,
                                LoginActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish();

                    })

                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())

                    .show();

        });
    }
}

