package com.example.gw_gerenciador_cartoes.application.dto;

import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;

import java.time.LocalDate;
import java.util.Objects;

public class CartaoResponseDTO {
    private Long id;
    private Long clienteId;
    private Long contaId;
    private String numero;
    private String cvv;
    private LocalDate dataVencimento;
    private StatusCartao status;
    private TipoCartao tipoCartao;
    private TipoEmissaoCartao tipoEmissaoCartao;
    private String motivoSegundaVia;

    public CartaoResponseDTO() {
    }

    public CartaoResponseDTO(Long id, Long clienteId, Long contaId, String numero, String cvv, LocalDate dataVencimento, StatusCartao status, TipoCartao tipoCartao, TipoEmissaoCartao tipoEmissaoCartao, String motivoSegundaVia) {
        this.id = id;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.tipoCartao = tipoCartao;
        this.tipoEmissaoCartao = tipoEmissaoCartao;
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

    public TipoEmissaoCartao getTipoEmissaoCartao() {
        return tipoEmissaoCartao;
    }

    public void setTipoEmissaoCartao(TipoEmissaoCartao tipoEmissaoCartao) {
        this.tipoEmissaoCartao = tipoEmissaoCartao;
    }

    public String getMotivoSegundaVia() {
        return motivoSegundaVia;
    }

    public void setMotivoSegundaVia(String motivoSegundaVia) {
        this.motivoSegundaVia = motivoSegundaVia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaoResponseDTO that = (CartaoResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(clienteId, that.clienteId) && Objects.equals(contaId, that.contaId) && Objects.equals(numero, that.numero) && Objects.equals(cvv, that.cvv) && Objects.equals(dataVencimento, that.dataVencimento) && status == that.status && tipoCartao == that.tipoCartao && tipoEmissaoCartao == that.tipoEmissaoCartao && Objects.equals(motivoSegundaVia, that.motivoSegundaVia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clienteId, contaId, numero, cvv, dataVencimento, status, tipoCartao, tipoEmissaoCartao, motivoSegundaVia);
    }

    @Override
    public String toString() {
        return "CartaoResponseDTO{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", contaId=" + contaId +
                ", numero='" + numero + '\'' +
                ", cvv='" + cvv + '\'' +
                ", dataVencimento=" + dataVencimento +
                ", status=" + status +
                ", tipoCartao=" + tipoCartao +
                ", tipoEmissaoCartao=" + tipoEmissaoCartao +
                ", motivoSegundaVia='" + motivoSegundaVia + '\'' +
                '}';
    }
}
