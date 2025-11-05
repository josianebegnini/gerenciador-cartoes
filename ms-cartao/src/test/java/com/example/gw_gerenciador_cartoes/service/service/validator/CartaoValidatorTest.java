package com.example.gw_gerenciador_cartoes.service.service.validator;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.exception.CampoObrigatorioException;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagemErro;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoValidatorTest {

    @Mock
    private CartaoRepositoryPort repository;

    @InjectMocks
    private CartaoValidator validator;

    @Test
    void deveValidarComSucessoQuandoTodosOsCamposEstaoPreenchidos() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        assertDoesNotThrow(() -> validator.validar(dto, 1L));
    }

    @Test
    void deveLancarExcecaoQuandoClienteIdForNulo() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setClienteId(null);
        Long solicitacaoId = 1L;

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, solicitacaoId);

        assertEquals(solicitacaoId, ex.getSolicitacaoId());
        assertContemMensagem(ex, "Campo clienteId é obrigatório.");
    }

    @Test
    void deveLancarExcecaoQuandoContaIdForNulo() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setContaId(null);
        Long solicitacaoId = 2L;

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, solicitacaoId);

        assertEquals(solicitacaoId, ex.getSolicitacaoId());
        assertContemMensagem(ex, "Campo contaId é obrigatório.");
    }

    @ParameterizedTest(name = "email inválido: \"{0}\"")
    @ValueSource(strings = {"email-invalido", "emailinvalido.com", "email@", "email@dominio"})
    void deveLancarExcecaoParaEmailsInvalidos(String email) {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setEmail(email);

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, 1L);

        assertAlgumaMensagemComecaCom(ex, "E-mail inválido");
    }

    @ParameterizedTest(name = "email obrigatório quando \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveLancarExcecaoQuandoEmailObrigatorio(String email) {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setEmail(email);

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, 1L);

        assertContemMensagem(ex, "Campo e-mail é obrigatório.");
    }

    @ParameterizedTest(name = "nome obrigatório quando \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveLancarExcecaoQuandoNomeObrigatorio(String nome) {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setNome(nome);

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, 1L);

        assertContemMensagem(ex, "Campo nome é obrigatório.");
    }

    @Test
    void deveLancarExcecaoQuandoTipoCartaoForNulo() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setTipoCartao(null);

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, 1L);

        assertContemMensagem(ex, "Campo tipoCartao é obrigatório.");
    }

    @Test
    void deveLancarExcecaoQuandoTipoEmissaoForNulo() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setTipoEmissao(null);

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, 1L);

        assertContemMensagem(ex, "Campo tipoEmissao é obrigatório.");
    }

    @Test
    void deveLancarExcecaoQuandoTodosOsCamposEstaoInvalidos() {
        ClienteContaCriadoDTO dto = new ClienteContaCriadoDTO(); // tudo nulo / em branco
        Long solicitacaoId = 99L;

        CampoObrigatorioException ex = validarEsperandoCampoObrigatorio(dto, solicitacaoId);

        assertEquals(solicitacaoId, ex.getSolicitacaoId());
        List<String> msgs = mensagens(ex);
        assertEquals(6, msgs.size());
        assertTrue(msgs.contains("Campo clienteId é obrigatório."));
        assertTrue(msgs.contains("Campo contaId é obrigatório."));
        assertTrue(msgs.contains("Campo nome é obrigatório."));
        assertTrue(msgs.contains("Campo e-mail é obrigatório."));
        assertTrue(msgs.contains("Campo tipoCartao é obrigatório."));
        assertTrue(msgs.contains("Campo tipoEmissao é obrigatório."));
    }

    @ParameterizedTest(name = "email válido: {0}")
    @ValueSource(strings = {
            "usuario.nome-teste@dominio.com.br",
            "teste@exemplo.info",
            "primeiro.ultimo@empresa.com"
    })
    void deveValidarEmailsValidos(String emailValido) {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setEmail(emailValido);

        assertDoesNotThrow(() -> validator.validar(dto, 1L));
    }

    @Test
    void deveValidarCartaoNaoExisteComSucesso() {
        String numero = "1234567890123456";
        String cvv = "123";

        when(repository.buscarCartaoPorNumeroECvv(numero, cvv)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validarCartaoNaoExiste(numero, cvv));
        verify(repository, times(1)).buscarCartaoPorNumeroECvv(numero, cvv);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deveLancarExcecaoQuandoCartaoJaExiste() {
        String numero = "1234567890123456";
        String cvv = "123";
        Cartao cartaoExistente = CartaoTestFactory.criarCartaoCompleto();

        when(repository.buscarCartaoPorNumeroECvv(numero, cvv)).thenReturn(Optional.of(cartaoExistente));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> validator.validarCartaoNaoExiste(numero, cvv));

        assertEquals(MensagensErroConstantes.CARTAO_JA_EXISTE, ex.getMessage());
        verify(repository, times(1)).buscarCartaoPorNumeroECvv(numero, cvv);
        verifyNoMoreInteractions(repository);
    }

    private static CampoObrigatorioException validarEsperandoCampoObrigatorio(ClienteContaCriadoDTO dto, Long solicitacaoId) {
        return assertThrows(CampoObrigatorioException.class, () -> {
            new CartaoValidator(null).validar(dto, solicitacaoId); // repo não é usado nesse método
        });
    }

    private static List<String> mensagens(CampoObrigatorioException ex) {
        return ex.getMensagensErros().stream().map(MensagemErro::getMensagem).toList();
    }

    private static void assertContemMensagem(CampoObrigatorioException ex, String mensagem) {
        assertTrue(mensagens(ex).contains(mensagem), "Deveria conter a mensagem: " + mensagem);
    }

    private static void assertAlgumaMensagemComecaCom(CampoObrigatorioException ex, String prefixo) {
        assertTrue(ex.getMensagensErros().stream().anyMatch(m -> m.getMensagem().startsWith(prefixo)),
                "Deveria conter mensagem iniciando com: " + prefixo);
    }
}