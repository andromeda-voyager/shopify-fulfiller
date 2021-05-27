package dev.matthewpotts.fulfiller;

import javax.sound.sampled.Line;

class CustomsLine {

    private int quantity;
    private String description;
    private double weight;
    private double price;

    CustomsLine(String description, LineItem lineItem){
        this.description = description;
        this.quantity = lineItem.getQuantity();
        this.weight = lineItem.getWeightInGrams();
        this.price = lineItem.getPrice();
    }

    void combineWith(LineItem lineItem){
        this.quantity +=  lineItem.getQuantity();
        this.weight += lineItem.getWeightInGrams();
        this.price += lineItem.getPrice();
    }

    void print(){
        System.out.println("Quantity :" + quantity + " Description: " + description + " Weight: " + weight + " Price: " + price);
    }


    int getQuantity(){
        return quantity;
    }

    double getWeight(){
        return weight;
    }

    String getDescription(){
        return description;
    }

    Double getPrice(){
        return price;
    }

    String getPriceString(){
        return "$"+String.format("%.2f", price);
    }

    String getWeightString() {
        Double weightInOunces = weight * 0.035274;
        return String.format("%.2f", weightInOunces) + " oz.";
    }
}
