



package com.example.gw_gerenciador_cartoes.infra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "contas")
public class ContaEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Agência é obrigatória")
    @Size(max = 10, message = "Agência deve ter no máximo 10 caracteres")
    private String agencia;

    @NotBlank(message = "Conta é obrigatória")
    @Size(max = 20, message = "Conta deve ter no máximo 20 caracteres")
    private String conta;

    @NotNull(message = "Saldo é obrigatório")
    @Digits(integer = 15, fraction = 4, message = "Saldo deve ter até 15 dígitos inteiros e 4 decimais")
    @Column(precision = 19, scale = 4)
    private BigDecimal saldo;



    public ContaEntity(String agencia, String conta, BigDecimal saldo) {
        this.agencia = agencia;
        this.conta = conta;
        this.saldo = saldo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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
