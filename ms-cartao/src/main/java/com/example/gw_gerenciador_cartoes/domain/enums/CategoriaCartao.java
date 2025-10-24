package com.example.gw_gerenciador_cartoes.domain.enums;

public enum CategoriaCartao {
    DEBITO,
    CREDITO,
    MULTIPLO;

    public static CategoriaCartao fromString(String value) {
        for (CategoriaCartao c : values()) {
            if (c.name().equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("CategoriaCartao inválida: " + value);
    }

}
