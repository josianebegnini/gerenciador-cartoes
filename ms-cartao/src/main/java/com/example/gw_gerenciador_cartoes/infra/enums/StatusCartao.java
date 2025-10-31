package com.example.gw_gerenciador_cartoes.infra.enums;

import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusCartao {
    DESATIVADO,
    ATIVADO,
    BLOQUEADO,
    REJEITADO,
    CANCELADO;

    @JsonCreator
    public static StatusCartao from(String value) {
        for (StatusCartao status : StatusCartao.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(MensagensErroConstantes.CARTAO_STATUS_CARTAO_INVALIDO + value);
    }
}
