module com.apaza.chitibrowser {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.apaza.chitibrowser to javafx.fxml;
    exports com.apaza.chitibrowser;
}