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
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserLogin {

    @FXML
    private AnchorPane anchorpane1;
    @FXML
    private Label label1;
    @FXML
    private AnchorPane anchorpane2;
    @FXML
    private Label label2;
    @FXML
    private Label label3;
    @FXML
    private Label label4;
    @FXML
    private TextField textfield1; // For user email input
    @FXML
    private PasswordField passwordfield; // For user password input
    @FXML
    private Button login;
    @FXML
    private Button register;
    @FXML
    private ImageView image1;
    @FXML
    private ImageView image2;

    // MongoDB collection reference
    private static final String COLLECTION_NAME = "user";

    @FXML
    public void onLoginButtonOnClick(ActionEvent actionEvent) {
        String email = textfield1.getText();
        String password = passwordfield.getText();

        // Perform validations
        if (validateEmail(email) && validatePassword(password)) {
            if (authenticateUser(email, password)) {
                // Fetch the username from the database
                String username = fetchUsernameFromDatabase(email);
                UserSession.setUsername(username);

                if (username == null) {
                    showAlert("Login Failed", "User not found.");
                } else {
                    // Load the user dashboard in a new window (stage)
                    textfield1.setText("");
                    passwordfield.setText("");
                    openNewWindow("User_Dashboard_New.fxml");
                }
            } else {
                showAlert("Login Failed", "Invalid email or password. Please try again.");
            }
        }
    }

    @FXML
    public void onRegisterButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Register.fxml");
    }

    @FXML
    private boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            showAlert("Validation Error", "Email cannot be empty.");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Validation Error", "Invalid email format.");
            return false;
        }
        return true;
    }

    @FXML
    private boolean validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return false;
        }
        if (password.length() < 6) {
            showAlert("Validation Error", "Password must be at least 6 characters long.");
            return false;
        }
        return true;
    }

    @FXML
    private boolean authenticateUser(String email, String password) {
        try {
            // Retrieve MongoDB database and collection
            MongoDatabase database = DatabaseManager.getDatabase();
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            // Query the MongoDB collection for a user with the given email
            Document query = new Document("email", email);
            Document user = collection.find(query).first();

            if (user != null) {
                // Get the stored hashed password
                String storedHashedPassword = user.getString("password");

                // Hash the provided password
                String inputHashedPassword = hashPassword(password);

                // Check if the hashed passwords match
                if (storedHashedPassword.equals(inputHashedPassword)) {
                    return true;
                } else {
                    showAlert("Authentication Failed", "Incorrect password.");
                }
            } else {
                showAlert("Authentication Failed", "No user found with the provided email.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while authenticating the user.");
        }
        return false; // Default return if authentication fails
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

    private String fetchUsernameFromDatabase(String email) {
        try {
            MongoDatabase database = DatabaseManager.getDatabase();
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            Document query = new Document("email", email);
            Document user = collection.find(query).first();

            return user != null ? user.getString("username") : null;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch username from the database.");
            return null;
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
            showAlert("Error", "Failed to load the requested page.");
        }
    }

    // Method to open a new window (stage) for user login
    private void openNewWindow(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Create a new stage for the user dashboard
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show(); // Show the new window without closing the current one

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the user dashboard.");
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
