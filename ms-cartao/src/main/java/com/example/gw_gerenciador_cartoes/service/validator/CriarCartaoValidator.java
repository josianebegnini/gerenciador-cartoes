package com.example.gw_gerenciador_cartoes.service.validator;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.CriarCartaoMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;
import com.example.gw_gerenciador_cartoes.infra.exception.CampoObrigatorioException;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagemErro;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class CriarCartaoValidator {

    public void validar(CriarCartaoMessageDTO dto, Long solicitacaoId) {
        validarCamposObrigatorios(dto, solicitacaoId);
    }

    private void validarCamposObrigatorios(CriarCartaoMessageDTO dto, Long solicitacaoId) {
        List<MensagemErro> mensagensErros = new ArrayList<>();

        if (Objects.isNull(dto.getClienteId())) {
            mensagensErros.add(new MensagemErro("Campo clienteId é obrigatório."));
        }
        if (Objects.isNull(dto.getContaId())) {
            mensagensErros.add(new MensagemErro("Campo contaId é obrigatório."));
        }
        if (isBlank(dto.getNome())) {
            mensagensErros.add(new MensagemErro("Campo nome é obrigatório."));
        }

        if (isBlank(dto.getEmail())) {
            mensagensErros.add(new MensagemErro("Campo e-mail é obrigatório."));
        } else if (!isEmailValido(dto.getEmail())) {
            mensagensErros.add(new MensagemErro("E-mail inválido: " + dto.getEmail()));
        }

        if (isBlank(dto.getTipoCartao())) {
            mensagensErros.add(new MensagemErro("Campo tipoCartao é obrigatório."));
        } else if (!isEnumValido(TipoCartao.class, dto.getTipoCartao())) {
            mensagensErros.add(new MensagemErro("Valor inválido para tipoCartao: " + dto.getTipoCartao()));
        }

        if (isBlank(dto.getTipoEmissao())) {
            mensagensErros.add(new MensagemErro("Campo tipoEmissao é obrigatório."));
        } else if (!isEnumValido(TipoEmissaoCartao.class, dto.getTipoEmissao())) {
            mensagensErros.add(new MensagemErro("Valor inválido para tipoEmissao: " + dto.getTipoEmissao()));
        }

        if (!mensagensErros.isEmpty()) {
            throw new CampoObrigatorioException(solicitacaoId, mensagensErros);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isEmailValido(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private <T extends Enum<T>> boolean isEnumValido(Class<T> enumClass, String value) {
        if (value == null) return false;
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}
