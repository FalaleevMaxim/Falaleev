package ru.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.storage.AuthGameStorage;
import ru.test.logic.AuthGame.Listeners.InviteListener;

import java.util.*;

/**
 * Реализация интерфейса {@link InvitationsService}
 * @param <P> Тип идентификатора
 */
@Service
public class InvitationServiceImpl<P> implements InvitationsService<P> {
    private Map<P,Set<GameCycle<P>>> invitations = new HashMap<>();
    private Map<P,InviteListener<String>> listeners = new HashMap<>();

    private final AuthGameStorage<P,String> gameStorage;

    @Autowired
    public InvitationServiceImpl(AuthGameStorage<P, String> gameStorage) {
        this.gameStorage = gameStorage;
    }

    @Override
    public Collection<GameCycle<P>> getInvitations(P player) {
        if(invitations.get(player)==null) return new ArrayList<>();
        return new ArrayList<>(invitations.get(player));
    }

    @Override
    public int getInvitationsCount(P player) {
        if(invitations.get(player)==null) return 0;
        return invitations.get(player).size();
    }

    @Override
    public void invite(P player, GameCycle<P> game) {
        if(!invitations.containsKey(player)) invitations.put(player,new HashSet<>());
        invitations.get(player).add(game);
        InviteListener<String> listener = listeners.get(player);
        if(listener!=null) listener.invited(gameStorage.getGameId(game));
    }

    @Override
    public void uninvite(P player, GameCycle<P> game) {
        Set<GameCycle<P>> games = invitations.get(player);
        if(games==null) return;
        if(games.remove(game)){
            InviteListener<String> listener = listeners.get(player);
            if(listener!=null) listener.uninvited(gameStorage.getGameId(game));
        }
    }

    @Override
    public void removeInvitation(P player, GameCycle<P> game) {
        Set<GameCycle<P>> games = invitations.get(player);
        if(games!=null) games.remove(game);
    }

    @Override
    public void setInvitationListener(P player, InviteListener listener) {
        listeners.put(player,listener);
    }

    @Override
    public void confirm(P player, GameCycle<P> game) {
        if(!invitations.containsKey(player)) invitations.put(player,new HashSet<>());
        invitations.get(player).add(game);
        InviteListener<String> listener = listeners.get(player);
        if(listener!=null) listener.confirmed(gameStorage.getGameId(game));
    }
}
