package com.example.gw_gerenciador_cartoes.infra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "tipo é obrigatório")
    @Size(max = 20, message = "tipo deve ter no máximo 20 caracteres")
    private String tipo;


    @Column(precision = 19, scale = 4)
    @Digits(integer = 15, fraction = 4, message = "Saldo deve ter até 15 dígitos inteiros e 4 decimais")
    private BigDecimal saldo = BigDecimal.ZERO;



    public ContaEntity(Long id, String agencia, String tipo, BigDecimal saldo) {
        this.id = id;
        this.agencia = agencia;
        this.tipo = tipo;
        this.saldo = saldo != null ? saldo : BigDecimal.ZERO;
    }

    public ContaEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Long getId() {
        return id;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getTipo() {
        return tipo;
    }


}
