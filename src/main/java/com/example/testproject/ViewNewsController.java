package com.example.testproject;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;

public class ViewNewsController {

    @FXML
    private ListView<Document> articleListView;

    // MongoDB connection parameters
    private static final String DATABASE_NAME = "NewsRecommendationDB";
    private static final String COLLECTION_NAME = "articles";

    // Initialize the ListView and load articles
    @FXML
    public void initialize() {
        setupListView();
        loadArticles();
    }

    /**
     * Configure the ListView to display custom cells with title, image, and author.
     */
    private void setupListView() {
        articleListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Document article, boolean empty) {
                super.updateItem(article, empty);
                if (empty || article == null) {
                    setGraphic(null);
                } else {
                    // Extract data from the article document
                    String title = article.getString("headline");
                    String author = article.getString("author");
                    String imagePath = article.getString("image");

                    // Create an HBox to layout the content
                    HBox hBox = new HBox(5);

                    // Image for the article
                    ImageView imageView = new ImageView();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            Image image = new Image("file:" + imagePath, 100, 100, true, true);
                            imageView.setImage(image);
                        } catch (Exception e) {
                            imageView.setImage(null); // Handle missing or invalid images
                        }
                    }

                    // Text elements for title and author
                    VBox textContainer = new VBox(10);
                    Text titleText = new Text( (title != null ? title : "Unknown"));
                    titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text authorText = new Text("By: " + (author != null ? author : "Unknown"));
                    authorText.setStyle("-fx-font-size: 12; -fx-text-fill: gray;");

                    textContainer.getChildren().addAll(titleText, authorText);

                    // Add elements to the HBox
                    hBox.getChildren().addAll(imageView, textContainer);

                    // Add a horizontal separator
                    VBox container = new VBox(hBox);
                    Text separator = new Text("-------------------------------------------------------------------------------------------------------------------------------------");
                    separator.setStyle("-fx-fill: lightgray;");
                    container.getChildren().add(separator);

                    setGraphic(container);
                    setGraphic(hBox);
                }
            }
        });

        articleListView.setOnMouseClicked(event -> handleArticleClick());
    }

    /**
     * Load articles from MongoDB into the ListView.
     */
    private void loadArticles() {
        try (var mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            articleListView.getItems().clear();

            for (Document article : collection.find()) {
                articleListView.getItems().add(article);
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load articles from the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Handle article selection and display its full content.
     */
    private void handleArticleClick() {
        // Get the selected article from the ListView
        Document selectedArticle = articleListView.getSelectionModel().getSelectedItem();
        if (selectedArticle == null) {
            // Show an alert if no article is selected
            showAlert("No Selection", "Please select an article to view.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Load the DetailedNews.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Detailed_News.fxml"));
            Parent detailedViewRoot = loader.load();

            // Pass the selected article to the DetailedNewsController
            DetailedNewsController controller = loader.getController();
            controller.loadArticle(selectedArticle);

            // Open a new stage for the detailed view
            Stage detailedStage = new Stage();
            detailedStage.setTitle("Detailed News");
            detailedStage.setScene(new Scene(detailedViewRoot));
            detailedStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open detailed view. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Show an alert with the specified title, message, and type.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     * @param type    The type of the alert.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handle the Home button click to load the Admin Dashboard.
     */
    public void onHomeButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "User_Dashboard_New.fxml");
    }

    /**
     * Load a new scene specified by the given FXML file.
     *
     * @param actionEvent The ActionEvent triggering this action.
     * @param fxmlFile    The FXML file to load.
     */
    private void loadScene(ActionEvent actionEvent, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the requested scene.", Alert.AlertType.ERROR);
        }
    }
}
