package com.example.gw_gerenciador_cartoes.application.mapper;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartaoMapper {
    Cartao toDomain(CartaoEntity entity);
    CartaoEntity toEntity(Cartao domain);
    CartaoResponseDTO toCartaoResponseDTO(Cartao cartao);
    SegundaViaCartaoResponseDTO toSegundaViaResponseDTO(Cartao cartao);
}
