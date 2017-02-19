package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.AuthGame.AuthGame;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.logic.Board;
import ru.test.model.AuthGameStorage;
import ru.test.model.Storage;
import ru.test.model.User;

@Controller
@RequestMapping("/SinglePlayerGame")
public class AuthGameController{
    @Autowired
    @Qualifier("AuthGames")
    private AuthGameStorage<Integer,String> games;
    @Autowired
    private Storage<User> userStorage;

    @RequestMapping(value = "/GameStart",method = RequestMethod.GET)
    public String GameStart(Model model){
        model.addAttribute("gameType","SinglePlayerGame");
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart",method = RequestMethod.POST)
    public String GameStart(@ModelAttribute GameProperties gameProperties){
        User owner = userStorage.findByName(
                        (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                );
        games.removeGame(owner.getCurrentGameId());
        owner.setCurrentGameId(games.createGame(owner.getId(),gameProperties));
        userStorage.update(owner);
        return "redirect:Game";
    }

    @RequestMapping("/Game")
    public ModelAndView Game(){
        ModelAndView modelAndView = new ModelAndView();
        User user = userStorage.findByName(
                (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
            );
        modelAndView.addObject("id",user.getId());
        GameCycle gameCycle = games.getGame(user.getCurrentGameId());
        if(gameCycle==null){
            modelAndView.setViewName("GameStartForm");
            return modelAndView;
        }
        switch (gameCycle.getStage()){
            case INVITATION:
                break;
            case CONNECTIONS_GATHERING:
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

    @RequestMapping(value = "/OpenCell", method = RequestMethod.POST)
    public @ResponseBody CellVM[] opencell(@ModelAttribute CellVM cell){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        CellVM[] cells = games.getGame(user.getCurrentGameId()).getGame().openCell(cell.getX(), cell.getY(), null);
        return cells;
    }

    @RequestMapping(value = "/SuggestBomb", method = RequestMethod.POST)
    public @ResponseBody boolean suggestBomb(@ModelAttribute CellVM cell){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        boolean isBomb = games.getGame(user.getCurrentGameId()).getGame().suggestBomb(cell.getX(), cell.getY(), null);
        return isBomb;
    }

    @RequestMapping("/Score")
    public @ResponseBody int checkScore(){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return games.getGame(user.getCurrentGameId()).getGame().getScore(null);
    }
}
