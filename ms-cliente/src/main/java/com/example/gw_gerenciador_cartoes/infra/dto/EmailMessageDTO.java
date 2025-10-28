package com.example.gw_gerenciador_cartoes.infra.dto;

import java.io.Serializable;
import java.util.Map;

public class EmailMessageDTO  implements Serializable {
        private static final long serialVersionUID = 1L;

        private String tipo;
        private String email;
        private String nome;
        private Map<String, Object> dados;


    public EmailMessageDTO(String tipo, String email, String nome, Map<String, Object> dados) {
        this.tipo = tipo;
        this.email = email;
        this.nome = nome;
        this.dados = dados;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Map<String, Object> getDados() {
        return dados;
    }

    public void setDados(Map<String, Object> dados) {
        this.dados = dados;
    }
}