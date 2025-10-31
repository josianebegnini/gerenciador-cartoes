package com.example.gw_gerenciador_cartoes.infra.entity;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissao;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_cartao")
public class SolicitacaoCartaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "cliente_id", nullable = true)
    private Long clienteId;

    @Column(name = "conta_id", nullable = true)
    private Long contaId;

    @Column(name = "cartao_id", nullable = true)
    private Long cartaoId;

    @Column(name = "nome", nullable = true)
    private String nome;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "motivo_rejeicao", nullable = true)
    private String motivoRejeicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private StatusSolicitacao status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cartao", nullable = true)
    private TipoCartao tipoCartao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_emissao", nullable = true)
    private TipoEmissao tipoEmissao;

    @Column(name = "data_solicitacao", nullable = true)
    private LocalDateTime dataSolicitacao;

    @Column(name = "ultima_data_processamento", nullable = true)
    private LocalDateTime ultimaDataProcessamento;

    @Lob
    @Column(name = "mensagem_solicitacao", columnDefinition = "TEXT", nullable = true)
    private String mensagemSolicitacao;

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

    public Long getCartaoId() {
        return cartaoId;
    }

    public void setCartaoId(Long cartaoId) {
        this.cartaoId = cartaoId;
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

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
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

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getUltimaDataProcessamento() {
        return ultimaDataProcessamento;
    }

    public void setUltimaDataProcessamento(LocalDateTime ultimaDataProcessamento) {
        this.ultimaDataProcessamento = ultimaDataProcessamento;
    }

    public String getMensagemSolicitacao() {
        return mensagemSolicitacao;
    }

    public void setMensagemSolicitacao(String mensagemSolicitacao) {
        this.mensagemSolicitacao = mensagemSolicitacao;
    }
}
