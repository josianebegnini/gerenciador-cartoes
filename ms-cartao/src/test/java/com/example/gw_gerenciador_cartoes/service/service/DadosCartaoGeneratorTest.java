package com.example.gw_gerenciador_cartoes.service.service;

import com.example.gw_gerenciador_cartoes.service.DadosCartaoGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

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
    void deveGerarNumeroCartaoComBinCorreto() {
        String numero = dadosCartaoGenerator.gerarNumeroCartao();

        assertTrue(numero.startsWith("411111"), "Número deve começar com o BIN 411111");
        assertEquals(16, numero.length());
    }

    @RepeatedTest(10)
    void deveGerarNumerosCartaoDiferentes() {
        String numero1 = dadosCartaoGenerator.gerarNumeroCartao();
        String numero2 = dadosCartaoGenerator.gerarNumeroCartao();

        assertNotEquals(numero1, numero2, "Números gerados devem ser diferentes");
        assertTrue(numero1.startsWith("411111"));
        assertTrue(numero2.startsWith("411111"));
        assertEquals(16, numero1.length());
        assertEquals(16, numero2.length());
    }

    @Test
    void deveGerarNumerosCartaoUnicosEmLote() {
        Set<String> numerosGerados = new HashSet<>();
        int quantidade = 100;

        for (int i = 0; i < quantidade; i++) {
            String numero = dadosCartaoGenerator.gerarNumeroCartao();
            numerosGerados.add(numero);
            assertTrue(numero.startsWith("411111"), 
                    "Número deve começar com BIN 411111: " + numero);
            assertEquals(16, numero.length(), 
                    "Número deve ter 16 dígitos: " + numero);
        }

        double taxaUnicidade = (double) numerosGerados.size() / quantidade;
        assertTrue(taxaUnicidade >= 0.95, 
                String.format("Taxa de unicidade muito baixa: %.2f%%. Esperado pelo menos 95%%", 
                        taxaUnicidade * 100));
    }

    @Test
    void deveGerarCvvComTresDigitos() {
        String cvv = dadosCartaoGenerator.gerarCvv();

        assertNotNull(cvv);
        assertEquals(3, cvv.length());
        assertTrue(cvv.matches("\\d{3}"));
    }

    @RepeatedTest(10)
    void deveGerarCvvValido() {
        String cvv = dadosCartaoGenerator.gerarCvv();

        assertNotNull(cvv);
        assertEquals(3, cvv.length());
        assertTrue(cvv.matches("\\d{3}"));
        
        int valorCvv = Integer.parseInt(cvv);
        assertTrue(valorCvv >= 0 && valorCvv <= 999, 
                "CVV deve estar no range válido de 0 a 999");
    }

    @Test
    void deveGerarCvvsDiferentes() {
        Set<String> cvvsGerados = new HashSet<>();
        int quantidade = 100;

        for (int i = 0; i < quantidade; i++) {
            String cvv = dadosCartaoGenerator.gerarCvv();
            cvvsGerados.add(cvv);
            assertEquals(3, cvv.length());
            assertTrue(cvv.matches("\\d{3}"));
        }

        assertTrue(cvvsGerados.size() > 1, 
                "Deveria gerar CVVs diferentes (com alta probabilidade)");
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

    @Test
    void deveCalcularDigitoLuhnParaNumerosConhecidos() {
        String[] bases = {
            "411111111111111",
            "555555555555444",
            "400000000000000",
            "510510510510510",
            "601111111111111"
        };

        int[] digitosEsperados = {1, 4, 2, 0, 7};

        for (int i = 0; i < bases.length; i++) {
            int digitoCalculado = dadosCartaoGenerator.calcularDigitoLuhn(bases[i]);
            assertEquals(digitosEsperados[i], digitoCalculado, 
                    "Dígito Luhn incorreto para base: " + bases[i]);

            String numeroCompleto = bases[i] + digitoCalculado;
            int soma = validarLuhn(numeroCompleto);
            assertEquals(0, soma % 10, 
                    "Número completo deve ser válido pelo algoritmo Luhn: " + numeroCompleto);
        }
    }

    @Test
    void deveCalcularDigitoLuhnQuandoSomaEhMultiploDe10() {
        String base = "400000000000000";
        int digito = dadosCartaoGenerator.calcularDigitoLuhn(base);
        
        assertTrue(digito >= 0 && digito <= 9, 
                "Dígito verificador deve estar entre 0 e 9");
        
        String numeroCompleto = base + digito;
        int soma = validarLuhn(numeroCompleto);
        assertEquals(0, soma % 10, 
                "Quando soma é múltiplo de 10, dígito deve fazer soma final ser múltiplo de 10");
    }

    @Test
    void deveCalcularDigitoLuhnParaBaseDoGerador() {
        String bin = "411111";
        String parteConta = "123456789";
        String base = bin + parteConta;
        
        int digito = dadosCartaoGenerator.calcularDigitoLuhn(base);
        
        assertTrue(digito >= 0 && digito <= 9);
        
        String numeroCompleto = base + digito;
        int soma = validarLuhn(numeroCompleto);
        assertEquals(0, soma % 10);
    }

    @RepeatedTest(20)
    void deveGerarNumeroCartaoComLuhnValidoSempre() {
        String numero = dadosCartaoGenerator.gerarNumeroCartao();
        
        int soma = validarLuhn(numero);
        assertEquals(0, soma % 10, 
                "Número gerado deve sempre ter dígito verificador Luhn válido: " + numero);
        
        assertTrue(numero.startsWith("411111"));
        assertEquals(16, numero.length());
    }

    private int validarLuhn(String numero) {
        int soma = 0;
        boolean dobrar = false;

        for (int i = numero.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(numero.charAt(i));
            if (dobrar) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            soma += n;
            dobrar = !dobrar;
        }
        return soma;
    }

}