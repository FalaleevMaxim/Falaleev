package ru.test.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.AuthGame.GameCycle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository("AuthGames")
public class AuthGameStorageImpl implements AuthGameStorage<Integer,String> {
    @Autowired
    Storage<User> userStorage;

    private Map<String,GameCycle<Integer>> games = new HashMap<>();

    @Override
    public GameCycle<Integer> getGame(String id) {
        return games.get(id);
    }

    @Override
    public String createGame(Integer ownerId, GameProperties properties) {
        String id = UUID.randomUUID().toString();
        games.put(id,new GameCycle<>(ownerId,properties));
        return id;
    }

    @Override
    public void removeGame(String id) {
        games.remove(id);
    }
}