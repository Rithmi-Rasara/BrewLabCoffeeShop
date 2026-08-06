package com.nibm.brewlab.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.Admin.Loyalty.Loyalty;
import com.nibm.brewlab.Customer.Cart.CartActivity;
import com.nibm.brewlab.Customer.Feedback.FeedbackActivity;
import com.nibm.brewlab.Customer.Orders.MyOrdersActivity;
import com.nibm.brewlab.Customer.Product.ProductListActivity;
import com.nibm.brewlab.Customer.Profile.MyProfileActivity;
import com.nibm.brewlab.LoginActivity;
import com.nibm.brewlab.R;

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView txtCustomerName, txtLoyaltyPoints, imgLogout;
    private TextView txtLoyaltyCardPoints, txtMemberLevel, txtNextReward;
    private ProgressBar progressLoyalty;

    private MaterialCardView cardBrowseProducts;
    private MaterialCardView cardMyCart;
    private MaterialCardView cardMyOrders;
    private MaterialCardView cardMyProfile;
    private MaterialCardView cardFeedback;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();

        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtLoyaltyPoints = findViewById(R.id.txtLoyaltyPoints);
        txtLoyaltyCardPoints = findViewById(R.id.txtLoyaltyCardPoints);
        txtMemberLevel = findViewById(R.id.txtMemberLevel);
        txtNextReward = findViewById(R.id.txtNextReward);
        progressLoyalty = findViewById(R.id.progressLoyalty);

        firestore = FirebaseFirestore.getInstance();

        cardBrowseProducts = findViewById(R.id.cardBrowseProducts);
        cardMyCart = findViewById(R.id.cardMyCart);
        cardMyOrders = findViewById(R.id.cardMyOrders);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        cardFeedback = findViewById(R.id.cardFeedback);

        imgLogout = findViewById(R.id.imgLogout);

        loadCustomerInfo();

        cardBrowseProducts.setOnClickListener(v ->
                startActivity(new Intent(CustomerDashboardActivity.this, ProductListActivity.class)));

        cardMyCart.setOnClickListener(v ->
                startActivity(new Intent(CustomerDashboardActivity.this, CartActivity.class)));

        cardMyOrders.setOnClickListener(v ->
                startActivity(new Intent(CustomerDashboardActivity.this, MyOrdersActivity.class)));

        cardMyProfile.setOnClickListener(v ->
                startActivity(new Intent(CustomerDashboardActivity.this, MyProfileActivity.class)));

        cardFeedback.setOnClickListener(v ->
                startActivity(new Intent(CustomerDashboardActivity.this, FeedbackActivity.class)));

        imgLogout.setOnClickListener(v -> {
            auth.signOut();

            Intent intent = new Intent(CustomerDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadCustomerInfo() {

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = snapshot.child("name").getValue(String.class);
                        txtCustomerName.setText(name != null ? name : "Customer");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        loadLoyaltyCard(uid);
    }

    private void loadLoyaltyCard(String uid) {

        // Admin's Manage Loyalty screen (Firestore "Loyalty" collection,
        // document id = uid) is the single source of truth for points/level,
        // so the dashboard card reads from there instead of Realtime DB.
        firestore.collection("Loyalty").document(uid)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) return;

                    if (snapshot == null || !snapshot.exists()) {
                        // No record yet (e.g. this account was created before
                        // this feature existed, or the doc was deleted).
                        // Self-heal by creating a real, uid-linked record so
                        // it also shows up in admin's Manage Loyalty list.
                        createMissingLoyaltyRecord(uid);
                        return;
                    }

                    Long p = snapshot.getLong("points");
                    int points = (p != null) ? p.intValue() : 0;

                    String storedLevel = snapshot.getString("level");
                    String level = storedLevel != null ? storedLevel : Loyalty.calculateLevel(points);

                    txtLoyaltyPoints.setText(points + " pts earned");
                    txtLoyaltyCardPoints.setText(points + " pts");
                    txtMemberLevel.setText(level + " Member");

                    updateProgress(points);
                });
    }

    private void createMissingLoyaltyRecord(String uid) {

        String email = auth.getCurrentUser().getEmail();
        String name = txtCustomerName.getText().toString();

        java.util.HashMap<String, Object> loyalty = new java.util.HashMap<>();
        loyalty.put("name", name);
        loyalty.put("email", email);
        loyalty.put("points", 0);
        loyalty.put("level", "Bronze");

        // set() (not add()) so the document id is this uid, keeping it
        // linked to this customer for admin's Manage Loyalty screen.
        firestore.collection("Loyalty").document(uid).set(loyalty);
    }

    private void updateProgress(int points) {

        int lowerBound;
        int upperBound;
        String nextLevel;

        if (points < 100) {
            lowerBound = 0;
            upperBound = 100;
            nextLevel = "Silver";
        } else if (points < 500) {
            lowerBound = 100;
            upperBound = 500;
            nextLevel = "Gold";
        } else if (points < 1000) {
            lowerBound = 500;
            upperBound = 1000;
            nextLevel = "Platinum";
        } else {
            progressLoyalty.setProgress(100);
            txtNextReward.setText("You've reached the highest tier!");
            return;
        }

        int progressPercent = (int) (((points - lowerBound) * 100.0) / (upperBound - lowerBound));
        progressLoyalty.setProgress(progressPercent);

        int pointsToGo = upperBound - points;
        txtNextReward.setText(pointsToGo + " more points to " + nextLevel);
    }
}