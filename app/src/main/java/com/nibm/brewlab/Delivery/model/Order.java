package com.nibm.brewlab.model;

import com.google.firebase.database.DataSnapshot;


public class Order {

    private String orderId;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String address;
    private double latitude;
    private double longitude;
    private double amount;
    private String status;
    private String deliveryPersonId;
    private long createdAt;
    private long deliveredAt;
    private String notes;

    public Order() {
        // Required empty constructor for Firebase
    }

    public Order(String orderId, String customerId, String customerName, String customerPhone,
                 String address, double latitude, double longitude, double amount,
                 String status, String deliveryPersonId, long createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amount = amount;
        this.status = status;
        this.deliveryPersonId = deliveryPersonId;
        this.createdAt = createdAt;
    }

    public static Order fromSnapshot(DataSnapshot snapshot) {
        Order order = new Order();

        order.orderId = snapshot.getKey();
        order.customerId = readString(snapshot, "customerId");
        order.customerName = readString(snapshot, "customerName");
        order.customerPhone = readString(snapshot, "customerPhone");
        order.address = readString(snapshot, "address");
        order.status = readString(snapshot, "status");
        order.deliveryPersonId = readString(snapshot, "deliveryPersonId");
        order.notes = readString(snapshot, "notes");

        order.latitude = readDouble(snapshot, "latitude");
        order.longitude = readDouble(snapshot, "longitude");
        order.amount = readDouble(snapshot, "amount");
        order.createdAt = readLong(snapshot, "createdAt");
        order.deliveredAt = readLong(snapshot, "deliveredAt");

        return order;
    }

    private static String readString(DataSnapshot snapshot, String field) {
        Object value = snapshot.child(field).getValue();
        if (value == null) return null;
        return String.valueOf(value);
    }

    private static double readDouble(DataSnapshot snapshot, String field) {
        Object value = snapshot.child(field).getValue();
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    private static long readLong(DataSnapshot snapshot, String field) {
        Object value = snapshot.child(field).getValue();
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(String deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(long deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}