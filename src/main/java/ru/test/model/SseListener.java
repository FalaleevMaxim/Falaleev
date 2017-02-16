package ru.test.model;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.test.ViewModel.CellVM;

import java.io.IOException;
import java.util.ArrayList;

public class SseListener<P> implements GameEventsListener<P> {

    private SseEmitter gameStatusEmitter;
    private SseEmitter ScoreEventEmitter;
    private SseEmitter BombSuggestedEmitter;
    private SseEmitter CellOpenedEmitter;
    private ArrayList<String> unsentMessages = new ArrayList<>();

    public SseListener(SseEmitter gameStatusEmitter, SseEmitter scoreEventEmitter, SseEmitter bombSuggestedEmitter, SseEmitter cellOpenedEmitter) {
        this.gameStatusEmitter = gameStatusEmitter;
        ScoreEventEmitter = scoreEventEmitter;
        BombSuggestedEmitter = bombSuggestedEmitter;
        CellOpenedEmitter = cellOpenedEmitter;
    }

    @Override
    public void onGameStarted(){
        try {
            gameStatusEmitter.send("started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScoreChange(P player, int score) {

    }

    @Override
    public void onCellsOpened(P player, CellVM[] opened) {

    }

    @Override
    public void onBombSuggested(P player, int x, int y, boolean isBomb) {

    }

    @Override
    public void onPlayerLoose(P player) {

    }

    @Override
    public void onPlayerQuit(P player) {

    }

    @Override
    public void onPlayerWin(P player) {

    }

    @Override
    public void onGameOver() {

    }

    public ArrayList<String> getUnsentMessages() {
        return new ArrayList<>(unsentMessages);
    }
}
