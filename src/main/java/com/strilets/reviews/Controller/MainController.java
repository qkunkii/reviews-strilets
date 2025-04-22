package com.strilets.reviews.Controller;

import com.strilets.reviews.Model.Product;
import com.strilets.reviews.Model.User;
import com.strilets.reviews.Repository.ProductRepository;
import com.strilets.reviews.Repository.ReviewRepository;
import com.strilets.reviews.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

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
    private ProductRepository productRepository;
    @Autowired
    private ReviewRepository reviewRepository;


    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/start")
    public String start(HttpSession httpSession, Model model) {;
        if(httpSession.getAttribute("user") == null) {
            return "login";
        }
        else{
            model.addAttribute("role", httpSession.getAttribute("role").toString());
            return "homePage";
        }
    }
    @PostMapping("/login")
    public String login(HttpSession httpSession, Model model, @RequestParam String login, @RequestParam String password) {
        User user = userRepository.findUserByUsername(login).orElse(null);
        if(user != null && user.getPassword().equalsIgnoreCase(password)) {
            if(user.getRole().startsWith("comp:")) {
                httpSession.setAttribute("role", user.getRole());
                model.addAttribute("role", user.getRole());
                model.addAttribute("companyName", user.getRole().substring(5));
                model.addAttribute("reviews", reviewRepository.findAllByProduct(productRepository.findAllByName(user.getRole().substring(5))));
                return "companyHomePage";
            }
            else if(user.getRole().startsWith("admin:")) {
                httpSession.setAttribute("role", user.getRole());
                model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
                model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
                return "adminHomePage";
            }
            else{
                httpSession.setAttribute("role", user.getRole());
                model.addAttribute("username", user.getUsername());
                httpSession.setAttribute("username", user.getUsername());
                Iterable<Product> companies = productRepository.findAllByConfirmed(true);
                model.addAttribute("companies", companies);
                return "homePage";
            }

        }
        else if(user != null) {
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
            Iterable<Product> companies = productRepository.findAllByConfirmed(true);
            model.addAttribute("companies", companies);
            model.addAttribute("username", login);
            httpSession.setAttribute("username", login);
            httpSession.setAttribute("role", "user");
            return "homePage";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession httpSession, Model model) {
        httpSession.invalidate();
        return "redirect:/";
    }
    @GetMapping("/home")
    public String home(HttpSession httpSession, Model model) {
        if(httpSession.getAttribute("username") == null) {
            return "redirect:/start";
        }
        model.addAttribute("username", httpSession.getAttribute("username").toString());
        Iterable<Product> companies = productRepository.findAllByConfirmed((true));
        model.addAttribute("companies", companies);
        return "homePage";
    }
}
