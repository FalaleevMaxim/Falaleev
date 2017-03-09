package ru.test.logic.AuthGame.Listeners;

import ru.test.ViewModel.GameProperties;
import ru.test.model.Storage;
import ru.test.model.User;

public class DbBeforeGameListener implements BeforeGameListener<Integer>{
    public DbBeforeGameListener(Storage<User> userStorage, Integer playerId, BeforeGameListener<Integer> otherListener) {
        this.userStorage = userStorage;
        this.playerId = playerId;
        this.otherListener = otherListener;
    }

    private final Storage<User> userStorage;
    private final Integer playerId;
    private final BeforeGameListener<Integer> otherListener;

    @Override
    public void gameStarted() {
        otherListener.gameStarted();
    }

    @Override
    public void playerJoined(Integer player) {
        otherListener.playerJoined(player);
    }

    @Override
    public void playerLeft(Integer player) {
        if(playerId.equals(player)){
            User user = userStorage.findById(playerId);
            user.setCurrentGameId(null);
            userStorage.update(user);
        }
        otherListener.playerLeft(player);
    }

    @Override
    public void gameDismissed() {
        User user = userStorage.findById(playerId);
        user.setCurrentGameId(null);
        userStorage.update(user);
        otherListener.gameDismissed();
    }

    @Override
    public void propertiesChange(GameProperties properties) {
        otherListener.propertiesChange(properties);
    }
}
