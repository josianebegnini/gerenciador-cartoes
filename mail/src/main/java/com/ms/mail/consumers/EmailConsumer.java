package com.ms.mail.consumers;

import com.ms.mail.dtos.EmailMessageDTO;
import com.ms.mail.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "email-normal-queue")
    public void listenNormalQueue(EmailMessageDTO message) {
        System.out.println("📩 Mensagem recebida da fila NORMAL: " + message);
        enviarEmailComLog(message);
    }

    @RabbitListener(queues = "email-alta-prioridade-queue")
    public void listenHighPriorityQueue(EmailMessageDTO message) {
        System.out.println("📩 Mensagem recebida da fila ALTA: " + message);
        enviarEmailComLog(message);
    }

    private void enviarEmailComLog(EmailMessageDTO message) {
        try {
            emailService.enviarEmail(message);
            System.out.println("✅ E-mail enviado com sucesso para: " + message.getEmail() +
                    " | Tipo: " + message.getTipo());
        } catch (Exception e) {
            System.err.println("❌ Falha ao enviar e-mail para: " + message.getEmail() +
                    " | Tipo: " + message.getTipo() +
                    " | Erro: " + e.getMessage());
            // Aqui você pode implementar envio para DLQ
        }
    }
}