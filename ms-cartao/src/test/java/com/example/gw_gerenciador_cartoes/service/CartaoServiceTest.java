//package com.example.gw_gerenciador_cartoes.service;
//
//import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
//import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
//import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
//import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
//import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
//import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoServicePort;
//import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
//import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;
//import com.example.gw_gerenciador_cartoes.service.validator.CartaoStatusValidator;
//import com.example.gw_gerenciador_cartoes.service.validator.CartaoValidator;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CartaoServiceTest {
//
//
//    @InjectMocks
//    private CartaoService cartaoService;
//
//    @Mock
//    private CartaoRepositoryPort repository;
//
//    @Mock
//    private SolicitacaoCartaoServicePort solicitacaoCartaoService;
//
//    @Mock
//    private CartaoValidator cartaoValidator;
//
//    @Mock
//    private CartaoGenerator cartaoGenerator;
//
//    @Mock
//    private CartaoMapper mapper;
//
//    @Mock
//    private CartaoEmailService cartaoEmailService;
//
//    @Mock
//    private CartaoStatusValidator cartaoStatusValidator;
//
//
//    @Test
//    void deveCriarCartaoComSucesso() {
//        // Arrange
//        ClienteContaCriadoDTO dto = new ClienteContaCriadoDTO();
//        dto.setClienteId(1L);
//        dto.setContaId(2L);
//        dto.setNome("Kamila");
//        dto.setCpf("12345678900");
//        dto.setEmail("kamila@email.com");
//        dto.setTipoCartao(TipoCartao.CONTA);
//        dto.setTipoEmissao(TipoEmissao.FISICO);
//
//        Long solicitacaoId = 10L;
//        String numeroGerado = "1234567890123456";
//        String cvvGerado = "123";
//        LocalDateTime dataVencimento = LocalDateTime.now().plusYears(3);
//
//        Cartao cartaoSalvo = new Cartao();
//        cartaoSalvo.setId(99L);
//
//        // Mockando dependências
//        when(cartaoGenerator.gerarCvv()).thenReturn(cvvGerado);
//        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);
//
//        // Act
//        Long idCartao = cartaoService.criarCartao(dto, solicitacaoId);
//
//        // Assert
//        assertEquals(99L, idCartao);
//        verify(cartaoGenerator).gerarCvv();
//        verify(repository).salvar(any(Cartao.class));
//        verify(cartaoEmailService).enviarEmailCartaoCriado(cartaoSalvo);
//    }
//
//
//}
