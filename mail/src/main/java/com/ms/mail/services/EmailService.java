package com.ms.mail.services;

import com.ms.mail.dtos.EmailMessageDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarEmail(EmailMessageDTO dto) {
        try {
            // Gera o corpo do e-mail usando Thymeleaf + base.html
            String corpo = gerarCorpoEmail(dto.getTipo(), dto.getNome(), dto.getDados());
            // Gera título baseado no tipo
            String titulo = gerarTituloEmail(dto.getTipo());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(dto.getEmail());
            helper.setSubject(titulo);
            helper.setText(corpo, true); // HTML
            helper.setFrom(((JavaMailSenderImpl) mailSender).getUsername());
            mailSender.send(message);
            System.out.println("📩 E-mail enviado com sucesso para: " + dto.getEmail() + " | Tipo: " + dto.getTipo());
        } catch (MessagingException e) {
            System.err.println("❌ Falha ao enviar e-mail para: " + dto.getEmail() + " | Tipo: " + dto.getTipo() + " | Erro: " + e.getMessage());
        }
    }

    private String gerarCorpoEmail(String tipo, String nome, Map<String, Object> dados) {
        Context context = new Context();

        // Adiciona variáveis dinâmicas do DTO
        if (dados != null) {
            dados.forEach(context::setVariable);
        }
        context.setVariable("nome", nome);

        // Define fragmento de conteúdo e título para base.html
        context.setVariable("conteudo", tipo != null ? tipo : "default");
        context.setVariable("titulo", gerarTituloEmail(tipo));

        // Processa template base.html
        return templateEngine.process("base", context);
    }

    private String gerarTituloEmail(String tipo) {
        return switch (tipo) {
            case "conta-criada" -> "Sua conta foi criada com sucesso!";
            case "cartao-criado" -> "Cartão criado!";
            case "cartao-ativo" -> "Seu cartão está ativo!";
            case "cartao-bloqueado" -> "Seu cartão foi bloqueado!";
            case "segunda-via" -> "Segunda via do seu cartão";
            case "recuperacao-senha" -> "Recuperação de senha";
            default -> "Notificação";
        };
    }
}
