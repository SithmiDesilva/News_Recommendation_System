package com.example.testproject;

import com.mongodb.client.*;
import org.bson.Document;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterController {

    @FXML
    private AnchorPane anchorpaneone;
    @FXML
    private Label labelone;
    @FXML
    private AnchorPane anchorpanetwo;
    @FXML
    private Label signup;
    @FXML
    private Label username;
    @FXML
    private Label email;
    @FXML
    private Label password;
    @FXML
    private Label confirmpassword;
    @FXML
    private TextField textfieldone; // Username input
    @FXML
    private TextField textfieldtwo; // Email input
    @FXML
    private PasswordField passwordfieldone; // Password input
    @FXML
    private PasswordField passwordfieldtwo; // Confirm Password input
    @FXML
    private Button buttonsignup;
    @FXML
    private Button buttoncancel;
    @FXML
    private ImageView newspaperimage;

    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "NewsRecommendationDB";
    private static final String COLLECTION_NAME = "user";

    @FXML
    public void onbuttonsignupButtonOnClick(ActionEvent actionEvent) {
        if (validateInput()) {
            // Store user data in MongoDB
            boolean isSaved = saveUserToDatabase(
                    textfieldone.getText(),
                    textfieldtwo.getText(),
                    passwordfieldone.getText()
            );


            if (isSaved) {
                showAlert("Success", "Registration successful! Redirecting to dashboard...");
                loadScene(actionEvent, "User_Dashboard_New.fxml");
            } else {
                showAlert("Error", "Failed to save user data. Please try again.");
            }
        }
    }

    @FXML
    public void onbuttoncancelButtonOnClick(ActionEvent actionEvent) {
        // Confirm cancellation
        boolean confirm = showConfirmationAlert("Cancel Registration", "Are you sure you want to cancel?");
        if (confirm) {
            // Close the application
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private boolean validateInput() {
        String username = textfieldone.getText();
        String email = textfieldtwo.getText();
        String password = passwordfieldone.getText();
        String confirmPassword = passwordfieldtwo.getText();

        if (username == null || username.isEmpty()) {
            showAlert("Validation Error", "Username cannot be empty.");
            return false;
        }

        if (email == null || email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Validation Error", "Invalid email format.");
            return false;
        }

        if (password == null || password.isEmpty() || password.length() < 6) {
            showAlert("Validation Error", "Password must be at least 6 characters long.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Validation Error", "Passwords do not match.");
            return false;
        }

        return true;
    }

    @FXML
    private boolean saveUserToDatabase(String username, String email, String password) {
        try (MongoClient mongoClient = MongoClients.create(MONGO_URI)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            // Hash the password for security
            String hashedPassword = hashPassword(password);

            // Create a new user document
            Document user = new Document()
                    .append("username", username)
                    .append("email", email)
                    .append("password", hashedPassword);

            // Insert the document into the collection
            collection.insertOne(user);
            UserSession.setUsername(username);


            return true; // Data saved successfully
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Data saving failed
        }
    }

    @FXML
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @FXML
    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
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

    @FXML
    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().isPresent();
    }
}
