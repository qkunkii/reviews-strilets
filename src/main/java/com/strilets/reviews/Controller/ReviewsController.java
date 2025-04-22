package com.strilets.reviews.Controller;

import com.strilets.reviews.Model.Product;
import com.strilets.reviews.Model.Review;
import com.strilets.reviews.Model.User;
import com.strilets.reviews.Repository.ProductRepository;
import com.strilets.reviews.Repository.ReviewRepository;
import com.strilets.reviews.Repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ReviewsController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/";
        }
        Product product = productOpt.get();
        Iterable<Review> reviews = reviewRepository.findAllByProductAndConfirmed(product, true);
        model.addAttribute("reviews", reviews);
        model.addAttribute("product", productOpt.get());
        model.addAttribute("reviewForm", new Review());
        return "productDetailsAndReview";
    }
    @PostMapping("/product/{id}/add")
    public String addReview(@PathVariable Long id,
                            @RequestParam int rating, @RequestParam String content,
                            Model model, @RequestParam String username, HttpSession httpSession) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/home";
        }

        model.addAttribute("username", httpSession.getAttribute("username"));
        User user = userRepository.findUserByUsername(httpSession.getAttribute("username").toString()).orElse(null);
        Review review = new Review(content, rating, false, product, user);
        reviewRepository.save(review);
        Iterable<Review> reviews = reviewRepository.findAllByUser(user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("username", httpSession.getAttribute("username").toString());

        return "myReviews";
    }
    @GetMapping("/my-reviews")
    public String myReviews(Model model, HttpSession httpSession) {
        User user = userRepository.findUserByUsername(httpSession.getAttribute("username").toString()).orElse(null);
        Iterable<Review> reviews = reviewRepository.findAllByUser(user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("username", httpSession.getAttribute("username").toString());
    return "myReviews";
    }
    @GetMapping("/suggest")
    public String suggest(Model model, HttpSession httpSession) {
        model.addAttribute("username", httpSession.getAttribute("username").toString());
        return "suggest";
    }
    @PostMapping("/suggest")
    public String suggestPost(@RequestParam String name,@RequestParam String url, Model model, HttpSession httpSession) {
        Product product = new Product(name, url, false);
        productRepository.save(product);
        return "redirect:/home";
    }
    @PostMapping("/admin/suggestions/reject/{id}")
    public String reject(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if(product != null) {
            productRepository.deleteById(id);
            productRepository.flush();
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }
        else{
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }
    }

    @PostMapping("/admin/suggestions/approve/{id}")
    public String approve(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if(product != null) {
            product.setConfirmed(true);
            productRepository.saveAndFlush(product);
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }
        else{
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }
    }
    @PostMapping("/admin/reviews/approve/{id}")
    public String reviewapprove(@PathVariable Long id, Model model){
        Review review = reviewRepository.findById(id).orElse(null);
        if(review != null){
            review.setConfirmed(true);
            reviewRepository.saveAndFlush(review);
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }
        else{
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }

    }
    @PostMapping("/admin/reviews/reject/{id}")
    public String reviewreject(@PathVariable Long id, Model model){
        Review review = reviewRepository.findById(id).orElse(null);
        if(review != null){
            reviewRepository.deleteById(id);
            reviewRepository.flush();
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }
        else{
            model.addAttribute("reviews", reviewRepository.findAllByConfirmed(false));
            model.addAttribute("suggestions", productRepository.findAllByConfirmed(false));
            return "adminHomePage";
        }

    }

}

