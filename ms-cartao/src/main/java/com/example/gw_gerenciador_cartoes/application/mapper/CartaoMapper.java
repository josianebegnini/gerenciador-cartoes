package com.example.gw_gerenciador_cartoes.application.mapper;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoInfoResponseDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartaoMapper {
    Cartao toDomain(CartaoEntity entity);
    CartaoEntity toEntity(Cartao domain);
    CartaoInfoResponseDTO toSegundaViaResponseDTO(Cartao cartao);
    CartaoResponseDTO toCartaoResponseDTO(Cartao cartao);
    CartaoInfoResponseDTO toCartaoInfoResponseDTO(Cartao cartao);
}
