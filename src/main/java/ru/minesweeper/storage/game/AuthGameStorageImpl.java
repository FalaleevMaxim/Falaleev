package ru.minesweeper.storage.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.logic.AuthGame.GameCycle;
import ru.minesweeper.service.invitation.InvitationsService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository("AuthGames")
public class AuthGameStorageImpl<P> implements AuthGameStorage<P,String> {
    private final InvitationsService<P,String> invitationsService;

    private Map<String,GameCycle<P, String>> gameById = new ConcurrentHashMap<>();
    private Map<GameCycle<P, String>,String> idByGame = new ConcurrentHashMap<>();

    @Autowired
    public AuthGameStorageImpl(InvitationsService<P,String> invitationsService) {
        this.invitationsService = invitationsService;
    }

    @Override
    public GameCycle<P, String> getGame(String id) {
        return gameById.get(id);
    }

    @Override
    public String getGameId(GameCycle<P, String> game) {
        return idByGame.get(game);
    }

    @Override
    public String createGame(P ownerId, GameProperties properties) {
        String id = UUID.randomUUID().toString();
        GameCycle<P, String> game = new GameCycle<>(id, ownerId, properties,invitationsService);
        gameById.put(id, game);
        idByGame.put(game,id);
        return id;
    }

    @Override
    public void removeGame(String id) {
        idByGame.remove(gameById.get(id));
        gameById.remove(id);
    }
}