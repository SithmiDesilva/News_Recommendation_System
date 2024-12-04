package com.example.testproject;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles user session information and interactions with the recommendation engine.
 */
public class UserSession {

    // Singleton instance
    private static volatile UserSession instance;
    static String currentUser;
    private String currentUserEmail;

    // Mocked data for demonstration purposes
    private final Set<String> likedCategories = new HashSet<>();
    private final Set<String> readCategories = new HashSet<>();

    // Private constructor to prevent direct instantiation
    private UserSession() {}

    /**
     * Get the singleton instance of UserSession.
     * Thread-safe using double-checked locking.
     *
     * @return the single instance of UserSession.
     */
    public static void setCurrentUser(User user) {
        currentUser = String.valueOf(user);
    }


    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    /**
     * Get the liked categories for the current user.
     *
     * @return a set of liked categories.
     */
    public static Set<String> getLikedCategories(Object currentUser) {
        return getInstance().likedCategories;
    }

    /**
     * Get the read categories for the current user.
     *
     * @return a set of read categories.
     */
    public static Set<String> getReadCategories(Object currentUser) {
        return getInstance().readCategories;
    }

    public static Set<String> getReadArticles(Object currentUser) {
        return getInstance().readCategories;
    }

    public static Set<String> getSavedArticles(Object currentUser) {
        return getInstance().readCategories;
    }

    public static void logout() {  instance = null; // Nullify the instance to log out the user
    }

    /**
     * Add a category to the liked categories for the current user.
     *
     * @param category the category to add.
     */
    public void addLikedCategory(String category) {
        likedCategories.add(category);
    }



    /**
     * Get the current logged-in user's username.
     *
     * @return the current user's username.
     */
    public String getCurrentUser()
    {
        return this.currentUser;
    }

    /**
     * Set the current logged-in user's username.
     *
     * @param username the username to set.
     */
    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    /**
     * Get the current logged-in user's email.
     *
     * @return the current user's email.
     */
    public String getCurrentUserEmail() {
        return this.currentUserEmail;
    }

    /**
     * Set the current logged-in user's email.
     *
     * @param email the email to set.
     */
    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
    }

    /**
     * Get the current logged-in user's username.
     * Alias for backward compatibility with other controllers.
     *
     * @return the current user's username.
     */
    public static String getUsername() {
        return getInstance().getCurrentUser();
    }

    /**
     * Set the current logged-in user's username.
     *
     * @param username the username to set.
     */
    public static void setUsername(String username) {
        getInstance().setCurrentUser(username);
    }
}
