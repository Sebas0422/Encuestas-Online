package com.example.encuestas_api.notifications.infrastructure.adapter.out.sender;

import com.example.encuestas_api.notifications.application.port.out.SendEmailPort;
import com.example.encuestas_api.notifications.domain.valueobject.Message;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderAdapter implements SendEmailPort {

    private final JavaMailSender mailSender;

    @Value("${notifications.mail.from:no-reply@example.com}")
    private String defaultFrom;

    public EmailSenderAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Recipient recipientEmail, Message message) throws Exception {
        if (recipientEmail == null || recipientEmail.getEmail() == null) {
            throw new IllegalArgumentException("Recipient email requerido");
        }
        SimpleMailMessage mm = new SimpleMailMessage();
        mm.setFrom(defaultFrom);
        mm.setTo(recipientEmail.getEmail());
        mm.setSubject(message.getSubject());
        mm.setText(message.getBody());
        mailSender.send(mm);
    }
}
