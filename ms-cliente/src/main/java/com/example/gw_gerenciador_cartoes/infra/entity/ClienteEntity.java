package com.example.gw_gerenciador_cartoes.infra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class ClienteEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Data de nascimento é obrigatória")
    private String dataNasc;

    @NotBlank(message = "cpf é obrigatório")
    private String cpf;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private EnderecoEntity endereco;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "conta_id", referencedColumnName = "id")
    private ContaEntity conta;


    public ClienteEntity() {
    }

    public ClienteEntity(Long id, String nome, String email, String dataNasc, String cpf, EnderecoEntity endereco, ContaEntity conta) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNasc = dataNasc;
        this.cpf = cpf;
        this.endereco = endereco;
        this.conta = conta;
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

    public String getCpf() {
        return cpf;
    }

    public EnderecoEntity getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoEntity endereco) {
        this.endereco = endereco;
    }

    public ContaEntity getConta() {
        return conta;
    }

    public void setConta(ContaEntity conta) {
        this.conta = conta;
    }
}






