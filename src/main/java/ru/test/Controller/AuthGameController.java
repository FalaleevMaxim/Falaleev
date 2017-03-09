package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.test.ViewModel.*;
import ru.test.logic.AuthGame.AuthGame;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.logic.AuthGame.GameInvitation;
import ru.test.logic.AuthGame.Listeners.Sse.SseBeforeGameListener;
import ru.test.logic.AuthGame.Listeners.Sse.SseGameEventsListener;
import ru.test.logic.AuthGame.Listeners.Sse.SseInviteListener;
import ru.test.logic.AuthGame.Listeners.Sse.SseJoinListener;
import ru.test.logic.Board;
import ru.test.model.*;
import ru.test.service.InvitationsService;
import ru.test.storage.AuthGameStorage;
import ru.test.storage.Storage;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Controller
@RequestMapping("/AuthGame")
public class AuthGameController{
    private final AuthGameStorage<Integer,String> gameStorage;
    private final Storage<User> userStorage;
    private final InvitationsService<Integer> invitationService;

    @Autowired
    public AuthGameController(@Qualifier("AuthGames") AuthGameStorage<Integer, String> gameStorage, Storage<User> userStorage, InvitationsService<Integer> invitationService) {
        this.gameStorage = gameStorage;
        this.userStorage = userStorage;
        this.invitationService = invitationService;
    }

