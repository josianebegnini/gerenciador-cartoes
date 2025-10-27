package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.infra.dto.ClienteContaCriadoDTO;

public interface CriarCartaoProducerPort {
    void sendCriarCartao(ClienteContaCriadoDTO clienteContaCriadoDTO);
}
