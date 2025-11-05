package com.example.gw_gerenciador_cartoes.testutil;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartaoTestFactory {

    private CartaoTestFactory() {}

    /** Tempo base estável para objetos de teste (evita flakiness). */
    public static final LocalDateTime AGORA_BASE = LocalDateTime.of(2030, 1, 10, 12, 0);

    /** Cria um cartão "completo" (defaults estáveis). */
    public static Cartao criarCartaoCompleto() {
        return criarCartao(
                100L,
                99L,
                CartaoTestConstants.NUMERO_PADRAO,
                CartaoTestConstants.CVV_PADRAO,
                AGORA_BASE.plusYears(3),
                AGORA_BASE,
                StatusCartao.ATIVADO,
                CartaoTestConstants.MOTIVO_STATUS_ATIVADO,
                TipoCartao.CONTA,
                TipoEmissao.FISICO,
                CartaoTestConstants.LIMITE_PADRAO,
                1L,
                2L
        );
    }

    public static Cartao criarCartao(Long id,
                                     Long solicitacaoId,
                                     String numero,
                                     String cvv,
                                     LocalDateTime dataVencimento,
                                     LocalDateTime dataCriacao,
                                     StatusCartao status,
                                     String motivoStatus,
                                     TipoCartao tipoCartao,
                                     TipoEmissao tipoEmissao,
                                     BigDecimal limite,
                                     Long clienteId,
                                     Long contaId) {
        Cartao c = new Cartao();
        c.setId(id);
        c.setSolicitacaoId(solicitacaoId);
        c.setNumero(numero);
        c.setCvv(cvv);
        c.setDataVencimento(dataVencimento);
        c.setDataCriacao(dataCriacao);
        c.setStatus(status);
        c.setMotivoStatus(motivoStatus);
        c.setTipoCartao(tipoCartao);
        c.setTipoEmissao(tipoEmissao);
        c.setLimite(limite);
        c.setClienteId(clienteId);
        c.setContaId(contaId);
        return c;
    }

    public static Cartao criarCartaoComNumero(String numero) {
        Cartao c = criarCartaoCompleto();
        c.setNumero(numero);
        return c;
    }

    public static SolicitacaoCartao criarSolicitacaoCartaoCompleto(Long id, StatusSolicitacao status) {
        SolicitacaoCartao s = new SolicitacaoCartao();
        s.setId(id);
        s.setCartaoId(99L);
        s.setNome(CartaoTestConstants.NOME_PADRAO);
        s.setEmail(CartaoTestConstants.EMAIL_PADRAO);
        s.setStatus(status);
        s.setMotivoRejeicao(CartaoTestConstants.MOTIVO_DADOS_INVALIDOS);
        s.setTipoCartao(TipoCartao.CONTA);
        s.setTipoEmissao(TipoEmissao.FISICO);
        s.setDataSolicitacao(AGORA_BASE.minusDays(1));
        s.setUltimaDataProcessamento(AGORA_BASE);
        s.setMensagemSolicitacao(CartaoTestConstants.MENSAGEM_SOLICITACAO_RECEBIDA);
        s.setClienteId(1L);
        s.setContaId(2L);
        return s;
    }

    public static SolicitacaoCartao criarSolicitacaoCartaoRejeitada(Long id) {
        SolicitacaoCartao s = new SolicitacaoCartao();
        s.setId(id);
        s.setCartaoId(null);
        s.setNome(CartaoTestConstants.NOME_PADRAO);
        s.setEmail(CartaoTestConstants.EMAIL_PADRAO);
        s.setStatus(StatusSolicitacao.REJEITADO);
        s.setMotivoRejeicao(CartaoTestConstants.MOTIVO_DADOS_INVALIDOS);
        s.setMensagemSolicitacao(CartaoTestConstants.MENSAGEM_SOLICITACAO_REJEITADA);
        s.setTipoCartao(TipoCartao.CONTA);
        s.setTipoEmissao(TipoEmissao.FISICO);
        s.setDataSolicitacao(AGORA_BASE.minusDays(1));
        s.setUltimaDataProcessamento(AGORA_BASE);
        s.setClienteId(1L);
        s.setContaId(2L);
        return s;
    }

    public static AlterarStatusRequestDTO criarAlterarStatusRequestDTO() {
        AlterarStatusRequestDTO dto = new AlterarStatusRequestDTO();
        dto.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        dto.setCvv(CartaoTestConstants.CVV_PADRAO);
        dto.setNovoStatus(StatusCartao.ATIVADO);
        return dto;
    }

    public static CadastrarCartaoExistenteRequestDTO criarCadastrarCartaoExistenteRequestDTO() {
        CadastrarCartaoExistenteRequestDTO dto = new CadastrarCartaoExistenteRequestDTO();
        dto.setClienteId(1L);
        dto.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        dto.setCvv(CartaoTestConstants.CVV_PADRAO);
        dto.setDataVencimento(CartaoTestConstants.VENCIMENTO_PADRAO);
        dto.setStatus(StatusCartao.ATIVADO);
        dto.setMotivoStatus(CartaoTestConstants.MOTIVO_STATUS_MIGRADO);
        dto.setTipoCartao(TipoCartao.DEBITO);
        dto.setTipoEmissao(TipoEmissao.FISICO);
        dto.setLimite(CartaoTestConstants.LIMITE_PADRAO);
        return dto;
    }

    public static CartaoResponseDTO criarCartaoResponseDTO() {
        CartaoResponseDTO dto = new CartaoResponseDTO();
        dto.setClienteId(1L);
        dto.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        dto.setCvv(CartaoTestConstants.CVV_PADRAO);
        dto.setDataVencimento(AGORA_BASE.plusYears(3));
        dto.setDataCriacao(AGORA_BASE);
        dto.setStatus(StatusCartao.ATIVADO);
        dto.setMotivoStatus(CartaoTestConstants.MOTIVO_STATUS_ATIVADO);
        dto.setTipoCartao(TipoCartao.CONTA);
        dto.setTipoEmissao(TipoEmissao.FISICO);
        dto.setLimite(CartaoTestConstants.LIMITE_PADRAO);
        return dto;
    }

    public static ClienteContaCriadoDTO criarClienteContaCriadoDTO() {
        ClienteContaCriadoDTO dto = new ClienteContaCriadoDTO();
        dto.setClienteId(1L);
        dto.setContaId(2L);
        dto.setNome(CartaoTestConstants.NOME_PADRAO);
        dto.setCpf(CartaoTestConstants.CPF_PADRAO);
        dto.setEmail(CartaoTestConstants.EMAIL_PADRAO);
        dto.setTipoCartao(TipoCartao.CONTA);
        dto.setTipoEmissao(TipoEmissao.FISICO);
        return dto;
    }

    public static SegundaViaCartaoRequestDTO criarSegundaViaCartaoRequestDTO() {
        SegundaViaCartaoRequestDTO dto = new SegundaViaCartaoRequestDTO();
        dto.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        dto.setCvv(CartaoTestConstants.CVV_PADRAO);
        dto.setMotivo(CartaoTestConstants.MOTIVO_CARTAO_DANIFICADO);
        return dto;
    }

    public static SegundaViaCartaoRequestDTO criarSegundaViaCartaoRequestDTOInvalido() {
        SegundaViaCartaoRequestDTO dto = new SegundaViaCartaoRequestDTO();
        dto.setNumero("0000000000000000");
        dto.setCvv("000");
        dto.setMotivo("Cartão vencido");
        return dto;
    }

    public static Cartao criarSegundaViaCompleta(Cartao original, SegundaViaCartaoRequestDTO dto) {
        Cartao segundaVia = new Cartao();
        segundaVia.setClienteId(original.getClienteId());
        segundaVia.setContaId(original.getContaId());
        segundaVia.setSolicitacaoId(original.getSolicitacaoId());
        segundaVia.setNumero("6543210987654321");
        segundaVia.setCvv("999");
        segundaVia.setDataCriacao(AGORA_BASE);
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setTipoEmissao(original.getTipoEmissao());
        segundaVia.setMotivoStatus(MensagensErroConstantes.MOTIVO_CARTAO_SEGUNDA_VIA_GERADA + dto.getMotivo());
        segundaVia.setLimite(original.getLimite());
        return segundaVia;
    }


    public static Cartao criarSegundaViaCompleta(Cartao original,
                                                 SegundaViaCartaoRequestDTO dto,
                                                 LocalDateTime vencimentoDefinidoPelaPolitica) {
        Cartao segundaVia = criarSegundaViaCompleta(original, dto);
        segundaVia.setDataVencimento(vencimentoDefinidoPelaPolitica);
        return segundaVia;
    }

    public static Cartao criarCartaoOriginalSegundaVia() {
        Cartao c = new Cartao();
        c.setId(100L);
        c.setSolicitacaoId(99L);
        c.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        c.setCvv(CartaoTestConstants.CVV_PADRAO);
        c.setDataVencimento(CartaoTestConstants.VENCIMENTO_PADRAO);
        c.setDataCriacao(AGORA_BASE.minusYears(1));
        c.setStatus(StatusCartao.ATIVADO);
        c.setMotivoStatus(CartaoTestConstants.MOTIVO_STATUS_ATIVADO);
        c.setTipoCartao(TipoCartao.CONTA);
        c.setTipoEmissao(TipoEmissao.FISICO);
        c.setLimite(CartaoTestConstants.LIMITE_PADRAO);
        c.setClienteId(1L);
        c.setContaId(2L);
        return c;
    }


    public static List<Cartao> criarListaCartoes(int quantidade) {
        List<Cartao> cartoes = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            Cartao c = criarCartaoCompleto();
            c.setNumero("400000000000" + String.format("%04d", i));
            cartoes.add(c);
        }
        return cartoes;
    }

    public static List<CartaoResponseDTO> criarListaCartaoResponseDTO(List<Cartao> cartoes) {
        return cartoes.stream()
                .map(cartao -> {
                    CartaoResponseDTO dto = criarCartaoResponseDTO();
                    dto.setNumero(cartao.getNumero());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
