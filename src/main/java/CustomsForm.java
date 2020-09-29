package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

class CustomsForm {

    private ObservableList<CustomsLine> customsLines = FXCollections.observableArrayList();
    private static HashMap<String, String> categoriesMap = loadCategories();

    CustomsForm(ArrayList<LineItem> lineItems){
        HashMap<String, CustomsLine> customsMap = new HashMap<>();
        String productDescription;
        for (LineItem lineItem : lineItems) {
            productDescription = parseProductDescription(lineItem.getTitle(), lineItem.getVariantTitle());
            if(customsMap.containsKey(productDescription)) {

                customsMap.get(productDescription).combineWith(lineItem);
            }
            else {
                customsMap.put(productDescription, new CustomsLine(productDescription, lineItem));
            }
        }
        customsLines.addAll(customsMap.values());
    }

    ObservableList<CustomsLine> getCustomsLines() {
        return customsLines;
    }

    void print(){
        for (CustomsLine customsLine : customsLines) {
            customsLine.print();
        }
    }

    private static HashMap<String, String> loadCategories(){
        HashMap<String, String> categories = new HashMap<>();
        try {
            String category = "";
            String file = "Categories.txt";
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if(line.startsWith("#")){
                    category = line.replaceFirst("#", "");
                }
                else {
                    categories.put(line, category);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return categories;
    }

    private String parseProductDescription(String title, String variantTitle){
        String[] variantParts = variantTitle.split(" - ");
        if(variantParts.length > 1) {
            if (categoriesMap.containsKey(variantParts[0])){
                return categoriesMap.get(variantParts[0]);
            }
            else {
                return variantParts[0];
            }
        } else if(variantParts.length == 1) {
            return title + " " + variantParts[0];
        } else {
            return title;
        }
    }
}

