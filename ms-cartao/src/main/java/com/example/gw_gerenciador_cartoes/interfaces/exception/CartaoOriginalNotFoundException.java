package com.example.gw_gerenciador_cartoes.interfaces.exception;

public class CartaoOriginalNotFoundException extends RuntimeException {
    public CartaoOriginalNotFoundException(String mensagem) {
        super(mensagem);
    }
}

