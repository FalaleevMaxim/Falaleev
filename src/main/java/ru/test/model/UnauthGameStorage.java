package ru.test.model;

import org.springframework.stereotype.Repository;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.Game;
import ru.test.logic.SinglePlayerGame;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository("UnauthGames")
public class UnauthGameStorage implements GameStorage<Integer,String>{
    public UnauthGameStorage() {}

    private Map<String,Game<Integer>> games = new ConcurrentHashMap<>();

    @Override
    public Game<Integer> getGameById(String id) {
        return games.get(id);
    }

    @Override
    public String createGame(Integer player,GameProperties properties) {
        Game<Integer> game = new SinglePlayerGame<>(properties);
        String id = UUID.randomUUID().toString();
        games.put(id,game);
        return id;
    }

    @Override
    public Game<Integer> getGameByPlayer(Integer player) {
        throw new NotImplementedException();
    }

    @Override
    public void removeGame(String id) {
        games.remove(id);
    }
}
