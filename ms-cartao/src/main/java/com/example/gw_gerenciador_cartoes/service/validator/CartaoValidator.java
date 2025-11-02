package com.example.gw_gerenciador_cartoes.service.validator;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.exception.CampoObrigatorioException;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagemErro;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CartaoValidator {

    private final CartaoRepositoryPort repository;

    public CartaoValidator(CartaoRepositoryPort repository) {
        this.repository = repository;
    }

    public void validar(ClienteContaCriadoDTO dto, Long solicitacaoId) {
        validarCamposObrigatorios(dto, solicitacaoId);
    }

    private void validarCamposObrigatorios(ClienteContaCriadoDTO dto, Long solicitacaoId) {
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

        if (dto.getTipoCartao() == null) {
            mensagensErros.add(new MensagemErro("Campo tipoCartao é obrigatório."));
        }

        if (dto.getTipoEmissao() == null) {
            mensagensErros.add(new MensagemErro("Campo tipoEmissao é obrigatório."));
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

    public void validarCartaoNaoExiste(String numero, String cvv) {
        boolean cartaoJaExiste = repository.buscarCartaoPorNumeroECvv(numero, cvv).isPresent();

        if (cartaoJaExiste) {
            throw new RegraNegocioException(MensagensErroConstantes.CARTAO_JA_EXISTE);
        }
    }

}
