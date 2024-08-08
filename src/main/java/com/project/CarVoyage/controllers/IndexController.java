package com.project.CarVoyage.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.CarVoyage.classes.car.Car;
import com.project.CarVoyage.classes.car.CarRepository;
import com.project.CarVoyage.classes.reservation.Reservation;
import com.project.CarVoyage.classes.reservation.ReservationRepository;

import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/search")
    public String getIndexSearchCars(
            @RequestParam("location") int locationId,
            @RequestParam("pickUpDate") String pickUpDateStr,
            @RequestParam("dropOffDate") String dropOffDateStr,
            HttpSession session,
            Model model) {

        // Convert date strings to LocalDate
        LocalDate pickUpDate = LocalDate.parse(pickUpDateStr);
        LocalDate dropOffDate = LocalDate.parse(dropOffDateStr);

        // Save dates in session
        session.setAttribute("pickUpDate", pickUpDate);
        session.setAttribute("dropOffDate", dropOffDate);

        // Fetch available cars based on location and dates
        List<Car> availableCars = carRepository.findAvailableCars(locationId, pickUpDate, dropOffDate);

        // Add available cars to the model
        model.addAttribute("cars", availableCars);

        return "cars";
    }

}
