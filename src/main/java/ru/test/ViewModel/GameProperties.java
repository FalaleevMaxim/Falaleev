package ru.test.ViewModel;

public class GameProperties {
    private int width;
    private int height;
    private int bombcount;
    private int score;

    public GameProperties(int width, int height, int bombcount,int score) {
        this.width = width;
        this.height = height;
        this.bombcount = bombcount;
        this.score = score;
    }

    public GameProperties() {

    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBombcount() {
        return bombcount;
    }

    public void setBombcount(int bombcount) {
        this.bombcount = bombcount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
