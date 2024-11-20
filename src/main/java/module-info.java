module com.example.testproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;


    opens com.example.testproject to javafx.fxml;
    exports com.example.testproject;
}