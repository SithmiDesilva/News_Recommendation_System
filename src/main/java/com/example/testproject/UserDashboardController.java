package com.example.testproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.example.testproject.UserSession.getUsername;

public class UserDashboardController {

    @FXML
    private AnchorPane anchorPaneTwo, anchorPaneThree, anchorPane1;
    @FXML
    private Button buttonOne, dashboard, view, recommendations, saveid, logout, back1, deleteAccount, report;
    @FXML
    private Pane paneOne;
    @FXML
    private ImageView imageLocal, imageScience, market, sea, dashboardImage, viewImage, recommendationsImage, saveImage, logoutImage;
    private String currentUser;

    @FXML
    private void initialize() {
        // Fetch the current user at initialization
        currentUser = getUsername();
    }

    @FXML
    public void onRecommendationsButtonOnClick(ActionEvent actionEvent) {
        loadScene("News_Recommendations.fxml", actionEvent);
    }

    private Set<String> fetchUserLikedCategories() {
        Document user = DatabaseManager.getUserInteractionCollection()
                .find(new Document("username", currentUser)).first();
        return user != null ? new HashSet<>(user.getList("preferredCategories", String.class)) : new HashSet<>();
    }

    private Set<String> fetchUserReadArticles() {
        Document user = DatabaseManager.getUserInteractionCollection()
                .find(new Document("username", currentUser)).first();
        return user != null ? new HashSet<>(user.getList("readArticles", String.class)) : new HashSet<>();
    }

    @FXML
    public void onDashboardButtonOnClick(ActionEvent actionEvent) {
        loadScene("User_Dashboard_New.fxml", actionEvent);
    }

    @FXML
    public void onViewButtonOnClick(ActionEvent actionEvent) {
        loadScene("View_News.fxml", actionEvent);
    }


    @FXML
    public void onLogoutButtonOnClick(ActionEvent actionEvent) {
        if (showConfirmationAlert("Logout", "Are you sure you want to logout?")) {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void onBackButtonOnClick(ActionEvent actionEvent) {
        loadScene("Prompt.fxml", actionEvent);
    }


    @FXML
    public void onDeleteButtonOnClick(ActionEvent actionEvent) {
        if (currentUser == null || currentUser.isEmpty()) {
            showAlert("Error", "No user is currently logged in.");
            return;
        }

        if (showConfirmationAlert("Delete Account", "Are you sure you want to delete your account? This action cannot be undone.")) {
            try {
                DatabaseManager.getuserCollection().deleteOne(new Document("username", currentUser));
                DatabaseManager.getUserInteractionCollection().deleteOne(new Document("username", currentUser));

                showAlert("Account Deleted", "Your account has been successfully deleted.");
                UserSession.logout();
                navigateToLoginPage(actionEvent);

            } catch (Exception e) {
                showAlert("Error", "Failed to delete the account: " + e.getMessage());
            }
        }
    }

    private void navigateToLoginPage(ActionEvent actionEvent) {
        loadScene("User_Login.fxml", actionEvent);
    }

    private void loadScene(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the scene: " + e.getMessage());
        }
    }

    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
