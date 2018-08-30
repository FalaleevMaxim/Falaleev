package ru.minesweeper.service.invitation;

import org.springframework.stereotype.Service;
import ru.minesweeper.logic.AuthGame.GameCycle;
import ru.minesweeper.logic.AuthGame.Listeners.InviteListener;

import java.util.*;

/**
 * Реализация интерфейса {@link InvitationsService}
 * @param <P> Тип идентификатора
 */
@Service
public class InvitationServiceImpl<P,I> implements InvitationsService<P,I> {
    private Map<P,Set<GameCycle<P,I>>> invitations = new HashMap<>();
    private Map<P,InviteListener<I>> listeners = new HashMap<>();

    @Override
    public Collection<GameCycle<P,I>> getInvitations(P player) {
        if(invitations.get(player)==null) return new ArrayList<>();
        return new ArrayList<>(invitations.get(player));
    }

    @Override
    public int getInvitationsCount(P player) {
        if(invitations.get(player)==null) return 0;
        return invitations.get(player).size();
    }

    @Override
    public void invite(P player, GameCycle<P,I> game) {
        if(!invitations.containsKey(player)) invitations.put(player,new HashSet<>());
        invitations.get(player).add(game);
        InviteListener<I> listener = listeners.get(player);
        if(listener!=null) listener.invited(game.getId());
    }

    @Override
    public void uninvite(P player, GameCycle<P,I> game) {
        Set<GameCycle<P,I>> games = invitations.get(player);
        if(games==null) return;
        if(games.remove(game)){
            InviteListener<I> listener = listeners.get(player);
            if(listener!=null) listener.uninvited(game.getId());
        }
    }

    @Override
    public void removeInvitation(P player, GameCycle<P,I> game) {
        Set<GameCycle<P,I>> games = invitations.get(player);
        if(games!=null) games.remove(game);
    }

    @Override
    public void setInvitationListener(P player, InviteListener<I> listener) {
        listeners.put(player,listener);
    }

    @Override
    public void confirm(P player, GameCycle<P,I> game) {
        if(!invitations.containsKey(player)) invitations.put(player,new HashSet<>());
        invitations.get(player).add(game);
        InviteListener<I> listener = listeners.get(player);
        if(listener!=null) listener.confirmed(game.getId());
    }
}
