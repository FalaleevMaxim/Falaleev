package ru.test.Model;

import ru.test.ViewModel.GameProperties;
import ru.test.logic.Game;

import java.util.Collection;

//Хранилище игр. I - тип идентификатора игры.
public interface GameStorage<P,I> {
    //Получить игру по её идентификатору
    Game<P> getGameById(I id);
    //Получить текущую игру игрока
    Game<P> getGameByPlayer(P player);
    //Подключить игрока к игре
    void connectGame(P player, Game<P> game);
    void connectGameById(P player, I gameId);
    //Игрок создаёт игру
    I createGame(P player, GameProperties properties);
    //Игрок покидает игру
    void quitGame(P player);
    //Получить все игры, к которым можно присоединиться
    Collection<Game<P>> getOpenedGames();
}

