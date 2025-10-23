package com.example.gw_gerenciador_cartoes.service;


import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.ports.ClienteRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.ClienteServicePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteServicePort {

    private final ClienteRepositoryPort clienteRepository;


    public ClienteServiceImpl(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;

    }

    @Override
    public Cliente criarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public void deletarCliente(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (!cliente.isPresent()) {
            throw new RuntimeException("Cliente bnao encontrado id " + id);
        }

    }

    public Page<Cliente> listarClientes(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return clienteRepository.findAll(pageable);
    }
}
