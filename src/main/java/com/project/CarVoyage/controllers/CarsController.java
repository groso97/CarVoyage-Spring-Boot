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
    public String getCarsPage(@RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "location", required = false) String location, Model model) {
        List<Car> cars;
        if (location != null && !location.isEmpty()) {
            cars = carRepository.findByLocation(location);
        } else {
            if ("priceAsc".equals(sort)) {
                cars = carRepository.findAllSortedByPriceAsc();
            } else if ("priceDesc".equals(sort)) {
                cars = carRepository.findAllSortedByPriceDesc();
            } else {
                cars = carRepository.findAll();
            }
        }
        model.addAttribute("cars", cars);
        model.addAttribute("location", location);
        return "cars";
    }

    @GetMapping("/cars/{carId}")
    public String getCarDetailsPage(Model model, @PathVariable int carId) {
        Car car = carRepository.findById(carId);
        model.addAttribute("car", car);
        return "car";
    }

}
