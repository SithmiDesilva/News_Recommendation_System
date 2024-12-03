package com.example.testproject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.File;
import java.io.IOException;

public class PostNewsController {

    @FXML
    private Label newsId;
    @FXML
    private Label category;
    @FXML
    private Label author;
    @FXML
    private Label headline;
    @FXML
    private Label content;
    @FXML
    private Label imageid;
    @FXML
    private ImageView backimage;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane pane;
    @FXML
    private Label postNews;
    @FXML
    private Button home;
    @FXML
    private TextField textOne;  // News ID
    @FXML
    private TextField textTwo;  // Category
    @FXML
    private TextField textThree;  // Author
    @FXML
    private TextField textFour;  // Headline
    @FXML
    private TextArea textArea;   // Content
    @FXML
    private ImageView imageview; // Image preview
    @FXML
    private Button image;        // Add image button
    @FXML
    private Button addNews;      // Add/Edit news button

    private String selectedImagePath = "";   // Variable to store selected image path
    private boolean isEditMode = false;      // Flag to check if the form is in edit mode
    private String originalHeadline = null;  // Store the original headline for updating

    @FXML
    public void onImageButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image("file:" + selectedImagePath);
            imageview.setImage(image);
        }
    }

    @FXML
    public void onAddNewsButtonOnClick(ActionEvent actionEvent) {
        String newsId = textOne.getText();
        String category = textTwo.getText();
        String author = textThree.getText();
        String headline = textFour.getText();
        String content = textArea.getText();

        if (newsId.isEmpty() || category.isEmpty() || author.isEmpty() || headline.isEmpty() || content.isEmpty()) {
            showAlert("Error", "All fields must be filled in.");
            return;
        }

        try {
            MongoDatabase database = DatabaseManager.getDatabase();
            MongoCollection<Document> collection = database.getCollection("articles");

            Document document = new Document("newsId", newsId)
                    .append("category", category)
                    .append("author", author)
                    .append("headline", headline)
                    .append("content", content);

            if (!selectedImagePath.isEmpty()) {
                document.append("image", selectedImagePath);
            }

            if (isEditMode) {
                // Update the existing article if in edit mode
                collection.updateOne(new Document("headline", originalHeadline), new Document("$set", document));
                showAlert("Success", "News article updated successfully.");
            } else {
                // Insert a new article if in add mode
                collection.insertOne(document);
                showAlert("Success", "News article added successfully.");
            }

            clearFormFields();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add or update news article.");
        }
    }

    @FXML
    public void onHomeButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Manage_News.fxml");
    }

    @FXML
    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the scene: " + e.getMessage());
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

    private void clearFormFields() {
        textOne.clear();
        textTwo.clear();
        textThree.clear();
        textFour.clear();
        textArea.clear();
        imageview.setImage(null);
        selectedImagePath = "";
        isEditMode = false;  // Reset edit mode
        originalHeadline = null;
    }

    public void setArticleDataForEditing(Article selectedArticle) {
        if (selectedArticle != null) {
            isEditMode = true;
            originalHeadline = selectedArticle.getHeadline();  // Store the original headline for updating
            textOne.setText(selectedArticle.getCategory());    // Populate form fields
            textTwo.setText(selectedArticle.getCategory());
            textThree.setText(selectedArticle.getAuthor());
            textFour.setText(selectedArticle.getHeadline());
            textArea.setText(selectedArticle.getContent());



            addNews.setText("Update News");  // Change button text to indicate edit mode
        }
    }
}
