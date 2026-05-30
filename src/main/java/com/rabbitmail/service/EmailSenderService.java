package com.rabbitmail.service;

import com.rabbitmail.entity.EmailMessage;
import com.rabbitmail.entity.Recipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${rabbitmail.mail.simulation-enabled:true}")
    private boolean simulationEnabled;

    public void send(Recipient recipient, EmailMessage emailMessage) {
        if (simulationEnabled) {
            log.info("Simulated email sent to {} with subject '{}'", recipient.getEmail(), emailMessage.getSubject());
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(recipient.getEmail());
        mailMessage.setSubject(emailMessage.getSubject());
        mailMessage.setText(emailMessage.getContent());

        javaMailSender.send(mailMessage);
        log.info("Email sent to {} with subject '{}'", recipient.getEmail(), emailMessage.getSubject());
    }
}
