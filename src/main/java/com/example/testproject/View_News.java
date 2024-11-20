package com.example.testproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class View_News {
    public Pane pane13;
    public Button profile2id;
    public Button view2id;
    public Button savenews2id;
    public Button logout2;
    public Button recommendation2id;
    public Pane pane15;
    public Pane pane16;
    public Label pane14; // Consider renaming for clarity, e.g., `titleLabel`
    public ScrollBar scrollbar1;
    public TextArea field1;
    public TextArea field2;
    public TextArea field3;
    public Label userlabel2;

    /**
     * Reusable method to load a new scene.
     *
     * @param actionEvent The event triggering the scene switch.
     * @param fxmlFileName The name of the FXML file to load.
     */
    private void loadScene(ActionEvent actionEvent, String fxmlFileName) {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = fxmlLoader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Print stack trace for debugging
            e.printStackTrace();
            System.out.println("Failed to load FXML file: " + fxmlFileName);
        }
    }

    // Event handlers for button clicks
    public void onprofile2idButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Profile.fxml");
    }

    public void onview2idButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "View_News.fxml");
    }

    public void onsavenews2idButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Saved_News.fxml");
    }

    public void onlogoutButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Logout.fxml");
    }

    public void onrecommendationidButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "News_Recommendations.fxml");
    }
}
