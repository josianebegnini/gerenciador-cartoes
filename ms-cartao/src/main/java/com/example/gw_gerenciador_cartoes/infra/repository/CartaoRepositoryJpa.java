package com.example.gw_gerenciador_cartoes.infra.repository;

import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepositoryJpa extends JpaRepository<CartaoEntity, Long> {
    boolean existsByNumero(String numero);
}
