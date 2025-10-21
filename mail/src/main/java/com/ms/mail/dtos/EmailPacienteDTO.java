package com.ms.mail.dtos;

import com.ms.mail.enums.StatusAgenda;
import com.ms.mail.enums.TipoConsulta;

import java.time.LocalDateTime;

public class EmailPacienteDTO {
    private Long id;
    private String pacienteNome;
    private String medicoNome;
    private String pacienteEmail;
    private LocalDateTime dataHora;
    private TipoConsulta tipoConsulta;
    private StatusAgenda status;
    private String assunto;
    private String mensagem;

    public EmailPacienteDTO() {
    }

    public EmailPacienteDTO(Long id, String pacienteNome, String pacienteEmail, String medicoNome, LocalDateTime dataHora, TipoConsulta tipoConsulta, StatusAgenda status, String assunto, String mensagem) {
        this.id = id;
        this.pacienteNome = pacienteNome;
        this.pacienteEmail = pacienteEmail;
        this.medicoNome = medicoNome;
        this.dataHora = dataHora;
        this.tipoConsulta = tipoConsulta;
        this.status = status;
        this.assunto = assunto;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public void setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
    }

    public String getMedicoNome() {
        return medicoNome;
    }

    public void setMedicoNome(String medicoNome) {
        this.medicoNome = medicoNome;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public StatusAgenda getStatus() {
        return status;
    }

    public void setStatus(StatusAgenda status) {
        this.status = status;
    }

    public String getPacienteEmail() {
        return pacienteEmail;
    }

    public void setPacienteEmail(String pacienteEmail) {
        this.pacienteEmail = pacienteEmail;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String pacienteNome;
        private String pacienteEmail;
        private String medicoNome;
        private LocalDateTime dataHora;
        private TipoConsulta tipoConsulta;
        private StatusAgenda status;
        private String assunto;
        private String mensagem;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder pacienteNome(String pacienteNome) {
            this.pacienteNome = pacienteNome;
            return this;
        }

        public Builder pacienteEmail(String pacienteEmail) {
            this.pacienteEmail = pacienteEmail;
            return this;
        }

        public Builder medicoNome(String medicoNome) {
            this.medicoNome = medicoNome;
            return this;
        }

        public Builder dataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
            return this;
        }

        public Builder tipoConsulta(TipoConsulta tipoConsulta) {
            this.tipoConsulta = tipoConsulta;
            return this;
        }

        public Builder status(StatusAgenda status) {
            this.status = status;
            return this;
        }
        public Builder assunto(String assunto) {
            this.assunto = assunto;
            return this;
        }
        public Builder mensagem(String mensagem) {
            this.mensagem = mensagem;
            return this;
        }

        public EmailPacienteDTO build() {
            return new EmailPacienteDTO(id, pacienteNome, pacienteEmail, medicoNome, dataHora, tipoConsulta, status, assunto, mensagem);
        }
    }
}
