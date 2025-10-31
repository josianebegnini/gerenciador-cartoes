package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissao;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CadastrarCartaoExistenteRequestDTO {

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @NotBlank(message = "O número do cartão é obrigatório")
    @Size(min = 16, max = 16, message = "O número do cartão deve ter 16 dígitos")
    private String numero;

    @NotBlank(message = "O CVV é obrigatório")
    @Pattern(regexp = "\\d{3}", message = "O CVV deve conter exatamente 3 dígitos")
    private String cvv;

    @NotNull(message = "A data de vencimento é obrigatória")
    @Future(message = "A data de vencimento deve ser futura")
    private LocalDateTime dataVencimento;

    @NotNull(message = "O status do cartão é obrigatório")
    private StatusCartao status;

    @Size(max = 255, message = "O motivo do status deve ter no máximo 255 caracteres")
    private String motivoStatus;

    @NotNull(message = "O tipo de cartão é obrigatório")
    private TipoCartao tipoCartao;

    @NotNull(message = "O tipo de emissão é obrigatório")
    private TipoEmissao tipoEmissao;

    @NotNull(message = "O limite do cartão é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O limite deve ser maior que zero")
    private BigDecimal limite;

    public CadastrarCartaoExistenteRequestDTO() {
    }

    public CadastrarCartaoExistenteRequestDTO(Long clienteId, String numero, String cvv, LocalDateTime dataVencimento, StatusCartao status, String motivoStatus, TipoCartao tipoCartao, TipoEmissao tipoEmissao, BigDecimal limite) {
        this.clienteId = clienteId;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.motivoStatus = motivoStatus;
        this.tipoCartao = tipoCartao;
        this.tipoEmissao = tipoEmissao;
        this.limite = limite;
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

    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDateTime dataVencimento) {
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

    public TipoEmissao getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(TipoEmissao tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }
}
