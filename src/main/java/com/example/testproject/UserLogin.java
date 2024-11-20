package com.example.testproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UserLogin {
    // FXML fields
    public Label signin;
    public Label email1id;
    public Label password1id;
    public TextField textbox1;
    public TextField textbox2;
    public Button login1id;
    public Button register1id;

    // Button click handler for the Login button
    public void onLogin1idButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "User_Home.fxml"); // Replace with the correct FXML file
    }

    // Button click handler for the Register button
    public void onRegisterButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Register.fxml"); // Replace with the correct FXML file
    }

    // Helper method to load a new scene
    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            // Load the specified FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
