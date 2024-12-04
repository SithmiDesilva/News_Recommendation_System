package com.example.testproject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;

public class ManageNewsController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane pane;
    @FXML
    private Label manageNewsLabel;
    @FXML
    private Button home;
    @FXML
    private ImageView homeImage;
    @FXML
    private TableView<Article> newsTable;  // TableView to display news articles
    @FXML
    private TableColumn<Article, String> category;
    @FXML
    private TableColumn<Article, String> author;
    @FXML
    private TableColumn<Article, String> headline;
    @FXML
    private TableColumn<Article, String> content;
    @FXML
    private Button delete;
    @FXML
    private Button addNews;
    @FXML
    private Button edit;
    @FXML
    private ObservableList<Article> articlesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up the TableView columns to bind with the article properties
        category.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        author.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        headline.setCellValueFactory(cellData -> cellData.getValue().headlineProperty());
        content.setCellValueFactory(cellData -> cellData.getValue().contentProperty());

        // Load articles from the database
        loadArticles();
    }

    private void loadArticles() {
        try {
            // Fetch data from the MongoDB database
            MongoDatabase database = DatabaseManager.getDatabase();
            MongoCollection<Document> collection = database.getCollection("articles");

            if (database == null) {
                showAlert("Error", "Database connection failed.");
                return;
            }

            // Clear the list before adding new data
            articlesList.clear();

            // Check if collection is empty
            if (collection.countDocuments() == 0) {
                showAlert("Info", "No articles found in the database.");
                return;
            }

            // Fetch all articles from the database
            for (Document doc : collection.find()) {
                String category = doc.getString("category") != null ? doc.getString("category") : "Unknown Category";
                String author = doc.getString("author") != null ? doc.getString("author") : "Unknown Author";
                String headline = doc.getString("headline") != null ? doc.getString("headline") : "Untitled";
                String content = doc.getString("content") != null ? doc.getString("content") : "No Content";

                // Log each document to verify the data
                System.out.println("Fetched Article: " + headline);

                // Create Article object and add to the list
                Article article = new Article(category, author, headline, content);
                articlesList.add(article);
            }

            // Set the items in the TableView
            newsTable.setItems(articlesList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load articles from the database: " + e.getMessage());
        }
    }


    @FXML
    public void onAddNewsButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Post_News.fxml");
    }

    @FXML
    public void onEditButtonOnClick(ActionEvent actionEvent) {
        // Get the selected article from the table
        Article selectedArticle = newsTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Post_News.fxml"));
                Parent root = loader.load();

                PostNewsController controller = loader.getController();
                controller.setArticleDataForEditing(selectedArticle); // Pass selected article to edit form

                Stage stage = (Stage) anchorPane.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the edit form.");
            }
        } else {
            showAlert("Error", "No article selected for editing.");
        }
    }

    @FXML
    public void onDeleteButtonOnClick(ActionEvent actionEvent) {
        // Get the selected article from the table
        Article selectedArticle = newsTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            try {
                MongoDatabase database = DatabaseManager.getDatabase();
                MongoCollection<Document> collection = database.getCollection("articles");

                // Delete the article from the database based on its headline (or other unique field)
                collection.deleteOne(new Document("headline", selectedArticle.getHeadline()));

                // Remove the article from the TableView
                articlesList.remove(selectedArticle);

                showAlert("Success", "Article deleted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete the article.");
            }
        } else {
            showAlert("Error", "No article selected to delete.");
        }
    }

    @FXML
    public void onHomeButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "Admin_Dashboard.fxml");
    }

    @FXML
    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(scene);
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
}
