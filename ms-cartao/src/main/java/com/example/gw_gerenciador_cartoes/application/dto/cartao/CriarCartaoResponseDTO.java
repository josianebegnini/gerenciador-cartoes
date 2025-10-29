package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import java.util.Objects;

public class CriarCartaoResponseDTO {
    private Long clienteId;
    private Long contaId;
    private boolean sucesso;
    private String mensagem;

    public CriarCartaoResponseDTO() {
    }

    public CriarCartaoResponseDTO(Long clienteId, Long contaId, boolean sucesso, String mensagem) {
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
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

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CriarCartaoResponseDTO that = (CriarCartaoResponseDTO) o;
        return sucesso == that.sucesso && Objects.equals(clienteId, that.clienteId) && Objects.equals(contaId, that.contaId) && Objects.equals(mensagem, that.mensagem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteId, contaId, sucesso, mensagem);
    }

    @Override
    public String toString() {
        return "CriarCartaoResponseDTO{" +
                "clienteId=" + clienteId +
                ", contaId=" + contaId +
                ", sucesso=" + sucesso +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}
