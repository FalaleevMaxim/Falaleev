package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.test.model.GameStorage;
import ru.test.ViewModel.GameProperties;
import ru.test.ViewModel.CellVM;
import ru.test.logic.Board;
import ru.test.logic.Game;

@Controller
@RequestMapping(value = "/UnauthGame")
public class UnauthGameController {
    @Autowired
    public UnauthGameController(@Qualifier("UnauthGames")GameStorage<Integer,String> games) {
        this.games = games;
    }
    private GameStorage<Integer,String> games;

    @RequestMapping(value = "/GameStart", method = RequestMethod.GET)
    public String GameStart(Model model){
        model.addAttribute("gameType","UnauthGame");
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart", method = RequestMethod.POST)
    public String GameStartPost(@ModelAttribute GameProperties gameProperties){
        String gameId = games.createGame(null,gameProperties);
        return "redirect:"+gameId+"/Game";
    }

    @RequestMapping(value = "/{id}/OpenCell", method = RequestMethod.POST)
    public @ResponseBody CellVM[] opencell(@ModelAttribute CellVM cell, @PathVariable String id){
        CellVM[] cells = games.getGameById(id).openCell(cell.getX(), cell.getY(), null);
        return cells;
    }

    @RequestMapping(value = "/{id}/SuggestBomb", method = RequestMethod.POST)
    public @ResponseBody boolean suggestBomb(@ModelAttribute CellVM cell, @PathVariable String id){
        boolean isBomb = games.getGameById(id).suggestBomb(cell.getX(), cell.getY(), null);
        return isBomb;
    }

    @RequestMapping("/{id}/Game")
    public ModelAndView Game(@PathVariable String id){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("id",id);
        Game game = games.getGameById(id);
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

    @RequestMapping("/{id}/Score")
    public @ResponseBody int checkScore(@PathVariable String id){
        return games.getGameById(id).getScore(null);
    }

}
