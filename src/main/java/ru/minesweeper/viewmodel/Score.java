package ru.minesweeper.viewmodel;

public class Score<P>{
    public Score(P player, int score) {
        this.setPlayer(player);
        this.setScore(score);
    }
    private P player;
    private int score;

    public P getPlayer() {
        return player;
    }

    public void setPlayer(P player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
