package com.example.gw_gerenciador_cartoes.infra.exception;

public final class MensagensErroConstantes {

    public static final String CARTAO_NAO_ENCONTRADO = "[CART001] Cartão não encontrado.";
    public static final String CARTAO_ATIVACAO_STATUS_INVALIDO = "[CART002] Só é possível ativar cartões com status DESATIVADO.";
    public static final String CARTAO_BLOQUEAR_STATUS_INVALIDO = "[CART003] Só pode ser bloqueado se estiver com status ATIVADO";
    public static final String CARTAO_ERRO_AO_ATIVAR = "[CART004] Erro ao ativar o cartão.";
    public static final String CARTAO_ERRO_AO_BLOQUEAR = "[CART005] Erro ao bloquear o cartão.";
    public static final String CARTAO_ERRO_AO_ATUALIZAR_CARTAO_ORIGINAL = "[CART006] Erro ao atualizar o cartão original.";
    public static final String CARTAO_ERRO_AO_SALVAR_SEGUNDA_VIA = "[CART07] Erro ao salvar a segunda via do cartão.";

    public static final String SEGUNDA_VIA_STATUS_INVALIDO = "[SGVC001] Segunda via só pode ser solicitada para cartões ATIVADOS ou BLOQUEADOS.";
}
