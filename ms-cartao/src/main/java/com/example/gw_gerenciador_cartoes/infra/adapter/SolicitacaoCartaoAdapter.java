package com.example.gw_gerenciador_cartoes.infra.adapter;

import com.example.gw_gerenciador_cartoes.application.mapper.SolicitacaoCartaoMapper;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.entity.SolicitacaoCartaoEntity;
import com.example.gw_gerenciador_cartoes.infra.repository.SolicitacaoCartaoRepositoryJpa;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SolicitacaoCartaoAdapter implements SolicitacaoCartaoRepositoryPort {

    private final SolicitacaoCartaoRepositoryJpa jpaRepository;
    private final SolicitacaoCartaoMapper mapper;

    public SolicitacaoCartaoAdapter(SolicitacaoCartaoRepositoryJpa jpaRepository, SolicitacaoCartaoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SolicitacaoCartao> salvar(SolicitacaoCartao solicitacaoCartao) {
        SolicitacaoCartaoEntity entity = mapper.toEntity(solicitacaoCartao);
        if (solicitacaoCartao.getId() != null) {
            entity.setId(solicitacaoCartao.getId());
        }
        SolicitacaoCartaoEntity savedEntity = jpaRepository.save(entity);
        return Optional.of(mapper.toDomain(savedEntity));
    }

    @Override
    public void alterar(SolicitacaoCartao solicitacaoCartao) {
        salvar(solicitacaoCartao);
    }

    @Override
    public Optional<SolicitacaoCartao> buscarPorId(Long solicitacaoId) {
        return jpaRepository.findById(solicitacaoId)
                .map(mapper::toDomain);
    }

}
