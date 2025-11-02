package com.example.gw_gerenciador_cartoes.service.application.restController;

import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartaoServicePort cartaoService;

    @Test
    void deveAlterarStatusComSucesso() throws Exception {
        var request = CartaoTestFactory.criarAlterarStatusRequestDTO();
        var response = CartaoTestFactory.criarCartaoResponseDTO();

        when(cartaoService.alterarStatus(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/cartoes/alterar-status")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cartoes/segunda-via")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(response.getNumero()));
    }

    @Test
    void deveBuscarCartoesPorCliente() throws Exception {
        var response = List.of(CartaoTestFactory.criarCartaoResponseDTO());
        var page = new org.springframework.data.domain.PageImpl<>(response);

        when(cartaoService.buscarPorCliente(any(), any())).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/cartoes/cliente/1")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cartoes/cadastrar-existente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value(response.getNumero()));
    }

    @Test
    void deveListarTodosOsCartoes() throws Exception {
        var response = List.of(CartaoTestFactory.criarCartaoResponseDTO());
        var page = new org.springframework.data.domain.PageImpl<>(response);

        when(cartaoService.listarTodos(any())).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/cartoes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

}