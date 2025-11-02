package com.example.gw_gerenciador_cartoes.service.testutil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class CartaoTestConstants {

    public static final String NUMERO_PADRAO = "1234567890123456";
    public static final String CVV_PADRAO = "123";
    public static final String NOME_PADRAO = "Kamila dos Santos";
    public static final String EMAIL_PADRAO = "kamila@email.com";
    public static final String CPF_PADRAO = "12345678900";
    public static final BigDecimal LIMITE_PADRAO = new BigDecimal("1000.00");
    public static final LocalDateTime VENCIMENTO_PADRAO = LocalDateTime.of(2028, 10, 31, 0, 0);

    public static final String MOTIVO_STATUS_ATIVADO = "Cartão ativado com sucesso";
    public static final String MOTIVO_STATUS_MIGRADO = "Cartão migrado";
    public static final String MOTIVO_DADOS_INVALIDOS = "Dados inválidos";
    public static final String MOTIVO_CARTAO_DANIFICADO = "Cartão danificado";

    public static final String MENSAGEM_SOLICITACAO_RECEBIDA = "Solicitação recebida";
    public static final String MENSAGEM_SOLICITACAO_REJEITADA =
            "Falha ao processar mensagem ClienteContaCriadoDTO na fila 'cartao-criar-queue'";


    private CartaoTestConstants() {}

}
