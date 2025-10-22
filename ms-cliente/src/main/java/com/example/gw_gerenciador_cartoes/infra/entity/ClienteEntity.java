package com.example.gw_gerenciador_cartoes.infra.entity;

import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String dataNasc;
    private String CPF;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private EnderecoEntity endereco;


    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private ContaEntity contas;


    public ClienteEntity() {
    }

    public ClienteEntity(Long id, String nome, String email, String dataNasc, String CPF, EnderecoEntity endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNasc = dataNasc;
        this.CPF = CPF;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getDataNasc() {
        return dataNasc;
    }

    public String getCPF() {
        return CPF;
    }

    public EnderecoEntity getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoEntity endereco) {
        this.endereco = endereco;
    }
}