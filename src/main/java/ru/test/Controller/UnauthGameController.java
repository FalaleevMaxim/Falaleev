package ru.test.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.test.ViewModel.GameProperties;
import ru.test.ViewModel.CellVM;
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
    public ModelAndView GameStartPost(@ModelAttribute GameProperties gameProperties){
        game = new UnauthGame(gameProperties.getWidth(),gameProperties.getHeight(),gameProperties.getBombcount(),10);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("Game");
        modelAndView.addObject("properties",gameProperties);
        return modelAndView;
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
}
