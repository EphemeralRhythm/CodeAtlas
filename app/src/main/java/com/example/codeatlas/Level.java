package com.example.codeatlas;

import java.util.ArrayList;

public class Level {
    private int index;
    private String title;
    private int state;
    private int rewards_stars;
    private int rewards_diamonds;
    public static final int LOCKED = -1;
    public static final int OPEN = 0;
    public static final int COMPLETED = 1;
    public ArrayList<Page> pages;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getState() {
        return state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRewards_stars() {
        return rewards_stars;
    }

    public void setRewards_stars(int rewards_stars) {
        this.rewards_stars = rewards_stars;
    }

    public int getRewards_diamonds() {
        return rewards_diamonds;
    }

    public void setRewards_diamonds(int rewards_diamonds) {
        this.rewards_diamonds = rewards_diamonds;
    }

    public void setState(int state) {
        this.state = state;
    }
}
