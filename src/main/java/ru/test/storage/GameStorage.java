package ru.test.storage;

import ru.test.ViewModel.GameProperties;
import ru.test.logic.UnauthGame;

/**
 * Интерфейс хранилища игр для незарегистрированных пользователей
 * @param <I> Тип идентификатора игры
 */
public interface GameStorage<I> {
    UnauthGame getGame(I id);
    I createGame(GameProperties properties);
    void removeGame(I id);
}
