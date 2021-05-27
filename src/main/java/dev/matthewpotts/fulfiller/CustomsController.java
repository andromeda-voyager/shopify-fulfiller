package dev.matthewpotts.fulfiller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class CustomsController {

    @FXML
    private TableView<CustomsLine> customsTableView;
    @FXML
    private TableColumn<CustomsLine, String> quantityColumn;
    @FXML
    private TableColumn<CustomsLine, String> priceColumn;
    @FXML
    private TableColumn<CustomsLine, String> weightColumn;
    @FXML
    private TableColumn<CustomsLine, String> descriptionColumn;

    private CustomsForm customsForm;

    @FXML
    public void initialize() {

        quantityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriceString()));
        weightColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWeightString()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        customsTableView.setItems(customsForm.getCustomsLines());
        customsTableView.setRowFactory(tv -> new TableRow<>());

    }

    CustomsController(CustomsForm customsForm){
        this.customsForm = customsForm;
    }
}
