package sample;

import com.google.gson.annotations.SerializedName;

class FulfillmentRequest {

    @SerializedName("fulfillment")
    private Fulfillment fulfillment;

    FulfillmentRequest(String location, String trackingNumber) {
        fulfillment = new Fulfillment(location, trackingNumber);
    }

    private static class Fulfillment {
        @SerializedName("location_id")
        private String location;
        @SerializedName("tracking_number")
        private String trackingNumber;
        @SerializedName("tracking_company")
        private String company;
        @SerializedName("notify_customer")
        private boolean notify;


        private Fulfillment(String location, String trackingNumber) {
            this.location = location;
            this.trackingNumber = trackingNumber;
            company = "USPS";
            notify = true;
        }
    }
}
