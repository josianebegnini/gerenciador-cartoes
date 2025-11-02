package com.example.gw_gerenciador_cartoes.service.application.restController;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.application.restController.CartaoController;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartaoController.class)
public class CartaoControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartaoServicePort cartaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveAlterarStatusComSucesso() throws Exception {
        var request = CartaoTestFactory.criarAlterarStatusRequestDTO();
        var response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.alterarStatus(any())).thenReturn(response);

        mockMvc.perform(put("/api/cartoes/alterar-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(response.getNumero()));
    }

    @Test
    void deveSolicitarSegundaViaComSucesso() throws Exception {
        var request = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();
        var response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.solicitarSegundaVia(any())).thenReturn(response);

        mockMvc.perform(post("/api/cartoes/segunda-via")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(response.getNumero()));
    }

    @Test
    void deveBuscarCartoesPorCliente() throws Exception {
        Long idCliente = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CartaoResponseDTO> lista = List.of(CartaoTestFactory.criarCartaoResponseDTO());
        Page<CartaoResponseDTO> page = new PageImpl<>(lista);

        when(cartaoService.buscarPorCliente(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/cartoes/cliente/{idCliente}", idCliente)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void deveCadastrarCartaoExistenteComSucesso() throws Exception {
        var request = CartaoTestFactory.criarCadastrarCartaoExistenteRequestDTO();
        var response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.cadastrarCartaoExistente(any())).thenReturn(response);

        mockMvc.perform(post("/api/cartoes/cadastrar-existente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value(response.getNumero()));
    }

    @Test
    void deveListarTodosOsCartoes() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<CartaoResponseDTO> lista = List.of(CartaoTestFactory.criarCartaoResponseDTO());
        Page<CartaoResponseDTO> page = new PageImpl<>(lista);

        when(cartaoService.listarTodos(any())).thenReturn(page);

        mockMvc.perform(get("/api/cartoes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
