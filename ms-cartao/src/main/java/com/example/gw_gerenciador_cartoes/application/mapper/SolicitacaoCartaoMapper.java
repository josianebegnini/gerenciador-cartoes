package com.example.gw_gerenciador_cartoes.application.mapper;

import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.infra.entity.SolicitacaoCartaoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitacaoCartaoMapper {
    SolicitacaoCartao toDomain(SolicitacaoCartaoEntity entity);
    SolicitacaoCartaoEntity toEntity(SolicitacaoCartao domain);
}
