package ru.minesweeper.storage.game;

import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.logic.AuthGame.GameCycle;

/**
 * Интерфейс хранилища игр для аутентифицированных пользователей
 * @param <P> Тип идентификатора игрока
 * @param <I> Тип идентификатора игры
 */
public interface AuthGameStorage<P,I> {
    /**
     * Получение игры по идентификатору
     */
    GameCycle<P,I> getGame(I id);

    /**
     * Получение идентификатора игры
     */
    I getGameId(GameCycle<P, I> game);

    /**
     * Создание новой игры
     * @param owner идентификатор создателя игры
     * @param properties свойства игры
     * @return идентификатор созданной игры
     */
    I createGame(P owner, GameProperties properties);

    /**
     * Удаление игры по идентификатору
     */
    void removeGame(I id);
}