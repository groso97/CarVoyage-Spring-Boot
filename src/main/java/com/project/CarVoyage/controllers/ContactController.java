package com.project.CarVoyage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/contact")
    public String getContactPage(Model model) {
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContactForm(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String details,
            RedirectAttributes redirectAttributes) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo("carvoyage.contact@gmail.com");
        message.setSubject("New Inquiry from " + firstName + " " + lastName);
        message.setText("First Name: " + firstName + "\n"
                + "Last Name: " + lastName + "\n"
                + "Email: " + email + "\n"
                + "Phone: " + phone + "\n"
                + "Details: " + details);
        message.setReplyTo(email);

        mailSender.send(message);

        redirectAttributes.addFlashAttribute("message", "Your inquiry has been sent successfully!");

        return "redirect:/contact";
    }
}
