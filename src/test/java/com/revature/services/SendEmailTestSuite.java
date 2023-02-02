package com.revature.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SendEmailTestSuite {
    @InjectMocks
    SendEmailService sendEmailService;

    @Mock
    JavaMailSender javaMailSender;

    @DisplayName("Test send email")
    @Test
    public void testSendEmail() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreplycongo.revature@gmail.com");
        mailMessage.setTo("to");
        mailMessage.setSubject("subject");
        mailMessage.setText("text");

        doNothing().when(javaMailSender).send(mailMessage);

        sendEmailService.sendEmail("to", "subject", "text");

        verify(javaMailSender, times(1)).send(mailMessage);
        verifyNoMoreInteractions(javaMailSender);
    }
}
