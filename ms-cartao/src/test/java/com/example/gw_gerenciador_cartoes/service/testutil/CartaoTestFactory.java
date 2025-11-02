package com.example.gw_gerenciador_cartoes.service.testutil;

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

    public static Cartao criarCartaoCompleto() {
        Cartao cartao = new Cartao();
        cartao.setId(100L);
        cartao.setSolicitacaoId(99L);
        cartao.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        cartao.setCvv(CartaoTestConstants.CVV_PADRAO);
        cartao.setDataVencimento(LocalDateTime.now().plusYears(3));
        cartao.setDataCriacao(LocalDateTime.now());
        cartao.setStatus(StatusCartao.ATIVADO);
        cartao.setMotivoStatus(CartaoTestConstants.MOTIVO_STATUS_ATIVADO);
        cartao.setTipoCartao(TipoCartao.CONTA);
        cartao.setTipoEmissao(TipoEmissao.FISICO);
        cartao.setLimite(new BigDecimal(String.valueOf(CartaoTestConstants.LIMITE_PADRAO)));
        cartao.setClienteId(1L);
        cartao.setContaId(2L);
        return cartao;
    }

    public static SolicitacaoCartao criarSolicitacaoCartaoCompleto(Long id, StatusSolicitacao status) {
        SolicitacaoCartao solicitacao = new SolicitacaoCartao();
        solicitacao.setId(id);
        solicitacao.setCartaoId(99L);
        solicitacao.setNome(CartaoTestConstants.NOME_PADRAO);
        solicitacao.setEmail(CartaoTestConstants.EMAIL_PADRAO);
        solicitacao.setStatus(status);
        solicitacao.setMotivoRejeicao(CartaoTestConstants.MOTIVO_DADOS_INVALIDOS);
        solicitacao.setTipoCartao(TipoCartao.CONTA);
        solicitacao.setTipoEmissao(TipoEmissao.FISICO);
        solicitacao.setDataSolicitacao(LocalDateTime.now().minusDays(1));
        solicitacao.setUltimaDataProcessamento(LocalDateTime.now());
        solicitacao.setMensagemSolicitacao(CartaoTestConstants.MENSAGEM_SOLICITACAO_RECEBIDA);
        solicitacao.setClienteId(1L);
        solicitacao.setContaId(2L);
        return solicitacao;
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
        dto.setLimite(new BigDecimal(String.valueOf(CartaoTestConstants.LIMITE_PADRAO)));
        return dto;
    }

    public static CartaoResponseDTO criarCartaoResponseDTO() {
        CartaoResponseDTO dto = new CartaoResponseDTO();
        dto.setClienteId(1L);
        dto.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        dto.setCvv(CartaoTestConstants.CVV_PADRAO);
        dto.setDataVencimento(LocalDateTime.now().plusYears(3));
        dto.setDataCriacao(LocalDateTime.now());
        dto.setStatus(StatusCartao.ATIVADO);
        dto.setMotivoStatus(CartaoTestConstants.MOTIVO_STATUS_ATIVADO);
        dto.setTipoCartao(TipoCartao.CONTA);
        dto.setTipoEmissao(TipoEmissao.FISICO);
        dto.setLimite(new BigDecimal(String.valueOf(CartaoTestConstants.LIMITE_PADRAO)));
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
        dto.setMotivoSegundaVia(CartaoTestConstants.MOTIVO_CARTAO_DANIFICADO);
        return dto;
    }

    public static Cartao criarSegundaViaCompleta(Cartao original, SegundaViaCartaoRequestDTO dto) {
        Cartao segundaVia = new Cartao();
        segundaVia.setClienteId(original.getClienteId());
        segundaVia.setContaId(original.getContaId());
        segundaVia.setSolicitacaoId(original.getSolicitacaoId());
        segundaVia.setNumero("6543210987654321"); // Simula número gerado
        segundaVia.setCvv("999"); // Simula CVV gerado
        segundaVia.setDataVencimento(original.getDataVencimento().plusYears(3));
        segundaVia.setDataCriacao(LocalDateTime.now());
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setTipoEmissao(original.getTipoEmissao());
        segundaVia.setMotivoStatus(MensagensErroConstantes.MOTIVO_CARTAO_SEGUNDA_VIA_GERADA + dto.getMotivoSegundaVia());
        segundaVia.setLimite(original.getLimite());
        return segundaVia;
    }

    public static SolicitacaoCartao criarSolicitacaoCartaoRejeitada(Long id) {
        SolicitacaoCartao solicitacao = new SolicitacaoCartao();
        solicitacao.setId(id);
        solicitacao.setCartaoId(null);
        solicitacao.setNome(CartaoTestConstants.NOME_PADRAO);
        solicitacao.setEmail(CartaoTestConstants.EMAIL_PADRAO);
        solicitacao.setStatus(StatusSolicitacao.REJEITADO);
        solicitacao.setMotivoRejeicao(CartaoTestConstants.MOTIVO_DADOS_INVALIDOS);
        solicitacao.setMensagemSolicitacao(CartaoTestConstants.MENSAGEM_SOLICITACAO_REJEITADA);
        solicitacao.setTipoCartao(TipoCartao.CONTA);
        solicitacao.setTipoEmissao(TipoEmissao.FISICO);
        solicitacao.setDataSolicitacao(LocalDateTime.now().minusDays(1));
        solicitacao.setUltimaDataProcessamento(LocalDateTime.now());
        solicitacao.setClienteId(1L);
        solicitacao.setContaId(2L);
        return solicitacao;
    }

    public static SegundaViaCartaoRequestDTO criarSegundaViaCartaoRequestDTOInvalido() {
        SegundaViaCartaoRequestDTO dto = new SegundaViaCartaoRequestDTO();
        dto.setNumero("0000000000000000");
        dto.setCvv("000");
        dto.setMotivoSegundaVia("Cartão vencido");
        return dto;
    }

    public static Cartao criarCartaoOriginalSegundaVia() {
        Cartao cartao = new Cartao();
        cartao.setId(100L);
        cartao.setSolicitacaoId(99L);
        cartao.setNumero(CartaoTestConstants.NUMERO_PADRAO);
        cartao.setCvv(CartaoTestConstants.CVV_PADRAO);
        cartao.setDataVencimento(CartaoTestConstants.VENCIMENTO_PADRAO);
        cartao.setDataCriacao(LocalDateTime.now().minusYears(1));
        cartao.setStatus(StatusCartao.ATIVADO);
        cartao.setMotivoStatus(CartaoTestConstants.MOTIVO_STATUS_ATIVADO);
        cartao.setTipoCartao(TipoCartao.CONTA);
        cartao.setTipoEmissao(TipoEmissao.FISICO);
        cartao.setLimite(CartaoTestConstants.LIMITE_PADRAO);
        cartao.setClienteId(1L);
        cartao.setContaId(2L);
        return cartao;
    }

    public static Cartao criarCartaoComNumero(String numero) {
        Cartao cartao = criarCartaoCompleto();
        cartao.setNumero(numero);
        return cartao;
    }

    public static Cartao criarCartaoComStatusInvalidoParaSegundaVia() {
        Cartao cartao = criarCartaoCompleto();
        cartao.setStatus(StatusCartao.CANCELADO);
        cartao.setMotivoStatus("Cartão cancelado pelo cliente");
        return cartao;
    }

    public static List<Cartao> criarListaCartoes(int quantidade) {
        List<Cartao> cartoes = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            Cartao cartao = criarCartaoCompleto();
            cartao.setNumero("400000000000" + String.format("%04d", i));
            cartoes.add(cartao);
        }
        return cartoes;
    }

    public static List<CartaoResponseDTO> criarListaCartaoResponseDTO(List<Cartao> cartoes) {
        return cartoes.stream().map(cartao -> {
            CartaoResponseDTO dto = criarCartaoResponseDTO();
            dto.setNumero(cartao.getNumero());
            return dto;
        }).collect(Collectors.toList());
    }

}
