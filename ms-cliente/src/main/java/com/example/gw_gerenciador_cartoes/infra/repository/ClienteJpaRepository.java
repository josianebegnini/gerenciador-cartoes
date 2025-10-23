package com.example.gw_gerenciador_cartoes.infra.repository;

import com.example.gw_gerenciador_cartoes.infra.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity,Long> {


    Page<ClienteEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<ClienteEntity> findByCpf(String cpf, Pageable pageable);

    Page<ClienteEntity> findByNomeContainingIgnoreCaseAndCpf(String nome, String cpf, Pageable pageable);

}
