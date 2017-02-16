package ru.test.model;

import ru.test.logic.AuthGame.GameCycle;

//Обработчик событий приглашений для игрока
public interface InviteListener {
    //Приглашение в игру
    void invited(GameCycle game);
    //Подтверждение участия в игре
    void confirmed(GameCycle game);
    //Приглашение отменено
    void uninvited(GameCycle game);
}