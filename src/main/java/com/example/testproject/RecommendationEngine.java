package com.example.testproject;

import org.bson.Document;

import java.util.*;
import java.util.concurrent.*;

public class RecommendationEngine {

    private static final ExecutorService executor = Executors.newFixedThreadPool(5); // Thread pool for concurrent tasks

    public List<Document> getRecommendations(Set<String> preferredCategories, String username) {
        // Fetch user's read and saved articles to exclude them from recommendations
        Set<String> readAndSavedArticles = fetchUserReadAndSavedArticles(username);

        // Fetch articles based on preferred categories
        List<Document> allArticles = fetchArticles(preferredCategories);

        List<Document> recommendations = new ArrayList<>();
        for (Document article : allArticles) {
            String headline = article.getString("headline");
            if (!readAndSavedArticles.contains(headline)) {
                recommendations.add(article);
            }
        }

        return recommendations;
    }

    public Map<String, List<Document>> getRecommendationsForUsers(List<String> usernames) throws InterruptedException, ExecutionException {
        Map<String, List<Document>> userRecommendations = new HashMap<>();

        List<Callable<Void>> tasks = new ArrayList<>();
        for (String username : usernames) {
            tasks.add(() -> {
                Set<String> preferredCategories = fetchUserPreferredCategories(username);
                List<Document> recommendations = getRecommendations(preferredCategories, username);
                userRecommendations.put(username, recommendations);
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get();
        }

        return userRecommendations;
    }

    private Set<String> fetchUserReadAndSavedArticles(String username) {
        Document user = DatabaseManager.getUserInteractionCollection()
                .find(new Document("username", username)).first();

        // Get read and saved articles (both stored in arrays)
        Set<String> readArticles = user != null ? new HashSet<>(user.getList("readArticles", String.class)) : new HashSet<>();
        Set<String> savedArticles = user != null ? new HashSet<>(user.getList("savedArticles", String.class)) : new HashSet<>();

        // Combine read and saved articles into one set to exclude them from recommendations
        readArticles.addAll(savedArticles);
        return readArticles;
    }

    private Set<String> fetchUserPreferredCategories(String username) {
        Document user = DatabaseManager.getUserInteractionCollection()
                .find(new Document("username", username)).first();
        return user != null ? new HashSet<>(user.getList("preferredCategories", String.class)) : new HashSet<>();
    }

    private List<Document> fetchArticles(Set<String> preferredCategories) {
        List<Document> articles = new ArrayList<>();

        // Fetch articles for each preferred category
        for (String category : preferredCategories) {
            articles.addAll(DatabaseManager.getArticleCollection()
                    .find(new Document("category", category))
                    .into(new ArrayList<>()));
        }

        return articles;
    }
}
