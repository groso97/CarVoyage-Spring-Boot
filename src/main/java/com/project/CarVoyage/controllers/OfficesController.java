package com.project.CarVoyage.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.CarVoyage.classes.location.Location;
import com.project.CarVoyage.classes.location.LocationRepository;

@Controller
public class OfficesController {

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping("/offices")
    public String getOfficesPage(Model model) {
        List<Location> locations;
        locations = locationRepository.findAll();
        model.addAttribute("locations", locations);
        return "offices";
    }

}
