package com.example.gw_gerenciador_cartoes.service.validator;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CartaoStatusValidator {

    public void validarAlteracaoStatus(Cartao cartao, StatusCartao novoStatus) {
        switch (novoStatus) {
            case ATIVADO:
                if (cartao.getStatus() != StatusCartao.DESATIVADO) {
                    throw new RegraNegocioException(MensagensErroConstantes.CARTAO_ATIVACAO_STATUS_INVALIDO);
                }
                cartao.atualizarStatus(StatusCartao.ATIVADO, MensagensErroConstantes.MOTIVO_CARTAO_ATIVADO);
                cartao.setDataCriacao(LocalDateTime.now());
                break;

            case BLOQUEADO:
                if (cartao.getStatus() != StatusCartao.ATIVADO) {
                    throw new RegraNegocioException(MensagensErroConstantes.CARTAO_BLOQUEAR_STATUS_INVALIDO);
                }
                cartao.atualizarStatus(StatusCartao.BLOQUEADO, MensagensErroConstantes.MOTIVO_CARTAO_BLOQUEADO_SEGURANCA);
                break;

            default:
                throw new RegraNegocioException(MensagensErroConstantes.CARTAO_STATUS_NÃO_SUPORTADO);
        }
    }

}
