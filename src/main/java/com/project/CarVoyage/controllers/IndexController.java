package com.project.CarVoyage.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.CarVoyage.classes.car.Car;
import com.project.CarVoyage.classes.car.CarRepository;

@Controller
public class IndexController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/")
    public String getIndexPage(Model model) {
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "index";
    }
}
