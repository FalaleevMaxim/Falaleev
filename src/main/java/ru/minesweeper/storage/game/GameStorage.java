package ru.minesweeper.storage.game;

import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.logic.UnauthGame;

/**
 * Интерфейс хранилища игр для незарегистрированных пользователей
 * @param <I> Тип идентификатора игры
 */
public interface GameStorage<I> {
    UnauthGame getGame(I id);
    I createGame(GameProperties properties);
    void removeGame(I id);
}
