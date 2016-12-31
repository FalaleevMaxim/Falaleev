package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.test.model.Storage;
import ru.test.model.User;

@Controller
@RequestMapping(value = "/main")
public class MainController {
    private Storage<User> userStorage;

    @Autowired
    public MainController(Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    @RequestMapping(value = "/header")
    public String header(Model model){
        if(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken){
            return "unauthheader";
        }else{
            User user = userStorage.findByName((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            model.addAttribute("UserName",user.getUserName());
            model.addAttribute("RealName",user.getRealName());
            return "authheader";
        }
    }
}
