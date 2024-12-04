package com.example.testproject;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public class DatabaseManager {

    private static final String HOST = "localhost"; // MongoDB host
    private static final int PORT = 27017; // MongoDB port
    private static final String DATABASE_NAME = "NewsRecommendationDB"; // Your database name

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    // Static block to initialize the MongoDB connection
    static {
        try {
            // Configure MongoClientSettings
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(builder ->
                            builder.hosts(Arrays.asList(new ServerAddress(HOST, PORT))))
                    .build();

            // Initialize MongoClient and connect to the database
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DATABASE_NAME);

            System.out.println("Successfully connected to MongoDB database: " + DATABASE_NAME);
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB:");
            e.printStackTrace();
        }
    }

    // Method to get the database instance
    public static MongoDatabase getDatabase() {
        return database;
    }
    public static MongoCollection<Document> getArticleCollection() {
        return database.getCollection("articles"); // Replace with the actual collection name
    }

    // Get the "user_interactions" collection
    public static MongoCollection<Document> getUserInteractionCollection() {
        return database.getCollection("userInteractions"); // Replace with the actual collection name
    }

    // Close the MongoClient (optional, for graceful shutdown)
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }

    public static MongoCollection<Document> getuserCollection() {
        return getDatabase().getCollection("user");
    }
}
