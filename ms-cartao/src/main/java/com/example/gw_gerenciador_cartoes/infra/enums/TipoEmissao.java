package com.example.gw_gerenciador_cartoes.infra.enums;

import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoEmissao {
    FISICO,
    VIRTUAL;

    @JsonCreator
    public static TipoEmissao from(String value) {
        for (TipoEmissao tipo : TipoEmissao.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException(MensagensErroConstantes.CARTAO_STATUS_IIPO_EMISSAO_INVALIDO + value);
    }

}