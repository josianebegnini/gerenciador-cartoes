package com.example.gw_gerenciador_cartoes.domain.enums;

public enum TipoEmissaoCartao {
    FISICO,
    VIRTUAL;

    public static TipoEmissaoCartao fromString(String value) {
        for (TipoEmissaoCartao tipo : TipoEmissaoCartao.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoEmissaoCartao inválido: " + value);
    }
}