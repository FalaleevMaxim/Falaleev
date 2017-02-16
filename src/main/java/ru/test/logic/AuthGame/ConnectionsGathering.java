package ru.test.logic.AuthGame;

import ru.test.logic.AuthGame.Exceptions.PlayerNotParticipatingException;
import ru.test.model.GameEventsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConnectionsGathering<P> {
    public ConnectionsGathering(GameCycle gameCycle,Collection<P> players) {
        this.gameCycle = gameCycle;
        for (P player:players) {
            listeners.put(player,null);
        }
    }
    private GameCycle gameCycle;
    private Map<P,GameEventsListener<P>> listeners = new HashMap<>();
    public void setConnection(P player, GameEventsListener<P> listener){
        if(getListeners().containsKey(player)) getListeners().put(player,listener);
        else throw new PlayerNotParticipatingException();
    }

    public Collection<P> getConnectedPlayers(){
        return getConnectedPlayers(true);
    }

    public Collection<P> getUnconnectedPlayers(){
        return getConnectedPlayers(false);
    }

    public void removePlayer(P player){
        getListeners().remove(player);
    }

    public void setListener(P player, GameEventsListener<P> listener){
        listeners.put(player,listener);
        boolean unsetRemaining = false;
        for (Map.Entry<P,GameEventsListener<P>> entry: getListeners().entrySet()) {
            if(entry.getValue()==null){
                unsetRemaining = true;
                break;
            }
        }
        if(!unsetRemaining) gameCycle.nextStage();
    }

    private Collection<P> getConnectedPlayers(boolean connected){
        Collection<P> players = new ArrayList<>();
        for (Map.Entry<P,GameEventsListener<P>> entry: getListeners().entrySet()) {
            if((entry.getValue()!=null)==connected) players.add(entry.getKey());
        }
        return players;
    }

    public Map<P, GameEventsListener<P>> getListeners() {
        return new HashMap<>(listeners);
    }
}