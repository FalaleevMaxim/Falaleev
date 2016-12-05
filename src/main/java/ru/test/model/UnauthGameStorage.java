package ru.test.model;

import org.springframework.stereotype.Repository;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.Game;
import ru.test.logic.UnauthGame;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class UnauthGameStorage implements GameStorage<Integer,Integer>{
    public UnauthGameStorage() {}

    List<Game<Integer>> games = new ArrayList<>();

    @Override
    public Game<Integer> getGameById(Integer id) {
        return games.get(id);
    }

    @Override
    public Game<Integer> getGameByPlayer(Integer player) {
        throw new NotImplementedException();
    }

    @Override
    public void connectGame(Integer player, Game<Integer> game) {
        throw new NotImplementedException();
    }

    @Override
    public void connectGameById(Integer player, Integer gameId) {
        throw new NotImplementedException();
    }

    @Override
    public Integer createGame(Integer player,GameProperties properties) {
        Game<Integer> game = new UnauthGame(properties);
        games.add(game);
        return games.size()-1;
    }

    @Override
    public void quitGame(Integer player) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<Game<Integer>> getOpenedGames() {
        throw new NotImplementedException();
    }
}
