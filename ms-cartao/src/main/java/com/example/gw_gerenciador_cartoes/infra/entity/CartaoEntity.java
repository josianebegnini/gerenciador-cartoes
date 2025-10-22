package com.example.gw_gerenciador_cartoes.infra.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cartao")
public class CartaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "numero", nullable = false, unique = true)
    private String numero;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "tipo_conta", nullable = false)
    private String tipoConta = "CONTA";

    @Column(name = "status", nullable = false)
    private String status = "DESATIVADO";

    @Column(name = "formato_cartao", nullable = false)
    private String formatoCartao = "FISICO";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormatoCartao() {
        return formatoCartao;
    }

    public void setFormatoCartao(String formatoCartao) {
        this.formatoCartao = formatoCartao;
    }
}
