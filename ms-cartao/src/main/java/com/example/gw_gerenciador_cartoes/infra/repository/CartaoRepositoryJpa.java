package com.example.gw_gerenciador_cartoes.infra.repository;

import com.example.gw_gerenciador_cartoes.infra.entity.CartaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartaoRepositoryJpa extends JpaRepository<CartaoEntity, Long> {
    boolean existsByNumero(String numero);
    Optional<CartaoEntity> findByNumeroAndCvv(String numero, String cvv);
    Page<CartaoEntity> findByClienteId(Long idCliente, Pageable pageable);
}
