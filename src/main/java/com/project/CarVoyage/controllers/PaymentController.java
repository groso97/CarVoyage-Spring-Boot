package com.project.CarVoyage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.LineItemCollection;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.Session.CustomerDetails;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionListLineItemsParams;

@Controller
public class PaymentController {

    @Value("${stripe.webhook.secret}")
    private String stripeWebhook;

    @Value("${spring.mail.username}")
    private String emailVlasnika;

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

            if (customerEmail != null) {
                try {
                    LineItemCollection lineItems = Session.retrieve(session.getId())
                            .listLineItems(SessionListLineItemsParams.builder().setLimit(5L).build());

                    StringBuilder emailContent = new StringBuilder(
                            "Hvala Vam na vašoj kupnji kod Pčelarstvo Pavlović! Ovime potvrđujemo primitak Vaše narudžbe.\n\nDetalji narudžbe:\n");
                    lineItems.getData().forEach(item -> {
                        emailContent.append(item.getDescription())
                                .append(" - Količina: ")
                                .append(item.getQuantity())
                                .append("\n");
                    });

                    emailContent.append("\nUkupno plaćeno: ")
                            .append(String.format("%.2f", session.getAmountTotal() / 100.0)).append(" ")
                            .append(session.getCurrency().toUpperCase());
                    emailContent.append(
                            "\nAko imate bilo kakvih pitanja ili trebate dodatne informacije, slobodno nas kontaktirajte na ")
                            .append(emailVlasnika).append(" ili putem telefona na broj +385 99 799 4174.");
                    emailContent.append(
                            "\n\nSrdačno,\nOPG Pčelarstvo Pavlović\nKotromanića 9, 21300 Makarska\nMIBPG: 287512\nR-11267");

                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(emailVlasnika);
                    message.setTo(customerEmail);
                    message.setSubject("Pčelarstvo Pavlović | Potvrda o kupnji");
                    message.setText(emailContent.toString());
                    mailSender.send(message);

                } catch (Exception e) {
                    System.err.println("Error sending email: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
