package com.example.testproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String username;
    private String email;
    private String signupDate;

    private Set<String> selectedCategories = new HashSet<>();
    private Set<String> likedCategories = new HashSet<>();
    private Set<String> readCategories = new HashSet<>();
    private Set<String> savedCategories = new HashSet<>();

    // Constructor with all parameters
    public User(String username, String email, String signupDate) {
        this.username = username;
        this.email = email;
        this.signupDate = signupDate;
    }

    // Optional constructor for testing or temporary creation of a User with only a username (if needed)
    public User(String username) {
        this.username = username;
        this.email = "";  // Default value
        this.signupDate = "";  // Default value
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSignupDate() {
        return signupDate;
    }

    public Set<String> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(Set<String> categories) {
        this.selectedCategories = categories;
    }

    public Set<String> getLikedCategories() {
        return likedCategories;
    }

    public void setLikedCategories(Set<String> likedCategories) {
        this.likedCategories = likedCategories;
    }

    public Set<String> getReadCategories() {
        return readCategories;
    }


    // ObservableValue properties for binding in TableView
    public StringProperty usernameProperty() {
        return new SimpleStringProperty(username);
    }

    public StringProperty emailProperty() {
        return new SimpleStringProperty(email);
    }

    public StringProperty signupDateProperty() {
        return new SimpleStringProperty(signupDate);
    }

    // Setter methods to allow updating of user details
    public void setUsername(String updatedUsername) {
        this.username = updatedUsername;
    }

    public void setEmail(String updatedEmail) {
        this.email = updatedEmail;
    }

    public void setSignupDate(String updatedSignupDate) {
        this.signupDate = updatedSignupDate;
    }

    public Set<String> getSavedCategories() {
        return savedCategories;
    }

    public void setSavedCategories(Set<String> savedCategories) {
        this.savedCategories = savedCategories;
    }

    /**
     * Adds a liked category to the user's liked categories.
     */

}
