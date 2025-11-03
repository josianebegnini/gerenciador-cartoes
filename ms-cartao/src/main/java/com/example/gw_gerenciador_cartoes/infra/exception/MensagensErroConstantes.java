package com.example.gw_gerenciador_cartoes.infra.exception;

public final class MensagensErroConstantes {

    public static final String CARTAO_NAO_ENCONTRADO = "[CART001] Cartão não encontrado.";
    public static final String CARTAO_JA_EXISTE = "[CART002]Cartão já Existe";
    public static final String CARTAO_FALHA_AO_CRIAR = "[CART003] Falha ao salvar cartão";
    public static final String CARTAO_ATIVACAO_STATUS_INVALIDO = "[CART004] Só é possível ativar cartões com status DESATIVADO.";
    public static final String CARTAO_BLOQUEAR_STATUS_INVALIDO = "[CART005] Só pode ser bloqueado se estiver com status ATIVADO";
    public static final String CARTAO_STATUS_NÃO_SUPORTADO = "[CART006] Status de alteração não suportado.";
    public static final String CARTAO_STATUS_IIPO_EMISSAO_INVALIDO = "[CART007] TipoEmissao inválido: ";
    public static final String CARTAO_STATUS_IIPO_CARTAO_INVALIDO = "[CART008]TipoCartao inválido: ";
    public static final String CARTAO_STATUS_CARTAO_INVALIDO = "[CART009] StatusCartao inválido: ";

    public static final String SEGUNDA_VIA_STATUS_INVALIDO = "[SGVC001] Segunda via só pode ser solicitada para cartões ATIVADOS ou BLOQUEADOS.";
    public static final String CARTAO_FALHA_AO_CRIAR_SEGUNDA_VIA = "[SGVC001] Falha ao salvar segunda via do cartão";

    public static final String SOLICITACAO_NAO_ENCONTRADA= "[SOL001] Não foi possível encontrar a solicitação.";

    public static final String MOTIVO_CARTAO_DESATIVADO_APOS_GERACAO = "[MOT001] Cartão gerado e aguardando ativação.";
    public static final String MOTIVO_CARTAO_ATIVADO = "[MOT002] Cartão ativado.";
    public static final String MOTIVO_CARTAO_BLOQUEADO_SEGURANCA = " [MOT003] Cartão bloqueado por segurança. Motivos possíveis: perda, roubo ou suspeita de fraude.";
    public static final String MOTIVO_CARTAO_CANCELADO_SEGUNDA_VIA = " [MOT004] Cartão cancelado automaticamente após solicitação de segunda via.";
    public static final String MOTIVO_CARTAO_CANCELADO = " [MOT005] Cartão cancelado com sucesso." ;
    public static final String CARTAO_JA_CANCELADO = " [MOT006] Cartão cancelado com sucesso." ;
    public static final String MOTIVO_CARTAO_SEGUNDA_VIA_GERADA = "[MOT007] Segunda via de cartão gerada. Motivo: ";

    public static final String ERRO_PERSISTENCIA = "[ERR001] Erro ao persistir os dados no banco.";
    public static final String ERRO_PROCESSAMENTO_MENSAGEM = "[ERR002] Erro inesperado no processamento da mensagem.";
    public static final String ERRO_REGRA_NEGOCIO_SEM_ID = "[ERR003] Erro de regra de negócio sem ID ou solicitação inexistente.";
    public static final String ERRO_INESPERADO_LOG = "[ERR004] [Erro inesperado] ";
    public static final String ERRO_REGRA_NEGOCIO_LOG = "[ERR005] [RegraNegocioException sem ID ou solicitação inexistente] ";

}
