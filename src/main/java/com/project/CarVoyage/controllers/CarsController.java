package com.project.CarVoyage.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.CarVoyage.classes.car.Car;
import com.project.CarVoyage.classes.car.CarRepository;

@Controller
public class CarsController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/cars")
    public String getCarsPage(Model model) {
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "cars";
    }

    @GetMapping("/cars/{carId}")
    public String getCarDetailsPage(Model model, @PathVariable int carId) {
        Car car = carRepository.findById(carId);
        model.addAttribute("car", car);
        return "car";
    }

}
