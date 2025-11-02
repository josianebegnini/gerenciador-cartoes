package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.email.CartaoEmailService;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.validator.PoliticaExpiracaoCartao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CartaoCreatorService {

    private final DadosCartaoGenerator dadosCartaoGenerator;
    private final CartaoRepositoryPort repository;
    private final CartaoEmailService cartaoEmailService;
    private final PoliticaExpiracaoCartao politicaExpiracaoCartao;

    public CartaoCreatorService(DadosCartaoGenerator dadosCartaoGenerator, CartaoRepositoryPort repository, CartaoEmailService cartaoEmailService, PoliticaExpiracaoCartao politicaExpiracaoCartao) {
        this.dadosCartaoGenerator = dadosCartaoGenerator;
        this.repository = repository;
        this.cartaoEmailService = cartaoEmailService;
        this.politicaExpiracaoCartao = politicaExpiracaoCartao;
    }

    public Long criarCartao(ClienteContaCriadoDTO dto, Long solicitacaoId) {
        Cartao cartao = new Cartao();
        cartao.setSolicitacaoId(solicitacaoId);
        cartao.setClienteId(dto.getClienteId());
        cartao.setContaId(dto.getContaId());
        cartao.setNumero(gerarNumeroCartaoUnico());
        cartao.setCvv(dadosCartaoGenerator.gerarCvv());
        cartao.setDataVencimento(politicaExpiracaoCartao.calcularParaCartaoNovo());
        cartao.setDataCriacao(LocalDateTime.now());
        cartao.setTipoCartao(dto.getTipoCartao());
        cartao.setTipoEmissao(dto.getTipoEmissao());

        cartao.atualizarStatus(StatusCartao.DESATIVADO, MensagensErroConstantes.MOTIVO_CARTAO_DESATIVADO_APOS_GERACAO);

        if (cartao.eCredito()) {
            cartao.setLimite(new BigDecimal("1000.00"));
        }

        Cartao cartaoCriado = repository.salvar(cartao);
        if (cartaoCriado == null || cartaoCriado.getId() == null) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR);
        }

        cartaoEmailService.enviarEmailCartaoCriado(cartaoCriado);

        return cartaoCriado.getId();
    }

    public String gerarNumeroCartaoUnico() {
        String numero;
        do {
            numero = dadosCartaoGenerator.gerarNumeroCartao();
        } while (repository.existePorNumero(numero));
        return numero;
    }

}
