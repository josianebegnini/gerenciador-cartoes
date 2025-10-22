package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;

public interface CartaoRepositoryPort {
    void salvar(Cartao cartao);
}
