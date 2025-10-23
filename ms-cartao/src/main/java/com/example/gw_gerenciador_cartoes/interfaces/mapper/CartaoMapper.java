package com.example.gw_gerenciador_cartoes.interfaces.mapper;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import org.springframework.stereotype.Component;

@Component
public class CartaoMapper {

    public static Cartao toDomain(CartaoEntity entity) {
        Cartao cartao = new Cartao();
        cartao.setId(entity.getId());
        cartao.setClienteId(entity.getClienteId());
        cartao.setNumero(entity.getNumero());
        cartao.setCvv(entity.getCvv());
        cartao.setDataVencimento(entity.getDataVencimento());
        cartao.setCategoriaCartao(entity.getCategoria());
        cartao.setStatus(entity.getStatus());
        cartao.setTipoCartao(entity.getTipo());
        cartao.setMotivoSegundaVia(entity.getMotivoSegundaVia());
        return cartao;
    }

    public static CartaoEntity fromDomain(Cartao cartao) {
        CartaoEntity entity = new CartaoEntity();
        entity.setId(cartao.getId());
        entity.setClienteId(cartao.getClienteId());
        entity.setNumero(cartao.getNumero());
        entity.setCvv(cartao.getCvv());
        entity.setDataVencimento(cartao.getDataVencimento());
        entity.setCategoria(cartao.getCategoriaCartao());
        entity.setStatus(cartao.getStatus());
        entity.setTipo(cartao.getTipoCartao());
        entity.setMotivoSegundaVia(cartao.getMotivoSegundaVia());
        return entity;
    }

}
