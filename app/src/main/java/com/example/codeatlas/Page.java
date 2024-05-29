package com.example.codeatlas;

import java.util.ArrayList;

public class Page {
    private int index;
    private int type; // 0 info 1 quiz
    private String content;
    private String correctAnswer;
    public ArrayList<String> choices;
    public static final int INFO = 0;
    public static final int QUIZ = 1;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
