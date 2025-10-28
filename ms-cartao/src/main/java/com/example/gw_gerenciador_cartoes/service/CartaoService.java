package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.*;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapperDTO;
import com.example.gw_gerenciador_cartoes.domain.enums.CategoriaCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.exception.CartaoNotFoundException;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;
    private final CartaoMapperDTO mapper;
    private final CartaoGenerator cartaoGenerator;

    public CartaoService(CartaoRepositoryPort repository, CartaoMapperDTO mapper, CartaoGenerator cartaoGenerator) {
        this.repository = repository;
        this.mapper = mapper;
        this.cartaoGenerator = cartaoGenerator;
    }

    @Override
    public void gerar(Long clienteId) {
        Cartao cartao = new Cartao();
        cartao.setNumero(gerarNumeroCartaoUnico());
        cartao.setCvv(cartaoGenerator.gerarCvv());
        cartao.setClienteId(clienteId);
        cartao.setDataVencimento(calcularDataVencimento());
        cartao.setStatus(StatusCartao.DESATIVADO);
        cartao.setCategoriaCartao(CategoriaCartao.DEBITO);
        cartao.setTipoCartao(TipoCartao.FISICO);

        repository.salvar(cartao);
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
    public CartaoResponseDTO bloquear(CartaoIdentificacaoRequestDTO dto) {

        Cartao cartao = repository.buscarPorNumeroECvv(dto.getNumero(), dto.getCvv())
                .orElseThrow(() -> new CartaoNotFoundException(MensagensErroConstantes.CARTAO_NAO_ENCONTRADO));

        if (cartao.getStatus() != StatusCartao.ATIVADO) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_BLOQUEAR_STATUS_INVALIDO);
        }

        cartao.setStatus(StatusCartao.BLOQUEADO);

        Cartao atualizado = repository.atualizar(cartao)
                .orElseThrow(() -> new RegraNegocioException(MensagensErroConstantes.CARTAO_ERRO_AO_BLOQUEAR));

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
        segundaVia.setDataVencimento(calcularNovaDataVencimento(original.getDataVencimento()));
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setCategoriaCartao(original.getCategoriaCartao());
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setMotivoSegundaVia(dto.getMotivoSegundaVia());
        return segundaVia;
    }

    private LocalDate calcularNovaDataVencimento(LocalDate dataOriginal) {
        LocalDate novaData = LocalDate.now().plusYears(3);
        return novaData.isAfter(dataOriginal) ? novaData : dataOriginal.plusYears(1);
    }

    public Optional<Cartao> buscarCartaoPorNumeroECvv(String numero, String cvv) {
        return repository.buscarPorNumeroECvv(numero, cvv);
    }

    @Override
    public Page<CartaoResponseDTO> buscarPorCliente(Long idCliente, Pageable pageable) {
        Page<Cartao> cartoes = repository.buscarPorIdCliente(idCliente, pageable);
        return cartoes.map(mapper::toCartaoResponseDTO);
    }

}
