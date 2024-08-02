package com.project.CarVoyage.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;
    private final ExecutorService executorService;

    public EmailService(JavaMailSender mailSender) {
        this.emailSender = mailSender;
        this.executorService = Executors.newFixedThreadPool(10); // You can adjust the pool size as needed
    }

    @Value("${spring.mail.username}")
    private String sender;

    public void sendMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }

    public void sendForgottenPasswordEmail(String to, String resetLink) {
        execute(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Zaboravljena lozinka - CarVoyage");
            message.setText("Odaberite poveznicu ispod kako biste poništili svoju lozinku:\n" + resetLink);
            emailSender.send(message);
        });
    }

    private void execute(Runnable task) {
        executorService.execute(task);
    }
}
