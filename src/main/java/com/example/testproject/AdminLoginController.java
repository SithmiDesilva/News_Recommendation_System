package com.example.testproject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;

public class AdminLoginController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label mainLabel;
    @FXML
    private Pane loginPane;
    @FXML
    private Label emailLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField textField; // Email input field
    @FXML
    private PasswordField passwordField; // Password input field
    @FXML
    private Button cancel;
    @FXML
    private Button logIn;
    @FXML
    private ImageView laNewsImage;

    private final MongoDatabase database = DatabaseManager.getDatabase(); // Access the database

    @FXML
    public void onCancelButtonOnClick(ActionEvent actionEvent) {
        boolean confirm = showConfirmationAlert("Cancel Login", "Are you sure you want to cancel?");
        if (confirm) {
            // Close the application
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Show the confirmation dialog and wait for response
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Return true if 'Yes' is clicked, false if 'No' is clicked
        return alert.showAndWait().filter(response -> response == yesButton).isPresent();
    }

    @FXML
    public void onLogInButtonOnClick(ActionEvent actionEvent) {
        String email = textField.getText();
        String password = passwordField.getText();

        if (validateAdmin(email, password)) {
            // Load the Admin Dashboard
            loadScene(actionEvent, "Admin_Dashboard.fxml");
        } else {
            // Show error message
            showAlert("Login Failed", "Invalid email or password. Please try again.");
        }
    }

    private boolean validateAdmin(String email, String password) {
        try {
            MongoCollection<Document> adminCollection = database.getCollection("admin");
            Document admin = adminCollection.find(new Document("email", email)).first();

            if (admin != null) {
                // Check if the password matches
                return admin.getString("password").equals(password);
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while validating admin credentials: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
