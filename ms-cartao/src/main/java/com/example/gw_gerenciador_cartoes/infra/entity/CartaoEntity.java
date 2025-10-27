package com.example.gw_gerenciador_cartoes.infra.entity;

import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;
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

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Column(name = "numero", nullable = true, unique = true)
    private String numero;

    @Column(name = "cvv", nullable = true)
    private String cvv;

    @Column(name = "data_vencimento", nullable = true)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCartao status;

    @Column(name = "motivo_status")
    private String motivoStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cartao", nullable = true)
    private TipoCartao tipoCartao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_emissao_cartao", nullable = true)
    private TipoEmissaoCartao tipoEmissao;

    public CartaoEntity() {
    }

    public CartaoEntity(Long id, Long clienteId, Long contaId, String numero, String cvv, LocalDate dataVencimento, StatusCartao status, String motivoStatus, TipoCartao tipoCartao, TipoEmissaoCartao tipoEmissao) {
        this.id = id;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.motivoStatus = motivoStatus;
        this.tipoCartao = tipoCartao;
        this.tipoEmissao = tipoEmissao;
    }

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

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
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

    public StatusCartao getStatus() {
        return status;
    }

    public void setStatus(StatusCartao status) {
        this.status = status;
    }

    public String getMotivoStatus() {
        return motivoStatus;
    }

    public void setMotivoStatus(String motivoStatus) {
        this.motivoStatus = motivoStatus;
    }

    public TipoCartao getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartao tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public TipoEmissaoCartao getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(TipoEmissaoCartao tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }
}
