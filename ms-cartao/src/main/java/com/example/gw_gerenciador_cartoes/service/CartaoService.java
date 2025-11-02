package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.email.CartaoEmailService;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.exception.CartaoNotFoundException;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoStatusValidator;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoValidator;
import com.example.gw_gerenciador_cartoes.service.validator.PoliticaExpiracaoCartao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;
    private final SolicitacaoCartaoServicePort solicitacaoCartaoService;
    private final CartaoValidator cartaoValidator;
    private final DadosCartaoGenerator dadosCartaoGenerator;
    private final CartaoMapper mapper;
    private final CartaoEmailService cartaoEmailService;
    private final CartaoStatusValidator cartaoStatusValidator;
    private final CartaoCreatorService cartaoCreatorService;
    private final PoliticaExpiracaoCartao politicaExpiracaoCartao;

    public CartaoService(CartaoRepositoryPort repository, SolicitacaoCartaoServicePort solicitacaoCartaoService, CartaoValidator cartaoValidator, DadosCartaoGenerator dadosCartaoGenerator, CartaoMapper mapper, CartaoEmailService cartaoEmailService, CartaoStatusValidator cartaoStatusValidator, CartaoCreatorService cartaoCreatorService, PoliticaExpiracaoCartao politicaExpiracaoCartao) {
        this.repository = repository;
        this.solicitacaoCartaoService = solicitacaoCartaoService;
        this.cartaoValidator = cartaoValidator;
        this.dadosCartaoGenerator = dadosCartaoGenerator;
        this.mapper = mapper;
        this.cartaoEmailService = cartaoEmailService;
        this.cartaoStatusValidator = cartaoStatusValidator;
        this.cartaoCreatorService = cartaoCreatorService;
        this.politicaExpiracaoCartao = politicaExpiracaoCartao;
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
        Long cartaoId = cartaoCreatorService.criarCartao(dto, solicitacao.getId());
        solicitacaoCartaoService.finalizarComoProcessada(solicitacao.getId(), cartaoId);
    }

    @Override
    public CartaoResponseDTO alterarStatus(AlterarStatusRequestDTO dto) {
        Cartao cartao = repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        cartaoStatusValidator.validarAlteracaoStatus(cartao, dto.getNovoStatus());

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        enviarEmailStatusAtualizado(dto.getNovoStatus(), atualizado);

        return mapper.toCartaoResponseDTO(atualizado);
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
    public CartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto) {

        Cartao original = repository.buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        if (original.getStatus() != StatusCartao.ATIVADO && original.getStatus() != StatusCartao.BLOQUEADO) {
            throw new RegraNegocioException(MensagensErroConstantes.SEGUNDA_VIA_STATUS_INVALIDO);
        }

        original.atualizarStatus(StatusCartao.CANCELADO, MensagensErroConstantes.MOTIVO_CARTAO_CANCELADO_SEGUNDA_VIA);
        repository.atualizar(original);

        Cartao segundaVia = criarSegundaVia(original, dto);

        Cartao cartaoSalvo = repository.salvar(segundaVia);

        if (cartaoSalvo == null || cartaoSalvo.getId() == null) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR_SEGUNDA_VIA);
        }

        cartaoEmailService.enviarEmailSegundaVia(cartaoSalvo);

        return mapper.toCartaoResponseDTO(cartaoSalvo);
    }

    private Cartao criarSegundaVia(Cartao original, SegundaViaCartaoRequestDTO dto) {
        Cartao segundaVia = new Cartao();
        segundaVia.setClienteId(original.getClienteId());
        segundaVia.setContaId(original.getContaId());
        segundaVia.setSolicitacaoId(original.getSolicitacaoId());
        segundaVia.setNumero(cartaoCreatorService.gerarNumeroCartaoUnico());
        segundaVia.setCvv(dadosCartaoGenerator.gerarCvv());
        segundaVia.setDataVencimento(politicaExpiracaoCartao.calcularParaSegundaVia(original.getDataVencimento()));
        segundaVia.setDataCriacao(LocalDateTime.now());
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setTipoEmissao(original.getTipoEmissao());
        segundaVia.setMotivoStatus(MensagensErroConstantes.MOTIVO_CARTAO_SEGUNDA_VIA_GERADA + dto.getMotivoSegundaVia());
        segundaVia.setLimite(original.getLimite());
        return segundaVia;
    }

    @Override
    public CartaoResponseDTO buscarPorNumeroECvv(String numero, String cvv) {
        Cartao cartao = repository.buscarCartaoPorNumeroECvv(numero, cvv)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        return mapper.toCartaoResponseDTO(cartao);
    }

    @Override
    public Page<CartaoResponseDTO> buscarPorCliente(Long clienteId, Pageable pageable) {
        Page<Cartao> cartoes = repository.buscarPorIdCliente(clienteId, pageable);
        return cartoes.map(mapper::toCartaoResponseDTO);
    }

    @Override
    public CartaoResponseDTO cadastrarCartaoExistente(CadastrarCartaoExistenteRequestDTO dto) {

        cartaoValidator.validarCartaoNaoExiste(dto.getNumero(), dto.getCvv());

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

        if (cartaoSalvo == null || cartaoSalvo.getId() == null) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR);
        }

        return mapper.toCartaoResponseDTO(cartaoSalvo);
    }

    @Override
    public Page<CartaoResponseDTO> listarTodos(Pageable pageable) {
        return repository.buscarTodos(pageable)
                .map(mapper::toCartaoResponseDTO);
    }

    @Override
    public Page<CartaoResponseDTO> buscarCartoes(Long clienteId, String numero, String cvv, Pageable pageable) {
        Page<Cartao> cartoes = repository.buscarPorFiltros(clienteId, numero, cvv, pageable);
        return cartoes.map(mapper::toCartaoResponseDTO);
    }

}
