package ru.minesweeper.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.minesweeper.storage.JpaUserStorage;
import ru.minesweeper.viewmodel.RegisterVM;
import ru.minesweeper.storage.Storage;
import ru.minesweeper.model.User;
import ru.minesweeper.service.security.PasswordEncryptor;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
@RequestMapping("/User")
public class UserController {
    private final JpaUserStorage userStorage;
    private PasswordEncryptor encryptor;

    @Autowired
    public UserController(JpaUserStorage userStorage, PasswordEncryptor encryptor) {
        this.userStorage = userStorage;
        this.encryptor = encryptor;
    }

    @RequestMapping(value="/Register",method = RequestMethod.GET)
    public String register(){
        return "registerForm";
    }

    @RequestMapping(value = "/Register",method = RequestMethod.POST)
    public String register(@ModelAttribute RegisterVM formData){
        User user = new User(0,formData.getUserName(),encryptor.encryptPassword(formData.getPassword(),formData.getUserName()),formData.getRealName());
        User saved = userStorage.save(user);
        return "redirect:Profile/"+saved.getId();
    }

    @RequestMapping(value = "/Login",method = RequestMethod.GET)
    public String login(){
        return "loginForm";
    }

    @RequestMapping(value = "/Profile")
    public  String userInfo(){
        User currentUser = userStorage.findUserByUserName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return "redirect:Profile/"+currentUser.getId();
    }

    @RequestMapping(value = "/Profile/{id}")
    public ModelAndView userInfo(@PathVariable int id, HttpServletResponse response){
        Optional<User> user = userStorage.findById(id);
        ModelAndView modelAndView = new ModelAndView();
        if(!user.isPresent()){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        modelAndView.setViewName("userInfo");
        modelAndView.addObject(user.get());
        return modelAndView;
    }
}