    @RequestMapping(value = "/GameStart",method = RequestMethod.GET)
    public String GameStart(Model model){
        model.addAttribute("gameType","AuthGame");
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart",method = RequestMethod.POST)
    public String GameStart(@ModelAttribute GameProperties gameProperties){
        User owner = userStorage.findByName(
                        (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                );
        //ToDo: удалить старую игру
        owner.setCurrentGameId(gameStorage.createGame(owner.getId(),gameProperties));
        userStorage.update(owner);
        return "redirect:/AuthGame/Game";
    }

    @RequestMapping("/Game")
    public String Game(HttpServletResponse response){
        User user = userStorage.findByName(
                (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
            );
        GameCycle<Integer> gameCycle = gameStorage.getGame(user.getCurrentGameId());
        if(gameCycle==null){
            return "redirect:/AuthGame/GameStart";
        }
        return "redirect:/AuthGame/Game/"+user.getCurrentGameId();


    }

    @RequestMapping("/Game/{id}")
    public ModelAndView Game(@PathVariable String id,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView();
        User user = userStorage.findByName(
                (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );
        GameCycle<Integer> gameCycle = gameStorage.getGame(id);
        if(gameCycle==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(!gameCycle.getPlayers().contains(user.getId()) && gameCycle.getStage()!=GameCycle.Stage.INVITATION){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        switch (gameCycle.getStage()){
            case INVITATION:
                modelAndView.setViewName("Invitation");
                modelAndView.addObject("properties",gameCycle.getGameProperties());
                modelAndView.addObject("gameId",id);
                modelAndView.addObject("isOwner",gameCycle.getOwner().equals(user.getId()));
                modelAndView.addObject("owner",gameCycle.getOwner());
                GameInvitation<Integer> invitation = gameCycle.getInvitations();
                modelAndView.addObject("isOwnerConfirmed", invitation.isOwnerConfirmed(user.getId()));
                modelAndView.addObject("isPlayerConfirmed", invitation.isPlayerConfirmed(user.getId()));
                ArrayList<InvitationStatus> players = new ArrayList<>();
                for (Integer playerId : invitation.getPlayers()) {
                    players.add(new InvitationStatus(
                            playerId,userStorage.findById(playerId).getUserName(),
                            invitation.isPlayerConfirmed(playerId),invitation.isOwnerConfirmed(playerId)));
                }
                modelAndView.addObject("players",players);
                break;
            case CONNECTIONS_GATHERING:
                modelAndView.addObject("properties",gameCycle.getGameProperties());
                modelAndView.addObject("userId",user.getId());
                ArrayList<PlayerConnected> connections = new ArrayList<>();
                for(Integer playerId:gameCycle.getPlayers()){
                    User player = userStorage.findById(playerId);
                    if(player==null) continue;
                    connections.add(new PlayerConnected(playerId,player.getUserName(),gameCycle.getConnectionsGathering().isConnected(playerId)));
                }
                modelAndView.addObject("connections",connections);
                modelAndView.setViewName("ConnectionsGathering");
                break;
            case GAME:
                AuthGame<Integer> game = gameCycle.getGame();
                Board.Cell[][] field = game.getField();
                modelAndView.addObject("userId",user.getId());
                modelAndView.addObject("field",field);
                modelAndView.addObject("bombsLeft",game.getBombsLeft());
                modelAndView.addObject("properties", game.getProperties());
                modelAndView.addObject("isFinished",game.isFinished());
                modelAndView.addObject("isStarted",game.isStarted());
                ArrayList<UserVM> players1 = new ArrayList<>();
                for (ScoreModel<Integer> score:game.getScores()) {
                    User player = userStorage.findById(score.getPlayer());
                    players1.add(new UserVM(player.getId(),player.getUserName(),player.getRealName()));
                }
                modelAndView.addObject("players1",players1);
                modelAndView.addObject("scores",new ArrayList<>(game.getScores()));
                modelAndView.addObject("winner",game.getWinners());
                modelAndView.setViewName("AuthGame");
                break;
            default:
                throw new IllegalStateException("Unsupported game stage");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/invite/{player}", method = RequestMethod.GET)
    public @ResponseBody InvitationStatus invite(@PathVariable String player,HttpServletResponse response){
        User toInvite = userStorage.findByName(player);
        if(toInvite==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> game = gameStorage.getGame(user.getCurrentGameId());
        if(game.getStage()!= GameCycle.Stage.INVITATION || game.getOwner()!=user.getId()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        GameInvitation<Integer> invitations = game.getInvitations();
        invitations.invitePlayer(toInvite.getId());
        return new InvitationStatus(toInvite.getId(),toInvite.getUserName(),
                invitations.isPlayerConfirmed(toInvite.getId()),invitations.isOwnerConfirmed(toInvite.getId()));
    }

    @RequestMapping("/join/{id}")
    public String joinGame(@PathVariable String id, HttpServletResponse response){
        GameCycle<Integer> game = gameStorage.getGame(id);
        if(game==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(game.getStage()!= GameCycle.Stage.INVITATION){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(user.getCurrentGameId()!=null){
            gameStorage.getGame(user.getCurrentGameId()).leave(user.getId());
        }
        game.getInvitations().joinGame(user.getId());
        user.setCurrentGameId(id);
        userStorage.update(user);
        return "redirect:/AuthGame/Game/"+id;
    }

    @RequestMapping("/Invitations")
    public ModelAndView invitations(){
        ModelAndView modelAndView = new ModelAndView("InvitationsList");
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ArrayList<String> invitationIds = new ArrayList<>(invitationService.getInvitationsCount(user.getId()));
        ArrayList<GameProperties> invitationGames = new ArrayList<>(invitationService.getInvitationsCount(user.getId()));
        ArrayList<UserVM> gamesOwners = new ArrayList<>(invitationService.getInvitationsCount(user.getId()));
        for (GameCycle<Integer> game:invitationService.getInvitations(user.getId())) {
            invitationIds.add(gameStorage.getGameId(game));
            invitationGames.add(game.getGameProperties());
            User owner = userStorage.findById(game.getOwner());
            gamesOwners.add(new UserVM(owner.getId(),owner.getUserName(),owner.getRealName()));
        }
        modelAndView.addObject("invitationIds",invitationIds);
        modelAndView.addObject("gamesOwners",gamesOwners);
        modelAndView.addObject("invitationGames",invitationGames);
        return modelAndView;
    }

    @RequestMapping("/InvitationListener")
    public SseEmitter invitationsListener(){
        User user = userStorage.findByName((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        SseEmitter emitter = new SseEmitter(-1L);
        invitationService.setInvitationListener(user.getId(),new SseInviteListener<Integer>(emitter));
        return emitter;
    }

    @RequestMapping("/JoinListener")
    public SseEmitter joinListener(HttpServletResponse response){
        User user = userStorage.findByName((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(user.getCurrentGameId()==null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        GameCycle<Integer> game = gameStorage.getGame(user.getCurrentGameId());
        if(game==null){
            user.setCurrentGameId(null);
            userStorage.update(user);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if(!Objects.equals(game.getOwner(),user.getId())){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        if(game.getStage()!= GameCycle.Stage.INVITATION){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        SseEmitter emitter = new SseEmitter(-1L);
        game.getInvitations().setJoinListener(new SseJoinListener(emitter,userStorage));
        return emitter;
    }

    @RequestMapping(value = "/CompleteInvitation",method = RequestMethod.GET)
    public String completeInvitation(HttpServletResponse response){
        User user = userStorage.findByName(
                (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );
        GameCycle<Integer> gameCycle = gameStorage.getGame(user.getCurrentGameId());
        if(gameCycle.getStage()== GameCycle.Stage.INVITATION){
            if(gameCycle.getInvitations().getOwner().equals(user.getId())){
                gameCycle.nextStage();
                return "redirect:/AuthGame/Game";
            }else{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return null;
            }
        }else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    @RequestMapping(value = "/BeforeGameListener")
    public  SseEmitter beforeGameListener(HttpServletResponse response){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> gameCycle = gameStorage.getGame(user.getCurrentGameId());
        if(gameCycle==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(gameCycle.getStage()!= GameCycle.Stage.INVITATION){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        SseEmitter emitter = new SseEmitter(-1L);
        gameCycle.getInvitations().setListener(user.getId(),new SseBeforeGameListener(emitter,userStorage,gameCycle,user.getCurrentGameId()));
        return emitter;
    }

    @RequestMapping(value = "/ConnectedPlayers")
    public @ResponseBody Collection<PlayerConnected> getPlayersConnected(HttpServletResponse response){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> gameCycle = gameStorage.getGame(user.getCurrentGameId());
        if(gameCycle==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(gameCycle.getStage()!= GameCycle.Stage.CONNECTIONS_GATHERING){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if(!gameCycle.getPlayers().contains(user.getId())){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        ArrayList<PlayerConnected> connections = new ArrayList<>();
        for(Integer playerId:gameCycle.getPlayers()){
            User player = userStorage.findById(playerId);
            if(player==null) continue;
            connections.add(new PlayerConnected(playerId,player.getUserName(),gameCycle.getConnectionsGathering().isConnected(playerId)));
        }
        return connections;
    }

    @RequestMapping(value = "/GameListener")
    public SseEmitter gameListener(HttpServletResponse response){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> gameCycle = gameStorage.getGame(user.getCurrentGameId());
        if(gameCycle==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(gameCycle.getStage()== GameCycle.Stage.INVITATION){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if(!gameCycle.getPlayers().contains(user.getId())){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        SseEmitter emitter = new SseEmitter(-1L);
        if(gameCycle.getStage()== GameCycle.Stage.CONNECTIONS_GATHERING){
            gameCycle.getConnectionsGathering().setConnection(user.getId(),new SseGameEventsListener<>(emitter));
        }else{
            gameCycle.getGame().setListener(user.getId(),new SseGameEventsListener<>(emitter));
        }
        return emitter;
    }

    @RequestMapping(value = "/OpenCell", method = RequestMethod.POST)
    public void openCell(@ModelAttribute CellVM cell, HttpServletResponse response){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> gameCycle = gameStorage.getGame(user.getCurrentGameId());
        if(gameCycle==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if(gameCycle.getStage()!= GameCycle.Stage.GAME){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(gameCycle.getGame().isFinished()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        gameStorage.getGame(user.getCurrentGameId()).getGame().openCell(cell.getX(), cell.getY(), user.getId());
    }

    @RequestMapping(value = "/SuggestBomb", method = RequestMethod.POST)
    public void suggestBomb(@ModelAttribute CellVM cell,HttpServletResponse response){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> gameCycle = gameStorage.getGame(user.getCurrentGameId());
        if(gameCycle==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if(gameCycle.getStage()!= GameCycle.Stage.GAME){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(gameCycle.getGame().isFinished()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        gameStorage.getGame(user.getCurrentGameId()).getGame().suggestBomb(cell.getX(), cell.getY(), user.getId());
    }

    @RequestMapping("/GetScores")
    public @ResponseBody Collection<ScoreModel<Integer>> getScores(HttpServletResponse response){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> game = gameStorage.getGame(user.getCurrentGameId());
        if(game==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(game.getStage()!= GameCycle.Stage.GAME){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return gameStorage.getGame(user.getCurrentGameId()).getGame().getScores();
    }

    @RequestMapping("/Score")
    public @ResponseBody Integer checkScore(HttpServletResponse response){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        GameCycle<Integer> game = gameStorage.getGame(user.getCurrentGameId());
        if(game==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(game.getStage()!= GameCycle.Stage.GAME){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return gameStorage.getGame(user.getCurrentGameId()).getGame().getScore(user.getId());
    }

    @RequestMapping("/Score/{playerId}")
    public @ResponseBody Integer checkScore(@PathVariable Integer playerId, HttpServletResponse response){
        User player = userStorage.findById(playerId);
        if(player==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(player.getCurrentGameId()==null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(!Objects.equals(player.getCurrentGameId(), user.getCurrentGameId())){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        GameCycle<Integer> game = gameStorage.getGame(player.getCurrentGameId());
        if(game==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(game.getStage()!= GameCycle.Stage.GAME){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return game.getGame().getScore(playerId);
    }


}