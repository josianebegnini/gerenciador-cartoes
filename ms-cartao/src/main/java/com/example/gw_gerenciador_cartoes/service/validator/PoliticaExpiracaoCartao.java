package com.example.gw_gerenciador_cartoes.service.validator;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Period;

@Component
public class PoliticaExpiracaoCartao {

    private final Clock clock;

    private static final Period JANELA_RENOVACAO = Period.ofMonths(6);

    public PoliticaExpiracaoCartao(Clock clock) {
        this.clock = clock;
    }

    public LocalDateTime calcularParaCartaoNovo() {
        return LocalDateTime.now(clock).plusYears(3);
    }

    public LocalDateTime calcularParaSegundaVia(LocalDateTime vencimentoOriginal) {
        LocalDateTime agora = LocalDateTime.now(clock);
        LocalDateTime limiteRenovacao = agora.plus(JANELA_RENOVACAO);

        if (vencimentoOriginal.isAfter(limiteRenovacao)) {
            return vencimentoOriginal;
        }

        return agora.plusYears(3);
    }
}
