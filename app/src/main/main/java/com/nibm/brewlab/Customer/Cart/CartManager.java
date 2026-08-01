package com.nibm.brewlab.Customer.Cart;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.Admin.Product.Product;

/**
 * Small helper class that keeps the customer's cart inside
 * Users/{uid}/Cart/{cartKey} on Cloud Firestore (matches the rest of the
 * project - Admin manages Products/Users/Orders in Firestore too).
 *
 * cartKey is built from productId + size + sugarLevel + addOns so that
 * the SAME coffee ordered with DIFFERENT customization (e.g. Cappuccino
 * Large vs Cappuccino Small) is stored as two separate cart lines instead
 * of overwriting each other.
 */
public class CartManager {

    public static CollectionReference getCartRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .collection("Cart");
    }

    // Firestore document IDs cannot contain '/'; keep it simple and safe.
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
        DocumentReference itemRef = getCartRef().document(cartKey);

        double basePrice;
        try {
            basePrice = Double.parseDouble(product.getPrice());
        } catch (NumberFormatException e) {
            basePrice = 0;
        }

        double finalPrice = basePrice + extraPrice;

        itemRef.get().addOnSuccessListener(snapshot -> {

            if (snapshot.exists()) {

                CartItem existing = snapshot.toObject(CartItem.class);
                int newQty = (existing != null ? existing.getQuantity() : 0) + 1;

                itemRef.update("quantity", newQty);

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

                itemRef.set(item);
            }

            Toast.makeText(context, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e ->
                Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void updateQuantity(String cartKey, int quantity) {

        if (quantity <= 0) {
            getCartRef().document(cartKey).delete();
        } else {
            getCartRef().document(cartKey).update("quantity", quantity);
        }
    }

    public static void removeItem(String cartKey) {
        getCartRef().document(cartKey).delete();
    }

    public static void clearCart() {
        getCartRef().get().addOnSuccessListener(snapshot ->
                snapshot.forEach(doc -> doc.getReference().delete()));
    }
}
