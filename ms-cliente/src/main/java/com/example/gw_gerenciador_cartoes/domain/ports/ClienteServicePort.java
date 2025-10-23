package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ClienteServicePort {
    Cliente criarCliente(Cliente cliente);

    List<Cliente> listarClientes();

    void deletarCliente(Long id);

    Page<Cliente> listarClientes(int page, int size, String sortBy, String direction, String nome, String cpf);
}
