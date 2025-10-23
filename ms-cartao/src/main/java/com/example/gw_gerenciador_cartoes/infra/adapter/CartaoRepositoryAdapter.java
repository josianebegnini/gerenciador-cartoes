package com.example.gw_gerenciador_cartoes.infra.adapter;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import com.example.gw_gerenciador_cartoes.infra.repository.CartaoRepositoryJpa;
import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
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
    public Optional<Cartao> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(entity -> new Cartao(
                        entity.getId(),
                        entity.getClienteId(),
                        entity.getNumero(),
                        entity.getCvv(),
                        entity.getDataVencimento(),
                        entity.getCategoria(),
                        entity.getStatus(),
                        entity.getTipo(),
                        entity.getMotivoSegundaVia()
                ));
    }

    @Override
    public boolean existePorNumero(String numero) {
        return jpaRepository.existsByNumero(numero);
    }

}
