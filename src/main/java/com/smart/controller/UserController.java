package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.doa.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // method for hiding common data
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        String username = principal.getName();
        System.out.println("USERNAME: " + username);

        // get the user using username
        User user = userRepository.getUserByUserName(username);
        System.out.println("USER: " + user);

        model.addAttribute("user", user);
    }

    // Dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // Open add-contact form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // Process the add-contact form
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute Contact contact,
                                 BindingResult result, 
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        try {
            // Validation error handling
            if (result.hasErrors()) {
                model.addAttribute("contact", contact);
                return "normal/add_contact_form"; // Return form with errors
            }

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            // Process and upload image file
            if (file.isEmpty()) {
                System.out.println("Image is empty");
                contact.setImage("default.png"); // Set a default image
            } else {
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user); // Save the contact

            System.out.println("DATA: " + contact);
            System.out.println("Added data to the database");

            // Add success message
            redirectAttributes.addFlashAttribute("message", "Contact saved successfully!");

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Error saving contact. Please try again.");
        }

        // Redirect back to the add-contact page
        return "redirect:/user/add-contact";
    }
    
    //show contact module or handler
    @GetMapping("/show-contacts")
    public String showContacts(Model m) {
    	return "normal/show_contacts";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
