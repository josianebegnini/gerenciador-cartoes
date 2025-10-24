package com.example.gw_gerenciador_cartoes.application.dto;

public class CriarCartaoMessageDTO {
    private Long clienteId;
    private Long contaId;
    private String nome;
    private String cpf;
    private String email;
    private String categoriaCartao;
    private String tipoCartao;

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

    public String getCategoriaCartao() {
        return categoriaCartao;
    }

    public void setCategoriaCartao(String categoriaCartao) {
        this.categoriaCartao = categoriaCartao;
    }

    public String getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(String tipoCartao) {
        this.tipoCartao = tipoCartao;
    }
}
