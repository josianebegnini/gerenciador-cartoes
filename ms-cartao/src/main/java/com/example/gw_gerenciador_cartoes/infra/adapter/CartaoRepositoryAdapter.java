package com.example.gw_gerenciador_cartoes.infra.adapter;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import com.example.gw_gerenciador_cartoes.infra.repository.CartaoRepositoryJpa;
import org.springframework.stereotype.Repository;

@Repository
public class CartaoRepositoryAdapter implements CartaoRepositoryPort {

    private final CartaoRepositoryJpa repository;

    public CartaoRepositoryAdapter(CartaoRepositoryJpa repository) {
        this.repository = repository;
    }

    public void salvar(Cartao cartao) {
        CartaoEntity entity = new CartaoEntity();
        entity.setNumero(cartao.getNumero());
        entity.setCvv(cartao.getCvv());
        entity.setClienteId(cartao.getClienteId());
        entity.setContaId(cartao.getContaId());
        entity.setDataVencimento(cartao.getDataVencimento());
        entity.setTipo(cartao.getTipo());
        entity.setStatus(cartao.getStatus());
        entity.setFisico(cartao.isFisico());
        repository.save(entity);
    }
}
