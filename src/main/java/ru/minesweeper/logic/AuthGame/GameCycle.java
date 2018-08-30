package ru.minesweeper.logic.AuthGame;

import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.logic.AuthGame.Listeners.GameEventsListener;
import ru.minesweeper.service.invitation.InvitationsService;

import java.util.Collection;
import java.util.Map;

/**
 * Управляет стадиями игры: Приглашением игроков, подключением и самой игрой.
 * @param <P> Тип идентификатора игрока.
 * @param <I> Тип идентификатора игры
 */
public class GameCycle<P, I> {
    /**
     * Перечисление стадий игры
     */
    public enum Stage{
        INVITATION,
        CONNECTIONS_GATHERING,
        GAME
    }

    private final I id;
    private Stage stage = Stage.INVITATION;
    private P owner;
    private GameInvitation<P,I> invitation;
    private ConnectionsGathering<P> connectionsGathering;
    private AuthGame<P> game;

    private GameProperties gameProperties;
    /**
     * @param id Идентификатор игры
     * @param owner Игрок, создавший игру, имеющий право приглашать и удалять игроков.
     * @param gameProperties Свойства игры
     * @param invitationsService Сервис отправки приглашений игрокам
     */
    public GameCycle(I id, P owner, GameProperties gameProperties, InvitationsService<P,I> invitationsService){
        this.id = id;
        invitation = new GameInvitation<>(this, owner,invitationsService);
        this.gameProperties = gameProperties;
        this.owner=owner;
    }

    public I getId() {
        return id;
    }

    /**
     * @return возвращает текущую стадию
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Переводит игру на следующую стадию
     * @return возвращает новую стадию
     */
    public Stage nextStage(){
        switch (stage){
            case INVITATION:
                Collection<P> players = invitation.complete();
                invitation = null;
                connectionsGathering = new ConnectionsGathering<>(owner,this,players);
                stage=Stage.CONNECTIONS_GATHERING;
                break;
            case CONNECTIONS_GATHERING:
                Map<P,GameEventsListener<P>> listeners = connectionsGathering.complete();
                if(listeners.size()>1){
                    game = new MultiplayerGame<>(gameProperties,listeners.keySet());
                }//ToDo: else singleplayer game
                stage=Stage.GAME;
                for (P player:listeners.keySet()) {
                    GameEventsListener<P> listener = listeners.get(player);
                    game.setListener(player,listener);
                    listener.onGameStarted();
                }
                break;
            default:
                break;
        }
        return stage;
    }

    /**
     * @return Возвращает владельца игры
     */
    public P getOwner(){return owner;}

    /**
     * @return Возвращает свойства игры
     */
    public GameProperties getGameProperties(){
        return gameProperties;
    }

    /**
     * @return На стадии приглашений (INVITATION) возвращает объект {@link GameInvitation}
     * @exception IllegalStateException Если стадия не INVITATION
     */
    public GameInvitation<P,I> getInvitations(){
        if(stage==Stage.INVITATION) return invitation;
        else throw new IllegalStateException("Stage is not invitation");
    }
    /**
     * @return На стадии сбора соединений (CONNECTIONS_GATHERING) возвращает объект {@link ConnectionsGathering}
     * @exception IllegalStateException Если стадия не CONNECTIONS_GATHERING
     */
    public ConnectionsGathering<P> getConnectionsGathering(){
        if(stage==Stage.CONNECTIONS_GATHERING) return connectionsGathering;
        else throw new IllegalStateException("Stage is not connections gathering");
    }

    /**
     * @return На стадии игры (GAME) возвращает объект {@link AuthGame}
     * @exception IllegalStateException Если стадия не GAME
     */
    public AuthGame<P> getGame(){
        if(stage==Stage.GAME) return game;
        else throw new IllegalStateException("Game has not started");
    }

    /**
     * @return возвращает коллекцию всех участников игры
     */
    public Collection<P> getPlayers(){
        switch (stage) {
            case INVITATION:
                return invitation.getConfirmedPlayers();
            case CONNECTIONS_GATHERING:
                return connectionsGathering.getPlayers();
            default:
                return game.getPlayers();
        }
    }

    /**
     * Игрок покидает игру. Добавлено для удобства, чтобы не проверять стадии игры вручную для удаления игроков
     * @param player идентификатор игрока
     */
    public void leave(P player){
        switch (stage){
            case INVITATION:
                invitation.leaveGame(player);
                break;
            case CONNECTIONS_GATHERING:
                connectionsGathering.removePlayer(player);
                break;
            case GAME:
                game.quit(player);
                break;
        }
    }
}