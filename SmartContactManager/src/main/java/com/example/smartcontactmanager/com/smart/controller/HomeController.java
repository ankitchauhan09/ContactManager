package com.example.smartcontactmanager.com.smart.controller;

import com.example.smartcontactmanager.com.smart.entities.User;
import com.example.smartcontactmanager.com.smart.repository.UserRepository;
import com.example.smartcontactmanager.com.smart.helper.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Home | Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About | Smart Contact Manager");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("pageTitle", "SignUp | Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login | Smart Contact Manager");
        return "login";
    }

    @Transactional
   @PostMapping("/register-user")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,Model model, HttpSession session){
        try{

            String passPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[@#\\$%])(?=.*\\d)[A-Za-z@#\\$%\\d]{8,15}$";
            final String patternFormat = "Password must follow the following pattern :  \n1. At least one Capital Letter\n2.At least one small alphabet\n3.At least one special character(@, $, #, %)\n4.At least one digit\n5. Password length should be between 8-15 characters";

            System.out.printf(String.valueOf(Pattern.matches(passPattern, user.getPassword())));
            if(!Pattern.matches(passPattern, user.getPassword())){
                bindingResult.addError(new FieldError("user","password",patternFormat));
            }

            if(bindingResult.hasErrors()){
//                System.out.println(bindingResult.toString());
                model.addAttribute("user", user);
                return "signup";
            }
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));


            User result = userRepository.save(user);
            session.setAttribute("message", new Message("Successfully registered!! ", "alert-success"));
            model.addAttribute("user", result);
            return "signup";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            session.setAttribute("message", new Message("Some error occurred !! " + e.getMessage(), "alert-danger"));
            model.addAttribute("user", new User());
            return "signup";
        }
   }

   @GetMapping("/signin")
    public String login(){
        return "login";
   }






}
