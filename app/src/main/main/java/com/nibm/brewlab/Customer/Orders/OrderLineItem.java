package com.nibm.brewlab.Customer.Orders;

public class OrderLineItem {

    private String productId;
    private String name;
    private String price;
    private int quantity;
    private String size;
    private String sugarLevel;
    private String addOns;
    private String imageUri;
    private String category;
    private int brewTimeMinutes;

    public OrderLineItem() {
    }

    public OrderLineItem(String productId, String name, String price, int quantity,
                          String size, String sugarLevel, String addOns,
                          String imageUri, String category, int brewTimeMinutes) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.sugarLevel = sugarLevel;
        this.addOns = addOns;
        this.imageUri = imageUri;
        this.category = category;
        this.brewTimeMinutes = brewTimeMinutes;
    }

    // Rough brew/prep time estimate by coffee category - used when an order
    // is placed so Order Details can show "how long this item takes to make".
    public static int estimateBrewTime(String category, String size) {

        int minutes;

        if (category == null) {
            minutes = 4;
        } else if (category.equalsIgnoreCase("Cold Coffee")) {
            minutes = 3;
        } else if (category.equalsIgnoreCase("Frappe")) {
            minutes = 6;
        } else {
            minutes = 4; // Hot Coffee / default
        }

        if ("Large".equalsIgnoreCase(size)) {
            minutes += 1;
        }

        return minutes;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getBrewTimeMinutes() {
        return brewTimeMinutes;
    }

    public void setBrewTimeMinutes(int brewTimeMinutes) {
        this.brewTimeMinutes = brewTimeMinutes;
    }
}
