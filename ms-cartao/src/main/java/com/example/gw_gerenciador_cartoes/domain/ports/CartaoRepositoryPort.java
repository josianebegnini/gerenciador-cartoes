package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CartaoRepositoryPort {
    Cartao salvar(Cartao cartao);
    Optional<Cartao> atualizar(Cartao cartao);
    Optional<Cartao> buscarPorNumeroECvv(String numero, String cvv);
    boolean existePorNumero(String numero);
    Page<Cartao> buscarPorIdCliente(Long idCliente, Pageable pageable);
    Optional<Cartao> atualizarStatus(Long cartaoId, StatusCartao novoStatus, String motivo);
}
