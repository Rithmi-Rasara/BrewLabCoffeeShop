package com.nibm.brewlab.Customer.Cart;

public class CartItem {

    private String productId;
    private String name;
    private String price;
    private String category;
    private String imageUri;
    private int quantity;
    private String size;
    private String sugarLevel;
    private String addOns;
    private String cartKey;

    // NEW: Used to mark whether this item is selected for checkout
    private boolean selected = false;

    public CartItem() {
    }

    public CartItem(String productId, String name, String price, String category, String imageUri, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUri = imageUri;
        this.quantity = quantity;
        this.size = "Medium";
        this.sugarLevel = "Normal";
        this.addOns = "None";
        this.selected = false;
    }

    public CartItem(String productId, String name, String price, String category, String imageUri,
                    int quantity, String size, String sugarLevel, String addOns) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUri = imageUri;
        this.quantity = quantity;
        this.size = size;
        this.sugarLevel = sugarLevel;
        this.addOns = addOns;
        this.selected = false;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(String sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public String getAddOns() {
        return addOns;
    }

    public void setAddOns(String addOns) {
        this.addOns = addOns;
    }

    public String getCartKey() {
        return cartKey;
    }

    public void setCartKey(String cartKey) {
        this.cartKey = cartKey;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // NEW
    public boolean isSelected() {
        return selected;
    }

    // NEW
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}