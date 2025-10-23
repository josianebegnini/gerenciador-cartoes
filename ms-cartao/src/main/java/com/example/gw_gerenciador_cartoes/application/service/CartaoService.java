package com.example.gw_gerenciador_cartoes.application.service;

import com.example.gw_gerenciador_cartoes.domain.enums.CategoriaCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.interfaces.dto.SegundaViaCartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.interfaces.exception.CartaoOriginalNotFoundException;
import com.example.gw_gerenciador_cartoes.interfaces.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.interfaces.mapper.SegundaViaCartaoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class CartaoService implements CartaoServicePort {

    private final CartaoRepositoryPort repository;
    private final SegundaViaCartaoMapper mapper;

    public CartaoService(CartaoRepositoryPort repository, SegundaViaCartaoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void gerarCartao(Long clienteId) {
        Cartao cartao = new Cartao();
        cartao.setNumero(gerarNumeroCartao());
        cartao.setCvv(gerarCvv());
        cartao.setClienteId(clienteId);
        cartao.setDataVencimento(LocalDate.now().plusYears(3));
        cartao.setStatus(StatusCartao.ATIVADO); //TODO colocar para DESATIVADO
        cartao.setCategoriaCartao(CategoriaCartao.CONTA);
        cartao.setTipoCartao(TipoCartao.FISICO);

        repository.salvar(cartao);
    }

    @Override
    public SegundaViaCartaoResponseDTO solicitarSegundaVia(Long idCartaoOriginal, String motivo) {
        Cartao original = repository.buscarPorId(idCartaoOriginal)
                .orElseThrow(() -> new CartaoOriginalNotFoundException("Cartão original não encontrado."));

        if (original.getStatus() != StatusCartao.ATIVADO && original.getStatus() != StatusCartao.BLOQUEADO) {
            throw new RegraNegocioException("Segunda via só pode ser solicitada para cartões ATIVADOS ou BLOQUEADOS.");
        }

        original.setStatus(StatusCartao.CANCELADO);
        repository.atualizar(original)
                .orElseThrow(() -> new RuntimeException("Erro ao atualizar cartão original"));

        Cartao segundaVia = new Cartao();
        segundaVia.setNumero(gerarNumeroCartaoUnico());
        segundaVia.setCvv(gerarCvv());
        segundaVia.setClienteId(original.getClienteId());
        segundaVia.setDataVencimento(LocalDate.now().plusYears(3));
        segundaVia.setStatus(StatusCartao.DESATIVADO);
        segundaVia.setCategoriaCartao(original.getCategoriaCartao());
        segundaVia.setTipoCartao(original.getTipoCartao());
        segundaVia.setMotivoSegundaVia(motivo);

        Cartao cartaoSalvo = repository.salvar(segundaVia)
                .orElseThrow(() -> new RuntimeException("Erro ao salvar segunda via"));

        return mapper.toResponseDTO(cartaoSalvo);
    }

    private String gerarNumeroCartaoUnico() {
        String numero;
        do {
            numero = gerarNumeroCartao();
        } while (repository.existePorNumero(numero));
        return numero;
    }

    private String gerarNumeroCartao() {
        String base = "400000" + String.format("%09d", new Random().nextInt(1_000_000_000));
        int digito = calcularDigitoLuhn(base);
        return base + digito;
    }

    private String gerarCvv() {
        return String.format("%03d", new Random().nextInt(1000));
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
