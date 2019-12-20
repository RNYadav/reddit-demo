package com.reddit.demo.model;

import org.json.JSONObject;

public class Comment {
    private String name, text, image;
    private int time, points;
    private JSONObject replies;

    public Comment(String name, String image, String text, int time, int points, JSONObject replies) {
        this.name = name;
        this.image = image;
        this.text = text;
        this.time = time;
        this.points = points;
        this.replies = replies;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public int getTime() {
        return time;
    }

    public int getPoints() {
        return points;
    }

    public JSONObject getReplies() {
        return replies;
    }

    public static Comment CommentParser(JSONObject item){
        return new Comment(item.optString("author"),item.optString("author_fullname"),
                item.optString("body"),
                item.optInt("created_utc"),
                item.optInt("score"),
                item.optJSONObject("replies"));
    }
}
