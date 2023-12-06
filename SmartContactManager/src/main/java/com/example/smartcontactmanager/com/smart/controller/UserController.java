package com.example.smartcontactmanager.com.smart.controller;

import com.example.smartcontactmanager.com.smart.entities.Contact;
import com.example.smartcontactmanager.com.smart.entities.User;
import com.example.smartcontactmanager.com.smart.helper.Message;
import com.example.smartcontactmanager.com.smart.repository.ContactRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import com.example.smartcontactmanager.com.smart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/user")
public class UserController {

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        model.addAttribute("user", userRepository.getUserByUserName(principal.getName()));
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashBoard(Model model, Principal principal) {
        model.addAttribute("pageTitle", "Dashboard | " + userRepository.getUserByUserName(principal.getName()).getName().toUpperCase() + " | Smart Contact Manager");
        model.addAttribute("user", userRepository.getUserByUserName(principal.getName()));
        return "normal/user_dashboard";
    }

    @RequestMapping("/add-contact")
    public String addContact(Model model, Principal principal) {
        model.addAttribute("contact", new Contact());
        model.addAttribute("pageTitle", "Add Contacts | " + userRepository.getUserByUserName(principal.getName()).getName().toUpperCase() + " | Smart Contact Manager");
        return "normal/add-contact";
    }

    @RequestMapping("/all-contact/{currentPage}")
    public String viewContacts(Model model, @PathVariable("currentPage") Integer currentPage, Principal principal) {
        //        model.addAttribute("contact", new Contact());
        model.addAttribute("pageTitle", "All Contacts | " + userRepository.getUserByUserName(principal.getName()).getName().toUpperCase() + " | Smart Contact Manager");

        PageRequest pageRequest = PageRequest.of(currentPage, 10);
        Page<Contact> contact = this.contactRepository.findContactByUser(this.userRepository.getUserByUserName(principal.getName()).getId(), pageRequest);
        model.addAttribute("totalPages", contact.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        //        contact.forEach(e -> System.out.println(e));

        model.addAttribute("contact", contact);


        return "normal/all-contact";
    }

    @PostMapping("/process-add-contact")
    public String processAddContact(@ModelAttribute("contact") Contact contact, @RequestParam("image") MultipartFile file, HttpSession session, Principal principal) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            // Processing and uploading file...
            if (!file.isEmpty()) {

                String fileName = file.getOriginalFilename();
                contact.setImageUrl(fileName); // Save the image filename

                // Create a unique file path for the image
                File saveFile = new ClassPathResource("static/image").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


            } else {

                contact.setImageUrl("defaultProfilePic.png");
            }
            System.out.println("contact id " + contact.getId());
            user.getcontacts().add(contact);
            contact.setUser(user);
            this.userRepository.save(user);

            session.setAttribute("message", new Message("C0NTACT ADDED SUCCESSFULLY !", "alert-success"));
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            session.setAttribute("message", new Message("Bad Credentials !", "alert-danger"));

            e.printStackTrace();
        }

        return "normal/add-contact";
    }


    @GetMapping("/contact/{contactId}/{currentPage}")
    public String showContactInfo(@PathVariable("contactId") Integer contactId, @PathVariable("currentPage") Integer currentPage, Model model, Principal principal) {

        User user = this.userRepository.getUserByUserName(principal.getName());
        Optional<Contact> opContact = this.contactRepository.findById(contactId);
        Contact contact = opContact.get();
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        }
        model.addAttribute("currentPage", currentPage);

        return "normal/view-contact";
    }

    @PostMapping("/contact/{contactId}/update-details")
    @ResponseBody
    public String updateContactDetails(@ModelAttribute("contact") Contact contact, @RequestParam("image") MultipartFile file, @PathVariable("contactId") Integer contactId, Model model) {
        System.out.println(file.getOriginalFilename());
        try {

            Optional<Contact> prevOp = this.contactRepository.findById(contactId);
            Contact oldContact = prevOp.get();

            contact.setId(contactId);
            contact.setUser(oldContact.getUser());

            if (!file.getOriginalFilename().equals("")) {
                contact.setImageUrl(file.getOriginalFilename());
            } else {
                if (!oldContact.getImageUrl().equals("")) {
                    contact.setImageUrl(oldContact.getImageUrl());
                } else {
                    contact.setImageUrl("defaultProfilePic.png");
                }
            }

            Contact result = this.contactRepository.save(contact);

            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                File saveFile = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            model.addAttribute("contact", contact);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @RequestMapping("/contact/delete-user/{contactId}")
    @ResponseBody
    public String deleteUser(@PathVariable("contactId") Integer contactid) {
        Optional<Contact> opContact = this.contactRepository.findById(contactid);
        Contact contact = opContact.get();

        try {
            this.contactRepository.delete(contact);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }

    @RequestMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        model.addAttribute("pageTitle", "Profile | " + userRepository.getUserByUserName(principal.getName()).getName().toUpperCase() + " | Smart Contact Manager");
        return "normal/profile";
    }

    @RequestMapping("/setting")
    public String settingPage(Model model, Principal principal) {
        model.addAttribute("pageTitle", "Setting | " + userRepository.getUserByUserName(principal.getName()).getName().toUpperCase() + " | Smart Contact Manager");
        return "normal/setting";
    }

    @RequestMapping("/update-user")
    @ResponseBody
    public String updateUser(@ModelAttribute("user") User user, @RequestParam("image") MultipartFile file, Principal principal) {

        try {

            User oldUser = this.userRepository.getUserByUserName(principal.getName());
            System.out.println(oldUser.getId());
            user.setId(oldUser.getId());
            user.setcontacts(oldUser.getcontacts());
            System.out.println(user.getId());
            if (!file.getOriginalFilename().equals("")) {
                user.setImageUrl(file.getOriginalFilename());
            } else {
                if (!oldUser.getImageUrl().equals("")) {
                    user.setImageUrl(oldUser.getImageUrl());
                } else {
                    user.setImageUrl("defaultProfilePic.png");
                }
            }

            User result = this.userRepository.save(user);

            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                File saveFile = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "success";

    }

    @RequestMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword, @RequestParam("oldPassword") String oldPassword, Principal principal, HttpSession session) {
        User user = this.userRepository.getUserByUserName(principal.getName());

        if (this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Password Changed Successfully...", "alert-success"));
        } else {
            session.setAttribute("message", new Message("Incorrect old password", "alert-danger"));
        }

        return "redirect:/user/setting";
    }

    @PostMapping("/create/order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data) throws Exception {
        int amnt = Integer.parseInt(data.get("amount").toString());
        var razorpayClient = new RazorpayClient("rzp_test_gyvRn5N8dED2Dm", "LCEW1MvE4GDqlhub8fWRRV9m");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amnt*100);
        jsonObject.put("currency", "INR");
        jsonObject.put("receipt", "txn_2345");

//        creating order
        Order order = razorpayClient.Orders.create(jsonObject);
        System.out.println("\n"+order);

        return order.toString();
    }


}