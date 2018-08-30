package ru.minesweeper.logic.AuthGame;

import ru.minesweeper.logic.AuthGame.Exceptions.PlayerNotParticipatingException;
import ru.minesweeper.logic.AuthGame.Listeners.GameEventsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для сбора подключений к игрею
 * @param <P> Тип идентификатора игрока
 */
public class ConnectionsGathering<P> {
    /**
     * @param gameCycle игра, которой принадлежит данны объект
     * @param players коллекция игроков игроков
     * @param owner Владелец игры. Не может быть удалён. Должен быть обязательно подключён, чтобы начать игру.
     */
    public ConnectionsGathering(P owner, GameCycle gameCycle,Collection<P> players) {
        this.owner = owner;
        this.gameCycle = gameCycle;
        for (P player:players) {
            gameListeners.put(player,null);
        }
    }
    private final P owner;
    private final GameCycle gameCycle;
    private final Map<P,GameEventsListener<P>> gameListeners = new HashMap<>();
    private boolean completed = false;

    /**
     * @return возвращает коллекцию всех игроков
     */
    public Collection<P> getPlayers(){
        return gameListeners.keySet();
    }

    /**
     * @return возвращает коллекцию подключенных игроков
     */
    public Collection<P> getConnectedPlayers(){
        return getPlayers(true);
    }

    /**
     * @return возвращает коллекцию неподключённых игроков
     */
    public Collection<P> getUnconnectedPlayers(){
        return getPlayers(false);
    }

    /**
     * Проверяет, подключился ли игрок
     */
    public boolean isConnected(P player){
        return gameListeners.get(player)!=null;
    }

    /**
     * Удаляет игрока
     * @exception IllegalStateException Если стадия подключений завершена
     * @exception IllegalArgumentException При попытке удалить владельца игры
     */
    public void removePlayer(P player){
        if(completed) throw new IllegalStateException("Connections gathering completed");
        if(player==owner) throw new IllegalArgumentException("Can not remove owner");
        getListeners().remove(player);
    }

    /**
     * Устанавливает подключение для игрока
     * @param player идентификатор игрока
     * @param listener подписка на события игры
     * @exception IllegalStateException Если стадия подключений завершена
     */
    public void setConnection(P player, GameEventsListener<P> listener){
        if(completed) throw new IllegalStateException("Connections gathering completed");
        if(!getListeners().containsKey(player)) throw new PlayerNotParticipatingException();
        gameListeners.put(player,listener);
        for (Map.Entry<P,GameEventsListener<P>> entry:gameListeners.entrySet()) {
            if(entry.getValue()!=null && !entry.getKey().equals(player)) entry.getValue().onScoreChange(player,1);
        }
        boolean unsetRemaining = false;
        for (Map.Entry<P,GameEventsListener<P>> entry: getListeners().entrySet()) {
            if(entry.getValue()==null){
                unsetRemaining = true;
                break;
            }
        }
        if(!unsetRemaining) gameCycle.nextStage();
    }

    /**
     * Возвращает список подключённых или неподключённых игроков, в зависимости от параметра connected
     * @param connected если true, вернёт подключённых игроков, если false, неподключённых
     */
    private Collection<P> getPlayers(boolean connected){
        Collection<P> players = new ArrayList<>();
        for (Map.Entry<P,GameEventsListener<P>> entry: getListeners().entrySet()) {
            if((entry.getValue()!=null)) players.add(entry.getKey());
        }
        return players;
    }
    public Map<P, GameEventsListener<P>> getListeners() {
        return new HashMap<>(gameListeners);
    }

    /**
     * Завершает этап сбора подключений.
     * Подключение или удаление игроков становится невозможно
     * Удаляет всех неподключённых игроков
     * @return Возвращает Map оставшихся (подключённых) игроков и их подключения
     * @exception IllegalStateException Если владелец игры не подключён
     */
    public Map<P, GameEventsListener<P>> complete(){
        if(gameListeners.get(owner)==null) throw new IllegalStateException("Owner not connected");
        completed = true;
        gameListeners.keySet().removeIf(p -> gameListeners.get(p) == null);
        return getListeners();
    }
}