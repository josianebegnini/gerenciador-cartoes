package com.ms.mail.controllers;

import com.ms.mail.dtos.EmailMessageDTO;
import com.ms.mail.services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test-email")
public class TestEmailController {

    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public String enviarTeste() throws MessagingException {
        EmailMessageDTO msg = new EmailMessageDTO();
        msg.setTipo("cartao-ativo");
        msg.setEmail("josianebegnini@gmail.com");
        msg.setNome("Josi");
        msg.setDados(Map.of("numeroCartao", "**** 1234", "dataAtivacao", "2025-10-24"));
        emailService.enviarEmail(msg);
        return "E-mail de teste enviado!";
    }
}