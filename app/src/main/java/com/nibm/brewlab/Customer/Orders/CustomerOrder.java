package com.nibm.brewlab.Customer.Orders;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
=======
import java.util.HashMap;
>>>>>>> origin/develop
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
<<<<<<< HEAD
    private List<OrderLineItem> items;
    private long preparationStartedAt;
    private String deliveryPersonUid;
    private String deliveryPersonName;
    private String stage;
    private long acceptedAt;
    private long deliveredAt;

    public CustomerOrder() {
        itemsData = new HashMap<>();
        items = new ArrayList<>();
=======

    public CustomerOrder() {
        itemsData = new HashMap<>();
>>>>>>> origin/develop
    }

    public CustomerOrder(String uid, String customerName, String totalAmount, String status,
                          String paymentMethod, String deliveryAddress, long timestamp,
<<<<<<< HEAD
                          String itemsSummary, Map<String, Long> itemsData, List<OrderLineItem> items) {
=======
                          String itemsSummary, Map<String, Long> itemsData) {
>>>>>>> origin/develop
        this.uid = uid;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.timestamp = timestamp;
        this.itemsSummary = itemsSummary;
        this.itemsData = itemsData;
<<<<<<< HEAD
        this.items = items;
=======
>>>>>>> origin/develop
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
<<<<<<< HEAD

    public List<OrderLineItem> getItems() {
        return items;
    }

    public void setItems(List<OrderLineItem> items) {
        this.items = items;
    }

    public long getPreparationStartedAt() {
        return preparationStartedAt;
    }

    public void setPreparationStartedAt(long preparationStartedAt) {
        this.preparationStartedAt = preparationStartedAt;
    }

    public String getDeliveryPersonUid() {
        return deliveryPersonUid;
    }

    public void setDeliveryPersonUid(String deliveryPersonUid) {
        this.deliveryPersonUid = deliveryPersonUid;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public long getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(long acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public long getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
=======
>>>>>>> origin/develop
}
