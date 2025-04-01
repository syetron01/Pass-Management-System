module com.example.zoo_passes_management_sys {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.zoo_passes_management_sys to javafx.fxml;
    exports com.example.zoo_passes_management_sys;
}