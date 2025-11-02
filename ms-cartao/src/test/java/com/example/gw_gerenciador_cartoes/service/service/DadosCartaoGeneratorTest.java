package com.example.gw_gerenciador_cartoes.service.service;

import com.example.gw_gerenciador_cartoes.service.DadosCartaoGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DadosCartaoGeneratorTest {

    private DadosCartaoGenerator dadosCartaoGenerator;

    @BeforeEach
    void setUp() {
        dadosCartaoGenerator = new DadosCartaoGenerator();
    }

    @Test
    void deveGerarNumeroCartaoValidoComLuhn() {
        String numero = dadosCartaoGenerator.gerarNumeroCartao();

        assertNotNull(numero);
        assertEquals(16, numero.length());

        int digitoCalculado = dadosCartaoGenerator.calcularDigitoLuhn(numero.substring(0, 15));
        int digitoFinal = Character.getNumericValue(numero.charAt(15));

        assertEquals(digitoCalculado, digitoFinal);
    }

    @Test
    void deveGerarCvvComTresDigitos() {
        String cvv = dadosCartaoGenerator.gerarCvv();

        assertNotNull(cvv);
        assertEquals(3, cvv.length());
        assertTrue(cvv.matches("\\d{3}"));
    }

    @Test
    void deveCalcularDigitoLuhnCorretamente() {
        String base = "400000123456789";
        int digito = dadosCartaoGenerator.calcularDigitoLuhn(base);

        String numeroCompleto = base + digito;

        int soma = 0;
        boolean dobrar = true;
        for (int i = numeroCompleto.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(numeroCompleto.charAt(i));
            if (dobrar) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            soma += n;
            dobrar = !dobrar;
        }

        assertEquals(0, soma % 10);
    }
}