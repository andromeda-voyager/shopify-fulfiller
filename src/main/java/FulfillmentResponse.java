package sample;

import com.google.gson.annotations.SerializedName;

class FulfillmentResponse {

    @SerializedName("fulfillment")
    private Fulfillment fulfillment;

    FulfillmentResponse(String orderID, String fulfillmentID, String status) {
        fulfillment = new Fulfillment(orderID, fulfillmentID, status);
    }

    Fulfillment getFulfillment() {
        return fulfillment;
    }


}
