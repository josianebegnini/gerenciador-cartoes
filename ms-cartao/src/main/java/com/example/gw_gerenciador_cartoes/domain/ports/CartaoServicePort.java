package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartaoServicePort {

    void processarSolicitacao(ClienteContaCriadoDTO dto);

    CartaoInfoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto);

    Page<CartaoClienteResponseDTO> buscarPorCliente(Long idCliente, Pageable pageable);

    CartaoClienteResponseDTO cadastrarCartaoExistente(CadastrarCartaoExistenteRequestDTO dto);

    CartaoInfoResponseDTO alterarStatus(AlterarStatusRequestDTO dto);

    Page<CartaoResponseDTO> listarTodos(Pageable pageable);

}
