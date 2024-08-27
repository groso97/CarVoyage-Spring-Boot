package com.project.CarVoyage.controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.HashMap;

import com.project.CarVoyage.classes.car.Car;
import com.project.CarVoyage.classes.car.CarRepository;
import com.project.CarVoyage.classes.user.User;
import com.project.CarVoyage.classes.user.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpSession;

@Controller
public class CarsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/cars")
    public String getCarsPage(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "fuelType", required = false) String fuelType,
            @RequestParam(value = "transmission", required = false) String transmission,
            @RequestParam(value = "seats", required = false) Integer seats,
            Model model) {
        List<Car> cars;

        if (location != null && !location.isEmpty()) {
            cars = carRepository.findByLocation(location);
        } else if (fuelType != null && !fuelType.isEmpty()) {
            cars = carRepository.findByFuelType(fuelType);
        } else if (transmission != null && !transmission.isEmpty()) {
            cars = carRepository.findByTransmissionType(transmission);
        } else if (seats != null) {
            cars = carRepository.findBySeats(seats);
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
        model.addAttribute("fuelType", fuelType);
        model.addAttribute("transmission", transmission);
        model.addAttribute("seats", seats);

        return "cars";
    }

    @GetMapping("/cars/{carId}")
    public String getCarDetailsPage(Model model, @PathVariable int carId, HttpSession httpSession) {
        // Provjeri je li postavljen pickUpDate i dropOffDate u sesiji
        LocalDate pickUpDate = (LocalDate) httpSession.getAttribute("pickUpDate");
        LocalDate dropOffDate = (LocalDate) httpSession.getAttribute("dropOffDate");
    
        if (pickUpDate == null || dropOffDate == null) {
            // Ako datumi nisu postavljeni preusmjeri korisnika na početnu stranicu s porukom
            httpSession.setAttribute("infoMessage", "You must first select a location, pick-up date, and drop-off date.");
            return "redirect:/";
        }
    
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
    
        Car car = carRepository.findById(carId);
    
        int totalAmount = (int) (car.getDailyRate() * ChronoUnit.DAYS.between(pickUpDate, dropOffDate));
    
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("pickUpDate", pickUpDate);
        model.addAttribute("dropOffDate", dropOffDate);
        model.addAttribute("car", car);
        model.addAttribute("user", user);
        return "car";
    }
    

    @GetMapping("/checkout/{carId}")
    public String createCheckoutSession(@PathVariable int carId, HttpSession httpSession)
            throws StripeException {

        Car car = carRepository.findById(carId);

        LocalDate pickUpDate = (LocalDate) httpSession.getAttribute("pickUpDate");
        LocalDate dropOffDate = (LocalDate) httpSession.getAttribute("dropOffDate");
        int numberOfDays = (int) ChronoUnit.DAYS.between(pickUpDate, dropOffDate);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        

        Map<String, String> metadata = new HashMap<>();
        metadata.put("user_id", String.valueOf(user.getUserId()));
        metadata.put("car_id", String.valueOf(carId));
        metadata.put("start_date", pickUpDate.toString());
        metadata.put("end_date", dropOffDate.toString());
        metadata.put("total_amount", String.valueOf(numberOfDays * car.getDailyRate()));

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount((long) (car.getDailyRate() * numberOfDays * 100))
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(car.getMake() + ' ' + car.getModel())
                                .build())
                        .build())
                .setQuantity((long) 1)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8090/?success")
                .setCancelUrl("http://localhost:8090/")
                .addLineItem(lineItem)
                .putAllMetadata(metadata)
                .setShippingAddressCollection(
                        SessionCreateParams.ShippingAddressCollection.builder()
                                .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.HR)
                                .build())
                .build();

        Session session = Session.create(params);

        return "redirect:" + session.getUrl();
    }
}