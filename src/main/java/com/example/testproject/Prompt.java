package com.example.testproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class Prompt {
    @FXML
    private ImageView image1;

    @FXML
    private Label labelid2;

    @FXML
    private Button user1id;

    @FXML
    private Button administratorid;

    @FXML
    public void onUser1idButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "User_Login.fxml");
    }

    @FXML
    public void onAdministratorButtonOnClick(ActionEvent event) {
        loadScene(event, "Admin_Login.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Unable to load the FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
