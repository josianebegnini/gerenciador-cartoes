package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissao;

public class ClienteContaCriadoDTO {
    private Long clienteId;
    private Long contaId;
    private String nome;
    private String cpf;
    private String email;
    private TipoCartao tipoCartao;
    private TipoEmissao tipoEmissao;

    public ClienteContaCriadoDTO() {
    }

    public ClienteContaCriadoDTO(Long clienteId, Long contaId, String nome, String cpf, String email, TipoCartao tipoCartao, TipoEmissao tipoEmissao) {
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

    public TipoCartao getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartao tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public TipoEmissao getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(TipoEmissao tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }
}
