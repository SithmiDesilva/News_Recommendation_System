package com.example.testproject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.bson.Document;


import java.util.ArrayList;
import java.util.List;

public class DetailedNewsController {

    @FXML
    private AnchorPane anchorPaneOne;
    @FXML
    private AnchorPane anchorPaneTwo;
    @FXML
    private Button buttonLike;
    @FXML
    private Button buttonSave;
    @FXML
    private ImageView like;
    @FXML
    private ImageView save;
    @FXML
    private ImageView imageViewId;
    @FXML
    private Label detailedNews;
    @FXML
    private ImageView back;
    @FXML
    private TextField headlineField;
    @FXML
    private TextArea contentField;

    private Document currentArticle; // Currently loaded article
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> userInteractionsCollection;

    public DetailedNewsController() {
        // Initialize MongoDB client and collections
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("NewsRecommendationDB");
        userInteractionsCollection = database.getCollection("userInteractions");
    }

    /**
     * Load article details into the UI.
     */
    public void loadArticle(Document article) {
        if (article != null) {
            this.currentArticle = article;
            headlineField.setText(article.getString("headline"));
            contentField.setText(article.getString("content"));
            handleUserInteraction("read");
        } else {
            showAlert("Error", "Article could not be loaded.");
        }
    }

    private String getCurrentUsername() {
        // Replace with actual session management logic
        return UserSession.getUsername(); // Retrieve the logged-in username
    }

    @FXML
    public void onButtonLikeButtonOnClick(ActionEvent actionEvent) {
        handleUserInteraction("like");
    }

    @FXML
    public void onButtonSaveButtonOnClick(ActionEvent actionEvent) {
        handleUserInteraction("save");
    }

    /**
     * Handles user interactions like "like" or "save" for the current article.
     *
     * @param action The action to record (e.g., "like" or "save").
     */
    private void handleUserInteraction(String action) {
        if (currentArticle != null) {
            storeUserInteraction(action, currentArticle);
            displayUserInteractions(); // Display updated interactions
        } else {
            showAlert("Error", "No article loaded to " + action + ".");
        }
    }

    /**
     * Stores a user interaction (like, save) in the `userInteractions` collection.
     */
    private void storeUserInteraction(String action, Document article) {
        String username = getCurrentUsername();
        if (username == null) {
            showAlert("Error", "User not logged in.");
            return;
        }

        String headline = article.getString("headline");
        String category = article.getString("category");

        // Find the user interaction document
        Document userDoc = userInteractionsCollection.find(new Document("username", username)).first();

        if (userDoc == null) {
            // Create a new user interaction document if it doesn't exist
            userDoc = new Document("username", username)
                    .append("likedArticles", new ArrayList<>())
                    .append("preferredCategories", new ArrayList<>())
                    .append("savedArticles", new ArrayList<>())
                    .append("readArticles", new ArrayList<>());
            userInteractionsCollection.insertOne(userDoc);
        }

        // Update the specific field based on the action
        String fieldToUpdate = switch (action) {
            case "like" -> "likedArticles";
            case "save" -> "savedArticles";
            case "read" -> "readArticles";
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        };

        // Add the article headline to the corresponding field
        userInteractionsCollection.updateOne(
                new Document("username", username),
                new Document("$addToSet", new Document(fieldToUpdate, headline))
        );

        // If the category is new, add it to preferred categories
        if (category != null) {
            userInteractionsCollection.updateOne(
                    new Document("username", username),
                    new Document("$addToSet", new Document("preferredCategories", category))
            );
        }
    }

    /**
     * Fetches and displays all user interactions in the `userInteractions` collection in the desired format.
     */
    private void displayUserInteractions() {
        System.out.println("User Interactions in the collection:");
        for (Document doc : userInteractionsCollection.find()) {
            System.out.println(formatUserInteractions(doc));
        }
    }

    /**
     * Formats a user interaction document in the desired format.
     *
     * @param doc The MongoDB document representing a user interaction.
     * @return A formatted string representing the document.
     */
    private String formatUserInteractions(Document doc) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("_id: ").append(doc.getObjectId("_id")).append("\n");
        formatted.append("username: ").append(doc.getString("username")).append("\n");
        formatted.append("email: ").append(doc.getString("email")).append("\n");
        formatted.append("password: ").append(doc.getString("password")).append("\n");

        // Format likedArticles
        formatted.append("likedArticles: Array (").append(getArraySize(doc, "likedArticles")).append(")\n");
        List<String> likedArticles = doc.getList("likedArticles", String.class);
        if (likedArticles != null) {
            for (int i = 0; i < likedArticles.size(); i++) {
                formatted.append("    ").append(i).append(": ").append(likedArticles.get(i)).append("\n");
            }
        }

        // Format preferredCategories
        formatted.append("preferredCategories: Array (").append(getArraySize(doc, "preferredCategories")).append(")\n");
        List<String> preferredCategories = doc.getList("preferredCategories", String.class);
        if (preferredCategories != null) {
            for (int i = 0; i < preferredCategories.size(); i++) {
                formatted.append("    ").append(i).append(": ").append(preferredCategories.get(i)).append("\n");
            }
        }

        // Format savedArticles
        formatted.append("savedArticles: Array (").append(getArraySize(doc, "savedArticles")).append(")\n");
        List<String> savedArticles = doc.getList("savedArticles", String.class);
        if (savedArticles != null) {
            for (int i = 0; i < savedArticles.size(); i++) {
                formatted.append("    ").append(i).append(": ").append(savedArticles.get(i)).append("\n");
            }
        }


        // Format readArticles
        formatted.append("readArticles: Array (").append(getArraySize(doc, "readArticles")).append(")\n");
        List<String> readArticles = doc.getList("readArticles", String.class);
        if (readArticles != null) {
            for (int i = 0; i < readArticles.size(); i++) {
                formatted.append("    ").append(i).append(": ").append(readArticles.get(i)).append("\n");
            }
        }


        return formatted.toString();
    }

    /**
     * Safely gets the size of an array field in the document.
     */
    private int getArraySize(Document doc, String key) {
        List<?> list = doc.getList(key, Object.class);
        return list != null ? list.size() : 0;
    }

    /**
     * Displays an alert message to the user.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
