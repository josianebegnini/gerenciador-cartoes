package com.example.gw_gerenciador_cartoes.infra.adapter;

import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import com.example.gw_gerenciador_cartoes.infra.exception.CartaoNotFoundException;
import com.example.gw_gerenciador_cartoes.infra.repository.CartaoRepositoryJpa;
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
    public Cartao salvar(Cartao cartao) {
        CartaoEntity entity = mapper.toEntity(cartao);
        CartaoEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);

    }

    @Override
    public Optional<Cartao> atualizar(Cartao cartao) {
        return jpaRepository.findById(cartao.getId())
                .map(entity -> {
                    CartaoEntity atualizado = mapper.toEntity(cartao);
                    atualizado.setId(entity.getId());
                    CartaoEntity saved = jpaRepository.save(atualizado);
                    return mapper.toDomain(saved);
                });
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

    @Override
    public Optional<Cartao> atualizarStatus(Long cartaoId, StatusCartao novoStatus, String motivo) {
        return jpaRepository.findById(cartaoId)
                .map(entity -> {
                    Cartao cartao = mapper.toDomain(entity);
                    cartao.atualizarStatus(novoStatus, motivo);
                    CartaoEntity atualizado = mapper.toEntity(cartao);
                    CartaoEntity saved = jpaRepository.save(atualizado);
                    return mapper.toDomain(saved);
                });
    }
}
