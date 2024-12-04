package com.example.testproject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ManageUsersController {

    @FXML
    private Button save;
    @FXML
    private Pane pane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<User> Table;
    @FXML
    private TableColumn<User, String> userName;
    @FXML
    private TableColumn<User, String> email;
    @FXML
    private TableColumn<User, String> signup;
    @FXML
    private TableColumn<User, String> action;
    @FXML
    private Button edit;
    @FXML
    private Button remove;
    @FXML
    private Label manageUserLabel;
    @FXML
    private Button home;
    @FXML
    private ImageView homeImage;

    // Form fields for editing
    @FXML
    private TextField editUsernameField;
    @FXML
    private TextField editEmailField;
    @FXML
    private TextField editSignupDateField;

    // MongoDB collection reference
    private static final String COLLECTION_NAME = "user";
    private User selectedUser; // Variable to hold the selected user

    @FXML
    public void initialize() {
        // Set up the columns for the TableView
        userName.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        email.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        signup.setCellValueFactory(cellData -> cellData.getValue().signupDateProperty());
        action.setCellValueFactory(cellData -> cellData.getValue().signupDateProperty()); // Adjust as needed for 'action'

        // Fetch data from MongoDB and populate the table
        loadUserData();
    }

    private void loadUserData() {
        ObservableList<User> userList = FXCollections.observableArrayList();

        try {
            MongoDatabase database = DatabaseManager.getDatabase();
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            // Fetch all users
            for (Document doc : collection.find()) {
                String username = doc.getString("username");
                String email = doc.getString("email");
                String signupDate = doc.getString("signupDate");

                userList.add(new User(username, email, signupDate));
            }

            Table.setItems(userList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load user data from MongoDB.");
        }
    }

    @FXML
    public void onEditButtonOnClick(ActionEvent actionEvent) {
        selectedUser = Table.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Set selected user data in the form fields for editing
            editUsernameField.setText(selectedUser.getUsername());
            editEmailField.setText(selectedUser.getEmail());
            editSignupDateField.setText(selectedUser.getSignupDate());
        } else {
            showAlert("Error", "No user selected for editing.");
        }
    }

    @FXML
    public void onRemoveButtonOnClick(ActionEvent actionEvent) {
        selectedUser = Table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                MongoDatabase database = DatabaseManager.getDatabase();
                MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

                // Delete the user from MongoDB based on the email
                collection.deleteOne(new Document("email", selectedUser.getEmail()));
                loadUserData(); // Reload the table data

                showAlert("Success", "User removed successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to remove user.");
            }
        } else {
            showAlert("Error", "No user selected to remove.");
        }
    }

    @FXML
    public void onSaveButtonOnClick(ActionEvent actionEvent) {
        if (selectedUser != null) {
            // Get the updated user data from the form fields
            String updatedUsername = editUsernameField.getText();
            String updatedEmail = editEmailField.getText();
            String updatedSignupDate = editSignupDateField.getText();

            // Update the user object with the new values
            selectedUser.setUsername(updatedUsername);
            selectedUser.setEmail(updatedEmail);
            selectedUser.setSignupDate(updatedSignupDate);

            try {
                MongoDatabase database = DatabaseManager.getDatabase();
                MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

                // Create a Document to update the user data in MongoDB
                Document updatedUserDoc = new Document("username", updatedUsername)
                        .append("email", updatedEmail)
                        .append("signupDate", updatedSignupDate);

                // Update the user in MongoDB based on the email
                collection.updateOne(new Document("email", selectedUser.getEmail()), new Document("$set", updatedUserDoc));
                loadUserData(); // Reload the user data in the table

                showAlert("Success", "User updated successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update user.");
            }
        } else {
            showAlert("Error", "No user selected to update.");
        }
    }

    @FXML
    public void onHomeButtonOnClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
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
}
