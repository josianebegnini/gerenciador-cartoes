package com.example.gw_gerenciador_cartoes.domain.model;

import java.math.BigDecimal;

public class Conta {

    private Long id;
    private String agencia;
    private String tipo;
    private BigDecimal saldo;

    public Conta(Long id, String agencia, String tipo, BigDecimal saldo) {
        this.id = id;
        this.agencia = agencia;
        this.tipo = tipo;
        this.saldo = saldo;
    }

    public Conta() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
