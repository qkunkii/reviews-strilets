package com.strilets.reviews.Controller;

import com.strilets.reviews.Model.User;
import com.strilets.reviews.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ErrorController errorController;


    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/start")
    public String start(HttpSession httpSession, Model model) {
        if(httpSession.getAttribute("role") == null) {
            return "login";
        }
        else{
            model.addAttribute("role", httpSession.getAttribute("role").toString());
            return "homePage";
        }
    }
    @PostMapping("/login")
    public String login(HttpSession httpSession, Model model, @RequestParam String login, @RequestParam String password) {
        Optional<User> user = userRepository.findUserByUsername(login);
        if(user.isPresent() && user.get().getPassword().equalsIgnoreCase(password)) {
            model.addAttribute("username", login);
            httpSession.setAttribute("user", user);
            return "homePage";
        }
        else if(user.isPresent()) {
            model.addAttribute("errorText", "Неправильний логін або пароль");
            return "login";
        }
        else{
            model.addAttribute("errorText", "Такого аккаунту не існує. Зареєструйтесь");
            return "login";
        }
    }

    @GetMapping("/register")
    public String register(HttpSession httpSession, Model model) {
        if(httpSession.getAttribute("role") != null) {
            return "homePage";
        }
        else{
            return "register";

        }
    }
    @PostMapping("/registerconf")
    public String register(HttpSession httpSession, Model model,  @RequestParam String login,@RequestParam String email, @RequestParam String password) {
        User userExists = userRepository.findUserByUsername(login).orElse(userRepository.findUserByEmail(email) == null ? null : userRepository.findUserByEmail(email));
        if(userExists != null) {
            model.addAttribute("errorText", "Юзер з таким email або login вже існує");
            return "register";
        }
        else{
            User user = new User(login, email, password, "user");
            userRepository.save(user);

            httpSession.setAttribute("user", user);
            httpSession.setAttribute("role", "user");
            return "homePage";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession httpSession, Model model) {
        httpSession.invalidate();
        return "redirect:/";
    }
}
