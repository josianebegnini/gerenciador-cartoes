package com.example.gw_gerenciador_cartoes.infra.entity;

import com.example.gw_gerenciador_cartoes.domain.enums.CategoriaCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_cartao", nullable = false)
    private CategoriaCartao categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCartao status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cartao", nullable = false)
    private TipoCartao tipo;

    @Column(name = "motivo_segunda_via")
    private String motivoSegundaVia;

    public CartaoEntity() {
    }

    public CartaoEntity(Long id, Long clienteId, String numero, String cvv, LocalDate dataVencimento, CategoriaCartao categoria, StatusCartao status, TipoCartao tipo, String motivoSegundaVia) {
        this.id = id;
        this.clienteId = clienteId;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.categoria = categoria;
        this.status = status;
        this.tipo = tipo;
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

    public CategoriaCartao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCartao categoria) {
        this.categoria = categoria;
    }

    public StatusCartao getStatus() {
        return status;
    }

    public void setStatus(StatusCartao status) {
        this.status = status;
    }

    public TipoCartao getTipo() {
        return tipo;
    }

    public void setTipo(TipoCartao tipo) {
        this.tipo = tipo;
    }

    public String getMotivoSegundaVia() {
        return motivoSegundaVia;
    }

    public void setMotivoSegundaVia(String motivoSegundaVia) {
        this.motivoSegundaVia = motivoSegundaVia;
    }

}
