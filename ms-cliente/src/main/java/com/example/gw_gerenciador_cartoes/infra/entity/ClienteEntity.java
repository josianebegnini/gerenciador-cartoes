package com.example.gw_gerenciador_cartoes.infra.entity;

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

    public ClienteEntity() {}

    public ClienteEntity(Long id, String nome, String email, String dataNasc, String CPF) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNasc = dataNasc;
        this.CPF = CPF;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getDataNasc() {return dataNasc;}
    public String getCPF() {return CPF;}
}