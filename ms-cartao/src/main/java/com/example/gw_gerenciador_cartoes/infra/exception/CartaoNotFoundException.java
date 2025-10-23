package com.example.gw_gerenciador_cartoes.infra.exception;

public class CartaoNotFoundException extends RuntimeException {
    public CartaoNotFoundException(String mensagem) {
        super(mensagem);
    }
}

