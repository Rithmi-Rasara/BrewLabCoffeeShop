package com.nibm.brewlab.Customer.Orders;

import java.util.HashMap;
import java.util.Map;

public class CustomerOrder {

    private String id;
    private String uid;
    private String customerName;
    private String totalAmount;
    private String status;
    private String paymentMethod;
    private String deliveryAddress;
    private long timestamp;
    private String itemsSummary;
    private Map<String, Long> itemsData;

    public CustomerOrder() {
        itemsData = new HashMap<>();
    }

    public CustomerOrder(String uid, String customerName, String totalAmount, String status,
                          String paymentMethod, String deliveryAddress, long timestamp,
                          String itemsSummary, Map<String, Long> itemsData) {
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }

    public Map<String, Long> getItemsData() {
        return itemsData;
    }

    public void setItemsData(Map<String, Long> itemsData) {
        this.itemsData = itemsData;
    }
}
