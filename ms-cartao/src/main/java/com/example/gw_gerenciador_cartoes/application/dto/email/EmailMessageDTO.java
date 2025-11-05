package com.example.gw_gerenciador_cartoes.application.dto.email;

import java.util.Map;

public class EmailMessageDTO {

    private String tipo;
    private String email;
    private String nome;
    private Map<String, String> dados;

    public EmailMessageDTO() {
    }

    public EmailMessageDTO(String tipo, String email, String nome, Map<String, String> dados) {
        this.tipo = tipo;
        this.email = email;
        this.nome = nome;
        this.dados = dados;
    }

    public Map<String, String> getDados() {
        return dados;
    }

    public void setDados(Map<String, String> dados) {
        this.dados = dados;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
