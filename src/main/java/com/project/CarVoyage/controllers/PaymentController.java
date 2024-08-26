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
    private JavaMailSender mailSender;

    @SuppressWarnings("deprecation")
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

                System.out.println("Primljen webhook zahtjev od Stripe-a");
    System.out.println("Payload: " + payload);
    System.out.println("Stripe-Signature: " + sigHeader);
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

        // Slanje potvrde emailom korisniku
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailVlasnika);
            message.setTo(customerEmail);
            message.setSubject("CarVoyage | Potvrda rezervacije vozila");

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Poštovani, \n\n");
            emailContent.append("Zahvaljujemo vam što ste odabrali CarVoyage za vašu rent-a-car uslugu.\n");
            emailContent.append("S velikim zadovoljstvom potvrđujemo vašu rezervaciju vozila.\n\n");
            emailContent.append("Detalji vaše rezervacije:\n");
            emailContent.append("Vozilo: ").append(session.getMetadata().get("car_name")).append("\n");
            emailContent.append("Početak najma: ").append(startDate.toString()).append("\n");
            emailContent.append("Kraj najma: ").append(endDate.toString()).append("\n");
            emailContent.append("Ukupno plaćeno: ").append(String.format("%.2f", totalAmount)).append(" ").append(session.getCurrency().toUpperCase()).append("\n\n");
            emailContent.append("Hvala vam još jednom i želimo vam sigurno i ugodno putovanje!\n");
            emailContent.append("Srdačno,\nCarVoyage tim");

            message.setText(emailContent.toString());

            mailSender.send(message);

            System.out.println("Email s potvrdom uspješno poslan korisniku: " + customerEmail);

        } catch (Exception e) {
            System.err.println("Greška prilikom slanja emaila: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
}
