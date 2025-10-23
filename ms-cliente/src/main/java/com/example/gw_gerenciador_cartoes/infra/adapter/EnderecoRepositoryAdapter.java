//package com.example.gw_gerenciador_cartoes.infra.adapter;
//
//
//import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
//import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
//import com.example.gw_gerenciador_cartoes.domain.ports.ClienteRepositoryPort;
//import com.example.gw_gerenciador_cartoes.domain.ports.EnderecoRepositoryPort;
//import com.example.gw_gerenciador_cartoes.infra.entity.ClienteEntity;
//import com.example.gw_gerenciador_cartoes.infra.entity.EnderecoEntity;
//import com.example.gw_gerenciador_cartoes.infra.repository.ClienteJpaRepository;
//import com.example.gw_gerenciador_cartoes.infra.repository.EnderecoJpaRepository;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Component
//public class EnderecoRepositoryAdapter implements EnderecoRepositoryPort {
//
//    private final EnderecoJpaRepository enderecoJpaRepository;
//
//    public EnderecoRepositoryAdapter(EnderecoJpaRepository enderecoJpaRepository) {
//        this.enderecoJpaRepository = enderecoJpaRepository;
//    }
//
//    @Override
//    public Endereco save(Endereco endereco) {
//        EnderecoEntity entity = toEntity(endereco);
//
//        return toDomain(enderecoJpaRepository.save(entity));
//    }
//
//    @Override
//    public List<Endereco> findAll() {
//        return enderecoJpaRepository.findAll()
//                .stream()
//                .map(this::toDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<Endereco> findById(Long id) {
//        return enderecoJpaRepository.findById(id).map(this::toDomain);
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        enderecoJpaRepository.deleteById(id);
//    }
//
//    private Endereco toDomain(EnderecoEntity entity) {
//        return new Endereco(
//                entity.getCidade(),
//                entity.getBairro(),
//                entity.getRua(),
//                entity.getCep(),
//                entity.getComplemento(),
//                entity.getNumero()
//        );
//    }
//
//
//    private EnderecoEntity toEntity(Endereco endereco) {
//        return new EnderecoEntity(
//                endereco.getCidade(),
//                endereco.getBairro(),
//                endereco.getRua(),
//                endereco.getCep(),
//                endereco.getComplemento(),
//                endereco.getNumero()
//        );
//    }
//}
