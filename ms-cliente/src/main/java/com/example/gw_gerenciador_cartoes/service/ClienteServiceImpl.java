package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.ports.ClienteRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.ClienteServicePort;
import com.example.gw_gerenciador_cartoes.domain.ports.CriarCartaoProducerPort;
import com.example.gw_gerenciador_cartoes.domain.ports.EmailNormalQueueProducerPort;
import com.example.gw_gerenciador_cartoes.infra.adapter.util.UtilsMapper;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteServicePort {

    private final ClienteRepositoryPort clienteRepository;
    private final CriarCartaoProducerPort criarCartaoProducer;
    private final EmailNormalQueueProducerPort emailNormalQueueProducer;



    public ClienteServiceImpl(ClienteRepositoryPort clienteRepository, CriarCartaoProducerPort criarCartaoProducer, EmailNormalQueueProducerPort emailNormalQueueProducer) {
        this.clienteRepository = clienteRepository;
        this.criarCartaoProducer = criarCartaoProducer;
        this.emailNormalQueueProducer = emailNormalQueueProducer;
    }

    @Override
    public Cliente criarCliente(Cliente cliente) {
        Cliente clienteSalvo = clienteRepository.save(cliente);

        criarCartaoProducer.sendCriarCartao(UtilsMapper.fromClienteToClienteContaDto(clienteSalvo, TipoCartao.CONTA, TipoEmissao.FISICO));

        emailNormalQueueProducer.sendEmailNormalQueue(UtilsMapper.fromClienteToEmailMessageDTOContaCriada(clienteSalvo));

        return clienteSalvo;
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public void deletarCliente(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (!cliente.isPresent()) {
            throw new RuntimeException("Cliente bnao encontrado id " + id);
        }
        clienteRepository.deleteById(cliente.get().getId());

    }

    @Override
    public Page<Cliente> listarClientes(int page, int size, String sortBy, String direction, String nome, String cpf) {

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return clienteRepository.findAll(pageable, nome, cpf);
    }

}
