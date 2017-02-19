package ru.test.model;

import ru.test.ViewModel.GameProperties;
import ru.test.logic.UnauthGame;

public interface GameStorage<I> {
    UnauthGame getGame(I id);
    I createGame(GameProperties properties);
    void removeGame(I id);
}
