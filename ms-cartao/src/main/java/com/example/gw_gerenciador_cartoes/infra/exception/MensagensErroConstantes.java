package com.example.gw_gerenciador_cartoes.infra.exception;

public final class MensagensErroConstantes {

    public static final String CARTAO_NAO_ENCONTRADO = "[CART001] Cartão não encontrado.";
    public static final String CARTAO_ATIVACAO_STATUS_INVALIDO = "[CART002] Só é possível ativar cartões com status DESATIVADO.";
    public static final String CARTAO_BLOQUEAR_STATUS_INVALIDO = "[CART003] Só pode ser bloqueado se estiver com status ATIVADO";

    public static final String SEGUNDA_VIA_STATUS_INVALIDO = "[SGVC001] Segunda via só pode ser solicitada para cartões ATIVADOS ou BLOQUEADOS.";

    public static final String SOLICITACAO_NAO_ENCONTRADA_REJEITAR = "[SOL001] Não foi possível encontrar a solicitação para atualizar o status para REJEITADO.";
    public static final String SOLICITACAO_NAO_ENCONTRADA_FINALIZAR = "[SOL002] Não foi possível encontrar a solicitação para finalizar como processada.";

    public static final String MOTIVO_CARTAO_DESATIVADO_APOS_GERACAO = "[MOT001] Cartão gerado e aguardando ativação.";
    public static final String MOTIVO_CARTAO_ATIVADO = "[MOT002] Cartão ativado.";
    public static final String MOTIVO_CARTAO_BLOQUEADO_SEGURANCA = " [MOT003] Cartão bloqueado por segurança. Motivos possíveis: perda, roubo ou suspeita de fraude.";
    public static final String MOTIVO_CARTAO_CANCELADO_SEGUNDA_VIA = " [MOT004] Cartão cancelado automaticamente após solicitação de segunda via.";
    public static final String MOTIVO_CARTAO_SEGUNDA_VIA_GERADA = "[MOT005] Segunda via de cartão gerada. Motivo: ";

    public static final String ERRO_PERSISTENCIA = "[ERR001] Erro ao persistir os dados no banco.";
    public static final String ERRO_PROCESSAMENTO_MENSAGEM = "[ERR002] Erro inesperado no processamento da mensagem.";
    public static final String ERRO_REGRA_NEGOCIO_SEM_ID = "[ERR003] Erro de regra de negócio sem ID ou solicitação inexistente.";
    public static final String ERRO_INESPERADO_LOG = "[ERR004] [Erro inesperado] ";
    public static final String ERRO_REGRA_NEGOCIO_LOG = "[ERR005] [RegraNegocioException sem ID ou solicitação inexistente] ";

}
