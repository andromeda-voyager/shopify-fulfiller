package dev.matthewpotts.fulfiller;

import com.google.gson.*;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class ShopifyClient {

    private final static String STORE_LOCATION = "63012733103";
    private final static Gson gson = new Gson();
    private final static String EMPTY_JSON_OBJECT = gson.toJson(new JsonObject());
    private final static String STORE_URL_BASE = Config.SHOPIFY_API_URL;
    private static HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            Config.SHOPIFY_API_KEY,
                            Config.SHOPIFY_PASSWORD.toCharArray());
                }
            })
            .build();

    // POST /admin/api/#{api_version}/orders/#{order_id}/fulfillments/#{fulfillment_id}/cancel.json
    static FulfillmentResponse cancelFulfillment(String ID, String fulfillmentID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(STORE_URL_BASE + "orders/" + ID + "/fulfillments/" + fulfillmentID + "/cancel.json"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(EMPTY_JSON_OBJECT))
                    .build();
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (requestSuccessful(response.statusCode())) {
                return new Gson().fromJson(response.body(), FulfillmentResponse.class);
            } else {
                return new FulfillmentResponse(ID, fulfillmentID, "error");
            }

        } catch (Exception e) {
            return new FulfillmentResponse(ID, fulfillmentID, "error");
        }
    }

    private static HttpResponse<String> getOrdersRequest(String url) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    static ShopifyProductsResponse getFulfilledOrders() {
        String url = STORE_URL_BASE + "orders.json?status=any&fulfillment_status=shipped&limit=" + 100;
        ShopifyProductsResponse shopifyProductsResponse = new ShopifyProductsResponse();
        try {
            HttpResponse<String> response = getOrdersRequest(url);
            if (requestSuccessful(response.statusCode())) {
                return new Gson().fromJson(response.body(), ShopifyProductsResponse.class);
            } else {
                return shopifyProductsResponse;
            }
        } catch (Exception e) {
            return shopifyProductsResponse;
        }
    }


    //fulfillment_status=unshipped&limit=250
    static ShopifyProductsResponse getAllUnfulfilledOrders(String option) {
        String url = STORE_URL_BASE + "orders.json?fulfillment_status=unshipped&limit=250" + option;
        System.out.println(url);
        ShopifyProductsResponse shopifyProductsResponse = new ShopifyProductsResponse();
        while (true) {
            try {
                HttpResponse<String> response = getOrdersRequest(url);
                if (requestSuccessful(response.statusCode())) {
                    ShopifyProductsResponse productsResponse = new Gson().fromJson(response.body(), ShopifyProductsResponse.class);
                    shopifyProductsResponse.combineWith(productsResponse);
                    LinkHeader linkHeader = new LinkHeader(response.headers().firstValue("link").toString());

                    if (linkHeader.hasNext) {
                        url = linkHeader.next;
                    } else {
                        return shopifyProductsResponse;
                    }

                } else {
                    return shopifyProductsResponse;
                }
            } catch (Exception e) {
                return shopifyProductsResponse;
            }
        }
    }


    ///orders/#{order_id}/fulfillments.json
    static FulfillmentResponse postFulfillment(String orderID, String trackingNumber) {
        try {
            FulfillmentRequest postRequest = new FulfillmentRequest(STORE_LOCATION, trackingNumber);
            String json = gson.toJson(postRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(STORE_URL_BASE + "orders/" + orderID + "/fulfillments.json"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (requestSuccessful(response.statusCode())) {
                return new Gson().fromJson(response.body(), FulfillmentResponse.class);
            } else {
                return new FulfillmentResponse(orderID, "n/a", "error");
            }
        } catch (Exception e) {
            return new FulfillmentResponse(orderID, "n/a", "error");
        }

    }

    private static boolean requestSuccessful(int statusCode) {
        return (statusCode == 200 || statusCode == 201);
    }

    private static class LinkHeader {

        String next = "";
        String previous = "";
        boolean hasPrevious = false;
        boolean hasNext = false;


        LinkHeader(String linkHeaderString) {

            String[] linkContainers = linkHeaderString.split(", ");
            for (String linkContainer : linkContainers) {
                if (linkContainer.contains("rel=\"previous\"")) {
                    previous = extractLink(linkContainer);
                    hasPrevious = true;
                }
                if (linkContainer.contains("rel=\"next\"")) {
                    next = extractLink(linkContainer);
                    hasNext = true;
                }
            }

        }


        private static String extractLink(String linkContainer) {
            return linkContainer.substring(linkContainer.indexOf("<") + 1, linkContainer.lastIndexOf(">"));
        }

    }
}
