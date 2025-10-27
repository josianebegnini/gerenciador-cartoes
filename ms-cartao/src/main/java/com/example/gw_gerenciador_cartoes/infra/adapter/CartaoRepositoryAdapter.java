package com.example.gw_gerenciador_cartoes.infra.adapter;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import com.example.gw_gerenciador_cartoes.infra.repository.CartaoRepositoryJpa;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CartaoRepositoryAdapter implements CartaoRepositoryPort {

    private final CartaoRepositoryJpa jpaRepository;
    private final CartaoMapper mapper;

    public CartaoRepositoryAdapter(CartaoRepositoryJpa repository, CartaoMapper mapper) {
        this.jpaRepository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Cartao> salvar(Cartao cartao) {

        CartaoEntity entity = new CartaoEntity();
        entity.setClienteId(cartao.getClienteId());
        entity.setNumero(cartao.getNumero());
        entity.setCvv(cartao.getCvv());
        entity.setDataVencimento(cartao.getDataVencimento());
        entity.setCategoria(cartao.getCategoriaCartao());
        entity.setStatus(cartao.getStatus());
        entity.setTipo(cartao.getTipoCartao());
        entity.setMotivoSegundaVia(cartao.getMotivoSegundaVia());

        CartaoEntity savedEntity = jpaRepository.save(entity);
        return Optional.of(mapper.toDomain(savedEntity));

    }

    public Optional<Cartao> atualizar(Cartao cartao) {

        CartaoEntity entity = jpaRepository.findById(cartao.getId())
                .orElse(null);

        if (entity == null) {
            return Optional.empty();
        }

        entity.setClienteId(cartao.getClienteId());
        entity.setNumero(cartao.getNumero());
        entity.setCvv(cartao.getCvv());
        entity.setDataVencimento(cartao.getDataVencimento());
        entity.setCategoria(cartao.getCategoriaCartao());
        entity.setStatus(cartao.getStatus());
        entity.setTipo(cartao.getTipoCartao());

        CartaoEntity saved = jpaRepository.save(entity);
        return Optional.of(mapper.toDomain(saved));

    }

    @Override
    public boolean existePorNumero(String numero) {
        return jpaRepository.existsByNumero(numero);
    }

    @Override
    public Optional<Cartao> buscarPorNumeroECvv(String numero, String cvv) {
        return jpaRepository.findByNumeroAndCvv(numero, cvv)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Cartao> buscarPorIdCliente(Long idCliente, Pageable pageable) {
        return jpaRepository.findByClienteId(idCliente, pageable)
                .map(mapper::toDomain);
    }
}
