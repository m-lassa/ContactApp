module com.example.contactapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens com.example.contactapp to javafx.fxml;
    exports com.example.contactapp;
    exports com.example.contactapp.datamodel;
    opens com.example.contactapp.datamodel to javafx.fxml;
}