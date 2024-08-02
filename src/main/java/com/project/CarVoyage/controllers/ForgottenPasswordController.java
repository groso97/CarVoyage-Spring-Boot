package com.project.CarVoyage.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.CarVoyage.classes.user.User;
import com.project.CarVoyage.classes.user.UserRepository;
import com.project.CarVoyage.config.EmailService;

@Controller
public class ForgottenPasswordController {
    private final EmailService emailService;
    private final Map<String, String> passwordResetTokens;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgottenPasswordController(EmailService emailService, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.passwordResetTokens = new HashMap<>();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgotten-password")
    public String showForgottenPasswordForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        model.addAttribute("user", user);

        return "forgotten-password";
    }

    @PostMapping("/forgotten-password")
    public String processForgottenPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There is no user account with the entered e-mail address!");

            return "redirect:/forgotten-password";

        }
        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, email);
        String resetLink = "http://localhost:8090/reset-password?token=" + token;

        emailService.sendForgottenPasswordEmail(email, resetLink);

        redirectAttributes.addFlashAttribute("successMessage",
                "We've sent you an email with password recovery instructions!");

        return "redirect:/forgotten-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        model.addAttribute("user", user);

        String email = passwordResetTokens.get(token);

        if (email != null) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while retrieving the token!");
            return "redirect:/reset-password?token=" + token;
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        String email = passwordResetTokens.get(token);

        if (email != null) {
            User user = userRepository.findByEmail(email);

            if (user != null) {
                if (!password.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "The password confirmation does not match the entered password!");

                    return "redirect:/reset-password?token=" + token;
                }

                user.setPassword(passwordEncoder.encode(password));
                userRepository.updatePassword(user);

                passwordResetTokens.remove(token);

                redirectAttributes.addFlashAttribute("successMessage",
                        "Password successfully changed! You can now log in with a new password.");

                return "redirect:/login";
            }
        }

        redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while resetting the password!");
        return "redirect:/reset-password?token=" + token;
    }

}
