package ru.test.model;

import ru.test.ViewModel.GameProperties;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.logic.Game;

import java.util.Collection;

//Хранилище игр. I - тип идентификатора игры.
public interface AuthGameStorage<P,I> {
    GameCycle<P> getGame(I id);
    I getGameId(GameCycle<P> game);
    I createGame(P owner, GameProperties properties);
    void removeGame(I id);
    //Удалить игру только если в ней нет игроков
    boolean tryRemoveGame(I id);
}