package ru.test.logic.AuthGame;

import ru.test.ViewModel.GameProperties;
import ru.test.model.GameEventsListener;

import java.util.Collection;
import java.util.Map;

public class GameCycle<P> {
    public GameCycle(P owner,GameProperties gameProperties){
        invitation = new GameInvitation<>(this, owner);
        this.gameProperties = gameProperties;
        this.owner=owner;
    }

    public enum Stage{
        INVITATION,
        CONNECTIONS_GATHERING,
        GAME
    }
    private Stage stage;
    public Stage getStage() {
        return stage;
    }
    public Stage nextStage(){
        switch (stage){
            case INVITATION:
                Collection<P> players = invitation.complete();
                if(players.size()>1){
                    game = new MultiplayerGame<>(gameProperties,players);
                }//ToDo: else singleplayer game
                invitation = null;
                connectionsGathering = new ConnectionsGathering<>(this,players);
                stage=Stage.CONNECTIONS_GATHERING;
                break;
            case CONNECTIONS_GATHERING:
                Map<P,GameEventsListener<P>> listeners = connectionsGathering.getListeners();
                for (P player:listeners.keySet()) {
                    GameEventsListener<P> listener = listeners.get(player);
                    if(listener !=null) game.setListener(player,listener);
                }
                stage=Stage.GAME;
                break;
            default:
                break;
        }
        return stage;
    }

    private P owner;
    private GameInvitation<P> invitation;
    private ConnectionsGathering<P> connectionsGathering;
    private AuthGame<P> game;
    private GameProperties gameProperties;

    public P getOwner(){return owner;}

    public GameProperties getGameProperties(){
        return gameProperties;
    }

    public GameInvitation<P> getInvitations(){
        if(stage==Stage.INVITATION) return invitation;
        else throw new IllegalStateException("Stage is not invitation");
    }

    public ConnectionsGathering<P> getConnectionsGathering(){
        if(stage==Stage.INVITATION) return connectionsGathering;
        else throw new IllegalStateException("Stage is not connections gathering");
    }

    public AuthGame<P> getGame(){
        if(stage==Stage.GAME) return game;
        else throw new IllegalStateException("Game has not started");
    }

    public Collection<P> getPlayers(){
        if(stage==Stage.INVITATION) return invitation.getConfirmedPlayers();
        else return game.getPlayers();
    }
}