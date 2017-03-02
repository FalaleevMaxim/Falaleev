package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;
import ru.test.ViewModel.InvitationStatus;
import ru.test.logic.AuthGame.AuthGame;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.logic.AuthGame.GameInvitation;
import ru.test.logic.Board;
import ru.test.model.*;
import ru.test.service.InvitationsService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Objects;

@Controller
@RequestMapping("/AuthGame")
public class AuthGameController{
    private final AuthGameStorage<Integer,String> games;
    private final Storage<User> userStorage;
    private final InvitationsService<Integer> invitationService;

    @Autowired
    public AuthGameController(@Qualifier("AuthGames") AuthGameStorage<Integer, String> games, Storage<User> userStorage, InvitationsService<Integer> invitationService) {
        this.games = games;
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
        owner.setCurrentGameId(games.createGame(owner.getId(),gameProperties));
        userStorage.update(owner);
        return "redirect:/AuthGame/Game";
    }

    @RequestMapping("/Game")
    public String Game(HttpServletResponse response){
        User user = userStorage.findByName(
                (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
            );
        GameCycle<Integer> gameCycle = games.getGame(user.getCurrentGameId());
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
        GameCycle<Integer> gameCycle = games.getGame(id);
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
                modelAndView.setViewName("ConnectionsGathering");
                break;
            case GAME:
                AuthGame game = gameCycle.getGame();
                Board.Cell[][] field = game.getField();
                modelAndView.addObject("properties", game.getProperties());
                modelAndView.addObject("isFinished",game.isFinished());
                if(field==null) {
                    modelAndView.setViewName("NewGame");
                }else{
                    modelAndView.addObject("field",field);
                    modelAndView.setViewName("GameStarted");
                }
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
        GameCycle<Integer> game = games.getGame(user.getCurrentGameId());
        if(game.getStage()!= GameCycle.Stage.INVITATION || game.getOwner()!=user.getId()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        GameInvitation<Integer> invitations = game.getInvitations();
        invitations.invitePlayer(toInvite.getId());
        return new InvitationStatus(toInvite.getId(),toInvite.getUserName(),
                invitations.isPlayerConfirmed(toInvite.getId()),invitations.isOwnerConfirmed(toInvite.getId()));
    }

    @RequestMapping("/InvitationListener")
    public SseEmitter getInvitationsListener(){
        User user = userStorage.findByName((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        SseEmitter emitter = new SseEmitter(-1L);
        invitationService.setInvitationListener(user.getId(),new SseInviteListener<Integer>(emitter));
        return emitter;
    }

    @RequestMapping("/JoinListener")
    public SseEmitter getJoinListener(HttpServletResponse response){
        User user = userStorage.findByName((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(user.getCurrentGameId()==null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        GameCycle<Integer> game = games.getGame(user.getCurrentGameId());
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
        GameCycle<Integer> gameCycle = games.getGame(user.getCurrentGameId());
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

    @RequestMapping("/join/{id}")
    public String joinGame(@PathVariable String id, HttpServletResponse response){
        GameCycle<Integer> game = games.getGame(id);
        if(game==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(game.getStage()!= GameCycle.Stage.INVITATION){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        game.getInvitations().joinGame(user.getId());
        if(game.getInvitations().isOwnerConfirmed(user.getId())){
            user.setCurrentGameId(id);
            userStorage.update(user);
        }
        return "redirect:/AuthGame/Game/"+id;
    }


    @RequestMapping(value = "/OpenCell", method = RequestMethod.POST)
    public @ResponseBody CellVM[] openCell(@ModelAttribute CellVM cell){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        CellVM[] cells = games.getGame(user.getCurrentGameId()).getGame().openCell(cell.getX(), cell.getY(), user.getId());
        return cells;
    }

    @RequestMapping(value = "/SuggestBomb", method = RequestMethod.POST)
    public @ResponseBody boolean suggestBomb(@ModelAttribute CellVM cell){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return games.getGame(user.getCurrentGameId()).getGame().suggestBomb(cell.getX(), cell.getY(), user.getId());
    }

    @RequestMapping("/Score")
    public @ResponseBody int checkScore(){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return games.getGame(user.getCurrentGameId()).getGame().getScore(user.getId());
    }
}
