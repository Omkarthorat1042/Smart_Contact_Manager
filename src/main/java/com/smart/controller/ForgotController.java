package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.doa.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	
	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	

	// email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session)
	{
		System.out.println("Email: " + email);
		
		//generating otp of 4 digit
		
		
		
		int otp = random.nextInt(99999);
		
		System.out.println("OTP: " +otp);
		
		//write code to send otp to your email..
		
		String subject = "OTP From Smart Contact Manager";
		
		String message = ""
				+ "<div style='border:1px solid #e2e2e2; padding:20px'>"
				+ "<h1>"
				+ "OTP is "
				+ " <b>" + otp 
				+ "</n>"
				+ "</h1>"
				+ "</div>";
		
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag)
		{
			
			session.setAttribute("myotp", otp);
			
			session.setAttribute("email", email);
			
			return "verify_otp";
			
		}else {
			
			session.setAttribute("message", "Check your email ID!!");
			
			
			return "forgot_email_form";
		}
			
		
		
		
		
		
	}
	
	@PostMapping("/verify_otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session)
	{
		
		int myOtp = (int)session.getAttribute("myotp");
		
		String email = (String)session.getAttribute("email");
		
		if(myOtp == otp)
		{
			
			
			//password change form
			User user = this.userRepository.getUserByUserName(email);
			
			if(user == null)
			{
				//send error message
				session.setAttribute("message", "No user exists on this email id..!");
				
				return "forgot_email_form";
				
			}else {
				
				//send password change password form
			}
			
			
			return "password_change_form";
		}
		else {
			
			session.setAttribute("message", "You have entered wrong OTP..!");
			
			return "verify_otp";
		}
	}
	
	
	//change password module
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session)
	{
		
		
		String email = (String)session.getAttribute("email");
		
		User user = this.userRepository.getUserByUserName(email);
		
		user.setPassword(this.bCrypt.encode(newPassword));
		
		this.userRepository.save(user);
		
		
		
		return "redirect:/signin?change=password changed successfully...";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
