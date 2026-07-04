package com.nibm.brewlab.Admin.Orders;

public class RecentOrder {

    private String customerName;
    private String totalAmount;
    private String status;

    public RecentOrder(String customerName, String totalAmount, String status) {
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }
}