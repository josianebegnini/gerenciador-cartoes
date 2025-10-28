package com.example.gw_gerenciador_cartoes.infra.messaging;

import java.util.Objects;

public class SolicitacaoCartaoMessage {
    private Long clienteId;
    private Long contaId;
    private String nome;
    private String cpf;
    private String email;
    private String tipoCartao;
    private String tipoEmissao;

    public SolicitacaoCartaoMessage() {
    }

    public SolicitacaoCartaoMessage(Long clienteId, Long contaId, String nome, String cpf, String email, String tipoCartao, String tipoEmissao) {
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.tipoCartao = tipoCartao;
        this.tipoEmissao = tipoEmissao;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(String tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public String getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(String tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitacaoCartaoMessage that = (SolicitacaoCartaoMessage) o;
        return Objects.equals(clienteId, that.clienteId) && Objects.equals(contaId, that.contaId) && Objects.equals(nome, that.nome) && Objects.equals(cpf, that.cpf) && Objects.equals(email, that.email) && Objects.equals(tipoCartao, that.tipoCartao) && Objects.equals(tipoEmissao, that.tipoEmissao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteId, contaId, nome, cpf, email, tipoCartao, tipoEmissao);
    }

    @Override
    public String toString() {
        return "SolicitacaoCartaoMessage{" +
                "clienteId=" + clienteId +
                ", contaId=" + contaId +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", tipoCartao='" + tipoCartao + '\'' +
                ", tipoEmissao='" + tipoEmissao + '\'' +
                '}';
    }
}
