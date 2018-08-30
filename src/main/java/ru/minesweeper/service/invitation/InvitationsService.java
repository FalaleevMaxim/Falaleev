package ru.minesweeper.service.invitation;

import ru.minesweeper.logic.AuthGame.GameCycle;
import ru.minesweeper.logic.AuthGame.Listeners.InviteListener;

import java.util.Collection;

/**
 * Интерфейс хранилища приглашений игроков в игры
 * @param <P> Тип идентификатора игрока
 */
public interface InvitationsService<P,I> {
    /**
     * @return  Возвращает все приглашения, отправленные заданному игроку
     * @param player идентификатор игрока
     */
    Collection<GameCycle<P,I>> getInvitations(P player);

    /**
     * @return Возвращает количество приглашений отправленных заданному игроку
     * @param player идентификатор игрока
     */
    int getInvitationsCount(P player);

    /**
     * Пригласить игрока в игру
     * @param player идентификатор игрока
     * @param game ссылка на игру
     */
    void invite(P player, GameCycle<P,I> game);

    /**
     * Подтверждение участия игрока владельцем игры
     * @param player идентификатор игрока
     * @param game ссылка на игру
     */
    void confirm(P player,GameCycle<P,I> game);

    /**
     * Отменить приглашение игрока в игру
     * @param player идентификатор игрока
     * @param game ссылка на игру
     */
    void uninvite(P player, GameCycle<P,I> game);

    /**
     * Удаление приглашения без оповещения (если приглашение принято или отклонено или игра уже началась)
     * @param player идентификатор игрока
     * @param game ссылка на игру
     */
    void removeInvitation(P player, GameCycle<P,I> game);

    /**
     * Подписка игрока на приглашения
     * @param player идентификатор игрока
     * @param listener обработчик приглашений игрока
     */
    void setInvitationListener(P player, InviteListener<I> listener);

}