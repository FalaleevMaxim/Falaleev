package ru.test.model;

import ru.test.ViewModel.CellVM;

import java.io.IOException;

public interface GameEventsListener<P> {
    void onGameStarted();
    void onScoreChange(P player, int score);
    void onCellsOpened(P player, CellVM[] opened);
    void onBombSuggested(P player, int x, int y, boolean isBomb);
    void onPlayerLoose(P player);
    void onPlayerQuit(P player);
    void onPlayerWin(P player);
    void onGameOver();
}