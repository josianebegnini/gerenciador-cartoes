package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.exception.*;
import com.example.gw_gerenciador_cartoes.service.validator.CriarCartaoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;
    private final CartaoMapper mapper;
    private final CartaoGenerator cartaoGenerator;
    private final SolicitacaoCartaoServicePort solicitacaoCartaoService;
    private final CartaoEmailService cartaoEmailService;
    private final CriarCartaoValidator criarCartaoValidator;

    public CartaoService(CartaoRepositoryPort repository, CartaoMapper mapper, CartaoGenerator cartaoGenerator, SolicitacaoCartaoService solicitacaoCartaoService, CartaoEmailService cartaoEmailService, CriarCartaoValidator criarCartaoValidator) {
        this.repository = repository;
        this.mapper = mapper;
        this.cartaoGenerator = cartaoGenerator;
        this.solicitacaoCartaoService = solicitacaoCartaoService;
        this.cartaoEmailService = cartaoEmailService;
        this.criarCartaoValidator = criarCartaoValidator;
    }

    @Override
    public void processarSolicitacao(ClienteContaCriadoDTO dto) {
        SolicitacaoCartao solicitacao = solicitacaoCartaoService.salvar(
                dto.getClienteId(),
                dto.getContaId(),
                dto.getTipoCartao(),
                dto.getTipoEmissao(),
                dto.getNome());
        criarCartaoValidator.validar(dto, solicitacao.getId());
        Long cartaoId = criarCartao(dto, solicitacao.getId());
        solicitacaoCartaoService.finalizarComoProcessada(solicitacao.getId(), cartaoId);
    }

    private Long criarCartao(ClienteContaCriadoDTO dto, Long solicitacaoId) {
        Cartao cartao = new Cartao();
        cartao.setSolicitacaoId(solicitacaoId);
        cartao.setClienteId(dto.getClienteId());
        cartao.setContaId(dto.getContaId());
        cartao.setNome(dto.getNome());
        cartao.setEmail(dto.getEmail());
        cartao.setNumero(gerarNumeroCartaoUnico());
        cartao.setCvv(cartaoGenerator.gerarCvv());
        cartao.setDataVencimento(calcularDataVencimentoNovoCartao());
        cartao.setDataCriacao(LocalDateTime.now());
        cartao.setTipoCartao(dto.getTipoCartao());
        cartao.setTipoEmissao(dto.getTipoEmissao());

        cartao.atualizarStatus(StatusCartao.DESATIVADO, MensagensErroConstantes.MOTIVO_CARTAO_DESATIVADO_APOS_GERACAO);

        if (cartao.eCredito()) {
            cartao.setLimite(new BigDecimal("2000.00"));
        }

        Cartao cartaoCriado = repository.salvar(cartao);
        cartaoEmailService.enviarEmailCartaoCriado(cartaoCriado);

        return cartaoCriado.getId();
    }

    private String gerarNumeroCartaoUnico() {
        String numero;
        do {
            numero = cartaoGenerator.gerarNumeroCartao();
        } while (repository.existePorNumero(numero));
        return numero;
    }

    @Override
    public CartaoResponseDTO ativar(CartaoRequestDTO dto) {
        Cartao cartao = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv());

        if (cartao.getStatus() != StatusCartao.DESATIVADO) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_ATIVACAO_STATUS_INVALIDO);
        }

        cartao.atualizarStatus(StatusCartao.ATIVADO, MensagensErroConstantes.MOTIVO_CARTAO_ATIVADO);
        cartao.setDataCriacao(LocalDateTime.now());

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        cartaoEmailService.enviarEmailCartaoAtivado(atualizado);

        return mapper.toCartaoResponseDTO(atualizado);
    }

    @Override
    public CartaoResponseDTO bloquear(CartaoRequestDTO dto) {
        Cartao cartao = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv());

        if (cartao.getStatus() != StatusCartao.ATIVADO) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_BLOQUEAR_STATUS_INVALIDO);
        }

        String motivoBloqueio = MensagensErroConstantes.MOTIVO_CARTAO_BLOQUEADO_SEGURANCA;
        cartao.atualizarStatus(StatusCartao.BLOQUEADO, motivoBloqueio);

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        cartaoEmailService.enviarEmailCartaoBloqueado(atualizado, motivoBloqueio);

        return mapper.toCartaoResponseDTO(atualizado);
    }

    @Override
    public SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto) {

        Cartao original = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv());

        if (original.getStatus() != StatusCartao.ATIVADO && original.getStatus() != StatusCartao.BLOQUEADO) {
            throw new RegraNegocioException(MensagensErroConstantes.SEGUNDA_VIA_STATUS_INVALIDO);
        }

        original.atualizarStatus(StatusCartao.CANCELADO, MensagensErroConstantes.MOTIVO_CARTAO_CANCELADO_SEGUNDA_VIA);
        repository.atualizar(original);

        Cartao segundaVia = criarSegundaVia(original, dto);

        Cartao cartaoSalvo = repository.salvar(segundaVia);

        cartaoEmailService.enviarEmailSegundaVia(cartaoSalvo);

        return mapper.toSegundaViaResponseDTO(cartaoSalvo);
    }

    private Cartao criarSegundaVia(Cartao original, SegundaViaCartaoRequestDTO dto) {
        Cartao segundaVia = new Cartao();
        segundaVia.setClienteId(original.getClienteId());
        segundaVia.setContaId(original.getContaId());
        segundaVia.setSolicitacaoId(original.getSolicitacaoId());
        segundaVia.setNome(original.getNome());
        segundaVia.setEmail(original.getEmail());
        segundaVia.setNumero(gerarNumeroCartaoUnico());
        segundaVia.setCvv(cartaoGenerator.gerarCvv());
        segundaVia.setDataVencimento(calcularDataVencimentoSegundaVia(original.getDataVencimento()));
        segundaVia.setDataCriacao(LocalDateTime.now());
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setTipoEmissao(original.getTipoEmissao());
        segundaVia.setMotivoStatus(MensagensErroConstantes.MOTIVO_CARTAO_SEGUNDA_VIA_GERADA + dto.getMotivoSegundaVia());
        segundaVia.setLimite(original.getLimite());
        return segundaVia;
    }

    private LocalDateTime calcularDataVencimentoNovoCartao() {
        return LocalDateTime.now().plusYears(3);
    }

    private LocalDateTime calcularDataVencimentoSegundaVia(LocalDateTime dataCriacaoOriginal) {
        long diasDesdeCriacao = Duration.between(dataCriacaoOriginal, LocalDateTime.now()).toDays();

        if (diasDesdeCriacao < 30) {
            return dataCriacaoOriginal.plusDays(1);
        } else {
            return LocalDateTime.now().plusYears(3);
        }
    }

    @Override
    public Cartao buscarCartaoPorNumeroECvv(String numero, String cvv) {
        return repository.buscarCartaoPorNumeroECvv(numero, cvv)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));
    }

    @Override
    public Page<CartaoClienteResponseDTO> buscarPorCliente(Long clienteId, Pageable pageable) {
        Page<Cartao> cartoes = repository.buscarPorIdCliente(clienteId, pageable);
        return cartoes.map(mapper::toCartaoClienteResponseDTO);
    }
}
