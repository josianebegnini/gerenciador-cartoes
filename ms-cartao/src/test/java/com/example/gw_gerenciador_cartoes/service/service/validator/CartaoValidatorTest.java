//package com.example.gw_gerenciador_cartoes.service.service.validator;
//
//import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
//import com.example.gw_gerenciador_cartoes.infra.exception.CampoObrigatorioException;
//import com.example.gw_gerenciador_cartoes.infra.exception.MensagemErro;
//import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestFactory;
//import com.example.gw_gerenciador_cartoes.service.validator.CartaoValidator;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//@ExtendWith(MockitoExtension.class)
//public class CartaoValidatorTest {
//
//    private final CartaoValidator validator = new CartaoValidator();
//
//
//    @Test
//    void deveValidarComSucessoQuandoTodosOsCamposEstaoPreenchidos() {
//        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
//
//        assertDoesNotThrow(() -> validator.validar(dto, 1L));
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoClienteIdForNulo() {
//        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
//        dto.setClienteId(null);
//
//        CampoObrigatorioException ex = assertThrows(CampoObrigatorioException.class, () -> {
//            validator.validar(dto, 1L);
//        });
//
//        boolean contemMensagem = ex.getMensagensErros().stream()
//                .anyMatch(m -> "Campo clienteId é obrigatório.".equals(m.getMensagem()));
//
//        assertTrue(contemMensagem, "Deveria conter a mensagem de erro para o campo clienteId");
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoEmailForInvalido() {
//        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
//        dto.setEmail("email-invalido");
//
//        CampoObrigatorioException ex = assertThrows(CampoObrigatorioException.class, () -> {
//            validator.validar(dto, 1L);
//        });
//
//        boolean contemMensagem = ex.getMensagensErros().stream()
//                .anyMatch(m -> m.getMensagem().startsWith("E-mail inválido"));
//
//        assertTrue(contemMensagem, "Deveria conter a mensagem de e-mail inválido");
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoTodosOsCamposEstaoInvalidos() {
//        ClienteContaCriadoDTO dto = new ClienteContaCriadoDTO();
//
//        CampoObrigatorioException ex = assertThrows(CampoObrigatorioException.class, () -> {
//            validator.validar(dto, 99L);
//        });
//
//        List<String> mensagens = ex.getMensagensErros().stream()
//                .map(MensagemErro::getMensagem)
//                .toList();
//
//        assertEquals(6, mensagens.size());
//        assertTrue(mensagens.contains("Campo clienteId é obrigatório."));
//        assertTrue(mensagens.contains("Campo contaId é obrigatório."));
//        assertTrue(mensagens.contains("Campo nome é obrigatório."));
//        assertTrue(mensagens.contains("Campo e-mail é obrigatório."));
//        assertTrue(mensagens.contains("Campo tipoCartao é obrigatório."));
//        assertTrue(mensagens.contains("Campo tipoEmissao é obrigatório."));
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoNomeEstiverEmBranco() {
//        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
//        dto.setNome("   ");
//
//        CampoObrigatorioException ex = assertThrows(CampoObrigatorioException.class, () -> {
//            validator.validar(dto, 1L);
//        });
//
//        boolean contemMensagem = ex.getMensagensErros().stream()
//                .anyMatch(m -> "Campo nome é obrigatório.".equals(m.getMensagem()));
//
//        assertTrue(contemMensagem, "Deveria conter a mensagem de erro para o campo nome");
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoEmailEstiverEmBranco() {
//        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
//        dto.setEmail("   ");
//
//        CampoObrigatorioException ex = assertThrows(CampoObrigatorioException.class, () -> {
//            validator.validar(dto, 1L);
//        });
//
//        boolean contemMensagem = ex.getMensagensErros().stream()
//                .anyMatch(m -> "Campo e-mail é obrigatório.".equals(m.getMensagem()));
//
//        assertTrue(contemMensagem, "Deveria conter a mensagem de erro para o campo e-mail");
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoTipoCartaoForNulo() {
//        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
//        dto.setTipoCartao(null);
//
//        CampoObrigatorioException ex = assertThrows(CampoObrigatorioException.class, () -> {
//            validator.validar(dto, 1L);
//        });
//
//        boolean contemMensagem = ex.getMensagensErros().stream()
//                .anyMatch(m -> "Campo tipoCartao é obrigatório.".equals(m.getMensagem()));
//        assertTrue(contemMensagem, "Deveria conter a mensagem de erro para o campo tipoCartao");
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoTipoEmissaoForNulo() {
//        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
//        dto.setTipoEmissao(null);
//
//        CampoObrigatorioException ex = assertThrows(CampoObrigatorioException.class, () -> {
//            validator.validar(dto, 1L);
//        });
//
//        boolean contemMensagem = ex.getMensagensErros().stream()
//                .anyMatch(m -> "Campo tipoEmissao é obrigatório.".equals(m.getMensagem()));
//
//        assertTrue(contemMensagem, "Deveria conter a mensagem de erro para o campo tipoEmissao");
//    }
//}
