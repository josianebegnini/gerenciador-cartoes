package com.example.gw_gerenciador_cartoes.application.mapper;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import org.springframework.stereotype.Component;

@Component
public class CartaoMapper {

    public Cartao toDomain(CartaoEntity entity) {
        Cartao cartao = new Cartao();
        cartao.setId(entity.getId());
        cartao.setClienteId(entity.getClienteId());
        cartao.setContaId(entity.getContaId());
        cartao.setNumero(entity.getNumero());
        cartao.setCvv(entity.getCvv());
        cartao.setDataVencimento(entity.getDataVencimento());
        cartao.setTipoCartao(entity.getTipoCartao());
        cartao.setStatus(entity.getStatus());
        cartao.setTipoEmissao(entity.getTipoEmissao());
        cartao.setMotivoStatus(entity.getMotivoStatus());
        return cartao;
    }

    public CartaoEntity fromDomain(Cartao cartao) {
        CartaoEntity entity = new CartaoEntity();
        entity.setId(cartao.getId());
        entity.setClienteId(cartao.getClienteId());
        entity.setContaId(cartao.getContaId());
        entity.setNumero(cartao.getNumero());
        entity.setCvv(cartao.getCvv());
        entity.setDataVencimento(cartao.getDataVencimento());
        entity.setTipoCartao(cartao.getTipoCartao());
        entity.setStatus(cartao.getStatus());
        entity.setTipoEmissao(cartao.getTipoEmissao());
        entity.setMotivoStatus(cartao.getMotivoStatus());
        return entity;
    }

}
