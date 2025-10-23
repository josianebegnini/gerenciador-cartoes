package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente);

    List<Cliente> findAll();

    Page<Cliente> findAll(Pageable pageable, String cpf, String nome);

    Optional<Cliente> findById(Long id);

    void deleteById(Long id);
}
