package dev.matthewpotts.fulfiller;

import com.google.gson.annotations.SerializedName;

class Fulfillment {


    @SerializedName("order_id")
    private String orderID;
    @SerializedName("id")
    private String id;
    @SerializedName("status")
    private String status;
    @SerializedName("tracking_number")
    private String trackingNumber;

//    pending: The fulfillment is pending.
//    open: The fulfillment has been acknowledged by the service and is in processing.
//    success: The fulfillment was successful.
//    cancelled: The fulfillment was cancelled.
//    error: There was an error with the fulfillment request.
//    failure: The fulfillment request failed.

    Fulfillment(String orderID, String id, String status) {
        this.orderID = orderID;
        this.id = id;
        this.status = status;
    }


    boolean statusIsFulfilled(){
        return status.equals("success");
    }

    boolean statusIsCancelled(){
        return status.equals("cancelled");
    }

    String getID() {
        return id;
    }

    String getTrackingNumber() {
        if(trackingNumber == null) {
            return "";
        }
        return trackingNumber;
    }

}
