package com.nibm.brewlab.Admin.Orders;

public class Order {

    private String id;
    private String userId;

    private String customerName;
    private String customerPhone;
    private String deliveryAddress;

    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;

    private double totalAmount;

    // Delivery Assignment
    private String assignedDeliveryId;
    private String assignedDeliveryName;
    private String assignedDeliveryPhone;
    private String assignedVehicleNumber;

    public Order() {
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getAssignedDeliveryId() {
        return assignedDeliveryId;
    }

    public String getAssignedDeliveryName() {
        return assignedDeliveryName;
    }

    public String getAssignedDeliveryPhone() {
        return assignedDeliveryPhone;
    }

    public String getAssignedVehicleNumber() {
        return assignedVehicleNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setAssignedDeliveryId(String assignedDeliveryId) {
        this.assignedDeliveryId = assignedDeliveryId;
    }

    public void setAssignedDeliveryName(String assignedDeliveryName) {
        this.assignedDeliveryName = assignedDeliveryName;
    }

    public void setAssignedDeliveryPhone(String assignedDeliveryPhone) {
        this.assignedDeliveryPhone = assignedDeliveryPhone;
    }

    public void setAssignedVehicleNumber(String assignedVehicleNumber) {
        this.assignedVehicleNumber = assignedVehicleNumber;
    }
}