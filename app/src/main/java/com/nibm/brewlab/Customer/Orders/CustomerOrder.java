package com.nibm.brewlab.Customer.Orders;

import java.util.HashMap;
import java.util.Map;

public class CustomerOrder {

    private String id;

    // NEW
    private String orderId;
    private String customerId;

    private String uid;
    private String customerName;
    private String totalAmount;
    private String status;
    private String paymentMethod;
    private String deliveryAddress;
    private long timestamp;
    private String itemsSummary;
    private Map<String, Long> itemsData;

    // ===========================
    // NEW: live delivery location
    // Delivery person's app should push these two values
    // to  Orders/{orderId}/deliveryLat  and  .../deliveryLng
    // whenever status == "On the way"
    // ===========================
    private Double deliveryLat;
    private Double deliveryLng;

    public CustomerOrder() {
        itemsData = new HashMap<>();
    }

    public CustomerOrder(String uid,
                         String customerName,
                         String totalAmount,
                         String status,
                         String paymentMethod,
                         String deliveryAddress,
                         long timestamp,
                         String itemsSummary,
                         Map<String, Long> itemsData) {

        this.uid = uid;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.timestamp = timestamp;
        this.itemsSummary = itemsSummary;
        this.itemsData = itemsData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // ===========================
    // Order ID
    // ===========================

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    // ===========================
    // Customer ID
    // ===========================

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // ===========================
    // UID
    // ===========================

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // ===========================
    // Customer Name
    // ===========================

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // ===========================
    // Total Amount
    // ===========================

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    // ===========================
    // Status
    // ===========================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ===========================
    // Payment
    // ===========================

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // ===========================
    // Address
    // ===========================

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    // ===========================
    // Time
    // ===========================

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // ===========================
    // Items Summary
    // ===========================

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }

    // ===========================
    // Items
    // ===========================

    public Map<String, Long> getItemsData() {
        return itemsData;
    }

    public void setItemsData(Map<String, Long> itemsData) {
        this.itemsData = itemsData;
    }

    // ===========================
    // NEW: Delivery live location
    // ===========================

    public Double getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(Double deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public Double getDeliveryLng() {
        return deliveryLng;
    }

    public void setDeliveryLng(Double deliveryLng) {
        this.deliveryLng = deliveryLng;
    }
}
