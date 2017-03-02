package ru.test.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.service.InvitationsService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository("AuthGames")
public class AuthGameStorageImpl<P> implements AuthGameStorage<P,String> {
    @Autowired
    private InvitationsService<P> invitationsService;

    private Map<String,GameCycle<P>> gameById = new HashMap<>();
    private Map<GameCycle<P>,String> idByGame = new HashMap<>();

    @Override
    public GameCycle<P> getGame(String id) {
        return gameById.get(id);
    }

    @Override
    public String getGameId(GameCycle<P> game) {
        return idByGame.get(game);
    }

    @Override
    public String createGame(P ownerId, GameProperties properties) {
        String id = UUID.randomUUID().toString();
        GameCycle<P> game = new GameCycle<>(ownerId, properties,invitationsService);
        gameById.put(id, game);
        idByGame.put(game,id);
        return id;
    }

    @Override
    public void removeGame(String id) {
        idByGame.remove(gameById.get(id));
        gameById.remove(id);
    }

    @Override
    public boolean tryRemoveGame(String id) {
        GameCycle<P> game = gameById.get(id);
        if(game==null) return false;
        if(game.getPlayers().size()>0){
            return false;
        }
        else{
            removeGame(id);
            return true;
        }
    }
}