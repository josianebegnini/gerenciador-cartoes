package com.ms.mail.services;

import com.ms.mail.dtos.EmailPacienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmail(EmailPacienteDTO dto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(dto.getPacienteEmail());
            message.setSubject(dto.getAssunto());
            message.setText(dto.getMensagem());

            mailSender.send(message);

            System.out.println("✅ E-mail enviado para: " + dto.getPacienteEmail());
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
