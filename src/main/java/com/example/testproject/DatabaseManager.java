package com.example.testproject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseManager {
    public static void main(String[] args) {
        // Replace the URI with your MongoDB connection string
        String uri = "mongodb://localhost:27017"; // Default URI for local MongoDB

        // Create a MongoClient
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            // Connect to the database
            MongoDatabase database = mongoClient.getDatabase("NewsRecommendationDB"); // Replace 'testdb' with your database name

            // Verify connection by printing database name
            System.out.println("Connected to database: " + database.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
