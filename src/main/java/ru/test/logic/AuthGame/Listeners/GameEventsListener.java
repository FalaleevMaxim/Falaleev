package ru.test.logic.AuthGame.Listeners;

import ru.test.ViewModel.CellVM;

import java.io.IOException;

public interface GameEventsListener<P> {
    void onGameStarted();
    void onScoreChange(P player, int score);
    void onCellsOpened(P player, CellVM[] opened, int scoreChange);
    void onBombSuggested(P player, int x, int y, boolean isBomb, int scoreChange);
    void onPlayerLoose(P player);
    void onPlayerQuit(P player);
    void onPlayerWin(P player);
    void onGameOver();
}