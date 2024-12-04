package com.example.testproject;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewNewsController {

    @FXML
    private ListView<Document> articleListView;

    // MongoDB connection parameters
    private static final String DATABASE_NAME = DatabaseManager.getDatabase().getName();
    private static final String COLLECTION_NAME = "articles";

    // Keywords for categorization
    private static final Map<String, String[]> CATEGORY_KEYWORDS = new HashMap<>() {{
        put("Technology", new String[]{"AI", "machine learning", "technology", "innovation", "software", "computers"});
        put("Sports", new String[]{"football", "cricket", "Olympics", "sports", "athlete", "tournament"});
        put("Health", new String[]{"health", "medicine", "fitness", "wellness", "disease", "cancer"});
        put("Politics", new String[]{"election", "government", "policy", "president", "senate", "politics"});
        put("Entertainment", new String[]{"movie", "music", "entertainment", "actor", "Hollywood", "Netflix"});
        put("Science", new String[]{"research", "science", "space", "physics", "biology", "discovery"});
        put("Business", new String[]{"stock", "market", "business", "economy", "startup", "corporate"});
    }};

    @FXML
    public void initialize() {
        setupListView();
        loadArticles();
    }

    private void setupListView() {
        articleListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Document article, boolean empty) {
                super.updateItem(article, empty);
                if (empty || article == null) {
                    setGraphic(null);
                } else {
                    String title = article.getString("headline");
                    String author = article.getString("author");
                    String imagePath = article.getString("image");

                    HBox hBox = new HBox(10);

                    ImageView imageView = new ImageView();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            Image image = new Image("file:" + imagePath, 100, 100, true, true);
                            imageView.setImage(image);
                        } catch (Exception e) {
                            imageView.setImage(null);
                        }
                    }

                    VBox textContainer = new VBox(10);
                    Text titleText = new Text(title != null ? title : "Unknown");
                    titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text authorText = new Text("By: " + (author != null ? author : "Unknown"));
                    authorText.setStyle("-fx-font-size: 12; -fx-text-fill: gray;");

                    textContainer.getChildren().addAll(titleText, authorText);

                    hBox.getChildren().addAll(imageView, textContainer);
                    setGraphic(hBox);
                }
            }
        });

        articleListView.setOnMouseClicked(event -> handleArticleClick());
    }

    private void loadArticles() {
        try (var mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            articleListView.getItems().clear();

            for (Document article : collection.find()) {
                String category = article.getString("category");

                if (category == null || category.isEmpty()) {
                    category = categorizeArticle(article.getString("content"));
                    article.put("category", category);
                    updateArticleCategoryInDb(collection, article.getObjectId("_id"), category);
                }

                articleListView.getItems().add(article);
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load articles from the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String categorizeArticle(String content) {
        if (content == null || content.isEmpty()) return "Uncategorized";

        for (Map.Entry<String, String[]> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            String[] keywords = entry.getValue();

            for (String keyword : keywords) {
                if (content.toLowerCase().contains(keyword.toLowerCase())) {
                    return category;
                }
            }
        }
        return "Uncategorized";
    }

    private void updateArticleCategoryInDb(MongoCollection<Document> collection, Object articleId, String category) {
        try {
            collection.updateOne(
                    Filters.eq("_id", articleId),
                    new Document("$set", new Document("category", category))
            );
        } catch (Exception e) {
            showAlert("Error", "Could not update article category in the database.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void handleArticleClick() {
        Document selectedArticle = articleListView.getSelectionModel().getSelectedItem();
        if (selectedArticle == null) {
            showAlert("No Selection", "Please select an article to view.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Detailed_News.fxml"));
            Parent detailedViewRoot = loader.load();

            DetailedNewsController controller = loader.getController();
            controller.loadArticle(selectedArticle);

            Stage detailedStage = new Stage();
            detailedStage.setTitle("Detailed News");
            detailedStage.setScene(new Scene(detailedViewRoot));
            detailedStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open detailed view. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onHomeButtonOnClick(ActionEvent actionEvent) {
        loadScene(actionEvent, "User_Dashboard_New.fxml");
    }

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
