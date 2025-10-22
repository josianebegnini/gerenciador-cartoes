package com.example.gw_gerenciador_cartoes.domain.ports;

public interface CartaoServicePort {
    void gerarCartao(String clienteId, String contaId);
}
