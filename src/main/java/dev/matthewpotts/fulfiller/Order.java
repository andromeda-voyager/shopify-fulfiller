package dev.matthewpotts.fulfiller;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Order implements Comparable<Order>{
    @SerializedName("id")
    private String ID;

    @SerializedName("name")
    private String invoice;

    @SerializedName("subtotal_price")
    private String subtotalPrice;

    @SerializedName("total_price")
    private String totalPrice;

    @SerializedName("fulfillment_status")
    private String fulfillmentStatus;  // fulfilled, null, or partial.

    @SerializedName("total_weight")
    private int totalWeight;

    @SerializedName("shipping_address")
    private Address shippingAddress;

    @SerializedName("billing_address")
    private Address billingAddress;

    @SerializedName("shipping_lines")
    private ShippingLine[] shippingLines;

    @SerializedName("line_items")
    private ArrayList<LineItem> lineItems;

    @SerializedName("fulfillments")
    private ArrayList<Fulfillment> fulfillments;

    public Order(Address shippingAddress, String invoice){
        this.shippingAddress = shippingAddress;
        this.invoice = invoice;
        this.billingAddress = shippingAddress;

    }

    Address getShippingAddress() {
        return shippingAddress;
    }

    Address getBillingAddress() {
        return billingAddress;
    }

    boolean hasName(String name) {
        return getShippingAddress().getName().toLowerCase().contains(name) || getBillingAddress().getName().toLowerCase().contains(name);
    }


    String getShippingMethod() {
        if (shippingLines != null) {
            if (shippingLines.length > 0) {
                return shippingLines[0].getShippingMethod();
            }
        }
        return "";
    }

    String getShippingPrice(){
        if (shippingLines != null) {
            if (shippingLines.length > 0) {
                return "$" + shippingLines[0].getPrice();
            }
        }
        return "";
    }

    String getWeight() {
        return Double.toString(totalWeight * (0.035274)).replaceAll("[.].*", "") + " lb";
    }

    String getName() {
        return billingAddress.getName();
    }

    String getStatus(){
        if(fulfillmentStatus == null) {
            return "Unfulfilled";
        }
        else {
            return "Fulfilled";
        }
    }

    String getInvoice() {
        return invoice.substring(1);
    } //removes '#' at the front of the invoice name

    // TODO there might only be one fulfillment. Also what if it keeps cancelled fulfillments (look into this)
    private String getFulfillmentID(){
        for (Fulfillment fulfillment: fulfillments) {
            if(fulfillment.statusIsFulfilled()) {
                return fulfillment.getID();
            }

        }
        return "";// This is technically impossible. TODO Change this
    }

    public String toString(){
        return this.getName() + " " + invoice;
    }

    void fulfill(String trackingNumber){
        FulfillmentResponse fulfillmentResponse = ShopifyClient.postFulfillment(ID, trackingNumber);
        if(fulfillmentResponse.getFulfillment().statusIsFulfilled()) {
            fulfillments.add(fulfillmentResponse.getFulfillment());
            fulfillmentStatus = "fulfilled";
        }
    }

    void cancelFulfillment() {
        FulfillmentResponse fulfillmentResponse = ShopifyClient.cancelFulfillment(ID, getFulfillmentID());
        if(fulfillmentResponse.getFulfillment().statusIsCancelled()) {
            fulfillmentStatus = null;
        }
    }

    boolean isFulfilled(){
        return (fulfillmentStatus != null);
    }
    boolean isUnfulfilled(){
        return (fulfillmentStatus == null);
    }

    String getTrackingNumber(){
        for (Fulfillment fulfillment: fulfillments) {
            if(fulfillment.statusIsFulfilled()) {
                return fulfillment.getTrackingNumber();
            }
        }
        return "";
    }

    ArrayList<LineItem> getLineItems(){
        return lineItems;
    }

    String getTotalPrice() {
        return "$" + totalPrice;
    }

    String getSubtotalPrice() {
        return "$" + subtotalPrice;
    }

    String getID(){
        return ID;
    }

    @Override
    public int compareTo(Order compareOrder) {
        return this.getInvoice().compareTo(compareOrder.getInvoice());
    }
}
