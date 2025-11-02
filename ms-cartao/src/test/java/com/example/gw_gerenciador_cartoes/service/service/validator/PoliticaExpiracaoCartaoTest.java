package com.example.gw_gerenciador_cartoes.service.service.validator;

import com.example.gw_gerenciador_cartoes.service.validator.PoliticaExpiracaoCartao;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PoliticaExpiracaoCartaoTest {

    private static Clock clockFixado(LocalDateTime dataHora) {
        return Clock.fixed(dataHora.atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    }

    @Test
    void deveCalcularVencimentoCartaoNovoEmTresAnos() {
        LocalDateTime agora = LocalDateTime.of(2025, 1, 15, 10, 0, 0);
        Clock clock = clockFixado(agora);
        var politica = new PoliticaExpiracaoCartao(clock);

        LocalDateTime esperado = agora.plusYears(3);
        LocalDateTime resultado = politica.calcularParaCartaoNovo();

        assertEquals(esperado, resultado);
    }

    @Test
    void deveManterVencimentoOriginalQuandoFaltaMaisDeSeisMeses() {
        LocalDateTime agora = LocalDateTime.of(2025, 3, 1, 9, 30, 0);
        Clock clock = clockFixado(agora);
        var politica = new PoliticaExpiracaoCartao(clock);

        LocalDateTime vencimentoOriginal = agora.plusMonths(6).plusDays(1);

        LocalDateTime resultado = politica.calcularParaSegundaVia(vencimentoOriginal);

        assertEquals(vencimentoOriginal, resultado);
    }

    @Test
    void deveRenovarQuandoFaltamExatosSeisMeses() {
        LocalDateTime agora = LocalDateTime.of(2025, 4, 10, 12, 0, 0);
        Clock clock = clockFixado(agora);
        var politica = new PoliticaExpiracaoCartao(clock);

        LocalDateTime vencimentoOriginal = agora.plusMonths(6);

        LocalDateTime esperado = agora.plusYears(3);
        LocalDateTime resultado = politica.calcularParaSegundaVia(vencimentoOriginal);

        assertEquals(esperado, resultado);
    }

    @Test
    void deveRenovarQuandoFaltaMenosDeSeisMeses() {
        LocalDateTime agora = LocalDateTime.of(2025, 7, 20, 8, 0, 0);
        Clock clock = clockFixado(agora);
        var politica = new PoliticaExpiracaoCartao(clock);

        LocalDateTime vencimentoOriginal = agora.plusMonths(5).plusDays(29);

        LocalDateTime esperado = agora.plusYears(3);
        LocalDateTime resultado = politica.calcularParaSegundaVia(vencimentoOriginal);

        assertEquals(esperado, resultado);
    }

    @Test
    void deveCalcularParaCartaoNovoConsiderandoAnoBissexto() {
        LocalDateTime agoraBissexto = LocalDateTime.of(2028, 2, 29, 14, 0, 0);
        Clock clock = clockFixado(agoraBissexto);
        var politica = new PoliticaExpiracaoCartao(clock);

        LocalDateTime resultado = politica.calcularParaCartaoNovo();

        assertEquals(LocalDateTime.of(2031, 2, 28, 14, 0, 0), resultado);
    }
}
