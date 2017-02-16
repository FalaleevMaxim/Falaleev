package ru.test.logic.AuthGame;

import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.Board;
import ru.test.model.GameEventsListener;

import java.util.Collection;

public interface AuthGame<P> {
//region Игроки
    Collection<P> getPlayers();
    boolean hasPlayer(P player);
    void quit(P player);
    Collection<ScoreModel<P>> getScores();
    int getScore(P player);
//endregion

//region События
    //Установка обработчика, передающего игроку события
    void setListener(P player, GameEventsListener<P> listener);
    //Получить обработчик событий игрока
    GameEventsListener<P> getListener(P player);
//endregion

//region Игра
    //Начат отсчёт времени
    boolean isStarted();
    //Начало отсчёта времени
    long getStartTime();
    //Получить свойства игры
    GameProperties getProperties();
    //Получить поле игры
    Board.Cell[][] getField();
    //Игрок открывает клетку поля
    CellVM[] openCell(int x, int y, P player);
    //Игрок проверяет бомбу
    boolean suggestBomb(int x, int y, P player);
//endregion

//region Итоги
    //Завершена ли игра
    boolean isFinished();
    //Время завершения игры
    long getFinishTime();
    //Является ли победителем игрок
    boolean isWinner(P player);
    //Является ли проигравшим игрок
    boolean isLooser(P player);
    //Время выбывания игрока из игры
    long getOutTime(P player);
//endregion

    //Класс для предоставления информации о счёте игрока
    class ScoreModel<P>{
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
}