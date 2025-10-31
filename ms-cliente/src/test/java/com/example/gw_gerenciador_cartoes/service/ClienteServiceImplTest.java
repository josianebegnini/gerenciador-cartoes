package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.model.Conta;
import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
import com.example.gw_gerenciador_cartoes.domain.ports.CriarCartaoProducerPort;
import com.example.gw_gerenciador_cartoes.domain.ports.EmailNormalQueueProducerPort;
import com.example.gw_gerenciador_cartoes.infra.adapter.ClienteRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClienteServiceImplTest {

    @Autowired
    private ClienteRepositoryAdapter clienteRepository;

    @Autowired
    private ClienteServiceImpl clienteService;


    @MockBean
    private CriarCartaoProducerPort criarCartaoProducer;

    @MockBean
    private EmailNormalQueueProducerPort emailNormalQueueProducer;


    @Test
    void deveCriarCliente() {

        Endereco endereco = new Endereco("São Paulo", "Centro", "Rua das Flores", "01000-000", "Apto 101", "123");

        Conta conta = new Conta(null, "0001", "Corrente", new BigDecimal("1000.00"));

        Cliente cliente = new Cliente(null, "Fulano", "12345678901", "fulano@email.com", "2000-01-01", endereco, conta);
        Cliente clienteSalvo = clienteService.criarCliente(cliente);

        // 5. Verificar se foi alterado
        Cliente clienteFind = clienteRepository.findById(clienteSalvo.getId()).orElseThrow();
        assertEquals(cliente.getNome(), clienteFind.getNome());
    }

    @Test
    void deveDeletarCliente() {

        Endereco endereco = new Endereco("São Paulo", "Centro", "Rua das Flores", "01000-000", "Apto 101", "123");
        Conta conta = new Conta(null, "0001", "Corrente", new BigDecimal("1000.00"));
        Cliente cliente = new Cliente(null, "Fulano", "12345678901", "fulano@email.com", "2000-01-01", endereco, conta);
        Cliente clienteSalvo = clienteService.criarCliente(cliente);

        clienteService.deletarCliente(clienteSalvo.getId());
        Optional<Cliente> clienteFind = clienteRepository.findById(clienteSalvo.getId());

        assertTrue(clienteFind.isEmpty(), "O cliente deveria estar ausente");
    }

    @Test
    void deveBuscarEPaginar() {

        Endereco endereco = new Endereco("São Paulo", "Centro", "Rua das Flores", "01000-000", "Apto 101", "123");
        Conta conta = new Conta(null, "0001", "Corrente", new BigDecimal("1000.00"));
        Cliente cliente = new Cliente(null, "Fulano", "fulanoo@gmail.com", "03/09/1989", "2000-01-01", endereco, conta);
        Cliente clienteSalvo = clienteService.criarCliente(cliente);

        Endereco endereco2 = new Endereco("São Paulo", "Centro", "Rua das Flores", "01000-000", "Apto 101", "123");
        Conta conta2 = new Conta(null, "0001", "Corrente", new BigDecimal("1000.00"));
        Cliente cliente2 = new Cliente(null, "Beltrano", "tomas@gmail.com", "03/09/1989", "2000-01-01", endereco, conta);
        Cliente clienteSalvo2 = clienteService.criarCliente(cliente2);


        Page<Cliente> paginasCliente = clienteService.listarClientes(0,6,"id","asc","Fulano",null);


        assertNotNull(paginasCliente);
        assertEquals(1, paginasCliente.getTotalElements());
        assertEquals("Fulano", paginasCliente.getContent().get(0).getNome());

    }



}
