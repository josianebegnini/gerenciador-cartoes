package com.example.gw_gerenciador_cartoes.infra.exception;

public class CartaoOriginalNotFoundException extends RuntimeException {
    public CartaoOriginalNotFoundException(String mensagem) {
        super(mensagem);
    }
}

