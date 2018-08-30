package ru.minesweeper.logic.AuthGame;

import ru.minesweeper.logic.AuthGame.Listeners.BeforeGameListener;
import ru.minesweeper.logic.AuthGame.Listeners.JoinListener;
import ru.minesweeper.service.invitation.InvitationsService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс для набора игроков в игру.
 * @param <P> Тип идентификатора игрока
 */
public class GameInvitation<P,I> {
    /**
     * @param gameCycle Объект {@link GameCycle}, которому принадлежит данный объект
     * @param owner Владелец игры. Изначально приглашён и подтверждён.
     * @param invitationsService Сервис для отправки приглашений игрокам.
     */
    public GameInvitation(GameCycle<P,I> gameCycle, P owner, InvitationsService<P,I> invitationsService) {
        players.put(owner,new PlayerParticipation(owner,true,true));
        this.gameCycle = gameCycle;
        this.owner = owner;
        this.invitationsService = invitationsService;
    }

    private final InvitationsService<P,I> invitationsService;
    private final GameCycle<P,I> gameCycle;
    private final P owner;
    private final Map<P,PlayerParticipation> players = new ConcurrentHashMap<>();
    private JoinListener<P> joinListener;
    private boolean completed = false;

    /**
     * @return Возвращает владельца игры
     */
    public P getOwner(){
        return owner;
    }

    /**
     * Вледелец игры приглашает игрока или подтверждает запрос игрока на присоединение к игре
     * @exception IllegalStateException Когда приглашение завершено
     */
    public void invitePlayer(P player){
        if(completed) throw new IllegalStateException("Invitation completed");
        if(Objects.equals(player,owner))throw new IllegalArgumentException("You can not invite yourself");
        if(players.containsKey(player)){
            if(players.get(player).isPlayerConfirmed()){
                players.get(player).setOwnerConfirmed(true);
                invitationsService.confirm(player,gameCycle);
                emitEvent(listener -> listener.playerJoined(player));
            }
        }else{
            players.put(player,new PlayerParticipation(player,true,false));
            invitationsService.invite(player,gameCycle);
        }
    }

    /**
     * Игрок запрашивает присоединение к игре или подтверждает приглашение
     * @exception IllegalStateException Когда приглашение завершено
     */
    public void joinGame(P player) {
        if(completed) throw new IllegalStateException("Invitation completed");
        if(Objects.equals(player,owner))throw new IllegalArgumentException("You can not invite yourself");
        if(players.containsKey(player)){
            if(players.get(player).isOwnerConfirmed()){
                players.get(player).setPlayerConfirmed(true);
                invitationsService.removeInvitation(player,gameCycle);
                if(joinListener!=null) joinListener.inviteAccepted(player);
                emitEvent(listener -> listener.playerJoined(player));
            }
        }else{
            players.put(player,new PlayerParticipation(player,false,true));
            joinListener.joinRequest(player);
        }
    }

    /**
     * Игрок отказывается от участия в игре.
     * Владелец игры не может выйти.
     * @param player идентификатор игрока
     * @exception IllegalStateException Если стадия приглашений завершена или если владелец пытается покинуть игру.
     */
    public void leaveGame(P player){
        if(completed) throw new IllegalStateException("Invitation completed");
        if(Objects.equals(player,owner))throw new IllegalArgumentException("Owner can not leave game.");
        if(players.containsKey(player)){
            players.get(player).setPlayerConfirmed(false);
            joinListener.inviteRejected(player);
            emitEvent(listener -> listener.playerLeft(player));
        }
    }

    /**
     * @return Возвращает коллекцию игроков, участие которых подтверждено и владельцем игры, и самим игроком
     */
    public Collection<P> getConfirmedPlayers(){
        ArrayList<P> confirmedPlayers = new ArrayList<>();
        for (PlayerParticipation pp:players.values()) {
            if(pp.isOwnerConfirmed() && pp.isPlayerConfirmed()) confirmedPlayers.add(pp.getPlayer());
        }
        return confirmedPlayers;
    }

    /**
     * @return Возвращает игроков, участие которых не подтвердил либо владелец игры, либо сам игрок
     */
    public Collection<P> getUnconfirmedPlayers(){
        ArrayList<P> unconfirmedPlayers = new ArrayList<>();
        for (PlayerParticipation pp:players.values()) {
            if(!pp.isOwnerConfirmed() || !pp.isPlayerConfirmed()) unconfirmedPlayers.add(pp.getPlayer());
        }
        return unconfirmedPlayers;
    }

