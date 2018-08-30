package ru.minesweeper.viewmodel;

public class ScoreModel<P>{
    public ScoreModel(P player, int score, boolean inGame) {
        this.player = player;
        this.score = score;
        this.inGame = inGame;
    }
    private P player;
    private int score;
    private boolean inGame;
    public P getPlayer() {return player;}
    public int getScore() {return score;}
    public boolean isInGame() {return inGame;}
}
