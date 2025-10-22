package com.example.gw_gerenciador_cartoes.domain.ports;


import com.example.gw_gerenciador_cartoes.domain.model.Cliente;

import java.util.List;

public interface ClienteServicePort {
    Cliente criarCliente(Cliente cliente);

    List<Cliente> listarClientes();

    void deletarCliente(Long id);
}
