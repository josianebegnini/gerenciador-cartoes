package com.example.gw_gerenciador_cartoes.infra.adapter.util;

import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.model.Conta;
import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
import com.example.gw_gerenciador_cartoes.infra.dto.ClienteContaCriadoDTO;
import com.example.gw_gerenciador_cartoes.infra.dto.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.infra.entity.ClienteEntity;
import com.example.gw_gerenciador_cartoes.infra.entity.ContaEntity;
import com.example.gw_gerenciador_cartoes.infra.entity.EnderecoEntity;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;

import java.time.LocalDate;
import java.util.Map;

public class UtilsMapper {

    private UtilsMapper() {
    }

    public static Cliente fromEntityToDomain(ClienteEntity entity) {
        if (entity == null) return null;

        return new Cliente(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getDataNasc(),
                entity.getCpf(),
                fromEntityToDomain(entity.getEndereco()),
                fromEntityToDomain(entity.getConta())
        );
    }

    public static Endereco fromEntityToDomain(EnderecoEntity entity) {
        if (entity == null) return null;

        return new Endereco(
                entity.getCidade(),
                entity.getBairro(),
                entity.getRua(),
                entity.getCep(),
                entity.getComplemento(),
                entity.getNumero()
        );
    }

    public static Conta fromEntityToDomain(ContaEntity entity) {
        if (entity == null) return null;

        return new Conta(
                entity.getId(),
                entity.getAgencia(),
                entity.getTipo(),
                entity.getSaldo()
        );
    }

    public static ClienteEntity FromDomainToEntity(Cliente cliente) {
        if (cliente == null) return null;

        return new ClienteEntity(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getDataNasc(),
                cliente.getCpf(),
                FromDomainToEntity(cliente.getEndereco()),
                FromDomainToEntity(cliente.getConta())
        );
    }

    public static EnderecoEntity FromDomainToEntity(Endereco endereco) {
        if (endereco == null) return null;

        return new EnderecoEntity(
                endereco.getCidade(),
                endereco.getBairro(),
                endereco.getRua(),
                endereco.getCep(),
                endereco.getComplemento(),
                endereco.getNumero()
        );
    }

    public static ContaEntity FromDomainToEntity(Conta conta) {
        if (conta == null) return null;

        return new ContaEntity(
                conta.getId(),
                conta.getAgencia(),
                conta.getTipo(),
                conta.getSaldo()
        );
    }

    public static ClienteContaCriadoDTO fromClienteToClienteContaDto(Cliente cliente, TipoCartao tipoCartao, TipoEmissao tipoEmissao) {

        return new ClienteContaCriadoDTO(
                cliente.getId(),
                cliente.getConta().getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                tipoCartao,
                tipoEmissao
        );
    }

    public static EmailMessageDTO fromClienteToEmailMessageDTOContaCriada(Cliente cliente) {


        Map<String, Object> dados = Map.of(
                "dataCriacao", LocalDate.now().toString(),
                "acessoContaLink", "https://meusistema.com/minha-conta"
        );


        return new EmailMessageDTO(
                "conta-criada",
                cliente.getEmail(),
                cliente.getNome(),
                dados
        );
    }


}

