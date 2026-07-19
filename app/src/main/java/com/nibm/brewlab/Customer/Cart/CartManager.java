package com.nibm.brewlab.Customer.Cart;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nibm.brewlab.Admin.Product.Product;

/**
 * Small helper class that keeps the customer's cart inside
 * Users/{uid}/Cart/{cartKey} on Firebase Realtime Database.
 *
 * cartKey is built from productId + size + sugarLevel + addOns so that
 * the SAME coffee ordered with DIFFERENT customization (e.g. Cappuccino
 * Large vs Cappuccino Small) is stored as two separate cart lines instead
 * of overwriting each other.
 */
public class CartManager {

    public static DatabaseReference getCartRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("Cart");
    }

    // Firebase keys cannot contain '.', '#', '$', '[', ']', '/' or spaces.
    private static String sanitize(String value) {
        if (value == null) return "x";
        return value.trim()
                .replaceAll("[^A-Za-z0-9]", "_")
                .toLowerCase();
    }

    public static String buildCartKey(String productId, String size, String sugarLevel, String addOns) {
        return sanitize(productId) + "_" + sanitize(size) + "_" + sanitize(sugarLevel) + "_" + sanitize(addOns);
    }

    // Add a plain product with no customization dialog (kept for places that
    // don't need it, e.g. quick reorder uses default customization values).
    public static void addToCart(Context context, Product product) {
        addToCart(context, product, "Medium", "Normal", "None", 0);
    }

    // Add a customized product to the cart.
    public static void addToCart(Context context, Product product,
                                  String size, String sugarLevel, String addOns, double extraPrice) {

        String cartKey = buildCartKey(product.getId(), size, sugarLevel, addOns);
        DatabaseReference itemRef = getCartRef().child(cartKey);

        double basePrice;
        try {
            basePrice = Double.parseDouble(product.getPrice());
        } catch (NumberFormatException e) {
            basePrice = 0;
        }

        double finalPrice = basePrice + extraPrice;

        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    CartItem existing = snapshot.getValue(CartItem.class);
                    int newQty = (existing != null ? existing.getQuantity() : 0) + 1;

                    itemRef.child("quantity").setValue(newQty);

                } else {

                    CartItem item = new CartItem(
                            product.getId(),
                            product.getName(),
                            String.format("%.2f", finalPrice),
                            product.getCategory(),
                            product.getImageUri(),
                            1,
                            size,
                            sugarLevel,
                            addOns
                    );

                    itemRef.setValue(item);
                }

                Toast.makeText(context, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void updateQuantity(String cartKey, int quantity) {

        if (quantity <= 0) {
            getCartRef().child(cartKey).removeValue();
        } else {
            getCartRef().child(cartKey).child("quantity").setValue(quantity);
        }
    }

    public static void removeItem(String cartKey) {
        getCartRef().child(cartKey).removeValue();
    }

    public static void clearCart() {
        getCartRef().removeValue();
    }
}
