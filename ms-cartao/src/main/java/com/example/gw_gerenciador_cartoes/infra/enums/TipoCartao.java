package com.example.gw_gerenciador_cartoes.infra.enums;

import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoCartao {
    DEBITO,
    CREDITO,
    MULTIPLO,
    CONTA;

    @JsonCreator
    public static TipoCartao from(String value) {
        for (TipoCartao tipo : TipoCartao.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException(MensagensErroConstantes.CARTAO_STATUS_IIPO_CARTAO_INVALIDO + value);
    }

}
