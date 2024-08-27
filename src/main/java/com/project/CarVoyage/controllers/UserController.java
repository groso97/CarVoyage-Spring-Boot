package com.project.CarVoyage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.CarVoyage.classes.reservation.ReservationRepository;
import com.project.CarVoyage.classes.user.User;
import com.project.CarVoyage.classes.user.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        User currentUser = getCurrentlyLoggedInUser();
        
        model.addAttribute("user", currentUser);
        model.addAttribute("reservations", reservationRepository.getReservationsByUserId(currentUser.getUserId()));
        
        return "profile";
    }
    

    @PostMapping("/update-password")
    public String updatePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentlyLoggedInUser();
    
        // Provjera da li je stara lozinka ispravna
        if (passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            // Enkripcija nove lozinke
            String encryptedNewPassword = passwordEncoder.encode(newPassword);
            currentUser.setPassword(encryptedNewPassword);
            userRepository.updatePassword(currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update password. Please check your current password.");
        }
    
        return "redirect:/user/profile";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(
            @RequestParam("confirmDelete") String confirmUsername, 
            RedirectAttributes redirectAttributes) {
    
        User currentUser = getCurrentlyLoggedInUser();

    // Provjera da li unijeto korisničko ime odgovara trenutnom korisniku
        if (currentUser.getUsername().equals(confirmUsername)) {
        userRepository.deleteByUsername(currentUser.getUsername());
        return "redirect:/logout";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete account. Username does not match.");
            return "redirect:/user/profile";
        }
    }

    private User getCurrentlyLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }
}
