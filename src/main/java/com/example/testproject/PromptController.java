package com.example.testproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class PromptController {

    @FXML
    private AnchorPane anchorpane1;
    @FXML
    private Label newsrecommendationlabel;
    @FXML
    private AnchorPane anchorpane2;
    @FXML
    private Label welcomelabel;
    @FXML
    private Pane pane1;
    @FXML
    private Pane pane2;
    @FXML
    private Label identificationlabel;
    @FXML
    private Button userbutton;
    @FXML
    private Button adminbutton;


    @FXML
    public void onUserButtonButtonOnClick(ActionEvent actionEvent) {
        loadScene("User_Login.fxml", actionEvent);
    }


    @FXML
    public void onAdminButtonButtonOnClick(ActionEvent actionEvent) {
        loadScene("Admin_Login.fxml", actionEvent);
    }

    @FXML
    private void loadScene(String fxmlFile, ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());

            // Get the current stage from the button that triggered the event
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
