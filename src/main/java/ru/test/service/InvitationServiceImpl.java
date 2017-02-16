package ru.test.service;

import org.springframework.stereotype.Service;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.model.InviteListener;

import java.util.*;

@Service
public class InvitationServiceImpl<P> implements InvitationsService<P> {
    private Map<P,Set<GameCycle<P>>> invitations = new HashMap<>();
    private Map<P,InviteListener> listeners = new HashMap<>();

    @Override
    public Collection<GameCycle<P>> getInvitations(P player) {
        return new ArrayList<>(invitations.get(player));
    }

    @Override
    public void invite(P player, GameCycle<P> game) {
        if(!invitations.containsKey(player)) invitations.put(player,new HashSet<>());
        invitations.get(player).add(game);
        InviteListener listener = listeners.get(player);
        if(listener!=null) listener.invited(game);
    }

    @Override
    public void uninvite(P player, GameCycle<P> game) {
        Set<GameCycle<P>> games = invitations.get(player);
        if(games==null) return;
        if(games.remove(game)){
            InviteListener listener = listeners.get(player);
            if(listener!=null) listener.uninvited(game);
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
        InviteListener listener = listeners.get(player);
        if(listener!=null) listener.confirmed(game);
    }
}
