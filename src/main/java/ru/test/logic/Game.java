package ru.test.logic;

import ru.test.ViewModel.CellVM;

import java.util.Collection;
import java.util.Map;

//Интерфейс многопользовательской игры.
//P - тип, которым идентифицируются игроки (например, числовой id, имя пользователя, объект игрока и т.п.)
public interface Game<P> {
    Board getBoard();

    //Присоединить игрока
    void addPlayer(P player);

    //Убрать игрока из игры
    void removePlayer(P player);

    //Проверить, подключен ли игрок к игре
    boolean hasPlayer(P player);

    //Получить всех игроков
    Collection<P> getPlayers();

    //Начать игру
    void start();

    //Проверить, начата ли игра
    boolean isStarted();

    boolean isWinner(P player);

    //Проверить, закончилась ли игра
    boolean isFinished();

    //Получить очки всех игроков
    Map<P,Integer> getScores();

    //Получить очки игрока
    Integer getScore(P player);

    long getStartTime();

    //Игрок предполагает что в клетке бомба
    boolean suggestBomb(int x, int y, P player);

    //Игрок открывает клетку
    CellVM[] openCell(int x, int y, P player);

}
