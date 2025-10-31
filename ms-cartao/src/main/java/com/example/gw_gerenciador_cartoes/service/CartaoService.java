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
import com.example.gw_gerenciador_cartoes.service.validator.CartaoStatusValidator;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;
    private final SolicitacaoCartaoServicePort solicitacaoCartaoService;
    private final CartaoValidator cartaoValidator;
    private final CartaoGenerator cartaoGenerator;
    private final CartaoMapper mapper;
    private final CartaoEmailService cartaoEmailService;
    private final CartaoStatusValidator cartaoStatusValidator;

    public CartaoService(CartaoRepositoryPort repository, SolicitacaoCartaoServicePort solicitacaoCartaoService, CartaoValidator cartaoValidator, CartaoGenerator cartaoGenerator, CartaoMapper mapper, CartaoEmailService cartaoEmailService, CartaoStatusValidator cartaoStatusValidator) {
        this.repository = repository;
        this.solicitacaoCartaoService = solicitacaoCartaoService;
        this.cartaoValidator = cartaoValidator;
        this.cartaoGenerator = cartaoGenerator;
        this.mapper = mapper;
        this.cartaoEmailService = cartaoEmailService;
        this.cartaoStatusValidator = cartaoStatusValidator;
    }

    @Override
    public void processarSolicitacao(ClienteContaCriadoDTO dto) {
        SolicitacaoCartao solicitacao = solicitacaoCartaoService.salvar(
                dto.getClienteId(),
                dto.getContaId(),
                dto.getTipoCartao(),
                dto.getTipoEmissao(),
                dto.getNome(),
                dto.getEmail());
        cartaoValidator.validar(dto, solicitacao.getId());
        Long cartaoId = criarCartao(dto, solicitacao.getId());
        solicitacaoCartaoService.finalizarComoProcessada(solicitacao.getId(), cartaoId);
    }

    private Long criarCartao(ClienteContaCriadoDTO dto, Long solicitacaoId) {
        Cartao cartao = new Cartao();
        cartao.setSolicitacaoId(solicitacaoId);
        cartao.setClienteId(dto.getClienteId());
        cartao.setContaId(dto.getContaId());
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
    public CartaoInfoResponseDTO alterarStatus(AlterarStatusRequestDTO dto) {
        Cartao cartao = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv());

        cartaoStatusValidator.validarAlteracaoStatus(cartao, dto.getNovoStatus());

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        enviarEmailStatusAtualizado(dto.getNovoStatus(), atualizado);

        return mapper.toCartaoInfoResponseDTO(atualizado);
    }

    private void enviarEmailStatusAtualizado(StatusCartao status, Cartao cartao) {
        switch (status) {
            case ATIVADO:
                cartaoEmailService.enviarEmailCartaoAtivado(cartao);
                break;
            case BLOQUEADO:
                cartaoEmailService.enviarEmailCartaoBloqueado(cartao, MensagensErroConstantes.MOTIVO_CARTAO_BLOQUEADO_SEGURANCA);
                break;
        }
    }

    @Override
    public CartaoInfoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto) {

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

    private Cartao buscarCartaoPorNumeroECvv(String numero, String cvv) {
        return repository.buscarCartaoPorNumeroECvv(numero, cvv)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));
    }

    @Override
    public Page<CartaoClienteResponseDTO> buscarPorCliente(Long clienteId, Pageable pageable) {
        Page<Cartao> cartoes = repository.buscarPorIdCliente(clienteId, pageable);
        return cartoes.map(mapper::toCartaoClienteResponseDTO);
    }

    @Override
    public CartaoClienteResponseDTO cadastrarCartaoExistente(CadastrarCartaoExistenteRequestDTO dto) {
        Cartao cartao = new Cartao();
        cartao.setClienteId(dto.getClienteId());
        cartao.setContaId(0L);
        cartao.setSolicitacaoId(0L);
        cartao.setNumero(dto.getNumero());
        cartao.setCvv(dto.getCvv());
        cartao.setDataVencimento(dto.getDataVencimento());
        cartao.setDataCriacao(LocalDateTime.now());
        cartao.setStatus(dto.getStatus());
        cartao.setMotivoStatus(dto.getMotivoStatus());
        cartao.setTipoCartao(dto.getTipoCartao());
        cartao.setTipoEmissao(dto.getTipoEmissao());
        cartao.setLimite(dto.getLimite());

        Cartao cartaoSalvo = repository.salvar(cartao);
        return mapper.toCartaoClienteResponseDTO(cartaoSalvo);
    }

    @Override
    public Page<CartaoResponseDTO> listarTodos(Pageable pageable) {
        return repository.buscarTodos(pageable)
                .map(mapper::toCartaoResponseDTO);
    }

}
