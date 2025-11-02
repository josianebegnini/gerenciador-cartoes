package com.example.gw_gerenciador_cartoes.service.application.restController;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.AlterarStatusRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CadastrarCartaoExistenteRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.restController.CartaoController;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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

        ResponseEntity<CartaoResponseDTO> result = controller.alterarStatus(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void deveSolicitarSegundaViaComSucesso() {
        SegundaViaCartaoRequestDTO request = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();
        CartaoResponseDTO response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.solicitarSegundaVia(request)).thenReturn(response);

        ResponseEntity<CartaoResponseDTO> result = controller.solicitarSegundaVia(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void deveBuscarCartoesPorCliente() {
        Long idCliente = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CartaoResponseDTO> lista = List.of(CartaoTestFactory.criarCartaoResponseDTO());
        Page<CartaoResponseDTO> page = new PageImpl<>(lista);

        when(cartaoService.buscarPorCliente(idCliente, pageable)).thenReturn(page);

        ResponseEntity<Page<CartaoResponseDTO>> result = controller.buscarPorCliente(idCliente, pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
    }

    @Test
    void deveCadastrarCartaoExistenteComSucesso() {
        CadastrarCartaoExistenteRequestDTO request = CartaoTestFactory.criarCadastrarCartaoExistenteRequestDTO();
        CartaoResponseDTO response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.cadastrarCartaoExistente(request)).thenReturn(response);

        ResponseEntity<CartaoResponseDTO> result = controller.cadastrarCartaoExistente(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void deveListarTodosOsCartoes() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CartaoResponseDTO> lista = List.of(CartaoTestFactory.criarCartaoResponseDTO());
        Page<CartaoResponseDTO> page = new PageImpl<>(lista);

        when(cartaoService.listarTodos(pageable)).thenReturn(page);

        ResponseEntity<Page<CartaoResponseDTO>> result = controller.listarTodos(pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
    }
}
