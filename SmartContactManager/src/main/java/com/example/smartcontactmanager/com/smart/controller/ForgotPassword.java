package com.example.smartcontactmanager.com.smart.controller;

import com.example.smartcontactmanager.com.smart.entities.User;
import com.example.smartcontactmanager.com.smart.helper.EmailSender;
import com.example.smartcontactmanager.com.smart.helper.Message;
import com.example.smartcontactmanager.com.smart.helper.SessionHelper;
import com.example.smartcontactmanager.com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.jsse.JSSEUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
public class ForgotPassword {

    @Autowired
    EmailSender emailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptpasswordEncoder;

    @GetMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession httpSession) {

        User checkUser = this.userRepository.getUserByUserName(email);
        if(checkUser == null){
            httpSession.setAttribute("message", new Message("No user found with this email", "alert-danger"));
            return "redirect:/forgot";
        }

        Random random = new Random();
        int otp = random.nextInt(1000000, 9999999);
        System.out.println(otp);

        System.out.println(email);
        boolean otpSent = emailSender.sendEmail(email, otp);
        if (otpSent) {
            httpSession.setAttribute("message", new Message("OTP Sent Successfully", "alert-success"));
            httpSession.setAttribute("email", email);
        } else {
            httpSession.setAttribute("message", new Message("Some error has occurred..", "alert-danger"));
            return "redirect:/forgot";
        }
        httpSession.setAttribute("otp", otp);
        return "send-otp";
    }


    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/forgot")
    public String forgotView() {
        return "forgot";
    }

    @PostMapping("/forgot-password/process")
    @ResponseBody
    public String processForgotPassword(@RequestParam("firstPass") String password, @RequestParam("confPass") String confirmPassword, HttpSession session) {
        System.out.println("lol");
        if (!password.equals(confirmPassword)) {
            return " password mismtah Some Error Occurred";
        } else {
            String email = session.getAttribute("email").toString();
            System.out.println("email  : " + email);
            User user = this.userRepository.getUserByUserName(email);
            user.setPassword(this.bCryptpasswordEncoder.encode(password));
            User result = this.userRepository.save(user);
            System.out.println(result);
            try {
                SessionHelper.removeSessionAttribute("email");
                SessionHelper.removeSessionAttribute("otp");
                return "success";
            } catch (Exception e) {
                e.printStackTrace();
                return " lol Some Error Occurred "+e.getMessage()+"";
            }
        }
    }

    @RequestMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {

        int generatedOtp = (int) session.getAttribute("otp");
        System.out.println("generated OTP : " + generatedOtp);
        if (generatedOtp != otp) {
            session.setAttribute("message", new Message("Invalid OTP Please try again...", "alert-danger"));
            return "send-otp";
        }

        return "redirect:/forgot-password";
    }

}
