package com.reddit.demo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "fav_Post")
public class Post {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String postId;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "image")
    private String image;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "votes")
    private int votes;
    @ColumnInfo(name = "comments")
    private int comments;
    @ColumnInfo(name = "time")
    private int time;
    @ColumnInfo(name = "fav")
    private Boolean isfav;
    @ColumnInfo(name = "posttype")
    private String postType;

    public Post(String postId, String postType, String name, String image, String category, String title, String content, String url, int votes, int comments, int time, Boolean isfav) {
        this.postId = postId;
        this.postType = postType;
        this.name = name;
        this.image = image;
        this.category = category;
        this.title = title;
        this.content = content;
        this.url = url;
        this.votes = votes;
        this.comments = comments;
        this.time = time;
        this.isfav = isfav;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public int getVotes() {
        return votes;
    }

    public int getComments() {
        return comments;
    }

    public int getTime() {
        return time;
    }

    public void setIsfav(Boolean isfav) {
        this.isfav = isfav;
    }

    public Boolean isfav() {
        return isfav;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
