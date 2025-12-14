package com.example.cs360projectjustinfaircloth;

public class Event { // For use in event adapter

    private long id;
    private String userId;
    private String title;
    private String date;
    private String time;
    private long timestamp;
    private String description;

    public Event(long id, String userId, String title, String date, String time, long timestamp, String description) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.description = description;
    }


    public long getId() {
        return this.id;
    }
    public String getUserId() {
        return this.userId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public String getDescription() {
        return this.description;
    }

}
