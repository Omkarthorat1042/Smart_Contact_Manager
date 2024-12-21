package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.doa.ContactRepository;
import com.smart.doa.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

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
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Principal principal, Model model,
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
				contact.setImage("contact.png"); // Set a default image
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

	// show contacts handler
	// per page = 5[n]
	// current page = 0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show user contacts...");

		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);

		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	@RequestMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		System.out.println("CID" + cid);

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);

		Contact contact = contactOptional.get();

		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	// delete handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);

		Contact contact = contactOptional.get();

		// check..

		User user = this.userRepository.getUserByUserName(principal.getName());
		
		user.getContacts().remove(contact);
		
		this.userRepository.save(user);

		session.setAttribute("message", new Message("Contact deleted  successfully", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,   Model m)
	{
		
		
		m.addAttribute("title", "Update Contact");
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		m.addAttribute("contact", contact);
		
		
		return "normal/update_form";
	}
	
	
	// update contact handler
	@RequestMapping(value = "/process-update", method= RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, 
			Model m, HttpSession session, Principal principal) {
		
		try {
			
			Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
			
			if(!file.isEmpty()) {
				//file work..
				// rewrite
				//delete old photos
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();
				
				
				//update old photos
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
				
			}else {
				
				contact.setImage(oldContactDetail.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		System.out.println("Contact name:" + contact.getName());
		
		
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	
	//your profile handler
	
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title", "Profile Page");
		
		return "normal/profile";
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings()
	{
		
		
		return "normal/settings";
	}
	
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, 
	        Principal principal, HttpSession session) {
	    System.out.println("Old Password: " + oldPassword);
	    System.out.println("New Password: " + newPassword);
	    
	    String userName = principal.getName();
	    User currentUser = this.userRepository.getUserByUserName(userName);
	    
	    if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
	        // Change the password
	        currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
	        this.userRepository.save(currentUser);
	        session.setAttribute("message", new Message("Your password has successfully changed", "success"));
	    } else {
	        // Error
	        session.setAttribute("message", new Message("Please enter correct old password", "danger"));
	    }
	    
	    return "redirect:/user/settings";
	}

	
	
	
	
	
	
	
	
}
