package ru.minesweeper.logic.AuthGame.Listeners;

//Обработчик событий для владельца игры
public interface JoinListener<P> {
    //Запрос на присоединение к игре
    void joinRequest(P player);
    //Игрок принял приглашение
    void inviteAccepted(P player);
    //Игрок отклонил приглашение
    void inviteRejected(P player);
}
