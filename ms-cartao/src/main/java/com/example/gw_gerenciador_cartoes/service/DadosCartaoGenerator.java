package com.example.gw_gerenciador_cartoes.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class DadosCartaoGenerator {

    private final SecureRandom random = new SecureRandom();

    public String gerarNumeroCartao() {

        final String bin = "411111";
        String parteConta = String.format("%09d", random.nextInt(1_000_000_000));
        String base = bin + parteConta;

        int digitoVerificador = calcularDigitoLuhn(base);

        return base + digitoVerificador;
    }

    public String gerarCvv() {
        return String.format("%03d", random.nextInt(1000));
    }

    public int calcularDigitoLuhn(String numero) {
        int soma = 0;
        boolean dobrar = true;

        for (int i = numero.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(numero.charAt(i));

            if (dobrar) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            soma += n;
            dobrar = !dobrar;
        }
        return (10 - (soma % 10)) % 10;
    }
}
