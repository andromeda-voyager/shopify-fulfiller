package sample;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;


public class Controller {


    @FXML
    private Label trackingLabel;
    @FXML
    private Button customsInformationButton;
    @FXML
    private Button cancelFulfillmentButton;
    @FXML
    private Button fulfillButton;
    @FXML
    private CheckBox showFulfilledCheckBox;
    @FXML
    private Label trackingNumberLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label shippingCostLabel;
    @FXML
    private Label totalCostLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView statusIcon;
    @FXML
    private Label shippingAddressLabel;
    @FXML
    private Label billingNameLabel;
    @FXML
    private Label shippingMethodLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, String> invoiceColumn;
    @FXML
    private TableColumn<Order, String> nameColumn;
    @FXML
    private TableView<Order> ordersTableView;

    private Orders orders;
    private Order selectedOrder;

    private final String IDLE_CLEAR_BUTTON_STYLE = "-fx-max-height: 20px; -fx-max-width: 20px; -fx-background-radius: 10em; -fx-background-size: 90%; -fx-background-image: url('/clear.png'); -fx-background-position: center; -fx-background-color:transparent;";
    private final String HOVERED_CLEAR_BUTTON_STYLE = "-fx-max-height: 20px; -fx-max-width: 20px; -fx-background-radius: 10em; -fx-background-size: 110%; -fx-background-image: url('/clear.png'); -fx-background-position: center; -fx-background-color:transparent";

    private final String IDLE_BUTTON_STYLE = "-fx-background-color: #0083ff; -fx-text-fill: white; -fx-content-display: right;";
    private final String HOVERED_BUTTON_STYLE = "-fx-background-color: #06a0ff; -fx-text-fill: white; -fx-content-display: right;";

    private final String IDLE_CANCEL_FULFILLMENT_BUTTON_STYLE = "-fx-background-color: red; -fx-text-fill: white;";
    private final String HOVERED_CANCEL_FULFILLMENT_BUTTON_STYLE = "-fx-background-color: #ff3e3a; -fx-text-fill: white;";

    private final Image errorIcon = new Image("/errorIcon.png");
    public Controller() {
        this.orders = new Orders();
    }


    private void errorMessage(String msg) {
        statusIcon.setImage(errorIcon);
        statusIcon.setVisible(true);
        statusLabel.setTextFill(Paint.valueOf("red"));
        statusLabel.setText(msg);
    }

    private void successMessage(String msg) {
        statusLabel.setTextFill(Paint.valueOf("green"));
        statusLabel.setText(msg);
    }

    private void setCostFields(Order order) {
        subtotalLabel.setText(order.getSubtotalPrice());
        totalCostLabel.setText(order.getTotalPrice());
        shippingCostLabel.setText(order.getShippingPrice());
    }

    private void removeStatusMessages() {
          statusLabel.setText("");
          statusIcon.setVisible(false);
    }

    private void populateOrderFields(Order order) {
        selectedOrder = order;
        setCostFields(order);
        shippingAddressLabel.setText(order.getShippingAddress().toString());
        billingNameLabel.setText(order.getBillingAddress().getName());
        shippingMethodLabel.setText(order.getShippingMethod());
        weightLabel.setText(order.getWeight());
    }

    private void setFulfilledDefaults() {
        fulfillButton.setVisible(false);
        cancelFulfillmentButton.setVisible(true);
        trackingLabel.setVisible(true);

    }

    private void setUnfulfilledDefaults(){
        cancelFulfillmentButton.setVisible(false);
        fulfillButton.setVisible(true);
        trackingNumberLabel.setText("");
        trackingLabel.setVisible(false);
        ClipboardIO.writeToClipboard(selectedOrder.getShippingAddress().toString());
    }

    private void setOrderFields(Order order){
        if (order != null) {
            selectedOrder = order;
            if (order.isFulfilled()) {
                setFulfilledDefaults();
                trackingNumberLabel.setText(order.getTrackingNumber());
            } else {
                setUnfulfilledDefaults();
            }
            removeStatusMessages();
            populateOrderFields(order);
        }
    }

    @FXML
    public void initialize() {

        clearButton.setOnMouseEntered(e -> clearButton.setStyle(HOVERED_CLEAR_BUTTON_STYLE));
        clearButton.setOnMouseExited(e -> clearButton.setStyle(IDLE_CLEAR_BUTTON_STYLE));
        clearButton.setCursor(Cursor.HAND);

        fulfillButton.setOnMouseEntered(e -> fulfillButton.setStyle(HOVERED_BUTTON_STYLE));
        fulfillButton.setOnMouseExited(e -> fulfillButton.setStyle(IDLE_BUTTON_STYLE));
        fulfillButton.setCursor(Cursor.HAND);

        cancelFulfillmentButton.setOnMouseEntered(e -> cancelFulfillmentButton.setStyle(HOVERED_CANCEL_FULFILLMENT_BUTTON_STYLE));
        cancelFulfillmentButton.setOnMouseExited(e -> cancelFulfillmentButton.setStyle(IDLE_CANCEL_FULFILLMENT_BUTTON_STYLE));
        cancelFulfillmentButton.setCursor(Cursor.HAND);

        customsInformationButton.setOnMouseEntered(e -> customsInformationButton.setStyle(HOVERED_BUTTON_STYLE));
        customsInformationButton.setOnMouseExited(e -> customsInformationButton.setStyle(IDLE_BUTTON_STYLE));
        customsInformationButton.setCursor(Cursor.HAND);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> orders.search(newValue, showFulfilledCheckBox.isSelected()));
        showFulfilledCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> orders.search(searchTextField.getText(), showFulfilledCheckBox.isSelected()));

        invoiceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInvoice()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        ordersTableView.setItems(orders.getOrdersObservableList());
        ordersTableView.setRowFactory(tv -> {
            TableRow<Order> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Order order = row.getItem();
                setOrderFields(order);
            });
            return row;
        });

    }


    public void handleClearSearchButtonAction() {
        searchTextField.clear();
        searchTextField.requestFocus();
    }

    public void handleFulfillButtonAction() {
//        AtomicInteger cycle = new AtomicInteger();
//        searchTextField.setText("Started");
//        PauseTransition pause = new PauseTransition(Duration.seconds(1));
//        pause.setOnFinished(event -> {
//                searchTextField.setText("Finished cycle " + cycle.getAndIncrement());
//        pause.play(); });
//
//        pause.play();
        fulfillButton.setVisible(false);
        orders.fulfillOrder(selectedOrder, ClipboardIO.readClipboard());
        if (selectedOrder.isUnfulfilled()) {
            errorMessage("Error! Order not fulfilled.");
        } else {
            successMessage(selectedOrder.getInvoice() + " was fulfilled successfully.");
        }

    }

    public void handleCancelFulfillmentButtonAction() {
        cancelFulfillmentButton.setVisible(false);
        orders.cancelFulfillment(selectedOrder);
        if (selectedOrder.isFulfilled()) {
            errorMessage("Fulfillment not cancelled.");
        } else {
            successMessage(selectedOrder.getInvoice() + " Fulfillment Canceled");
        }
    }


    public void handleCustomsInformationButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/customs.fxml"
                    )
            );

            Stage stage = new Stage();
            CustomsController controller = new CustomsController(new CustomsForm(selectedOrder.getLineItems()));
            loader.setController(controller);
            stage.setTitle("Customs Information");
            stage.setScene(new Scene(loader.load(), 600, 300));

            stage.setResizable(false);

            stage.show();
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());

        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
