package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartaoServicePort {

    void processarSolicitacao(ClienteContaCriadoDTO dto);

    CartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto);

    Page<CartaoResponseDTO> buscarPorCliente(Long idCliente, Pageable pageable);

    CartaoResponseDTO cadastrarCartaoExistente(CadastrarCartaoExistenteRequestDTO dto);

    CartaoResponseDTO alterarStatus(AlterarStatusRequestDTO dto);

    Page<CartaoResponseDTO> listarTodos(Pageable pageable);

}
