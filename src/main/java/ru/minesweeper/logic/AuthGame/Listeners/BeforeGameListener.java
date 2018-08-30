package ru.minesweeper.logic.AuthGame.Listeners;

import ru.minesweeper.viewmodel.GameProperties;

/**
 * Интерфейс обработчика событий, происходящих на этапе приглашения в игру для участника игры
 * @param <P> Тип идентификатора игрока
 */
public interface BeforeGameListener<P> {
    /**
     * Событие завершения этапа приглашений и начала подключения к игре
     */
    void gameStarted();

    /**
     * Событие присоединения нового игрока к игре
     * @param player идентификатор игрока
     */
    void playerJoined(P player);

    /**
     * Событие выхода игрока из игры
     * @param player идентификатор игрока
     */
    void playerLeft(P player);

    /**
     * Событие удаления игры (владелец покинул игру на этапе приглашений)
     */
    void gameDismissed();

    /**
     * Событие изменения свойств игры
     * @param properties новые свойства
     */
    void propertiesChange(GameProperties properties);
}