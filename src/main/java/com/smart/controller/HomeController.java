package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.doa.UserRepository;
import com.smart.entities.User;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Signup - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1, 
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
                               Model model, HttpSession session) {
        try {
            if (!agreement) {
                model.addAttribute("errorMessage", "You have not agreed to the terms and conditions.");
                return "signup"; // Directly return to the signup page with error message
            }

            if (result1.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.jpg");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            

            User result = this.userRepository.save(user);

            model.addAttribute("user", new User());
            model.addAttribute("successMessage", "Successfully registered !!");
            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "Something went wrong !! " + e.getMessage());
            return "signup";
        }
        
       
    }
    
    //handler for custom login
    @GetMapping("/signin")
    public String customLogin(Model model)
    {
    	model.addAttribute("title", "Login Page");
    	return "login";

    }
    
    
    
    
    
    
    
    
    
    
}


