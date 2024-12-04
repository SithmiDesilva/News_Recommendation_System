package com.example.testproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationController {

    @FXML
    private AnchorPane anchorPaneOne;
    @FXML
    private AnchorPane anchorPaneTwo;
    @FXML
    private Button home;
    @FXML
    private ImageView homeImage;
    @FXML
    private ListView<Document> listViewId;

    private final RecommendationEngine recommendationEngine = new RecommendationEngine();

    @FXML
    public void initialize() {
        String currentUsername = UserSession.getUsername();
        configureListView();
        loadRecommendationsForUser(currentUsername);
    }

    private void configureListView() {
        listViewId.setCellFactory(listView -> new ListCell<>() {
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

                    VBox textContainer = new VBox(5);
                    Text titleText = new Text(title != null ? title : "Unknown Title");
                    titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text authorText = new Text("By: " + (author != null ? author : "Unknown Author"));
                    authorText.setStyle("-fx-font-size: 12; -fx-text-fill: gray;");

                    textContainer.getChildren().addAll(titleText, authorText);
                    hBox.getChildren().addAll(imageView, textContainer);
                    setGraphic(hBox);
                }
            }
        });

        listViewId.setOnMouseClicked(event -> handleArticleClick());
    }

    public void loadRecommendationsForUser(String username) {
        listViewId.getItems().clear();

        new Thread(() -> {
            try {
                Set<String> preferredCategories = fetchUserPreferredCategories(username);
                List<Document> recommendations = recommendationEngine.getRecommendations(preferredCategories, username);

                javafx.application.Platform.runLater(() -> {
                    listViewId.getItems().clear();
                    if (recommendations.isEmpty()) {
                        listViewId.getItems().add(new Document("headline", "No recommendations available"));
                    } else {
                        listViewId.getItems().addAll(recommendations);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    listViewId.getItems().clear();
                    listViewId.getItems().add(new Document("headline", "Error fetching recommendations"));
                });
            }
        }).start();
    }

    private Set<String> fetchUserPreferredCategories(String username) {
        Document user = DatabaseManager.getUserInteractionCollection()
                .find(new Document("username", username)).first();
        return user != null ? new HashSet<>(user.getList("preferredCategories", String.class)) : new HashSet<>();
    }

    @FXML
    public void onHomeButtonOnClick(ActionEvent actionEvent) {
        loadScene("User_Dashboard_New.fxml");
    }

    private void handleArticleClick() {
        Document selectedArticle = listViewId.getSelectionModel().getSelectedItem();
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

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) anchorPaneOne.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the scene: " + e.getMessage(), Alert.AlertType.WARNING);
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
