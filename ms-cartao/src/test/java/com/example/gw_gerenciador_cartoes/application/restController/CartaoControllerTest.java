package com.example.gw_gerenciador_cartoes.application.restController;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.AlterarStatusRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CadastrarCartaoExistenteRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoControllerTest {

    @Mock
    private CartaoServicePort cartaoService;

    @InjectMocks
    private CartaoController controller;

    @Test
    void deveAlterarStatusComSucesso() {
        AlterarStatusRequestDTO request = CartaoTestFactory.criarAlterarStatusRequestDTO();
        CartaoResponseDTO response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.alterarStatus(request)).thenReturn(response);

        ResponseEntity<CartaoResponseDTO> resposta = controller.alterarStatus(request);

        assertEquals(200, resposta.getStatusCodeValue());
        assertSame(response, resposta.getBody());
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getNumero()).isEqualTo(response.getNumero());

        verify(cartaoService, times(1)).alterarStatus(request);
        verifyNoMoreInteractions(cartaoService);
    }

    @Test
    void deveSolicitarSegundaViaComSucesso() {
        SegundaViaCartaoRequestDTO request = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();
        CartaoResponseDTO response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.solicitarSegundaVia(request)).thenReturn(response);

        ResponseEntity<CartaoResponseDTO> resposta = controller.solicitarSegundaVia(request);

        assertEquals(200, resposta.getStatusCodeValue());
        assertSame(response, resposta.getBody());
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getNumero()).isEqualTo(response.getNumero());

        verify(cartaoService, times(1)).solicitarSegundaVia(request);
        verifyNoMoreInteractions(cartaoService);
    }

    @Test
    void deveBuscarCartoesPorCliente() {
        Long idCliente = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CartaoResponseDTO> conteudo = CartaoTestFactory.criarListaCartaoResponseDTO(
                CartaoTestFactory.criarListaCartoes(2)
        );
        Page<CartaoResponseDTO> page = new PageImpl<>(conteudo, pageable, 2);

        when(cartaoService.buscarPorCliente(idCliente, pageable)).thenReturn(page);

        ResponseEntity<Page<CartaoResponseDTO>> resposta = controller.buscarPorCliente(idCliente, pageable);

        assertEquals(200, resposta.getStatusCodeValue());
        assertSame(page, resposta.getBody());
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getContent()).hasSize(2);
        verify(cartaoService, times(1)).buscarPorCliente(idCliente, pageable);
        verifyNoMoreInteractions(cartaoService);
    }

    @Test
    void deveCadastrarCartaoExistenteComSucesso() {
        CadastrarCartaoExistenteRequestDTO request = CartaoTestFactory.criarCadastrarCartaoExistenteRequestDTO();
        CartaoResponseDTO response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.cadastrarCartaoExistente(request)).thenReturn(response);

        ResponseEntity<CartaoResponseDTO> result = controller.cadastrarCartaoExistente(request);

        assertEquals(201, result.getStatusCodeValue());
        assertSame(response, result.getBody());
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getNumero()).isEqualTo(response.getNumero());

        verify(cartaoService, times(1)).cadastrarCartaoExistente(request);
        verifyNoMoreInteractions(cartaoService);
    }

    @Test
    void deveListarTodosOsCartoes() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CartaoResponseDTO> lista = List.of(CartaoTestFactory.criarCartaoResponseDTO());
        Page<CartaoResponseDTO> page = new PageImpl<>(lista);

        when(cartaoService.listarTodos(pageable)).thenReturn(page);

        ResponseEntity<Page<CartaoResponseDTO>> result = controller.listarTodos(pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertSame(page, result.getBody());
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).hasSize(1);

        verify(cartaoService, times(1)).listarTodos(pageable);
        verifyNoMoreInteractions(cartaoService);
    }
}
