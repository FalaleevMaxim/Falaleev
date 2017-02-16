package ru.test.logic.AuthGame;

import org.springframework.beans.factory.annotation.Autowired;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.model.JoinListener;
import ru.test.service.InvitationsService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameInvitation<P> {
    public GameInvitation(GameCycle<P> gameCycle, P owner) {
        players.put(owner,new PlayerParticipation(owner,true,true));
        this.gameCycle = gameCycle;
        this.owner = owner;
        this.invitationsService = invitationsService;
    }

    @Autowired
    private InvitationsService<P> invitationsService;

    private GameCycle<P> gameCycle;
    private P owner;
    private Map<P,PlayerParticipation> players = new ConcurrentHashMap<>();
    private JoinListener<P> joinListener;
    private boolean complete = false;

    public P getOwner(){
        return owner;
    }

    //Вледелец игры приглашает игрока или подтверждает запрос игрока на присоединение к игре
    public void invitePlayer(P player){
        if(players.containsKey(player) && players.get(player).playerConfirmed){
            players.get(player).ownerConfirmed=true;
            invitationsService.confirm(player,gameCycle);
        }else{
            players.put(player,new PlayerParticipation(player,true,false));
            invitationsService.invite(player,gameCycle);
        }
    }

    //Игрок запрашивает присоединение к игре или подтверждает приглашение
    public void addPlayer(P player) {
        if(players.containsKey(player) && players.get(player).ownerConfirmed){
            players.get(player).playerConfirmed=true;
            joinListener.joinRequest(player);
        }else{
            players.put(player,new PlayerParticipation(player,true,false));
            joinListener.inviteAccepted(player);
        }
    }

    public Collection<P> getConfirmedPlayers(){
        ArrayList<P> confirmedPlayers = new ArrayList<>();
        for (PlayerParticipation pp:players.values()) {
            if(pp.ownerConfirmed && pp.playerConfirmed) confirmedPlayers.add(pp.player);
        }
        return confirmedPlayers;
    }

    public Collection<P> getUnconfirmedPlayers(){
        ArrayList<P> unconfirmedPlayers = new ArrayList<>();
        for (PlayerParticipation pp:players.values()) {
            if(!pp.ownerConfirmed || !pp.playerConfirmed) unconfirmedPlayers.add(pp.player);
        }
        return unconfirmedPlayers;
    }

    public Collection<P> complete(){
        ArrayList<P> confirmedPlayers = new ArrayList<>();
        Iterator<P> it = players.keySet().iterator();
        while (it.hasNext()){
            PlayerParticipation pp = players.get(it.next());
            if(pp.ownerConfirmed && pp.playerConfirmed) confirmedPlayers.add(pp.player);
            else{
                invitationsService.uninvite(pp.player,gameCycle);
                it.remove();
            }
        }
        return confirmedPlayers;
    }

    private class PlayerParticipation{
        PlayerParticipation(P player, boolean ownerConfirmed, boolean playerConfirmed) {
            this.player = player;
            this.ownerConfirmed = ownerConfirmed;
            this.playerConfirmed = playerConfirmed;
        }
        //Идентификатор игрока
        P player;
        //Подтвердил ли создатель игры участие игрока
        boolean ownerConfirmed;
        //Подтвердил ли игрок своё участие в этой игре
        boolean playerConfirmed;
    }
}
