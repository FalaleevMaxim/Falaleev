package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.test.model.Storage;
import ru.test.model.User;
import ru.test.model.UserStorage;

@Controller
@RequestMapping("/User")
public class UserController {
    private final Storage<User> userStorage;

    @Autowired
    public UserController(Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    @RequestMapping(value="/Register",method = RequestMethod.GET)
    public String register(Model model){
        model.addAttribute(new User());
        return "registerForm";
    }

    @RequestMapping(value = "/Register",method = RequestMethod.POST)
    public ModelAndView register(@ModelAttribute User user){
        userStorage.add(user);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userInfo");
        modelAndView.addObject(user);
        return modelAndView;
    }
}
