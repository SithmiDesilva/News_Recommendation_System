package com.example.testproject;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import java.util.List;

public class SavedNewsController {

    @FXML
    private Button home;

    @FXML
    private Button delete;

    @FXML
    private ListView<String> savedNewsListView; // ListView for saved article titles

    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;

    @FXML
    public void initialize() {
        if (savedNewsListView == null) {
            System.out.println("savedNewsListView is null! Check FXML bindings.");
            return;
        }
        initializeDatabase(); // Initialize MongoDB connection
        configureListView(); // Configure custom ListView rendering
        loadSavedArticles(); // Fetch and display saved articles
    }

    private void initializeDatabase() {
        database = MongoClients.create("mongodb://localhost:27017").getDatabase("NewsRecommendationDB");
        usersCollection = database.getCollection("userInteractions");
    }

    private void loadSavedArticles() {
        String username = UserSession.getUsername(); // Replace with your user session management logic

        if (username != null) {
            Document user = usersCollection.find(new Document("username", username)).first();

            if (user != null) {
                List<String> savedArticles = user.getList("savedArticles", String.class);

                if (savedArticles != null && !savedArticles.isEmpty()) {
                    savedNewsListView.getItems().setAll(savedArticles); // Populate ListView with titles
                } else {
                    savedNewsListView.getItems().clear();
                    showAlert("Info", "No saved articles found.", Alert.AlertType.INFORMATION);
                }
            } else {
                showAlert("Error", "User not found in the database.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "User not logged in.", Alert.AlertType.ERROR);
        }
    }

    private void configureListView() {
        savedNewsListView.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String title, boolean empty) {
                super.updateItem(title, empty);

                if (empty || title == null) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10);
                    ImageView imageView = new ImageView();

                    Document article = getArticleByTitle(title);
                    String author = article != null ? article.getString("author") : "Unknown";
                    String imagePath = article != null ? article.getString("image") : null;

                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            imageView.setImage(new Image("file:" + imagePath, 100, 100, true, true));
                        } catch (Exception e) {
                            imageView.setImage(new Image("file:placeholder.jpg", 100, 100, true, true));
                        }
                    } else {
                        imageView.setImage(new Image("file:placeholder.jpg", 100, 100, true, true));
                    }

                    VBox textContainer = new VBox(10);
                    Text titleText = new Text(title);
                    titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text authorText = new Text("By: " + author);
                    authorText.setStyle("-fx-font-size: 12; -fx-text-fill: gray;");

                    textContainer.getChildren().addAll(titleText, authorText);
                    hBox.getChildren().addAll(imageView, textContainer);
                    setGraphic(hBox);
                }
            }
        });

        savedNewsListView.setOnMouseClicked(event -> handleArticleClick());
    }

    private Document getArticleByTitle(String title) {
        Document article = usersCollection.find(new Document("savedArticles", title)).first();
        return article != null && article.getString("headline").equals(title) ? article : null;
    }

    private void handleArticleClick() {
        String selectedTitle = savedNewsListView.getSelectionModel().getSelectedItem();
        if (selectedTitle == null) {
            showAlert("No Selection", "Please select an article to view.", Alert.AlertType.WARNING);
            return;
        }

        Document selectedArticle = getArticleByTitle(selectedTitle);
        if (selectedArticle != null) {
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
    }

    @FXML
    public void onDeleteButtonOnClick(ActionEvent event) {
        String selectedArticle = savedNewsListView.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            String username = UserSession.getUsername();
            if (username != null) {
                usersCollection.updateOne(
                        new Document("username", username),
                        new Document("$pull", new Document("savedArticles", selectedArticle))
                );

                loadSavedArticles();
                showAlert("Success", "Article deleted successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "User not logged in.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "No article selected for deletion.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void onHomeButtonOnClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("User_Dashboard_New.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load home screen.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
