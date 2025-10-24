package com.example.gw_gerenciador_cartoes.domain.model;

import com.example.gw_gerenciador_cartoes.domain.enums.CategoriaCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;

import java.time.LocalDate;

public class Cartao {

    private Long id;
    private Long clienteId;
    private Long contaId;
    private String numero;
    private String cvv;
    private LocalDate dataVencimento;
    private CategoriaCartao categoriaCartao;
    private StatusCartao status;
    private TipoCartao tipoCartao;
    private String motivoSegundaVia;

    public Cartao() {
    }

    public Cartao(Long id, Long clienteId, Long contaId, String numero, String cvv, LocalDate dataVencimento, CategoriaCartao categoriaCartao, StatusCartao status, TipoCartao tipoCartao, String motivoSegundaVia) {
        this.id = id;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.categoriaCartao = categoriaCartao;
        this.status = status;
        this.tipoCartao = tipoCartao;
        this.motivoSegundaVia = motivoSegundaVia;
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

    public CategoriaCartao getCategoriaCartao() {
        return categoriaCartao;
    }

    public void setCategoriaCartao(CategoriaCartao categoriaCartao) {
        this.categoriaCartao = categoriaCartao;
    }

    public StatusCartao getStatus() {
        return status;
    }

    public void setStatus(StatusCartao status) {
        this.status = status;
    }

    public TipoCartao getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartao tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public String getMotivoSegundaVia() {
        return motivoSegundaVia;
    }

    public void setMotivoSegundaVia(String motivoSegundaVia) {
        this.motivoSegundaVia = motivoSegundaVia;
    }
}
