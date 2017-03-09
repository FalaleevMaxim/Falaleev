package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.Board;
import ru.test.logic.UnauthGame;
import ru.test.storage.GameStorage;

@Controller
@RequestMapping(value = "/UnauthGame")
public class UnauthGameController {
    @Autowired
    public UnauthGameController(@Qualifier("UnauthGames")GameStorage<String> games) {
        this.games = games;
    }
    private GameStorage<String> games;

    @RequestMapping(value = "/GameStart", method = RequestMethod.GET)
    public String GameStart(Model model){
        model.addAttribute("gameType","UnauthGame");
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart", method = RequestMethod.POST)
    public String GameStartPost(@ModelAttribute GameProperties gameProperties){
        String gameId = games.createGame(gameProperties);
        return "redirect:"+gameId+"/Game";
    }

    @RequestMapping(value = "/{id}/OpenCell", method = RequestMethod.POST)
    public @ResponseBody CellVM[] opencell(@ModelAttribute CellVM cell, @PathVariable String id){
        CellVM[] cells = games.getGame(id).openCell(cell.getX(), cell.getY());
        return cells;
    }

    @RequestMapping(value = "/{id}/SuggestBomb", method = RequestMethod.POST)
    public @ResponseBody boolean suggestBomb(@ModelAttribute CellVM cell, @PathVariable String id){
        boolean isBomb = games.getGame(id).suggestBomb(cell.getX(), cell.getY());
        return isBomb;
    }

    @RequestMapping("/{id}/Game")
    public ModelAndView Game(@PathVariable String id){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("id",id);
        UnauthGame game = games.getGame(id);
        Board.Cell[][] field = game.getBoard().getField();
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
