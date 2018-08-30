package ru.minesweeper.logic.AuthGame.Listeners;

//Обработчик событий приглашений для игрока
//I - тип идентификатора игры
public interface InviteListener<I> {
    //Приглашение в игру
    void invited(I game);
    //Подтверждение участия в игре
    void confirmed(I game);
    //Приглашение отменено
    void uninvited(I game);
}