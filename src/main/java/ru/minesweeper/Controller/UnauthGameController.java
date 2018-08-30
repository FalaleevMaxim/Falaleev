package ru.minesweeper.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.minesweeper.logic.Cell;
import ru.minesweeper.viewmodel.cell.CellVM;
import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.logic.Board;
import ru.minesweeper.logic.UnauthGame;
import ru.minesweeper.storage.game.GameStorage;

@Controller
@RequestMapping(value = "/UnauthGame")
public class UnauthGameController {
    @Autowired
    public UnauthGameController(@Qualifier("UnauthGames")GameStorage<String> games) {
        this.games = games;
    }
    private GameStorage<String> games;

    @RequestMapping(value = "/GameStart", method = RequestMethod.GET)
    public String gameStart(Model model){
        model.addAttribute("gameType","UnauthGame");
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart", method = RequestMethod.POST)
    public String gameStartPost(@ModelAttribute GameProperties gameProperties){
        String gameId = games.createGame(gameProperties);
        return "redirect:"+gameId+"/Game";
    }

    @RequestMapping(value = "/{id}/OpenCell", method = RequestMethod.POST)
    public @ResponseBody CellVM[] opencell(@ModelAttribute CellVM cell, @PathVariable String id){
        return games.getGame(id).openCell(cell.getX(), cell.getY());
    }

    @RequestMapping(value = "/{id}/SuggestBomb", method = RequestMethod.POST)
    public @ResponseBody boolean suggestBomb(@ModelAttribute CellVM cell, @PathVariable String id){
        return games.getGame(id).suggestBomb(cell.getX(), cell.getY());
    }

    @RequestMapping("/{id}/Game")
    public ModelAndView game(@PathVariable String id){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("id",id);
        UnauthGame game = games.getGame(id);
        Cell[][] field = game.getBoard().getField();
        modelAndView.addObject("properties", new GameProperties(
                game.getBoard().getFieldWidth(), game.getBoard().getFieldHeight(),
                game.getBoard().getBombsLeft(), game.getScore())
        );
        modelAndView.addObject("isFinished",game.isFinished());
        if(field==null) {
            modelAndView.setViewName("NewGame");
        }else{
            modelAndView.addObject("field",field);
            modelAndView.setViewName("GameStarted");
        }
        return modelAndView;
    }

    @RequestMapping("/{id}/win")
    public @ResponseBody boolean isWin(@PathVariable String id){
        return games.getGame(id).isWin();
    }

    @RequestMapping("/{id}/loose")
    public @ResponseBody boolean isLoose(@PathVariable String id){
        return games.getGame(id).isLoose();
    }

    @RequestMapping("/{id}/Score")
    public @ResponseBody int checkScore(@PathVariable String id){
        return games.getGame(id).getScore();
    }
}
