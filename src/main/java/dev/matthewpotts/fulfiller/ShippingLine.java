package dev.matthewpotts.fulfiller;

import com.google.gson.annotations.SerializedName;

class ShippingLine {

    @SerializedName("title")
    private String shippingMethod;

    @SerializedName("price")
    private String price;

    String getShippingMethod(){
        return shippingMethod;
    }

    String getPrice(){
        return price;
    }

}
