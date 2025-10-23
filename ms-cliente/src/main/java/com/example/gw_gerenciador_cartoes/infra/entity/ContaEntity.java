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


    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;


    public ContaEntity(Long id, String agencia, String conta) {
        this.id = id;
        this.agencia = agencia;
        this.conta = conta;
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


}