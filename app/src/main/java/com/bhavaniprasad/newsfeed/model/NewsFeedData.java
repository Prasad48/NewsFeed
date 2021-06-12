package com.bhavaniprasad.newsfeed.model;

import java.util.List;

public class NewsFeedData {

    private String author;
    private String url;
    private String name;
    private String title;
    private String description;
    private String imageurl;
    private String publishedAt;
    private String content;

    private List<NewsFeedData> articles;

    public List<NewsFeedData> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsFeedData> articles) {
        this.articles = articles;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
