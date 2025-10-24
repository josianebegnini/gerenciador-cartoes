package com.ms.mail.services;

import com.ms.mail.dtos.EmailMessageDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(EmailMessageDTO dto) {
        try {
            String corpo = gerarCorpoEmail(dto.getTipo(), dto.getNome(), dto.getDados());
            String titulo = gerarTituloEmail(dto.getTipo());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(dto.getEmail());
            helper.setSubject(titulo);
            helper.setText(corpo, true); // true = HTML
            helper.setFrom("josianebegnini@gmail.com");

            mailSender.send(message);
            System.out.println("📩 E-mail enviado com sucesso para: " + dto.getEmail() + " | Tipo: " + dto.getTipo());
        } catch (MessagingException | IOException e) {
            System.err.println("❌ Falha ao enviar e-mail para: " + dto.getEmail() + " | Tipo: " + dto.getTipo() + " | Erro: " + e.getMessage());
        }
    }

    private String gerarCorpoEmail(String tipo, String nome, Map<String, Object> dados) throws IOException {
        String template = tipo;
        ClassPathResource resource = new ClassPathResource("templates/" + template + ".html");
        if (!resource.exists()) {
            resource = new ClassPathResource("templates/default.html");
        }

        String conteudo;
        try (InputStream is = resource.getInputStream()) {
            conteudo = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        if (dados != null) {
            for (Map.Entry<String, Object> entry : dados.entrySet()) {
                conteudo = conteudo.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }
        }
        if (nome != null) {
            conteudo = conteudo.replace("{{nome}}", nome);
        }
        return conteudo;
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
