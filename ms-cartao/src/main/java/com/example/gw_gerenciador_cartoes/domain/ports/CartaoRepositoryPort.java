package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;

import java.util.Optional;

public interface CartaoRepositoryPort {
    Optional<Cartao> salvar(Cartao cartao);
    Optional<Cartao> buscarPorId(Long id);
    Optional<Cartao> atualizar(Cartao cartao);
    boolean existePorNumero(String numero);
}
