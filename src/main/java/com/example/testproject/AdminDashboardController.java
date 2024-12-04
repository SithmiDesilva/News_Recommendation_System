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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.example.testproject.UserSession.currentUser;

public class AdminDashboardController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button normal;
    @FXML
    private Label dashboardLabel;
    @FXML
    private ImageView dashImage;
    @FXML
    private Button manageUsers;
    @FXML
    private Button manageNews;
    @FXML
    private Button logout;
    @FXML
    private Button dashboard;
    @FXML
    private ImageView home;
    @FXML
    private ImageView user;
    @FXML
    private ImageView news;
    @FXML
    private ImageView power;
    @FXML
    private Button back;
    @FXML
    private Button report;
    @FXML
    private ImageView saveImage1;

    private LocalDateTime loggedInTime;  // To track the login time
    private LocalDateTime loggedOutTime; // To track the logout time

    // Method called when user logs in
    public void setLoggedInTime() {
        loggedInTime = LocalDateTime.now();
    }

    // Method called when user logs out
    public void setLoggedOutTime() {
        loggedOutTime = LocalDateTime.now();
    }

    @FXML
    public void onManageUsersButtonOnClick(ActionEvent actionEvent) {
        loadScene("Manage_Users.fxml", actionEvent);
    }

    @FXML
    public void onManageNewsButtonOnClick(ActionEvent actionEvent) {
        loadScene("Manage_News.fxml", actionEvent);
    }

    @FXML
    public void onLogoutButtonOnClick(ActionEvent actionEvent) {
        boolean confirm = showConfirmationAlert("Logout", "Are you sure you want to logout?");
        if (confirm) {
            // Track the logout time
            setLoggedOutTime();

            // Close the current stage
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void onDashboardButtonOnClick(ActionEvent actionEvent) {
        loadScene("Admin_Dashboard.fxml", actionEvent);
    }

    @FXML
    public void onBackButtonOnClick(ActionEvent actionEvent) {
        loadScene("Prompt.fxml", actionEvent);
    }

    @FXML
    private void loadScene(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the scene: " + fxmlFile + "\n" + e.getMessage());
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

        // Show the confirmation dialog and wait for user response
        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    public void onReportButtonOnClick(ActionEvent actionEvent) {
        if (currentUser == null || currentUser.isEmpty()) {
            showAlert("Error", "No user is currently logged in.");
            return;
        }

        StringBuilder reportContent = new StringBuilder(1024);  // Pre-allocate memory to optimize performance
        reportContent.append("User Activity Report\n")
                .append("Generated on: ").append(LocalDateTime.now()).append("\n")
                .append("Username: ").append(currentUser).append("\n")
                .append("Login Time: ").append(loggedInTime).append("\n")
                .append("Logout Time: ").append(loggedOutTime).append("\n\n");

        Set<String> readHistory = fetchUserReadArticles();
        reportContent.append("Articles Read:\n");
        if (readHistory.isEmpty()) {
            reportContent.append("No articles read during this session.\n");
        } else {
            readHistory.forEach(article -> reportContent.append("- ").append(article).append("\n"));
        }

        // Disable the report button after generating the report
        report.setDisable(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("UserActivityReport_" + currentUser + ".txt"))) {
            writer.write(reportContent.toString());
            showAlert("Report Generated", "The activity report has been saved.");
        } catch (IOException e) {
            showAlert("Error", "Failed to save the report: " + e.getMessage());
            report.setDisable(false); // Re-enable the button in case of error
        }
    }

    private Set<String> fetchUserReadArticles() {
        Document user = DatabaseManager.getUserInteractionCollection()
                .find(new Document("username", currentUser)).first();
        return user != null ? new HashSet<>(user.getList("readArticles", String.class)) : new HashSet<>();
    }
}
