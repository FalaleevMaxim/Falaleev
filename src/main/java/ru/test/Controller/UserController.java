package ru.test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.test.ViewModel.RegisterVM;
import ru.test.model.Storage;
import ru.test.model.User;
import ru.test.service.PasswordEncryptor;

import java.io.IOException;

@Controller
@RequestMapping("/User")
public class UserController {
    private final Storage<User> userStorage;
    private PasswordEncryptor encryptor;

    @Autowired
    public UserController(Storage<User> userStorage,PasswordEncryptor encryptor) {
        this.userStorage = userStorage;
        this.encryptor = encryptor;
    }

    @RequestMapping(value="/Register",method = RequestMethod.GET)
    public String register(Model model){
        model.addAttribute(new User());
        return "registerForm";
    }

    @RequestMapping(value = "/Register",method = RequestMethod.POST)
    public String register(@ModelAttribute RegisterVM formData){
        User user = new User(0,formData.getUserName(),encryptor.encryptPassword(formData.getPassword(),formData.getUserName()),formData.getRealName());
        int id = userStorage.add(user);
        return "redirect:Profile/"+id;
    }

    @RequestMapping(value = "/Profile")
    public  String userInfo(){
        User user = userStorage.findByName((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return "redirect:Profile/"+user.getId();
    }

    @RequestMapping(value = "/Profile/{id}")
    public ModelAndView userInfo(@PathVariable int id){
        User user = userStorage.findById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userInfo");
        modelAndView.addObject(user);
        return modelAndView;
    }
}
