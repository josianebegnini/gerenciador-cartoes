package com.example.gw_gerenciador_cartoes.infra.repository;

import com.example.gw_gerenciador_cartoes.infra.entity.SolicitacaoCartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoCartaoRepositoryJpa extends JpaRepository<SolicitacaoCartaoEntity, Long> {
}
