package com.example.gw_gerenciador_cartoes.infra.converter;

import com.example.gw_gerenciador_cartoes.infra.exception.MensagemErro;

import java.util.List;
import java.util.stream.Collectors;

public class StringConverter {

    public static String juntarMensagens(List<MensagemErro> mensagens) {
        return mensagens.stream()
                .map(m -> m.getMensagem())
                .collect(Collectors.joining(" | "));
    }

}
