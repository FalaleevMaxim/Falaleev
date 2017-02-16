package ru.test.model;

//Обработчик событий для владельца игры
public interface JoinListener<P> {
    //Запрос на присоединение к игре
    void joinRequest(P player);
    //Игрок принял приглашение
    void inviteAccepted(P player);
    //Игрок отклонил приглашение
    void inviteRejected(P player);
}
