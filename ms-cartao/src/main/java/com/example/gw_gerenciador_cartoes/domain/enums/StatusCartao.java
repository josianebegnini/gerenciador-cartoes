package com.example.gw_gerenciador_cartoes.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusCartao {
    DESATIVADO,
    ATIVADO,
    BLOQUEADO,
    REJEITADO,
    CANCELADO;
    
    @JsonCreator
    public static StatusCartao from(String value) {
        try {
            return StatusCartao.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("StatusCartao inválido: " + value);
        }
    }

}