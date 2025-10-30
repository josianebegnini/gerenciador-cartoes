package com.example.gw_gerenciador_cartoes.infra.entity;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "solicitacao_id", nullable = false)
    private Long solicitacaoId;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "numero", nullable = true, unique = false)
    private String numero;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDateTime dataVencimento;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCartao status;

    @Column(name = "motivo_status")
    private String motivoStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cartao", nullable = false)
    private TipoCartao tipoCartao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_emissao_cartao", nullable = false)
    private TipoEmissaoCartao tipoEmissao;

    @Column(name = "limite", nullable = true)
    private BigDecimal limite;

    public CartaoEntity() {
    }

    public CartaoEntity(Long id, Long clienteId, Long contaId, Long solicitacaoId, String nome, String email, String numero, String cvv, LocalDateTime dataVencimento, LocalDateTime dataCriacao, StatusCartao status, String motivoStatus, TipoCartao tipoCartao, TipoEmissaoCartao tipoEmissao, BigDecimal limite) {
        this.id = id;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.solicitacaoId = solicitacaoId;
        this.nome = nome;
        this.email = email;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.motivoStatus = motivoStatus;
        this.tipoCartao = tipoCartao;
        this.tipoEmissao = tipoEmissao;
        this.limite = limite;
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

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
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

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }
}
