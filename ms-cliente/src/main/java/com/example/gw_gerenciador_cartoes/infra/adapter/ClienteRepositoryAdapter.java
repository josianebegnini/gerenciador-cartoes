package com.example.gw_gerenciador_cartoes.infra.adapter;


import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
import com.example.gw_gerenciador_cartoes.domain.ports.ClienteRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.adapter.util.UtilsMapper;
import com.example.gw_gerenciador_cartoes.infra.entity.ClienteEntity;
import com.example.gw_gerenciador_cartoes.infra.entity.EnderecoEntity;
import com.example.gw_gerenciador_cartoes.infra.repository.ClienteJpaRepository;
import com.example.gw_gerenciador_cartoes.infra.repository.EnderecoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gw_gerenciador_cartoes.infra.adapter.util.UtilsMapper.*;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final EnderecoJpaRepository enderecoJpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository clienteJpaRepository, EnderecoJpaRepository enderecoJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
        this.enderecoJpaRepository = enderecoJpaRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        Endereco endereco = cliente.getEndereco();
        EnderecoEntity enderecoEntity = UtilsMapper.FromDomainToEntity(endereco);
        enderecoEntity = enderecoJpaRepository.save(enderecoEntity);

        ClienteEntity clienteEntity = FromDomainToEntity(cliente);
        clienteEntity.setEndereco(enderecoEntity);
        return fromEntityToDomain(clienteJpaRepository.save(clienteEntity));

    }

    @Override
    public List<Cliente> findAll() {
        return clienteJpaRepository.findAll()
                .stream()
                .map(UtilsMapper::fromEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteJpaRepository.findAll(pageable)
                .map(UtilsMapper::fromEntityToDomain);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteJpaRepository.findById(id).map(UtilsMapper::fromEntityToDomain);
    }

    @Override
    public void deleteById(Long id) {
        clienteJpaRepository.deleteById(id);
    }

}
