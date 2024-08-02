package com.project.CarVoyage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TermsController {

    @GetMapping("/termsAndConditions")
    public String showTermsAndConditions(Model model) {
        return "termsAndConditions";
    }

}