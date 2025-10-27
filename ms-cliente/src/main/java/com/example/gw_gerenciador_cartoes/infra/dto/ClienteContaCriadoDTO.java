package com.example.gw_gerenciador_cartoes.infra.dto;

import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import java.io.Serializable;

public class ClienteContaCriadoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long clienteId;
    private Long contaid;
    private String nome;
    private String cpf;
    private String email;
    private TipoCartao tipoCartao;
    private TipoEmissao tipoEmissao;

    public ClienteContaCriadoDTO(Long clienteId, Long contaid, String nome, String cpf, String email, TipoCartao tipoCartao, TipoEmissao tipoEmissao) {
        this.clienteId = clienteId;
        this.contaid = contaid;
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

    public Long getContaid() {
        return contaid;
    }

    public void setContaid(Long contaid) {
        this.contaid = contaid;
    }
}
