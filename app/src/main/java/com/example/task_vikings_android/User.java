package com.example.task_vikings_android;

public class User {
    private String title;
    private String description;
    private String date;
    private String time;
    private String event;

    public User(String title, String description, String date, String time, String event) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getEvent() {
        return event;
    }
}
