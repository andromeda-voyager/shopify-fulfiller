package dev.matthewpotts.fulfiller;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class ShopifyProductsResponse {

    @SerializedName("orders")
    private ArrayList<Order> orders;


    ArrayList<Order> getOrders() {
        return orders;
    }

    ShopifyProductsResponse() {
        orders = new ArrayList<>();
    }

    void combineWith(ShopifyProductsResponse shopifyProductsResponse) {
        this.orders.addAll(shopifyProductsResponse.orders);
    }

    String getNewestOrderID() {
        if (orders.size() > 0) {
            return orders.get(0).getID();
        }
        return "";
    }

    boolean hasOrders() {
        return orders.size() > 0;
    }
}
