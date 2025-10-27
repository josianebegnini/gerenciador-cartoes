package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.CriarCartaoMessageDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CartaoRespostaService {

//    private final CartaoRejeitadoRepositoryPort repository;
//
//    public CartaoRespostaService(CartaoRejeitadoRepositoryPort repository) {
//        this.repository = repository;
//    }
//
//    public void salvarRejeicao(CriarCartaoMessageDTO dto, String motivo) {
//        CartaoRejeitado rejeitado = new CartaoRejeitado();
//        rejeitado.setClienteId(dto.getClienteId());
//        rejeitado.setContaId(dto.getContaId());
//        rejeitado.setNome(dto.getNome());
//        rejeitado.setCpf(dto.getCpf());
//        rejeitado.setEmail(dto.getEmail());
//        rejeitado.setTipoCartao(dto.getTipoCartao());
//        rejeitado.setTipoEmissao(dto.getTipoEmissao());
//        rejeitado.setMotivoRejeicao(motivo);
//        rejeitado.setDataRejeicao(LocalDateTime.now());
//
//        repository.salvar(rejeitado);
//    }

}