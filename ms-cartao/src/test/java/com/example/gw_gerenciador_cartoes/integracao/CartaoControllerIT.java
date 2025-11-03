package com.example.gw_gerenciador_cartoes.integracao;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.AlterarStatusRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CadastrarCartaoExistenteRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.email.CartaoEmailService;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.messaging.EmailPublisher;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartaoControllerIT {

    @MockBean
    private EmailPublisher emailPublisher;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartaoRepositoryPort cartaoRepository;

    @MockBean
    private Clock clock;

    @MockBean
    private CartaoEmailService cartaoEmailService;

    private Cartao cartaoSalvo;
    private String numeroCartao;
    private String cvv;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(Instant.parse("2025-01-01T12:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        numeroCartao = "4111111111111111";
        cvv = "123";

        Cartao cartao = new Cartao();
        cartao.setClienteId(1L);
        cartao.setContaId(2L);
        cartao.setSolicitacaoId(100L);
        cartao.setNumero(numeroCartao);
        cartao.setCvv(cvv);
        cartao.setDataVencimento(LocalDateTime.now().plusYears(3));
        cartao.setDataCriacao(LocalDateTime.now());
        cartao.setStatus(StatusCartao.DESATIVADO);
        cartao.setMotivoStatus("Cartão criado");
        cartao.setTipoCartao(TipoCartao.DEBITO);
        cartao.setTipoEmissao(TipoEmissao.FISICO);
        cartao.setLimite(BigDecimal.ZERO);

        cartaoSalvo = cartaoRepository.salvar(cartao);
        assertNotNull(cartaoSalvo.getId());
        assertEquals(StatusCartao.DESATIVADO, cartaoSalvo.getStatus());
    }

    @Test
    void deveAlterarStatusDeDesativadoParaAtivadoComSucesso() throws Exception {
        AlterarStatusRequestDTO request = CartaoTestFactory.criarAlterarStatusRequestDTO();

        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        cartao.setNumero(request.getNumero());
        cartao.setCvv(request.getCvv());
        cartao.setStatus(StatusCartao.DESATIVADO);
        cartao.setMotivoStatus("Cartão criado");
        cartao.setId(null);

        cartaoRepository.salvar(cartao);

        mockMvc.perform(put("/api/cartoes/alterar-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("1234567890123456"))
                .andExpect(jsonPath("$.status").value(StatusCartao.ATIVADO.name()));

        Optional<Cartao> cartaoAtualizadoOpt = cartaoRepository.buscarCartaoPorNumeroECvv("1234567890123456", "123");
        assertTrue(cartaoAtualizadoOpt.isPresent());

        Cartao cartaoAtualizado = cartaoAtualizadoOpt.get();
        assertEquals(StatusCartao.ATIVADO, cartaoAtualizado.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_ATIVADO, cartaoAtualizado.getMotivoStatus());
        assertNotNull(cartaoAtualizado.getDataCriacao());

        verify(cartaoEmailService, times(1)).enviarEmailCartaoAtivado(any(Cartao.class));
    }

    @Test
    void deveAlterarStatusDeAtivadoParaBloqueadoComSucesso() throws Exception {
        cartaoSalvo.setStatus(StatusCartao.ATIVADO);
        cartaoSalvo = cartaoRepository.atualizar(cartaoSalvo)
                .orElseThrow(() -> new RuntimeException("Falha ao atualizar cartão"));

        AlterarStatusRequestDTO request = new AlterarStatusRequestDTO();
        request.setNumero(numeroCartao);
        request.setCvv(cvv);
        request.setNovoStatus(StatusCartao.BLOQUEADO);

        mockMvc.perform(put("/api/cartoes/alterar-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(numeroCartao))
                .andExpect(jsonPath("$.status").value(StatusCartao.BLOQUEADO.name()));

        Optional<Cartao> cartaoAtualizadoOpt = cartaoRepository.buscarCartaoPorNumeroECvv(numeroCartao, cvv);
        assertTrue(cartaoAtualizadoOpt.isPresent());
        
        Cartao cartaoAtualizado = cartaoAtualizadoOpt.get();
        assertEquals(StatusCartao.BLOQUEADO, cartaoAtualizado.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_BLOQUEADO_SEGURANCA, cartaoAtualizado.getMotivoStatus());

        verify(cartaoEmailService, times(1)).enviarEmailCartaoBloqueado(any(Cartao.class), anyString());
    }

    @Test
    void deveRetornarErroQuandoCartaoNaoForEncontrado() throws Exception {
        AlterarStatusRequestDTO request = CartaoTestFactory.criarAlterarStatusRequestDTO();
        request.setNumero("0000000000000000");
        request.setCvv("999");

        mockMvc.perform(put("/api/cartoes/alterar-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        verify(cartaoEmailService, never()).enviarEmailCartaoAtivado(any());
        verify(cartaoEmailService, never()).enviarEmailCartaoBloqueado(any(), anyString());
    }

    @Test
    void deveRetornarErroQuandoTentarAtivarCartaoJaAtivado() throws Exception {
        cartaoSalvo.setStatus(StatusCartao.ATIVADO);
        cartaoSalvo = cartaoRepository.atualizar(cartaoSalvo)
                .orElseThrow(() -> new RuntimeException("Falha ao atualizar cartão"));

        AlterarStatusRequestDTO request = new AlterarStatusRequestDTO();
        request.setNumero(numeroCartao);
        request.setCvv(cvv);
        request.setNovoStatus(StatusCartao.ATIVADO);

        mockMvc.perform(put("/api/cartoes/alterar-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(MensagensErroConstantes.CARTAO_ATIVACAO_STATUS_INVALIDO));

        Optional<Cartao> cartaoOpt = cartaoRepository.buscarCartaoPorNumeroECvv(numeroCartao, cvv);
        assertTrue(cartaoOpt.isPresent());
        assertEquals(StatusCartao.ATIVADO, cartaoOpt.get().getStatus());

        verify(cartaoEmailService, never()).enviarEmailCartaoAtivado(any());
    }

    @Test
    void deveRetornarErroQuandoValidacaoFalhar() throws Exception {
        AlterarStatusRequestDTO request = CartaoTestFactory.criarAlterarStatusRequestDTO();
        request.setNumero("");
        request.setCvv("");

        mockMvc.perform(put("/api/cartoes/alterar-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cartaoEmailService, never()).enviarEmailCartaoAtivado(any());
    }

    @Test
    void deveSolicitarSegundaViaComSucesso() throws Exception {

        Cartao original = CartaoTestFactory.criarCartaoOriginalSegundaVia();
        original.setId(null);
        cartaoRepository.salvar(original);

        SegundaViaCartaoRequestDTO request = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();

        String responseContent = mockMvc.perform(post("/api/cartoes/segunda-via")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").isNotEmpty())
                .andExpect(jsonPath("$.cvv").isNotEmpty())
                .andExpect(jsonPath("$.status").value(StatusCartao.DESATIVADO.name()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CartaoResponseDTO response = objectMapper.readValue(responseContent, CartaoResponseDTO.class);

        Optional<Cartao> novaViaOpt = cartaoRepository.buscarCartaoPorNumeroECvv(
                response.getNumero(), response.getCvv());

        assertTrue(novaViaOpt.isPresent());

        Cartao novaVia = novaViaOpt.get();
        assertEquals(StatusCartao.DESATIVADO, novaVia.getStatus());
        assertEquals(
                MensagensErroConstantes.MOTIVO_CARTAO_SEGUNDA_VIA_GERADA + request.getMotivoSegundaVia(),
                novaVia.getMotivoStatus()
        );

        verify(cartaoEmailService, times(1)).enviarEmailSegundaVia(any(Cartao.class));
    }

    @Test
    void deveBuscarCartoesPorIdClienteComSucesso() throws Exception {
        mockMvc.perform(get("/api/cartoes/cliente/{idCliente}", cartaoSalvo.getClienteId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numero").value(numeroCartao));
    }

    @Test
    void deveBuscarCartaoPorNumeroECvvComSucesso() throws Exception {
        mockMvc.perform(get("/api/cartoes/por-numero-e-cvv")
                        .param("numero", numeroCartao)
                        .param("cvv", cvv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(numeroCartao))
                .andExpect(jsonPath("$.cvv").value(cvv));
    }

    @Test
    void deveCadastrarCartaoExistenteComSucesso() throws Exception {
        CadastrarCartaoExistenteRequestDTO request = CartaoTestFactory.criarCadastrarCartaoExistenteRequestDTO();

        mockMvc.perform(post("/api/cartoes/cadastrar-existente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("1234567890123456"))
                .andExpect(jsonPath("$.status").value(StatusCartao.ATIVADO.name()));

        Optional<Cartao> cartaoOpt = cartaoRepository.buscarCartaoPorNumeroECvv("1234567890123456", "123");
        assertTrue(cartaoOpt.isPresent());

        Cartao cartao = cartaoOpt.get();
        assertEquals(StatusCartao.ATIVADO, cartao.getStatus());
    }

    @Test
    void deveListarTodosOsCartoesComSucesso() throws Exception {
        mockMvc.perform(get("/api/cartoes/todos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].numero").value(numeroCartao));
    }

    @Test
    void deveBuscarCartoesComFiltrosComSucesso() throws Exception {
        mockMvc.perform(get("/api/cartoes/filtro")
                        .param("clienteId", "1")
                        .param("numero", numeroCartao)
                        .param("cvv", cvv)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numero").value(numeroCartao));
    }
}

