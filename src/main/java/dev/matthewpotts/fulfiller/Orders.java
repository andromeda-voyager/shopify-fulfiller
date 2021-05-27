package dev.matthewpotts.fulfiller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

class Orders {
    private ObservableList<Order> ordersObservableList = FXCollections.observableArrayList();
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
    private HashMap<String, Order> unfulfilledOrdersMap;
    private ArrayList<Order> fulfilledOrdersList;
    private String sinceID = "";

    Orders() {
        unfulfilledOrdersMap = new HashMap<>();
        fulfilledOrdersList = new ArrayList<>();

        addRecentlyFulfilledOrders();
        scheduleUpdate();
    }

    void fulfillOrder(Order order, String trackingNumber) {
        order.fulfill(trackingNumber);
        if(order.isFulfilled()){
            unfulfilledOrdersMap.remove(order.getInvoice());
            fulfilledOrdersList.add(order);
        }
    }

    private boolean isNumber(String str) {
        return str.matches("[0-9]+");
    }

    private void searchMainFields(String searchTerm, Collection<Order> list) {
        for (Order order : list) {
            if (order.hasName(searchTerm)) {
                ordersObservableList.add(order);
            }
            if (order.getTrackingNumber().contains(searchTerm)) {
                ordersObservableList.add(order);
            }
        }
    }

    private void invoiceSearchUnfulfilled(String invoice) {
        if (isNumber(invoice)) {
            if (unfulfilledOrdersMap.containsKey(invoice)) {
                ordersObservableList.add(unfulfilledOrdersMap.get(invoice));
            }
        }
    }
    private void invoiceSearchFulfilled(String invoice) {
        if (isNumber(invoice)) {
            for(Order order: fulfilledOrdersList){
                if(order.getInvoice().matches(invoice)){
                    ordersObservableList.add(order);
                }
            }
        }
    }

    void search(String searchTerm, boolean includeFulfilled) {
        ordersObservableList.clear();
        searchTerm = searchTerm.trim();
        if (searchTerm.isEmpty()) {
            ordersObservableList.addAll(unfulfilledOrdersMap.values());
            if(includeFulfilled) {
                ordersObservableList.addAll(fulfilledOrdersList);
            }
        } else {
            searchMainFields(searchTerm, unfulfilledOrdersMap.values());
            invoiceSearchUnfulfilled(searchTerm);
            if(includeFulfilled) {
                searchMainFields(searchTerm, fulfilledOrdersList);
                invoiceSearchFulfilled(searchTerm);
            }
        }
        FXCollections.sort(ordersObservableList);
    }

    //fulfillment status options shipped, partial, unshipped, any
    private void scheduleUpdate() {
        final Runnable updater = this::updateOrders;
        // final ScheduledFuture<?> updaterHandler = scheduler.scheduleAtFixedRate(updater, 0, 1000, SECONDS);
        scheduler.scheduleAtFixedRate(updater, 0, 900, SECONDS); //15 minutes
    }


    private void addRecentlyFulfilledOrders() {
        ShopifyProductsResponse shopifyProductsResponse = ShopifyClient.getFulfilledOrders();
        fulfilledOrdersList.addAll(shopifyProductsResponse.getOrders());
    }

    private void updateOrders() {
        try {
            if(fulfilledOrdersList.size() > 250){
                Collections.sort(fulfilledOrdersList);
                fulfilledOrdersList = new ArrayList<>(fulfilledOrdersList.subList(fulfilledOrdersList.size()/2, fulfilledOrdersList.size()));
            }
            ShopifyProductsResponse shopifyProductsResponse = ShopifyClient.getAllUnfulfilledOrders(sinceID);
            shopifyProductsResponse.getOrders().forEach(order -> unfulfilledOrdersMap.put(order.getInvoice(), order));

            if (shopifyProductsResponse.hasOrders()) {
                sinceID = "&since_id=" + shopifyProductsResponse.getNewestOrderID();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void cancelFulfillment(Order order) {
        order.cancelFulfillment();
        if(order.isUnfulfilled()){
            fulfilledOrdersList.remove(order);
            unfulfilledOrdersMap.put(order.getInvoice(), order);
        }
    }

    ObservableList<Order> getOrdersObservableList() {
        return ordersObservableList;
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }
}
