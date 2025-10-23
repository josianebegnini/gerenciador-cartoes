//package com.example.gw_gerenciador_cartoes.service;
//
//
//import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
//import com.example.gw_gerenciador_cartoes.domain.ports.EnderecoRepositoryPort;
//import com.example.gw_gerenciador_cartoes.domain.ports.EnderecoServicePort;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class EnderecoServiceImpl implements EnderecoServicePort {
//
//    private final EnderecoRepositoryPort enderecoRepository;
//
//
//    public EnderecoServiceImpl(EnderecoRepositoryPort enderecoRepository) {
//        this.enderecoRepository = enderecoRepository;
//
//    }
//
//
//    @Override
//    public Endereco criarEndereco(Endereco endereco) {
//        return enderecoRepository.save(endereco);
//    }
//
//    @Override
//    public List<Endereco> listarEnderecos() {
//        return enderecoRepository.findAll();
//    }
//
//    @Override
//    public void deletarEndereco(Long id) {
//        Optional<Endereco> endereco = enderecoRepository.findById(id);
//        if (!endereco.isPresent()) {
//            throw new RuntimeException("Endereco nao encontrado id " + id);
//        }
//    }
//}
