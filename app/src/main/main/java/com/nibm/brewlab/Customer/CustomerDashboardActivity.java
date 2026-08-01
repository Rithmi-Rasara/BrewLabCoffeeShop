package com.nibm.brewlab.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.Customer.Cart.CartActivity;
import com.nibm.brewlab.Customer.Feedback.FeedbackActivity;
import com.nibm.brewlab.Customer.Orders.MyOrdersActivity;
import com.nibm.brewlab.Customer.Product.ProductListActivity;
import com.nibm.brewlab.Customer.Profile.MyProfileActivity;
import com.nibm.brewlab.LoginActivity;
import com.nibm.brewlab.R;

public class CustomerDashboardActivity extends AppCompatActivity {

    TextView txtCustomerName, txtLoyaltyPoints;

    LinearLayout cardBrowseProducts, cardMyCart, cardMyOrders, cardMyProfile, cardFeedback;
    android.widget.ImageView imgLogout;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtLoyaltyPoints = findViewById(R.id.txtLoyaltyPoints);

        cardBrowseProducts = findViewById(R.id.cardBrowseProducts);
        cardMyCart = findViewById(R.id.cardMyCart);
        cardMyOrders = findViewById(R.id.cardMyOrders);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        cardFeedback = findViewById(R.id.cardFeedback);
        imgLogout = findViewById(R.id.imgLogout);

        auth = FirebaseAuth.getInstance();

        loadCustomerInfo();

        cardBrowseProducts.setOnClickListener(v -> startActivity(new Intent(this, ProductListActivity.class)));
        cardMyCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        cardMyOrders.setOnClickListener(v -> startActivity(new Intent(this, MyOrdersActivity.class)));
        cardMyProfile.setOnClickListener(v -> startActivity(new Intent(this, MyProfileActivity.class)));
        cardFeedback.setOnClickListener(v -> startActivity(new Intent(this, FeedbackActivity.class)));

        imgLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadCustomerInfo() {

        String uid = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("Users").document(uid)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    String name = snapshot.getString("name");
                    Long points = snapshot.getLong("loyaltyPoints");

                    txtCustomerName.setText(name != null ? name : "Customer");
                    txtLoyaltyPoints.setText((points != null ? points : 0) + " pts");
                });
    }
}
