package com.example.gw_gerenciador_cartoes.infra.repository;

import com.example.gw_gerenciador_cartoes.infra.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaJpaRepository extends JpaRepository<ContaEntity,Long> {
}
