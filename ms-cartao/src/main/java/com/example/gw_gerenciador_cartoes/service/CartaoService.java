package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.*;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapperDTO;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.exception.CartaoNotFoundException;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.infra.messaging.CartaoRespostaPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;
    private final CartaoMapperDTO mapper;
    private final CartaoGenerator cartaoGenerator;
    private final CartaoRespostaPublisher respostaPublisher;
    private final CartaoRespostaService rejeitadoService;

    public CartaoService(CartaoRepositoryPort repository, CartaoMapperDTO mapper, CartaoGenerator cartaoGenerator, CartaoRespostaPublisher respostaPublisher, CartaoRespostaService rejeitadoService) {
        this.repository = repository;
        this.mapper = mapper;
        this.cartaoGenerator = cartaoGenerator;
        this.respostaPublisher = respostaPublisher;
        this.rejeitadoService = rejeitadoService;
    }

    @Override
    public void gerar(CriarCartaoMessageDTO dto) {

        Cartao cartao = new Cartao();
//        CriarCartaoResponseDTO responseDTO = new CriarCartaoResponseDTO();
//        responseDTO.setClienteId(dto.getClienteId());
//        responseDTO.setContaId(dto.getContaId());

        try {
            // Validações obrigatórias
            validarCamposObrigatorios(dto);

            // Validação dos enums
            TipoCartao tipoCartao = validarEnumObrigatorio(TipoCartao.class, dto.getTipoCartao(), "TipoCartao");
            TipoEmissaoCartao tipoEmissao = validarEnumObrigatorio(TipoEmissaoCartao.class, dto.getTipoEmissao(), "TipoEmissaoCartao");

            // Criação do cartão válido
            cartao.setNumero(gerarNumeroCartaoUnico());
            cartao.setCvv(cartaoGenerator.gerarCvv());
            cartao.setDataVencimento(calcularDataVencimento());
            cartao.setClienteId(dto.getClienteId());
            cartao.setContaId(dto.getContaId());
            cartao.setTipoCartao(tipoCartao);
            cartao.setTipoEmissao(tipoEmissao);
            cartao.atualizarStatus(StatusCartao.DESATIVADO, "Cartão gerado e aguardando ativação");

            // Persistência
            repository.salvar(cartao);

            // Resposta de sucesso
//            responseDTO.setSucesso(true);
//            responseDTO.setMensagem("Cartão gerado com sucesso.");
//            respostaPublisher.enviarResposta(responseDTO);

        } catch (Exception e) {
            // Criação do cartão rejeitado — sem gerar número, CVV ou vencimento
            cartao.setClienteId(dto.getClienteId());
            cartao.setContaId(dto.getContaId());
            cartao.setTipoCartao(tentarConverterEnum(TipoCartao.class, dto.getTipoCartao()));
            cartao.setTipoEmissao(tentarConverterEnum(TipoEmissaoCartao.class, dto.getTipoEmissao()));
            cartao.atualizarStatus(StatusCartao.REJEITADO, e.getMessage());

            // Persistência do cartão rejeitado
            repository.salvar(cartao);

            // Resposta de erro
//            responseDTO.setSucesso(false);
//            responseDTO.setMensagem("Erro ao gerar cartão: " + e.getMessage());
//            respostaPublisher.enviarResposta(responseDTO);

            // Propaga exceção
            throw e;
        }

    }

    private String gerarNumeroCartaoUnico() {
        String numero;
        do {
            numero = cartaoGenerator.gerarNumeroCartao();
        } while (repository.existePorNumero(numero));
        return numero;
    }

    private LocalDate calcularDataVencimento() {
        return LocalDate.now().plusYears(3);
    }

    @Override
    public CartaoResponseDTO ativar(CartaoIdentificacaoRequestDTO dto) {
        Cartao cartao = repository.buscarPorNumeroECvv(dto.getNumero(), dto.getCvv())
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        if (cartao.getStatus() != StatusCartao.DESATIVADO) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_ATIVACAO_STATUS_INVALIDO);
        }

        cartao.setStatus(StatusCartao.ATIVADO);

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new RegraNegocioException(MensagensErroConstantes.CARTAO_ERRO_AO_ATIVAR));

        return mapper.toCartaoResponseDTO(atualizado);
    }

    @Override
    public SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto) {

        Cartao original = buscarCartaoPorNumeroECvv(dto.getNumero(), dto.getCvv())
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        if (original.getStatus() != StatusCartao.ATIVADO && original.getStatus() != StatusCartao.BLOQUEADO) {
            throw new RegraNegocioException(MensagensErroConstantes.SEGUNDA_VIA_STATUS_INVALIDO);
        }

        original.setStatus(StatusCartao.CANCELADO);
        repository.atualizar(original)
                .orElseThrow(() -> new RegraNegocioException(MensagensErroConstantes.CARTAO_ERRO_AO_ATUALIZAR_CARTAO_ORIGINAL));

        Cartao segundaVia = criarSegundaVia(original, dto);

        Cartao cartaoSalvo = repository.salvar(segundaVia)
                .orElseThrow(() -> new RegraNegocioException(MensagensErroConstantes.CARTAO_ERRO_AO_SALVAR_SEGUNDA_VIA));

        return mapper.toSegundaViaResponseDTO(cartaoSalvo);
    }

    private Cartao criarSegundaVia(Cartao original, SegundaViaCartaoRequestDTO dto) {
        Cartao segundaVia = new Cartao();
        segundaVia.setNumero(gerarNumeroCartaoUnico());
        segundaVia.setCvv(cartaoGenerator.gerarCvv());
        segundaVia.setClienteId(original.getClienteId());
        segundaVia.setContaId(original.getContaId());
        segundaVia.setDataVencimento(calcularNovaDataVencimento(original.getDataVencimento()));
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setTipoEmissao(original.getTipoEmissao());
        segundaVia.setMotivoStatus(dto.getMotivoSegundaVia());
        return segundaVia;
    }

    private LocalDate calcularNovaDataVencimento(LocalDate dataOriginal) {
        LocalDate novaData = LocalDate.now().plusYears(3);
        return novaData.isAfter(dataOriginal) ? novaData : dataOriginal.plusYears(1);
    }

    public Optional<Cartao> buscarCartaoPorNumeroECvv(String numero, String cvv) {
        return repository.buscarPorNumeroECvv(numero, cvv);
    }

    private void validarCamposObrigatorios(CriarCartaoMessageDTO dto) {

        if (dto.getClienteId() == null) {
            throw new IllegalArgumentException("Campo clienteId é obrigatório.");
        }
        if (dto.getContaId() == null) {
            throw new IllegalArgumentException("Campo contaId é obrigatório.");
        }
        if (isBlank(dto.getNome())) {
            throw new IllegalArgumentException("Campo nome é obrigatório.");
        }
        if (isBlank(dto.getCpf())) {
            throw new IllegalArgumentException("Campo CPF é obrigatório.");
        }
        if (isBlank(dto.getEmail())) {
            throw new IllegalArgumentException("Campo e-mail é obrigatório.");
        }
        if (dto.getTipoCartao() == null) {
            throw new IllegalArgumentException("Campo tipoCartao é obrigatório.");
        }
        if (dto.getTipoEmissao() == null) {
            throw new IllegalArgumentException("Campo tipoEmissao é obrigatório.");
        }

        if (!isEmailValido(dto.getEmail())) {
            throw new RegraNegocioException("E-mail inválido.");
        }

        if (!isCpfValido(dto.getCpf())) {
            throw new RegraNegocioException("CPF inválido.");
        }

    }

    private boolean isEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private boolean isCpfValido(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void validarEnums(CriarCartaoMessageDTO dto) {
        validarEnumObrigatorio(TipoCartao.class, dto.getTipoCartao(), "TipoCartao");
        validarEnumObrigatorio(TipoEmissaoCartao.class, dto.getTipoEmissao(), "TipoEmissaoCartao");
    }



    private <T extends Enum<T>> T validarEnumObrigatorio(Class<T> enumClass, String value, String campo) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String valoresValidos = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new RegraNegocioException("Valor inválido para " + campo + ": " + value +
                    ". Valores válidos: " + valoresValidos);
        }
    }

    private <T extends Enum<T>> T tentarConverterEnum(Class<T> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception ex) {
            return null;
        }
    }

}
