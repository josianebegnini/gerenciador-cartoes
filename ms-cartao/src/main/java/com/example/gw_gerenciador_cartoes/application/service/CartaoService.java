package com.example.gw_gerenciador_cartoes.application.service;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;

    public CartaoService(CartaoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void gerarCartao(Long clienteId) {
        Cartao cartao = new Cartao();
        cartao.setNumero(gerarNumeroCartao());
        cartao.setCvv(String.format("%03d", new Random().nextInt(1000)));
        cartao.setClienteId(clienteId);
        cartao.setDataVencimento(LocalDate.now().plusYears(3));
        cartao.setStatus("DESATIVADO");
        cartao.setTipoConta("CONTA");
        cartao.setFormatoCartao("FíSICO");

        repository.salvar(cartao);

    }

    private String gerarNumeroCartao() {
        String base = "400000" + String.format("%09d", new Random().nextInt(1_000_000_000));
        int digito = calcularDigitoLuhn(base);
        return base + digito;
    }

    private int calcularDigitoLuhn(String numero) {
        int soma = 0;
        boolean alternar = true;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(numero.charAt(i));
            if (alternar) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            soma += n;
            alternar = !alternar;
        }
        return (10 - (soma % 10)) % 10;
    }
}
