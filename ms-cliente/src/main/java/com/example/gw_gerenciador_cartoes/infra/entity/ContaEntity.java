package com.example.gw_gerenciador_cartoes.infra.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contas")
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String agencia;
    private String conta;

    @Column(name = "cliente_id")
    private Long clienteId;

    public ContaEntity(Long id, String agencia, String conta, Long clienteId) {
        this.id = id;
        this.agencia = agencia;
        this.conta = conta;
        this.clienteId = clienteId;
    }

    public Long getId() {
        return id;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getConta() {
        return conta;
    }

    public Long getClienteId() {
        return clienteId;
    }
}