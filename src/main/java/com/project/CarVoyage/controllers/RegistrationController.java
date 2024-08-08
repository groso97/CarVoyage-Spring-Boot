package com.project.CarVoyage.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.CarVoyage.classes.user.User;
import com.project.CarVoyage.classes.user.UserRepository;
import com.project.CarVoyage.config.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @GetMapping("/registration")
    public String getRegistrationPage() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String termsAccepted,
            RedirectAttributes redirectAttributes) {

        if (termsAccepted == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must accept the terms and conditions.");
            return "redirect:/registration";
        }

        User existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email is already in use.");
            return "redirect:/registration";
        }

        User existingUserByUsername = userRepository.findByUsername(username);
        if (existingUserByUsername != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username is already in use.");
            return "redirect:/registration";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/registration";
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        user.setEmailVerified(false);

        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);

        userRepository.saveUser(user);

        String confirmationLink = "http://localhost:8090/confirm?token=" + token;
        emailService.sendMessage(
                user.getEmail(),
                "Confirm your e-mail address - CarVoyage",
                "Hello, " + user.getFirstName() + "!\n" +
                        "We are happy to see you want to join us!\n" +
                        "We just need one more thing to get you going... click the link below to confirm your e-mail address!\n"
                        +
                        "Confirmation link: " + confirmationLink);

        redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! Please check your email to verify your account.");

        return "redirect:/registration";
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {

        User user = userRepository.findByConfirmationToken(token);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email verification token.");
            return "redirect:/registration";
        }

        if (user.isEmailVerified()) {
            redirectAttributes.addFlashAttribute("infoMessage",
                    "Email address is already verified. You can now log in.");
            return "redirect:/registration";
        }

        user.setEmailVerified(true);
        userRepository.updateEmailVerification(user);

        redirectAttributes.addFlashAttribute("successMessage",
                "Your email address has been successfully verified. You can now log in.");

        return "redirect:/login";
    }

}
