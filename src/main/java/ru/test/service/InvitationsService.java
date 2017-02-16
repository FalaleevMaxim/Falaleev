package ru.test.service;

import ru.test.logic.AuthGame.GameCycle;
import ru.test.model.InviteListener;

import java.util.Collection;

//Интерфейс хранилища приглашений игроков в игры
public interface InvitationsService<P> {
    //Получить все приглашения, отправленные игроку
    Collection<GameCycle<P>> getInvitations(P player);
    //Пригласить игрока в игру
    void invite(P player, GameCycle<P> game);
    //Отменить приглашение игрока в игру
    void uninvite(P player, GameCycle<P> game);
    //Удаление приглашения (приглашение принято/отклонено или игра уже началась)
    void removeInvitation(P player, GameCycle<P> game);
    //Подписка игрока на приглашения
    void setInvitationListener(P player, InviteListener listener);
    //Подтверждение участия игрока владельцем игры
    void confirm(P player,GameCycle<P> game);
}