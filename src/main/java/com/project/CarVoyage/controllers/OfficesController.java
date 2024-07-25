package com.project.CarVoyage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.CarVoyage.classes.car.CarRepository;

@Controller
public class OfficesController {

    @GetMapping("/offices")
    public String getOfficesPage(Model model) {
        return "offices";
    }

}
