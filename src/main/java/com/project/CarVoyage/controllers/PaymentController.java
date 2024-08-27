package com.project.CarVoyage.controllers;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.project.CarVoyage.classes.car.Car;
import com.project.CarVoyage.classes.car.CarRepository;
import com.project.CarVoyage.classes.reservation.Reservation;
import com.project.CarVoyage.classes.reservation.ReservationRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.Session.CustomerDetails;
import com.stripe.net.Webhook;

@RestController
public class PaymentController {
    @Value("${stripe.webhook.secret}")
    private String stripeWebhook;

    @Value("${spring.mail.username}")
    private String emailVlasnika;

    @Value("${stripe.api.key}")
    private String stripeApiKey;


    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private JavaMailSender mailSender;

    @SuppressWarnings("deprecation")
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            final Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhook);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getData().getObject();
                handleSuccessfulPayment(session);
            }
            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            System.err.println("Signature verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            System.err.println("Unhandled error processing Stripe webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

private void handleSuccessfulPayment(Session session) {
    String customerEmail = session.getCustomerEmail();

    if (customerEmail == null) {
        CustomerDetails details = session.getCustomerDetails();
        if (details != null) {
            customerEmail = details.getEmail();
        }
    }

    if (customerEmail != null) {
        // Pretpostavljamo da imamo userId, carId, startDate, endDate i totalAmount u metadata ili nekom drugom dijelu sesije
        int userId = Integer.parseInt(session.getMetadata().get("user_id"));
        int carId = Integer.parseInt(session.getMetadata().get("car_id"));
        Date startDate = Date.valueOf(session.getMetadata().get("start_date"));
        Date endDate = Date.valueOf(session.getMetadata().get("end_date"));
        double totalAmount = session.getAmountTotal() / 100.0;

        // Kreiramo novi objekt rezervacije
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setCarId(carId);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setTotalAmount(totalAmount);
        reservation.setStatus("confirmed");

        // Spremamo rezervaciju u bazu podataka
        reservationRepository.saveReservation(reservation);

        Car car = carRepository.findCarMakeAndModelById(carId);
        String carName = car.getMake() + " " + car.getModel();

        // Slanje potvrde emailom korisniku
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailVlasnika);
            message.setTo(customerEmail);
            message.setSubject("CarVoyage | Confirmation of vehicle reservation");

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Dear Customer, \n\n");
            emailContent.append("Thank you for choosing CarVoyage for your car rental service.\n");
            emailContent.append("We are pleased to confirm your vehicle reservation.\n\n");
            emailContent.append("Your reservation details:\n");
            emailContent.append("Vehicle: ").append(carName).append("\n");
            emailContent.append("Rental start: ").append(startDate.toString()).append("\n");
            emailContent.append("Rental end: ").append(endDate.toString()).append("\n");
            emailContent.append("Total paid: ").append(String.format("%.2f", totalAmount)).append(" ").append(session.getCurrency().toUpperCase()).append("\n\n");
            emailContent.append("Thank you once again, and we wish you a safe and pleasant journey!\n");
            emailContent.append("Best regards,\nCarVoyage team");

            message.setText(emailContent.toString());

            mailSender.send(message);


        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
}
