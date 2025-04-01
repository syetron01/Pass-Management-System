package com.example.zoo_passes_management_sys;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ManagementDashboardController {
    @FXML
    private TextField customerNameField;

    @FXML
    private Button addGeneralButton;

    @FXML
    private Button addVIPButton;

    @FXML
    private Button serveGeneralButton;

    @FXML
    private Button serveVIPButton;

    @FXML
    private TableView<ServedCustomer> servedTable;

    @FXML
    private TableColumn<ServedCustomer, String> generalServedColumn;

    @FXML
    private TableColumn<ServedCustomer, String> vipServedColumn;

    @FXML
    private TextField historyField;

    @FXML
    private Label generalCountLabel;

    @FXML
    private Label vipCountLabel;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    @FXML
    private TableColumn<Customer, String> customerTypeColumn;

    @FXML
    private Button displayReport;

    private PassesManagementSystem system;
    private ObservableList<Customer> customerList;
    private ObservableList<ServedCustomer> servedList;
    private String lastAction = "";

    // Track the current serving row
    private ServedCustomer currentServingRow;

    public static class ServedCustomer {
        private final SimpleStringProperty generalCustomer;
        private final SimpleStringProperty vipCustomer;

        public ServedCustomer(String generalCustomer, String vipCustomer) {
            this.generalCustomer = new SimpleStringProperty(generalCustomer);
            this.vipCustomer = new SimpleStringProperty(vipCustomer);
        }

        public String getGeneralCustomer() {
            return generalCustomer.get();
        }

        public SimpleStringProperty generalCustomerProperty() {
            return generalCustomer;
        }

        public String getVipCustomer() {
            return vipCustomer.get();
        }

        public SimpleStringProperty vipCustomerProperty() {
            return vipCustomer;
        }

        // Method to set generalCustomer
        public void setGeneralCustomer(String name) {
            this.generalCustomer.set(name);
        }

        // Method to set vipCustomer
        public void setVipCustomer(String name) {
            this.vipCustomer.set(name);
        }

        // Check if this row is complete (has both customers)
        public boolean isComplete() {
            return !generalCustomer.get().isEmpty() && !vipCustomer.get().isEmpty();
        }

        // Check if this row has an empty general slot
        public boolean hasEmptyGeneralSlot() {
            return generalCustomer.get().isEmpty();
        }

        // Check if this row has an empty VIP slot
        public boolean hasEmptyVIPSlot() {
            return vipCustomer.get().isEmpty();
        }
    }

    public static class ReportItem {
        private final SimpleStringProperty category;
        private final SimpleStringProperty value;

        public ReportItem(String category, String value) {
            this.category = new SimpleStringProperty(category);
            this.value = new SimpleStringProperty(value);
        }

        public String getCategory() {
            return category.get();
        }

        public String getValue() {
            return value.get();
        }
    }

    @FXML
    public void initialize() {
        system = new PassesManagementSystem();
        customerList = FXCollections.observableArrayList();
        servedList = FXCollections.observableArrayList();

        // Configure table columns
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        customerTypeColumn.setCellFactory(column -> new TableCell<Customer, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(type);
                    // Set background color based on type, add border radius, bold text, and padding
                    if ("General".equals(type)) {
                        setStyle("-fx-background-color: #7cb342; -fx-alignment: CENTER; " +
                                "-fx-background-radius: 30; " +
                                "-fx-font-weight: bold; ");
                    } else if ("VIP".equals(type)) {
                        setStyle("-fx-background-color: #ffd54f; -fx-alignment: CENTER; " +
                                "-fx-background-radius: 30; " +
                                "-fx-font-weight: bold; ");
                    }
                }
            }
        });
        generalServedColumn.setCellValueFactory(new PropertyValueFactory<>("generalCustomer"));
        vipServedColumn.setCellValueFactory(new PropertyValueFactory<>("vipCustomer"));

        customerTable.setItems(customerList);
        servedTable.setItems(servedList);

        // Initialize with an empty row
        currentServingRow = new ServedCustomer("", "");
        servedList.add(currentServingRow);

        // Add input validation for the customer name field
        setupInputValidation();

        // Update the display
        updateDisplay();

        // Add button event handlers
        addGeneralButton.setOnAction(e -> addGeneralCustomer());
        addVIPButton.setOnAction(e -> addVIPCustomer());
        serveGeneralButton.setOnAction(e -> serveGeneralCustomer());
        serveVIPButton.setOnAction(e -> serveVIPCustomer());
        displayReport.setOnAction(e -> showDisplayReport());
    }

    private void setupInputValidation() {
        // Use a TextFormatter to validate input
        customerNameField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // If the change is deleting text (like backspace or delete key), allow it
            if (change.isDeleted()) {
                return change;
            }

            // If the new character being added is a letter or space, allow it
            if (change.getText().matches("[a-zA-Z\\s]*")) {
                return change;
            }

            // Show alert for invalid input
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Letters Only");
            alert.setContentText("Please enter letters only for customer names.");
            alert.show();

            // Reject the change
            return null;
        }));
    }

    private void addGeneralCustomer() {
        String name = customerNameField.getText().trim();
        if (!name.isEmpty()) {
            system.addGeneralCustomer(name);
            lastAction = "Added General Customer: " + name;
            customerNameField.clear();
            updateDisplay();
        }
    }

    private void addVIPCustomer() {
        String name = customerNameField.getText().trim();
        if (!name.isEmpty()) {
            system.addVIPCustomer(name);
            lastAction = "Added VIP Customer: " + name;
            customerNameField.clear();
            updateDisplay();
        }
    }

    private void serveGeneralCustomer() {
        Customer served = system.serveGeneralCustomer();
        if (served != null) {
            // If current row has an empty general slot, use it
            if (currentServingRow.hasEmptyGeneralSlot()) {
                currentServingRow.setGeneralCustomer(served.getName());
            } else {
                // Create a new row if needed
                currentServingRow = new ServedCustomer(served.getName(), "");
                servedList.add(currentServingRow);
            }

            lastAction = "Served General Customer: " + served.getName();

            // If row is complete, prepare a new row for next time
            if (currentServingRow.isComplete()) {
                currentServingRow = new ServedCustomer("", "");
                servedList.add(currentServingRow);
            }

            updateDisplay();
        } else {
            lastAction = "No General Customers to serve!";
            updateDisplay();
        }
    }

    private void serveVIPCustomer() {
        Customer served = system.serveVIPCustomer();
        if (served != null) {
            // If current row has an empty VIP slot, use it
            if (currentServingRow.hasEmptyVIPSlot()) {
                currentServingRow.setVipCustomer(served.getName());
            } else {
                // Create a new row if needed
                currentServingRow = new ServedCustomer("", served.getName());
                servedList.add(currentServingRow);
            }

            lastAction = "Served VIP Customer: " + served.getName();

            // If row is complete, prepare a new row for next time
            if (currentServingRow.isComplete()) {
                currentServingRow = new ServedCustomer("", "");
                servedList.add(currentServingRow);
            }

            updateDisplay();
        } else {
            lastAction = "No VIP Customers to serve!";
            updateDisplay();
        }
    }

    private void updateDisplay() {
        // Update counts
        generalCountLabel.setText(String.valueOf(system.getGeneralCustomersRemaining()));
        vipCountLabel.setText(String.valueOf(system.getVIPCustomersRemaining()));

        // Update history with last action only
        historyField.setText(lastAction);

        // Update customer table
        customerList.clear();

        // Add general customers to the table
        for (Customer customer : system.getGeneralCustomers()) {
            customerList.add(customer);
        }

        // Add VIP customers to the table (convert Stack to List first)
        Stack<Customer> vipStack = system.getVIPCustomers();
        List<Customer> vipList = new ArrayList<>(vipStack);
        for (Customer customer : vipList) {
            customerList.add(customer);
        }

        // Refresh the served table (force update)
        servedTable.refresh();
    }

    private void showDisplayReport() {
        // Create a new stage for the report
        Stage reportStage = new Stage();
        reportStage.setTitle("Day Report");

        // Create table for report data
        TableView<ReportItem> reportTable = new TableView<>();
        reportTable.setPrefWidth(400);
        reportTable.setPrefHeight(300);

        // Create columns
        TableColumn<ReportItem, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(200);

        TableColumn<ReportItem, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setPrefWidth(200);

        reportTable.getColumns().add(categoryColumn);
        reportTable.getColumns().add(valueColumn);

        // Add report data
        ObservableList<ReportItem> reportData = FXCollections.observableArrayList(
                new ReportItem("Total General Customers Served", String.valueOf(system.getTotalGeneralServed())),
                new ReportItem("Total VIP Customers Served", String.valueOf(system.getTotalVIPServed())),
                new ReportItem("Total Customers Served", String.valueOf(system.getTotalGeneralServed() + system.getTotalVIPServed())),
                new ReportItem("General Customers Remaining", String.valueOf(system.getGeneralCustomersRemaining())),
                new ReportItem("VIP Customers Remaining", String.valueOf(system.getVIPCustomersRemaining())),
                new ReportItem("Total Customers Remaining", String.valueOf(system.getTotalCustomersRemaining()))
        );

        reportTable.setItems(reportData);

        // Create a close button
        Button closeButton = new Button("Close Report");
        closeButton.setOnAction(e -> reportStage.close());

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                new Label("Zoo Passes Management System - End of Day Report"),
                reportTable,
                closeButton
        );

        // Show the stage
        Scene scene = new Scene(layout);
        reportStage.setScene(scene);
        reportStage.show();

        // Update the last action
        lastAction = "Generated End of Day Report";
        updateDisplay();
    }
}
