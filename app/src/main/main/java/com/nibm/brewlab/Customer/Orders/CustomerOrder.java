package com.nibm.brewlab.Customer.Orders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    }

    public CustomerOrder(String uid, String customerName, String totalAmount, String status,
                          String paymentMethod, String deliveryAddress, long timestamp,
                          String itemsSummary, Map<String, Long> itemsData, List<OrderLineItem> items) {
        this.uid = uid;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.timestamp = timestamp;
        this.itemsSummary = itemsSummary;
        this.itemsData = itemsData;
        this.items = items;
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

    // Builds a CustomerOrder from a real "Orders" Firestore document.
    // Firestore's automatic toObject() mapping can't be used here because
    // the field names on the document (address, payment, total, userId)
    // don't match this class's property names (deliveryAddress,
    // paymentMethod, totalAmount, uid) - Admin/Delivery read those raw
    // field names directly, so we mirror that here instead of renaming
    // this whole class.
    public static CustomerOrder fromFirestore(com.google.firebase.firestore.DocumentSnapshot doc) {

        CustomerOrder order = new CustomerOrder();
        order.setId(doc.getId());
        order.setUid(doc.getString("userId"));
        order.setCustomerName(doc.getString("customerName"));
        order.setDeliveryAddress(doc.getString("address"));
        order.setPaymentMethod(doc.getString("payment"));
        order.setStatus(doc.getString("status"));
        order.setStage(doc.getString("stage"));
        order.setDeliveryPersonName(doc.getString("deliveryPersonName"));
        order.setDeliveryPersonUid(doc.getString("deliveryPersonId"));
        order.setItemsSummary(doc.getString("itemsSummary"));

        Double total = doc.getDouble("total");
        order.setTotalAmount(total != null ? String.format("%.2f", total) : "0.00");

        Long timestamp = doc.getLong("timestamp");
        order.setTimestamp(timestamp != null ? timestamp : 0);

        Long deliveredAt = doc.getLong("deliveredAt");
        order.setDeliveredAt(deliveredAt != null ? deliveredAt : 0);

        Long prepStarted = doc.getLong("preparationStartedAt");
        order.setPreparationStartedAt(prepStarted != null ? prepStarted : 0);

        Long acceptedAt = doc.getLong("acceptedAt");
        order.setAcceptedAt(acceptedAt != null ? acceptedAt : 0);

        List<OrderLineItem> lineItems = new ArrayList<>();

        try {
            Object itemsRaw = doc.get("items");

            if (itemsRaw instanceof List) {
                for (Object entry : (List<?>) itemsRaw) {

                    if (!(entry instanceof Map)) continue;
                    Map<?, ?> raw = (Map<?, ?>) entry;

                    String productId = String.valueOf(raw.get("productId"));
                    String name = String.valueOf(raw.get("name"));
                    Object priceObj = raw.get("price");
                    String price = priceObj != null ? String.valueOf(priceObj) : "0";
                    Object qtyObj = raw.get("quantity");
                    int quantity = (qtyObj instanceof Number) ? ((Number) qtyObj).intValue() : 1;
                    String size = raw.get("size") != null ? String.valueOf(raw.get("size")) : "Medium";
                    String sugarLevel = raw.get("sugarLevel") != null ? String.valueOf(raw.get("sugarLevel")) : "Normal";
                    String addOns = raw.get("addOns") != null ? String.valueOf(raw.get("addOns")) : "None";
                    String imageUri = raw.get("imageUri") != null ? String.valueOf(raw.get("imageUri")) : "";
                    String category = raw.get("category") != null ? String.valueOf(raw.get("category")) : "";
                    Object brewObj = raw.get("brewTimeMinutes");
                    int brewTime = (brewObj instanceof Number) ? ((Number) brewObj).intValue() : 4;

                    lineItems.add(new OrderLineItem(productId, name, price, quantity,
                            size, sugarLevel, addOns, imageUri, category, brewTime));
                }
            }
        } catch (Exception ignored) {
            // Older/malformed order documents without a proper "items"
            // array just show up with no itemized breakdown instead of
            // crashing the screen.
        }

        order.setItems(lineItems);

        return order;
    }
}
