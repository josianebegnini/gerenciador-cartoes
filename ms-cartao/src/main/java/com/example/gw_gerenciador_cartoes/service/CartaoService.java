package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.exception.*;
import com.example.gw_gerenciador_cartoes.infra.messaging.CartaoRespostaPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;
    private final CartaoMapper mapper;
    private final CartaoGenerator cartaoGenerator;
    private final CartaoRespostaPublisher respostaPublisher;
    private final SolicitacaoCartaoServicePort solicitacaoCartaoService;

    public CartaoService(CartaoRepositoryPort repository, CartaoMapper mapper, CartaoGenerator cartaoGenerator, CartaoRespostaPublisher respostaPublisher, SolicitacaoCartaoService solicitacaoCartaoService) {
        this.repository = repository;
        this.mapper = mapper;
        this.cartaoGenerator = cartaoGenerator;
        this.respostaPublisher = respostaPublisher;
        this.solicitacaoCartaoService = solicitacaoCartaoService;

    }

    @Override
    public void processarSolicitacao(CriarCartaoMessageDTO dto) {
        SolicitacaoCartao solicitacao = criarSolicitacaoInicial(dto);
        validarCriacaoCartao(dto, solicitacao.getId());
        Cartao cartao = criarCartao(dto, solicitacao.getId());
        finalizarComoProcessada(solicitacao, cartao.getId());

    }

    private SolicitacaoCartao criarSolicitacaoInicial(CriarCartaoMessageDTO dto) {
        return solicitacaoCartaoService.salvar(
                dto.getClienteId(),
                dto.getContaId(),
                dto.getTipoCartao(),
                dto.getTipoEmissao(),
                dto.getNome());
    }

    private void finalizarComoProcessada(SolicitacaoCartao solicitacao, Long idCartao) {
        solicitacao.setStatus(StatusSolicitacao.PROCESSADO);
        solicitacao.setCartaoId(idCartao);
        solicitacao.setUltimaDataProcessamento(LocalDateTime.now());
        solicitacaoCartaoService.update(solicitacao);
    }

    private Cartao criarCartao(CriarCartaoMessageDTO dto, Long solicitacaoId) {
        Cartao cartao = new Cartao();
        cartao.setSolicitacaoId(solicitacaoId);
        cartao.setClienteId(dto.getClienteId());
        cartao.setContaId(dto.getContaId());
        cartao.setNumero(gerarNumeroCartaoUnico());
        cartao.setCvv(cartaoGenerator.gerarCvv());
        cartao.setNome(dto.getNome());
        cartao.setDataVencimento(calcularDataVencimentoNovoCartao());
        cartao.setDataCriacao(LocalDateTime.now());
        cartao.setTipoCartao(TipoCartao.valueOf(dto.getTipoCartao().toUpperCase()));
        cartao.setTipoEmissao(TipoEmissaoCartao.valueOf(dto.getTipoEmissao().toUpperCase()));
        cartao.atualizarStatus(StatusCartao.DESATIVADO, "Cartão gerado e aguardando ativação");

        if (cartao.eCredito()) {
            cartao.setLimite(new BigDecimal("2000.00"));
        }

        return repository.salvar(cartao);
    }

    private void validarCriacaoCartao(CriarCartaoMessageDTO dto, Long solicitacaoId) {
        validarCamposObrigatorios(dto, solicitacaoId);
        validarEnums(dto);
    }

    private void validarCamposObrigatorios(CriarCartaoMessageDTO dto, Long solicitacaoId) {
        List<MensagemErro> mensagensErros = new ArrayList<>();

        if (Objects.isNull(dto.getClienteId())) {
            mensagensErros.add(new MensagemErro("Campo clienteId é obrigatório."));
        }
        if (Objects.isNull(dto.getContaId())) {
            mensagensErros.add(new MensagemErro("Campo contaId é obrigatório."));
        }
        if (isBlank(dto.getNome())) {
            mensagensErros.add(new MensagemErro("Campo nome é obrigatório."));
        }

        if (isBlank(dto.getEmail())) {
            mensagensErros.add(new MensagemErro("Campo e-mail é obrigatório."));
        } else if (!isEmailValido(dto.getEmail())) {
            mensagensErros.add(new MensagemErro("E-mail inválido: " + dto.getEmail()));
        }

        if (isBlank(dto.getTipoCartao())) {
            mensagensErros.add(new MensagemErro("Campo tipoCartao é obrigatório."));
        } else if (!isEnumValido(TipoCartao.class, dto.getTipoCartao())) {
            mensagensErros.add(new MensagemErro("Valor inválido para tipoCartao: " + dto.getTipoCartao()));
        }

        if (isBlank(dto.getTipoEmissao())) {
            mensagensErros.add(new MensagemErro("Campo tipoEmissao é obrigatório."));
        } else if (!isEnumValido(TipoEmissaoCartao.class, dto.getTipoEmissao())) {
            mensagensErros.add(new MensagemErro("Valor inválido para tipoEmissao: " + dto.getTipoEmissao()));
        }

        if (!mensagensErros.isEmpty()) {
            throw new CampoObrigatorioException(solicitacaoId, mensagensErros);
        }

    }

    private boolean isEmailValido(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private void validarEnums(CriarCartaoMessageDTO dto) {
        validarEnumObrigatorio(TipoCartao.class, dto.getTipoCartao(), "TipoCartao");
        validarEnumObrigatorio(TipoEmissaoCartao.class, dto.getTipoEmissao(), "TipoEmissaoCartao");
    }

    private boolean isCpfValido(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }


    private <T extends Enum<T>> boolean isEnumValido(Class<T> enumClass, String value) {
        if (value == null) return false;
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }

    private <T extends Enum<T>> T validarEnumObrigatorio(Class<T> enumClass, String value, String campo) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            String valoresValidos = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new RegraNegocioException("Valor inválido para " + campo + ": " + value +
                    ". Valores válidos: " + valoresValidos);
        }
    }

    private String gerarNumeroCartaoUnico() {
        String numero;
        do {
            numero = cartaoGenerator.gerarNumeroCartao();
        } while (repository.existePorNumero(numero));
        return numero;
    }

    @Override
    public CartaoResponseDTO ativar(CartaoIdentificacaoRequestDTO dto) {
        Cartao cartao = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv());

        if (cartao.getStatus() != StatusCartao.DESATIVADO) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_ATIVACAO_STATUS_INVALIDO);
        }

        cartao.atualizarStatus(StatusCartao.ATIVADO, "Cartão ativado");
        cartao.setDataCriacao(LocalDateTime.now());

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        return mapper.toCartaoResponseDTO(atualizado);
    }

    @Override
    public CartaoResponseDTO bloquear(CartaoIdentificacaoRequestDTO dto) {
        Cartao cartao = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv());

        if (cartao.getStatus() != StatusCartao.ATIVADO) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_BLOQUEAR_STATUS_INVALIDO);
        }

        String motivoBloqueio = "Cartão bloqueado por segurança. Motivos possíveis: perda, roubo ou suspeita de fraude.";
        cartao.atualizarStatus(StatusCartao.BLOQUEADO, motivoBloqueio);

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        return mapper.toCartaoResponseDTO(atualizado);
    }

    @Override
    public SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto) {

        Cartao original = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv());

        if (original.getStatus() != StatusCartao.ATIVADO && original.getStatus() != StatusCartao.BLOQUEADO) {
            throw new RegraNegocioException(MensagensErroConstantes.SEGUNDA_VIA_STATUS_INVALIDO);
        }

        original.atualizarStatus(StatusCartao.CANCELADO, "Cartão cancelado automaticamente após solicitação de segunda via." );
        repository.atualizar(original);

        Cartao segundaVia = criarSegundaVia(original, dto);

        Cartao cartaoSalvo = repository.salvar(segundaVia);
        if (cartaoSalvo == null) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_ERRO_AO_SALVAR_SEGUNDA_VIA);
        }
        return mapper.toSegundaViaResponseDTO(cartaoSalvo);
    }

    private Cartao criarSegundaVia(Cartao original, SegundaViaCartaoRequestDTO dto) {
        Cartao segundaVia = new Cartao();
        segundaVia.setClienteId(original.getClienteId());
        segundaVia.setContaId(original.getContaId());
        segundaVia.setSolicitacaoId(original.getSolicitacaoId());
        segundaVia.setNome(original.getNome());
        segundaVia.setNumero(gerarNumeroCartaoUnico());
        segundaVia.setCvv(cartaoGenerator.gerarCvv());
        segundaVia.setDataVencimento(calcularDataVencimentoSegundaVia(original.getDataVencimento()));
        segundaVia.setDataCriacao(LocalDateTime.now());
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setTipoEmissao(original.getTipoEmissao());
        segundaVia.setMotivoStatus(dto.getMotivoSegundaVia());
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
        return repository.buscarPorNumeroECvv(numero, cvv)
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));
    }

    @Override
    public Page<CartaoResponseDTO> buscarPorCliente(Long idCliente, Pageable pageable) {
        Page<Cartao> cartoes = repository.buscarPorIdCliente(idCliente, pageable);
        return cartoes.map(mapper::toCartaoResponseDTO);
    }
}
