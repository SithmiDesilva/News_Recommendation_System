package com.example.testproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Article {
    private final StringProperty category;
    private final StringProperty author;
    private final StringProperty headline;
    private final StringProperty content;


    public Article(String category, String author, String headline, String content) {
        this.category = new SimpleStringProperty(category);
        this.author = new SimpleStringProperty(author);
        this.headline = new SimpleStringProperty(headline);
        this.content = new SimpleStringProperty(content);
        // Initialize the image property
    }

    public String getCategory() {
        return category.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public String getHeadline() {
        return headline.get();
    }

    public String getContent() {
        return content.get();
    }



    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty authorProperty() {
        return author;
    }

    public StringProperty headlineProperty() {
        return headline;
    }

    public StringProperty contentProperty() {
        return content;
    }


}
