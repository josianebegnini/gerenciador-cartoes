

package com.example.gw_gerenciador_cartoes.infra.adapter.util;


import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
import com.example.gw_gerenciador_cartoes.infra.entity.ClienteEntity;
import com.example.gw_gerenciador_cartoes.infra.entity.EnderecoEntity;

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
            entity.getCPF(),
            fromEntityToDomain(entity.getEndereco())
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

    public static ClienteEntity FromDomainToEntity(Cliente cliente) {
        if (cliente == null) return null;

        return new ClienteEntity(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getDataNasc(),
                cliente.getCPF(),
                FromDomainToEntity(cliente.getEndereco())
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
}





