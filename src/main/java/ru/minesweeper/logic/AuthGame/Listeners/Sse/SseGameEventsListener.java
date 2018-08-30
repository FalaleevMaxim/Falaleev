package ru.minesweeper.logic.AuthGame.Listeners.Sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.minesweeper.viewmodel.cell.CellVM;
import ru.minesweeper.viewmodel.Score;
import ru.minesweeper.logic.AuthGame.Listeners.GameEventsListener;

public class SseGameEventsListener<P> implements GameEventsListener<P> {
    public SseGameEventsListener(SseEmitter emitter) {
        this.emitter = emitter;
    }

    private SseEmitter emitter;

    @Override
    public void onGameStarted() {
        try {
            emitter.send(SseEmitter.event().name("Started").data(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScoreChange(P player, int score) {

        try {
            emitter.send(SseEmitter.event().name("Score").data(new Score<>(player,score)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCellsOpened(P player, CellVM[] opened, int scoreChange) {
        class CellsOpened{
            private P player;
            private CellVM[] opened;
            private int scoreChange;
            public CellsOpened(P player, CellVM[] opened, int scoreChange) {
                this.setPlayer(player);
                this.setOpened(opened);
                this.setScoreChange(scoreChange);
            }
            public P getPlayer() {return player;}
            public void setPlayer(P player) {this.player = player;}
            public CellVM[] getOpened() {return opened;}
            public void setOpened(CellVM[] opened) {this.opened = opened;}
            public int getScoreChange() {return scoreChange;}
            public void setScoreChange(int scoreChange) {this.scoreChange = scoreChange;}
        }

        try {
            emitter.send(SseEmitter.event().name("CellsOpened").data(new CellsOpened(player,opened,scoreChange)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBombSuggested(P player, int x, int y, boolean isBomb, int scoreChange) {
        class SuggestedBomb{
            public SuggestedBomb(P player, int x, int y, boolean bomb, int scoreChange) {
                this.setPlayer(player);
                this.setX(x);
                this.setY(y);
                this.setBomb(bomb);
                this.setScoreChange(scoreChange);
            }
            private P player;
            private int x;
            private int y;
            private boolean bomb;
            private int scoreChange;
            public P getPlayer() {return player;}
            public void setPlayer(P player) {this.player = player;}
            public int getX() {return x;}
            public void setX(int x) {this.x = x;}
            public int getY() {return y;}
            public void setY(int y) {this.y = y;}
            public boolean isBomb() {return bomb;}
            public void setBomb(boolean bomb) {this.bomb = bomb;}
            public int getScoreChange() {return scoreChange;}
            public void setScoreChange(int scoreChange) {this.scoreChange = scoreChange;}
        }

        try {
            emitter.send(SseEmitter.event().name("SuggestedBomb").data(new SuggestedBomb(player,x,y,isBomb,scoreChange)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerLoose(P player) {
        try {
            emitter.send(SseEmitter.event().name("Loose").data(player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerQuit(P player) {
        try {
            emitter.send(SseEmitter.event().name("Quit").data(player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerWin(P player) {
        try {
            emitter.send(SseEmitter.event().name("Win").data(player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameOver() {
        try {
            emitter.send(SseEmitter.event().name("GameOver").data(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

