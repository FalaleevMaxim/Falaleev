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
import ru.test.logic.Board;
import ru.test.logic.Game;
import ru.test.model.GameStorage;
import ru.test.model.Storage;
import ru.test.model.User;

@Controller
@RequestMapping("/SinglePlayerGame")
public class AuthGameController{
    private GameStorage<Integer,Integer> games;
    private Storage<User> userStorage;

    @Autowired
    public AuthGameController(@Qualifier("AuthGames")GameStorage<Integer, Integer> games,Storage<User> userStorage) {
        this.games = games;
        this.userStorage = userStorage;
    }

    @RequestMapping(value = "/GameStart",method = RequestMethod.GET)
    public String GameStart(Model model){
        model.addAttribute("gameType","SinglePlayerGame");
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart",method = RequestMethod.POST)
    public String GameStart(@ModelAttribute GameProperties gameProperties){
        Integer id = userStorage.findByName(
                        (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                ).getId();
        games.removeGame(id);
        games.createGame(id,gameProperties);
        return "redirect:Game";
    }

    @RequestMapping("/Game")
    public ModelAndView Game(){
        ModelAndView modelAndView = new ModelAndView();
        Integer id = userStorage.findByName(
                (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
            ).getId();
        modelAndView.addObject("id",id);
        Game game = games.getGameByPlayer(id);
        if(game==null){
            modelAndView.setViewName("GameStartForm");
            return modelAndView;
        }
        Board.Cell[][] field = game.getBoard().getField();
        modelAndView.addObject("properties", new GameProperties(game.getBoard().getFieldWidth(),game.getBoard().getFieldHeight(),game.getBoard().getBombsLeft(),game.getScore(null)));
        modelAndView.addObject("isFinished",game.isFinished());
        if(field==null) {
            modelAndView.setViewName("NewGame");
        }else{
            modelAndView.addObject("field",field);
            modelAndView.setViewName("GameStarted");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/OpenCell", method = RequestMethod.POST)
    public @ResponseBody CellVM[] opencell(@ModelAttribute CellVM cell){
        int id = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        CellVM[] cells = games.getGameById(id).openCell(cell.getX(), cell.getY(), null);
        return cells;
    }

    @RequestMapping(value = "/SuggestBomb", method = RequestMethod.POST)
    public @ResponseBody boolean suggestBomb(@ModelAttribute CellVM cell){
        int id = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        boolean isBomb = games.getGameById(id).suggestBomb(cell.getX(), cell.getY(), null);
        return isBomb;
    }

    @RequestMapping("/Score")
    public @ResponseBody int checkScore(){
        int id = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return games.getGameById(id).getScore(null);
    }
}
