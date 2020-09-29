package sample;

import com.google.gson.annotations.SerializedName;

class LineItem {
    @SerializedName("price")
    private String price;

    @SerializedName("variant_title")
    private String variantTitle;

    @SerializedName("title")
    private String title;

    @SerializedName("grams")
    private int weightInGrams;

    @SerializedName("quantity")
    private int quantity;

     int getQuantity(){
        return quantity;
    }

     int getWeightInGrams(){
        return weightInGrams * quantity;
    }

     String getVariantTitle(){
         if (variantTitle == null) {
             return "";
         }
        return variantTitle;
    }

     String getTitle(){
        return title;
    }

     Double getPrice(){
        return Double.parseDouble(price) * quantity;
    }
}
