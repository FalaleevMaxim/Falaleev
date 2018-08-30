package ru.minesweeper.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.minesweeper.logic.AuthGame.GameCycle;
import ru.minesweeper.storage.JpaUserStorage;
import ru.minesweeper.storage.game.AuthGameStorage;
import ru.minesweeper.storage.Storage;
import ru.minesweeper.model.User;
import ru.minesweeper.service.invitation.InvitationsService;

@Controller
@RequestMapping(value = "/main")
public class MainController {
    private final JpaUserStorage userStorage;
    private final AuthGameStorage<Integer,String> gameStorage;
    private final InvitationsService<Integer, String> invitationsService;

    @Autowired
    public MainController(JpaUserStorage userStorage, AuthGameStorage<Integer, String> gameStorage, InvitationsService<Integer, String> invitationsService) {
        this.userStorage = userStorage;
        this.gameStorage = gameStorage;
        this.invitationsService = invitationsService;
    }

    @RequestMapping(value = "/header")
    public String header(Model model){
        if(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken){
            return "UnauthHeader";
        }else{
            User user = userStorage.findUserByUserName((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            model.addAttribute("userId",user.getId());
            model.addAttribute("UserName",user.getUserName());
            model.addAttribute("RealName",user.getRealName());
            boolean beforeGame = false,inGame=false;
            if(user.getCurrentGameId()!=null){
                GameCycle<Integer, String> game = gameStorage.getGame(user.getCurrentGameId());
                if(game ==null){
                    user.setCurrentGameId(null);
                    userStorage.save(user);
                }else{
                    inGame = true;
                    if(game.getStage()== GameCycle.Stage.INVITATION){
                        beforeGame = true;
                    }
                }
            }
            model.addAttribute("inGame",inGame);
            model.addAttribute("beforeGame",beforeGame);
            model.addAttribute("invitations",invitationsService.getInvitationsCount(user.getId()));
            return "AuthHeader";
        }
    }
}