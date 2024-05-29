package com.example.codeatlas;
public class Contest {
    private int id;
    private String name;
    private long startTimeSeconds;
    private long durationSeconds;

    public Contest(int id, String name, long startTimeSeconds, long durationSeconds) {
        this.id = id;
        this.name = name;
        this.startTimeSeconds = startTimeSeconds;
        this.durationSeconds = durationSeconds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public void setStartTimeSeconds(long startTimeSeconds) {
        this.startTimeSeconds = startTimeSeconds;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
