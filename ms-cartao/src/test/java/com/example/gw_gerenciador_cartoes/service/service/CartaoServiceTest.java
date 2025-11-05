package com.example.gw_gerenciador_cartoes.service.service;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.email.CartaoEmailService;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.infra.exception.CartaoNotFoundException;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.CartaoCreatorService;
import com.example.gw_gerenciador_cartoes.service.CartaoService;
import com.example.gw_gerenciador_cartoes.service.DadosCartaoGenerator;
import com.example.gw_gerenciador_cartoes.service.SolicitacaoCartaoService;
import com.example.gw_gerenciador_cartoes.service.validator.PoliticaExpiracaoCartao;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoStatusValidator;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoServiceTest {

    @InjectMocks
    private CartaoService cartaoService;

    @Mock
    private SolicitacaoCartaoService solicitacaoCartaoServiceTest;

    @Mock
    private CartaoValidator cartaoValidator;

    @Mock
    private CartaoCreatorService cartaoCreatorService;

    @Mock
    private CartaoRepositoryPort repository;

    @Mock
    private CartaoStatusValidator cartaoStatusValidator;

    @Mock
    private CartaoEmailService cartaoEmailService;

    @Mock
    private CartaoMapper mapper;

    @Mock
    private DadosCartaoGenerator dadosCartaoGenerator;

    @Mock
    private PoliticaExpiracaoCartao politicaExpiracaoCartao;

    @Test
    void deveProcessarSolicitacaoComSucesso() {

        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        SolicitacaoCartao solicitacao = CartaoTestFactory.criarSolicitacaoCartaoCompleto(123L, StatusSolicitacao.PROCESSADO);
        Long cartaoId = 456L;

        when(solicitacaoCartaoServiceTest.salvar(dto.getClienteId(), dto.getContaId(), dto.getTipoCartao(), dto.getTipoEmissao(), dto.getNome(), dto.getEmail())).thenReturn(solicitacao);

        doNothing().when(cartaoValidator).validar(dto, solicitacao.getId());
        when(cartaoCreatorService.criarCartao(dto, solicitacao.getId())).thenReturn(cartaoId);

        cartaoService.processarSolicitacao(dto);

        verify(solicitacaoCartaoServiceTest).salvar(dto.getClienteId(), dto.getContaId(), dto.getTipoCartao(), dto.getTipoEmissao(), dto.getNome(), dto.getEmail());
        verify(cartaoValidator).validar(dto, solicitacao.getId());
        verify(cartaoCreatorService).criarCartao(dto, solicitacao.getId());
        verify(solicitacaoCartaoServiceTest).finalizarComoProcessada(solicitacao.getId(), cartaoId);
    }

    @Test
    void deveLancarExcecaoQuandoCriacaoDeCartaoFalhar() {

        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        SolicitacaoCartao solicitacao = CartaoTestFactory.criarSolicitacaoCartaoCompleto(99L, StatusSolicitacao.PROCESSADO);

        when(solicitacaoCartaoServiceTest.salvar(
                dto.getClienteId(),
                dto.getContaId(),
                dto.getTipoCartao(),
                dto.getTipoEmissao(),
                dto.getNome(),
                dto.getEmail()
        )).thenReturn(solicitacao);

        doNothing().when(cartaoValidator).validar(dto, solicitacao.getId());
        when(cartaoCreatorService.criarCartao(dto, solicitacao.getId()))
                .thenThrow(new RegraNegocioException(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            cartaoService.processarSolicitacao(dto);
        });

        assertEquals(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR, exception.getMessage());

        verify(solicitacaoCartaoServiceTest, never()).finalizarComoProcessada(anyLong(), anyLong());
    }

    @Test
    void deveAlterarStatusComSucesso() {

        AlterarStatusRequestDTO dto = CartaoTestFactory.criarAlterarStatusRequestDTO();
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        Cartao atualizado = CartaoTestFactory.criarCartaoCompleto();
        atualizado.setStatus(StatusCartao.ATIVADO);

        CartaoResponseDTO responseDTO = CartaoTestFactory.criarCartaoResponseDTO();
        responseDTO.setStatus(StatusCartao.ATIVADO);

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())).thenReturn(Optional.of(cartao));
        doNothing().when(cartaoStatusValidator).validarAlteracaoStatus(cartao, dto.getNovoStatus());
        when(repository.atualizar(cartao)).thenReturn(Optional.of(atualizado));
        when(mapper.toCartaoResponseDTO(atualizado)).thenReturn(responseDTO);

        CartaoResponseDTO resultado = cartaoService.alterarStatus(dto);

        assertNotNull(resultado);
        assertEquals(dto.getNumero(), resultado.getNumero());
        assertEquals(StatusCartao.ATIVADO, resultado.getStatus());

        verify(cartaoEmailService).enviarEmailCartaoAtivado(atualizado);
        verify(cartaoEmailService, never()).enviarEmailCartaoBloqueado(any(), anyString());
    }

    @Test
    void deveLancarExcecaoAoAlterarStatusQuandoCartaoNaoForEncontrado() {

        AlterarStatusRequestDTO dto = CartaoTestFactory.criarAlterarStatusRequestDTO();

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())).thenReturn(Optional.empty());

        CartaoNotFoundException exception = assertThrows(CartaoNotFoundException.class, () -> {
            cartaoService.alterarStatus(dto);
        });

        assertEquals(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO, exception.getMessage());

        verify(cartaoStatusValidator, never()).validarAlteracaoStatus(any(), any());
        verify(repository, never()).atualizar(any());
        verify(cartaoEmailService, never()).enviarEmailCartaoAtivado(any());
        verify(cartaoEmailService, never()).enviarEmailCartaoBloqueado(any(), anyString());
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveSolicitarSegundaViaComSucesso() {

        SegundaViaCartaoRequestDTO dto = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();
        Cartao original = CartaoTestFactory.criarCartaoOriginalSegundaVia(); // ATIVADO

        LocalDateTime vencimentoPolitica = LocalDateTime.of(2032, 6, 10, 12, 0);
        when(politicaExpiracaoCartao.calcularParaSegundaVia(original.getDataVencimento()))
                .thenReturn(vencimentoPolitica);

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv()))
                .thenReturn(Optional.of(original));
        when(cartaoCreatorService.gerarNumeroCartaoUnico()).thenReturn("6543210987654321");
        when(dadosCartaoGenerator.gerarCvv()).thenReturn("999");

        Cartao salvo = CartaoTestFactory.criarSegundaViaCompleta(original, dto, vencimentoPolitica);
        salvo.setId(200L);
        when(repository.salvar(any(Cartao.class))).thenReturn(salvo);

        CartaoResponseDTO respostaMapeada = CartaoTestFactory.criarCartaoResponseDTO();
        respostaMapeada.setNumero(salvo.getNumero());
        respostaMapeada.setStatus(salvo.getStatus());
        respostaMapeada.setDataVencimento(vencimentoPolitica);
        when(mapper.toCartaoResponseDTO(salvo)).thenReturn(respostaMapeada);

        CartaoResponseDTO resultado = cartaoService.solicitarSegundaVia(dto);

        assertNotNull(resultado);
        assertEquals(salvo.getNumero(), resultado.getNumero());
        assertEquals(StatusCartao.DESATIVADO, resultado.getStatus());
        assertEquals(vencimentoPolitica, resultado.getDataVencimento());

        var captor = org.mockito.ArgumentCaptor.forClass(Cartao.class);
        verify(repository).salvar(captor.capture());
        Cartao enviado = captor.getValue();
        assertEquals(vencimentoPolitica, enviado.getDataVencimento());

        verify(repository).atualizar(original);
        verify(cartaoEmailService).enviarEmailSegundaVia(salvo);
    }

    @Test
    void deveLancarExcecaoQuandoStatusDoCartaoOriginalForInvalido() {

        SegundaViaCartaoRequestDTO dto = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();
        Cartao original = CartaoTestFactory.criarCartaoCompleto();
        original.setStatus(StatusCartao.CANCELADO);

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())).thenReturn(Optional.of(original));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            cartaoService.solicitarSegundaVia(dto);
        });

        assertEquals(MensagensErroConstantes.SEGUNDA_VIA_STATUS_INVALIDO, exception.getMessage());

        verify(repository, never()).atualizar(any());
        verify(repository, never()).salvar(any());
        verify(cartaoEmailService, never()).enviarEmailSegundaVia(any());
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveLancarExcecaoQuandoCartaoNaoForEncontradoParaSegundaVia() {

        SegundaViaCartaoRequestDTO dto = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())).thenReturn(Optional.empty());

        CartaoNotFoundException exception = assertThrows(CartaoNotFoundException.class, () -> {
            cartaoService.solicitarSegundaVia(dto);
        });

        assertEquals(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO, exception.getMessage());

        verify(repository, never()).atualizar(any());
        verify(repository, never()).salvar(any());
        verify(cartaoEmailService, never()).enviarEmailSegundaVia(any());
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveLancarExcecaoQuandoFalharAoSalvarSegundaVia() {

        SegundaViaCartaoRequestDTO dto = CartaoTestFactory.criarSegundaViaCartaoRequestDTO();
        Cartao original = CartaoTestFactory.criarCartaoOriginalSegundaVia();

        LocalDateTime vencimentoPolitica = LocalDateTime.of(2032, 6, 10, 12, 0);
        when(politicaExpiracaoCartao.calcularParaSegundaVia(original.getDataVencimento()))
                .thenReturn(vencimentoPolitica);

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv()))
                .thenReturn(Optional.of(original));
        when(cartaoCreatorService.gerarNumeroCartaoUnico()).thenReturn("6543210987654321");
        when(dadosCartaoGenerator.gerarCvv()).thenReturn("999");

        when(repository.salvar(any(Cartao.class))).thenReturn(null);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> cartaoService.solicitarSegundaVia(dto));

        assertEquals(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR_SEGUNDA_VIA, ex.getMessage());

        var captor = org.mockito.ArgumentCaptor.forClass(Cartao.class);
        verify(repository).salvar(captor.capture());
        assertEquals(vencimentoPolitica, captor.getValue().getDataVencimento());

        verify(repository).atualizar(original);
        verify(cartaoEmailService, never()).enviarEmailSegundaVia(any());
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveBuscarCartaoPorNumeroECvvComSucesso() {
        String numero = "1234567890123456";
        String cvv = "123";

        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        cartao.setNumero(numero);
        cartao.setCvv(cvv);

        CartaoResponseDTO responseDTO = CartaoTestFactory.criarCartaoResponseDTO();
        responseDTO.setNumero(numero);
        responseDTO.setCvv(cvv);

        when(repository.buscarCartaoPorNumeroECvv(numero, cvv)).thenReturn(Optional.of(cartao));
        when(mapper.toCartaoResponseDTO(cartao)).thenReturn(responseDTO);

        CartaoResponseDTO resultado = cartaoService.buscarPorNumeroECvv(numero, cvv);

        assertNotNull(resultado);
        assertEquals(numero, resultado.getNumero());
        assertEquals(cvv, resultado.getCvv());
        assertEquals(StatusCartao.ATIVADO, resultado.getStatus());

        verify(repository).buscarCartaoPorNumeroECvv(numero, cvv);
        verify(mapper).toCartaoResponseDTO(cartao);
    }

    @Test
    void deveLancarExcecaoAoBuscarCartaoPorNumeroECvvQuandoNaoEncontrado() {
        String numero = "0000000000000000";
        String cvv = "000";

        when(repository.buscarCartaoPorNumeroECvv(numero, cvv)).thenReturn(Optional.empty());

        CartaoNotFoundException exception = assertThrows(CartaoNotFoundException.class, () -> {
            cartaoService.buscarPorNumeroECvv(numero, cvv);
        });

        assertEquals(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO, exception.getMessage());

        verify(repository).buscarCartaoPorNumeroECvv(numero, cvv);
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveBuscarCartoesPorClienteComSucesso() {

        Long clienteId = 1L;
        Pageable pageable = PageRequest.of(0, 2);

        Cartao cartao1 = CartaoTestFactory.criarCartaoComNumero("1111222233334444");
        Cartao cartao2 = CartaoTestFactory.criarCartaoComNumero("5555666677778888");

        CartaoResponseDTO dto1 = CartaoTestFactory.criarCartaoResponseDTO();
        dto1.setNumero(cartao1.getNumero());
        CartaoResponseDTO dto2 = CartaoTestFactory.criarCartaoResponseDTO();
        dto2.setNumero(cartao2.getNumero());

        Page<Cartao> cartoesPage = new PageImpl<>(List.of(cartao1, cartao2), pageable, 2);

        when(repository.buscarPorIdCliente(clienteId, pageable)).thenReturn(cartoesPage);
        when(mapper.toCartaoResponseDTO(cartao1)).thenReturn(dto1);
        when(mapper.toCartaoResponseDTO(cartao2)).thenReturn(dto2);

        Page<CartaoResponseDTO> resultado = cartaoService.buscarPorCliente(clienteId, pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(dto1.getNumero(), resultado.getContent().get(0).getNumero());
        assertEquals(dto2.getNumero(), resultado.getContent().get(1).getNumero());

        verify(repository).buscarPorIdCliente(clienteId, pageable);
        verify(mapper).toCartaoResponseDTO(cartao1);
        verify(mapper).toCartaoResponseDTO(cartao2);
    }

    @Test
    void deveRetornarPaginaVaziaQuandoClienteNaoTemCartoes() {

        Long clienteId = 999L;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Cartao> paginaVazia = Page.empty(pageable);

        when(repository.buscarPorIdCliente(clienteId, pageable)).thenReturn(paginaVazia);

        Page<CartaoResponseDTO> resultado = cartaoService.buscarPorCliente(clienteId, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveCadastrarCartaoExistenteComSucesso() {

        CadastrarCartaoExistenteRequestDTO dto = CartaoTestFactory.criarCadastrarCartaoExistenteRequestDTO();

        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();
        cartaoSalvo.setId(10L);
        cartaoSalvo.setNumero(dto.getNumero());
        cartaoSalvo.setStatus(dto.getStatus());

        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        CartaoResponseDTO mapeado = CartaoTestFactory.criarCartaoResponseDTO();
        mapeado.setNumero(dto.getNumero());
        mapeado.setStatus(dto.getStatus());
        when(mapper.toCartaoResponseDTO(cartaoSalvo)).thenReturn(mapeado);

        var resultado = cartaoService.cadastrarCartaoExistente(dto);

        assertNotNull(resultado);
        assertEquals(dto.getNumero(), resultado.getNumero());
        assertEquals(dto.getStatus(), resultado.getStatus());

        var captor = org.mockito.ArgumentCaptor.forClass(Cartao.class);
        verify(repository).salvar(captor.capture());
        Cartao enviado = captor.getValue();

        assertEquals(dto.getClienteId(), enviado.getClienteId());
        assertEquals(0L, enviado.getContaId());
        assertEquals(0L, enviado.getSolicitacaoId());
        assertEquals(dto.getNumero(), enviado.getNumero());
        assertEquals(dto.getCvv(), enviado.getCvv());
        assertEquals(dto.getStatus(), enviado.getStatus());
        assertEquals(dto.getTipoCartao(), enviado.getTipoCartao());
        assertEquals(dto.getTipoEmissao(), enviado.getTipoEmissao());
        assertEquals(dto.getLimite(), enviado.getLimite());
        assertEquals(dto.getDataVencimento(), enviado.getDataVencimento());
        assertNotNull(enviado.getDataCriacao());
    }

    @Test
    void deveLancarExcecaoQuandoFalharAoSalvarCartaoExistente() {

        CadastrarCartaoExistenteRequestDTO dto = CartaoTestFactory.criarCadastrarCartaoExistenteRequestDTO();
        dto.setNumero("0000000000000000");
        dto.setCvv("000");

        when(repository.salvar(any(Cartao.class))).thenReturn(null);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            cartaoService.cadastrarCartaoExistente(dto);
        });

        assertEquals(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR, exception.getMessage());

        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveListarTodosOsCartoesComSucesso() {

        Pageable pageable = PageRequest.of(0, 2);

        Cartao cartao1 = CartaoTestFactory.criarCartaoComNumero("1111222233334444");
        Cartao cartao2 = CartaoTestFactory.criarCartaoComNumero("5555666677778888");

        CartaoResponseDTO dto1 = CartaoTestFactory.criarCartaoResponseDTO();
        dto1.setNumero(cartao1.getNumero());
        CartaoResponseDTO dto2 = CartaoTestFactory.criarCartaoResponseDTO();
        dto2.setNumero(cartao2.getNumero());

        Page<Cartao> cartoesPage = new PageImpl<>(List.of(cartao1, cartao2), pageable, 2);

        when(repository.buscarTodos(pageable)).thenReturn(cartoesPage);
        when(mapper.toCartaoResponseDTO(cartao1)).thenReturn(dto1);
        when(mapper.toCartaoResponseDTO(cartao2)).thenReturn(dto2);

        Page<CartaoResponseDTO> resultado = cartaoService.listarTodos(pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(dto1.getNumero(), resultado.getContent().get(0).getNumero());
        assertEquals(dto2.getNumero(), resultado.getContent().get(1).getNumero());

        verify(repository).buscarTodos(pageable);
        verify(mapper).toCartaoResponseDTO(cartao1);
        verify(mapper).toCartaoResponseDTO(cartao2);
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoExistemCartoes() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cartao> paginaVazia = Page.empty(pageable);

        when(repository.buscarTodos(pageable)).thenReturn(paginaVazia);

        Page<CartaoResponseDTO> resultado = cartaoService.listarTodos(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveAlterarStatusParaBloqueadoComSucesso() {

        AlterarStatusRequestDTO dto = CartaoTestFactory.criarAlterarStatusRequestDTO();
        dto.setNovoStatus(StatusCartao.BLOQUEADO);
        
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        Cartao atualizado = CartaoTestFactory.criarCartaoCompleto();
        atualizado.setStatus(StatusCartao.BLOQUEADO);

        CartaoResponseDTO responseDTO = CartaoTestFactory.criarCartaoResponseDTO();
        responseDTO.setStatus(StatusCartao.BLOQUEADO);

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())).thenReturn(Optional.of(cartao));
        doNothing().when(cartaoStatusValidator).validarAlteracaoStatus(cartao, dto.getNovoStatus());
        when(repository.atualizar(cartao)).thenReturn(Optional.of(atualizado));
        when(mapper.toCartaoResponseDTO(atualizado)).thenReturn(responseDTO);

        CartaoResponseDTO resultado = cartaoService.alterarStatus(dto);

        assertNotNull(resultado);
        assertEquals(dto.getNumero(), resultado.getNumero());
        assertEquals(StatusCartao.BLOQUEADO, resultado.getStatus());

        verify(cartaoEmailService).enviarEmailCartaoBloqueado(atualizado, MensagensErroConstantes.MOTIVO_CARTAO_BLOQUEADO_SEGURANCA);
        verify(cartaoEmailService, never()).enviarEmailCartaoAtivado(any());
    }

    @Test
    void deveLancarExcecaoAoAlterarStatusQuandoAtualizacaoFalhar() {

        AlterarStatusRequestDTO dto = CartaoTestFactory.criarAlterarStatusRequestDTO();
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();

        when(repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())).thenReturn(Optional.of(cartao));
        doNothing().when(cartaoStatusValidator).validarAlteracaoStatus(cartao, dto.getNovoStatus());
        when(repository.atualizar(cartao)).thenReturn(Optional.empty());

        CartaoNotFoundException exception = assertThrows(CartaoNotFoundException.class, () -> {
            cartaoService.alterarStatus(dto);
        });

        assertEquals(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO, exception.getMessage());

        verify(cartaoEmailService, never()).enviarEmailCartaoAtivado(any());
        verify(cartaoEmailService, never()).enviarEmailCartaoBloqueado(any(), anyString());
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveLancarExcecaoQuandoValidacaoCartaoExistenteFalhar() {

        CadastrarCartaoExistenteRequestDTO dto = CartaoTestFactory.criarCadastrarCartaoExistenteRequestDTO();

        doThrow(new RegraNegocioException(MensagensErroConstantes.CARTAO_JA_EXISTE))
                .when(cartaoValidator).validarCartaoNaoExiste(dto.getNumero(), dto.getCvv());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            cartaoService.cadastrarCartaoExistente(dto);
        });

        assertEquals(MensagensErroConstantes.CARTAO_JA_EXISTE, exception.getMessage());

        verify(repository, never()).salvar(any());
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveBuscarCartoesComFiltrosComSucesso() {

        Long clienteId = 1L;
        String numero = "1234567890123456";
        String cvv = "123";
        Pageable pageable = PageRequest.of(0, 10);

        Cartao cartao1 = CartaoTestFactory.criarCartaoComNumero(numero);
        Cartao cartao2 = CartaoTestFactory.criarCartaoComNumero("9876543210987654");

        CartaoResponseDTO dto1 = CartaoTestFactory.criarCartaoResponseDTO();
        dto1.setNumero(cartao1.getNumero());
        CartaoResponseDTO dto2 = CartaoTestFactory.criarCartaoResponseDTO();
        dto2.setNumero(cartao2.getNumero());

        Page<Cartao> cartoesPage = new PageImpl<>(List.of(cartao1, cartao2), pageable, 2);

        when(repository.buscarPorFiltros(clienteId, numero, cvv, pageable)).thenReturn(cartoesPage);
        when(mapper.toCartaoResponseDTO(cartao1)).thenReturn(dto1);
        when(mapper.toCartaoResponseDTO(cartao2)).thenReturn(dto2);

        Page<CartaoResponseDTO> resultado = cartaoService.buscarCartoes(clienteId, numero, cvv, pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(dto1.getNumero(), resultado.getContent().get(0).getNumero());
        assertEquals(dto2.getNumero(), resultado.getContent().get(1).getNumero());

        verify(repository).buscarPorFiltros(clienteId, numero, cvv, pageable);
        verify(mapper).toCartaoResponseDTO(cartao1);
        verify(mapper).toCartaoResponseDTO(cartao2);
    }

    @Test
    void deveBuscarCartoesComFiltrosRetornandoPaginaVazia() {

        Long clienteId = 999L;
        String numero = "0000000000000000";
        String cvv = "000";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Cartao> paginaVazia = Page.empty(pageable);

        when(repository.buscarPorFiltros(clienteId, numero, cvv, pageable)).thenReturn(paginaVazia);

        Page<CartaoResponseDTO> resultado = cartaoService.buscarCartoes(clienteId, numero, cvv, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(repository).buscarPorFiltros(clienteId, numero, cvv, pageable);
        verify(mapper, never()).toCartaoResponseDTO(any());
    }

    @Test
    void deveBuscarCartoesComFiltrosNulos() {

        Pageable pageable = PageRequest.of(0, 10);

        Cartao cartao1 = CartaoTestFactory.criarCartaoComNumero("1111222233334444");
        Cartao cartao2 = CartaoTestFactory.criarCartaoComNumero("5555666677778888");

        CartaoResponseDTO dto1 = CartaoTestFactory.criarCartaoResponseDTO();
        dto1.setNumero(cartao1.getNumero());
        CartaoResponseDTO dto2 = CartaoTestFactory.criarCartaoResponseDTO();
        dto2.setNumero(cartao2.getNumero());

        Page<Cartao> cartoesPage = new PageImpl<>(List.of(cartao1, cartao2), pageable, 2);

        when(repository.buscarPorFiltros(null, null, null, pageable)).thenReturn(cartoesPage);
        when(mapper.toCartaoResponseDTO(cartao1)).thenReturn(dto1);
        when(mapper.toCartaoResponseDTO(cartao2)).thenReturn(dto2);

        Page<CartaoResponseDTO> resultado = cartaoService.buscarCartoes(null, null, null, pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());

        verify(repository).buscarPorFiltros(null, null, null, pageable);
        verify(mapper).toCartaoResponseDTO(cartao1);
        verify(mapper).toCartaoResponseDTO(cartao2);
    }

}

