package com.example.gw_gerenciador_cartoes.application.service;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.adapter.CartaoRepositoryAdapter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryAdapter repository;

    public CartaoService(CartaoRepositoryAdapter repository) {
        this.repository = repository;
    }

    @Override
    public void gerarCartao(String clienteId, String contaId) {
        Cartao cartao = new Cartao();
        cartao.setNumero(gerarNumeroCartao());
        cartao.setCvv(String.format("%03d", new Random().nextInt(1000)));
        cartao.setClienteId(clienteId);
        cartao.setContaId(contaId);
        cartao.setDataVencimento(LocalDate.now().plusYears(3));
        repository.salvar(cartao);
    }

    private String gerarNumeroCartao() {
        String base = "400000" + new Random().nextInt(99999999);
        return base + calcularDigitoLuhn(base);
    }

    private int calcularDigitoLuhn(String numero) {
        int soma = 0;
        boolean alternar = true;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numero.substring(i, i + 1));
            if (alternar) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            soma += n;
            alternar = !alternar;
        }
        return (10 - (soma % 10)) % 10;
    }}
