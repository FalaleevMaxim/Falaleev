package ru.test.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.test.ViewModel.GameProperties;
import ru.test.ViewModel.CellVM;
import ru.test.logic.Board;
import ru.test.logic.UnauthGame;

@Controller
@RequestMapping(value = "/UnauthGame")
public class UnauthGameController {
    //Статической игра сделана временно для тестов. TODO заменить на GameStorage
    static UnauthGame game;
    @RequestMapping(value = "/GameStart", method = RequestMethod.GET)
    public String GameStart(){
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart", method = RequestMethod.POST)
    public String GameStartPost(@ModelAttribute GameProperties gameProperties){
        game = new UnauthGame(gameProperties.getWidth(),gameProperties.getHeight(),gameProperties.getBombcount(),10);
        return "redirect:Game";
    }

    @RequestMapping(value = "/OpenCell", method = RequestMethod.POST)
    public @ResponseBody CellVM[] opencell(@ModelAttribute CellVM cell){
        CellVM[] cells = game.openCell(cell.getX(), cell.getY(), null);
        return cells;
    }

    @RequestMapping(value = "/SuggestBomb", method = RequestMethod.POST)
    public @ResponseBody boolean suggestBomb(@ModelAttribute CellVM cell){
        boolean isBomb = game.suggestBomb(cell.getX(), cell.getY(), null);
        return isBomb;
    }

    @RequestMapping("/Game")
    public ModelAndView Game(){
        ModelAndView modelAndView = new ModelAndView();
        Board.Cell[][] field = game.getBoard().getField();
        modelAndView.addObject("properties", new GameProperties(game.getBoard().getFieldWidth(),game.getBoard().getFieldHeight(),game.getBoard().getBombCount()));
        if(field==null) {
            modelAndView.setViewName("NewGame");
        }else{
            modelAndView.addObject("field",field);
            modelAndView.setViewName("GameStarted");
        }
        return modelAndView;
    }
}
