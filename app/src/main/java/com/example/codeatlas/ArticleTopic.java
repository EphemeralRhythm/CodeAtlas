package com.example.codeatlas;

import java.util.ArrayList;

public class ArticleTopic {
    private String title;
    private ArrayList<Article> articles;
    public int index;

    public String getTitle() {
        return title;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
