package com.example.gw_gerenciador_cartoes.infra.adapter;

import com.example.gw_gerenciador_cartoes.application.mapper.CartaoMapper;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import com.example.gw_gerenciador_cartoes.infra.repository.CartaoRepositoryJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Optional<Cartao> buscarCartaoPorNumeroECvv(String numero, String cvv) {
        return jpaRepository.findByNumeroAndCvv(numero, cvv)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Cartao> buscarPorIdCliente(Long idCliente, Pageable pageable) {
        return jpaRepository.findByClienteId(idCliente, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Cartao> buscarTodos(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);

    }

    @Override
    public Page<Cartao> buscarPorFiltros(Long clienteId, String numero, String cvv, Pageable pageable) {
        Specification<CartaoEntity> spec = (root, query, cb) -> cb.conjunction();

        if (clienteId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("clienteId"), clienteId));
        }
        if (numero != null && !numero.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("numero"), numero));
        }
        if (cvv != null && !cvv.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cvv"), cvv));
        }

        return jpaRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }
}