    /**
     * @return Возвращает всех игроков, приглашённых или запросивших участие в игре
     */
    public Collection<P> getPlayers(){
        return players.keySet();
    }

    /**
     * Проверяет, подтверждено ли участие игрока самим игроком
     */
    public boolean isPlayerConfirmed(P player){
        if(!players.containsKey(player)) return false;
        return players.get(player).isPlayerConfirmed();
    }

    /**
     * Проверяет, подтверждено ли участие игрока владельцем игры
     */
    public boolean isOwnerConfirmed(P player){
        if(!players.containsKey(player)) return false;
        return players.get(player).isOwnerConfirmed();
    }

    /**
     * Завершает стадию приглашений. Приглашение или принятие игроков становится недоступным. Удаляет всех неподтверждённых игроков.
     * @return Возвращает коллекцию подтверждённых игроков
     */
    public Collection<P> complete(){
        if(completed) throw new IllegalStateException("Invitation already completed");
        completed = true;
        ArrayList<P> confirmedPlayers = new ArrayList<>();
        Iterator<P> it = players.keySet().iterator();
        while (it.hasNext()){
            PlayerParticipation pp = players.get(it.next());
            if(pp.isOwnerConfirmed() && pp.isPlayerConfirmed()) confirmedPlayers.add(pp.getPlayer());
            else{
                invitationsService.uninvite(pp.getPlayer(),gameCycle);
                it.remove();
            }
        }
        emitEvent(BeforeGameListener::gameStarted);
        return confirmedPlayers;
    }

    /**
     * Завершить приглашение игроков и разослать событие удаления игры.
     */
    public void gameDismissed(){
        if(completed) throw new IllegalStateException("Invitation already completed");
        completed = true;
        emitEvent(BeforeGameListener::gameDismissed);
        players.clear();
    }

    /**
     * Устанавливает обработчик событий запросов на участие в игре или принятия приглашений для владнльца игры
     */
    public void setJoinListener(JoinListener<P> joinListener) {
        this.joinListener = joinListener;
    }

    /**
     * Устанавливает для игрока обработчик событий начала или удаления игры, подключения игроков и изменений свойств игры
     */
    public void setListener(P player, BeforeGameListener<P> listener){
        players.get(player).setListener(listener);
    }
    /**
     * Рассылка игрокам события
     */
    private void emitEvent(BeforeGameEvent<P> event){
        for (PlayerParticipation player :players.values()) {
            if(player.getListener()!=null) event.emit(player.getListener());
        }
    }

    /**
     * Внутренний класс для хранения игроков и их статусов подтверждения
     */
    private class PlayerParticipation{
        PlayerParticipation(P player, boolean ownerConfirmed, boolean playerConfirmed) {
            this.setPlayer(player);
            this.setOwnerConfirmed(ownerConfirmed);
            this.setPlayerConfirmed(playerConfirmed);
        }
        /**
         * Идентификатор игрока
         */
        private P player;
        /**
         * Подтвердил ли создатель игры участие игрока
         */
        private boolean ownerConfirmed;
        /**
         * Подтвердил ли игрок своё участие в игре
         */
        private boolean playerConfirmed;
        /**
         * Обработчик событий начала или удаления игры, подключения или ухода игроков, изменения свойств игры
         */
        private BeforeGameListener<P> listener;

        public P getPlayer() {
            return player;
        }
        public void setPlayer(P player) {
            this.player = player;
        }
        boolean isOwnerConfirmed() {
            return ownerConfirmed;
        }
        void setOwnerConfirmed(boolean ownerConfirmed) {
            this.ownerConfirmed = ownerConfirmed;
        }
        boolean isPlayerConfirmed() {
            return playerConfirmed;
        }
        void setPlayerConfirmed(boolean playerConfirmed) {
            this.playerConfirmed = playerConfirmed;
        }

        public BeforeGameListener<P> getListener() {
            return listener;
        }

        public void setListener(BeforeGameListener<P> listener) {
            this.listener = listener;
        }
    }
}

/**
 * Функциональный интерфейс для рассылки игрокам события
 * @param <P> тип идентификатора игрока
 */
interface BeforeGameEvent<P>{
    void emit(BeforeGameListener<P> listener);
}