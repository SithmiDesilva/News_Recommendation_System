package com.example.testproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Register {


    public Pane pane6id;
    public Pane pane7id;
    public Label signup;
    public Label usernameid;
    public Label email2id;
    public Label password3id;
    public Label password2id;
    public TextField text5id;
    public TextField text6id;
    public TextField text7id;
    public TextField text8id;
    public Button signupbuttonid;
    public Label label1id;
    public ImageView image4id;
    public ImageView image3id;

    // Method to handle the signup button click
    public void onSignupbuttonidButtonOnClick(ActionEvent actionEvent) {
        try {
            // Load the new scene from the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View_News.fxml"));
            Parent root = fxmlLoader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the User_Login.fxml file.");
        }
    }
}
