package com.nibm.brewlab.Admin.Orders;

import com.google.firebase.database.DataSnapshot;

public class OrderMapper {

    public static Order fromSnapshot(DataSnapshot snap) {

        Order order = new Order();

        order.setId(snap.getKey());
        order.setUserId(snap.child("uid").getValue(String.class));
        order.setCustomerName(snap.child("customerName").getValue(String.class));
        order.setDeliveryAddress(snap.child("deliveryAddress").getValue(String.class));
        order.setPaymentMethod(snap.child("paymentMethod").getValue(String.class));
        order.setOrderStatus(snap.child("status").getValue(String.class));

        String totalStr = snap.child("totalAmount").getValue(String.class);
        if (totalStr != null) {
            try {
                order.setTotalAmount(Double.parseDouble(totalStr));
            } catch (NumberFormatException ignored) {
            }
        }

        order.setAssignedDeliveryId(snap.child("deliveryPersonUid").getValue(String.class));
        order.setAssignedDeliveryName(snap.child("deliveryPersonName").getValue(String.class));

        return order;
    }
}
