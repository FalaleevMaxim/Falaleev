package ru.test.model;

import ru.test.ViewModel.GameProperties;
import ru.test.logic.Game;

import java.util.Collection;

//Хранилище игр. I - тип идентификатора игры.
public interface GameStorage<P,I> {
    Game<P> getGameById(I id);
    I createGame(P player, GameProperties properties);
    Game<P> getGameByPlayer(P player);
    void removeGame(I id);
}

