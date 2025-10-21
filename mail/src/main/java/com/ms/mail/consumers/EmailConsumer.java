package com.ms.mail.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.mail.config.RabbitMQConfig;
import com.ms.mail.dtos.EmailPacienteDTO;
import com.ms.mail.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "agendamento.medico.queue")
    public void receberMensagem(EmailPacienteDTO dto) {
        System.out.println("📩 Mensagem recebida da fila: " + dto.getPacienteEmail());
        emailService.enviarEmail(dto);
    }
}
