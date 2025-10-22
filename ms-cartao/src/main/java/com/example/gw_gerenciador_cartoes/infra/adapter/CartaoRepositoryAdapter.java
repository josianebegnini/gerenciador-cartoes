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

    @Override
    public void salvar(Cartao cartao) {
        CartaoEntity entity = new CartaoEntity();
        entity.setClienteId(cartao.getClienteId());
        entity.setNumero(cartao.getNumero());
        entity.setCvv(cartao.getCvv());
        entity.setDataVencimento(cartao.getDataVencimento());
        entity.setTipoConta(cartao.getTipoConta());
        entity.setStatus(cartao.getStatus());
        entity.setFormatoCartao(cartao.isFormatoCartao());

        repository.save(entity);
    }
}
