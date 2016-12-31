package ru.test.model;

import org.springframework.stereotype.Repository;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.Game;
import ru.test.logic.SinglePlayerGame;
import java.util.HashMap;
import java.util.Map;

@Repository("AuthGames")
public class AuthGameStorage implements GameStorage<Integer,Integer> {

    private Map<Integer,Game<Integer>> games = new HashMap<>();

    @Override
    public Game<Integer> getGameById(Integer id) {
        return getGameByPlayer(id);
    }

    @Override
    public Integer createGame(Integer player, GameProperties properties) {
        games.put(player,new SinglePlayerGame<>(properties,player));
        return games.size();
    }

    @Override
    public Game<Integer> getGameByPlayer(Integer player) {
        return games.get(player);
    }

    @Override
    public void removeGame(Integer id) {
        games.remove(id);
    }
}
