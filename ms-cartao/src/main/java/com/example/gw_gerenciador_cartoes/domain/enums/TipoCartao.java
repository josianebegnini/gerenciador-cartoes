package com.example.gw_gerenciador_cartoes.domain.enums;

import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoCartao {
    DEBITO,
    CREDITO,
    MULTIPLO;

    @JsonCreator
    public static TipoCartao from(String value) {
        try {
            return TipoCartao.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new RegraNegocioException("Valor inválido para TipoCartao: " + value);
        }
    }
}
